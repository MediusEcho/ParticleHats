package com.mediusecho.particlehats.database.type.mysql;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

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
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MySQLDatabase implements Database {

	// TODO: [Opt] ability to update tables
	
	private HikariDataSource dataSource;
	private MySQLHelper helper;
	
	private final String hostname = SettingsManager.DATABASE_HOSTNAME.asString();
	private final String username = SettingsManager.DATABASE_USERNAME.getString();
	private final String password = SettingsManager.DATABASE_PASSWORD.getString();
	private final String port     = SettingsManager.DATABASE_PORT.asString();
	private final String database = SettingsManager.DATABASE_DATABASE.getString();
	private final String useSSL   = SettingsManager.DATABASE_USESSL.getString();
	
	private final boolean legacy;
	
	private Map<String, String> menuCache;
	private Map<String, BufferedImage> imageCache;
	private Map<String, String> groupCache;
	private List<String> labelCache;
	
	private long lastMenuUpdate = 0L;
	private long lastImageUpdate = 0L;
	private long lastGroupUpdate = 0L;
	private long lastLabelUpdate = 0L;
	
	private boolean connected = false;
	
	// Fetch MySQL changes every 30 seconds
	private final long UPDATE_INTERVAL = 30000L;
	
	public MySQLDatabase (ParticleHats core)
	{		
		menuCache = new HashMap<String, String>();
		imageCache = new HashMap<String, BufferedImage>();
		groupCache = new LinkedHashMap<String, String>();
		labelCache = new ArrayList<String>();
		
		legacy = ParticleHats.serverVersion < 13;
		helper = new MySQLHelper(this);
		
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useSSL=" + useSSL);
		config.setUsername(username);
		config.setPassword(password);
		
		try 
		{
			dataSource = new HikariDataSource(config);
			helper.initDatabase(core);
			connected = true;
			
			ParticleHats.log("Using database: MySQL");
		}
		
		catch (Exception e) {
			e.printStackTrace();
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
	public boolean isEnabled () {
		return connected;
	}

	@Override
	public MenuInventory loadInventory(String menuName, PlayerState playerState)
	{		
		try (Connection connection = dataSource.getConnection())
		{
			return loadInventory(connection, menuName, playerState);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public MenuInventory getInventoryFromAlias (String alias, PlayerState playerState)
	{
		try (Connection connection = dataSource.getConnection())
		{
			String aliasQuery = "SELECT name FROM " + Table.MENUS.getFormat() + " WHERE alias = ? LIMIT 1";
			try (PreparedStatement aliasStatement = connection.prepareStatement(aliasQuery))
			{
				aliasStatement.setString(1, alias);
				ResultSet set = aliasStatement.executeQuery();
				
				while (set.next()) {
					return loadInventory(connection, set.getString("name"), playerState);
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
				String createMenuStatement= "INSERT INTO " + Table.MENUS.getFormat() + " VALUES(?, ?, ?, ?)";
				try (PreparedStatement statement = connection.prepareStatement(createMenuStatement))
				{
					statement.setString(1, menuName); // Name
					statement.setString(2, menuName); // Title (Same until title is changed)
					statement.setInt(3, 6);
					statement.setString(4, null);
					
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
				String deleteQuery = "DELETE FROM " + Table.MENUS.getFormat() + " WHERE name = ?";
				try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery))
				{
					deleteStatement.setString(1, menuName);
					if (deleteStatement.executeUpdate() >= 1)
					{
						String dropQuery = "DROP TABLE IF EXISTS "
								+ Table.META.format(menuName) + ","
								+ Table.PARTICLES.format(menuName) + ","
								+ Table.NODES.format(menuName) + ","
								+ Table.ITEMS.format(menuName); // Make sure the items table is deleted last since all menus reference this one
						
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
				try (PreparedStatement statement = connection.prepareStatement("SELECT name, title FROM " + Table.MENUS.getFormat()))
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
	public Map<String, BufferedImage> getImages (boolean forceUpdate)
	{
		if (forceUpdate || (System.currentTimeMillis() - lastImageUpdate) > UPDATE_INTERVAL)
		{
			lastImageUpdate = System.currentTimeMillis();
			imageCache.clear();
			connect((connection) -> 
			{
				String imageQuery = "SELECT * FROM " + Table.IMAGES.getFormat();
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
	public List<String> getLabels (boolean forceUpdate)
	{
		if (forceUpdate || (System.currentTimeMillis() - lastLabelUpdate) > UPDATE_INTERVAL)
		{
			labelCache.clear();
			
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT label FROM (");
			
			for (Entry<String, String> menu : getMenus(true).entrySet()) {
				builder.append("SELECT label FROM ").append(Table.ITEMS.format(menu.getKey())).append(" WHERE label IS NOT NULL %");
			}
			builder.deleteCharAt(builder.lastIndexOf("%")).append(") AS count");
						
			connect((connection) ->
			{
				String query = builder.toString().replaceAll("%", "UNION ");
				try (PreparedStatement statement = connection.prepareStatement(query))
				{
					ResultSet set = statement.executeQuery();
					while (set.next()) {
						labelCache.add(set.getString("label"));
					}
				}
			});
		}
		
		return labelCache;
	}
	
	@Override
	public Map<String, String> getGroups (boolean forceUpdate)
	{
		if (forceUpdate || (System.currentTimeMillis() - lastGroupUpdate) > UPDATE_INTERVAL)
		{
			groupCache.clear();
			
			connect((connection) ->
			{
				String groupQuery = "SELECT * FROM " + Table.GROUPS.getFormat() + " ORDER BY weight ASC";
				try (PreparedStatement statement = connection.prepareStatement(groupQuery))
				{
					ResultSet set = statement.executeQuery();
					while (set.next()) {
						groupCache.put(set.getString("name"), set.getString("menu"));
					}
				}
			});
		}
		return groupCache;
	}
	
	@Override
	public boolean labelExists(String menuName, String label) 
	{
		try (Connection connection = dataSource.getConnection())
		{
			String labelQuery = "SELECT COUNT(*) AS labels FROM " + Table.ITEMS.format(menuName) + " WHERE label = ?";
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
			builder.append("SELECT slot, '").append(menu.getKey()).append("' AS TableName FROM ").append(Table.ITEMS.format(menu.getKey())).append(" WHERE label = '").append(label).append("' %");
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
				String createQuery = "INSERT INTO " + Table.ITEMS.format(menuName) + "(slot) VALUES(?)";
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
		ParticleHats.debug("loading hat");
		connect((connection) ->
		{
			String hatQuery = "SELECT * FROM " + Table.ITEMS.format(menuName) + " WHERE slot = ?";
			try (PreparedStatement statement = connection.prepareStatement(hatQuery))
			{
				statement.setInt(1, slot);
				
				ResultSet set = statement.executeQuery();
				while (set.next()) {
					loadHat(connection, set, hat, menuName);
				}
			}
		});
	}
	
	@Override
	public void saveHat (String menuName, int slot, Hat hat)
	{
		String query = hat.getSQLUpdateQuery();
		async(() ->
		{
			connect((connection) ->
			{
				String saveQuery = "UPDATE " + Table.ITEMS.format(menuName) + " " + query + " WHERE slot = ?";
				try (PreparedStatement saveStatement = connection.prepareStatement(saveQuery))
				{
					saveStatement.setInt(1, slot);
					saveStatement.executeUpdate();
				}
			});
		});
	}
	
	@Override
	public void saveNode (String menuName, int nodeIndex, Hat hat)
	{
		String insertQuery = helper.getNodeInsertQuery(menuName, hat, hat.getSlot(), nodeIndex);
		async(() ->
		{
			connect((connection) ->
			{
				ParticleHats.debug(insertQuery);
				try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
					statement.executeUpdate();
				}
			});
		});
	}
	
	@Override
	public void cloneHat (String menuName, Hat hat, int newSlot)
	{
		int currentSlot = hat.getSlot();
		
		// Clone items
		cloneTableRow(Table.ITEMS.format(menuName), currentSlot, newSlot);
		
		// Clone Meta
		cloneTableRow(Table.META.format(menuName), currentSlot, newSlot);
		
		// Clone particles
		cloneTableRow(Table.PARTICLES.format(menuName), currentSlot, newSlot);
	}
	
	@Override
	public void moveHat (Hat fromHat, Hat toHat, String fromMenu, String toMenu, int fromSlot, int toSlot, boolean swapping)
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
				String deleteQuery = "DELETE FROM " + Table.ITEMS.format(menuName) + " WHERE slot = ?";
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
				String deleteQuery = "DELETE FROM " + Table.NODES.format(menuName) + " WHERE slot = ? AND node_index = ?";
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
	public void saveParticleData (String menuName, Hat hat, int index)
	{
		async(() ->
		{
			connect((connection) ->
			{
				String insertQuery = helper.getParticleInsertQuery(menuName, hat, index);
				ParticleHats.debug(insertQuery);
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
				String deleteQuery = "DELETE FROM " + Table.META.format(menuName) + " WHERE slot = ? AND type = ? AND line_ex = ? AND node_index = ?";
				try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery))
				{
					deleteStatement.setInt(1, hat.getSlot());
					deleteStatement.setInt(2, type.getID());
					deleteStatement.setInt(3, index);
					deleteStatement.setInt(4, hat.getIndex());
					deleteStatement.execute();
				}
				
				String insertQuery = "INSERT INTO " + Table.META.format(menuName) + " VALUES(?,?,?,?,?,?)";
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
							List<String> materials = hat.getIconData().getItemNames();
							
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
				String titleQuery = "UPDATE " + Table.MENUS.getFormat() + " SET title = ? WHERE name = ?";
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
	public void saveMenuAlias (String menuName, String alias)
	{
		async (() ->
		{
			connect ((connection) ->
			{
				String aliasQuery = "UPDATE " + Table.MENUS.getFormat() + " SET alias = ? WHERE name = ?";
				try (PreparedStatement aliasStatement = connection.prepareStatement(aliasQuery))
				{
					aliasStatement.setString(1, alias);
					aliasStatement.setString(2, menuName);
					aliasStatement.executeUpdate();
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
				String sizeQuery = "UPDATE " + Table.MENUS.getFormat() + " SET size = ? WHERE name = ?";
				try (PreparedStatement sizeStatement = connection.prepareStatement(sizeQuery))
				{
					sizeStatement.setInt(1, rows);
					sizeStatement.setString(2, menuName);
					sizeStatement.executeUpdate();
				}
				
				String deleteQuery = "DELETE FROM " + Table.ITEMS.format(menuName) + " WHERE slot >= ?";
				try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery))
				{
					deleteStatement.setInt(1, rows * 9);
					deleteStatement.execute();
				}
			});
		});
	}
	
	@Override
	public void savePlayerEquippedHats (UUID id, List<Hat> hats)
	{
		async(() ->
		{
			connect((connection) ->
			{
				String uid = id.toString();
				
				String deleteQuery = "DELETE FROM " + Table.EQUIPPED.getFormat() + " WHERE id = ?";
				try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery))
				{
					deleteStatement.setString(1, id.toString());
					deleteStatement.executeUpdate();
				}
				
				String insertQuery = "INSERT INTO " + Table.EQUIPPED.getFormat() + " VALUES(?,?,?,?)";
				try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery))
				{
					for (Hat hat : hats)
					{
						if (hat.isPermanent() && !hat.getMenu().equals(""))
						{
							insertStatement.setString(1, uid);
							insertStatement.setString(2, hat.getMenu());
							insertStatement.setInt(3, hat.getSlot());
							insertStatement.setBoolean(4, hat.isHidden());
							insertStatement.addBatch();
						}
					}
					
					insertStatement.executeBatch();
				}
			});
		});
	}
	
	@Override
	public void loadPlayerEquippedHats (UUID id, DatabaseCallback callback)
	{
		async(() ->
		{
			connect((connection) ->
			{
				List<Hat> loadedHats = new ArrayList<Hat>();
				
				String hatQuery = "SELECT name, slot, hidden FROM " + Table.EQUIPPED.getFormat() + " WHERE id = ?";
				try (PreparedStatement statement = connection.prepareStatement(hatQuery))
				{
					statement.setString(1, id.toString());
					
					ResultSet set = statement.executeQuery();
					while (set.next())
					{
						Hat hat = new Hat();
						loadHat(set.getString("name"), set.getInt("slot"), hat);
						hat.setHidden(set.getBoolean("hidden"));
						loadedHats.add(hat);
					}
				}
				
				sync(() ->
				{
					callback.execute(loadedHats);
				});
			});
		});
//		connect((connection) ->
//		{
//			String hatQuery = "SELECT name, slot FROM " + Table.EQUIPPED.getFormat() + " WHERE id = ?";
//			try (PreparedStatement statement = connection.prepareStatement(hatQuery))
//			{
//				statement.setString(1, id.toString());
//				
//				ResultSet set = statement.executeQuery();
//				PlayerState playerState = core.getPlayerState(id);
//				
//				while (set.next())
//				{
//					Hat hat = new Hat();
//					loadHat(set.getString("name"), set.getInt("slot"), hat);
//					
//					playerState.addHat(hat);
//				}
//			}
//		});
	}
	
	@Override
	public void savePlayerPurchase(UUID id, Hat hat) 
	{
		async(() ->
		{
			connect((connection) ->
			{
				String insertQuery = "INSERT IGNORE INTO " + Table.PURCHASED.getFormat() + " VALUES(?,?,?)";
				try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery))
				{
					if (hat.isPermanent() && !hat.getMenu().equals(""))
					{
						insertStatement.setString(1, id.toString());
						insertStatement.setString(2, hat.getMenu());
						insertStatement.setInt(3, hat.getSlot());
						insertStatement.executeUpdate();
					}
				}
			});
		});
	}

	@Override
	public void loadPlayerPurchasedHats(UUID id, DatabaseCallback callback) 
	{
		async(() ->
		{
			connect((connection) ->
			{
				List<HatReference> purchasedHats = new ArrayList<HatReference>();
				
				String hatQuery = "SELECT name, slot FROM " + Table.PURCHASED.getFormat() + " WHERE id = ?";
				try (PreparedStatement statement = connection.prepareStatement(hatQuery))
				{
					statement.setString(1, id.toString());
					
					ResultSet set = statement.executeQuery();
					while (set.next()) {
						purchasedHats.add(new HatReference(set.getString("name"), set.getInt("slot")));
					}
				}
				
				sync(() ->
				{
					callback.execute(purchasedHats);
				});
			});
		});
	}
	
	@Override
	public void addGroup (String groupName, String defaultMenu, int weight)
	{
		connect((connection) ->
		{
			String addQuery = "INSERT IGNORE INTO " + Table.GROUPS.getFormat() + " VALUES (?,?,?)";
			try (PreparedStatement statement = connection.prepareStatement(addQuery))
			{
				statement.setString(1, groupName);
				statement.setString(2, defaultMenu);
				statement.setInt(3, weight);
				statement.executeUpdate();
			}
		});
		
		getGroups(true);
	}
	
	@Override
	public void deleteGroup (String groupName)
	{
		connect((connection) ->
		{
			String deleteQuery = "DELETE FROM " + Table.GROUPS.getFormat() + " WHERE name = ?";
			try (PreparedStatement statement = connection.prepareStatement(deleteQuery))
			{
				statement.setString(1, groupName);
				statement.execute();
			}
		});
		
		getGroups(true);
	}
	
	@Override
	public void editGroup (String groupName, String defaultMenu, int weight)
	{
		connect((connection) ->
		{
			if (weight == -1)
			{
				String editQuery = "UPDATE " + Table.GROUPS.getFormat() + " SET menu = ? WHERE name = ?";
				try (PreparedStatement statement = connection.prepareStatement(editQuery))
				{
					statement.setString(1, defaultMenu);
					statement.setString(2, groupName);
					statement.execute();
				}
			}
			
			else
			{
				String editQuery = "UPDATE " + Table.GROUPS.getFormat() + " SET menu = ?, weight = ? WHERE name = ?";
				try (PreparedStatement statement = connection.prepareStatement(editQuery))
				{
					statement.setString(1, defaultMenu);
					statement.setInt(2, weight);
					statement.setString(3, groupName);
					statement.execute();
				}
			}
		});
	}
	
	@Override
	public boolean deleteImage (String imageName)
	{
		try (Connection connection = dataSource.getConnection())
		{
			String deleteQuery = "DELETE FROM " + Table.IMAGES.getFormat() + " WHERE name = ?";
			try (PreparedStatement statement = connection.prepareStatement(deleteQuery))
			{
				statement.setString(1, imageName);
				
				if (statement.executeUpdate() > 0)
				{
					imageCache.remove(imageName);
					return true;
				}
				
				return false;
			}
		}
		
		catch (SQLException e) {
			return false;
		}
	}
	
	@Override
	public void onReload () {}
	
	/**
	 * Adds an exiting .yml menu into the database
	 * @param menuConfig
	 * @return
	 * @throws SQLException 
	 */
	public void importMenu (CustomConfig menuConfig) throws SQLException
	{
		Connection connection = dataSource.getConnection();
	}
	
	/**
	 * Inserts an image into the database
	 * @param imageName
	 * @param image
	 * @return
	 */
	public boolean insertImage (String imageName, BufferedImage image)
	{
		try (Connection connection = dataSource.getConnection())
		{
			InputStream stream = null;
			try
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(image, "png", baos);
				stream = new ByteArrayInputStream(baos.toByteArray());
			} catch (IOException e) {
				return false;
			}
			
			String insertQuery = "INSERT INTO " + Table.IMAGES.getFormat() + " VALUES (?,?)";
			try (PreparedStatement statement = connection.prepareStatement(insertQuery))
			{
				statement.setString(1, imageName);
				statement.setBlob(2, stream);
				statement.executeUpdate();
			}
			
			imageCache.put(imageName, image);
			return true;
		}
		
		catch (SQLException e) {
			return false;
		}
	}
	
	private MenuInventory loadInventory (Connection connection, String menuName, PlayerState playerState) throws SQLException
	{
		String menuQuery = "SELECT * FROM " + Table.MENUS.getFormat() + " WHERE name = ?";
		try (PreparedStatement menuStatement = connection.prepareStatement(menuQuery))
		{
			menuStatement.setString(1, menuName);
			ResultSet menuResult = menuStatement.executeQuery();
			
			while (menuResult.next())
			{
				final String menuTitle = ChatColor.translateAlternateColorCodes('&', menuResult.getString("title"));
				final int menuSize = menuResult.getInt("size");
				final String alias = menuResult.getString("alias");
				
				final MenuInventory inventory = new MenuInventory(menuName, menuTitle, menuSize, alias);
				
				String hatQuery = "SELECT * FROM " + Table.ITEMS.format(inventory.getName());
				try (PreparedStatement hatStatement = connection.prepareStatement(hatQuery))
				{
					ResultSet set = hatStatement.executeQuery();	
					while (set.next())
					{
						Hat hat = new Hat();
						loadHat(connection, set, hat, menuName);
						
						hat.setMenu(menuName);
						
						if (!playerState.hasPurchased(hat))
						{
							Player player = playerState.getOwner();
							hat.setLocked(
									!player.hasPermission(hat.getFullPermission()) && 
									!player.hasPermission(Permission.PARTICLE_ALL.getPermission()));
						}
						
						ItemStack item = hat.getItem();//ItemUtil.createItem(hat.getMaterial(), 1);
						ItemUtil.setItemName(item, hat.getDisplayName());
						
						loadMetaData(connection, menuName, hat, item);
						
						int slot = hat.getSlot();
						inventory.setItem(slot, item);
						inventory.setHat(slot, hat);
					}
				}
				
				return inventory;
			}
		}
		return null;
	}
	
	private void loadHat (Connection connection, ResultSet set, Hat hat, String menuName) throws SQLException
	{		
		hat.setMenu(menuName);
		hat.setSlot(set.getInt("slot"));
		hat.setName(getString(set, "title", Message.EDITOR_MISC_NEW_PARTICLE.getValue()));
		hat.setPermission(getString(set, "permission", "all"));
		hat.setType(ParticleType.fromID(set.getInt("type")));
		hat.setLocation(ParticleLocation.fromId(set.getInt("location")));
		hat.setMode(ParticleMode.fromId(set.getInt("mode")));
		hat.setAnimation(ParticleAnimation.fromID(set.getInt("animation")));
		hat.setTrackingMethod(ParticleTracking.fromID(set.getInt("tracking")));
		hat.setLabel(getString(set, "label", ""));
		hat.setEquipMessage(getString(set, "equip_message", ""));
		hat.setOffset(set.getDouble("offset_x"), set.getDouble("offset_y"), set.getDouble("offset_z"));
		hat.setRandomOffset(set.getDouble("random_offset_x"), set.getDouble("random_offset_y"), set.getDouble("random_offset_z"));
		hat.setAngle(set.getDouble("angle_x"), set.getDouble("angle_y"), set.getDouble("angle_z"));
		hat.setUpdateFrequency(getInt(set, "update_frequency", 2));
		hat.setSpeed(set.getInt("speed"));
		hat.setCount(getInt(set, "count", 1));
		hat.setPrice(set.getInt("price"));
		hat.setLeftClickAction(ParticleAction.fromId(set.getInt("left_action")));
		hat.setRightClickAction(ParticleAction.fromId(set.getInt("right_action")));
		hat.setLeftClickArgument(getString(set, "left_argument", ""));
		hat.setRightClickArgument(getString(set, "right_argument", ""));
		hat.setDemoDuration(set.getInt("duration"));
		hat.setDisplayMode(IconDisplayMode.fromId(set.getInt("display_mode")));
		hat.setScale(getDouble(set, "scale", 1.0));
		hat.setSoundVolume(set.getDouble("volume"));
		hat.setSoundPitch(set.getDouble("pitch"));
		
		Material material = ItemUtil.getMaterial(set.getString("id"), CompatibleMaterial.SUNFLOWER.getMaterial());
		if (legacy) {
			hat.setItem(ItemUtil.createItem(material, set.getShort("durability")));
		} else {
			hat.setItem(new ItemStack(material));
		}
		hat.setItem(ItemUtil.createItem(material, set.getShort("durability")));
		
		String potionName = set.getString("potion");
		if (!set.wasNull())
		{
			PotionEffectType pt = PotionEffectType.getByName(potionName);
			if (pt != null) {
				hat.setPotion(pt, set.getInt("potion_strength"));
			}
		}
		
		String soundName = set.getString("sound");
		if (!set.wasNull())
		{
			Sound sound = Sound.valueOf(soundName);
			if (sound != null) {
				hat.setSound(sound);
			}
		}
		
		String customName = set.getString("custom_type");
		if (!set.wasNull())
		{
			Map<String, BufferedImage> images = getImages(false);
			if (images.containsKey(customName)) {
				hat.setCustomType(new PixelEffect(images.get(customName), customName));
			}
			
			// Set our type to default since the custom type doesn't exist
			else {
				hat.setType(ParticleType.NONE);
			}
		}
		
		loadNodeData(connection, menuName, hat);
		loadParticleData(connection, menuName, hat);
		loadItemStackData(connection, menuName, hat);
		
		hat.clearPropertyChanges();
		hat.setLoaded(true);
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
				String saveQuery = "UPDATE " + Table.ITEMS.format(menuName) + " " + sqlQuery + " WHERE slot = ?";
				try (PreparedStatement saveStatement = connection.prepareStatement(saveQuery))
				{
					saveStatement.setInt(1, slot);
					saveStatement.executeUpdate();
				}
			});
		});
	}
	
	@SuppressWarnings("incomplete-switch")
	private void loadMetaData (Connection connection, String menuName, Hat hat, ItemStack item) throws SQLException
	{
		String query = "SELECT type, value FROM " + Table.META.format(menuName) + " WHERE slot = ? ORDER BY line ASC";
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
						String matName = set.getString("value");
						if (legacy && matName.contains(":")) 
						{
							String[] matInfo = matName.split(":");
							short durability = Short.valueOf(matInfo[1]);
							data.addItem(ItemUtil.createItem(Material.getMaterial(matInfo[0]), durability));
						}
						
						else {
							data.addItem(new ItemStack(ItemUtil.getMaterial(matName, Material.STONE)));
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
		String nodeQuery = "SELECT * FROM " + Table.NODES.format(menuName) + " WHERE slot = ? ORDER BY node_index ASC";
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
				node.setUpdateFrequency(getInt(set, "update_frequency", 2));		
				node.setSpeed(set.getInt("speed"));
				node.setCount(set.getInt("count"));
				node.setScale(set.getDouble("scale"));
				
				String customTypeName = set.getString("custom_type");
				if (!set.wasNull())
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
		String particleQuery = "SELECT * FROM " + Table.PARTICLES.format(menuName) + " WHERE slot = ? ORDER BY particle_index ASC";
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
					data.setScale(getDouble(set, "scale", 1));
					
					ColorData colorData = data.getColorData();
					colorData.setColor(Color.fromRGB(set.getInt("color")));
					colorData.setRandom(set.getBoolean("random"));
					
					ItemStackData itemStackData = h.getParticleData(index).getItemStackData();
					itemStackData.setDuration(set.getInt("duration"));
					itemStackData.setGravity(set.getBoolean("gravity"));
					itemStackData.setVelocity(set.getDouble("velocity_x"), set.getDouble("velocity_y"), set.getDouble("velocity_z"));
					
					String itemData = set.getString("item_data");
					if (!set.wasNull()) 
					{
						if (legacy && itemData.contains(":"))
						{
							String[] itemInfo = itemData.split(":");
							short durability = Short.valueOf(itemInfo[1]);
							data.setItem(ItemUtil.createItem(ItemUtil.getMaterial(itemInfo[0], Material.APPLE), durability));
						}
						
						else {
							data.setItem(new ItemStack(ItemUtil.getMaterial(itemData, Material.APPLE)));
						}
					}
					
					String blockData = set.getString("block_data");
					if (!set.wasNull()) 
					{
						if (legacy && blockData.contains(":"))
						{
							String[] blockInfo = blockData.split(":");
							short durability = Short.valueOf(blockInfo[1]);
							data.setBlock(ItemUtil.createItem(ItemUtil.getMaterial(blockInfo[0], Material.STONE), durability));
						}
						
						else {
							data.setBlock(new ItemStack(ItemUtil.getMaterial(blockData, Material.STONE)));
						}
					}
					
					data.clearPropertyChanges();
				}
			}
		}
	}
	
	private void loadItemStackData (Connection connection, String menuName, Hat hat) throws SQLException
	{
		// Load ItemStack data
		String itemStackQuery = "SELECT * FROM " + Table.META.format(menuName) + " WHERE slot = ? AND type = ? ORDER BY line ASC";
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
					itemStackData.addItem(new ItemStack(ItemUtil.getMaterial(set.getString("value"), Material.STONE)));
					
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
				String changeQuery = "UPDATE " + Table.ITEMS.format(menuName) + " SET slot = ? WHERE slot = ?";
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
				String swapQuery = "UPDATE " + Table.ITEMS.format(menuName) + " SET slot = ? WHERE slot = ?";
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
//	 * Checks to see if a table exists in the database
//	 * @param tableName
//	 * @return
//	 */
//	private boolean tableExists (String tableName)
//	{
//		try (Connection connection = dataSource.getConnection())
//		{
//			String tableQuery = "SELECT COUNT(*) AS count FROM information_schema.tables WHERE table_name = ?";
//			try (PreparedStatement statement = connection.prepareStatement(tableQuery))
//			{
//				statement.setString(1, tableName);
//				ResultSet set = statement.executeQuery();
//				while (set.next()) {
//					return set.getInt("count") > 0;
//				}
//				return false;
//			}
//		}
//		
//		catch (SQLException e) {
//			return false;
//		}
//	}
	
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
			e.printStackTrace();
//			Core.debug(e.getStackTrace());
//			Core.log(e.getMessage());
		}
	}
	
	public void async (TaskCallback callback)
	{
		new BukkitRunnable()
		{
			public void run () {
				callback.execute();
			}
		}.runTaskAsynchronously(ParticleHats.instance);
	}
	
	public void sync (TaskCallback callback)
	{
		new BukkitRunnable()
		{
			public void run () {
				callback.execute();
			}
		}.runTask(ParticleHats.instance);
	}
	
	@FunctionalInterface
	public static interface ConnectionCallback {
		public void execute (Connection connection) throws SQLException;
	}
	
	@FunctionalInterface
	public static interface TaskCallback {
		public void execute();
	}

	
//	private void setMenusLastUpdateTime (Connection connection, String menuName) throws SQLException
//	{
//		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//		Date date = new Date();
//		
//		String format = dateFormat.format(date);
//		String menuQuery = "UPDATE menus SET modified = ? WHERE name = ?";
//		
//		try (PreparedStatement statement = connection.prepareStatement(menuQuery))
//		{
//			statement.setString(1, format);
//			statement.setString(2, menuName);
//			
//			statement.executeUpdate();
//		}
//	}
	
	/**
	 * Returns the value stored at the column label, or the default value if null
	 * @param set
	 * @param columnLabel
	 * @param defaultValue
	 * @return
	 * @throws SQLException
	 */
	private String getString (ResultSet set, String columnLabel, String defaultValue) throws SQLException
	{
		String value = set.getString(columnLabel);
		if (set.wasNull()) {
			return defaultValue;
		}
		return value;
	}
	
	/**
	 * Returns the value stored at the column index, or the default value if 0
	 * @param set
	 * @param columnLabel
	 * @param defaultValue
	 * @return
	 * @throws SQLException
	 */
	private int getInt (ResultSet set, String columnLabel, int defaultValue) throws SQLException
	{
		int value = set.getInt(columnLabel);
		if (value == 0) {
			return defaultValue;
		}
		return value;
	}
	
	/**
	 * Returns the value stored at the column index, or the default value if 0
	 * @param set
	 * @param columnLabel
	 * @param defaultValue
	 * @return
	 * @throws SQLException
	 */
	private double getDouble (ResultSet set, String columnLabel, double defaultValue) throws SQLException
	{
		double value = set.getDouble(columnLabel);
		if (value == 0) {
			return defaultValue;
		}
		return value;
	}
	
	public enum Table
	{
		MENUS     ("ph_menus"),
		IMAGES    ("ph_images"),
		EQUIPPED  ("ph_equipped_hats"),
		PURCHASED ("ph_purchased_hats"),
		VERSION   ("ph_version"),
		GROUPS    ("ph_groups"),
		ITEMS     ("ph_menu_%_items"),
		META      ("ph_menu_%_meta"),
		PARTICLES ("ph_menu_%_particles"),
		NODES     ("ph_menu_%_nodes");
		
		private final String format;
		
		private Table (String format)
		{
			this.format = format;
		}
		
		public String format (String menuName) {
			return format.replace("%", menuName);
		}
		
		public String getFormat () {
			return format;
		}
	}
	
	public enum TableType
	{
		MENU      ("menus"),
		ITEMS     ("items"),
		META      ("meta"),
		NODES     ("nodes"),
		PARTICLES ("particles"),
		IMAGES    ("images"),
		EQUIPPED  ("equipped"),
		PURCHASED ("purchased");
		
		private final String value;
		
		private TableType (final String value)
		{
			this.value = value;
		}
		
		public String getValue () {
			return value;
		}
	}


}
