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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.configuration.CustomConfig;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.database.properties.Group;
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
import com.mediusecho.particlehats.particles.properties.ParticleModes;
import com.mediusecho.particlehats.particles.properties.ParticleTag;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;
import com.mediusecho.particlehats.particles.properties.ParticleType;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;
import com.mediusecho.particlehats.util.YamlUtil;
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
	private final String configurationProperties = SettingsManager.DATABASE_CONFIGURATION_PROPERTIES.getString();
	
	private final boolean legacy;
	
	private Map<String, String> menuCache;
	private Map<String, BufferedImage> imageCache;
	private List<Group> groupCache;
	private List<String> labelCache;
	
	private long lastMenuUpdate = 0L;
	private long lastImageUpdate = 0L;
	private long lastGroupUpdate = 0L;
	private long lastLabelUpdate = 0L;
	
	private Exception lastException;
	
	private boolean connected = false;
	
	// Fetch MySQL changes every 30 seconds
	private final long UPDATE_INTERVAL = 30000L;
	
	public MySQLDatabase (ParticleHats core)
	{				
		menuCache = new HashMap<String, String>();
		imageCache = new HashMap<String, BufferedImage>();
		groupCache = new ArrayList<Group>();
		labelCache = new ArrayList<String>();
		
		legacy = ParticleHats.serverVersion < 13;
		helper = new MySQLHelper(this);
		
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database + configurationProperties);
		config.setUsername(username);
		config.setPassword(password);
		
		try 
		{
			dataSource = new HikariDataSource(config);
			helper.initDatabase(core);
			connected = true;
			
			ParticleHats.log("Successfully connected to MySQL database");
		}
		
		catch (Exception e) {
			lastException = e;
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
	public Exception getException () {
		return lastException;
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
	public MenuInventory getPurchaseMenu (PlayerState playerState)
	{
		try (Connection connection = dataSource.getConnection())
		{
			String tableQuery = "SELECT COUNT(*) AS count FROM " + Table.MENUS.getFormat() + " WHERE name = 'purchase'";
			try (PreparedStatement statement = connection.prepareStatement(tableQuery))
			{
				ResultSet set = statement.executeQuery();
				while (set.next())
				{
					if (set.getInt("count") == 0)
					{
						createMenu(connection, "purchase", "Do you want to unlock this Hat?", 5, null, false);
						helper.populatePurchaseMenu(connection);
					}
				}
			}
		} 
		
		catch (SQLException e) {
			e.printStackTrace();
		}
		return loadInventory("purchase", playerState);
	}
	
	@Override
	public void createMenu(String menuName) 
	{
		async(() ->
		{
			connect ((connection) ->
			{
				createMenu(connection, menuName, menuName, 6, null, true);
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
					while (set.next()) 
					{
						if (!set.getString("name").equalsIgnoreCase("purchase")) {
							menuCache.put(set.getString("name"), set.getString("title"));
						}
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
	public List<Group> getGroups (boolean forceUpdate)
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
						groupCache.add(new Group(set.getString("name"), set.getString("menu"), set.getInt("weight")));
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
					String hatQuery = "SELECT * FROM " + Table.ITEMS.format(menuName) + " WHERE slot = ?";
					try (PreparedStatement hatStatement = connection.prepareStatement(hatQuery))
					{
						hatStatement.setInt(1, slot);
						ResultSet hatSet = hatStatement.executeQuery();
						while (hatSet.next())
						{
							loadHat(connection, hatSet, hat, menuName);
							
							ItemStack item = hat.getItem();
							ItemUtil.setItemName(item, hat.getDisplayName());
							loadMetaData(connection, menuName, hat, item);
						}
					}
							
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
	public void createHat(String menuName, Hat hat) 
	{
		final int slot = hat.getSlot();
		
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
		connect((connection) ->
		{
			String hatQuery = "SELECT * FROM " + Table.ITEMS.format(menuName) + " WHERE slot = ?";
			try (PreparedStatement statement = connection.prepareStatement(hatQuery))
			{
				statement.setInt(1, slot);
				
				ResultSet set = statement.executeQuery();
				while (set.next()) 
				{
					loadHat(connection, set, hat, menuName);
					
					ItemStack item = hat.getItem();
					ItemUtil.setItemName(item, hat.getDisplayName());
					loadMetaData(connection, menuName, hat, item);
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
						case MODE_WHITELIST:
						{
							ParticleHats.debug("saving mode metadata to mysql database for menu: " + menuName);
							
							int index_num = 1;
							
							for (ParticleModes mode : hat.getWhitelistedModes())
							{
								insertStatement.setInt(1, slot);
								insertStatement.setInt(2, type.getID());
								insertStatement.setInt(3, index_num++);
								insertStatement.setInt(4, index);
								insertStatement.setInt(5, hat.getIndex());
								insertStatement.setString(6, mode.toString().toUpperCase());
								insertStatement.addBatch();
							}
							
							insertStatement.executeBatch();
							break;
						}
						
						case MODE_BLACKLIST:
						{
							int index_num = 1;
							
							for (ParticleModes mode : hat.getBlacklistedModes())
							{
								insertStatement.setInt(1, slot);
								insertStatement.setInt(2, type.getID());
								insertStatement.setInt(3, index_num++);
								insertStatement.setInt(4, index);
								insertStatement.setInt(5, hat.getIndex());
								insertStatement.setString(6, mode.toString().toUpperCase());
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
				
				if (hats.size() > 0)
				{
					String insertQuery = "INSERT INTO " + Table.EQUIPPED.getFormat() + " VALUES(?,?,?,?)";
					try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery))
					{
						for (Hat hat : hats)
						{
							if (hat.isPermanent() && hat.canBeSaved() && !hat.getMenu().equals(""))
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
				List<Hat> equippedHats = new ArrayList<Hat>();
				String equippedQuery = "SELECT name, slot, hidden FROM " + Table.EQUIPPED.getFormat() + " WHERE id = ?";
				
				try (PreparedStatement equippedStatement = connection.prepareStatement(equippedQuery))
				{
					equippedStatement.setString(1, id.toString());
					
					ResultSet equippedSet = equippedStatement.executeQuery();
					while (equippedSet.next())
					{
						String name = equippedSet.getString("name");
						int slot = equippedSet.getInt("slot");
						boolean hidden = equippedSet.getBoolean("hidden");
						
						String menuQuery = "SELECT COUNT(*) as count FROM " + Table.MENUS.getFormat() + " WHERE name = ?";
						try (PreparedStatement menuStatement = connection.prepareStatement(menuQuery))
						{
							menuStatement.setString(1, name);
							ResultSet menuSet = menuStatement.executeQuery();
							
							while (menuSet.next())
							{
								if (menuSet.getInt("count") > 0)
								{
									Hat hat = new Hat();
									hat.setHidden(hidden);
									
									String hatQuery = "SELECT * FROM " + Table.ITEMS.format(name) + " WHERE slot = ?";
									try (PreparedStatement hatStatement = connection.prepareStatement(hatQuery))
									{
										hatStatement.setInt(1, slot);
										
										ResultSet hatSet = hatStatement.executeQuery();
										while (hatSet.next())
										{
											loadHat(connection, hatSet, hat, name);
											
											ItemStack item = hat.getItem();
											ItemUtil.setItemName(item, hat.getDisplayName());
											loadMetaData(connection, name, hat, item);
											
											equippedHats.add(hat);
										}
									}
								}
							}
						}
					}
				}
				
				sync(() ->
				{
					callback.execute(equippedHats);
				});
			});
		});
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
	public void onLabelChange (String oldLabel, String newLabel, String menu, int slot) {}
	
	@Override
	public void onReload () {}
	
	public void importMenu (Sender sender, CustomConfig menuConfig)
	{
		async (() ->
		{
			try (Connection connection = dataSource.getConnection())
			{
				importMenu(menuConfig, connection);
				
				sync (() -> {
					sender.sendMessage(Message.COMMAND_IMPORT_SUCCESS.replace("{1}", menuConfig.getName()));
				});
			}
			
			catch (SQLException e) 
			{
				sync(() -> 
				{
					sender.sendMessage(Message.COMMAND_IMPORT_ERROR.replace("{1}", e.getClass().getSimpleName()));
					e.printStackTrace();
				});
			}
		});
	}
	
	/**
	 * Adds an exiting .yml menu into the database
	 * @param menuConfig
	 * @return
	 * @throws SQLException 
	 */
	public void importMenu (CustomConfig menuConfig, Connection connection) throws SQLException
	{
		FileConfiguration config = menuConfig.getConfig();
		
		// Update this menu first
		if (!YamlUtil.isUpdated(menuConfig)) {
			YamlUtil.updateMenuSaveFormat(menuConfig);
		}
		
		// Create the initial menu
		String name = menuConfig.getName();
		String title = config.getString("settings.title", "");
		int size = config.getInt("settings.size", 6);
		
		createMenu(connection, name, title, size, null, true);
		
		StringBuilder propertyBuilder = new StringBuilder();
		StringBuilder nodeBuilder = new StringBuilder();
		StringBuilder particleBuilder = new StringBuilder();
		StringBuilder metaBuilder = new StringBuilder();
		
		StringBuilder properties = new StringBuilder();
		
		// Loop through each slot
		if (config.contains("items"))
		{
			Set<String> keys = config.getConfigurationSection("items").getKeys(false);
			for (String key : keys)
			{
				if (key == null) {
					continue;
				}
				
				String path = "items." + key + ".";
				int slot = StringUtil.toInt(key, -1);
				
				properties.append("(").append(slot);
				properties.append(",").append(1); // version
				properties.append(",").append(getImportString(config.getString(path + "id"), "'STONE'"));
				properties.append(",").append(config.getInt(path + "damage-value"));
				properties.append(",").append(getImportString(config.getString(path + "name"), "NULL"));
				properties.append(",").append(getImportString(config.getString(path + "permission"), "NULL"));
				properties.append(",").append(getImportString(config.getString(path + "permission-denied"), "NULL"));
				
				ParticleType type;
				if (config.isString(path + "type")) 
				{
					type = ParticleType.fromName(config.getString(path + "type"));
					properties.append(",").append(ParticleType.fromName(config.getString(path + "type")).getID());
					properties.append(",").append("NULL");
				}
				
				else
				{
					type = ParticleType.fromName(config.getString(path + "type.id"));
					properties.append(",").append(ParticleType.fromName(config.getString(path + "type.id")).getID());
					properties.append(",").append(getImportString(config.getString(path + "type.name"), "NULL")); // Custom Type
				}
				
				properties.append(",").append(ParticleLocation.fromName(config.getString(path + "location")).getID());
				properties.append(",").append(ParticleMode.fromName(config.getString(path + "mode")).getID());
				properties.append(",").append(ParticleAnimation.fromName(config.getString(path + "animated")).getID());
				
				// Tracking Method
				String trackingMethod = config.getString(path + "tracking");
				if (trackingMethod != null) {
					properties.append(",").append(ParticleTracking.fromName(config.getString(path + "tracking")).getID());
				}
				
				else {
					properties.append(",").append(type.getEffect().getDefaultTrackingMethod().getID());
				}
				
				properties.append(",").append(getImportString(config.getString(path + "label"), "NULL"));
				properties.append(",").append(getImportString(config.getString(path + "equip-message"), "NULL"));
				properties.append(",").append(config.getDouble(path + "offset.x"));
				properties.append(",").append(config.getDouble(path + "offset.y"));
				properties.append(",").append(config.getDouble(path + "offset.z"));
				properties.append(",").append(config.getDouble(path + "random-offset.x"));
				properties.append(",").append(config.getDouble(path + "random-offset.y"));
				properties.append(",").append(config.getDouble(path + "random-offset.z"));
				properties.append(",").append(config.getDouble(path + "angle.x"));
				properties.append(",").append(config.getDouble(path + "angle.y"));
				properties.append(",").append(config.getDouble(path + "angle.z"));
				properties.append(",").append(config.getInt(path + "update-frequency", 2));
				properties.append(",").append(config.getInt(path + "icon-update-frequency", 2));
				properties.append(",").append(config.getInt(path + "speed"));
				properties.append(",").append(config.getInt(path + "count", 1));
				properties.append(",").append(config.getInt(path + "price"));
				properties.append(",").append(getImportString(config.getString(path + "sound.id"), "NULL"));
				properties.append(",").append(config.getDouble(path + "sound.volume", 1.0));
				properties.append(",").append(config.getDouble(path + "sound.pitch", 1.0));
				properties.append(",").append(ParticleAction.fromName(config.getString(path + "action.left-click.id"), ParticleAction.EQUIP).getID());
				properties.append(",").append(ParticleAction.fromName(config.getString(path + "action.right-click.id"), ParticleAction.MIMIC).getID());
				properties.append(",").append(getImportString(config.getString(path + "action.left-click.argument"), "NULL"));
				properties.append(",").append(getImportString(config.getString(path + "action.right-click.argument"), "NULL"));
				properties.append(",").append(config.getInt(path + "duration"));
				properties.append(",").append(IconDisplayMode.fromName(config.getString(path + "display-mode")).getID());
				properties.append(",").append(config.getDouble(path + "scale", 1.0));
				properties.append(",").append(getImportString(config.getString(path + "potion.id"), "NULL"));
				properties.append(",").append(config.getInt(path + "potion.strength"));
				properties.append(")");
				
				propertyBuilder.append(",").append(properties.toString());
				properties.setLength(0);
				
				// Particles
				if (config.contains(path + "particles"))
				{
					Set<String> particleKeys = config.getConfigurationSection(path + "particles").getKeys(false);
					for (String particleKey : particleKeys)
					{
						if (particleKey == null) {
							continue;
						}
						
						String particlePath = path + "particles." + particleKey + ".";
						int index = StringUtil.toInt(particleKey, 1) - 1;
						
						importParticleData(config, slot, index, -1, particlePath, properties);
						
						particleBuilder.append(",").append(properties.toString());
						properties.setLength(0);
						
						// ItemStack items
						List<String> items = config.getStringList(particlePath + "items");
						if (items.size() > 0) {
							this.importMetaData(items, slot, DataType.ITEMSTACK.getID(), index, -1, properties, metaBuilder);
						}
					}
				}
				
				// Meta
				List<String> description = config.getStringList(path + "description");
				if (!description.isEmpty()) {
					importMetaData(description, slot, DataType.DESCRIPTION.getID(), 0, -1, properties, metaBuilder);
				}
				
				List<String> permissionDescription = config.getStringList(path + "permission-description");
				if (!permissionDescription.isEmpty()) {
					importMetaData(permissionDescription, slot, DataType.PERMISSION_DESCRIPTION.getID(), 0, -1, properties, metaBuilder);
				}
				
				List<String> icons = config.getStringList(path + "icons");
				if (!icons.isEmpty()) {
					importMetaData(icons, slot, DataType.ICON.getID(), 0, -1, properties, metaBuilder);
				}
				
				List<String> tags = config.getStringList(path + "tags");
				if (!tags.isEmpty()) {
					importMetaData(tags, slot, DataType.TAGS.getID(), 0, -1, properties, metaBuilder);
				}
				
				// Grab all nodes
				if (config.contains(path + "nodes"))
				{
					Set<String> nodeKeys = config.getConfigurationSection(path + "nodes").getKeys(false);
					for (String nodeKey : nodeKeys)
					{
						if (nodeKey == null) {
							continue;
						}
						
						String nodePath = path + "nodes." + nodeKey + ".";
						int nodeIndex = StringUtil.toInt(nodeKey, 1) - 1;
						
						properties.append("(").append(slot);
						properties.append(",").append(nodeIndex);
						properties.append(",").append(1);
						properties.append(",").append(ParticleType.fromName(config.getString(nodePath + "type")).getID());
						properties.append(",").append("NULL"); // Custom Type
						properties.append(",").append(ParticleLocation.fromName(config.getString(nodePath + "location")).getID());
						properties.append(",").append(ParticleMode.fromName(config.getString(nodePath + "mode")).getID());
						properties.append(",").append(ParticleAnimation.fromName(config.getString(nodePath + "animated")).getID());
						properties.append(",").append(ParticleTracking.fromName(config.getString(nodePath + "tracking")).getID());
						properties.append(",").append(config.getDouble(nodePath + "offset.x"));
						properties.append(",").append(config.getDouble(nodePath + "offset.y"));
						properties.append(",").append(config.getDouble(nodePath + "offset.z"));
						properties.append(",").append(config.getDouble(nodePath + "random-offset.x"));
						properties.append(",").append(config.getDouble(nodePath + "random-offset.y"));
						properties.append(",").append(config.getDouble(nodePath + "random-offset.z"));
						properties.append(",").append(config.getDouble(nodePath + "angle.x"));
						properties.append(",").append(config.getDouble(nodePath + "angle.y"));
						properties.append(",").append(config.getDouble(nodePath + "angle.z"));
						properties.append(",").append(config.getInt(nodePath + "update-frequency"));
						properties.append(",").append(config.getInt(nodePath + "speed"));
						properties.append(",").append(config.getInt(nodePath + "count"));
						properties.append(",").append(config.getDouble(nodePath + "scale"));
						properties.append(")");
						
						nodeBuilder.append(",").append(properties.toString());
						properties.setLength(0);
						
						if (config.contains(nodePath + "particles"))
						{
							Set<String> particleKeys = config.getConfigurationSection(nodePath + "particles").getKeys(false);
							for (String particleKey : particleKeys)
							{
								if (particleKey == null) {
									continue;
								}
								
								String particlePath = path + "nodes." + nodeKey + ".particles." + particleKey + ".";
								int index = StringUtil.toInt(particleKey, 1) - 1;
								
								importParticleData(config, slot, index, nodeIndex, particlePath, properties);
								
								particleBuilder.append(",").append(properties.toString());
								properties.setLength(0);
								
								// ItemStack items
								List<String> items = config.getStringList(particlePath + "items");
								if (items.size() > 0) {
									this.importMetaData(items, slot, DataType.ITEMSTACK.getID(), index, nodeIndex, properties, metaBuilder);
								}
							}
						}
					}
				}
			}
		}
		
		if (propertyBuilder.length() > 0)
		{
			String itemInsertQuery = helper.getImportQuery(name).replace("{1}", propertyBuilder.deleteCharAt(0).toString());
			try (PreparedStatement itemStatement = connection.prepareStatement(itemInsertQuery)) {
				itemStatement.executeUpdate();
			}
		}
		
		if (nodeBuilder.length() > 0)
		{
			String nodeInsertQuery = helper.getNodeImportQuery(name).replace("{1}", nodeBuilder.deleteCharAt(0).toString());
			try (PreparedStatement nodeStatement = connection.prepareStatement(nodeInsertQuery)) {
				nodeStatement.executeUpdate();
			}
		}
		
		if (particleBuilder.length() > 0)
		{
			String particleInsertQuery = helper.getParticleImportQuery(name).replace("{1}", particleBuilder.deleteCharAt(0).toString());
			try (PreparedStatement particleStatement = connection.prepareStatement(particleInsertQuery)) {
				particleStatement.executeUpdate();
			}
		}
		
		if (metaBuilder.length() > 0)
		{
			String metaInsertQuery = helper.getMetaImportQuery(name).replace("{1}", metaBuilder.deleteCharAt(0).toString());
			try (PreparedStatement metaStatement = connection.prepareStatement(metaInsertQuery)) {
				metaStatement.executeUpdate();
			}
		}
	}
	
	private void importMetaData (List<String> data, int slot, int dataType, int particleIndex, int nodeIndex, StringBuilder properties, StringBuilder metaBuilder)
	{
		int line = 0;
		for (String s : data)
		{
			properties.append("(").append(slot);
			properties.append(",").append(dataType);
			properties.append(",").append(line++);
			properties.append(",").append(particleIndex);
			properties.append(",").append(nodeIndex);
			properties.append(",'").append(s.replace("'", "''")).append("'");
			properties.append(")");
			
			metaBuilder.append(",").append(properties.toString());
			properties.setLength(0);
		}
	}
	
	private void importParticleData (FileConfiguration config, int slot, int index, int nodeIndex, String particlePath, StringBuilder properties)
	{			
		properties.append("(").append(slot);
		properties.append(",").append(index);
		properties.append(",").append(nodeIndex);
		properties.append(",").append(ParticleEffect.fromName(config.getString(particlePath + "particle")).getID());
		
		if (config.isInt(particlePath + "color")) {
			properties.append(",").append(config.getInt(particlePath + "color"));
		}
		
		else
		{
			int r = config.getInt(particlePath + "color.r");
			int g = config.getInt(particlePath + "color.g");
			int b = config.getInt(particlePath + "color.b");
			properties.append(",").append(Color.fromRGB(r, g, b).asRGB());
		}
		
		properties.append(",").append(config.getString(particlePath + "color", "").equals("random")); // Random
		properties.append(",").append(config.getDouble(particlePath + "size"));
		
		if (config.isString(particlePath + "item-data")) {
			properties.append(",").append(getImportString(config.getString(particlePath + "item-data"), "NULL")); 
		}
		
		else 
		{
			String itemData = config.getString(particlePath + "item-data.id", "APPLE") + ":" + config.getInt(particlePath + "item-data.damage-value");
			properties.append(",").append("'" + itemData + "'");
		}
		
		if (config.isString(particlePath + "block-data")) {
			properties.append(",").append(getImportString(config.getString(particlePath + "block-data"), "NULL"));
		}
		
		else
		{
			String blockData = config.getString(particlePath + "block-data.id", "STONE") + ":" + config.getInt(particlePath + "block-data.damage-value");
			properties.append(",").append("'" + blockData + "'");
		}
		
		properties.append(",").append(config.getInt(particlePath + "item-duration"));
		properties.append(",").append(config.getBoolean(particlePath + "item-gravity", true));
		properties.append(",").append(config.getDouble(particlePath + "item-velocity.x"));
		properties.append(",").append(config.getDouble(particlePath + "item-velocity.y"));
		properties.append(",").append(config.getDouble(particlePath + "item-velocity.z"));
		properties.append(")");
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
	
	/**
	 * Creates an empty menu in the database
	 * @param menuName
	 * @param title
	 * @param rows
	 * @param alias
	 * @throws SQLException 
	 */
	private void createMenu (Connection connection, String menuName, String title, int rows, String alias, boolean addToCache) throws SQLException
	{
		// Menu Entry
		String createMenuStatement= "INSERT INTO " + Table.MENUS.getFormat() + " VALUES(?, ?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(createMenuStatement))
		{
			statement.setString(1, menuName); // Name
			statement.setString(2, title);
			statement.setInt(3, rows);
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
				if (addToCache) {
					menuCache.put(menuName, menuName);
				}
			}
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
							if (hat.canBeLocked())
							{
								hat.setLocked(
										!player.hasPermission(hat.getFullPermission()) && 
										!player.hasPermission(Permission.PARTICLE_ALL.getPermission()));
							}
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
		hat.setPermissionDeniedMessage(getString(set, "permission_denied", ""));
		hat.setType(ParticleType.fromID(set.getInt("type")));
		hat.setLocation(ParticleLocation.fromId(set.getInt("location")));
		//hat.setMode(ParticleMode.fromId(set.getInt("mode")));
		hat.setAnimation(ParticleAnimation.fromID(set.getInt("animation")));
		hat.setTrackingMethod(ParticleTracking.fromID(set.getInt("tracking")));
		hat.setLabel(getString(set, "label", ""));
		hat.setEquipMessage(getString(set, "equip_message", ""));
		hat.setOffset(set.getDouble("offset_x"), set.getDouble("offset_y"), set.getDouble("offset_z"));
		hat.setRandomOffset(set.getDouble("random_offset_x"), set.getDouble("random_offset_y"), set.getDouble("random_offset_z"));
		hat.setAngle(set.getDouble("angle_x"), set.getDouble("angle_y"), set.getDouble("angle_z"));
		hat.setUpdateFrequency(getInt(set, "update_frequency", 2));
		hat.setIconUpdateFrequency(getInt(set, "icon_update_frequency", 1));
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
			hat.setItem(ItemUtil.createItem(material, 1));
		}
		
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
			try 
			{
				Sound sound = Sound.valueOf(soundName);
				if (sound != null) {
					hat.setSound(sound);
				}
			} catch (IllegalArgumentException e) {}	
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
		String query = "SELECT type,node_index,value FROM " + Table.META.format(menuName) + " WHERE slot = ? ORDER BY line ASC";
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
					case MODE_WHITELIST:
					{
						int nodeIndex = set.getInt("node_index");
						Hat h = nodeIndex == -1 ? hat : hat.getNodeAtIndex(nodeIndex);
						if (h == null) {
							break;
						}
						
						ParticleModes mode = ParticleModes.fromName(set.getString("value"));
						h.addWhitelistedMode(mode);
						break;
					}
					
					case MODE_BLACKLIST:
					{
						int nodeIndex = set.getInt("node_index");
						Hat h = nodeIndex == -1 ? hat : hat.getNodeAtIndex(nodeIndex);
						if (h == null) {
							break;
						}
						
						ParticleModes mode = ParticleModes.fromName(set.getString("value"));
						h.addBlacklistedMode(mode);
						break;
					}
				}
			}
			
			if (description.size() > 0) 
			{
				if (!hat.isLocked() || SettingsManager.MENU_SHOW_DESCRIPTION_WHEN_LOCKKED.getBoolean()) {
					lore.addAll(StringUtil.parseDescription(hat, description));
				}
				hat.setDescription(description);
			}
			
			if (permissionDescription.size() > 0)
			{
				// Add our permission description if the player doesn't have permission for this hat
				if (SettingsManager.FLAG_PERMISSION.getBoolean() && hat.isLocked()) {
					lore.addAll(StringUtil.parseDescription(hat, permissionDescription));
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
				//node.setMode(ParticleMode.fromId(set.getInt("mode")));
				node.setAnimation(ParticleAnimation.fromID(set.getInt("animation")));
				node.setTrackingMethod(ParticleTracking.fromID(set.getInt("tracking")));
				node.setOffset(set.getDouble("offset_x"), set.getDouble("offset_y"), set.getDouble("offset_z"));
				node.setAngle(set.getDouble("angle_x"), set.getDouble("angle_y"), set.getDouble("angle_z"));
				node.setUpdateFrequency(getInt(set, "update_frequency", 2));		
				node.setSpeed(set.getInt("speed"));
				node.setCount(set.getInt("count"));
				node.setScale(getDouble(set, "scale", 1.0));
				
				String customTypeName = set.getString("custom_type");
				if (!set.wasNull())
				{
					Map<String, BufferedImage> images = getImages(false);
					if (images.containsKey(customTypeName)) {
						node.setCustomType(new PixelEffect(images.get(customTypeName), customTypeName));
					}
				}
				
				node.setParent(hat);
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
	
	private String getImportString (String string, String fallback)
	{
		if (string == null) {
			return fallback;
		}
		return "'" + string + "'";
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
		MENU      ("menus", 1.0),
		ITEMS     ("items", 1.0),
		META      ("meta", 1.0),
		NODES     ("nodes", 1.0),
		PARTICLES ("particles", 1.0),
		IMAGES    ("images", 1.0),
		EQUIPPED  ("equipped", 1.0),
		PURCHASED ("purchased", 1.0);
		
		private final String value;
		private final double version;
		
		private TableType (final String value, final double version)
		{
			this.value = value;
			this.version = version;
		}
		
		public String getValue () {
			return value;
		}
		
		public double getVersion () {
			return version;
		}
	}

	@FunctionalInterface
	public interface ImportCallback {
		public void onImportFail(Exception e);
	}
}
