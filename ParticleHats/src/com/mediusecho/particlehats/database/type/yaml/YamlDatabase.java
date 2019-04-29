package com.mediusecho.particlehats.database.type.yaml;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.configuration.CustomConfig;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.particles.effects.PixelEffect;
import com.mediusecho.particlehats.particles.properties.ColorData;
import com.mediusecho.particlehats.particles.properties.IconDisplayMode;
import com.mediusecho.particlehats.particles.properties.ItemStackData;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.particles.properties.ParticleAnimation;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleMode;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;
import com.mediusecho.particlehats.particles.properties.ParticleType;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.util.StringUtil;

// TODO: Implement yml database
public class YamlDatabase implements Database {

	private final Core core;
	
	private final CustomConfig groupConfig;
	
	private final Map<String, CustomConfig> menus;
	private final Map<String, String> menuInfo;
	private final Map<String, String> groups;
	private final List<String> labels;
	
	public YamlDatabase (Core core)
	{
		this.core = core;
		
		groupConfig = new CustomConfig(core, "", "groups.yml", true);
		
		menus = new HashMap<String, CustomConfig>();
		menuInfo = new HashMap<String, String>();
		groups = new HashMap<String, String>();
		labels = new ArrayList<String>();
		
		onReload();
	}
	
	@Override
	public void onDisable() 
	{
		
	}
	
	@Override
	public MenuInventory loadInventory (String menuName, PlayerState playerState) 
	{		
		if (!menus.containsKey(menuName)) {
			return null;
		}
		
		CustomConfig menuConfig = menus.get(menuName);
		FileConfiguration config = menuConfig.getConfig();
		
		final String menuTitle = config.getString("settings.title", "New Menu");
		final int menuSize = config.getInt("settings.size", 6);
		final MenuInventory inventory = new MenuInventory(menuName, menuTitle, menuSize);
		
		if (config.contains("items"))
		{
			Set<String> keys = config.getConfigurationSection("items").getKeys(false);
			if (keys != null)
			{
				for (String key : keys)
				{
					if (key == null) {
						continue;
					}
					
					String path = "items." + key + ".";
					int slot = StringUtil.toInt(key, -1);
					
					if (slot > -1 && slot < inventory.getSize())
					{
						Hat hat = new Hat();
						
						loadBaseHatData(config, hat, path);
						loadEssentialHatData(config, hat, path, menuName, slot);
						
						if (!playerState.hasPurchased(hat))
						{
							Player player = playerState.getOwner();
							hat.setLocked(
									!player.hasPermission(hat.getFullPermission()) && 
									!player.hasPermission(Permission.PARTICLE_ALL.getPermission()));
						}
						
						ItemStack item = ItemUtil.createItem(hat.getMaterial(), 1);
						ItemUtil.setItemName(item, hat.getDisplayName());
						
						inventory.setItem(slot, item);
						inventory.setHat(slot, hat);
					}
				}
			}
		}
		
		return inventory;
	}
	
	@Override
	public void createMenu(String menuName) 
	{
		CustomConfig menuConfig = new CustomConfig(core, "menus", menuName + ".yml", true);
		
		menuConfig.set("settings.title", menuName);
		menuConfig.set("settings.size", 6);
		menuConfig.save();
		
		menus.put(menuName, menuConfig);
		menuInfo.put(menuName, menuName);
	}
	
	@Override
	public void deleteMenu(String menuName) 
	{
		if (!menus.containsKey(menuName)) {
			return;
		}
		
		CustomConfig config = menus.get(menuName);
		if (config.delete()) 
		{
			menus.remove(menuName);
			menuInfo.remove(menuName);
		}
	}
	
	@Override
	public boolean menuExists(String menuName) {
		return menus.containsKey(menuName);
	}

	@Override
	public Map<String, String> getMenus(boolean forceUpdate) {
		return menuInfo;
	}
	
