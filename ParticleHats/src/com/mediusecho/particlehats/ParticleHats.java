package com.mediusecho.particlehats;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.mediusecho.particlehats.api.HatAPI;
import com.mediusecho.particlehats.api.ParticleHatsAPI;
import com.mediusecho.particlehats.configuration.CustomConfig;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.database.type.DatabaseType;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.CommandManager;
import com.mediusecho.particlehats.managers.EventManager;
import com.mediusecho.particlehats.managers.HookManager;
import com.mediusecho.particlehats.managers.ParticleManager;
import com.mediusecho.particlehats.managers.ResourceManager;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.renderer.ParticleRenderer;
import com.mediusecho.particlehats.particles.renderer.legacy.LegacyParticleRenderer;
import com.mediusecho.particlehats.particles.renderer.spigot.SpigotParticleRenderer;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.prompt.BukkitPrompt;
import com.mediusecho.particlehats.prompt.Prompt;
import com.mediusecho.particlehats.prompt.SpigotPrompt;
import com.mediusecho.particlehats.stats.Metrics;
import com.mediusecho.particlehats.tasks.MenuTask;
import com.mediusecho.particlehats.tasks.ParticleTask;
import com.mediusecho.particlehats.tasks.PromptTask;
import com.mediusecho.particlehats.ui.MenuManagerFactory;
import com.mediusecho.particlehats.util.ResourceUtil;
import com.mediusecho.particlehats.util.YamlUtil;

@SuppressWarnings("unused")
public class ParticleHats extends JavaPlugin {
	
	// [Future] Are features that i plan on adding.
	// [?] Are ideas that may or may not get added.
	
	// TODO: [?] Enjin economy support?
	// TODO: [?] Custom Player Head support? (Head Database, Heads Plugin)
	// TODO: [?] Type texture support? Lets built-in types display custom images (eg. capes)
	// TODO: [?] More flexible mode support?
	// Similar to tags, Have a list of tags that will trigger particles displaying / disabling
	// Display when: (running, walking, flying, etc)
	// Disable when: (pvp, swimming, etc)
	
	// TODO: [Future] Add update notifier
	// TODO: [Future] Allow adding custom types images as frames to an animation
	// TODO: [Future] Re-implement text particle type
	// TODO: [Future] Let particles be attached to blocks...
	// Have a separate block menu that shows nearby particles for the player to edit.
	
	public static ParticleHats instance;
	public static int serverVersion;
	private static Logger logger;
	private static ParticleHatsAPI hatAPI;
	
	private MenuManagerFactory menuManagerFactory;
	
	private Database database;
	private DatabaseType databaseType;
	
	private ParticleRenderer particleRenderer;
	
	// Managers
	private ResourceManager resourceManager;
	private EventManager eventManager;
	private CommandManager commandManager;
	private ParticleManager particleManager;
	private HookManager hookManager;
	
	// Lang
	private File langFile;
	private YamlConfiguration lang;
	
	// Update en_US.lang version as well.
	private final double LANG_VERSION = 1.4;
	
	private Map<UUID, EntityState> entityState;
	
	// Lets us know we can use the BaseComponent class from the bungee api
	private boolean supportsBaseComponent = true;
	private Prompt prompt;
	
	// Tasks
	private MenuTask menuTask;
	private PromptTask promptTask;
	private ParticleTask particleTask;
	
	private boolean enabled = false;
	
	// Debugging
	public static final boolean debugging = false;
	
