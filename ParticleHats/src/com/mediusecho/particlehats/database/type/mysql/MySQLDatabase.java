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

import javax.imageio.ImageIO;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.particles.effects.CustomEffect;
import com.mediusecho.particlehats.particles.properties.IconData;
import com.mediusecho.particlehats.particles.properties.IconDisplayMode;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.particles.properties.ParticleAnimation;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleMode;
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
	
	public final int TYPE_DESCRIPTION            = 1;
	public final int TYPE_PERMISSION_DESCRIPTION = 2;
	public final int TYPE_ICON                   = 3;
	public final int TYPE_TAGS                   = 4;
	public final int TYPE_PARTICLES              = 5;
	
	private Map<String, String> menuCache;
	private Map<String, BufferedImage> imageCache;
	
	private long lastMenuUpdate = 0L;
	private long lastImageUpdate = 0L;
	
	// Fetch MySQL changes every 30 seconds
	private final long UPDATE_INTERVAL = 30000L;
	
	public MySQLDatabase (Core core)
	{
		menuCache = new HashMap<String, String>();
		imageCache = new HashMap<String, BufferedImage>();
		
		helper = new MySQLHelper(this);
		
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useSSL=false");
		config.setUsername(username);
		config.setPassword(password);
		
		try {
			dataSource = new HikariDataSource(config);
			helper.initDatabase(core);
		}
		
		catch (Exception e)
		{
			Core.log("There was an error connecting to the MySQL database");
			e.printStackTrace();
		}
	}
	
	public boolean init ()
	{
		
		return true;
	}
	
	@Override
	public void onDisable () {
		dataSource.close();
	}

	@Override
	public MenuInventory loadInventory(String menuName)
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
					final int  menuSize = menuResult.getInt("size");
					final MenuInventory inventory = new MenuInventory(menuName, menuTitle, menuSize);
					
					// Load this menus items
					String itemQuery = helper.getShallowHatQuery(menuName);
					try (PreparedStatement itemStatement = connection.prepareStatement(itemQuery))
					{
						ResultSet itemSet = itemStatement.executeQuery();
						
						while (itemSet.next())
						{
							int itemSlot     = itemSet.getInt("slot");
							String itemID    = itemSet.getString("id");
							String itemTitle = ChatColor.translateAlternateColorCodes('&', itemSet.getString("title"));
							
							ItemStack item = ItemUtil.createItem(Material.valueOf(itemID), 1);
							ItemMeta meta = item.getItemMeta();
							meta.setDisplayName(itemTitle);
							
							Hat hat = new Hat();
							hat.setMaterial(item.getType());
							hat.setIconUpdateFrequency(itemSet.getInt("icon_update_frequency"));
							hat.setDisplayMode(IconDisplayMode.fromId(itemSet.getInt("display_mode")));
							
							String permissionDenied = itemSet.getString("permission_denied");
							if (permissionDenied != null) {
								hat.setPermissionDeniedMessage(permissionDenied);
							}
							
							String equipMessage = itemSet.getString("equip_message");
							if (equipMessage != null) {
								hat.setEquipMessage(equipMessage);
							}
							
							hat.setLeftClickAction(ParticleAction.fromId(itemSet.getInt("left_action")));
							hat.setRightClickAction(ParticleAction.fromId(itemSet.getInt("right_action")));
							
							String leftArgument = itemSet.getString("left_argument");
							if (leftArgument != null) {
								hat.setLeftClickArgument(leftArgument);
							}
							
							String rightArgument = itemSet.getString("right_argument");
							if (rightArgument != null) {
								hat.setRightClickArgument(rightArgument);
							}
							
							// Create our description
							String query = "SELECT type, value FROM menu_" + menuName + "_meta WHERE slot = ? ORDER BY line ASC";
							try (PreparedStatement descriptionStatement = connection.prepareStatement(query))
							{
								descriptionStatement.setInt(1, itemSlot);
								
								ResultSet set = descriptionStatement.executeQuery();
								int type = 0;
								
								List<String> description = new ArrayList<String>();
								List<String> permissionDescription = new ArrayList<String>();
								IconData data = hat.getIconData();
								
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
									}
								}
								
								if (description.size() > 0) 
								{
									meta.setLore(StringUtil.colorize(description));
									hat.setDescription(description);
								}
								
								if (permissionDescription.size() > 0) {
									hat.setPermissionDescription(permissionDescription);
								}
							}
							
							hat.clearPropertyChanges();
							
							item.setItemMeta(meta);
							inventory.setItem(itemSlot, item);
							inventory.setHat(itemSlot, hat);
						}
					}
					
					return inventory;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, String> getMenus(boolean forceUpdate) 
	{	
		// Only refresh our menu cache every UPDATE_INTERVAL to prevent spamming
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
		return menuCache;
	}
	
	@Override
	public void createEmptyMenu(String menuName) 
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
					}
				}
			});
		});
		
		// TODO cache update didn't work, probably due to async method
		updateMenuCache();
	}
	
	/**
	 * Deletes the menu and all tables associated
	 * @param menuName
	 */
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
								+ "menu_" + menuName + "_items,"
								+ "menu_" + menuName + "_particles";
						
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
	public void loadHatData (String menuName, int slot, Hat hat)
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
					//hat.setReferenceID(set.getInt("reference"));
					hat.setSlot(set.getInt("slot"));
					hat.setName(set.getString("title"));
					hat.setPermission(set.getString("permission"));	
					// Permission Denied
					hat.setType(ParticleType.fromID(set.getInt("type")));
					hat.setLocation(ParticleLocation.fromId(set.getInt("location")));
					hat.setMode(ParticleMode.fromId(set.getInt("mode")));
					hat.setAnimation(ParticleAnimation.fromID(set.getInt("animation")));
					hat.setTrackingMethod(ParticleTracking.fromID(set.getInt("tracking")));
					String label = set.getString("label");
					if (label != null) {
						hat.setLabel(label);
					}
					hat.setOffset(set.getDouble("offset_x"), set.getDouble("offset_y"), set.getDouble("offset_z"));
					hat.setAngle(set.getDouble("angle_x"), set.getDouble("angle_y"), set.getDouble("angle_z"));
					hat.setUpdateFrequency(set.getInt("update_frequency"));		
					hat.setSpeed(set.getInt("speed"));
					String soundName = set.getString("sound");
					if (soundName != null) {
						hat.setSound(Sound.valueOf(soundName));
					}
					hat.setSoundVolume(set.getDouble("volume"));
					hat.setSoundPitch(set.getDouble("pitch"));
					hat.setDemoDuration(set.getInt("duration"));
					hat.setParticleScale(set.getDouble("particle_scale"));
					
					String customTypeName = set.getString("custom_type");
					if (customTypeName != null)
					{
						Map<String, BufferedImage> images = getImages(false);
						if (images.containsKey(customTypeName)) {
							hat.setCustomType(new CustomEffect(images.get(customTypeName), customTypeName, 0.2));
						}
					}
					
					hat.clearPropertyChanges();
					hat.setLoaded(true);
				}
			}
			
			// Load particles
			String particleQuery = "SELECT * FROM menu_" + menuName + "_particles WHERE slot = ? ORDER BY particle_index ASC";
			try (PreparedStatement particleStatement = connection.prepareStatement(particleQuery))
			{
				particleStatement.setInt(1, slot);
				ResultSet set = particleStatement.executeQuery();
				
				int index = 0;
				while (set.next())
				{
					hat.setParticle(index, ParticleEffect.fromID(set.getInt("particle_id")));
					hat.setParticleColor(index, Color.fromRGB(set.getInt("color")));
					hat.getParticleColor(index).setRandom(set.getBoolean("random"));
					hat.setParticleScale(index, set.getDouble("scale"));
					
					String itemData = set.getString("item_data");
					if (itemData != null) {
						hat.setParticleItem(index, new ItemStack(ItemUtil.materialFromString(itemData, Material.APPLE)));
					}
					
					String blockData = set.getString("block_data");
					if (blockData != null) {
						hat.setParticleBlock(index, ItemUtil.materialFromString(blockData, Material.STONE));
					}
				}
			}
		});
	}

	/**
	 * 
	 * @param menuName
	 * @param slot
	 */
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
	
	/**
	 * 
	 * @param menuName
	 * @param previousSlot
	 * @param newSlot
	 * @param swapping
	 */
	@Override
	public void changeSlot (String menuName, int previousSlot, int newSlot, boolean swapping)
	{
		if (swapping)
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
		
		else
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
	public void saveMetaData (String menuName, Hat hat, DataType type)
	{
		async(() ->
		{
			connect((connection) ->
			{
				String deleteQuery = "DELETE FROM menu_" + menuName + "_meta WHERE slot = ? AND type = ?";
				try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery))
				{
					deleteStatement.setInt(1, hat.getSlot());
					deleteStatement.setInt(2, type.getID());
					deleteStatement.execute();
				}
				
				String insertQuery = "INSERT INTO menu_" + menuName + "_meta VALUES(?,?,?,?)";
				try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery))
				{
					int slot = hat.getSlot();
					switch (type)
					{
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
								insertStatement.setString(4, s);
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
								insertStatement.setInt(2, TYPE_ICON);
								insertStatement.setInt(3, index_num++);
								insertStatement.setString(4, mat);
								insertStatement.addBatch();
							}
							
							insertStatement.executeBatch();
							
							break;
						}
						
						case TAGS:
						{
							break;
						}
						
						case PARTICLES:
						{
							break;
						}
					}

				}
			});
		});
	}
	
