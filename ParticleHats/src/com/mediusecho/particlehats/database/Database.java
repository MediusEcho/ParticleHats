package com.mediusecho.particlehats.database;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.ui.MenuInventory;

public interface Database {
	
	/**
	 * Save any changes or close any connections when this plugin is disabled
	 */
	public void onDisable ();
	
	/**
	 * Loads this menu and all its hats
	 * @param menuName
	 * @return
	 */
	public MenuInventory loadInventory (String menuName);
	
	/**
	 * Creates and inserts an empty menu into our database
	 * @param menuName
	 */
	public void createEmptyMenu (String menuName);
	
	/**
	 * Deletes a menu and all it's data
	 * @param menuName
	 */
	public void deleteMenu (String menuName);
	
	/**
	 * Check to see if this menu exists in our database
	 * @param menuName
	 * @return
	 */
	public boolean menuExists (String menuName);
	
	/**
	 * Returns a list of menus that exist in our database
	 * @param forceUpdate Forces the menu cache to be updated
	 * @return
	 */
	public Map<String, String> getMenus (boolean forceUpdate);
	
	/**
	 * Checks to see if this label has already been used in the database
	 * @param menuName
	 * @param label
	 * @return
	 */
	public boolean labelExists (String menuName, String label);
	
	/**
	 * Inserts a new hat entry into the database
	 * @param menuName
	 * @param slot
	 */
	public void createHat (String menuName, int slot);
	
	/**
	 * Loads all data for this hat
	 * @param menuName
	 * @param slot
	 * @param hat
	 */
	public void loadHatData (String menuName, int slot, Hat hat);
	
	/**
	 * 
	 * @return
	 */
	public Map<String, BufferedImage> getImages (boolean forceUpdate);
	
	/**
	 * Save this hats meta data to the menu
	 * @param menuName
	 * @param hat
	 * @param type
	 */
	public void saveMetaData (String menuName, Hat hat, DataType type);
	
	/**
	 * Deletes this hat from the database
	 * @param menuName
	 * @param slot
	 */
	public void deleteHat (String menuName, int slot);
	
	public void changeSlot (String menuName, int previousSlot, int newSlot, boolean swapping);
	
	public void saveMenuTitle (String menuName, String title);
	
	public void saveMenuSize (String menuName, int rows);
	
	public enum DataType
	{
		DESCRIPTION (1),
		PERMISSION_DESCRIPTION (2),
		ICON (3),
		TAGS (4),
		PARTICLES (5);
		
		private final int id;
		
		private DataType (final int id)
		{
			this.id = id;
		}
		
		public int getID () {
			return id;
		}
	}
}
