package com.mediusecho.particlehats.ui.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.ui.MenuManager;
import com.mediusecho.particlehats.ui.properties.MenuButton;

/**
 * Represents a menu with only one inventory to interact with
 * @author MediusEcho
 *
 */
public abstract class SingularMenu extends MenuImpl {

	protected Inventory inventory;
	
	public SingularMenu(ParticleHats core, MenuManager menuManager, Player owner) 
	{
		super(core, menuManager, owner);
	}

	@Override
	public void open() 
	{
		menuManager.isOpeningMenu(this);
		owner.openInventory(inventory);
	}

	@Override
	public boolean hasInventory(Inventory inventory) {
		return this.inventory.equals(inventory);
	}
	
	@Override
	public void onClose (boolean forced) {}
	
	@Override
	public void onTick (int ticks) {}
	
	/**
	 * Returns the item stored at the given slot
	 * @param slot
	 * @return
	 */
	protected ItemStack getItem (int slot) {
		return inventory.getItem(slot);
	}
	
	/**
	 * Sets the item stored at the given slot
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
