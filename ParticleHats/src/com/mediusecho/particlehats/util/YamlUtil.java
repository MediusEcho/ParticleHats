package com.mediusecho.particlehats.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.configuration.CustomConfig;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.managers.SettingsManager.Type;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.particles.properties.ParticleAnimation;

public class YamlUtil {

	private static final double MENU_VERSION = 4.0;
	private static final double CONFIG_VERSION = 2.0;
	
	/**
	 * Checks to see if this CustomConfig is updated to the current version
	 * @param config
	 * @return
	 */
	public static boolean isUpdated (CustomConfig config) {
		return config.getConfig().getDouble("version", 0) >= MENU_VERSION;
	}
	
	/**
	 * Updates a legacy menu to the current save format
	 * @param config
	 */
	public static void updateMenuSaveFormat (CustomConfig menuConfig)
	{
		ParticleHats.log("Updating " + menuConfig.getFileName() + " to new save format");
		
		FileConfiguration config = menuConfig.getConfig();
		config.set("version", MENU_VERSION);
		
		if (config.contains("items"))
		{
			Set<String> keys = config.getConfigurationSection("items").getKeys(false);
			for (String key : keys)
			{
				if (key == null) {
					continue;
				}
				
				String path = "items." + key + ".";
				
				// Update properties only belonging to the base hat
				updateBaseFormat(config, path);
				
				// Updates particles & any related particle data
				updateParticlesFormat(config, path, path);
				
				// Update node data
				if (config.contains(path + "node"))
				{
					updateNodeFormat(config, path + "node.", path, 1);
					config.set(path + "node", null);
				}
			}
		}
		
		menuConfig.save();
	}
	
	/**
	 * Updates properties that only belong to a base hat
	 * @param config
	 * @param path
	 */
	private static void updateBaseFormat (FileConfiguration config, String path)
	{
		if (config.contains(path + "no-permission"))
		{
			String noPermission = config.getString(path + "no-permission");
			
			config.set(path + "no-permission", null);
			config.set(path + "permission-denied", noPermission);
		}
		
		if (config.isDouble(path + "offset") || config.isInt(path + "offset")) {
			config.set(path + "offset.y", config.getDouble(path + "offset"));
		}
		
		if (config.contains(path + "no-permission-lore"))
		{
			List<String> noPermissionLore = config.getStringList(path + "no-permission-lore");
			
			config.set(path + "no-permission-lore", null);
			config.set(path + "permission-description", noPermissionLore);
		}
		
		if (config.contains(path + "action"))
		{
			String action = config.getString(path + "action");
			config.set(path + "action.left-click.id", ParticleAction.fromName(action, ParticleAction.DUMMY).getName());
		}
		
		if (config.contains(path + "command"))
		{
			String command = config.getString(path + "command");
			
			config.set(path + "command", null);
			config.set(path + "action.left-click.argument", command);
		}
		
		config.set(path + "animated", ParticleAnimation.fromBoolean(config.getBoolean(path + "animated")).getName());
	}
	
	/**
	 * Updates properties shared between hats & nodes
	 * @param config
	 * @param path
	 */
	private static void updateNodeFormat (FileConfiguration config, String legacyPath, String newPath, int index)
	{
		String nodePath = newPath + "nodes." + index + ".";
		
		if (config.contains(legacyPath + "type"))
		{
			config.set(nodePath + "type", config.getString(legacyPath + "type"));
			config.set(legacyPath + "type", null);
		}
		
		if (config.contains(legacyPath + "location"))
		{
			config.set(nodePath + "location", config.getString(legacyPath + "location"));
			config.set(legacyPath + "location", null);
		}
		
		if (config.contains(legacyPath + "mode"))
		{
			config.set(nodePath + "mode", config.getString(legacyPath + "mode"));
			config.set(legacyPath + "mode", null);
		}
		
		if (config.contains(legacyPath + "tracking"))
		{
			config.set(nodePath + "tracking", config.getString(legacyPath + "tracking"));
			config.set(legacyPath + "tracking", null);
		}
		
		if (config.contains(legacyPath + "animated"))
		{
			config.set(nodePath + "animated", ParticleAnimation.fromBoolean(config.getBoolean(legacyPath + "animated")).getName());
			config.set(legacyPath + "animated", null);
		}
		
		if (config.contains(legacyPath + "count"))
		{
			config.set(nodePath + "count", config.getInt(legacyPath + "count"));
			config.set(legacyPath + "count", null);
		}
		
		if (config.contains(legacyPath + "speed"))
		{
			config.set(nodePath + "speed", config.getInt(legacyPath + "speed"));
			config.set(legacyPath + "speed", null);
		}
		
		if (config.isDouble(legacyPath + "offset") || config.isInt(legacyPath + "offset")) {
			config.set(nodePath + "offset.y", config.getDouble(legacyPath + "offset"));
		}
		
		else
		{
			if (config.contains(legacyPath + "offset.x"))
			{
				config.set(nodePath + "offset.x", config.getDouble(legacyPath + "offset.x"));
				config.set(legacyPath + "offset.x", null);
			}
			
			if (config.contains(legacyPath + "offset.y"))
			{
				config.set(nodePath + "offset.y", config.getDouble(legacyPath + "offset.y"));
				config.set(legacyPath + "offset.y", null);
			}
			
			if (config.contains(legacyPath + "offset.z"))
			{
				config.set(nodePath + "offset.z", config.getDouble(legacyPath + "offset.z"));
				config.set(legacyPath + "offset.z", null);
			}
		}
		
		updateParticlesFormat(config, legacyPath, nodePath);
		
		if (config.contains(legacyPath + "node")) {
			updateNodeFormat(config, legacyPath + "node.", newPath, index + 1);
		}
	}
	
