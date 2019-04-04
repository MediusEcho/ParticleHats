package com.mediusecho.particlehats.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.Menu;
import com.mediusecho.particlehats.ui.MenuState;

public class MenuManager {

	private final Core core;
	
	private Map<UUID, Stack<Menu>> menus;
	
	public MenuManager (final Core core)
	{
		this.core = core;
		
		menus = new HashMap<UUID, Stack<Menu>>();
	}
	
	/**
	 * Opens this menu
	 * @param menu
	 */
	public void openMenu (Menu menu) {
		openMenu(menu, false);
	}
	
	/**
	 * Opens this menu
	 * @param menu
	 * @param fromCommand
	 */
	public void openMenu (Menu menu, boolean fromCommand)
	{
		UUID id = menu.getOwnerID();
		
		Stack<Menu> stack = getMenuStack(id);
		if (!stack.contains(menu)) {
			stack.push(menu);
		}
		
		PlayerState playerState = core.getPlayerState(id);
		playerState.setMenuState(fromCommand ? MenuState.OPEN_FROM_COMMAND : MenuState.OPEN);
		
		menu.open();
	}
	
	/**
	 * Removes all cached menus for this player
	 * @param id
	 */
	public void closeAllMenus (UUID id) {
		getMenuStack(id).clear();
	}
	
	/**
	 * Get the most recent menu this player has opened
	 * @param id
	 * @return
	 */
	@Nullable
	public Menu getLastMenu (UUID id) {
		return getMenuStack(id).peek();
	}
	
	/**
	 * Removes the most recent menu this player has opened
	 * @param id
	 */
	public void removeLastMenu (UUID id) {
		getMenuStack(id).pop();
	}
	
	/**
	 * Searches and returns a cached menu matching the menuName<br>
	 * @param id Players UUID
	 * @param menuName Name of the menu
	 * @return NULL if no menu is found
	 */
	@Nullable
	public Menu getMenu (UUID id, String menuName)
	{
		Stack<Menu> stack = getMenuStack(id);
		
		if (stack.empty()) {
			return null;
		}
		
		int index = stack.search(menuName);
		if (index < 0) {
			return null;
		}
		
		return stack.get(index);
	}
	
	/**
	 * Get the ArrayDeque value assigned to this id
	 * @param id
	 * @return
	 */
	private Stack<Menu> getMenuStack (UUID id) 
	{
		if (menus.containsKey(id)) {
			return menus.get(id);
		}
		
		Stack<Menu> stack = new Stack<Menu>();
		menus.put(id, stack);
		
		return stack;
	}
}
