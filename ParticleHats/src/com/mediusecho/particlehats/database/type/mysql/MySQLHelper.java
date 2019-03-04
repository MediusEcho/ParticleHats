package com.mediusecho.particlehats.database.type.mysql;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.SettingsManager;

public class MySQLHelper {

	private final MySQLDatabase database;
	private int menuTableVersion = 1;
	
	public MySQLHelper (final MySQLDatabase database)
	{
		this.database = database;
	}
	
	/**
	 * Creates our initial database
	 * @throws SQLException 
	 */
	public void initDatabase (Core core) throws SQLException
	{	
		database.async(() -> 
		{
			database.connect((connection) ->
			{
				String menuTable = "CREATE TABLE IF NOT EXISTS menus ("
						+ "name VARCHAR(128) PRIMARY KEY,"
						+ "title VARCHAR(40) NOT NULL,"
						+ "size TINYINT(3) NOT NULL DEFAULT 6"
						+ ")";
				try (PreparedStatement menuStatement = connection.prepareStatement(menuTable)) {
					menuStatement.execute();
				}
				
				String imageTable = "CREATE TABLE IF NOT EXISTS images ("
						+ "name VARCHAR(64) PRIMARY KEY,"
						+ "image BLOB"
						+ ")";
				try (PreparedStatement imageStatement = connection.prepareStatement(imageTable)) {
					imageStatement.execute();
				}
				
				// Try to add our included custom types to the database
				if (SettingsManager.LOAD_INCLUDED_CUSTOM_TYPES.getBoolean())
				{
					String imageInsertQuery = "INSERT IGNORE INTO images VALUES(?,?)";
					try (PreparedStatement imageInsertStatement = connection.prepareStatement(imageInsertQuery))
					{
						InputStream vampireWingsStream = core.getResource("types/vampire_wings.png");
						if (vampireWingsStream != null)
						{
							try 
							{
								InputStream vampireStream = getTrimmedStream(vampireWingsStream);
								imageInsertStatement.setString(1, "vampire_wings");
								imageInsertStatement.setBlob(2, vampireStream);
								imageInsertStatement.addBatch();
							} catch (IOException e) {}
						}
						
						InputStream butterflyWingsStream = core.getResource("types/butterfly_wings_colorable.png");
						if (butterflyWingsStream != null)
						{
							try
							{
								InputStream butterflyStream = getTrimmedStream(butterflyWingsStream);
								imageInsertStatement.setString(1, "butterfly_wings_colorable");
								imageInsertStatement.setBlob(2, butterflyStream);
								imageInsertStatement.addBatch();
							} catch (IOException e) {}
						}
						
						imageInsertStatement.executeBatch();
					}
				}
				
				// TODO: Push any local images to the database
			});
		});
	}
	
	public String getShallowHatQuery (String menuName)
	{
		String itemQuery = "SELECT "
				+ "slot,"
				+ "id,"
				+ "title,"
				+ "icon_update_frequency,"
				+ "display_mode,"
				+ "permission_denied,"
				+ "equip_message,"
				+ "left_action,"
				+ "right_action,"
				+ "left_argument,"
				+ "right_argument,"
				+ "duration"
				+ " FROM menu_" + menuName + "_items";
		return itemQuery;
	}
	
	public String getItemTableQuery (String menuName)
	{
		String query = "CREATE TABLE IF NOT EXISTS  menu_" + menuName + "_items ("
				+ "slot TINYINT PRIMARY KEY,"
				+ "ver SMALLINT NOT NULL DEFAULT " + menuTableVersion + ","
				+ "id VARCHAR(64) NOT NULL DEFAULT 'SUNFLOWER',"
				+ "title VARCHAR(128) NOT NULL DEFAULT '" + Message.EDITOR_MISC_NEW_PARTICLE.getRawValue() + "',"
				+ "permission VARCHAR(64) NOT NULL DEFAULT 'all',"
				+ "permission_denied VARCHAR(128),"
				+ "type TINYINT NOT NULL DEFAULT 0,"
				+ "custom_type VARCHAR(64),"
				+ "location TINYINT NOT NULL DEFAULT 0,"
				+ "mode TINYINT NOT NULL DEFAULT 0,"
				+ "animation TINYINT NOT NULL DEFAULT 0,"
				+ "tracking TINYINT NOT NULL DEFAULT 0,"
				+ "label VARCHAR(128),"
				+ "equip_message VARCHAR(128),"
				+ "offset_x DOUBLE NOT NULL DEFAULT 0,"
				+ "offset_y DOUBLE NOT NULL DEFAULT 0,"
				+ "offset_z DOUBLE NOT NULL DEFAULT 0,"
				+ "angle_x DOUBLE NOT NULL DEFAULT 0,"
				+ "angle_y DOUBLE NOT NULL DEFAULT 0,"
				+ "angle_z DOUBLE NOT NULL DEFAULT 0,"
				+ "update_frequency TINYINT NOT NULL DEFAULT 2,"
				+ "icon_update_frequency TINYINT NOT NULL DEFAULT 1,"
				+ "speed TINYINT NOT NULL DEFAULT 0,"
				+ "count TINYINT NOT NULL DEFAULT 1,"
				+ "price INT NOT NULL DEFAULT 0,"
				+ "sound VARCHAR(64),"
				+ "volume DOUBLE NOT NULL DEFAULT 1,"
				+ "pitch DOUBLE NOT NULL DEFAULT 1,"
				+ "left_action TINYINT NOT NULL DEFAULT 0,"
				+ "right_action TINYINT NOT NULL DEFAULT 12,"
				+ "left_argument VARCHAR(128),"
				+ "right_argument VARCHAR(128),"
				+ "duration MEDIUMINT NOT NULL DEFAULT 20,"
				+ "display_mode TINYINT NOT NULL DEFAULT 0,"
				+ "particle_scale DOUBLE NOT NULL DEFAULT 0.2"
				+ ")";
		return query;
	}
	
	public String getMetaTableQuery (String menuName)
	{
		String query = "CREATE TABLE IF NOT EXISTS menu_" + menuName + "_meta ("
				+ "slot TINYINT,"
				+ "type TINYINT,"
				+ "line TINYINT,"
				+ "value VARCHAR(64),"
				+ "PRIMARY KEY(slot, type, line),"
				+ "FOREIGN KEY(slot) REFERENCES menu_" + menuName + "_items(slot) ON DELETE CASCADE ON UPDATE CASCADE"
				+ ")";
		return query;
	}
	
	/**
	 * Creates a new InputStream from an existing InputStream removing comments
	 * @param stream
	 * @return
	 */
	private InputStream getTrimmedStream (InputStream stream) throws IOException
	{
		BufferedImage image = ImageIO.read(stream);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", baos);
		return new ByteArrayInputStream(baos.toByteArray());
	}
}