	@Override
	public Map<String, BufferedImage> getImages (boolean forceUpdate) {
		return core.getResourceManager().getImages();
	}
	
	@Override
	public List<String> getLabels (boolean forceUpdate) {
		return labels;
	}
	
	@Override
	public Map<String, String> getGroups(boolean forceUpdate) {
		return groups;
	}
	
	@Override
	public boolean labelExists(String menuName, String label) {
		return labels.contains(label);
	}
	
	@Override
	public Hat getHatFromLabel(String label) {
		return null;
	}

	@Override
	public void createHat(String menuName, int slot) {}
	
	@Override
	public void loadHat(String menuName, int slot, Hat hat) {
		Core.debug("should we load a hat?");
	}
	
	@Override
	public void saveHat (String menuName, int slot, Hat hat)
	{
		if (!menus.containsKey(menuName)) {
			return;
		}
		
		CustomConfig config = menus.get(menuName);
		if (config != null)
		{
			String path = "items." + slot + ".";
			
			// Reset this slot
			config.set("items." + slot, "");
			
			setBaseHatData(config, path, hat);
			setEssentialHatData(config, path, hat);
			
			config.save();
		}
	}
	
	@Override
	public void cloneHat(String menuName, Hat hat, int newSlot) 
	{
		if (!menus.containsKey(menuName)) {
			return;
		}
		
		CustomConfig config = menus.get(menuName);
		if (config != null)
		{
			String path = "items." + newSlot + ".";
			setBaseHatData(config, path, hat);
			setEssentialHatData(config, path, hat);
			
			config.save();
		}
	}
	
	@Override
	public void moveHat(String fromMenu, String toMenu, int fromSlot, int toSlot, boolean swapping) 
	{
		if (!menus.containsKey(fromMenu)) {
			return;
		}
		
		CustomConfig config = menus.get(fromMenu);
		if (config != null)
		{
			// Working inside the fromMenu only
			if (toMenu == null)
			{
				if (swapping)
				{
					
				}
				
				else
				{
					
				}
			}
		}
	}

	@Override
	public void deleteHat(String menuName, int slot) 
	{
		if (!menus.containsKey(menuName)) {
			return;
		}
		
		CustomConfig config = menus.get(menuName);
		if (config != null)
		{
			config.set("items." + slot, null);
			config.save();
		}
	}
	
	@Override
	public void saveNode (String menuName, int nodeIndex, Hat hat)
	{
		if (!menus.containsKey(menuName)) {
			return;
		}
		
		CustomConfig config = menus.get(menuName);
		if (config != null)
		{
			String path = "items." + hat.getSlot() + ".nodes." + (nodeIndex + 1);
			config.set(path, "");
			
			setEssentialHatData(config, path + ".", hat);
			
			config.save();
		}
	}
	
	@Override
	public void deleteNode(String menuName, int slot, int nodeIndex) 
	{
		if (!menus.containsKey(menuName)) {
			return;
		}
		
		CustomConfig config = menus.get(menuName);
		if (config != null)
		{
			String path = "items." + slot + ".nodes." + (nodeIndex + 1);
			
			Core.debug("deleting node " + (nodeIndex + 1));
			
			config.set(path, null);
			config.save();
		}
	}
	
	@Override
	public void saveParticleData(String menuName, Hat hat, int index) 
	{
		if (!menus.containsKey(menuName)) {
			return;
		}
		
		CustomConfig config = menus.get(menuName);
		if (config != null)
		{
			String path = "items." + hat.getSlot() + "." + getNodePath(hat) + "particles." + (index + 1);
			
			// Reset this particle
			config.set(path, "");
			path += ".";
			
			setParticleData(config, path, hat, index);
			config.save();
		}
	}
	
