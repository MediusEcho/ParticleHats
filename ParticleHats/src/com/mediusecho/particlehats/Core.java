package com.mediusecho.particlehats;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.mediusecho.particlehats.configuration.CustomConfig;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.database.type.DatabaseType;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.CommandManager;
import com.mediusecho.particlehats.managers.EventManager;
import com.mediusecho.particlehats.managers.HookManager;
import com.mediusecho.particlehats.managers.ParticleManager;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.stats.Metrics;
import com.mediusecho.particlehats.tasks.MenuTask;
import com.mediusecho.particlehats.tasks.ParticleTask;
import com.mediusecho.particlehats.tasks.PromptTask;

@SuppressWarnings("unused")
public class Core extends JavaPlugin {

	// TODO: Purchasing saving / loading
	
	// TODO: [Opt] Add menu cache feature, only load menus that have changes
	
	// TODO: [?] Separate menu for block-fixed particles?
	// Store fixed particles in a menu that is separate from player menus
	// Players can sort by nearest and teleport to the particle
	
	public static Core instance;
	private static Logger logger;
	
	private Database database;
	private DatabaseType databaseType;
	
	// Managers
	private EventManager eventManager;
	private CommandManager commandManager;
	private ParticleManager particleManager;
	private HookManager hookManager;
	
	// Configuration files
	CustomConfig locale;
	
	// Player State
	private Map<UUID, PlayerState> playerState;
	
	// Lets us know we can use the BaseComponent class from the bungee api
	private boolean supportsBaseComponent = true;
	
	// Tasks
	private MenuTask menuTask;
	private PromptTask promptTask;
	private ParticleTask particleTask;
	
	// Debugging
	private static final boolean debugging = true;
	
	@Override
	public void onEnable ()
	{
		instance = this;	
		logger = getServer().getLogger();
		
		// Make sure we're running on a supported version
		if (getServerVersion() < 13)
		{
			log("-----------------------------------------------------------------------");
			log("This version of ParticleHats is not compatible with your server version");
			log("Download version 3.7.5 if your server is on 1.7.10 - 1.12.2");
			log("-----------------------------------------------------------------------");
			
			getServer().getPluginManager().disablePlugin(this);
		}
		
		// Check to see if we're running on Spigot
		try {
			Class.forName("net.md_5.bungee.api.chat.BaseComponent");
		} catch (ClassNotFoundException e) {
			supportsBaseComponent = false;
		}
		
		// Save default config
		saveDefaultConfig();
		
		// Override our default_message.yml config
		InputStream localStream = getResource("default_messages.yml");
		if (localStream != null) 
		{
			File defaultLocale = new File(this.getDataFolder() + File.separator + "default_messages.yml");
			try {
				ResourceUtil.copyFile(localStream, defaultLocale);
			} catch (IOException e) {}
		}
		
		log("Initializing");
		log("");
		{		
			// Load our database
			databaseType = DatabaseType.fromAlias(SettingsManager.DATABASE_TYPE.getString());
			database = databaseType.getDatabase(this, (e) ->
			{
				// Default to YAML if our mysql database can't connect
				log("---------------------------------------------------");
				log("There was an error connecting to the MySQL database");
				log("Error: " + e.getClass().getSimpleName());
				log("Switching to yaml");
				log("---------------------------------------------------");
				log("");
				
				databaseType = DatabaseType.YAML;
				database = DatabaseType.YAML.getDatabase();
			});
			
			// Initialize our player state map
			playerState = new HashMap<UUID, PlayerState>();
			
			// Create our configuration files
			locale = new CustomConfig(this, "", SettingsManager.DEFAULT_MESSAGES.getString(), true);
			
			// Create our managers
			eventManager = new EventManager(this);
			commandManager = new CommandManager(this, "h");
			particleManager = new ParticleManager(this);
			hookManager = new HookManager(this);
			
			// Enable Metrics
			Metrics metrics = new Metrics(this);
			metrics.addCustomChart(new Metrics.SimplePie("database_type", () -> databaseType.toString().toLowerCase()));
			
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
		log("" + this.getDescription().getVersion() + " loaded");
	}
	
	@Override
	public void onDisable () 
	{
		database.onDisable();
		
		menuTask.cancel();
		promptTask.cancel();
		particleTask.cancel();
	}
	
	public void onReload ()
	{
		locale.reload();
		reloadConfig();
		
		SettingsManager.onReload();
		Message.onReload();
		
		particleTask.onReload();
		hookManager.onReload();
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
	 * @return
	 */
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
	public PlayerState getPlayerState (UUID id)
	{
		if (playerState.containsKey(id)) {
			return playerState.get(id);
		}
		
		PlayerState state = new PlayerState(Bukkit.getPlayer(id));
		playerState.put(id, state);
		
		return state;
	}
	
	public void removePlayerState (UUID id) {
		playerState.remove(id);
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
	
	/**
	 * Gets the CustomConfig responsible for our plugins locale
	 * @return
	 */
	public CustomConfig getLocaleConfig () {
		return locale;
	}
	
	/**
	 * Sends the player a message using their Action Bar
	 * @param player
	 * @param message
	 */
	public void prompt (Player player, MetaState message) {
		promptTask.prompt(player, message.getDescription());
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
			logger.log(Level.INFO, "[ParticleHats] " + obj.toString());
		}
	}
}
