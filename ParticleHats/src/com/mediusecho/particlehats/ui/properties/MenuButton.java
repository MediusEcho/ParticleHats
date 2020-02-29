package com.mediusecho.particlehats.ui.properties;

import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ui.menus.Menu.MenuAction;

/**
 * Represents a clickable button that can be placed inside a menu
 * @author MediusEcho
 *
 */
public class MenuButton {
	
	private final ItemStack item;
	private final MenuAction action;
	
	public MenuButton (final ItemStack item, final MenuAction action)
	{
		this.item = item;
		this.action = action;
	}
	
	/**
	 * Get the item that represents this button
	 * @return
	 */
	public ItemStack getItem () {
		return item;
	}
	
	/**
	 * Get the action to preform when clicking this button
	 * @return
	 */
	public MenuAction getAction () {
		return action;
	}

}