	@Override
	public void saveMetaData(String menuName, Hat hat, DataType type, int index) 
	{
		if (!menus.containsKey(menuName)) {
			return;
		}
		
		CustomConfig config = menus.get(menuName);
		if (config != null)
		{
			String path = "items." + hat.getSlot() + ".";
			switch (type)
			{
				case NONE:
					break;
					
				case DESCRIPTION:
				{
					if (!hat.getDescription().isEmpty()) {
						config.set(path + "description", hat.getDescription());
					}
					break;
				}
				
				case PERMISSION_DESCRIPTION:
				{
					if (!hat.getPermissionDescription().isEmpty()) {
						config.set(path + "permission-description", hat.getPermissionDescription());
					}
					break;
				}
				
				case ICON:
				{
					if (!hat.getIconData().getMaterials().isEmpty()) {
						config.set(path + "icons", hat.getIconData().getMaterialNames());
					}
					break;
				}
				
				case ITEMSTACK:
				{
					String itemPath = path + getNodePath(hat) + "particles." + (index + 1) + ".";
					setItemStackItems(config, hat, index, itemPath);
					break;
				}
				
				case TAGS:
				{
					if (!hat.getTags().isEmpty()) {
						config.set(path + "tags", hat.getTagNames());
					}
					break;
				}
			}
		}
		
		config.save();
	}

	@Override
	public void saveMenuTitle(String menuName, String title) 
	{
		if (!menus.containsKey(menuName)) {
			return;
		}
		
		CustomConfig config = menus.get(menuName);
		if (config != null)
		{
			config.set("settings.title", title);
			config.save();
		}
	}

	@Override
	public void saveMenuSize(String menuName, int rows) 
	{
		if (!menus.containsKey(menuName)) {
			return;
		}
		
		CustomConfig config = menus.get(menuName);
		if (config != null)
		{
			config.set("settings.size", rows);
			config.save();
		}
	}
	
	@Override
	public void savePlayerEquippedHats(UUID id, List<Hat> hats) {
		
	}

	@Override
	public void loadPlayerEquippedHats(UUID id, DatabaseCallback callback) {
		
	}

	@Override
	public void savePlayerPurchase(UUID id, Hat hat) {
		
	}

	@Override
	public void loadPlayerPurchasedHats(UUID id, DatabaseCallback callback) {
		
	}

	@Override
	public void addGroup(String groupName, String defaultMenu, int weight) {
		
	}

	@Override
	public void deleteGroup(String groupName) {
		
	}

	@Override
	public void editGroup(String groupName, String defaultMenu, int weight) {
		
	}

	@Override
	public boolean deleteImage(String imageName) {
		return false;
	}

	@Override
	public void onReload ()
	{
		menus.clear();
		menuInfo.clear();
		groups.clear();
		
		File menusFolder = new File(core.getDataFolder() + File.separator + "menus");
		if (!menusFolder.isDirectory())
		{
			Core.log("Unable to find menus folder");
			return;
		}
		
		File[] files = menusFolder.listFiles();
		for (int i = 0; i < files.length; i++)
		{
			if (files[i].isFile())
			{
				File menu = files[i];
				if (menu.getName().endsWith(".yml") && !menu.getName().startsWith("."))
				{
					CustomConfig menuConfig = new CustomConfig(core, "menus", menu.getName(), false);
					if (menuConfig != null)
					{
						String menuName = FilenameUtils.removeExtension(menu.getName());
						menus.put(menuName, menuConfig);
						menuInfo.put(menuName, menuConfig.getConfig().getString("settings.title", ""));
					}
				}
			}
		}
		
		groupConfig.reload();
		for (String key : groupConfig.getConfig().getKeys(false)) {
			groups.put(key, groupConfig.getConfig().getString(key + ".default_menu"));
		}
	}
	