	@Override
	public void onEnable ()
	{
		instance = this;	
		serverVersion = getServerVersion();
		logger = getServer().getLogger();
		hatAPI = new HatAPI(this);
		
		// Make sure we're running on a supported version
		if (serverVersion < 13)
		{
			particleRenderer = new LegacyParticleRenderer();
			
			if (serverVersion < 8)
			{
				log("-----------------------------------------------------------------------");
				log("This version of ParticleHats is not compatible with your server version");
				log("Download version 3.7.5 if your server is on a version older than 1.8");
				log("-----------------------------------------------------------------------");
				
				getServer().getPluginManager().disablePlugin(this);
				return;	
			}
		} else {
			particleRenderer = new SpigotParticleRenderer();
		}
		
		// Check to see if we're running on Spigot
		try {
			Class.forName("net.md_5.bungee.api.chat.BaseComponent");
		} catch (ClassNotFoundException e) {
			supportsBaseComponent = false;
		}
		
		// Save default config
		saveDefaultConfig();
		
		log("Loading ParticleHats v" + getDescription().getVersion());
		log("");
		{		
			// Create our menu manager factory
			menuManagerFactory = new MenuManagerFactory(this);
			
			if (YamlUtil.checkConfigForUpdates(getConfig()))
			{
				if (SettingsManager.CONFIG_AUTO_UPDATE.getBoolean())
				{
					log("Updating config.yml");
					YamlUtil.updateConfig(this, getConfig());
				}
				
				else {
					log("There is an update for config.yml, auto updates are disabled.");
				}
			}
			
			// Load our database
			databaseType = DatabaseType.fromAlias(SettingsManager.DATABASE_TYPE.getString());
			database = databaseType.getDatabase(this);
			
			// yaml always returns true, mysql will return false if the connection could not be made
			if (!database.isEnabled())
			{
				log("---------------------------------------------------");
				log("There was an error connecting to the MySQL database");
				
				if (database.getException() != null) {
					log("Error: " + database.getException().getClass().getSimpleName());
					if (debugging) {
						database.getException().printStackTrace();
					}
				}
				
				log("Switching to yaml");
				log("---------------------------------------------------");
				log("");
				
				databaseType = DatabaseType.YAML;
				database = databaseType.getDatabase(this);
			}
			
			// Initialize our player state map
			entityState = new HashMap<UUID, EntityState>();
			
			log("");
			checkDefaultLang();
			loadLang();
			
			// Create our managers
			resourceManager = new ResourceManager(this);
			eventManager = new EventManager(this);
			commandManager = new CommandManager(this, "h");
			particleManager = new ParticleManager(this);
			hookManager = new HookManager(this);
			
			// Enable Metrics
			Metrics metrics = new Metrics(this);
			metrics.addCustomChart(new Metrics.SimplePie("database_type", () -> databaseType.toString().toLowerCase()));
			
			if (SettingsManager.EDITOR_USE_ACTION_BAR.getBoolean() && supportsBaseComponent) {
				prompt = new SpigotPrompt();
			} else {
				prompt = new BukkitPrompt();
			}
			
			// Handles menu updates
			menuTask = new MenuTask(this);
			menuTask.runTaskTimer(this, 0, SettingsManager.LIVE_MENU_UPDATE_FREQUENCY.getInt());
			
			// Handles meta editing prompts
			promptTask = new PromptTask(this);
			promptTask.runTaskTimer(this, 0, 40);
			
			// Handles displaying particles
			particleTask = new ParticleTask(this);
			particleTask.runTaskTimer(this, 0, 1);
		}
		log("");
		log("Done :)");
		
		enabled = true;
	}
	
	@Override
	public void onDisable () 
	{
		if (enabled)
		{
			database.onDisable();
			
			menuTask.cancel();
			promptTask.cancel();
			particleTask.cancel();
		}
	}
	
	public void onReload ()
	{
		//locale.reload();
		reloadConfig();
		
		SettingsManager.onReload();
		
		if (langFile == null || !(langFile.getName().equals(SettingsManager.LANG.getString()))) {
			loadLang();
		}
		lang = YamlConfiguration.loadConfiguration(langFile);
		
		Message.onReload();
		
		if (SettingsManager.EDITOR_USE_ACTION_BAR.getBoolean() && supportsBaseComponent) {
			prompt = new SpigotPrompt();
		} else {
			prompt = new BukkitPrompt();
		}
		
		database.onReload();
		particleTask.onReload();
		resourceManager.onReload();
		hookManager.onReload();
	}
	
	/**
	 * Get the ParticleHats Hat API
	 * @return
	 */
	public ParticleHatsAPI getAPI () {
		return hatAPI;
	}
	
	/**
	 * Returns the Database this plugin is using
	 * @return
	 */
	public Database getDatabase () {
		return database;
	}
	
	/**
	 * Returns the type of database this server is using
	 * @return
	 */
	public DatabaseType getDatabaseType () {
		return databaseType;
	}
	
	/**
	 * Returns the MenuManagerFactory class
	 * @return
	 */
	public MenuManagerFactory getMenuManagerFactory () {
		return menuManagerFactory;
	}
	
	/**
	 * Get the ParticleRenderer for this server version
	 * @return
	 */
	public ParticleRenderer getParticleRenderer () {
		return particleRenderer;
	}
	
	/**
	 * Get the ResourceManager class
	 * @return
	 */
	public ResourceManager getResourceManager () {
		return resourceManager;
	}
	
	/**
	 * Get the ParticleManager class
	 * @return
	 */
	public ParticleManager getParticleManager () {
		return particleManager;
	}
	
	/**
	 * Get the HookManager class
	 * @return
	 */
	public HookManager getHookManager () {
		return hookManager;
	}
	
	/**
	 * Returns the PlayerState object that belongs to this player
	 * @param id
	 * @return
	 */
	public PlayerState getPlayerState (Player player) {
		return (PlayerState)getEntityState(player);
	}
	
