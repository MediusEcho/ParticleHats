package com.mediusecho.particlehats.editor.citizens;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.IconData;
import com.mediusecho.particlehats.particles.properties.IconData.ItemStackTemplate;
import com.mediusecho.particlehats.ui.AbstractStaticMenu;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.ui.MenuManager;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class CitizensHatSelectionMenu extends AbstractStaticMenu {

	private final MenuInventory menuInventory;
	private final MenuAction hatAction;
	
	public CitizensHatSelectionMenu(ParticleHats core, MenuManager menuManager, Player owner, MenuInventory inventory, MenuObjectCallback callback)
	{
		super(core, menuManager, owner);
		
		this.menuInventory = inventory;
		this.inventory = Bukkit.createInventory(null, inventory.getSize(), EditorLore.getTrimmedMenuTitle(menuInventory.getTitle(), Message.NPC_HAT_SELECTION_MENU_TITLE));
		
		hatAction = (event, slot) ->
		{
			if (event.isRightClick())
			{
				menuManager.closeCurrentMenu();
				return MenuClickResult.NEUTRAL;
			}
			
			Hat hat = menuInventory.getHat(slot);
			if (hat == null) {
				return MenuClickResult.NONE;
			}
			
			callback.onSelect(hat);
			return MenuClickResult.NEUTRAL;
		};
		
		build();
	}

	@Override
	protected void build() 
	{		
		final MenuAction cancelAction = (event, slot) ->
		{
			menuManager.closeCurrentMenu();
			return MenuClickResult.NEUTRAL;
		};
		
		final ItemStack emptyItem = ItemUtil.createItem(CompatibleMaterial.LIGHT_GRAY_STAINED_GLASS_PANE, StringUtil.colorize("&cEmpty Slot"), StringUtil.colorize("&cClick to Cancel"));
		
		for (int i = 0; i < menuInventory.getSize(); i++)
		{
			ItemStack item = menuInventory.getItem(i);
			
			if (item == null) 
			{
				setButton(i, emptyItem, cancelAction);
				continue;
			}
			
			Hat hat = menuInventory.getHat(i);
			if (hat == null || !hat.isEquipable()) 
			{
				setButton(i, emptyItem, cancelAction);
				continue;
			}
			
			ItemUtil.setItemDescription(item, Message.NPC_HAT_SELECTION_MENU_DESCRIPTION);
			setButton(i, item, hatAction);
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
			
			if (hat == null) {
				continue;
			}
			
			if (!hat.isEquipable()) {
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

}
