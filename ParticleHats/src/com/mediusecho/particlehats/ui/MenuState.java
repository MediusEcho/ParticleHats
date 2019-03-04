package com.mediusecho.particlehats.ui;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.editor.menus.EditorBaseMenu;
import com.mediusecho.particlehats.player.PlayerState;

public enum MenuState {

	/**
	 * Is not viewing any menus
	 */
	CLOSED,
	/**
	 * Has any normal menu open
	 */
	OPEN,
	/**
	 * Set when a player opens a specific menu
	 */
	OPEN_FROM_COMMAND,
	/**
	 * Is switching between pages.
	 */
	SWITCHING,
	/**
	 * Has the menu editor open
	 */
	BUILDING,
	/**
	 * Has the purchase menu open
	 */
	PURCHASING,
	/**
	 * Has the settings menu open
	 */
	SETTINGS;
	
	public void onClick (InventoryClickEvent event, PlayerState playerState)
	{
		if (this == CLOSED) {
			return;
		}
		
		ItemStack clickedItem = event.getCurrentItem();
		if (clickedItem != null && !clickedItem.getType().equals(Material.AIR))
		{		
			switch (this)
			{
				case BUILDING:
				{
					MenuBuilder menuBuilder = playerState.getMenuBuilder();
					if (menuBuilder != null)
					{
						event.setCancelled(true);
						boolean inMenu = event.getRawSlot() < event.getInventory().getSize();
						menuBuilder.onClick(event, inMenu);
					}
				}
				break;
			
			default: break;
			}
		}
	}
	
	public void onClose (PlayerState playerState)
	{
		switch (this)
		{
			default: break;
			case BUILDING:
			{
				// Remove this menuBuilder if the players MetaState is NONE
				if (playerState.getMetaState() == MetaState.NONE)
				{
					MenuBuilder menuBuilder = playerState.getMenuBuilder();
					if (menuBuilder != null) {
						menuBuilder.onClose();
					}
					
					playerState.setMenuState(MenuState.CLOSED);
					playerState.setMenuBuilder(null);
				}
			}
			break;
		}
	}
	
	public void onTick (PlayerState playerState, int ticks)
	{
		switch (this)
		{
			case BUILDING:
			{
				MenuBuilder menuBuilder = playerState.getMenuBuilder();
				if (menuBuilder != null) {
					menuBuilder.onTick(ticks);
				}
			}
			break;
		
		default: break;
		}
	}
}
