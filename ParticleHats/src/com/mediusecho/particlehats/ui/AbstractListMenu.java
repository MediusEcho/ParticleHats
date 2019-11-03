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

	protected Map<Integer, Inventory> menus;
	
	protected int totalPages = 0;
	protected int currentPage = 0;
	
	public AbstractListMenu(ParticleHats core, MenuManager menuManager, Player owner) 
	{
		super(core, menuManager, owner);
		
		menus = new HashMap<Integer, Inventory>();
	}
	
	@Override
	public void open () 
	{
		Inventory inventory = menus.get(currentPage);
		if (inventory == null) {
			return;
		}
		
		menuManager.isOpeningMenu();
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
	 * Calculates how many pages we'll need to display all content
	 * @param totalCount
	 * @param itemsPerPage
	 * @return
	 */
	protected int calculatePageCount (double totalCount, int itemsPerPage) {
		return (int) Math.max(Math.ceil((totalCount - 1) / itemsPerPage), 1);
	}

}
