package com.mediusecho.particlehats.database.type.yaml;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.configuration.CustomConfig;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.HatReference;
import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.particles.effects.PixelEffect;
import com.mediusecho.particlehats.particles.properties.ColorData;
import com.mediusecho.particlehats.particles.properties.IconData;
import com.mediusecho.particlehats.particles.properties.IconDisplayMode;
import com.mediusecho.particlehats.particles.properties.ItemStackData;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.particles.properties.ParticleAnimation;
import com.mediusecho.particlehats.particles.properties.ParticleData;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleMode;
import com.mediusecho.particlehats.particles.properties.ParticleTag;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;
import com.mediusecho.particlehats.particles.properties.ParticleType;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.util.ResourceUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class YamlDatabase implements Database {

	private final ParticleHats core;
	
	private final CustomConfig groupConfig;
	
	private final double MENU_VERSION = 4.0;
	
	private final Map<String, CustomConfig> menus;
	private final Map<String, String> menuInfo;
	private final Map<String, ParticleLabel> labels;
	private final Map<String, String> aliases;
	private final Map<UUID, CustomConfig> playerConfigs;
	private final List<Group> groups;
	
	public YamlDatabase (ParticleHats core)
	{
		this.core = core;
		
		groupConfig = new CustomConfig(core, "", "groups.yml", true);
		
		menus = new HashMap<String, CustomConfig>();
		menuInfo = new HashMap<String, String>();
		labels = new HashMap<String, ParticleLabel>();
		aliases = new HashMap<String, String>();
		playerConfigs = new HashMap<UUID, CustomConfig>();
		groups = new ArrayList<Group>();
		
		onReload();
	}
	
	@Override
	public void onDisable() { }
	
	@Override
	public boolean isEnabled () {
		return true;
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
		final String alias = config.getString("settings.alias");
		final MenuInventory inventory = new MenuInventory(menuName, menuTitle, menuSize, alias);
		
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
						
						ItemStack item = hat.getItem();//ItemUtil.createItem(hat.getMaterial(), 1);
						ItemUtil.setItemName(item, hat.getDisplayName());
						
						loadMetaData(config, hat, path, item);
						
						inventory.setItem(slot, item);
						inventory.setHat(slot, hat);
					}
				}
			}
		}
		
		return inventory;
	}
	
	@Override
	public MenuInventory getInventoryFromAlias (String alias, PlayerState playerState)
	{
		if (!aliases.containsKey(alias)) {
			return null;
		}
		
		return loadInventory(aliases.get(alias), playerState);
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
		return new ArrayList<String>(labels.keySet());
	}
	
	@Override
	public Map<String, String> getGroups(boolean forceUpdate) 
	{	
		Collections.sort(groups, new Comparator<Group>()
		{
			@Override
			public int compare (Group g1, Group g2) {
				return Integer.compare(g1.getWeight(), g2.getWeight());
			}
		});

		Map<String, String> groupMap = new LinkedHashMap<String, String>();
		for (Group g : groups) {
			groupMap.put(g.getName(), g.getMenuName());
		}
		return groupMap;
	}
	
	@Override
	public boolean labelExists(String menuName, String label) {
		return labels.containsKey(label);
	}
	
	@Override
	public Hat getHatFromLabel(String label) 
	{
		if (!labels.containsKey(label)) {
			return null;
		}
		
		ParticleLabel pl = labels.get(label);
		if (pl == null) {
			return null;
		}
		
		CustomConfig menuConfig = pl.getConfig();
		FileConfiguration config = menuConfig.getConfig();
		String path = "items." + pl.getSlot() + ".";
		
		Hat hat = new Hat();
		loadBaseHatData(config, hat, path);
		loadEssentialHatData(config, hat, path, menuConfig.getName(), pl.getSlot());
		
		return hat;
	}

	@Override
	public void createHat(String menuName, int slot) {}
	
	@Override
	public void loadHat(String menuName, int slot, Hat hat) {
		ParticleHats.debug("should we load a hat?");
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
	public void moveHat(Hat fromHat, Hat toHat, String fromMenu, String toMenu, int fromSlot, int toSlot, boolean swapping) 
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
				if (swapping && toHat != null)
				{
					config.set("items." + fromSlot, null);
					config.set("items." + toSlot, null);
					
					String toPath = "items." + fromSlot + ".";
					setBaseHatData(config, toPath, toHat);
					setEssentialHatData(config, toPath, toHat);
					
					String fromPath = "items." + toSlot + ".";
					setBaseHatData(config, fromPath, fromHat);
					setEssentialHatData(config, fromPath, fromHat);
					
					config.save();
				}
				
				else
				{
					config.set("items." + fromSlot, null);
					String path = "items." + toSlot + ".";
					
					setBaseHatData(config, path, fromHat);
					setEssentialHatData(config, path, fromHat);
					config.save();
				}
			}
			
			else
			{
				if (!menus.containsKey(toMenu)) {
					return;
				}
				
				CustomConfig toConfig = menus.get(toMenu);
				if (toConfig != null)
				{
					String path = "items." + toSlot + ".";
					setBaseHatData(toConfig, path, toHat);
					setEssentialHatData(toConfig, path, toHat);
					
					toConfig.save();
					
					config.set("items." + fromSlot, null);
					config.save();
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
			
			ParticleHats.debug("deleting node " + (nodeIndex + 1));
			
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
					if (!hat.getIconData().getItems().isEmpty()) 
					{
						List<String> items = hat.getIconData().getItemNames();
						
						// Remove the first entry since that is saved as 'id'
						items.remove(0);
						
						config.set(path + "icons", items);
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
					} else {
						config.set(path + "tags", null);
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
	public void saveMenuAlias (String menuName, String alias)
	{
		if (!menus.containsKey(menuName)) {
			return;
		}
		
		CustomConfig config = menus.get(menuName);
		if (config != null)
		{
			config.set("settings.alias", alias);
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
	public void savePlayerEquippedHats(UUID id, List<Hat> hats) 
	{
		CustomConfig playerConfig = getPlayerConfig(id);
		
		List<String> equippedHats = playerConfig.getConfig().getStringList("equipped-hats");
		for (Hat hat : hats) {
			equippedHats.add(hat.getMenu() + ":" + hat.getSlot());
		}
		
		playerConfig.set("equipped-hats", equippedHats);
		playerConfig.save();
	}

	@Override
	public void loadPlayerEquippedHats(UUID id, DatabaseCallback callback) 
	{
		CustomConfig playerConfig = getPlayerConfig(id);
		
		List<String> equippedHats = playerConfig.getConfig().getStringList("equipped-hats");
		List<Hat> loadedHats = new ArrayList<Hat>();
		
		for (String hatReference : equippedHats)
		{
			String[] info = hatReference.split(":");
			String menuName = info[0];
			
			if (!menus.containsKey(menuName)) {
				continue;
			}
			
			CustomConfig menuConfig = menus.get(menuName);
			if (menuConfig != null)
			{
				String path = "items." + info[1] + ".";
				int slot = StringUtil.toInt(info[1], 0);
				
				FileConfiguration config = menuConfig.getConfig();
				Hat hat = new Hat();
				
				loadBaseHatData(config, hat, path);
				loadEssentialHatData(config, hat, path, info[0], slot);
				
				loadedHats.add(hat);
			}
		}
		
		callback.execute(loadedHats);
	}

	@Override
	public void savePlayerPurchase(UUID id, Hat hat) 
	{
		CustomConfig playerConfig = getPlayerConfig(id);
		
		List<String> purchases = playerConfig.getConfig().getStringList("purchased-hats");
		purchases.add(hat.getMenu() + ":" + hat.getSlot());
		
		playerConfig.set("purchased-hats", purchases);
		playerConfig.save();
	}

	@Override
	public void loadPlayerPurchasedHats(UUID id, DatabaseCallback callback) 
	{
		CustomConfig playerConfig = getPlayerConfig(id);
		
		List<HatReference> purchasedHats = new ArrayList<HatReference>();
		List<String> purchases = playerConfig.getConfig().getStringList("purchased-hats");
		
		for (String purchase : purchases)
		{
			String[] info = purchase.split(":");
			purchasedHats.add(new HatReference(info[0], StringUtil.toInt(info[1], -1)));
		}
		
		callback.execute(purchasedHats);
	}

	@Override
	public void addGroup(String groupName, String defaultMenu, int weight) 
	{
		groupConfig.set(groupName + ".default-menu", defaultMenu);
		groupConfig.set(groupName + ".weight", weight);
		groupConfig.save();
		
		groups.add(new Group(groupName, defaultMenu, weight));
	}

	@Override
	public void deleteGroup(String groupName) 
	{	
		Group group = getGroup(groupName);
		if (group != null) 
		{
			groups.remove(group);
			
			groupConfig.set(groupName, null);
			groupConfig.save();
		}
	}

	@Override
	public void editGroup(String groupName, String defaultMenu, int weight) 
	{	
		Group group = getGroup(groupName);
		if (group != null)
		{
			group.setMenuName(defaultMenu);
			groupConfig.set(groupName + ".default-menu", defaultMenu);
			
			if (weight >= 0) 
			{
				group.setWeight(weight);
				groupConfig.set(groupName + ".weight", weight);
			}
			
			groupConfig.save();
		}
	}

	@Override
	public boolean deleteImage(String imageName) 
	{
		return false;
	}

	@Override
	public void onReload ()
	{
		menus.clear();
		menuInfo.clear();
		groups.clear();
		labels.clear();
		aliases.clear();
		
		File menusFolder = new File(core.getDataFolder() + File.separator + "menus");
		if (!menusFolder.isDirectory())
		{
			ParticleHats.log("Unable to find menus folder");
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
						String menuName = ResourceUtil.removeExtension(menu.getName());
						menus.put(menuName, menuConfig);
						menuInfo.put(menuName, menuConfig.getConfig().getString("settings.title", ""));
						
						String alias = menuConfig.getConfig().getString("settings.alias");
						if (alias != null) {
							aliases.put(alias, menuName);
						}
						
						FileConfiguration config = menuConfig.getConfig();
						
						// Update our menus save format if this was created in an older version
						if (!config.contains("version")) {
							updateMenuFormat(menuConfig);
						}
						
						// Find any labels in this menu
						if (config.contains("items"))
						{
							Set<String> keys = config.getConfigurationSection("items").getKeys(false);
							for (String key : keys)
							{
								if (key == null) {
									continue;
								}
								
								String path = "items." + key + ".";
								if (config.contains(path + "label"))
								{
									String label = config.getString(path + "label");
									if (!labels.containsKey(label)) 
									{
										int slot = StringUtil.toInt(key, -1);
										if (slot != -1) {
											labels.put(label, new ParticleLabel(menuConfig, slot));
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		groupConfig.reload();
		FileConfiguration config = groupConfig.getConfig();
		for (String key : config.getKeys(false)) 
		{
			String menuName = config.getString(key + ".default-menu");
			int weight = config.getInt(key + ".weight", 0);
			groups.add(new Group(key, menuName, weight));
		}
	}
	
	@SuppressWarnings("deprecation")
	private void setParticleData (CustomConfig config, String path, Hat hat, int index)
	{
		ParticleEffect particle = hat.getParticle(index);
		if (particle != ParticleEffect.NONE)
		{
			config.set(path + "particle", particle.getName());
			ParticleData data = hat.getParticleData(index);
			
			switch (particle.getProperty())
			{
				case NO_DATA:
					break;
					
				case COLOR:
				{
					ColorData colorData = data.getColorData();
					if (colorData.isRandom()) {
						config.set(path + "color", "random");
					} else {
						config.set(path + "color", colorData.getColor().asRGB());
					}
					
					if (data.getScale() != 1.0) {
						config.set(path + "size", data.getScale());
					}
					
					break;
				}
				
				case BLOCK_DATA:
				{
					ItemStack block = data.getBlock();
					if (ParticleHats.serverVersion < 13)
					{
						config.set(path + "block-data.id", block.getType().toString());
						config.set(path + "block-data.damage-value", block.getDurability());
					}
					
					else {
						config.set(path + "block-data", block.getType().toString());
					}
					break;
				}
				
				case ITEM_DATA:
				{
					ItemStack item = data.getItem();
					if (ParticleHats.serverVersion < 13)
					{
						config.set(path + "item-data.id", item.getType().toString());
						config.set(path + "item-data.damage-value", item.getDurability());
					}
					
					else {
						config.set(path + "item-data", data.getItem().getType().toString());
					}
					break;
				}
				
				case ITEMSTACK_DATA:
				{
					ItemStackData itemStackData = data.getItemStackData();
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
							if (config.isString(path + "block-data")) {
								hat.getParticleData(index).setBlock(new ItemStack(ItemUtil.getMaterial(config.getString(path + "block-data"), Material.STONE)));
							}
							
							else
							{
								Material material = ItemUtil.getMaterial(config.getString(path + "block-data.id", "STONE"), Material.STONE);
								ItemStack block = ItemUtil.createItem(material, (short) config.getInt(path + "block-data.damage-value"));
								hat.getParticleData(index).setBlock(block);
							}
							break;
						}
						
						case ITEM_DATA:
						{
							if (config.isString(path + "item-data")) {
								hat.getParticleData(index).setItem(new ItemStack(ItemUtil.getMaterial(config.getString(path + "item-data"), Material.APPLE), 1));
							}
							
							else
							{
								Material material = ItemUtil.getMaterial(config.getString(path + "item-data.id", "STONE"), Material.STONE);
								ItemStack item = ItemUtil.createItem(material, (short) config.getInt(path + "item-data.damage-value"));
								hat.getParticleData(index).setItem(item);
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
	
	private void loadMetaData (FileConfiguration config, Hat hat, String path, ItemStack item)
	{
		if (config.contains(path + "icons"))
		{
			IconData iconData = hat.getIconData();
			boolean legacy = ParticleHats.serverVersion < 13;
			
			for (String material : config.getStringList(path + "icons")) 
			{
				if (legacy && material.contains(":")) 
				{
					String[] materialInfo = material.split(":");
					iconData.addItem(ItemUtil.createItem(materialInfo[0], Short.valueOf(materialInfo[1])));
				} 
				
				else {
					iconData.addItem(new ItemStack(ItemUtil.getMaterial(material, Material.STONE)));
				}
			}
		}
		
		if (config.contains(path + "tags"))
		{
			for (String tag : config.getStringList(path + "tags")) {
				hat.addTag(ParticleTag.fromName(tag));
			}
		}
		
		List<String> description = new ArrayList<String>();
		if (config.contains(path + "description")) 
		{
			List<String> desc = config.getStringList(path + "description");
			description.addAll(StringUtil.colorize(desc));
			hat.setDescription(desc);
		}
		
		if (config.contains(path + "permission-description"))
		{
			List<String> permissionDescription = StringUtil.colorize(config.getStringList(path + "permission-description"));
			if (SettingsManager.FLAG_PERMISSION.getBoolean() && hat.isLocked()) {
				description.addAll(permissionDescription);
			}
			hat.setPermissionDescription(permissionDescription);
		}
		
		if (!description.isEmpty())
		{
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setLore(description);
			item.setItemMeta(itemMeta);
		}
	}
	
	/**
	 * Sets data that is only needed for a menus base hat
	 * @param config
	 * @param path
	 * @param hat
	 */
	@SuppressWarnings("deprecation")
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
		
		config.set(path + "id", hat.getItem().getType().toString());
		if (ParticleHats.serverVersion < 13)  {
			config.set(path + "damage-value", hat.getItem().getDurability());
		}
		
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
		Material material = ItemUtil.getMaterial(config.getString(path + "id"), CompatibleMaterial.SUNFLOWER.getMaterial());
		if (ParticleHats.serverVersion < 13) {
			hat.setItem(ItemUtil.createItem(material, (short) config.getInt(path + "damage-value")));
		} else {
			hat.setItem(ItemUtil.createItem(material, 1));
		}
		
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
		hat.setAnimation(ParticleAnimation.fromName(config.getString(path + "animated")));	
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
		
		if (config.contains(path + "particles")) {
			loadParticleData(config, path + "particles", hat);
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
		
		hat.setLoaded(true);
		hat.clearPropertyChanges();
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
	
	/**
	 * Get this players CustomConfig file
	 * @param id
	 * @return
	 */
	private CustomConfig getPlayerConfig (UUID id)
	{
		if (playerConfigs.containsKey(id)) {
			return playerConfigs.get(id);
		}
		
		CustomConfig playerConfig = new CustomConfig(core, "players", id.toString(), false);
		playerConfigs.put(id, playerConfig);
		
		return playerConfig;
	}
	
	/**
	 * Gets the group matching this name
	 * @param groupName
	 * @return
	 */
	private Group getGroup (String groupName)
	{
		for (Group group : groups)
		{
			if (group.getName().equalsIgnoreCase(groupName)) {
				return group;
			}
		}
		return null;
	}
	
	private void updateMenuFormat (CustomConfig menuConfig)
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
				
				updateEssentialFormat(config, path);
				updateLegacyParticleFormat(config, path, path);
				
				if (config.contains(path + "node")) 
				{
					updateLegacyNodeFormat(config, path + "node.", path, 1);
					config.set(path + "node", null);
				}
			}
			
			menuConfig.save();
			menuConfig.reload();
		}
	}
	
	private void updateEssentialFormat (FileConfiguration config, String path)
	{
		if (config.contains(path + "no-permission")) 
		{
			String noPermission = config.getString(path + "no-permission");
			
			config.set(path + "no-permission", null);
			config.set(path + "permission-denied", noPermission);
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
			config.set(path + "action.left-click.id", action);
		}
		
		if (config.contains(path + "command"))
		{
			String command = config.getString(path + "command");
			
			config.set(path + "command", null);
			config.set(path + "action.left-click.argument", command);
		}
	}
	
	/**
	 * Updates this hat's particle data save format
	 * @param config
	 * @param path
	 */
	private void updateLegacyParticleFormat (FileConfiguration config, String legacyPath, String newPath)
	{
		if (config.contains(legacyPath + "particle"))
		{
			String particleName = config.getString(legacyPath + "particle");
			config.set(legacyPath + "particle", null);
			config.set(newPath + "particles.1.particle", particleName);
		}
		
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
	
	/**
	 * Updates this hat's node save format
	 * @param config
	 * @param path
	 */
	// TODO: Verify all node data is loaded
	private void updateLegacyNodeFormat (FileConfiguration config, String legacyPath, String newPath, int index)
	{	
		String type = config.getString(legacyPath + "type");
		String location = config.getString(legacyPath + "location");
		String mode = config.getString(legacyPath + "mode");
		String tracking = config.getString(legacyPath + "trcking");
		String animated = config.getString(legacyPath + "animated");
		
		int count = config.getInt(legacyPath + "count");
		int speed = config.getInt(legacyPath + "speed");
		
		double offsetX = config.getDouble(legacyPath + "offset.x");
		double offsetY = config.getDouble(legacyPath + "offset.y");
		double offsetZ = config.getDouble(legacyPath + "offset.z");
		
		String nodePath = newPath + "nodes." + index + ".";
		
		if (type != null) {
			config.set(nodePath + "type", type);
		}
		
		if (location != null) {
			config.set(nodePath + "location", location);
		}
		
		if (mode != null) {
			config.set(nodePath + "mode", mode);
		}
		
		if (tracking != null) {
			config.set(nodePath + "tracking", tracking);
		}
		
		if (animated != null) {
			config.set(nodePath + "animated", animated);
		}
		
		config.set(nodePath + "count", count);
		config.set(nodePath + "speed", speed);
		config.set(nodePath + "offset.x", offsetX);
		config.set(nodePath + "offset.y", offsetY);
		config.set(nodePath + "offset.z", offsetZ);
		
		updateLegacyParticleFormat(config, legacyPath, nodePath);
		
		if (config.contains(legacyPath + "node")) {
			updateLegacyNodeFormat(config, legacyPath + "node.", newPath, index + 1);
		}
	}
	
	private class ParticleLabel {
		
		private final CustomConfig config;
		private final int slot;
		
		public ParticleLabel (CustomConfig config, int slot)
		{
			this.config = config;
			this.slot = slot;
		}
		
		public CustomConfig getConfig () {
			return config;
		}
		
		public int getSlot () {
			return slot;
		}
	}
	
	private class Group {
		
		private final String name;
		private String menuName;
		private int weight;
		
		public Group (String name, String menuName, int weight)
		{
			this.name = name;
			this.menuName = menuName;
			this.weight = weight;
		}
		
		public String getName () {
			return name;
		}
		
		public String getMenuName () {
			return menuName;
		}
		
		public void setMenuName (String menuName) {
			this.menuName = menuName;
		}
		
		public int getWeight () {
			return weight;
		}
		
		public void setWeight (int weight) {
			this.weight = weight;
		}
	}
}