//	public void saveIconData (String menuName, Hat hat)
//	{
//		List<String> materials = hat.getIconData().getMaterialsAsStringList();
//		
//		// Remove our first entry since that one is saved in the _items table
//		if (materials.size() > 0) {
//			materials.remove(0);
//		}
//		async(() ->
//		{
//			connect((connection) ->
//			{
//				// Delete our existing entries
//				String deleteQuery = "DELETE FROM menu_" + menuName + "_meta WHERE slot = ? AND type = ?";
//				try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery))
//				{
//					deleteStatement.setInt(1, hat.getSlot());
//					deleteStatement.setInt(2, TYPE_ICON);
//					deleteStatement.execute();
//				}
//				
//				String insertQuery = "INSERT INTO menu_" + menuName + "_meta VALUES(?,?,?,?)";
//				try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery))
//				{
//					int index_num = 1;
//					int slot = hat.getSlot();
//					for (String mat : materials)
//					{
//						insertStatement.setInt(1, slot);
//						insertStatement.setInt(2, TYPE_ICON);
//						insertStatement.setInt(3, index_num++);
//						insertStatement.setString(4, mat);
//						insertStatement.addBatch();
//					}
//					
//					insertStatement.executeBatch();
//				}
//			});
//		});
//	}
	
//	public void saveDescriptionData (String menuName, int slot, List<String> description)
//	{
//		async(() ->
//		{
//			connect((connection) ->
//			{
//				String deleteQuery = "DELETE FROM menu_" + menuName + "_meta WHERE slot = ? AND type = ?";
//				try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery))
//				{
//					deleteStatement.setInt(1, slot);
//					deleteStatement.setInt(2, TYPE_DESCRIPTION);
//					deleteStatement.execute();
//				}
//				
//				String insertQuery = "INSERT INTO menu_" + menuName + "_meta VALUES(?,?,?,?)";
//				try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery))
//				{
//					int index_num = 1;
//					for (String s : description)
//					{
//						insertStatement.setInt(1, slot);
//						insertStatement.setInt(2, TYPE_DESCRIPTION);
//						insertStatement.setInt(3, index_num++);
//						insertStatement.setString(4, s);
//						insertStatement.addBatch();
//					}
//					
//					insertStatement.executeBatch();
//				}
//			});
//		});
//	}
	
	/**
	 * Updates the menu title in the database
	 * @param menuName
	 * @param title
	 */
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
	
	/**
	 * Updates the menu size in the database<br>
	 * Deletes any particles that are placed outside of the resized menu
	 * @param menuName
	 * @param rows
	 */
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
	
	public void connect (ConnectionCallback callback)
	{
		try (Connection connection = dataSource.getConnection()) {
			callback.execute(connection);
		}
		
		catch (SQLException e) {
			e.printStackTrace();
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
	
	public static interface ConnectionCallback {
		public void execute (Connection connection) throws SQLException;
	}
	
	@FunctionalInterface
	public static interface TaskCallback {
		public void execute();
	}

	@Override
	public boolean labelExists(String menuName, String label) {
		// TODO Add label exists data
		return false;
	}
}
