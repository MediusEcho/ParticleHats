package com.mediusecho.particlehats.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.util.ResourceUtil;

public enum SettingsManager {

	/**
	 * General Properties
	 */
	CONFIG_AUTO_UPDATE ("config-auto-update", Type.BOOLEAN, true),
	DEFAULT_MENU ("default-menu", Type.STRING, "particles.yml"),
	LANG ("lang", Type.STRING, "en_US.lang"),
	LOAD_INCLUDED_MENUS ("load-included-menus", Type.BOOLEAN, true),
	LOAD_INCLUDED_CUSTOM_TYPES ("load-included-custom-types", Type.BOOLEAN, true),
	DISABLED_WORLDS ("disabled-worlds", Type.STRING_LIST, new ArrayList<String>()),
	CHECK_WORLD_PERMISSION ("check-world-permission", Type.BOOLEAN, true),
	CLOSE_MENU_ON_EQUIP ("close-menu-on-equip", Type.BOOLEAN, true),
	CURRENCY ("currency", Type.STRING, "$"),
	LIVE_MENUS ("live-menus", Type.BOOLEAN, true),
	LIVE_MENU_UPDATE_FREQUENCY ("live-menu-update-frequency", Type.INT, 5),
	MAXIMUM_HAT_LIMIT ("max-hats", Type.INT, 7),
	UNEQUIP_OVERFLOW_HATS ("unequip-overflow-hats", Type.BOOLEAN, false),
	CHECK_AGAINST_LEGACY_PURCHASES ("check-against-legacy-purchases", Type.BOOLEAN, false),
	
	/**
	 * Database properties
	 */
	DATABASE_TYPE ("database.type", Type.STRING, "yaml"),
	DATABASE_USERNAME ("database.username", Type.STRING, "username"),
	DATABASE_PASSWORD ("database.password", Type.STRING, "password"),
	DATABASE_HOSTNAME ("database.hostname", Type.STRING, "hostname"),
	DATABASE_PORT ("database.port", Type.STRING, "3306"),
	DATABASE_DATABASE ("database.database", Type.STRING, "ParticleHats"),
	DATABASE_CONFIGURATION_PROPERTIES ("database.configuration-properties", Type.STRING, "?useSSL=false"),
	
	/**
	 * Flags
	 */
	FLAG_VAULT ("flags.vault", Type.BOOLEAN, false),
	FLAG_PLAYERPOINTS ("flags.playerpoints", Type.BOOLEAN, false),
	FLAG_EXPERIENCE ("flags.experience", Type.BOOLEAN, false),
	FLAG_PERMISSION ("flags.permission", Type.BOOLEAN, true),
	FLAG_VANISH ("flags.vanish", Type.BOOLEAN, false),
	FLAG_ESSENTIALS_VANISH ("flags.essentials-vanish", Type.BOOLEAN, false),
	
	/**
	 * Afk / Combat cooldown and flags
	 */
	AFK_COOLDOWN ("afk.cooldown", Type.INT, 7),
	COMBAT_COOLDOWN ("combat.cooldown", Type.INT, 5),
	COMBAT_CHECK_PLAYERS ("combat.check-players", Type.BOOLEAN, true),
	COMBAT_CHECK_MONSTERS ("combat.check-monsters", Type.BOOLEAN, true),
	COMBAT_CHECK_ANIMALSS ("combat.check-animals", Type.BOOLEAN, false),
	COMBAT_CHECK_NPC ("combat.check-npc", Type.BOOLEAN, false),
	
	/**
	 * Menu Properties
	 */
	MENU_LOCK_HATS_WITHOUT_PERMISSION ("menu.lock-hats-without-permission", Type.BOOLEAN, false),
	MENU_SHOW_DESCRIPTION_WHEN_LOCKKED ("menu.show-description-when-locked", Type.BOOLEAN, false),
	MENU_LOCKED_ITEM ("menu.locked-item.id", Type.MATERIAL, CompatibleMaterial.LAPIS_LAZULI.getMaterial()),
	MENU_LOCKED_ITEM_DAMAGE ("menu.locked-item.damage-value", Type.INT, 0),
	MENU_LOCKED_ITEM_TITLE ("menu.locked-item.title", Type.STRING, "&cLocked"),
	
