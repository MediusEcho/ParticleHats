package com.mediusecho.particlehats.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;

public enum SettingsManager {

	/**
	 * General Properties
	 */
	DEFAULT_MENU               ("default-menu",               Type.STRING,      "particles.yml"),
	DEFAULT_MESSAGES           ("default-messages",           Type.STRING,      "default_messages.yml"),
	LOAD_INCLUDED_MENUS        ("load-included-menus",        Type.BOOLEAN,     true),
	LOAD_INCLUDED_CUSTOM_TYPES ("load-included-custom-types", Type.BOOLEAN,     true),
	DISABLED_WORLDS            ("disabled-worlds",            Type.STRING_LIST, new ArrayList<String>()),
	CHECK_WORLD_PERMISSION     ("check-world-permission",     Type.BOOLEAN,     true),
	CLOSE_MENU_ON_EQUIP        ("close-menu-on-equip",        Type.BOOLEAN,     true),
	CURRENCY                   ("currency",                   Type.STRING,      "$"),
	LIVE_MENUS                 ("live-menus",                 Type.BOOLEAN,     true),
	LIVE_MENU_UPDATE_FREQUENCY ("live-menu-update-frequency", Type.INT,         5),
	MAXIMUM_HAT_LIMIT          ("max-hats",                   Type.INT_CLAMPED, 7, 28),
	
	/**
	 * Database properties
	 */
	DATABASE_TYPE     ("database.type",     Type.STRING, "yaml"),
	DATABASE_USERNAME ("database.username", Type.STRING, "username"),
	DATABASE_PASSWORD ("database.password", Type.STRING, "password"),
	DATABASE_HOSTNAME ("database.hostname", Type.STRING, "hostname"),
	DATABASE_PORT     ("database.port",     Type.STRING, "3306"),
	DATABASE_DATABASE ("database.database", Type.STRING, "ParticleHats"),
	
	/**
	 * Flags
	 */
	FLAG_VAULT        ("flags.vault",        Type.BOOLEAN, false),
	FLAG_PLAYERPOINTS ("flags.playerpoints", Type.BOOLEAN, false),
	FLAG_EXPERIENCE   ("flags.experience",   Type.BOOLEAN, false),
	FLAG_PERMISSION   ("flags.permission",   Type.BOOLEAN, false),
	FLAG_VANISH       ("flags.vanish",       Type.BOOLEAN, false),
	
	/**
	 * Afk / Combat cooldown and flags
	 */
	AFK_COOLDOWN          ("afk.cooldown",          Type.INT,     1000),
	COMBAT_COOLDOWN       ("combat.cooldown",       Type.INT,     1000),
	COMBAT_CHECK_PLAYERS  ("combat.check-players",  Type.BOOLEAN, true),
	COMBAT_CHECK_MONSTERS ("combat.check-monsters", Type.BOOLEAN, true),
	COMBAT_CHECK_ANIMALSS ("combat.check-animals",  Type.BOOLEAN, false),
	COMBAT_CHECK_NPC      ("combat.check-npc",      Type.BOOLEAN, false),
	
	/**
	 * Menu Properties
	 */
	MENU_LOCK_HATS_WITHOUT_PERMISSION  ("menu.lock-hats-without-permission", Type.BOOLEAN,  false),
	MENU_SHOW_DESCRIPTION_WHEN_LOCKKED ("menu.show-description-when-locked", Type.BOOLEAN,  false),
	MENU_LOCKED_ITEM                   ("menu.locked-item.id",               Type.MATERIAL, Material.LAPIS_LAZULI),
	MENU_LOCKED_ITEM_TITLE             ("menu.locked-item.title",            Type.STRING,   "&cLocked"),
	
	MENU_OPEN_WITH_ITEM          ("menu.open-menu-with-item.enabled", Type.BOOLEAN, false),
	MENU_OPEN_DEFAULT_MENU       ("menu.open-menu-with-item.default-menu", Type.STRING, "particles"),
	MENU_OPEN_WITH_GROUP         ("menu.open-menu-with-item.use-player-group", Type.BOOLEAN, true),
	MENU_OPEN_WITH_ITEM_MATERIAL ("menu.open-menu-with-item.id", Type.MATERIAL, Material.NETHER_STAR),
	MENU_OPEN_WITH_ITEM_DAMAGE   ("menu.open-menu-with-item.damage-value", Type.INT, 0),
	
	MENU_SOUND_ENABLED ("menu.sound.enabled", Type.BOOLEAN, true),
	MENU_SOUND_ID      ("menu.sound.id",      Type.SOUND,   Sound.UI_BUTTON_CLICK),
	MENU_SOUND_VOLUME  ("menu.sound.volume",  Type.DOUBLE,  1.0),
	MENU_SOUND_PITCH   ("menu.sound.pitch",   Type.DOUBLE,  1.0),
	
