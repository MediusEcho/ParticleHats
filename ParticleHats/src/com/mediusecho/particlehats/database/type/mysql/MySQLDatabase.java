package com.mediusecho.particlehats.database.type.mysql;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.database.type.DatabaseType.DatabaseCallback;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.particles.effects.PixelEffect;
import com.mediusecho.particlehats.particles.properties.IconData;
import com.mediusecho.particlehats.particles.properties.IconDisplayMode;
import com.mediusecho.particlehats.particles.properties.ItemStackData;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.particles.properties.ParticleAnimation;
import com.mediusecho.particlehats.particles.properties.ColorData;
import com.mediusecho.particlehats.particles.properties.ParticleData;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleMode;
import com.mediusecho.particlehats.particles.properties.ParticleTag;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;
import com.mediusecho.particlehats.particles.properties.ParticleType;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MySQLDatabase implements Database {

	private HikariDataSource dataSource;
	private MySQLHelper helper;
	
	private final String hostname = SettingsManager.DATABASE_HOSTNAME.asString();
	private final String username = SettingsManager.DATABASE_USERNAME.getString();
	private final String password = SettingsManager.DATABASE_PASSWORD.getString();
	private final String port     = SettingsManager.DATABASE_PORT.asString();
	private final String database = SettingsManager.DATABASE_DATABASE.getString();
	
	private Map<String, String> menuCache;
	private Map<String, BufferedImage> imageCache;
	
	private long lastMenuUpdate = 0L;
	private long lastImageUpdate = 0L;
	
	private boolean connected = false;
	
	// Fetch MySQL changes every 30 seconds
	private final long UPDATE_INTERVAL = 30000L;
	
	public MySQLDatabase (Core core, DatabaseConnectionCallback callback)
	{
		this.core = core;
		
		menuCache = new HashMap<String, String>();
		imageCache = new HashMap<String, BufferedImage>();
		
		helper = new MySQLHelper(this);
		
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useSSL=false");
		config.setUsername(username);
		config.setPassword(password);
		
		try 
		{
			dataSource = new HikariDataSource(config);
			helper.initDatabase(core);
			connected = true;
			
			Core.log("Using database: MySQL");
		}
		
		catch (Exception e) {
			callback.onTimeout(e);
		}
	}
	
	@Override
	public void onDisable () 
	{
		if (connected) {
			dataSource.close();
		}
	}

	@Override
	public MenuInventory loadInventory(String menuName, Player player)
	{		
		try (Connection connection = dataSource.getConnection())
		{
			String menuQuery = "SELECT * FROM menus WHERE name = ?";
			try (PreparedStatement menuStatement = connection.prepareStatement(menuQuery))
			{
				menuStatement.setString(1, menuName);
				ResultSet menuResult = menuStatement.executeQuery();
				
				while (menuResult.next())
				{
					final String menuTitle = ChatColor.translateAlternateColorCodes('&', menuResult.getString("title"));
					final int menuSize = menuResult.getInt("size");
					final MenuInventory inventory = new MenuInventory(menuName, menuTitle, menuSize);
					
					// Load a preview of this menu
					loadHat(connection, inventory, player);
					
					return inventory;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void createMenu(String menuName) 
	{
		async(() ->
		{
			connect((connection) ->
			{
				// Menu Entry
				String createMenuStatement= "INSERT INTO menus VALUES(?, ?, ?)";
				try (PreparedStatement statement = connection.prepareStatement(createMenuStatement))
				{
					statement.setString(1, menuName); // Name
					statement.setString(2, menuName); // Title (Same until title is changed)
					statement.setInt(3, 6);
					
					if (statement.executeUpdate() > 0)
					{						
						// Items Table
						String createMenuItemsTable = helper.getItemTableQuery(menuName);
						try (PreparedStatement itemsStatement = connection.prepareStatement(createMenuItemsTable)) {
							itemsStatement.execute();
						}
						
						// Nodes Table
						String createMenuNodesTable = helper.getNodeTableQuery(menuName);
						try (PreparedStatement nodesStatement = connection.prepareStatement(createMenuNodesTable)) {
							nodesStatement.execute();
						}
						
						// Meta Table
						String createMenuMetaTable = helper.getMetaTableQuery(menuName);
						try (PreparedStatement metaStatement = connection.prepareStatement(createMenuMetaTable)) {
							metaStatement.execute();
						}
						
						// Particle Table
						String createMenuParticleTable = helper.getParticleTableQuery(menuName);
						try (PreparedStatement particleStatement = connection.prepareCall(createMenuParticleTable)) {
							particleStatement.execute();
						}
						
						// Add this menu to the cache
						menuCache.put(menuName, menuName);
					}
				}
			});
		});
	}

	@Override
	public void deleteMenu (String menuName)
	{
		async(() ->
		{
			connect((connection) ->
			{
				String deleteQuery = "DELETE FROM menus WHERE name = ?";
				try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery))
				{
					deleteStatement.setString(1, menuName);
					if (deleteStatement.executeUpdate() >= 1)
					{
						String dropQuery = "DROP TABLE IF EXISTS "
								+ "menu_" + menuName + "_meta,"
								+ "menu_" + menuName + "_particles,"
								+ "menu_" + menuName + "_nodes,"
								+ "menu_" + menuName + "_items"; // Make sure the items table is deleted last since all menus reference this one
						
						try (PreparedStatement dropStatement = connection.prepareStatement(dropQuery)) {
							dropStatement.executeUpdate();
						}
					}
				}
			});
		});
	}
	
	@Override
	public boolean menuExists(String menuName) 
	{
		Map<String, String> menus = getMenus(true);
		return menus.containsKey(menuName);
	}
	
	@Override
	public Map<String, String> getMenus(boolean forceUpdate) 
	{	
		if (forceUpdate || (System.currentTimeMillis() - lastMenuUpdate) > UPDATE_INTERVAL)
		{
			lastMenuUpdate = System.currentTimeMillis();
			menuCache.clear();
			connect((connection) -> 
			{
				try (PreparedStatement statement = connection.prepareStatement("SELECT name, title FROM menus"))
				{
					ResultSet set = statement.executeQuery();
					while (set.next()) {
						menuCache.put(set.getString("name"), set.getString("title"));
					}
				}
			});
		}
		return new HashMap<String, String>(menuCache);
	}
	
	@Override
	public boolean labelExists(String menuName, String label) 
	{
		try (Connection connection = dataSource.getConnection())
		{
			String labelQuery = "SELECT COUNT(*) AS labels FROM menu_" + menuName + "_items WHERE label = ?";
			try (PreparedStatement statement = connection.prepareStatement(labelQuery))
			{
				statement.setString(1, label);
				ResultSet set = statement.executeQuery();
				while(set.next()) {
					return set.getInt("labels") > 0;
				}
				return false;
			}
		}
		
		catch (SQLException e) {
			return false;
		}
	}
	
	@Override
	public Hat getHatFromLabel (String label)
	{
		StringBuilder builder = new StringBuilder();
		for (Entry<String, String> menu : getMenus(true).entrySet()) {
			builder.append("SELECT slot, '").append(menu.getKey()).append("' AS TableName FROM menu_").append(menu.getKey()).append("_items WHERE label = '").append(label).append("' %");
		}
		builder.deleteCharAt(builder.lastIndexOf("%"));
		builder.append("LIMIT 1");
		
		try (Connection connection = dataSource.getConnection())
		{
			String query = builder.toString().replaceAll("%", "UNION ALL ");
			try (PreparedStatement statement = connection.prepareStatement(query))
			{
				ResultSet set = statement.executeQuery();
				while (set.next())
				{
					String menuName = set.getString("TableName");
					int slot = set.getInt("slot");
					
					Hat hat = new Hat();
					loadHat(menuName, slot, hat);
					
					return hat;
				}
				return null;
			}
		
		} 
		
		catch (SQLException e) {
			return null;
		}
	}
	
	@Override
	public void createHat(String menuName, int slot) 
	{
		async(() ->
		{
			connect((connection) ->
			{
				String createQuery = "INSERT INTO menu_" + menuName + "_items (slot) VALUES(?)";
				try (PreparedStatement createStatement = connection.prepareStatement(createQuery))
				{
					createStatement.setInt(1, slot);
					createStatement.execute();
				}
			});
		});
	}
	
	@Override
	public void loadHat (String menuName, int slot, Hat hat)
	{		
		connect((connection) ->
		{
			String hatQuery = "SELECT * FROM menu_" + menuName + "_items WHERE slot = ?";
			try (PreparedStatement hatStatement = connection.prepareStatement(hatQuery))
			{
				hatStatement.setInt(1, slot);
				ResultSet set = hatStatement.executeQuery();
				
				while (set.next())
				{
					hat.setSlot(set.getInt("slot"));
					hat.setMaterial(ItemUtil.materialFromString(set.getString("id"), Material.SUNFLOWER));
					hat.setName(set.getString("title"));
					hat.setPermission(set.getString("permission"));
					hat.setPermissionDeniedMessage(set.getString("permission_denied"));
					hat.setType(ParticleType.fromID(set.getInt("type")));
					hat.setLocation(ParticleLocation.fromId(set.getInt("location")));
					hat.setMode(ParticleMode.fromId(set.getInt("mode")));
					hat.setAnimation(ParticleAnimation.fromID(set.getInt("animation")));
					hat.setTrackingMethod(ParticleTracking.fromID(set.getInt("tracking")));
					hat.setLabel(set.getString("label"));
					hat.setEquipMessage(set.getString("equip_message"));
					hat.setOffset(set.getDouble("offset_x"), set.getDouble("offset_y"), set.getDouble("offset_z"));
					hat.setAngle(set.getDouble("angle_x"), set.getDouble("angle_y"), set.getDouble("angle_z"));
					hat.setUpdateFrequency(set.getInt("update_frequency"));		
					hat.setSpeed(set.getInt("speed"));
					hat.setCount(set.getInt("count"));
					hat.setPrice(set.getInt("price"));
					String soundName = set.getString("sound");
					if (soundName != null) {
						hat.setSound(Sound.valueOf(soundName));
					}
					hat.setSoundVolume(set.getDouble("volume"));
					hat.setSoundPitch(set.getDouble("pitch"));
					hat.setLeftClickAction(ParticleAction.fromId(set.getInt("left_action")));
					hat.setRightClickAction(ParticleAction.fromId(set.getInt("right_action")));
					hat.setLeftClickArgument(set.getString("left_argument"));
					hat.setRightClickArgument(set.getString("right_argument"));
					hat.setDemoDuration(set.getInt("duration"));
					hat.setDisplayMode(IconDisplayMode.fromId(set.getInt("display_mode")));
					hat.setScale(set.getDouble("scale"));
					
					String potion = set.getString("potion");
					if (potion != null)
					{
						PotionEffectType pt = PotionEffectType.getByName(potion);
						if (pt != null)
						{
							int amplifier = set.getInt("potion_strength");
							hat.setPotion(pt, amplifier);
						}
					}
					
					String customTypeName = set.getString("custom_type");
					if (customTypeName != null)
					{
						Map<String, BufferedImage> images = getImages(false);
						if (images.containsKey(customTypeName)) {
							hat.setCustomType(new PixelEffect(images.get(customTypeName), customTypeName));
						} else {
							Core.debug("failed to load image " + customTypeName);
						}
					}
					
					hat.clearPropertyChanges();
					hat.setLoaded(true);
				}
			}
			
			// Load nodes
			loadNodeData(connection, menuName, hat);
			
			// Load particles
			loadParticleData(connection, menuName, hat);
			
			// Load ItemStack data
			loadItemStackData(connection, menuName, hat);
		});
	}
	
	@Override
	public void cloneHat (String menuName, int currentSlot, int newSlot)
	{
		// Clone items
		cloneTableRow(Table.ITEMS.format(menuName), currentSlot, newSlot);
		
		// Clone Meta
		cloneTableRow(Table.META.format(menuName), currentSlot, newSlot);
		
		// Clone particles
		cloneTableRow(Table.PARTICLES.format(menuName), currentSlot, newSlot);
	}
	
	@Override
	public void moveHat (String fromMenu, String toMenu, int fromSlot, int toSlot, boolean swapping)
	{
		// Changing Slots
		if (toMenu == null)
		{
			if (swapping) {
				swapSlot(fromMenu, fromSlot, toSlot);
			} else {
				changeSlot(fromMenu, fromSlot, toSlot);
			}
		}
		
		else
		{
			// Move Items
			moveTableRow(Table.ITEMS.format(fromMenu), Table.ITEMS.format(toMenu), fromSlot, toSlot);
			
			// Move Meta
			moveTableRow(Table.META.format(fromMenu), Table.META.format(toMenu), fromSlot, toSlot);
			
			// Move Particles
			moveTableRow(Table.PARTICLES.format(fromMenu), Table.PARTICLES.format(toMenu), fromSlot, toSlot);
			
			// Delete this hat
			deleteHat(fromMenu, fromSlot);
		}
	}

	@Override
	public void deleteHat(String menuName, int slot) 
	{
		async(() ->
		{
			connect((connection) ->
			{				
				String deleteQuery = "DELETE FROM menu_" + menuName + "_items WHERE slot = ?";
				try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery))
				{
					deleteStatement.setInt(1, slot);
					deleteStatement.execute();
				}
			});
		});
	}
	
	@Override
	public void deleteNode (String menuName, int slot, int nodeIndex)
	{
		async(() ->
		{
			connect((connection) ->
			{
				String deleteQuery = "DELETE FROM menu_" + menuName + "_nodes WHERE slot = ? AND node_index = ?";
				try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery))
				{
					deleteStatement.setInt(1, slot);
					deleteStatement.setInt(2, nodeIndex);
					deleteStatement.execute();
				}
			});
		});
	}
	
	@Override
	public Map<String, BufferedImage> getImages (boolean forceUpdate)
	{
		if (forceUpdate || (System.currentTimeMillis() - lastImageUpdate) > UPDATE_INTERVAL)
		{
			lastImageUpdate = System.currentTimeMillis();
			imageCache.clear();
			connect((connection) -> 
			{
				String imageQuery = "SELECT * FROM images";
				try (PreparedStatement imageStatement = connection.prepareStatement(imageQuery))
				{
					ResultSet set = imageStatement.executeQuery();
					while (set.next())
					{
						String name = set.getString("name");
						InputStream stream = set.getBinaryStream("image");
						
						try 
						{
							BufferedImage image = ImageIO.read(stream);
							imageCache.put(name, image);
							
						} catch (Exception e) {}
					}
				}
			});
		}
		return new HashMap<String, BufferedImage>(imageCache);
	}
	
	@Override
	public void saveParticleData (String menuName, Hat hat, int index)
	{
		async(() ->
		{
			connect((connection) ->
			{
				String insertQuery = helper.getParticleInsertQuery(menuName, hat, index);
				Core.debug(insertQuery);
				try (PreparedStatement statement = connection.prepareStatement(insertQuery)) 
				{
					statement.executeUpdate();
					hat.getParticleData(index).clearPropertyChanges();
				}
			});
		});
	}
	
	@Override
	public void saveMetaData (String menuName, Hat hat, DataType type, int index)
	{
		async(() ->
		{
			connect((connection) ->
			{
				String deleteQuery = "DELETE FROM menu_" + menuName + "_meta WHERE slot = ? AND type = ? AND line_ex = ? AND node_index = ?";
				try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery))
				{
					deleteStatement.setInt(1, hat.getSlot());
					deleteStatement.setInt(2, type.getID());
					deleteStatement.setInt(3, index);
					deleteStatement.setInt(4, hat.getIndex());
					deleteStatement.execute();
				}
				
				String insertQuery = "INSERT INTO menu_" + menuName + "_meta VALUES(?,?,?,?,?,?)";
				try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery))
				{
					int slot = hat.getSlot();
					switch (type)
					{
						case NONE: break;
						case PERMISSION_DESCRIPTION:
						case DESCRIPTION:
						{
							List<String> description = type == DataType.DESCRIPTION ? hat.getDescription() : hat.getPermissionDescription();
							
							int index_num = 1;
							for (String s : description)
							{
								insertStatement.setInt(1, slot);
								insertStatement.setInt(2, type.getID());
								insertStatement.setInt(3, index_num++);
								insertStatement.setInt(4, index);
								insertStatement.setInt(5, hat.getIndex());
								insertStatement.setString(6, s);
								insertStatement.addBatch();
							}
							
							insertStatement.executeBatch();	
							break;
						}
						
						case ICON:
						{
							List<String> materials = hat.getIconData().getMaterialsAsStringList();
							
							// Remove our first entry since that one is saved in the _items table
							if (materials.size() > 0) {
								materials.remove(0);
							}
							
							int index_num = 1;
							for (String mat : materials)
							{
								insertStatement.setInt(1, slot);
								insertStatement.setInt(2, type.getID());
								insertStatement.setInt(3, index_num++);
								insertStatement.setInt(4, index);
								insertStatement.setInt(5, hat.getIndex());
								insertStatement.setString(6, mat);
								insertStatement.addBatch();
							}
							
							insertStatement.executeBatch();
							break;
						}
						
						case TAGS:
						{
							List<ParticleTag> tags = hat.getTags();
							
							int index_num = 1;
							for (ParticleTag tag : tags)
							{
								insertStatement.setInt(1, slot);
								insertStatement.setInt(2, type.getID());
								insertStatement.setInt(3, index_num++);
								insertStatement.setInt(4, index);
								insertStatement.setInt(5, hat.getIndex());
								insertStatement.setString(6, tag.getName());
								insertStatement.addBatch();
							}
							
							insertStatement.executeBatch();
							break;
						}
						
						case ITEMSTACK:
						{
							ItemStackData itemStackData = hat.getParticleData(index).getItemStackData();
							List<ItemStack> items = itemStackData.getItems();
							int index_num = 1;
							
							for (ItemStack item : items)
							{
								insertStatement.setInt(1, slot);
								insertStatement.setInt(2, type.getID());
								insertStatement.setInt(3, index_num++);
								insertStatement.setInt(4, index);
								insertStatement.setInt(5, hat.getIndex());
								insertStatement.setString(6, item.getType().toString());
								insertStatement.addBatch();
							}
							
							insertStatement.executeBatch();
							break;
						}
					}

				}
			});
		});
	}
	
	@Override
	public void saveMenuTitle(String menuName, String title) 
	{
		async(() ->
		{
			connect((connection) ->
			{
				String titleQuery = "UPDATE menus SET title = ? WHERE name = ?";
				try (PreparedStatement titleStatement = connection.prepareStatement(titleQuery))
				{
					titleStatement.setString(1, title);
					titleStatement.setString(2, menuName);
					titleStatement.executeUpdate();
				}
			});
		});
	}
	
	@Override
	public void saveMenuSize(String menuName, int rows) 
	{
		async(() ->
		{
			connect((connection) ->
			{
				String sizeQuery = "UPDATE menus SET size = ? WHERE name = ?";
				try (PreparedStatement sizeStatement = connection.prepareStatement(sizeQuery))
				{
					sizeStatement.setInt(1, rows);
					sizeStatement.setString(2, menuName);
					sizeStatement.executeUpdate();
				}
				
				String deleteQuery = "DELETE FROM menu_" + menuName + "_items WHERE slot >= ?";
				try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery))
				{
					deleteStatement.setInt(1, rows * 9);
					deleteStatement.execute();
				}
			});
		});
	}
	
	/**
	 * Updates the database with any changes
	 * @param menuName
	 * @param slot
	 * @param sqlQuery
	 */
	public void saveIncremental (String menuName, int slot, String sqlQuery)
	{
		async(() ->
		{
			connect((connection) ->
			{
				String saveQuery = "UPDATE menu_" + menuName + "_items " + sqlQuery + " WHERE slot = ?";
				try (PreparedStatement saveStatement = connection.prepareStatement(saveQuery))
				{
					saveStatement.setInt(1, slot);
					saveStatement.executeUpdate();
				}
			});
		});
	}
	
	public void saveNodeIncremental (String menuName, Hat parent, Hat node, int index)
	{
		Core.debug("saving node in slot: " + parent.getSlot());
		
		async(() ->
		{
			connect((connection) ->
			{
				String insertQuery = helper.getNodeInsertQuery(menuName, node, parent.getSlot(), index);
				Core.debug(insertQuery);
				try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
					statement.executeUpdate();
				}
			});
		});
	}
	
	/**
	 * Loads a preview of this menu.
	 * @param connection
	 * @param inventory
	 * @throws SQLException 
	 */
	private void loadHat (Connection connection, MenuInventory inventory, Player player) throws SQLException
	{
		final String menuName = inventory.getName();
		
		String hatQuery = "SELECT * FROM menu_" + inventory.getName() + "_items";
		try (PreparedStatement hatStatement = connection.prepareStatement(hatQuery))
		{
			ResultSet set = hatStatement.executeQuery();	
			while (set.next())
			{
				Hat hat = new Hat();
				
				hat.setSlot(set.getInt("slot"));
				hat.setMaterial(ItemUtil.materialFromString(set.getString("id"), Material.SUNFLOWER));
				hat.setName(set.getString("title"));
				hat.setPermission(set.getString("permission"));
				hat.setLocked(!player.hasPermission(hat.getFullPermission()));
				hat.setPermissionDeniedMessage(set.getString("permission_denied"));
				hat.setType(ParticleType.fromID(set.getInt("type")));
				hat.setLocation(ParticleLocation.fromId(set.getInt("location")));
				hat.setMode(ParticleMode.fromId(set.getInt("mode")));
				hat.setAnimation(ParticleAnimation.fromID(set.getInt("animation")));
				hat.setTrackingMethod(ParticleTracking.fromID(set.getInt("tracking")));
				hat.setLabel(set.getString("label"));
				hat.setEquipMessage(set.getString("equip_message"));
				hat.setOffset(set.getDouble("offset_x"), set.getDouble("offset_y"), set.getDouble("offset_z"));
				hat.setAngle(set.getDouble("angle_x"), set.getDouble("angle_y"), set.getDouble("angle_z"));
				hat.setUpdateFrequency(set.getInt("update_frequency"));		
				hat.setSpeed(set.getInt("speed"));
				hat.setCount(set.getInt("count"));
				hat.setPrice(set.getInt("price"));
				String soundName = set.getString("sound");
				if (soundName != null) {
					hat.setSound(Sound.valueOf(soundName));
				}
				hat.setSoundVolume(set.getDouble("volume"));
				hat.setSoundPitch(set.getDouble("pitch"));
				hat.setLeftClickAction(ParticleAction.fromId(set.getInt("left_action")));
				hat.setRightClickAction(ParticleAction.fromId(set.getInt("right_action")));
				hat.setLeftClickArgument(set.getString("left_argument"));
				hat.setRightClickArgument(set.getString("right_argument"));
				hat.setDemoDuration(set.getInt("duration"));
				hat.setDisplayMode(IconDisplayMode.fromId(set.getInt("display_mode")));
				hat.setScale(set.getDouble("scale"));
				
				String potion = set.getString("potion");
				if (potion != null)
				{
					PotionEffectType pt = PotionEffectType.getByName(potion);
					if (pt != null)
					{
						int amplifier = set.getInt("potion_strength");
						hat.setPotion(pt, amplifier);
					}
				}
				
				String customTypeName = set.getString("custom_type");
				if (customTypeName != null)
				{
					Map<String, BufferedImage> images = getImages(false);
					if (images.containsKey(customTypeName)) {
						hat.setCustomType(new PixelEffect(images.get(customTypeName), customTypeName));
					} else {
						Core.debug("failed to load image " + customTypeName);
					}
				}
				
				// Load nodes
				loadNodeData(connection, menuName, hat);
				
				// Load particles
				loadParticleData(connection, menuName, hat);
				
				// Load ItemStack data
				loadItemStackData(connection, menuName, hat);
				
				ItemStack item = ItemUtil.createItem(hat.getMaterial(), 1);
				ItemMeta meta = item.getItemMeta();
				
				meta.setDisplayName(hat.getDisplayName());
				item.setItemMeta(meta);
				
				// Load our meta data
				loadMetaData(connection, menuName, hat, item);
				
				// Insert into menu
				hat.clearPropertyChanges();
				
				int slot = hat.getSlot();
				inventory.setItem(slot, item);
				inventory.setHat(slot, hat);
			}
		}
		
//		String hatQuery = helper.getHatPreviewQuery(inventory.getName());
//		try (PreparedStatement hatStatement = connection.prepareStatement(hatQuery))
//		{
//			ResultSet hatSet = hatStatement.executeQuery();
//			while (hatSet.next())
//			{
//				Hat hat = new Hat();
//				
//				hat.setMenu(inventory.getName());
//				hat.setSlot(hatSet.getInt("slot"));
//				hat.setMaterial(ItemUtil.materialFromString(hatSet.getString("id"), Material.STONE));
//				hat.setName(hatSet.getString("title"));
//				hat.setPermission(hatSet.getString("permission"));
//				hat.setLocked(!player.hasPermission(hat.getFullPermission()));
//				
//				String permissionDenied = hatSet.getString("permission_denied");
//				if (permissionDenied != null) {
//					hat.setPermissionDeniedMessage(permissionDenied);
//				}
//				
//				hat.setType(ParticleType.fromID(hatSet.getInt("type")));
//				
//				String customTypeName = hatSet.getString("custom_type");
//				if (customTypeName != null)
//				{
//					Map<String, BufferedImage> images = getImages(false);
//					if (images.containsKey(customTypeName)) {
//						hat.setCustomType(new PixelEffect(images.get(customTypeName), customTypeName));
//					}
//				}
//				
//				hat.setLocation(ParticleLocation.fromId(hatSet.getInt("location")));
//				hat.setMode(ParticleMode.fromId(hatSet.getInt("mode")));
//				hat.setTrackingMethod(ParticleTracking.fromID(hatSet.getInt("tracking")));
//			
//				hat.setUpdateFrequency(hatSet.getInt("update_frequency"));
//				hat.setIconUpdateFrequency(hatSet.getInt("icon_update_frequency"));
//				
//				hat.setLeftClickAction(ParticleAction.fromId(hatSet.getInt("left_action")));
//				hat.setRightClickAction(ParticleAction.fromId(hatSet.getInt("right_action")));
//				
//				String leftArgument = hatSet.getString("left_argument");
//				if (leftArgument != null) {
//					hat.setLeftClickArgument(leftArgument);
//				}
//				
//				String rightArgument = hatSet.getString("right_argument");
//				if (rightArgument != null) {
//					hat.setRightClickArgument(rightArgument);
//				}
//				
//				hat.setDemoDuration(hatSet.getInt("duration"));
//				hat.setDisplayMode(IconDisplayMode.fromId(hatSet.getInt("display_mode")));
//				
//				// Load our particle count preview
//				String particleCountQuery = "SELECT COUNT(*) FROM menu_" + inventory.getName() + "_particles WHERE slot = ?";
//				try (PreparedStatement particleStatement = connection.prepareStatement(particleCountQuery))
//				{
//					particleStatement.setInt(1, hat.getSlot());
//					ResultSet set = particleStatement.executeQuery();
//					
//					while (set.next()) {
//						hat.setParticleCountPreview(set.getInt(1));
//					}
//				}
//				
//				// Load our node count preview
//				String nodeCountQuery = "SELECT COUNT(*) FROM menu_" + inventory.getName() + "_nodes WHERE slot = ?";
//				try (PreparedStatement nodeCountStatement = connection.prepareStatement(nodeCountQuery))
//				{
//					nodeCountStatement.setInt(1, hat.getSlot());
//					ResultSet set = nodeCountStatement.executeQuery();
//					
//					while (set.next()) {
//						hat.setNodeCountPreview(set.getInt(1));
//					}
//				}
//				
//				ItemStack item = ItemUtil.createItem(hat.getMaterial(), 1);
//				ItemMeta meta = item.getItemMeta();
//				
//				meta.setDisplayName(hat.getDisplayName());
//				item.setItemMeta(meta);
//				
//				// Load our meta data
//				loadMetaData(connection, inventory.getName(), hat, item);
//				
//				// Insert into menu
//				hat.clearPropertyChanges();
//				
//				int slot = hat.getSlot();
//				inventory.setItem(slot, item);
//				inventory.setHat(slot, hat);
//			}
//		}
	}
	
	@SuppressWarnings("incomplete-switch")
	private void loadMetaData (Connection connection, String menuName, Hat hat, ItemStack item) throws SQLException
	{
		String query = "SELECT type, value FROM menu_" + menuName + "_meta WHERE slot = ? ORDER BY line ASC";
		try (PreparedStatement descriptionStatement = connection.prepareStatement(query))
		{
			descriptionStatement.setInt(1, hat.getSlot());
			
			ResultSet set = descriptionStatement.executeQuery();
			
			List<String> description = new ArrayList<String>();
			List<String> permissionDescription = new ArrayList<String>();
			List<String> lore = new ArrayList<String>();
			IconData data = hat.getIconData();
			
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(hat.getDisplayName());
			
			while (set.next()) 
			{
				DataType type = DataType.fromID(set.getInt("type"));
				switch (type)
				{	
					case DESCRIPTION:
					{
						description.add(set.getString("value"));
						break;
					}
					
					case PERMISSION_DESCRIPTION:
					{
						permissionDescription.add(set.getString("value"));
						break;
					}
					
					case ICON:
					{
						Material mat = Material.getMaterial(set.getString("value"));
						if (mat != null) {
							data.addMaterial(mat);
						}
						break;
					}
					
					case TAGS:
					{
						ParticleTag tag = ParticleTag.fromName(set.getString("value"));
						if (tag != ParticleTag.NONE) {
							hat.addTag(tag);
						}
						break;
					}
				}
			}
			
			if (description.size() > 0) 
			{
				lore.addAll(StringUtil.colorize(description));
				hat.setDescription(description);
			}
			
			if (permissionDescription.size() > 0)
			{
				// Add our permission description if the player doesn't have permission for this hat
				if (SettingsManager.FLAG_PERMISSION.getBoolean() && hat.isLocked()) {
					lore.addAll(StringUtil.colorize(permissionDescription));
				}
				
				hat.setPermissionDescription(permissionDescription);
			}
			
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
	}
	
	private void loadNodeData (Connection connection, String menuName, Hat hat) throws SQLException
	{
		String nodeQuery = "SELECT * FROM menu_" + menuName + "_nodes WHERE slot = ? ORDER BY node_index ASC";
		try (PreparedStatement nodeStatement = connection.prepareStatement(nodeQuery))
		{
			nodeStatement.setInt(1, hat.getSlot());
			ResultSet set = nodeStatement.executeQuery();
			
			while (set.next())
			{
				Hat node = new Hat();
				
				node.setSlot(set.getInt("slot"));
				node.setIndex(set.getInt("node_index"));
				node.setType(ParticleType.fromID(set.getInt("type")));
				node.setLocation(ParticleLocation.fromId(set.getInt("location")));
				node.setMode(ParticleMode.fromId(set.getInt("mode")));
				node.setAnimation(ParticleAnimation.fromID(set.getInt("animation")));
				node.setTrackingMethod(ParticleTracking.fromID(set.getInt("tracking")));
				node.setOffset(set.getDouble("offset_x"), set.getDouble("offset_y"), set.getDouble("offset_z"));
				node.setAngle(set.getDouble("angle_x"), set.getDouble("angle_y"), set.getDouble("angle_z"));
				node.setUpdateFrequency(set.getInt("update_frequency"));		
				node.setSpeed(set.getInt("speed"));
				node.setCount(set.getInt("count"));
				node.setScale(set.getDouble("scale"));
				
				String customTypeName = set.getString("custom_type");
				if (customTypeName != null)
				{
					Map<String, BufferedImage> images = getImages(false);
					if (images.containsKey(customTypeName)) {
						node.setCustomType(new PixelEffect(images.get(customTypeName), customTypeName));
					}
				}
				
				node.clearPropertyChanges();
				node.setLoaded(true);
				hat.addNode(node);
			}
		}
	}
	
	private void loadParticleData (Connection connection, String menuName, Hat hat) throws SQLException
	{
		String particleQuery = "SELECT * FROM menu_" + menuName + "_particles WHERE slot = ? ORDER BY particle_index ASC";
		try (PreparedStatement particleStatement = connection.prepareStatement(particleQuery))
		{
			particleStatement.setInt(1, hat.getSlot());
			ResultSet set = particleStatement.executeQuery();
			
			while (set.next())
			{
				int index = set.getInt("particle_index");
				int nodeIndex = set.getInt("node_index");
				
				Hat h = nodeIndex == -1 ? hat : hat.getNodeAtIndex(nodeIndex);
				if (h != null)
				{
					ParticleData data = h.getParticleData(index);
					data.setParticle(ParticleEffect.fromID(set.getInt("particle_id")));
					data.setScale(set.getDouble("scale"));
					
					ColorData colorData = data.getColorData();
					colorData.setColor(Color.fromRGB(set.getInt("color")));
					colorData.setRandom(set.getBoolean("random"));
					
					ItemStackData itemStackData = h.getParticleData(index).getItemStackData();
					itemStackData.setDuration(set.getInt("duration"));
					itemStackData.setGravity(set.getBoolean("gravity"));
					itemStackData.setVelocity(set.getDouble("velocity_x"), set.getDouble("velocity_y"), set.getDouble("velocity_z"));
					
					String itemData = set.getString("item_data");
					if (itemData != null) {
						data.setItem(new ItemStack(ItemUtil.materialFromString(itemData, Material.APPLE)));
					}
					
					String blockData = set.getString("block_data");
					if (blockData != null) {
						data.setBlock(ItemUtil.materialFromString(blockData, Material.STONE));
					}
					
					data.clearPropertyChanges();
				}
			}
		}
	}
	
	private void loadItemStackData (Connection connection, String menuName, Hat hat) throws SQLException
	{
		// Load ItemStack data
		String itemStackQuery = "SELECT * FROM menu_" + menuName + "_meta WHERE slot = ? AND type = ? ORDER BY line ASC";
		try (PreparedStatement statement = connection.prepareCall(itemStackQuery))
		{
			statement.setInt(1, hat.getSlot());
			statement.setInt(2, DataType.ITEMSTACK.getID());
			ResultSet set = statement.executeQuery();
			
			while (set.next())
			{
				int nodeIndex = set.getInt("node_index");
				Hat h = nodeIndex == -1 ? hat : hat.getNodeAtIndex(nodeIndex);
				if (h != null)
				{
					int index = set.getInt("line_ex");
					
					ItemStackData itemStackData = h.getParticleData(index).getItemStackData();
					itemStackData.addItem(new ItemStack(ItemUtil.materialFromString(set.getString("value"), Material.STONE)));
					
					h.getParticleData(index).clearPropertyChanges();
				}
			}
		}
	}
	
	private void cloneTableRow (String menuName, int currentSlot, int newSlot)
	{
		sync(() ->
		{
			connect((connection) ->
			{
				// Create a temporary table and copy this rows data to it
				String cloneQuery = "CREATE TEMPORARY TABLE tmp SELECT * FROM " + menuName + " WHERE slot = ?";
				try (PreparedStatement cloneStatement = connection.prepareStatement(cloneQuery))
				{
					cloneStatement.setInt(1, currentSlot);
					cloneStatement.execute();
					
					// Set the slot we want to clone to in this temp table
					String updateQuery = "UPDATE tmp SET slot = ? WHERE slot = ?";
					try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery))
					{
						updateStatement.setInt(1, newSlot);
						updateStatement.setInt(2, currentSlot);
						updateStatement.executeUpdate();
						
						// Move this data back into the original table with the new slot set
						String switchQuery = "INSERT INTO " + menuName + " SELECT * FROM tmp WHERE slot = ?";
						try (PreparedStatement switchStatement = connection.prepareStatement(switchQuery))
						{
							switchStatement.setInt(1, newSlot);
							switchStatement.executeUpdate();
							
							// Delete the temporary table
							String dropQuery = "DROP TABLE tmp";
							try (PreparedStatement dropStatement = connection.prepareStatement(dropQuery)) {
								dropStatement.execute();
							}
						}
					}
				}
			});
		});
	}
	
	/**
	 * Moves data from one table to another table
	 * @param fromMenu
	 * @param toMenu
	 * @param fromSlot
	 * @param toSlot
	 */
	private void moveTableRow (String fromMenu, String toMenu, int fromSlot, int toSlot)
	{
		sync(() ->
		{
			connect((connection) ->
			{
				// Move row to temporary table
				String moveQuery = "CREATE TEMPORARY TABLE tmp SELECT * FROM " + fromMenu + " WHERE slot = ?";
				try (PreparedStatement moveStatement = connection.prepareStatement(moveQuery))
				{
					moveStatement.setInt(1, fromSlot);
					
					if (moveStatement.executeUpdate() > 0)
					{
						String updateQuery = "UPDATE tmp SET slot = ? WHERE slot = ?";
						try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery))
						{
							updateStatement.setInt(1, toSlot);
							updateStatement.setInt(2, fromSlot);
							
							if (updateStatement.executeUpdate() > 0)
							{
								String addQuery = "INSERT INTO " + toMenu + " SELECT * FROM tmp WHERE slot = ?";
								try (PreparedStatement addStatement = connection.prepareStatement(addQuery))
								{
									addStatement.setInt(1, toSlot);
									addStatement.executeUpdate();
								}
							}
						}
					}
					
					// Delete the tmp table
					String dropQuery = "DROP TABLE tmp";
					try (PreparedStatement dropStatement = connection.prepareStatement(dropQuery)) {
						dropStatement.execute();
					}
				}
			});
		});
	}
	
	/**
	 * Moves hat data to a new slot
	 * @param menuName
	 * @param previousSlot
	 * @param newSlot
	 */
	private void changeSlot (String menuName, int previousSlot, int newSlot)
	{
		async(() ->
		{
			connect((connection) ->
			{
				String changeQuery = "UPDATE menu_" + menuName + "_items SET slot = ? WHERE slot = ?";
				try (PreparedStatement changeStatement = connection.prepareStatement(changeQuery))
				{
					changeStatement.setInt(1, newSlot);
					changeStatement.setInt(2, previousSlot);
					changeStatement.execute();
				}
			});
		});
	}
	
	/**
	 * Swaps hat data with an existing slot
	 * @param menuName
	 * @param previousSlot
	 * @param newSlot
	 */
	private void swapSlot (String menuName, int previousSlot, int newSlot)
	{
		async(() ->
		{
			connect((connection) ->
			{
				String swapQuery = "UPDATE menu_" + menuName + "_items SET slot = ? WHERE slot = ?";
				try (PreparedStatement swapStatement = connection.prepareStatement(swapQuery))
				{
					swapStatement.setInt(1, 54);
					swapStatement.setInt(2, previousSlot);
					swapStatement.addBatch();
					
					swapStatement.setInt(1, previousSlot);
					swapStatement.setInt(2, newSlot);
					swapStatement.addBatch();
					
					swapStatement.setInt(1, newSlot);
					swapStatement.setInt(2, 54);
					swapStatement.addBatch();
					
					swapStatement.executeBatch();
				}
			});
		});
	}
	
