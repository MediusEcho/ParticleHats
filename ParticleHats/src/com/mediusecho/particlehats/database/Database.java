package com.mediusecho.particlehats.database;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mediusecho.particlehats.database.properties.Group;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.MenuInventory;

public interface Database {
	
	/**
	 * Save any changes or close any connections when this plugin is disabled
	 */
	public void onDisable ();
	
	/**
	 * Check to see if this database initialized successfully
	 * @return
	 */
	public boolean isEnabled ();
	
	/**
	 * Get this databases last exception
	 * @return
	 */
	public Exception getException ();
	
	/**
	 * Loads this menu and all its hats
	 * @param menuName
	 * @return
	 */
	public MenuInventory loadInventory (String menuName, PlayerState playerState);
	
	/**
	 * Loads and returns a MenuInventory linked to this alias
	 * @param alias
	 * @return
	 */
	public MenuInventory getInventoryFromAlias (String alias, PlayerState playerState);
	
	/**
	 * Gets the purchase menu
	 * @param playerState
	 * @return
	 */
	public MenuInventory getPurchaseMenu (PlayerState playerState);
	
	/**
	 * Creates and inserts an empty menu into our database
	 * @param menuName
	 */
	public void createMenu (String menuName);
	
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
	 * Gets all stored images on the database
	 * @return
	 */
	public Map<String, BufferedImage> getImages (boolean forceUpdate);
	
	/**
	 * Gets a list of unique labels, duplicate labels are discarded
	 * @param forceUpdate
	 * @return
	 */
	public List<String> getLabels (boolean forceUpdate);
	
	/**
	 * Gets a list of groups
	 * @param forceUpdate
	 * @return
	 */
	public List<Group> getGroups (boolean forceUpdate);
	
	/**
	 * Checks to see if this label has already been used in the database
	 * @param menuName
	 * @param label
	 * @return
	 */
	public boolean labelExists (String menuName, String label);
	
	/**
	 * Loads a hat with the given label
	 * @param label
	 * @return
	 */
	public Hat getHatFromLabel (String label);
	
	/**
	 * Inserts a new hat entry into the database
	 * @param menuName
	 * @param slot
	 */
	public void createHat (String menuName, Hat hat);
	
	/**
	 * Loads all data for this hat
	 * @param menuName
	 * @param slot
	 * @param hat
	 */
	public void loadHat (String menuName, int slot, Hat hat);
	
	/**
	 * Saves this hats modified values
	 * @param menuName
	 * @param slot
	 * @param hat
	 */
	public void saveHat (String menuName, int slot, Hat hat);
	
	/**
	 * Saves this nodes modified values
	 * @param menuName
	 * @param nodeIndex
	 * @param hat
	 */
	public void saveNode (String menuName, int nodeIndex, Hat hat);
	
	/**
	 * Creates a duplicate copy of this hat in a different slot
	 * @param menuName
	 * @param currentSlot
	 * @param newSlot
	 */
	public void cloneHat (String menuName, Hat hat, int newSlot);
	
	/**
	 * Moves this hat and all data to a new menu
	 * @param hat
	 * @param fromMenu Current menu the hat is inside of
	 * @param toMenu Menu we're moving this hat to, leave as <b>NULL</b> if moving slots in the same menu
	 * @param fromSlot Current slot the hat is in
	 * @param toSlot New slot the hat will be moved to
	 * @param swapping Swap this hat with the hat existing at the toSlot slot
	 */
	public void moveHat (Hat fromHat, Hat toHat, String fromMenu, String toMenu, int fromSlot, int toSlot, boolean swapping);
	
	/**
	 * Deletes a hat from this menu
	 * @param menuName
	 * @param slot
	 */
	public void deleteHat (String menuName, int slot);
	
	/**
	 * Deletes a node from this menu
	 * @param menuName
	 * @param slot
	 */
	public void deleteNode (String menuName, int slot, int nodeIndex);
	
	/**
	 * Save this hats particle data to the menu
	 * @param menuName
	 * @param hat
	 */
	public void saveParticleData (String menuName, Hat hat, int index);
	
	/**
	 * Save this hats meta data to the menu
	 * @param menuName
	 * @param hat
	 * @param type
	 */
	public void saveMetaData (String menuName, Hat hat, DataType type, int index);
	
	/**
	 * Saves a menu's title
	 * @param menuName
	 * @param title
	 */
	public void saveMenuTitle (String menuName, String title);
	
	/**
	 * Save a menu's alias
	 * @param menuName
	 * @param alias
	 */
	public void saveMenuAlias (String menuName, String alias);
	
	/**
	 * Saves a menu's size
	 * @param menuName
	 * @param rows
	 */
	public void saveMenuSize (String menuName, int rows);
	
	/**
	 * Saves the player's equipped hats
	 * @param id
	 * @param hats
	 */
	public void savePlayerEquippedHats (UUID id, List<Hat> hats);
	
	/**
	 * Loads the player's equipped hats
	 * @param id
	 * @param callback
	 */
	public void loadPlayerEquippedHats (UUID id, DatabaseCallback callback);
	
	/**
	 * Saves the player's purchased hat
	 * @param id
	 * @param hat
	 */
	public void savePlayerPurchase (UUID id, Hat hat);
	
	/**
	 * Loads all hats the player has purchased
	 * @param id
	 * @param callback
	 */
	public void loadPlayerPurchasedHats (UUID id, DatabaseCallback callback);
	
	/**
	 * Adds a new group to the database
	 * @param groupName
	 * @param defaultMenu
	 * @param weight
	 */
	public void addGroup (String groupName, String defaultMenu, int weight);
	
	/**
	 * Deletes a group from the database
	 * @param groupName
	 */
	public void deleteGroup (String groupName);
	
	/**
	 * Edits an existing group
	 * @param groupName
	 * @param defaultMenu
	 * @param weight
	 */
	public void editGroup (String groupName, String defaultMenu, int weight);
	
	/***
	 * Removes a custom type image from the database
	 * @param imageName
	 * @return
	 */
	public boolean deleteImage (String imageName);
	
	/**
	 * Reloads any important data in this database
	 */
	public void onReload ();
	
	@FunctionalInterface
	public interface DatabaseCallback {
		public void execute(Object o);
	}
	
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
