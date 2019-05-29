package com.mediusecho.particlehats.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.IconData;
import com.mediusecho.particlehats.particles.properties.IconData.ItemStackTemplate;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class StaticMenu extends Menu {

	private final List<Hat> playerEquippedHats;
	
	public StaticMenu(ParticleHats core, Player owner) 
	{
		super(core, owner);
		playerEquippedHats = new ArrayList<Hat>();
	}
	
	public StaticMenu(ParticleHats core, Player owner, MenuInventory inventory)
	{
		super(core, owner, inventory);
		
		playerEquippedHats = new ArrayList<Hat>();
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
				if (iconData.isLive()) 
				{
					ItemStackTemplate itemTemplate = iconData.getNextItem(ticks);
					ItemUtil.setItemType(inventory.getItem(slot), itemTemplate.getMaterial(), itemTemplate.getDurability());
				}
			}
		}
	}
	
	@Override
	public void open ()
	{
		PlayerState playerState = core.getPlayerState(ownerID);
		List<Hat> equippedHats = playerState.getActiveHats();
		
		if (equippedHats.size() > 0 && playerEquippedHats.size() > 0)
		{
			ListIterator<Hat> iterator = playerEquippedHats.listIterator();
			while (iterator.hasNext())
			{
				Hat hat = iterator.next();
				if (equippedHats.contains(hat)) {
					continue;
				}
				
				ItemStack item = inventory.getItem(hat.getSlot());
				if (item != null)
				{
					iterator.remove();
					ItemUtil.stripHighlight(item);
					ItemUtil.setItemDescription(item, hat.getCachedDescription());
				}
			}
		}
		
		super.open();
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
			
			if (!hat.playSound(owner)) {
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
		PlayerState playerState = core.getPlayerState(ownerID);
		
		// Get all equipped hats that belong to this menu
		List<Hat> equippedHats = playerState.getActiveHats();
		
		Material lockedMaterial = SettingsManager.MENU_LOCKED_ITEM.getMaterial();
		int lockedDurability = SettingsManager.MENU_LOCKED_ITEM_DAMAGE.getInt();
		
		String lockedTitle = StringUtil.colorize(SettingsManager.MENU_LOCKED_ITEM_TITLE.getString());
		
		for (int i = 0; i < inventory.getSize(); i++)
		{
			ItemStack item = inventory.getItem(i);
			Hat hat = inventory.getHat(i);
			
			if (item == null || hat == null) {
				continue;
			}
			
			if (hat.getLeftClickAction() == ParticleAction.PURCHASE_ITEM)
			{
				Hat pendingHat = playerState.getPendingPurchase();
				if (pendingHat != null) {
					inventory.setItem(i, pendingHat.getMenuItem());
				}
				continue;
			}
			
			if (equippedHats.contains(hat)) 
			{
				playerEquippedHats.add(hat);
				
				ItemUtil.highlightItem(item);
				
				ItemMeta itemMeta = item.getItemMeta();
				List<String> lore = itemMeta.getLore();
				
				if (lore == null) {
					lore = new ArrayList<String>();
				}
				
				String equippedDescription = Message.HAT_EQUIPPED_DESCRIPTION.getValue();
				String[] lineInfo = StringUtil.parseValue(equippedDescription, "1");
				
				if (lore.size() > 0) {
					equippedDescription = equippedDescription.replace(lineInfo[0], lineInfo[1]);
				} else {
					equippedDescription = equippedDescription.replace(lineInfo[0], "");
				}
				
				lore.addAll(StringUtil.parseDescription(equippedDescription));
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
						ItemUtil.setItemType(item, lockedMaterial, lockedDurability);
						ItemUtil.setItemName(item, lockedTitle);
					}
				}
			}
		}
	}
}
