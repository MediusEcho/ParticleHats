package com.mediusecho.particlehats.editor.citizens;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.ui.AbstractStaticMenu;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.ui.MenuManager;
import com.mediusecho.particlehats.util.ItemUtil;

public class CitizensMainMenu extends AbstractStaticMenu {
	
	private final Entity citizenEntity;
	private final MenuButton emptyHatButton = new MenuButton(ItemUtil.createItem(CompatibleMaterial.BARRIER, Message.EDITOR_MISC_EMPTY_MENU), (event, slot) ->
	{
		owner.playSound(owner.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
		return MenuClickResult.NONE;
	});
	
	public CitizensMainMenu (final ParticleHats core, final MenuManager menuManager, final Player owner, final Entity citizenEntity)
	{
		super(core, menuManager, owner);
		
		this.citizenEntity = citizenEntity;
		this.inventory = Bukkit.createInventory(null, 54, "Citizens Manager");
		
		build();
	}

	@Override
	protected void build() 
	{
		EntityState entityState = core.getEntityState(citizenEntity);
		List<Hat> activeHats = entityState.getActiveHats();
		
		setButton(48, ItemUtil.createItem(Material.NETHER_STAR, Message.EDITOR_MISC_CLOSE), (event, slot) -> 
		{
			owner.closeInventory();
			return MenuClickResult.NEUTRAL;
		});
		
		setButton(50, ItemUtil.createItem(CompatibleMaterial.TURTLE_HELMET, "Equip a Hat"), (event, slot) ->
		{
			CitizensMenuSelectionMenu selectionMenu = new CitizensMenuSelectionMenu(core, menuManager, owner, (menuName) ->
			{	
				final MenuInventory inventory = core.getDatabase().loadInventory((String)menuName, menuManager.getOwnerState());
				if (inventory == null) 
				{
					menuManager.closeCurrentMenu();
					return;
				}
				
				menuManager.removeCurrentMenu();
				
				CitizensHatSelectionMenu hatSelectionMenu = new CitizensHatSelectionMenu(core, menuManager, owner, inventory, (hat) ->
				{
					entityState.addHat((Hat)hat);
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
			setButton(22, emptyHatButton);
			return;
		}
		
		final MenuAction editAction = (event, slot) ->
		{				
			int i = getClampedIndex(slot, 10, 2);
			if (i >= activeHats.size()) {
				return MenuClickResult.NONE;
			}
			
			Hat hat = activeHats.get(i);
			if (hat == null) {
				return MenuClickResult.NONE;
			}
			
			
			
			return MenuClickResult.NONE;
		};
		
		int index = 0;
		for (Hat hat : entityState.getActiveHats()) {	
			setButton(getNormalIndex(index++, 10, 2), hat.getItem(), editAction);
		}
	}

	@Override
	protected void onClose(boolean forced) {
		
	}

	@Override
	protected void onTick(int ticks) {
		
	}
	
}
