package com.mediusecho.particlehats.ui;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.AbstractMenu.MenuClickResult;

public abstract class MenuManager {

	protected final ParticleHats core;
	
	protected final Player owner;
	protected final UUID ownerID;
	protected final PlayerState ownerState;
	
	protected Deque<AbstractMenu> openMenus;
	
	private boolean openingMenu = false;
	
	public MenuManager (final ParticleHats core, final Player owner)
	{
		this.core = core;
		
		this.owner = owner;
		this.ownerID = owner.getUniqueId();
		this.ownerState = core.getPlayerState(owner);
		
		openMenus = new ArrayDeque<AbstractMenu>();
	}
	
	protected void onClick (InventoryClickEvent event, boolean inMenu, AbstractMenu menu)
	{
		if (menu == null) {
			return;
		}
		
		final Inventory inventory = event.getInventory();
		if (!menu.hasInventory(inventory)) 
		{
			ParticleHats.debug(owner.getName() + " is using a foreign menu, unregistering menu manager");
			
			ownerState.setMenuManager(null);
			return;
		}
		
		event.setCancelled(true);
		
		MenuClickResult result = menu.onClick(event, event.getRawSlot(), inMenu);
		if (result != MenuClickResult.NONE) {
			playSound(result);
		}
	}
	
	public abstract void onClick (InventoryClickEvent event, boolean inMenu);
	
	/**
	 * Returns the owner's PlayerState
	 * @return
	 */
	public PlayerState getOwnerState () {
		return ownerState;
	}
	
	/**
	 * Notifies the manager that this menu will be opened
	 * @param menu
	 */
	public void isOpeningMenu () {
		openingMenu = true;
	}
	
	/**
	 * Add a new menu to the stack
	 * @param menu
	 */
	public void addMenu (AbstractMenu menu) {
		openMenus.add(menu);
	}
	
	/**
	 * Returns the menu currently being viewed
	 * @return
	 */
	public AbstractMenu getCurrentMenu () {
		return openMenus.getLast();
	}
	
	/**
	 * Closes the current menu and opens the previous menu.
	 * This method will always make sure there is 1 menu in the stack at all times.
	 */
	public void closeCurrentMenu () 
	{
		if (openMenus.size() > 1) {
			openMenus.pollLast().onClose(false);
		}
		openMenus.getLast().open();
	}
	
	/**
	 * Removes the current menu from the stack
	 */
	public void removeCurrentMenu () {
		openMenus.removeLast();
	}
	
	/**
	 * Check to see if this MenuManager can be unregistered
	 * @return
	 */
	protected boolean canUnregister () {
		return !openingMenu;
	}
	
	/**
	 * Notifies the MenuManager that it is about to unregister
	 * Use this method to save any changes
	 */
	public void willUnregister ()
	{
		for (AbstractMenu menu : openMenus) {
			menu.onClose(true);
		}
		unregister();
	}
	
	/**
	 * Unregisters this MenuManager
	 */
	protected void unregister () {
		ownerState.setMenuManager(null);
	}
	
	/**
	 * Open the first menu in the stack
	 */
	public abstract void open ();
	
	/**
	 * 
	 * @param ticks
	 */
	public abstract void onTick (int ticks);
	
	/**
	 * Plays a sound any time a button is clicked inside a menu
	 * @param result
	 */
	public abstract void playSound (MenuClickResult result);
	
	/**
	 * Called any time an inventory is opened for this MenuManager
	 * @param event
	 */
	public void onInventoryOpen (InventoryOpenEvent event)
	{
		openingMenu = false;
		ParticleHats.debug("inventory opened");
	}
	
	/**
	 * Called any time an inventory is closed for this MenuManager
	 * @param event
	 */
	public void onInventoryClose (InventoryCloseEvent event)
	{
		// Unregister this menu manager since we're not opening another menu
		if (canUnregister())
		{
			ParticleHats.debug("Unregistering menu manager since no other menu is being opened");
			willUnregister();
		}
		
		openingMenu = false;
		
		ParticleHats.debug("inventory closed");
	}
	
}
