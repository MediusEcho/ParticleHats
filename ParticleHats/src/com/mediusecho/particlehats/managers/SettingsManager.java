package com.mediusecho.particlehats.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import com.mediusecho.particlehats.Core;

public enum SettingsManager {

	/**
	 * General Properties
	 */
	DEFAULT_MENU               ("default-menu",               Type.STRING,      "particles.yml"),
	DEFAULT_MESSAGES           ("default-messages",           Type.STRING,      "messages.yml"),
	LOAD_INCLUDED_MENUS        ("load-included-menus",        Type.BOOLEAN,     true),
	LOAD_INCLUDED_CUSTOM_TYPES ("load-included-custom-types", Type.BOOLEAN,     true),
	DISABLED_WORLDS            ("disabled-worlds",            Type.STRING_LIST, new ArrayList<String>()),
	CHECK_WORLD_PERMISSION     ("check-world-permission",     Type.BOOLEAN,     true),
	HAT_EQUIPPED_TOOLTIP       ("hat-equipped-tooltip",       Type.STRING_LIST, new ArrayList<String>()),
	CLOSE_MENU_ON_EQUIP        ("close-menu-on-equip",        Type.BOOLEAN,     true),
	CURRENCY                   ("currency",                   Type.STRING,      "$"),
	LIVE_MENUS                 ("live-menus",                 Type.BOOLEAN,     true),
	LIVE_MENU_UPDATE_FREQUENCY ("live-menu-update-frequency", Type.INT,         5),
	
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
	MENU_LOCK_HATS_WITHOUT_PERMISSION ("menu.lock-hats-without-permission", Type.BOOLEAN,  false),
	MENU_LOCKED_ITEM_ID               ("menu.locked-item.id",               Type.MATERIAL, Material.LAPIS_LAZULI),
	MENU_LOCKED_ITEM_TITLE            ("menu.locked-item.title",            Type.STRING,   "&8Lockled"),
	
	MENU_SOUND_ENABLED ("menu.sound.enabled", Type.BOOLEAN, true),
	MENU_SOUND_ID      ("menu.sound.id",      Type.SOUND,   Sound.UI_BUTTON_CLICK),
	MENU_SOUND_VOLUME  ("menu.sound.volume",  Type.FLOAT,   1.0),
	MENU_SOUND_PITCH   ("menu.sound.pitch",   Type.FLOAT,   1.0),
	
	/**
	 * Editor Properties
	 */
	EDITOR_USE_ACTION_BAR  ("editor.use-actionbar",   Type.BOOLEAN, true),
	EDITOR_META_TIME_LIMIT ("editor.meta-time-limit", Type.INT,     30),
	EDITOR_SOUND_ENABLED   ("editor.sound.enabled",   Type.BOOLEAN, true),
	EDITOR_SOUND_ID        ("editor.sound.id",        Type.SOUND,   Sound.BLOCK_METAL_PLACE),
	EDITOR_SOUND_VOLUME    ("editor.sound.volume",    Type.FLOAT,   1.0),
	EDITOR_SOUND_PITCH     ("editor.sound.pitch",     Type.FLOAT,   1.0),
	EDITOR_SOUND_MODIFIER  ("editor.sound.modifier",  Type.FLOAT,   0.25);
	
	
	private final String key;
	private final Type dataType;
	private final Object defaultData;
	
	private static Map<String, Object> data = new HashMap<String, Object>();
	private static final Core plugin = Core.instance;
	
	static
	{
		FileConfiguration config = plugin.getConfig();
		if (config != null)
		{
			for (SettingsManager entry : values())
			{
				Object value = config.get(entry.key);
				if (value != null) {
					data.put(entry.key, value);
				} else {
					data.put(entry.key, entry.defaultData);
				}
			}
		}
	}
	
	private SettingsManager (final String key, final Type dataType, Object defaultData)
	{
		this.key = key;
		this.dataType = dataType;
		this.defaultData = defaultData;
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
	 * Returns the Integer value of this enum, or -1
	 * @return
	 */
	public int getInt () {
		return dataType.equals(Type.INT) ? (int)getData() : -1;
	}

	/**
	 * Returns the Float value of this enum, or -1.0
	 * @return
	 */
	public float getFloat () {
		return dataType.equals(Type.FLOAT) ? Float.valueOf(asString()): -1.0f;
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
		return dataType.equals(Type.SOUND) ? Sound.valueOf(asString()) : Sound.UI_BUTTON_CLICK;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> getList () {
		return dataType.equals(Type.STRING_LIST) ? (ArrayList<String>)getData() : new ArrayList<String>();
	}
	
	/**
	 * Forces this enum's data to be returned as a String
	 * @return
	 */
	public String asString () {
		return String.valueOf(getData());
	}
	
	private enum Type
	{
		INT,
		FLOAT,
		STRING,
		BOOLEAN,
		MATERIAL,
		STRING_LIST,
		SOUND;
	}
}
