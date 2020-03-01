package com.mediusecho.particlehats.ui.menus;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.ui.MenuManager;
import com.mediusecho.particlehats.ui.properties.MenuButton;
import com.mediusecho.particlehats.ui.properties.MenuContentRegion;

/**
 * Represents a menu that can contain multiple inventories to interact with
 * @author MediusEcho
 *
 */
public abstract class ListMenu extends MenuImpl {

	protected final MenuContentRegion contentRegion;
	
	protected Map<Integer, Inventory> pages;
	protected boolean isEmpty;
	
	protected int currentPage = 0;
	
	public ListMenu(ParticleHats core, MenuManager menuManager, Player owner, MenuContentRegion region) 
	{
		super(core, menuManager, owner);
		
		this.contentRegion = region;
		this.pages = new HashMap<Integer, Inventory>();
		this.isEmpty = false;
	}

	@Override
	public void onClose(boolean forced) {}
	
	@Override
	public void open () 
	{
		Inventory inventory = getInventory(currentPage);
		if (inventory == null) {
			return;
		}
		
		menuManager.isOpeningMenu(this);
		owner.openInventory(inventory);
	}

	@Override
	public void onTick(int ticks) {}

	@Override
	public boolean hasInventory(Inventory inventory) {
		return pages.containsValue(inventory);
	}
	
	/**
	 * Sets whether this menu is empty or not.
	 * Will call either <b>{@link #insertEmptyItem()} or {@link #removeEmptyItem()}</b> depending on empty state
	 * @param empty
	 */
	protected void setEmpty(boolean empty) 
	{
		if (isEmpty == empty) {
			return;
		}
		
		isEmpty = empty;
		if (empty) {
			insertEmptyItem();
		} else {
			removeEmptyItem();
		}
	}

	/**
	 * Returns an item stored at the given slot for the given page
	 * @param page Which page (inventory) to get the item from, 0 being the first page
	 * @param slot
	 * @return ItemStack
	 */
	protected ItemStack getItem(int page, int slot) {
		return getInventory(page).getItem(slot);
	}

	/**
	 * Sets the item for the given page at the given slot
	 * @param page
	 * @param slot
	 */
	protected void setItem(int page, int slot, ItemStack item) {
		getInventory(page).setItem(slot, item);
	}

	/**
	 * Returns an inventory for the given page
	 * @param page
	 * @return
	 */
	protected Inventory getInventory(int page) {
		return pages.get(page);
	}

	/**
	 * Sets an inventory for the given page
	 * @param page
	 * @param inventory
	 */
	protected void setInventory(int page, Inventory inventory) {
		pages.put(page, inventory);
	}

	/**
	 * Removes an item from the given slot in the given page
	 * @param page
	 * @param slot
	 */
	protected void deleteItem(int page, int slot) 
	{
		int totalPages = getTotalPages();
		if (page >= totalPages) {
			return;
		}
		
		setItem(page, slot, null);
		
		int startingSlot = (page * contentRegion.getTotalSlots()) + contentRegion.getClampedIndex(slot);
		int totalSlots = contentRegion.getTotalSlots() * totalPages;
		
		for (int i = startingSlot + 1; i < totalSlots; i++)
		{
			int currentPage = contentRegion.getPage(i);
			int currentSlot = contentRegion.getNextSlot(i - (currentPage * contentRegion.getTotalSlots()));
			
			int shiftedPage = contentRegion.getPage(i - 1);
			int shiftedSlot = contentRegion.getNextSlot((i - 1) - (shiftedPage * contentRegion.getTotalSlots()));
			
			ItemStack item = getInventory(currentPage).getItem(currentSlot);
			
			setItem(currentPage, currentSlot, null);
			setItem(shiftedPage, shiftedSlot, item);
		}		
	}

	/**
	 * Clears all items inside this menus content region
	 */
	protected void clearContent() 
	{
		for (int i = 0; i < contentRegion.getTotalSlots(); i++) 
		{
			int page = contentRegion.getPage(i);
			int slot = contentRegion.getNormalIndex(i);
			
			setItem(page, slot, null);
		}
	}
	
	/**
	 * Sets the item and action for the given slot
	 * @param page
	 * @param slot
	 * @param item
	 * @param action
	 */
	protected void setButton (int page, int slot, ItemStack item, MenuAction action)
	{
		setItem(page, slot, item);
		setAction(slot, action);
	}
	
	/**
	 * Set the MenuButton for the given slot in the given menu
	 * @param page
	 * @param slot
	 * @param button
	 */
	protected void setButton (int page, int slot, MenuButton button)
	{
		setItem(page, slot, button.getItem());
		setAction(slot, button.getAction());
	}
	
	/**
	 * Get how many inventories this menu is responsible for
	 * @return
	 */
	protected int getTotalPages () {
		return pages.size();
	}
	
	/**
	 * Called when a menu's content section is empty
	 */
	protected void insertEmptyItem () {}
	
	/**
	 * Called when an empty menu has received content
	 */
	protected void removeEmptyItem () {}

}