	MENU_OPEN_WITH_ITEM ("menu.open-menu-with-item.enabled", Type.BOOLEAN, false),
	MENU_OPEN_DEFAULT_MENU ("menu.open-menu-with-item.default-menu", Type.STRING, "particles"),
	MENU_OPEN_WITH_GROUP ("menu.open-menu-with-item.use-player-group", Type.BOOLEAN, true),
	MENU_OPEN_WITH_ITEM_MATERIAL ("menu.open-menu-with-item.id", Type.MATERIAL, Material.NETHER_STAR),
	MENU_OPEN_WITH_ITEM_DAMAGE ("menu.open-menu-with-item.damage-value", Type.INT, 0),
	
	MENU_SOUND_ENABLED ("menu.sound.enabled", Type.BOOLEAN, true),
	MENU_SOUND_ID ("menu.sound.id", Type.SOUND, ResourceUtil.getSound("UI_BUTTON_CLICK", "CLICK")),
	MENU_SOUND_VOLUME ("menu.sound.volume", Type.DOUBLE, 1.0),
	MENU_SOUND_PITCH ("menu.sound.pitch", Type.DOUBLE, 1.0),
	
	/**
	 * Editor Properties
	 */
	EDITOR_USE_ACTION_BAR ("editor.use-actionbar", Type.BOOLEAN, true),
	EDITOR_META_TIME_LIMIT ("editor.meta-time-limit", Type.INT, 30),
	EDITOR_RESTRICT_COMMANDS ("editor.restrict-commands-while-editing", Type.BOOLEAN, true),
	EDITOR_SOUND_ENABLED ("editor.sound.enabled", Type.BOOLEAN, true),
	EDITOR_SOUND_ID ("editor.sound.id", Type.SOUND, ResourceUtil.getSound("BLOCK_METAL_PLACE", "STEP_STONE")),
	EDITOR_SOUND_VOLUME ("editor.sound.volume", Type.DOUBLE, 1.0),
	EDITOR_SOUND_PITCH ("editor.sound.pitch", Type.DOUBLE, 1.0),
	EDITOR_SOUND_MODIFIER ("editor.sound.modifier", Type.DOUBLE, 0.25),
	EDITOR_SHOW_BLACKLISTED_SOUNDS ("editor.show-blacklisted-sounds", Type.BOOLEAN, false),
	EDITOR_SHOW_BLACKLISTED_POTIONS ("editor.show-blacklisted-potions", Type.BOOLEAN, false);
	
	private final String key;
	private final Type dataType;
	private final Object defaultData;
	private final int range;
	
	private static Map<String, Object> data = new HashMap<String, Object>();
	private static final ParticleHats plugin = ParticleHats.instance;
	
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
	
	public String getKey () {
		return key;
	}
	
	public Type getType () {
		return dataType;
	}
	
	public Object getDefaultData () {
		return defaultData;
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
	
	/**
	 * Returns a config friendly version of the default data
	 */
	@SuppressWarnings("unchecked")
	public Object getDefaultConfigValue () 
	{
		switch (dataType)
		{
			case INT:
				return (Integer)defaultData;
				
			case DOUBLE:
				return (Double)defaultData;
				
			case STRING:
				return (String)defaultData;
				
			case BOOLEAN:
				return (Boolean)defaultData;
				
			case MATERIAL:
				return ((Material)defaultData).toString();
				
			case STRING_LIST:
				return (ArrayList<String>)defaultData;
				
			case SOUND:
				return ((Sound)defaultData).toString();
				
			default:
				return defaultData;
		}
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
						value = config.getInt(entry.key, (Integer) entry.defaultData);
						break;
						
					case DOUBLE:
						value = config.getDouble(entry.key, (Double) entry.defaultData);
						break;
						
					case STRING:
						value = config.getString(entry.key, (String) entry.defaultData);
						break;
						
					case BOOLEAN:
						value = config.getBoolean(entry.key, (Boolean) entry.defaultData);
						break;
						
					case MATERIAL:
						value = ItemUtil.getMaterial(config.getString(entry.key), (Material) entry.defaultData);
						break;
						
					case STRING_LIST:
						value = config.getStringList(entry.key);
						break;
						
					case SOUND:
						value = ResourceUtil.getSound(entry.key, "CLICK");
						break;
					
					default:
						value = config.get(entry.key);
				}
				
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
	
	public enum Type
	{
		INT,
		INT_CLAMPED,
		DOUBLE,
		STRING,
		BOOLEAN,
		MATERIAL,
		STRING_LIST,
		SOUND,
		DEPRECATED;
	}
}
