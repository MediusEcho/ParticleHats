package com.mediusecho.particlehats.editor.menus;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorSlotMenu extends EditorMenu {
	
	private final boolean cloning;
	
	private final EditorBaseMenu editorBaseMenu;
	private final int size;
	
	public EditorSlotMenu(ParticleHats core, Player owner, MenuBuilder menuBuilder, boolean cloning) 
	{
		super(core, owner, menuBuilder);
		this.cloning = cloning;
		
		editorBaseMenu = menuBuilder.getEditingMenu();
		size = editorBaseMenu.getInventory().getSize();
		
		inventory = Bukkit.createInventory(null, size, Message.EDITOR_SLOT_MENU_TITlE.getValue());
		build();
	}

	@Override
	protected void build() 
	{
		EditorBaseMenu editorBaseMenu = menuBuilder.getEditingMenu();
		int targetSlot = menuBuilder.getTargetSlot();

		final EditorAction cancelAction = (event, slot) ->
		{
			menuBuilder.goBack();
			return EditorClickType.NEUTRAL;
		};
		
		final EditorAction selectAction = (event, slot) ->
		{
			if (cloning) 
			{
				editorBaseMenu.cloneHat(targetSlot, slot);
				menuBuilder.openEditingMenu();
				return EditorClickType.NEUTRAL;
			}
			
			editorBaseMenu.changeSlots(targetSlot, slot, false);
			menuBuilder.goBack();
			return EditorClickType.NEUTRAL;
		};
		
		final EditorAction swapAction = (event, slot) ->
		{
			editorBaseMenu.changeSlots(targetSlot, slot, true);
			menuBuilder.goBack();
			return EditorClickType.NEUTRAL;
		};
		
		final EditorAction secretAction = (event, slot) ->
		{
			owner.playSound(owner.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1f);
			return EditorClickType.NONE;
		};
		
		for (int i = 0; i < size; i++)
		{
			ItemStack item;
			Hat hat = editorBaseMenu.getHat(i);
			
			if (hat != null) {
				item = ItemUtil.createItem(hat.getMaterial(), 1);
			}
			
			else {
				item = ItemUtil.createItem(CompatibleMaterial.LIGHT_GRAY_STAINED_GLASS_PANE, Message.EDITOR_SLOT_MENU_SELECT);
			}
			
			String displayName = Message.EDITOR_SLOT_MENU_SELECT.getValue();
			if (i  == targetSlot) 
			{
				item.setType(Material.NETHER_STAR);
				displayName = Message.EDITOR_SLOT_MENU_CANCEL.getValue();
				setAction(i, cancelAction);
			}
			
			else if (editorBaseMenu.getHat(i) != null)
			{
				if (!cloning)
				{
					displayName = Message.EDITOR_SLOT_MENU_SWAP.getValue();
					setAction(i, swapAction);
				}
				
				else 
				{
					displayName = Message.EDITOR_SLOT_MENU_OCCUPIED.getValue();
					setAction(i, secretAction);
				}
			}
			
			else {
				setAction(i, selectAction);
			}
			
			ItemUtil.setItemName(item, displayName);
			ItemUtil.setItemDescription(item, Arrays.asList());
			
			inventory.setItem(i, item);
		}
	}

}