//	/**
//	 * 
//	 * @param menuName
//	 * @param previousSlot
//	 * @param newSlot
//	 * @param swapping
//	 */
//	@Override
//	public void changeSlot (String menuName, int previousSlot, int newSlot, boolean swapping)
//	{
//		if (swapping)
//		{
//			async(() ->
//			{
//				connect((connection) ->
//				{
//					String swapQuery = "UPDATE menu_" + menuName + "_items SET slot = ? WHERE slot = ?";
//					try (PreparedStatement swapStatement = connection.prepareStatement(swapQuery))
//					{
//						swapStatement.setInt(1, 54);
//						swapStatement.setInt(2, previousSlot);
//						swapStatement.addBatch();
//						
//						swapStatement.setInt(1, previousSlot);
//						swapStatement.setInt(2, newSlot);
//						swapStatement.addBatch();
//						
//						swapStatement.setInt(1, newSlot);
//						swapStatement.setInt(2, 54);
//						swapStatement.addBatch();
//						
//						swapStatement.executeBatch();
//					}
//				});
//			});
//		}
//		
//		else
//		{
//			async(() ->
//			{
//				connect((connection) ->
//				{
//					String changeQuery = "UPDATE menu_" + menuName + "_items SET slot = ? WHERE slot = ?";
//					try (PreparedStatement changeStatement = connection.prepareStatement(changeQuery))
//					{
//						changeStatement.setInt(1, newSlot);
//						changeStatement.setInt(2, previousSlot);
//						changeStatement.execute();
//					}
//				});
//			});
//		}
//	}
	
	/**
	 * Checks to see if a table exists in the database
	 * @param tableName
	 * @return
	 */
	private boolean tableExists (String tableName)
	{
		try (Connection connection = dataSource.getConnection())
		{
			String tableQuery = "SELECT COUNT(*) AS count FROM information_schema.tables WHERE table_name = ?";
			try (PreparedStatement statement = connection.prepareStatement(tableQuery))
			{
				statement.setString(1, tableName);
				ResultSet set = statement.executeQuery();
				while (set.next()) {
					return set.getInt("count") > 0;
				}
				return false;
			}
		}
		
		catch (SQLException e) {
			return false;
		}
	}
	
	/**
	 * Get the name of this table
	 * @param menuName
	 * @param table
	 * @return
	 */