	private void setParticleData (CustomConfig config, String path, Hat hat, int index)
	{
		ParticleEffect particle = hat.getParticle(index);
		if (particle != ParticleEffect.NONE)
		{
			config.set(path + "particle", particle.getName());
			
			switch (particle.getProperty())
			{
				case NO_DATA:
					break;
					
				case COLOR:
				{
					ColorData colorData = hat.getParticleData(index).getColorData();
					if (colorData.isRandom()) {
						config.set(path + "color", "random");
					} else {
						config.set(path + "color", colorData.getColor().asRGB());
					}
					break;
				}
				
				case BLOCK_DATA:
				{
					config.set(path + "block-data", hat.getParticleData(index).getBlock().getMaterial().toString());
					break;
				}
				
				case ITEM_DATA:
				{
					config.set(path + "item-data", hat.getParticleData(index).getItem().getType().toString());
					break;
				}
				
				case ITEMSTACK_DATA:
				{
					ItemStackData itemStackData = hat.getParticleData(index).getItemStackData();
					setItemStackItems(config, hat, index, path);
					
					if (itemStackData.getDuration() != 20) {
						config.set(path + "item-duration", itemStackData.getDuration());
					}
					
					if (!itemStackData.hasGravity()) {
						config.set(path + "item-gravity", false);
					}
					
					Vector velocity = itemStackData.getVelocity();
					if (velocity.getX() != 0) {
						config.set(path + "item-velocity.x", velocity.getX());
					}
					
					if (velocity.getY() != 0) {
						config.set(path + "item-velocity.y", velocity.getY());
					}
					
					if (velocity.getY() != 0) {
						config.set(path + "item-velocity.y", velocity.getY());
					}
					break;
				}
			}
		}
	}
	
	private void loadParticleData (FileConfiguration config, String rootPath, Hat hat)
	{
		Set<String> keys = config.getConfigurationSection(rootPath).getKeys(false);
		if (keys != null)
		{
			for (String key : keys)
			{
				if (key == null) {
					continue;
				}
				
				String path = rootPath + "." + key + ".";				
				if (config.contains(path + "particle"))
				{
					int index = MathUtil.clamp(StringUtil.toInt(key, 1) - 1, 0, 100);
					ParticleEffect particle = ParticleEffect.fromName(config.getString(path + "particle", "NONE"));
					
					hat.setParticle(index, particle);
					
					switch (particle.getProperty())
					{
						case NO_DATA:
							break;
					
						case COLOR:
						{
							hat.setParticleScale(index, config.getDouble(path + "size", 1.0));
							
							// RGB int value
							if (config.isInt(path + "color")) {
								hat.getParticleData(index).getColorData().setColor(Color.fromRGB(config.getInt(path + "color")));
							}
							
							else if (config.isString(path + "color")) 
							{
								if (config.getString(path + "color", "").equals("random")) {
									hat.getParticleData(index).getColorData().setRandom(true);
								}
							}
							
							else
							{
								int r = config.getInt(path + "color.r", 255);
								int g = config.getInt(path + "color.g", 255);
								int b = config.getInt(path + "color.b", 255);
								hat.getParticleData(index).getColorData().setColor(Color.fromRGB(r, g, b));
							}
							break;
						}
						
						case BLOCK_DATA:
						{
							if (config.contains(path + "block-data")) {
								hat.getParticleData(index).setBlock(ItemUtil.getMaterial(config.getString(path + "block-data"), Material.STONE));
							}
							break;
						}
						
						case ITEM_DATA:
						{
							if (config.contains(path + "item-data")) {
								hat.getParticleData(index).setItem(new ItemStack(ItemUtil.getMaterial(config.getString(path + "item-data"), Material.APPLE), 1));
							}
							break;
						}
						
						case ITEMSTACK_DATA:
						{
							ItemStackData itemStackData = hat.getParticleData(index).getItemStackData();
							
							List<String> items = config.getStringList(path + "items");
							if (items.size() > 0)
							{
								for (String itemNane : items) {
									itemStackData.addItem(new ItemStack(ItemUtil.getMaterial(itemNane, Material.STONE), 1));
								}
							}
							
							if (config.contains(path + "item-duration")) {
								itemStackData.setDuration(config.getInt(path + "item-duration", 1));
							}
							
							if (config.contains(path + "item-gravity")) {
								itemStackData.setGravity(config.getBoolean(path + "item-duration"));
							}
							
							double x = config.getDouble(path + "item-velocity.x", 0);
							double y = config.getDouble(path + "item-velocity.y", 0);
							double z = config.getDouble(path + "item-velocity.z", 0);
							itemStackData.setVelocity(new Vector(x, y, z));
							break;
						}
					}
					
					hat.getParticleData(index).clearPropertyChanges();
				}
			}
		}
	}