	/**
	 * Updates legacy particle data
	 * @param config
	 * @param legacyPath
	 * @param newPath
	 */
	private static void updateParticlesFormat (FileConfiguration config, String legacyPath, String newPath)
	{
		if (config.contains(legacyPath + "particle"))
		{
			String particle = config.getString(legacyPath + "particle");
			
			config.set(legacyPath + "particle", null);
			config.set(newPath + "particles.1.particle", particle);
			
			if (config.contains(legacyPath + "color"))
			{
				int r = config.getInt(legacyPath + "color.r");
				int g = config.getInt(legacyPath + "color.g");
				int b = config.getInt(legacyPath + "color.b");
				
				config.set(legacyPath + "color", null);
				config.set(newPath + "particles.1.color.r", r);
				config.set(newPath + "particles.1.color.g", g);
				config.set(newPath + "particles.1.color.b", b);
			}
			
			else {
				config.set(newPath + "particles.1.color", "random");
			}
			
			if (config.contains(legacyPath + "block-data")) 
			{
				String blockMaterial = config.getString(legacyPath + "block-data.id");
				int durability = config.getInt(legacyPath + "block-data.damage-value");
				
				config.set(legacyPath + "block-data", null);
				config.set(newPath + "particles.1.block-data.id", blockMaterial);
				config.set(newPath + "particles.1.block-data.damage-value", durability);
			}
		}
	}
	
	/**
	 * Checks this configuration file for updates and returns true if updates are found
	 * @param config
	 * @return
	 */
	public static boolean checkConfigForUpdates (FileConfiguration config)
	{
		if (!config.contains("version")) {
			return false;
		}
		return config.getDouble("version") < CONFIG_VERSION;
	}
	
	public static void updateConfig (ParticleHats core, FileConfiguration config)
	{
		if (config.getDouble("version") >= CONFIG_VERSION) {
			return;
		}
		
		// Update our legacy config
		if (config.isConfigurationSection("defaults")) {
			updateLegacyConfig(config);
		}
		
		// Update any missing values
		for (SettingsManager setting : SettingsManager.values())
		{
			if (!config.isSet(setting.getKey())) {
				config.set(setting.getKey(), setting.getDefaultConfigValue());
			}
		}
		
		config.set("version", CONFIG_VERSION);
		core.saveConfig();
	}
	
	private static void updateLegacyConfig (FileConfiguration config)
	{
		// Loop through all legacy config keys and update
		for (LegacyType type : LegacyType.values())
		{
			switch (type.dataType)
			{
			case INT:
				int i = config.getInt(type.oldKey, (int) type.defaultValue);
				config.set(type.oldKey, null);
				config.set(type.newKey, i);
				break;
			
			case DOUBLE:
				double d = config.getDouble(type.oldKey, (Double) type.defaultValue);
				config.set(type.oldKey, null);
				config.set(type.newKey, d);
				break;
				
			case STRING:
				String s = config.getString(type.oldKey, (String) type.defaultValue);
				config.set(type.oldKey, null);
				config.set(type.newKey, s);
				break;
				
			case BOOLEAN:
				boolean b = config.getBoolean(type.oldKey, (Boolean) type.defaultValue);
				config.set(type.oldKey, null);
				config.set(type.newKey, b);
				break;
				
			case STRING_LIST:
				List<String> l = config.getStringList(type.oldKey);
				config.set(type.oldKey, null);
				config.set(type.newKey, l);
				break;
				
			case DEPRECATED:
				config.set(type.oldKey, null);
				break;
				
			default:
				break;
			}
		}
		
		config.set("defaults", null);
	}
	
