package com.mediusecho.particlehats.database;

import java.awt.image.BufferedImage;
import java.util.HashMap;
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
	 * Creates a duplicate copy of this hat in a different slot
	 * @param menuName
	 * @param currentSlot
	 * @param newSlot
	 */
	public void cloneHatData (String menuName, int currentSlot, int newSlot);
	
	/**
	 * Moves this hat and all data to a new menu
	 * @param fromMenu
	 * @param toMenu
	 * @param slot
	 */
	public void moveHatData (String fromMenu, String toMenu, int fromSlot, int toSlot);
	
	/**
	 * Gets all stored images on the database
	 * @return
	 */
	public Map<String, BufferedImage> getImages (boolean forceUpdate);
	
	/**
	 * Save this hats meta data to the menu
	 * @param menuName
	 * @param hat
	 * @param type
	 */
	public void saveMetaData (String menuName, Hat hat, DataType type, int index);
	
	/**
	 * Save this hats particle data to the menu
	 * @param menuName
	 * @param hat
	 */
	public void saveParticleData (String menuName, Hat hat, int index);
	
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
		NONE (0),
		DESCRIPTION (1),
		PERMISSION_DESCRIPTION (2),
		ICON (3),
		TAGS (4),
		ITEMSTACK (5);
		
		private static Map<Integer, DataType> dataID = new HashMap<Integer, DataType>();
		static 
		{
			for (DataType type : values()) {
				dataID.put(type.id, type);
			}
		}
		
		private final int id;
		
		private DataType (final int id)
		{
			this.id = id;
		}
		
		public int getID () {
			return id;
		}
		
		public static DataType fromID (int id) 
		{
			if (dataID.containsKey(id)) {
				return dataID.get(id);
			}
			return DataType.NONE;
		}
	}
}
