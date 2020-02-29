package com.mediusecho.particlehats.ui.properties;

public class ItemPointer {
	
	private final int slot;
	private final int page;
	private final String tag;
	
	public ItemPointer (int slot, int page, String tag)
	{
		this.slot = slot;
		this.page = page;
		this.tag = tag;
	}
	
	public ItemPointer (int slot, int page)
	{
		this(slot, page, "");
	}
	
	public ItemPointer (int slot)
	{
		this(slot, 0, "");
	}
	
	/**
	 * Get the inventory slot this item is in
	 * @return
	 */
	public int getSlot () {
		return slot;
	}
	
	/**
	 * Get the inventory page this item is in.  
	 * Only applies to menus that have more than one inventory to display.
	 * Defaults to 0 if the menu only has one inventory to display.
	 * @return
	 */
	public int getPage () {
		return page;
	}
	
	/**
	 * Additional data that can be applied to this pointer
	 * @return
	 */
	public String getTag () {
		return tag;
	}
	
}