	private void loadLegacyParticleData (FileConfiguration config, String path, Hat hat)
	{
		ParticleEffect particle = ParticleEffect.fromLegacyName(config.getString(path + "particle", "NONE"));
		hat.setParticle(0, particle);
		
		switch (particle.getProperty())
		{
			case NO_DATA:				
			case ITEMSTACK_DATA:
				break;
				
			case COLOR:
			{
				if (!config.isString(path + "color"))
				{
					int r = config.getInt(path + "color.r", 255);
					int g = config.getInt(path + "color.g", 255);
					int b = config.getInt(path + "color.b", 255);
					hat.getParticleData(0).getColorData().setColor(Color.fromRGB(r, g, b));
				}
				
				else {
					hat.getParticleData(0).getColorData().setRandom(config.getBoolean(path + "color"));
				}
				break;
			}
			
			case BLOCK_DATA:
			{
				if (config.contains(path + "block-data")) {
					hat.getParticleData(0).setBlock(ItemUtil.getMaterial(config.getString(path + "block-data"), Material.STONE));
				}
				break;
			}
			
			case ITEM_DATA:
			{
				if (config.contains(path + "item-data")) {
					hat.getParticleData(0).setItem(new ItemStack(ItemUtil.getMaterial(config.getString(path + "item-data"), Material.STONE), 1));
				}
				break;
			}
		}
		
		hat.getParticleData(0).clearPropertyChanges();
	}

//	private void loadNodeData (FileConfiguration config, String rootPath, int slot, Hat hat)
//	{
//		Set<String> keys = config.getConfigurationSection(rootPath).getKeys(false);
// 		
//		if (keys != null)
//		{
//			for (String key : keys)
//			{
//				if (key == null) {
//					continue;
//				}
//				
//				String path = rootPath + "." + key + ".";
//				int index = StringUtil.toInt(key, -1);
//				if (index > 0)
//				{
//					Hat node = new Hat();
//					
//					node.setSlot(slot);
//					node.setLocation(ParticleLocation.fromName(config.getString(path + "location")));
//					node.setMode(ParticleMode.fromName(config.getString(path + "mode")));
//					node.setAnimation(ParticleAnimation.fromName(config.getString(path + "animation")));
//					node.setTrackingMethod(ParticleTracking.fromName(config.getString(path + "tracking")));	
//					node.setOffset(config.getDouble(path + "offset.x", 0), config.getDouble(path + "offset.y", 0), config.getDouble(path + "offset.z", 0));
//					node.setAngle(config.getDouble(path + "angle.x"), config.getDouble(path + "angle.y"), config.getDouble(path + "angle.z"));
//					node.setUpdateFrequency(config.getInt(path + "update-frequency", 2));
//					node.setSpeed(config.getInt(path + "speed", 0));
//					node.setCount(config.getInt(path + "count", 1));
//					node.setScale(config.getDouble(path + "scale", 1.0));
//					
//					if (config.isString(path + "type")) {
//						node.setType(ParticleType.fromName(config.getString(path + "type")));
//					}
//					
//					else
//					{
//						node.setType(ParticleType.fromName(config.getString(path + "type.id")));
//						
//						String imageName = config.getString(path + "type.name");
//						if (core.getResourceManager().imageExists(imageName)) {
//							hat.setCustomType(new PixelEffect(core.getResourceManager().getImage(imageName), imageName));
//						}
//					}
//					
//					if (config.contains(path + "particles")) {
//						loadParticleData(config, path + "particles", node);
//					} else {
//						loadLegacyParticleData(config, path, node);
//					}
//					
//					node.setIndex(index - 1);
//					node.setLoaded(true);
//					node.clearPropertyChanges();
//					
//					hat.addNode(node);
//				}
//			}
//		}
//	}
	
