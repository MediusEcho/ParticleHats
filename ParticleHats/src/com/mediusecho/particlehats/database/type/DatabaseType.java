package com.mediusecho.particlehats.database.type;

import java.util.Arrays;
import java.util.List;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.database.type.mysql.MySQLDatabase;
import com.mediusecho.particlehats.database.type.yaml.YamlDatabase;

public enum DatabaseType {

	YAML ("yml, yaml"),
	MYSQL ("mysql", "MySQL");
	
	private List<String> aliases;
	
	private DatabaseType (String... aliases) {
		this.aliases = Arrays.asList(aliases);
	}
	
	/**
	 * Returns the Database object of this type
	 * @return
	 */
	public Database getDatabase (Core core, DatabaseConnectionCallback callback)
	{
		switch (this)
		{	
		case MYSQL:
			return new MySQLDatabase(core, callback);
		default:
			return new YamlDatabase(core);
		}
	}
	
	public Database getDatabase (Core core) {
		return new YamlDatabase(core);
	}
	
	/**
	 * Returns the DatabaseType that uses this alias, or YAML if none are found
	 * @param alias
	 * @return
	 */
	public static DatabaseType fromAlias (String alias)
	{
		for (DatabaseType type : values())
		{
			if (type.aliases.contains(alias.toLowerCase())) {
				return type;
			}
		}
		return YAML;
	}
	
	@FunctionalInterface
	public interface DatabaseConnectionCallback {
		public void onTimeout(Exception e);
	}
}
