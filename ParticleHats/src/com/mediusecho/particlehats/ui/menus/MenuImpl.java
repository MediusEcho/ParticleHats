package com.mediusecho.particlehats.ui.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.ui.MenuManager;
import com.mediusecho.particlehats.ui.properties.ItemPointer;
import com.mediusecho.particlehats.ui.properties.MenuClickEvent;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.util.ItemUtil;

public abstract class MenuImpl implements Menu {

	protected final ParticleHats core;
	protected final MenuManager menuManager;
	protected final Player owner;
	
	protected final ItemStack mainMenuButtonItem = ItemUtil.createItem(Material.NETHER_STAR, Message.EDITOR_MISC_MAIN_MENU);
	protected final ItemStack backButtonItem = ItemUtil.createItem(Material.NETHER_STAR, Message.EDITOR_MISC_GO_BACK);
	protected final MenuAction backButtonAction;
	
	protected final static MenuAction emptyAction = (event, slot) -> { return MenuClickResult.NONE; };
	protected Map<Integer, MenuAction> actions;
	
	protected final List<ItemPointer> selectedItems;
	
	public MenuImpl (final ParticleHats core, final MenuManager menuManager, final Player owner)
	{
		this.core = core;
		this.menuManager = menuManager;
		this.owner = owner;
		
		this.actions = new HashMap<Integer, MenuAction>();
		this.selectedItems = new ArrayList<ItemPointer>();
		this.backButtonAction = (event, slot) ->
		{
			menuManager.closeCurrentMenu();
			return MenuClickResult.NEUTRAL;
		};
	}
	
	@Override
	public MenuClickResult onClick(InventoryClickEvent event, int slot, boolean inMenu) 
	{
		if (inMenu) {
			return getAction(slot).onClick(new MenuClickEvent(event), slot);
		}
		return onClickOutside(event, slot);
	}

	@Override
	public MenuClickResult onClickOutside(InventoryClickEvent event, int slot) {
		return MenuClickResult.NONE;
	}
	
	@Override
	public String getName () {
		return "";
	}

	@Override
	public Player getOwner() {
		return owner;
	}

	@Override
	public UUID getOwnerId() {
		return owner.getUniqueId();
	}

	@Override
	public void setAction(int slot, MenuAction action) {
		actions.put(slot, action);
	}

	@Override
	public MenuAction getAction(int slot) {
		return actionExists(slot) ? actions.get(slot) : emptyAction;
	}

	@Override
	public boolean actionExists(int slot) {
		return actions.containsKey(slot);
	}
	
	@Override
	public void selectItem(ItemPointer pointer)
	{
		if (!selectedItems.contains(pointer)) 
		{
			selectedItems.add(pointer);
			onItemSelect(pointer);
		}
	}
	
	@Override
	public void unselectItem(ItemPointer pointer)
	{
		if (selectedItems.contains(pointer))
		{
			selectedItems.remove(pointer);
			onItemUnselect(pointer);
		}
	}

	@Override
	public int getClampedIndex(int normalSlot, int startingIndex, int offset) {
		return Math.max((normalSlot - (((normalSlot / 9) - 1) * offset) - startingIndex), 0);
	}

	@Override
	public int getNormalIndex(int clampedSlot, int startingIndex, int offset) {
		return (clampedSlot + ((clampedSlot / (9 - offset)) * offset) + startingIndex);
	}
	
	/**
	 * Called when an item is selected
	 * @param pointer
	 */
	protected void onItemSelect (ItemPointer pointer) {}
	
	/**
	 * Called when an item is unselected
	 * @param pointer
	 */
	protected void onItemUnselect (ItemPointer pointer) {}
	
	/**
	 * Creates this menus content
	 */
	protected abstract void build ();
}