	private void loadLegacyNodeData (FileConfiguration config, String path, Hat hat)
	{
		
	}
	
	/**
	 * Sets data that is only needed for a menus base hat
	 * @param config
	 * @param path
	 * @param hat
	 */
	private void setBaseHatData (CustomConfig config, String path, Hat hat)
	{
		config.set(path + "name", hat.getName());
		
		if (!hat.getLabel().equals("")) {
			config.set(path + "label", hat.getLabel());
		}
		
		if (!hat.getEquipMessage().equals("")) {
			config.set(path + "equip-message", hat.getEquipMessage());
		}
		
		if (!hat.getTags().isEmpty()) {
			config.set(path + "tags", hat.getTagNames());
		}
		
		config.set(path + "id", hat.getMaterial().toString());
		
		if (!hat.getDescription().isEmpty()) {
			config.set(path + "description", hat.getDescription());
		}
		
		if (!hat.getPermissionDescription().isEmpty()) {
			config.set(path + "permission-description", hat.getPermissionDescription());
		}
		
		if (!hat.getPermission().equals("all")) {
			config.set(path + "permission", hat.getPermission());
		}
		
		if (!hat.getPermissionDeniedMessage().equals("")) {
			config.set(path + "permission-denied", hat.getPermissionDeniedMessage());
		}
		
		ParticleAction leftAction = hat.getLeftClickAction();
		if (leftAction != ParticleAction.EQUIP)
		{
			config.set(path + "action.left-click.id", leftAction.getName());
			if (leftAction.hasData()) {
				config.set(path + "action.left-click.argument", hat.getLeftClickArgument());
			}
		}
		
		ParticleAction rightAction = hat.getRightClickAction();
		if (rightAction != ParticleAction.MIMIC)
		{
			config.set(path + "action.right-click.id", rightAction.getName());
			if (rightAction.hasData()) {
				config.set(path + "action.right-click.argument", hat.getRightClickArgument());
			}
		}
		
		Sound sound = hat.getSound();
		if (sound != null)
		{
			config.set(path + "sound.id", sound.toString());
			
			if (hat.getSoundVolume() != 1.0) {
				config.set(path + "sound.volume", hat.getSoundVolume());
			}
			
			if (hat.getSoundPitch() != 1.0) {
				config.set(path + "sound.pitch", hat.getSoundPitch());
			}
		}
		
		PotionEffect potion = hat.getPotion();
		if (potion != null)
		{
			config.set(path + "potion.id", potion.getType().getName());
			config.set(path + "potion.strength", potion.getAmplifier());
		}
	}
	
