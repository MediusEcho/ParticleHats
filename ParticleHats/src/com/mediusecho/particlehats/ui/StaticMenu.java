package com.mediusecho.particlehats.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.IconData;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class StaticMenu extends Menu {

	// TODO: Finish Static Menu
	public StaticMenu(Core core, Player owner) 
	{
		super(core, owner);
	}
	
	public StaticMenu(Core core, Player owner, MenuInventory inventory)
	{
		super(core, owner, inventory);
		build();
	}

	@Override
	public void onTick(int ticks) 
	{
		for (Entry<Integer, Hat> set : inventory.getHats().entrySet())
		{
			int slot = set.getKey();
			Hat hat = set.getValue();
			
			if (hat != null && !hat.isLocked())
			{
				IconData iconData = hat.getIconData();
				if (iconData.isLive()) {
					inventory.getItem(slot).setType(iconData.getNextMaterial(ticks));
				}
			}
		}
	}

	@Override
	public void onClick(InventoryClickEvent event) 
	{
		int slot = event.getRawSlot();
		Hat hat = inventory.getHat(slot);
		
		if (hat != null)
		{
			if (!hat.isLoaded()) {
				core.getDatabase().loadHat(getName(), hat.getSlot(), hat);
			}
			
			if (hat.getSound() != null) {
				owner.playSound(owner.getLocation(), hat.getSound(), (float) hat.getSoundVolume(), (float) hat.getSoundPitch()); 
			} else {
				playSound();
			}
			
			if (event.isLeftClick()) {
				hat.getLeftClickAction().onClick(owner, hat, slot, inventory, hat.getLeftClickArgument());
			}
			
			else if (event.isRightClick())
			{
				ParticleAction action = hat.getRightClickAction();
				if (action == ParticleAction.MIMIC) {
					hat.getLeftClickAction().onClick(owner, hat, slot, inventory, hat.getLeftClickArgument());
				} else {
					action.onClick(owner, hat, slot, inventory, hat.getRightClickArgument());
				}
			}
		}
	}

	/**
	 * Checks each hat in this menu and updates any missing information
	 */
	private void build ()
	{
		// Get all equipped hats that belong to this menu
		List<Hat> equippedHats = core.getPlayerState(ownerID).getActiveHats();
		
		Material lockedMaterial = SettingsManager.MENU_LOCKED_ITEM.getMaterial();
		String lockedTitle = StringUtil.colorize(SettingsManager.MENU_LOCKED_ITEM_TITLE.getString());
		
		for (int i = 0; i < inventory.getSize(); i++)
		{
			ItemStack item = inventory.getItem(i);
			Hat hat = inventory.getHat(i);
			
			if (item == null || hat == null) {
				continue;
			}
			
			if (equippedHats.contains(hat)) 
			{
				ItemUtil.highlightItem(item);
				
				ItemMeta itemMeta = item.getItemMeta();
				List<String> lore = itemMeta.getLore();
				
				if (lore == null) {
					lore = new ArrayList<String>();
				}
				
				if (lore.size() > 0) {
					lore.add("");
				}
				
				lore.addAll(StringUtil.parseDescription(Message.HAT_EQUIPPED_DESCRIPTION.getValue()));
				itemMeta.setLore(lore);
				
				item.setItemMeta(itemMeta);
			}
			
			else
			{				
				// Lock items that the player doesn't have permission for
				if (hat.canBeLocked() && hat.isLocked())
				{
					if (SettingsManager.MENU_LOCK_HATS_WITHOUT_PERMISSION.getBoolean())
					{		
						item.setType(lockedMaterial);
						ItemUtil.setNameAndDescription(item, lockedTitle);
					}
				}
			}
		}
	}
}
