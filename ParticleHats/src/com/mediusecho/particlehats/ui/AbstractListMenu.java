package com.mediusecho.particlehats.ui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;

/**
 * Menu that will have multiple pages of inventories
 * @author MediusEcho
 *
 */
public abstract class AbstractListMenu extends AbstractMenu {

	protected final boolean canEdit;
	
	protected Map<Integer, Inventory> menus;
	
	protected int totalPages = 0;
	protected int currentPage = 0;
	
	public AbstractListMenu(ParticleHats core, MenuManager menuManager, Player owner, final boolean canEdit) 
	{
		super(core, menuManager, owner);
		
		this.canEdit = canEdit;
		this.menus = new HashMap<Integer, Inventory>();
	}
	
	@Override
	public void open () 
	{
		Inventory inventory = menus.get(currentPage);
		if (inventory == null) {
			return;
		}
		
		menuManager.isOpeningMenu(this);
		owner.openInventory(inventory);
	}
	
	@Override
	public boolean hasInventory (Inventory inventory) {
		return menus.containsValue(inventory);
	}
	
	/**
	 * Get the ItemStack at the current slot
	 * @param page
	 * @param slot
	 * @return
	 */
	protected ItemStack getItem (int page, int slot)
	{
		Inventory inventory = menus.get(page);
		if (inventory == null) {
			return null;
		}
		return inventory.getItem(slot);
	}
	
	/**
	 * Place an item into the inventory at the given page
	 * @param slot
	 * @param item
	 */
	protected void setItem (int page, int slot, ItemStack item) 
	{
		Inventory inventory = menus.get(page);
		if (inventory == null) {
			return;
		}
		inventory.setItem(slot, item);
	}
	
	/**
	 * Set the ItemStack and MenuAction for the given slot in the given menu
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
	 * Set the inventory at the given page
	 * @param page
	 * @param inventory
	 */
	protected void setMenu (int page, Inventory inventory) {
		menus.put(page, inventory);
	}
	
	/**
	 * Deletes the content at the given slot and shifts all other content over
	 * @param page
	 * @param slot
	 */
	protected void deleteSlot (int page, int slot)
	{
		if (!canEdit) {
			return;
		}
		
		if (page >= this.totalPages) {
			return;
		}
		
		setItem(page, slot, null);
		int startingIndex = (page * 28) + getClampedIndex(slot, 10, 2);
		int totalSlots = 28 * totalPages;
		
		for (int i = startingIndex + 1; i < totalSlots; i++)
		{
			int currentPage = Math.floorDiv(i, 28);
			int currentIndex = getNormalIndex(i - (currentPage * 28), 10, 2);
			
			int shiftedPage = Math.floorDiv(i - 1, 28);
			int shiftedIndex = getNormalIndex((i - 1) - (shiftedPage * 28), 10, 2);
			
			ItemStack item = menus.get(currentPage).getItem(currentIndex);
			
			setItem(currentPage, currentIndex, null);
			setItem(shiftedPage, shiftedIndex, item);
		}
	}
	
	/**
	 * Clear all content in each menu
	 */
	protected void clearContent ()
	{
		if (!canEdit) {
			return;
		}
		
		int totalSlots = 28 * totalPages;
		for (int i = 0; i < totalSlots; i++)
		{
			int currentPage = Math.floorDiv(i, 28);
			int currentIndex = getNormalIndex(i - (currentPage * 28), 10, 2);
			
			menus.get(currentPage).setItem(currentIndex, null);
		}
		
		for (int i = 0; i < 28; i++) {
			setAction(getNormalIndex(i, 10, 2), null);
		}
	}

}