	private enum LegacyType {
		
		GENERATE_CONFIG_FILES ("defaults.generate_config_files", "load-included-menus", Type.BOOLEAN, true),
		DISABLED_WORLDS ("defaults.disabled_worlds", "disabled-worlds", Type.STRING_LIST, new ArrayList<String>()),
		CHECK_WORLD_PERMISSION ("defaults.check_world_permission", "check-world-permission", Type.BOOLEAN, false),
		HALO ("defaults.halo", "", Type.DEPRECATED, 0),
		EFFECT ("defaults.effect", "", Type.DEPRECATED, 0),
		FLAGS_VAULT ("defaults.flags.vault", "flags.vault", Type.BOOLEAN, false),
		FLAGS_EXPERIENCE ("defaults.flags.experience", "flags.experience", Type.BOOLEAN, false),
		FLAGS_PERMISSION ("defaults.flags.permission", "flags.permission", Type.BOOLEAN, true),
		FLAGS_VANISH ("defaults.flags.vanish", "flags.vanish", Type.BOOLEAN, false),
		CURRENCY_TYPE ("defaults.currency_type", "currency", Type.STRING, "$"),
		CLOSE_MENU_ON_EQUIP ("defaults.close_menu_on_equip", "close-menu-on-equip", Type.BOOLEAN, true),
		MENU_SOUND_ENABLED ("defaults.menu_sound.enabled", "menu.sound.enabled", Type.BOOLEAN, true),
		MENU_SOUND ("defaults.menu_sound.sound", "menu.sound.id", Type.STRING, "UI_BUTTON_CLICK"),
		MENU_SOUND_VOLUME ("defaults.menu_sound.volume", "menu.sound.volume", Type.DOUBLE, 1.0),
		MENU_SOUND_PITCH ("defaults.menu_sound.pitch", "menu.sound.pitch", Type.DOUBLE, 1.0),
		AFK_COOLDOWN ("defaults.afk.cooldown", "afk.cooldown", Type.INT, 7),
		COMBAT_COOLDOWN ("defaults.combat.cooldown", "combat.cooldown", Type.INT, 5),
		COMBAT_CHECK_PLAYERS ("defaults.combat.check_players", "combat.check-players", Type.BOOLEAN, true),
		COMBAT_CHECK_MONSTERS ("defaults.combat.check_monsters", "combat.check-monsters", Type.BOOLEAN, true),
		COMBAT_CHECK_ANIMALS ("defaults.combat.check_animals", "combat.check-animals", Type.BOOLEAN, false),
		COMBAT_CHECK_NPC ("defaults.combat.check_npc", "combat.check-npc", Type.BOOLEAN, false),
		OPEN_MENU_WITH_ITEM_ENABLED ("defaults.open_menu_with_item.enabled", "menu.open-menu-with-item.enabled", Type.BOOLEAN, false),
		OPEN_MENU_WITH_ITEM_DEFAULT_MENU ("defaults.open_menu_with_item.default_menu", "menu.open-menu-with-item.default-menu", Type.STRING, "particles.yml"),
		OPEN_MENU_WITH_ITEM_ID ("defaults.open_menu_with_item.id", "menu.open-menu-with-item.id", Type.STRING, "NETHER_STAR"),
		OPEN_MENU_WITH_ITEM_DAMAGE_VALUE ("defaults.open_menu_with_item.damage-value", "menu.open-menu-with-item.damage-value", Type.INT, 0);
		
		private final String oldKey;
		private final String newKey;
		private final Type dataType;
		private final Object defaultValue;
		
		private LegacyType (final String oldKey, final String newKey, final Type dataType, final Object defaultValue)
		{
			this.oldKey = oldKey;
			this.newKey = newKey;
			this.dataType = dataType;
			this.defaultValue = defaultValue;
		}
	}
}
