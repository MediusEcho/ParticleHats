package com.mediusecho.particlehats.ui;

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
	
	public int getSlot () {
		return slot;
	}
	
	public int getPage () {
		return page;
	}
	
	public String getTag () {
		return tag;
	}
	
}