//	private String getTable (String menuName, Table table) {
//		return table.getFormat().replace("%", menuName);
//	}
	
	public void connect (ConnectionCallback callback)
	{
		try (Connection connection = dataSource.getConnection()) {
			callback.execute(connection);
		}
		
		catch (SQLException e) 
		{
			Core.debug(e.getStackTrace());
			Core.log(e.getMessage());
		}
	}
	
	public void async (TaskCallback callback)
	{
		new BukkitRunnable()
		{
			public void run () {
				callback.execute();
			}
		}.runTaskAsynchronously(Core.instance);
	}
	
	public void sync (TaskCallback callback)
	{
		new BukkitRunnable()
		{
			public void run () {
				callback.execute();
			}
		}.runTask(Core.instance);
	}
	
	@FunctionalInterface
	public static interface ConnectionCallback {
		public void execute (Connection connection) throws SQLException;
	}
	
	@FunctionalInterface
	public static interface TaskCallback {
		public void execute();
	}
	
	private enum Table
	{
		ITEMS     ("menu_%_items"),
		META      ("menu_%_meta"),
		PARTICLES ("menu_%_particles"),
		NODES     ("menu_%_nodes");
		
		private final String format;
		
		private Table (String format)
		{
			this.format = format;
		}
		
		public String format (String menuName) {
			return format.replace("%", menuName);
		}
	}
}
