package com.mediusecho.particlehats.player;

import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.ui.MenuState;

public class PlayerState {
	
	private MenuBuilder menuBuilder;
	private MenuState menuState;
	private MenuState previousMenuState;

	/**
	 * Set this players menu builder class
	 * @param menuBuidler
	 */
	public void setMenuBuilder (MenuBuilder menuBuilder) {
		this.menuBuilder = menuBuilder;
	}
	
	/**
	 * Returns this players menu builder class
	 * @return
	 */
	public MenuBuilder getMenuBuilder () {
		return menuBuilder;
	}
	
	/**
	 * Sets this players menu state class
	 * @param menuState
	 */
	public void setMenuState (MenuState menuState) 
	{
		this.previousMenuState = this.menuState;
		this.menuState = menuState;
	}
	
	/**
	 * Returns this players menu state
	 * @return
	 */
	public MenuState getMenuState () {
		return menuState != null ? menuState : MenuState.CLOSED;
	}
	
	/**
	 * Returns this players previous menu state
	 * @return
	 */
	public MenuState getPreviousMenuState () {
		return previousMenuState != null ? previousMenuState : MenuState.CLOSED;
	}
}
