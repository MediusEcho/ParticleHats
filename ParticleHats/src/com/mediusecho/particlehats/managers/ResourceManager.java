package com.mediusecho.particlehats.managers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.configuration.CustomConfig;

public class ResourceManager {

	private final Core core;
	private final Map<String, BufferedImage> images;
	private final List<CustomConfig> menus;
	
	public ResourceManager (final Core core)
	{
		this.core = core;
		images = new HashMap<String, BufferedImage>();
		menus = new ArrayList<CustomConfig>();
		
		loadResources();
	}
	
	public void onReload ()
	{
		images.clear();
		menus.clear();
		
		loadResources();
	}
	
	/**
	 * Get all images stored locally on this server
	 * @return
	 */
	public Map<String, BufferedImage> getImages () {
		return new HashMap<String, BufferedImage>(images);
	}
	
	/**
	 * Get all menus stored locally on this server
	 * @return
	 */
	public List<CustomConfig> getMenus () {
		return new ArrayList<CustomConfig>(menus);
	}
	
	private void loadResources ()
	{
		// Images
		File resourceDirectory = new File(core.getDataFolder() + File.separator + "types");
		if (resourceDirectory.isDirectory())
		{
			File[] imageFiles = resourceDirectory.listFiles();
			for (int i = 0; i < imageFiles.length; i++)
			{
				if (imageFiles[i].isFile())
				{	
					try
					{
						File imageFile = imageFiles[i];
						BufferedImage image = ImageIO.read(imageFile);
						images.put(FilenameUtils.removeExtension(imageFile.getName()), image);
						
					} catch (IOException e) {}
				}
			}
		}
		
		// Menus
		File menuDirectory = new File(core.getDataFolder() + File.separator + "menus");
		if (menuDirectory.isDirectory())
		{
			File[] menuFiles = menuDirectory.listFiles();
			for (int i = 0; i < menuFiles.length; i++)
			{
				if (menuFiles[i].isFile())
				{
					File menuFile = menuFiles[i];
					if (menuFile.getName().endsWith("yml") && !menuFile.getName().startsWith("."))
					{
						CustomConfig menu = new CustomConfig(core, "menus", menuFile, true);
						menus.add(menu);
					}
				}
			}
		}
	}
}