	/**
	 * Sets data that belongs to both base hats and node hats
	 * @param config
	 * @param path
	 * @param hat
	 */
	private void setEssentialHatData (CustomConfig config, String path, Hat hat)
	{
		ParticleType type = hat.getType();
		if (type != ParticleType.NONE)
		{
			if (type != ParticleType.CUSTOM) {
				config.set(path + "type", type.getName());
			}
			
			else
			{
				config.set(path + "type.id", type.getName());
				
				PixelEffect effect = hat.getCustomEffect();
				if (effect != null) {
					config.set(path + "type.name", effect.getImageNameWithoutExtension());
				}
			}
		}
		
		if (hat.getLocation() != ParticleLocation.HEAD) {
			config.set(path + "location", hat.getLocation().getName());
		}
		
		if (hat.getMode() != ParticleMode.ACTIVE) {
			config.set(path + "mode", hat.getMode().getName());
		}
		
		if (hat.getAnimation() == ParticleAnimation.ANIMATED) {
			config.set(path + "animated", true);
		}
		
		if (hat.getDemoDuration() != 200) {
			config.set(path + "duration", hat.getDemoDuration());
		}
		
		if (hat.getPrice() > 0) {
			config.set(path + "price", hat.getPrice());
		}
		
		if (hat.getSpeed() > 0) {
			config.set(path + "speed", hat.getSpeed());
		}
		
		if (hat.getCount() > 1) {
			config.set(path + "count", hat.getCount());
		}
		
		if (hat.getScale() != 1.0) {
			config.set(path + "scale", hat.getScale());
		}
		
		setVectorValues(config, hat.getOffset(), path + "offset.");
		setVectorValues(config, hat.getRandomOffset(), path + "random-offset.");
		setVectorValues(config, hat.getAngle(), path + "angle.");
		
		if (hat.getUpdateFrequency() != 2) {
			config.set(path + "update-frequency", hat.getUpdateFrequency());
		}
		
		if (hat.getTrackingMethod() != hat.getType().getEffect().getDefaultTrackingMethod()) {
			config.set(path + "tracking", hat.getSavedTrackingMethod().getName());
		}
		
		if (hat.getParticleCount() > 0)
		{
			for (int index : hat.getParticleData().keySet()) 
			{		
				String particlePath = path + "particles." + (index + 1) + ".";
				setParticleData(config, particlePath, hat, index);
			}
		}
		
		if (hat.getNodeCount() > 0)
		{
			for (Hat node : hat.getNodes())
			{
				String nodePath = path + "nodes." + (node.getIndex() + 1) + ".";
				setEssentialHatData(config, nodePath, node);
			}
		}
	}
	
	/**
	 * Loads all properties necessary for a base hat
	 * @param config
	 * @param hat
	 * @param path
	 */
	private void loadBaseHatData (FileConfiguration config, Hat hat, String path)
	{
		hat.setMaterial(ItemUtil.materialFromString(config.getString(path + "id"), Material.SUNFLOWER));
		hat.setName(config.getString(path + "name", Message.EDITOR_MISC_NEW_PARTICLE.getValue()));
		hat.setPermission(config.getString(path + "permission", "all"));	
		hat.setLabel(config.getString(path + "label", ""));
		hat.setEquipMessage(config.getString(path + "equip-message", ""));
		hat.setLeftClickAction(ParticleAction.fromName(config.getString(path + "action.left-click.id"), ParticleAction.EQUIP));
		hat.setRightClickAction(ParticleAction.fromName(config.getString(path + "action.right-click.id"), ParticleAction.MIMIC));
		hat.setLeftClickArgument(config.getString(path + "action.left-click.argument", ""));
		hat.setRightClickArgument(config.getString(path + "action.right-click.argument", ""));
		hat.setDemoDuration(config.getInt(path + "duration", 200));
		hat.setDisplayMode(IconDisplayMode.fromName(config.getString(path + "display-mode")));
		
		String potionName = config.getString(path + "potion.id", "");
		if (!potionName.equals(""))
		{
			PotionEffectType pt = PotionEffectType.getByName(potionName);
			if (pt != null) {
				hat.setPotion(pt, config.getInt(path + "potion.strength", 0));
			}
		}
		
		String soundName = config.getString(path + "sound.id", "");
		if (!soundName.equals(""))
		{
			Sound sound = Sound.valueOf(soundName);
			if (sound != null) {
				hat.setSound(sound);
			}
		}
		
		hat.setLoaded(true);
		hat.clearPropertyChanges();
	}
	
