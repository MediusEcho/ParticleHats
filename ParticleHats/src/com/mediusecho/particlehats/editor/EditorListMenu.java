package com.mediusecho.particlehats.editor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;

/**
 * Represents a menu that lets a user add or delete elements of a list
 * @author MediusEcho
 *
 */
public abstract class EditorListMenu extends EditorMenu {

	protected EditorAction addAction;
	protected EditorAction editAction;
	
	protected ItemStack addItem;
	
	public EditorListMenu(Core core, Player owner, MenuBuilder menuBuilder) 
	{
		super(core, owner, menuBuilder);
	}
	
	@Override
	protected void build () 
	{
		for (int i = 0; i <= 27; i++) {
			setAction(getNormalIndex(i, 10, 2), editAction);
		}	
	}
	
	/**
	 * Deletes the current slot, and shifts all elements left starting at the slot
	 * @param slot
	 */
	protected void onDelete (int slot)
	{
		int clampedIndex = getClampedIndex(slot, 10, 2);
		
		setItem(slot, null);
		for (int i = clampedIndex + 1; i <= 27; i++)
		{
			int normalIndex = getNormalIndex(i, 10, 2);
			int shiftedIndex = getNormalIndex(i - 1, 10, 2);
			
			if (!itemExists(normalIndex)) {
				break;
			}
			
			// Grab our item and action before setting this slot null
			ItemStack item = getItem(normalIndex);
			
			setItem(normalIndex, null);
			setItem(shiftedIndex, item);
		}
	}
}
