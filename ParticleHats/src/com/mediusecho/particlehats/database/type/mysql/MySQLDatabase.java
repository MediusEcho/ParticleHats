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
import com.mediusecho.particlehats.database.type.DatabaseType.DatabaseCallback;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.particles.effects.CustomEffect;
import com.mediusecho.particlehats.particles.properties.IconData;
import com.mediusecho.particlehats.particles.properties.IconDisplayMode;
import com.mediusecho.particlehats.particles.properties.ItemStackData;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.particles.properties.ParticleAnimation;
import com.mediusecho.particlehats.particles.properties.ColorData;
import com.mediusecho.particlehats.particles.properties.ParticleData;
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
	
	private boolean connected = false;
	
	// Fetch MySQL changes every 30 seconds
	private final long UPDATE_INTERVAL = 30000L;
	
	public MySQLDatabase (Core core, DatabaseCallback callback)
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
			connected = true;
		}
		
		catch (SQLException e)
		{
			Core.log(e.getMessage());
			callback.onTimeout();
		}
	}
	
//	public boolean init ()
//	{
//		return true;
//	}
	
	@Override
	public void onDisable () 
	{
		if (connected) {
			dataSource.close();
		}
	}

	@SuppressWarnings("incomplete-switch")
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
					final int menuSize = menuResult.getInt("size");
					final MenuInventory inventory = new MenuInventory(menuName, menuTitle, menuSize);
					
					// Load this menus items
					String itemQuery = helper.getShallowHatQuery(menuName);
					try (PreparedStatement itemStatement = connection.prepareStatement(itemQuery))
					{
						ResultSet itemSet = itemStatement.executeQuery();
						
						while (itemSet.next())
						{
							int itemSlot = itemSet.getInt("slot");
							String itemID = itemSet.getString("id");
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
						
						// Add this menu to the cache
						menuCache.put(menuName, menuName);
					}
				}
			});
		});
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
				
				while (set.next())
				{
					int index = set.getInt("particle_index");
					
					ParticleData data = hat.getParticleData(index);
					data.setParticle(ParticleEffect.fromID(set.getInt("particle_id")));
					data.setScale(set.getDouble("scale"));
					
					ColorData colorData = data.getColorData();
					colorData.setColor(Color.fromRGB(set.getInt("color")));
					colorData.setRandom(set.getBoolean("random"));
					
					ItemStackData itemStackData = hat.getParticleData(index).getItemStackData();
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
			
			// Load ItemStack data
			String itemStackQuery = "SELECT * FROM menu_" + menuName + "_meta WHERE slot = ? AND type = ? ORDER BY line ASC";
			try (PreparedStatement itemStatement = connection.prepareCall(itemStackQuery))
			{
				itemStatement.setInt(1, slot);
				itemStatement.setInt(2, DataType.ITEMSTACK.getID());
				ResultSet set = itemStatement.executeQuery();
				
				while (set.next())
				{
					ItemStackData itemStackData = hat.getParticleData(set.getInt("line_ex")).getItemStackData();
					itemStackData.addItem(new ItemStack(ItemUtil.materialFromString(set.getString("value"), Material.STONE)));
				}
			}
		});
	}
	
	@Override
	public void cloneHatData (String menuName, int currentSlot, int newSlot)
	{
		// Clone items
		cloneTableRow(Table.ITEMS.format(menuName), currentSlot, newSlot);
		
		// Clone Meta
		cloneTableRow(Table.META.format(menuName), currentSlot, newSlot);
		
		// Clone particles
		cloneTableRow(Table.PARTICLES.format(menuName), currentSlot, newSlot);
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
	
	@Override
	public void moveHatData (String fromMenu, String toMenu, int fromSlot, int toSlot)
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
	public void saveMetaData (String menuName, Hat hat, DataType type, int index)
	{
		async(() ->
		{
			connect((connection) ->
			{
				String deleteQuery = "DELETE FROM menu_" + menuName + "_meta WHERE slot = ? AND type = ? AND line_ex = ?";
				try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery))
				{
					deleteStatement.setInt(1, hat.getSlot());
					deleteStatement.setInt(2, type.getID());
					deleteStatement.setInt(3, index);
					deleteStatement.execute();
				}
				
				String insertQuery = "INSERT INTO menu_" + menuName + "_meta VALUES(?,?,?,?,?)";
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
								insertStatement.setString(5, s);
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
								insertStatement.setString(5, mat);
								insertStatement.addBatch();
							}
							
							insertStatement.executeBatch();
							break;
						}
						
						case TAGS:
						{
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
								insertStatement.setString(5, item.getType().toString());
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
		PARTICLES ("menu_%_particles");
		
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
