package com.mediusecho.particlehats.ui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;

/**
 * Menu that only has one inventory
 * @author MediusEcho
 *
 */
public abstract class AbstractStaticMenu extends AbstractMenu {

	protected Inventory inventory;
	
	public AbstractStaticMenu(ParticleHats core, MenuManager menuManager, Player owner) 
	{
		super(core, menuManager, owner);
	}
	
	@Override
	public void open () 
	{
		menuManager.isOpeningMenu(this);
		owner.openInventory(inventory);
	}
	
	@Override
	public boolean hasInventory (Inventory inventory) {
		return this.inventory == inventory;
	}
	
	@Override
	public String getName () {
		return "";
	}
	
	/**
	 * Get the item at this slot
	 * @param slot
	 * @return
	 */
	protected ItemStack getItem (int slot) {
		return inventory.getItem(slot);
	}
	
	/**
	 * Place an item into this inventory
	 * @param slot
	 * @param item
	 */
	protected void setItem (int slot, ItemStack item) {
		inventory.setItem(slot, item);
	}
	
	/**
	 * Set the ItemStack and MenuAction for the given slot
	 * @param slot
	 * @param item
	 * @param action
	 */
	protected void setButton (int slot, ItemStack item, MenuAction action)
	{
		setItem(slot, item);
		setAction(slot, action);
	}
	
	/**
	 * Set the MenuButton for the given slot
	 * @param button
	 */
	protected void setButton (int slot, MenuButton button)
	{
		setItem(slot, button.getItem());
		setAction(slot, button.getAction());
	}

}