	/**
	 * Editor Properties
	 */
	EDITOR_USE_ACTION_BAR  ("editor.use-actionbar",   Type.BOOLEAN, true),
	EDITOR_META_TIME_LIMIT ("editor.meta-time-limit", Type.INT,     30),
	EDITOR_SOUND_ENABLED   ("editor.sound.enabled",   Type.BOOLEAN, true),
	EDITOR_SOUND_ID        ("editor.sound.id",        Type.SOUND,   Sound.BLOCK_METAL_PLACE),
	EDITOR_SOUND_VOLUME    ("editor.sound.volume",    Type.DOUBLE,  1.0),
	EDITOR_SOUND_PITCH     ("editor.sound.pitch",     Type.DOUBLE,  1.0),
	EDITOR_SOUND_MODIFIER  ("editor.sound.modifier",  Type.DOUBLE,  0.25);
	
	
	private final String key;
	private final Type dataType;
	private final Object defaultData;
	private final int range;
	
	private static Map<String, Object> data = new HashMap<String, Object>();
	private static final Core plugin = Core.instance;
	
	static {
		loadData();
	}
	
	private SettingsManager (final String key, final Type dataType, Object defaultData, int range)
	{
		this.key = key;
		this.dataType = dataType;
		this.defaultData = defaultData;
		this.range = range;
	}
	
	private SettingsManager (final String key, final Type dataType, Object defaultData)
	{
		this(key, dataType, defaultData, -1);
	}
	
	/**
	 * Returns the data that belongs to this key, or the default data
	 * @return
	 */
	public Object getData ()
	{
		if (data.containsKey(key)) {
			return data.get(key);
		}
		return defaultData;
	}
	
	/**
	 * Override the current value
	 * @param o
	 */
	@SuppressWarnings("incomplete-switch")
	public void addOverride (Object o)
	{
		switch (dataType)
		{
			case INT:
				if (o instanceof Integer) {
					data.put(key, (int)o);
				}
				break;
				
			case DOUBLE:
				if (o instanceof Double) {
					data.put(key, (double)o);
				}
				break;
				
			case STRING:
				if (o instanceof String) {
					data.put(key, (String)o);
				}
				break;
				
			case BOOLEAN:
				if (o instanceof Boolean) {
					data.put(key, (boolean)o);
				}
				break;
				
			case MATERIAL:
				if (o instanceof Material) {
					data.put(key, (Material)o);
				}
				break;
				
			case SOUND:
				if (o instanceof Sound) {
					data.put(key, (Sound)o);
				}
				break;
		}
	}
	
	/**
	 * Returns the Integer value of this enum, or -1
	 * @return
	 */
	public int getInt () 
	{
		if (dataType.equals(Type.INT)) {
			return (int)getData();
		}
		
		if (dataType.equals(Type.INT_CLAMPED)) {
			return MathUtil.clamp((int)getData(), 0, range);
		}
		
		return -1;
	}

	/**
	 * Returns the Float value of this enum, or -1.0
	 * @return
	 */
	public double getDouble () {
		return dataType.equals(Type.DOUBLE) ? (double)getData() : -1.0f;
	}
	
	/**
	 * Returns the String value of this enum
	 * @return
	 */
	public String getString () {
		return dataType.equals(Type.STRING) ? (String)getData() : "";
	}
	
	/**
	 * Returns the Boolean value of this enum
	 * @return
	 */
	public Boolean getBoolean () {
		return dataType.equals(Type.BOOLEAN) ? (Boolean)getData() : false;
	}
	
	/**
	 * Returns the Sound value of this enum
	 * @return
	 */
	public Sound getSound () {
		return dataType.equals(Type.SOUND) ? (Sound)getData() : Sound.UI_BUTTON_CLICK;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> getList () {
		return dataType.equals(Type.STRING_LIST) ? (ArrayList<String>)getData() : new ArrayList<String>();
	}
	
	/**
	 * Returns the Material value of this enum
	 * @return
	 */
	public Material getMaterial () {
		return dataType.equals(Type.MATERIAL) ? (Material)getData() : (Material)defaultData;
	}
	
	/**
	 * Forces this enum's data to be returned as a String
	 * @return
	 */
	public String asString () {
		return String.valueOf(getData());
	}
	
	private static void loadData ()
	{
		FileConfiguration config = plugin.getConfig();
		if (config != null)
		{
			for (SettingsManager entry : values())
			{
				Object value;
				
				switch (entry.dataType)
				{
					case INT:
						value = config.getInt(entry.key);
						break;
						
					case DOUBLE:
						value = config.getDouble(entry.key);
						break;
						
					case STRING:
						value = config.getString(entry.key);
						break;
						
					case BOOLEAN:
						value = config.getBoolean(entry.key);
						break;
						
					case MATERIAL:
						value = ItemUtil.materialFromString(config.getString(entry.key), (Material) entry.defaultData);
						break;
						
					case STRING_LIST:
						value = config.getStringList(entry.key);
						break;
						
					case SOUND:
						value = Sound.valueOf(config.getString(entry.key));
						break;
					
					default:
						value = config.get(entry.key);
				}
				
				//Object value = config.get(entry.key);
				if (value != null) {
					data.put(entry.key, value);
				} else {
					data.put(entry.key, entry.defaultData);
				}
			}
		}
	}
	
	public static void onReload ()
	{
		data.clear();
		loadData();
	}
	
	private enum Type
	{
		INT,
		INT_CLAMPED,
		DOUBLE,
		STRING,
		BOOLEAN,
		MATERIAL,
		STRING_LIST,
		SOUND;
	}
}
