package com.mediusecho.particlehats.database.type.mysql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mediusecho.particlehats.Core;

public class MySQLHelper {

	private final MySQLDatabase database;
	
	public MySQLHelper (final MySQLDatabase database)
	{
		this.database = database;
	}
	
	/**
	 * Creates our initial database
	 * @throws SQLException 
	 */
	public void initDatabase () throws SQLException
	{
		database.async(() -> 
		{
			database.connect((connection) ->
			{
				String menuTableCreate = "CREATE TABLE IF NOT EXISTS menus ("
						+ "name VARCHAR(128) PRIMARY KEY,"
						+ "title VARCHAR(40) NOT NULL,"
						+ "size TINYINT(3) NOT NULL DEFAULT 6"
						+ ")";
				
				try (PreparedStatement statement = connection.prepareStatement(menuTableCreate))
				{
					if (statement.execute()) {
						Core.log("Creating initial database");
					}
				}
			});
		});
	}
}