	public EntityState getEntityState (Entity entity, int entityID)
	{
		UUID id = entity.getUniqueId();
		
		if (entityState.containsKey(id)) {
			return entityState.get(id);
		}
		
		if (entity instanceof Player)
		{
			EntityState eState;
			
			if (entity.hasMetadata("NPC")) {
				eState = new EntityState(entity, entityID);
			} else {
				eState = new PlayerState((Player)entity);
			}
			
			entityState.put(id, eState);
			return eState;
		}
		
		EntityState eState = new EntityState(entity, entityID);
		
		entityState.put(id, eState);
		return eState;
	}
	
	public EntityState getEntityState (Entity entity) {
		return getEntityState(entity, -1);
	}
	
	/**
	 * Checks to see if this entity has an EntityState object loaded
	 * @param entity
	 * @return
	 */
	public boolean hasEntityState (Entity entity) {
		return entityState.containsKey(entity.getUniqueId());
	}
	
	/**
	 * Returns all currently active player states
	 * @return
	 */
	public Collection<EntityState> getEntityStates () {
		return entityState.values();
	}
	
	public void removePlayerState (UUID id) {
		entityState.remove(id);
	}
	
	/**
	 * Check to see if we can use the bungee BaseComponent class
	 * @return
	 */
	public Boolean canUseBungee () {
		return supportsBaseComponent;
	}
	
	/**
	 * Gets the current server version
	 * @return
	 */
	public int getServerVersion() 
	{
		String version = Bukkit.getServer().getClass().getPackage().getName().substring(23);
		return Integer.parseInt(version.split("_")[1]);
	}
	
	/**
	 * Get this plugins CommandManager
	 * @return
	 */
	public CommandManager getCommandManager () {
		return commandManager;
	}
	
	public YamlConfiguration getLocale () {
		return lang;
	}
	
	/**
	 * Sends the player a message using their Action Bar
	 * @param player
	 * @param message
	 */
	public void prompt (Player player, MetaState message) {
		prompt.prompt(player, message);
	}
	
	public Prompt getPrompt () {
		return prompt;
	}
	
	/**
	 * Checks to see if a file exists with this name in the given folder
	 * @param folderName
	 * @param fileName
	 * @return
	 */
	public boolean fileExists (String folderName, String fileName)
	{
		String directory = getDataFolder() + File.separator + folderName + File.separator + fileName;
		File file = new File(directory);
		
		return file.exists();
	}
	
	/**
	 * Logs a message to the server console
	 * @param obj
	 */
	public static void log (Object obj) {
		logger.log(Level.INFO, "[ParticleHats] " + obj.toString());
	}
	
	/**
	 * Logs a debug message to the server console if debugging is enabled
	 * @param obj
	 */
	public static void debug (Object obj) 
	{
		if (debugging) {
			logger.log(Level.INFO, "[ParticleHats Debug] " + obj.toString());
		}
	}
	
	private void loadLang ()
	{
		String targetLang = SettingsManager.LANG.getString();
		
		File langFile = new File(getDataFolder() + File.separator + "lang" + File.separator + targetLang);
		if (!langFile.exists()) 
		{
			if (targetLang.equals("en_US.lang")) {
				log("Creating en_US.lang");
			} else {
				log("Could not find locale " + targetLang + ", switching to en_US.lang");
			}
		
			// Create our default .lang file since the specified one doesn't exist
			createDefaultLang();
			targetLang = "en_US.lang";
		}
		
		log("Using locale " + targetLang);
		this.langFile = new File(getDataFolder() + File.separator + "lang" + File.separator + targetLang);
		this.lang = YamlConfiguration.loadConfiguration(this.langFile);
	}
	
	private void createDefaultLang ()
	{
		File langFolder = new File(getDataFolder() + File.separator + "lang");
		if (!langFolder.exists()) {
			langFolder.mkdirs();
		}
		
		InputStream langStream = getResource("lang/en_US.lang");
		if (langStream != null)
		{
			File langFile = new File(getDataFolder() + File.separator + "lang" + File.separator + "en_US.lang");
			
			if (langFile.exists()) {
				langFile.delete();
			}
			
			try {
				Files.copy(langStream, Paths.get(langFile.getPath()));
			} catch (IOException e) {}
		}
	}
	
	private void checkDefaultLang ()
	{
		File langFile = new File(getDataFolder() + File.separator + "lang" + File.separator + "en_US.lang");
		if (!langFile.exists()) {
			createDefaultLang();
		}
		
		else
		{
			YamlConfiguration tempLangConfig = YamlConfiguration.loadConfiguration(langFile);
			if (tempLangConfig.getDouble("version", 1.0) != LANG_VERSION) 
			{
				log("Updating en_US.lang");
				createDefaultLang();
			}
		}
	}
}
