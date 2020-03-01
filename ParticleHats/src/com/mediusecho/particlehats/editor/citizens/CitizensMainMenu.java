package com.mediusecho.particlehats.editor.citizens;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.ui.AbstractListMenu;
import com.mediusecho.particlehats.ui.MenuManager;
import com.mediusecho.particlehats.ui.properties.MenuButton;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.ui.properties.MenuInventory;
import com.mediusecho.particlehats.util.ItemUtil;

public class CitizensMainMenu extends AbstractListMenu {
	
	private final EntityState citizenState;
	
	private final MenuButton emptyHatButton = new MenuButton(ItemUtil.createItem(CompatibleMaterial.BARRIER, Message.NPC_MAIN_MENU_NO_EQUIPPED_HATS), (event, slot) ->
	{
		owner.playSound(owner.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
		return MenuClickResult.NONE;
	});
	
	private final MenuAction editAction;
	
	public CitizensMainMenu (final ParticleHats core, final MenuManager menuManager, final Player owner, final Entity citizenEntity)
	{
		super(core, menuManager, owner, true);
		
		this.citizenState = core.getEntityState(citizenEntity);
		
		this.totalPages = 1;
		this.menus.put(0, Bukkit.createInventory(null, 54, "Citizens Manager"));
		
		List<Hat> activeHats = citizenState.getActiveHats();
		editAction = (event, slot) ->
		{				
			int i = getClampedIndex(slot, 10, 2);
			if (i >= activeHats.size()) {
				return MenuClickResult.NONE;
			}
			
			Hat hat = activeHats.get(i);
			if (hat == null) {
				return MenuClickResult.NONE;
			}
			
			if (event.isLeftClick())
			{
				hat.setHidden(!hat.isHidden());
				
				ItemStack item = menus.get(currentPage).getItem(slot);
				EditorLore.updateActiveHatDescription(item, hat);
				
				if (hat.isHidden()) {
					ItemUtil.stripHighlight(item);
				} else {
					ItemUtil.highlightItem(item);
				}
			}
			
			else if (event.isShiftRightClick()) 
			{
				citizenState.removeHat(getClampedIndex(slot, 10, 2));
				deleteSlot(currentPage, slot);
			}
			
			return MenuClickResult.NEUTRAL;
		};
		
		build();
	}
	
	@Override
	public void insertEmptyItem () {
		setButton(0, 22, emptyHatButton);
	}
	
	@Override
	public void removeEmptyItem () {
		setButton(0, 22, null, emptyAction);
	}

	@Override
	protected void build() 
	{
		List<Hat> activeHats = citizenState.getActiveHats();
		
		setButton(0, 49, ItemUtil.createItem(Material.NETHER_STAR, Message.EDITOR_MISC_CLOSE), (event, slot) -> 
		{
			owner.closeInventory();
			return MenuClickResult.NEUTRAL;
		});
		
		setButton(0, 47, ItemUtil.createItem(CompatibleMaterial.FIRE_CHARGE, Message.NPC_MAIN_MENU_CLEAR_EQUIPPED_HATS), (event, slot) ->
		{
			citizenState.clearActiveHats();
			clearContent();
			setEmpty(true);
			
			return MenuClickResult.NEUTRAL;
		});
		
		setButton(0, 51, ItemUtil.createItem(CompatibleMaterial.TURTLE_HELMET, Message.NPC_MAIN_MENU_EQUIP_HAT), (event, slot) ->
		{
			CitizensMenuSelectionMenu selectionMenu = new CitizensMenuSelectionMenu(core, menuManager, owner, (menuName) ->
			{	
				final MenuInventory inventory = core.getDatabase().loadInventory((String)menuName, menuManager.getOwnerState());
				if (inventory == null) 
				{
					// Back out to the previous menu
					menuManager.closeCurrentMenu();
					return;
				}
				
				// Remove the selection menu before opening the hat selection menu
				menuManager.removeCurrentMenu();
				
				CitizensHatSelectionMenu hatSelectionMenu = new CitizensHatSelectionMenu(core, menuManager, owner, inventory, (hat) ->
				{
					citizenState.addHat((Hat)hat);
					refresh();
					menuManager.closeCurrentMenu();
				});
				
				menuManager.addMenu(hatSelectionMenu);
				hatSelectionMenu.open();
			});
			
			menuManager.addMenu(selectionMenu);
			selectionMenu.open();
			
			return MenuClickResult.NEUTRAL;
		});
		
		if (activeHats.size() == 0) 
		{
			setButton(0, 22, emptyHatButton);
			isEmpty = true;
			return;
		}
		
		refresh();
	}
	
	private void refresh ()
	{		
		if (isEmpty && citizenState.getHatCount() > 0) {
			setEmpty(false);
		}
		
		int index = 0;
		for (Hat hat : citizenState.getActiveHats())
		{
			int i = getNormalIndex(index++, 10, 2);
			
			ItemStack item = hat.getItem();
			EditorLore.updateActiveHatDescription(item, hat);
			
			if (hat.isHidden()) {
				ItemUtil.stripHighlight(item);
			} else {
				ItemUtil.highlightItem(item);
			}
			
			setButton(0, i, item, editAction);
		}
	}
	
}
