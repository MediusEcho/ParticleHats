package com.mediusecho.particlehats.player;

import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.ui.MenuState;

public class PlayerState {
	
	private MenuBuilder menuBuilder;
	private MenuState menuState         = MenuState.CLOSED;
	private MenuState previousMenuState = MenuState.CLOSED;
	
	private int metaDescriptionLine = 0;
	
	private MetaState metaState = MetaState.NONE;
	private int metaStateTime = 15;

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
		return menuState;
	}
	
	/**
	 * Returns this players previous menu state
	 * @return
	 */
	public MenuState getPreviousMenuState () {
		return previousMenuState;
	}
	
	/**
	 * Set which description line is being edited
	 * @param line
	 */
	public void setMetaDescriptionLine (int line) {
		this.metaDescriptionLine = line;
	}
	
	/**
	 * Get which description line is being edited
	 * @return
	 */
	public int getMetaDescriptionLine () {
		return metaDescriptionLine;
	}
	
	/**
	 * Set this players MetaState
	 * @param metaState
	 */
	public void setMetaState (MetaState metaState) 
	{
		this.metaState = metaState;
		metaStateTime = SettingsManager.EDITOR_META_TIME_LIMIT.getInt();
	}
	
	/**
	 * Get this players MetaState
	 * @return
	 */
	public MetaState getMetaState () {
		return metaState;
	}
	
	/**
	 * Get the current time left for the MetaState
	 * @return
	 */
	public int getMetaStateTime () {
		return metaStateTime--;
	}
}
