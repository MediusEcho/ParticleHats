package com.mediusecho.particlehats.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.IconData;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.particles.properties.IconData.ItemStackTemplate;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class StaticMenu extends AbstractStaticMenu {

	private final MenuInventory menuInventory;
	private final PlayerState ownerState;
	
	private final MenuAction hatAction;
	
	public StaticMenu(ParticleHats core, MenuManager menuManager, Player owner, MenuInventory menuInventory) 
	{
		super(core, menuManager, owner);
		
		this.menuInventory = menuInventory;
		this.ownerState = core.getPlayerState(owner);
		this.inventory = Bukkit.createInventory(null, menuInventory.getSize(), menuInventory.getDisplayTitle());
		
		hatAction = (event, slot) ->
		{
			Hat hat = menuInventory.getHat(slot);
			MenuClickResult result = MenuClickResult.NEUTRAL;
			
			if (hat == null) {
				return MenuClickResult.NONE;
			}
			
			if (!hat.isLoaded()) {
				core.getDatabase().loadHat(getName(), slot, hat);
			}
			
			if (hat.playSound(owner)) {
				result = MenuClickResult.NONE;
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
			
			return result;
		};
		
		build();
	}
	
//	@Override
//	public void open () 
//	{
//		menuManager.isOpeningMenu(this);
//		menuInventory.open(owner);
//	}

	@Override
	protected void build() 
	{
		Material lockedMaterial = SettingsManager.MENU_LOCKED_ITEM.getMaterial();
		int lockedMaterialDurability = SettingsManager.MENU_LOCKED_ITEM_DAMAGE.getInt();
		String lockedTitle = StringUtil.colorize(SettingsManager.MENU_LOCKED_ITEM_TITLE.getString());
		
		List<Hat> equippedHats = core.getPlayerState(owner).getActiveHats();
		
		for (int i = 0; i < inventory.getSize(); i++)
		{
			ItemStack item = menuInventory.getItem(i);
			Hat hat = menuInventory.getHat(i);
			
			if (item == null || hat == null) {
				continue;
			}
			
			// Check for our PURCHASE_ITEM action
			if (hat.getLeftClickAction() == ParticleAction.PURCHASE_ITEM)
			{
				Hat pendingPurchase = ownerState.getPendingPurchase();
				if (pendingPurchase != null) {
					inventory.setItem(i, pendingPurchase.getMenuItem());
				}
				continue;
			}
			
			// Highlight our equipped hats
			if (equippedHats.contains(hat))
			{
				ItemUtil.highlightItem(item);
				
				ItemMeta itemMeta = item.getItemMeta();
				List<String> lore = itemMeta.getLore();
				
				if (lore == null) {
					lore = new ArrayList<String>();
				}
				
				String equippedLore = Message.HAT_EQUIPPED_DESCRIPTION.getValue();
				String[] lineInfo = StringUtil.parseValue(equippedLore, "1");
				
				if (lore.size() > 0) {
					equippedLore = equippedLore.replace(lineInfo[0], lineInfo[1]);
				} else {
					equippedLore = equippedLore.replace(lineInfo[0], "");
				}
				
				lore.addAll(StringUtil.parseDescription(equippedLore));
				itemMeta.setLore(lore);
				
				item.setItemMeta(itemMeta);
			}
			
			// Lock hats that aren't equipped if we can
			else if (hat.canBeLocked() && hat.isLocked())
			{
				if (SettingsManager.MENU_LOCK_HATS_WITHOUT_PERMISSION.getBoolean())
				{
					ItemUtil.setItemType(item, lockedMaterial, lockedMaterialDurability);
					ItemUtil.setItemName(item, lockedTitle);
				}
			}
			
			inventory.setItem(i, item);
		}
		
		for (int i = 0; i < inventory.getSize(); i++) {
			setAction(i, hatAction);
		}
	}

	@Override
	public void onClose(boolean forced) {}

	@Override
	public void onTick(int ticks) 
	{		
		for (Entry<Integer, Hat> set : menuInventory.getHats().entrySet())
		{
			int slot = set.getKey();
			Hat hat = set.getValue();
			
			if (hat == null || hat.isLocked()) {
				continue;
			}
			
			IconData iconData = hat.getIconData();
			if (iconData.isLive()) 
			{
				ItemStackTemplate itemTemplate = iconData.getNextItem(ticks);
				ItemUtil.setItemType(inventory.getItem(slot), itemTemplate.getMaterial(), itemTemplate.getDurability());
			}
		}
	}
	
	@Override
	public String getName () {
		return menuInventory.getName();
	}
	
	/**
	 * Get all hats in this menu
	 * @return
	 */
	public Map<Integer, Hat> getHats () {
		return menuInventory.getHats();
	}

}
