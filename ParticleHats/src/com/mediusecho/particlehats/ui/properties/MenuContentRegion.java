package com.mediusecho.particlehats.ui.properties;

import com.mediusecho.particlehats.ui.menus.Menu;
import com.mediusecho.particlehats.ui.menus.Menu.MenuAction;

/**
 * Represents a region of dynamic content in a menu
 * @author MediusEcho
 *
 */
public class MenuContentRegion {
	
	private final int startingSlot;
	private final int offset;
	private final int totalSlots;
	
	public static final MenuContentRegion defaultLayout = new MenuContentRegion(10, 43);
	public static final MenuContentRegion extendedLayout = new MenuContentRegion(0, 44);
	
	public MenuContentRegion (int startingSlot, int endingSlot)
	{
		this.startingSlot = startingSlot;
		
		int leftMargin = startingSlot % 9;
		int rightMargin = 8 - (endingSlot % 9);
		int regionWidth = 9 - (leftMargin + rightMargin);
		
		int topMargin = Math.floorDiv(startingSlot, 9);
		int bottomMargin = 5 - (Math.floorDiv(endingSlot, 9));
		int regionHeight = 6 - (topMargin + bottomMargin);
		
		this.offset = leftMargin + rightMargin;
		this.totalSlots = regionWidth * regionHeight;
	}
	
	/**
	 * Get the starting slot for this region
	 * @return
	 */
	public int getStartingSlot () {
		return startingSlot;
	}
	
	/**
	 * Get the amount of slots per row skipped
	 * @return
	 */
	public int getOffset () {
		return offset;
	}
	
	/**
	 * Gets the total amount of usable slots in this region
	 * @return
	 */
	public int getTotalSlots () {
		return totalSlots;
	}
	
	/**
	 * Returns an index that can be used in a array/list based on the visible content region
	 * @param slot
	 * @return
	 */
	public int getListIndex (int slot) {
		return Math.max((slot - (((slot / 9) - 1) * offset) - startingSlot), 0);
	}

	/**
	 * Returns an inventory slot based on the visible content region from the provided index (starting at 0)
	 * @param slot 
	 * @return
	 */
	public int getInventorySlot (int index) {
		return (index + ((index / (9 - offset)) * offset) + startingSlot);
	}
	
	/**
	 * Combines page count + slot and returns an index that can be used in arrays/lists
	 * @param page
	 * @param slot
	 * @return
	 */
	public int getInclusiveIndex (int page, int slot) {
		return (page * totalSlots) + getListIndex(slot);
	}
	
	/**
	 * Returns the current page number based on the size provided.  
	 * @param size
	 * @return
	 */
	public int getPage (int size) {
		return Math.floorDiv(size, totalSlots);
	}
	
	/**
	 * Returns the next inventory slot based on the size provided
	 * @param size
	 * @return
	 */
	public int getNextSlot (int size) {
		return getInventorySlot(size % totalSlots);
	}
	
	/**
	 * Fills the content region with a given action
	 * @param menu
	 * @param action
	 */
	public void fillRegion (Menu menu, MenuAction action)
	{
		for (int i = 0; i < getTotalSlots(); i++) {
			menu.setAction(getInventorySlot(i), action);
		}
	}
	
}