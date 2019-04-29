package com.mediusecho.particlehats.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.util.ItemUtil;

public class ActiveParticlesMenu extends Menu {
	
	private final boolean fromMenu;
	private final Inventory inventory;
	private final List<Hat> activeHats;
	private final PlayerState ownerState;
	
	private Map<Integer, MenuAction> actions;
	
	public ActiveParticlesMenu(Core core, Player owner, boolean fromMenu) 
	{
		super(core, owner);
		
		this.fromMenu = fromMenu;
		ownerState = core.getPlayerState(ownerID);
		activeHats = ownerState.getActiveHats();
		
		actions = new HashMap<Integer, MenuAction>();
		
		inventory = Bukkit.createInventory(null, 54, Message.ACTIVE_PARTICLES_MENU_TITLE.getValue());
		build();
	}
	
	@Override
	public void onTick(int ticks) {
		
	}

	@Override
	public void onClick(InventoryClickEvent event) 
	{
		int slot = event.getRawSlot();
		if (actions.containsKey(slot)) 
		{
			if (actions.get(slot).onClick(event, slot)) {
				playSound();
			}
		}
	}
	
	@Override
	public void open () {
		owner.openInventory(inventory);
	}
	
	private void onDelete (int slot)
	{
		int clampedIndex = getClampedIndex(slot, 10, 2);
		
		inventory.setItem(slot, null);
		activeHats.remove(clampedIndex);
		
		for (int i = clampedIndex + 1; i <= 27; i++)
		{
			int normalIndex = getNormalIndex(i, 10, 2);
			int shiftedIndex = getNormalIndex(i - 1, 10, 2);
			
			ItemStack item = inventory.getItem(normalIndex);
			if (item != null)
			{
				inventory.setItem(normalIndex, null);
				inventory.setItem(shiftedIndex, item);
			}
		}
	}
	
	private void setAction (int slot, MenuAction action) {
		actions.put(slot, action);
	}
	
	/**
	 * Returns an index relative to 0 starting at the startingIndex<br>
	 * eg: (10, 10, 2) -> 0, (11, 10, 2) -> 1, (17, 10, 2) -> 7
	 * @param slot Slot in inventory
	 * @param startingIndex Where to start clamping in the inventory
	 * @param offset How many slots to ignore in each row
	 * @return
	 */
	private int getClampedIndex (int slot, int startingIndex, int offset) {
		return Math.max((slot - (((slot / 9) - 1) * offset) - startingIndex), 0);
	}
	
	/**
	 * Returns an inventory slot relative to 0 starting at startingIndex<br>
	 * eg: (0, 10, 2) -> 10, (7, 10, 2) -> 19
	 * @param slot Clamped index
	 * @param startingIndex Which slot 0 is relative to
	 * @param offset How many slots are ignored in each row
	 * @return
	 */
	private int getNormalIndex (int slot, int startingIndex, int offset) {
		return (slot + ((slot / (9 - offset)) * offset) + startingIndex);
	}
	
	private void build ()
	{	
		if (fromMenu)
		{
			inventory.setItem(49, ItemUtil.createItem(Material.NETHER_STAR, Message.EDITOR_MISC_GO_BACK));
			setAction(49, (event, slot) ->
			{
				PlayerState playerState = core.getPlayerState(ownerID);
				Menu menu = playerState.getPreviousOpenMenu();
				
				playerState.setOpenMenu(menu);
				playerState.setGuiState(GuiState.SWITCHING_MENU);
				
				menu.open();
				return true;
			});
		}
		
		else
		{
			inventory.setItem(49, ItemUtil.createItem(Material.NETHER_STAR, Message.EDITOR_MISC_CLOSE));
			setAction(49, (event, slot) ->
			{
				owner.closeInventory();
				return true;
			});
		}
		
		final MenuAction editAction = (event, slot) ->
		{
			int index = getClampedIndex(slot, 10, 2);
			
			if (index >= activeHats.size()) {
				return false;
			}
			
			Hat hat = activeHats.get(index);
			if (hat != null)
			{
				if (event.isLeftClick())
				{
					boolean hidden = hat.isHidden();
					hat.setHidden(!hidden);
					
					ItemStack item = inventory.getItem(slot);
					EditorLore.updateActiveHatDescription(item, hat);
					
					if (hidden) {
						ItemUtil.highlightItem(item);
					} else {
						ItemUtil.stripHighlight(item);
					}
				}
				
				else if (event.isRightClick() && event.isShiftClick()) {
					onDelete(slot);
				}
			}
			return true;
		};
		
		for (int i = 0; i < 27; i++) {
			setAction(getNormalIndex(i, 10, 2), editAction);
		}
		
		int index = 0;
		for (Hat hat : activeHats)
		{
			ItemStack item = ItemUtil.createItem(hat.getMaterial(), hat.getDisplayName());
			EditorLore.updateActiveHatDescription(item, hat);
			
			if (!hat.isHidden()) {
				ItemUtil.highlightItem(item);
			}
			
			inventory.setItem(getNormalIndex(index++, 10, 2), item);
		}
	}
	
	@FunctionalInterface
	private interface MenuAction {
		public boolean onClick(InventoryClickEvent event, int slot);
	}
}
