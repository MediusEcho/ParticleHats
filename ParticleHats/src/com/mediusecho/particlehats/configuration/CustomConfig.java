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
	private final String fileName;
	private final String name;
	private final String directory;
	
	public CustomConfig (final Core core, final String path, final String name, boolean logOutput)
	{
		this.core = core;
		this.path = path;
		this.fileName = name;
		this.name = ResourceUtil.removeExtension(name);
		this.directory = core.getDataFolder() + File.separator + path;
		this.file = new File(directory + File.separator + name);
		this.config = new YamlConfiguration();
		
		// Load the configuration file
		if (!file.exists()) {
			file = createFile(logOutput);
		} else if (logOutput) {
			Core.log("Loading " + path + File.separator + fileName);
		}
		
		try {
			config.load(file);
		} catch (Exception e) {
			Core.log("There was an error loading " + name + ", error: " + e.getClass().getSimpleName());
		}
	}
	
	public CustomConfig (final Core core, final String path, File file,  boolean logOutput)
	{
		this.core = core;
		this.path = path;
		this.fileName = file.getName();
		this.name = ResourceUtil.removeExtension(file.getName());
		this.file = file;
		this.directory = core.getDataFolder() + File.separator + path;
		this.config = new YamlConfiguration();
		
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
	 * Tries to delete this Configuration File
	 * @return
	 */
	public boolean delete () {
		return file.delete();
	}
	
	public String getFileName () {
		return fileName;
	}
	
	/**
	 * Returns the name of this file
	 * @return
	 */
	public String getName () {
		return name;
	}
	
	public void set (String path, Object value) {
		config.set(path, value);
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
		file = new File(directory + File.separator + fileName);
		
		// Try to copy an existing .yml file into this one
		if (core.getResource(fileName) != null) 
		{
			try {
				ResourceUtil.copyFile(core.getResource(fileName), file);
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
			Core.log("Creating " + path + File.separator + fileName);
		}
		
		return file;
	}
}