	/**
	 * Loads all essential properties for hats
	 */
	private void loadEssentialHatData (FileConfiguration config, Hat hat, String path, String menuName, int slot)
	{
		hat.setMenu(menuName);
		hat.setSlot(slot);
		hat.setLocation(ParticleLocation.fromName(config.getString(path + "location")));
		hat.setMode(ParticleMode.fromName(config.getString(path + "mode")));
		hat.setAnimation(ParticleAnimation.fromName(config.getString(path + "animation")));	
		hat.setOffset(config.getDouble(path + "offset.x", 0), config.getDouble(path + "offset.y", 0), config.getDouble(path + "offset.z", 0));
		hat.setRandomOffset(config.getDouble(path + "random-offset.x", 0), config.getDouble(path + "random-offset.y", 0), config.getDouble(path + "random-offset.z", 0));
		hat.setAngle(config.getDouble(path + "angle.x"), config.getDouble(path + "angle.y"), config.getDouble(path + "angle.z"));
		hat.setUpdateFrequency(config.getInt(path + "update-frequency", 2));
		hat.setSpeed(config.getInt(path + "speed", 0));
		hat.setCount(config.getInt(path + "count", 1));
		hat.setPrice(config.getInt(path + "price", 0));
		hat.setDemoDuration(config.getInt(path + "duration", 200));
		hat.setScale(config.getDouble(path + "scale", 1.0));
		
		if (config.isString(path + "type")) {
			hat.setType(ParticleType.fromName(config.getString(path + "type")));
		}
		
		else
		{
			hat.setType(ParticleType.fromName(config.getString(path + "type.id")));
			
			String imageName = config.getString(path + "type.name");
			if (core.getResourceManager().imageExists(imageName)) {
				hat.setCustomType(new PixelEffect(core.getResourceManager().getImage(imageName), imageName));
			}
		}
		
		if (config.contains(path + "tracking")) {
			hat.setTrackingMethod(ParticleTracking.fromName(config.getString(path + "tracking")));
		} else {
			hat.setTrackingMethod(hat.getType().getEffect().getDefaultTrackingMethod());
		}
		
		if (config.contains(path + "nodes")) 
		{
			Set<String> keys = config.getConfigurationSection(path + "nodes").getKeys(false);
			for (String key : keys)
			{
				if (key == null) {
					continue;
				}
				
				String nodePath = path + "nodes." + key + ".";
				
				Hat node = new Hat();
				
				node.setIndex(StringUtil.toInt(key, 1) - 1);
				loadEssentialHatData(config, node, nodePath, menuName, slot);
				
				hat.addNode(node);
			}
		}
//		// TODO: Test legacy format
//		if (config.contains(path + "nodes")) {
//			loadNodeData(config, path + "nodes", slot, hat);
//		} else {
//			loadLegacyNodeData(config, path, hat);
//		}
		
		if (config.contains(path + "particles")) {
			loadParticleData(config, path + "particles", hat);
		} else {
			loadLegacyParticleData(config, path, hat);
		}
		
		hat.setLoaded(true);
		hat.clearPropertyChanges();
	}
	
	private void loadMetaData (FileConfiguration config, Hat hat, String path)
	{
		
	}
	
	private void setItemStackItems (CustomConfig config, Hat hat, int index, String path)
	{
		ItemStackData itemStackData = hat.getParticleData(index).getItemStackData();
		if (!itemStackData.getItems().isEmpty()) {
			config.set(path + "items", itemStackData.getItemNames());
		}
	}
	
	private void setVectorValues (CustomConfig config, Vector vector, String path)
	{
		if (vector.getX() != 0) {
			config.set(path + "x", vector.getX());
		}
		
		if (vector.getY() != 0) {
			config.set(path + "y", vector.getY());
		}
		
		if (vector.getZ() != 0) {
			config.set(path + "z", vector.getZ());
		}
	}
	
	private String getNodePath (Hat hat)
	{
		if (hat.getIndex() < 0) {
			return "";
		}
		return "nodes." + (hat.getIndex() + 1) + ".";
	}
}
