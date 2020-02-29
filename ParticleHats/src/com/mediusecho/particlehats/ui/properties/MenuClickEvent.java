package com.mediusecho.particlehats.ui.properties;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Convenient click event with shortcut methods for frequently used checks.
 * @author MediusEcho
 *
 */
public class MenuClickEvent {

	private final InventoryClickEvent event;
	
	private final boolean isMiddleClick;
	private final boolean isShiftLeftClick;
	private final boolean isShiftRightClick;
	
	public MenuClickEvent (final InventoryClickEvent event)
	{
		this.event = event;
		
		isShiftLeftClick  = event.isLeftClick() && event.isShiftClick();
		isShiftRightClick = event.isRightClick() && event.isShiftClick();
		isMiddleClick     = event.getClick().equals(ClickType.MIDDLE);
	}
	
	/**
	 * Get this EditorClickEvent's InventoryClickEvent
	 * @return
	 */
	public InventoryClickEvent getEvent () {
		return event;
	}
	
	/**
	 * Returns true if this event is a left click
	 * @return
	 */
	public boolean isLeftClick () {
		return event.isLeftClick();
	}
	
	/**
	 * Returns true if this event is a right click
	 * @return
	 */
	public boolean isRightClick () {
		return event.isRightClick();
	}
	
	/**
	 * Returns true if this event is a shift click
	 * @return
	 */
	public boolean isShiftClick () {
		return event.isShiftClick();
	}
	
	/**
	 * Returns true if this event is a left shift click
	 * @return
	 */
	public boolean isShiftLeftClick () {
		return isShiftLeftClick;
	}
	
	/**
	 * Returns true if this event is a right shift click
	 * @return
	 */
	public boolean isShiftRightClick () {
		return isShiftRightClick;
	}
	
	/**
	 * Returns true if this event is a middle click
	 * @return
	 */
	public boolean isMiddleClick () {
		return isMiddleClick;
	}
}
