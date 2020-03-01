package com.mediusecho.particlehats.ui.menus;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.mediusecho.particlehats.ui.properties.ItemPointer;
import com.mediusecho.particlehats.ui.properties.MenuClickEvent;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;

public interface Menu {

	/**
	 * Returns a result from clicking this menu
	 * @param event
	 * @param slot The slot being clicked
	 * @param inMenu Whether the click was inside the menus inventory
	 * @return
	 */
	MenuClickResult onClick (InventoryClickEvent event, final int slot, final boolean inMenu);
	
	/**
	 * Returns a result from clicking outside this menu
	 * @param event
	 * @param slot
	 * @return
	 */
	MenuClickResult onClickOutside (InventoryClickEvent event, final int slot);
	
	/**
	 * Signals to the menu that is has been closed
	 * @param forced Whether the menu was forced closed by it's parent menu controller
	 */
	void onClose (boolean forced);
	
	/**
	 * Opens this menu
	 */
	void open ();
	
	/**
	 * Updates this menus content
	 * @param ticks
	 */
	void onTick (int ticks);
	
	/**
	 * Returns this menu's name
	 * @return
	 */
	String getName ();
	
	/**
	 * Get the player who opened this menu
	 * @return
	 */
	Player getOwner ();
	
	/**
	 * Get the menu owner's UUID
	 * @return
	 */
	UUID getOwnerId ();
	
	/**
	 * Sets a clickable action to the given slot
	 * @param slot
	 * @param action
	 */
	void setAction (int slot, MenuAction action);
	
	/**
	 * Returns a clickable action in the given slot
	 * @param slot
	 * @return
	 */
	MenuAction getAction (int slot);
	
	/**
	 * Returns true if a clickable action exists in the given slot
	 * @param slot
	 * @return
	 */
	boolean actionExists (int slot);
	
	/**
	 * Highlights the item found at the pointer location
	 * @param pointer
	 */
	void selectItem (ItemPointer pointer);
	
	/**
	 * Removes an item's highlight at the pointer location
	 * @param pointer
	 */
	void unselectItem (ItemPointer pointer);
	
	/**
	 * Returns true if this menu contains the given inventory
	 * @param inventory
	 * @return
	 */
	boolean hasInventory (Inventory inventory);
	
	/**
	 * Returns an inventory slot relative to 0 starting at the starting index<br>
	 * eg: (10, 10, 2) -> 0, (11, 10, 2) -> 1, (19, 10, 2) -> 7
	 * @param normalSlot
	 * @param startingIndex
	 * @param offset
	 * @return
	 */
	int getClampedIndex (int normalSlot, int startingIndex, int offset);
	
	/**
	 * Returns an inventory slot relative to the starting index starting at 0
	 * eg: (0, 10, 2) -> 10, (1, 10, 2) -> 11, (7, 10, 2) -> 19
	 * @param clampedSlot
	 * @param startingIndex
	 * @param offset
	 * @return
	 */
	int getNormalIndex (int clampedSlot, int startingIndex, int offset);
	
	/**
	 * Action to perform when selecting an Object
	 * @author MediusEcho
	 *
	 */
	@FunctionalInterface
	interface MenuObjectCallback {
		public void onSelect (Object obj);
	}
	
	/**
	 * Action to perform when returning from a menu
	 * @author MediusEcho
	 *
	 */
	@FunctionalInterface
	interface MenuCallback {
		public void onCallback ();
	}
	
	/**
	 * Action to perform when clicking on an item
	 * @author MediusEcho
	 *
	 */
	@FunctionalInterface
	public interface MenuAction {
		public MenuClickResult onClick (MenuClickEvent event, int slot);
	}
	
}
