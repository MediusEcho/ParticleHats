package com.mediusecho.particlehats.configuration;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.util.ResourceUtil;

public class CustomConfig {

	private final Core core;
	
	private File file;
	private FileConfiguration config;
	
	private final String path;
	private final String name;
	private final String directory;
	
	public CustomConfig (final Core core, final String path, final String name, boolean logOutput)
	{
		this.core = core;
		
		this.path = path;
		this.name = name;
		
		directory = core.getDataFolder() + File.separator + path;
		file = new File(directory + File.separator + name);
		config = new YamlConfiguration();
		
		// Load the configuration file
		if (!file.exists()) {
			file = createFile(logOutput);
		} else {
			Core.log("Loadaing " + path + File.separator + name);
		}
		
		try {
			config.load(file);
		} catch (Exception e) {
			Core.log("There was an error loading " + name + ", error: " + e.getClass().getSimpleName());
		}
	}
	
	/**
	 * Saves any changes in this configuration file
	 */
	public void save ()
	{
		try {
			config.save(file);
		} catch (Exception e) {}
	}
	
	/**
	 * Reloads this configuration file
	 */
	public void reload ()
	{
		try {
			config = YamlConfiguration.loadConfiguration(file);
		} catch (Exception e) {
			Core.log("There was an error loading " + name + ", error: " + e.getClass().getSimpleName());
		}
	}
	
	/**
	 * Get this CustomConfig configuration file
	 * @return
	 */
	public FileConfiguration getConfig () {
		return config;
	}
	
	/**
	 * Creates a file<br>
	 * will try to load from an existing file first before creating a blank file
	 * @param logOutput
	 * @return
	 */
	private File createFile (boolean logOutput)
	{
		file.getParentFile().mkdirs();
		file = new File(directory + File.separator + name);
		
		// Try to copy an existing .yml file into this one
		if (core.getResource(name) != null) 
		{
			try {
				ResourceUtil.copyFile(core.getResource(name), file);
			} catch (IOException e) { }
		}
		
		else
		{
			if (!file.exists()) 
			{
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (logOutput) {
			Core.log("Creating " + path + File.separator + name);
		}
		
		return file;
	}
}
