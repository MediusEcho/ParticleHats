package com.mediusecho.particlehats.editor.purchase.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.database.Database.DataType;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.editor.menus.EditorActionMenu;
import com.mediusecho.particlehats.editor.menus.EditorDescriptionMenu;
import com.mediusecho.particlehats.editor.menus.EditorIconOverviewMenu;
import com.mediusecho.particlehats.editor.menus.EditorSlotMenu;
import com.mediusecho.particlehats.editor.menus.EditorSoundMenu;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorPurchaseMainMenu extends EditorMenu {

	private Hat targetHat;
	
	public EditorPurchaseMainMenu(ParticleHats core, Player owner, MenuBuilder menuBuilder) 
	{
		super(core, owner, menuBuilder);
		
		targetHat = menuBuilder.getBaseHat();
		
		inventory = Bukkit.createInventory(null, 36, Message.EDITOR_MAIN_MENU_TITLE.getValue());
		build();
	}
	
	@Override
	public void open ()
	{	
		EditorLore.updateNameDescription(getItem(13), targetHat);	
		EditorLore.updateDescriptionDescription(getItem(15), targetHat.getDescription());
		
		super.open();
	}

	@Override
	protected void build() 
	{
		setButton(31, backButton, backAction);
	
		// Action
		ItemStack actionItem = ItemUtil.createItem(CompatibleMaterial.GUNPOWDER, Message.EDITOR_MAIN_MENU_SET_ACTION);
		EditorLore.updateSpecificActionDescription(actionItem, targetHat, targetHat.getLeftClickAction(), targetHat.getLeftClickArgument());
		setButton(11, actionItem, (event, slot) ->
		{
			EditorActionMenu editorActionMenu = new EditorActionMenu(core, owner, menuBuilder, true, true, (action) ->
			{
				targetHat.setLeftClickAction(action);
				EditorLore.updateSpecificActionDescription(getItem(11), targetHat, targetHat.getLeftClickAction(), targetHat.getLeftClickArgument());
				menuBuilder.goBack();
			});
			
			menuBuilder.addMenu(editorActionMenu);
			editorActionMenu.open();
			return EditorClickType.NEUTRAL;
		});
		
		// Name
		ItemStack nameItem = ItemUtil.createItem(CompatibleMaterial.PLAYER_HEAD, Message.EDITOR_META_MENU_SET_NAME);
		setButton(13, nameItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				menuBuilder.setOwnerState(MetaState.HAT_NAME);
				core.prompt(owner, MetaState.HAT_NAME);
				owner.closeInventory();
			}
			
			else if (event.isRightClick())
			{
				targetHat.setName(Message.EDITOR_MISC_NEW_PARTICLE.getRawValue());
				EditorLore.updateNameDescription(getItem(13), targetHat);
			}
			
			return EditorClickType.NEUTRAL;
		});
		
		// Description
		ItemStack descriptionItem = ItemUtil.createItem(CompatibleMaterial.WRITABLE_BOOK, Message.EDITOR_META_MENU_SET_DESCRIPTION);
		setButton(15, descriptionItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorDescriptionMenu editorDescriptionMenu = new EditorDescriptionMenu(core, owner, menuBuilder, true);
				menuBuilder.addMenu(editorDescriptionMenu);
				editorDescriptionMenu.open();
			}
			
			else if (event.isShiftRightClick())
			{
				if (!targetHat.getDescription().isEmpty())
				{
					targetHat.getDescription().clear();
					
					Database database = core.getDatabase();
					String menuName = menuBuilder.getEditingMenu().getName();
					database.saveMetaData(menuName, targetHat, DataType.DESCRIPTION, 0);
					
					EditorLore.updateDescriptionDescription(getItem(15), targetHat.getDescription());
				}
			}
			return EditorClickType.NEUTRAL;
		});
		
		// Slot
		ItemStack slotItem = ItemUtil.createItem(Material.ITEM_FRAME, Message.EDITOR_MAIN_MENU_SET_SLOT, Message.EDITOR_MAIN_MENU_SLOT_DESCRIPTION);
		setButton(19, slotItem, (event, slot) ->
		{
			EditorSlotMenu editorSlotMenu = new EditorSlotMenu(core, owner, menuBuilder, false);
			menuBuilder.addMenu(editorSlotMenu);
			editorSlotMenu.open();
			return EditorClickType.NEUTRAL;
		});
		
		// Clone
		ItemStack cloneItem = ItemUtil.createItem(CompatibleMaterial.PRISMARINE_SHARD, Message.EDITOR_MAIN_MENU_CLONE, Message.EDITOR_MAIN_MENU_CLONE_DESCRIPTION);
		setButton(21, cloneItem, (event, slot) ->
		{
			EditorSlotMenu editorSlotMenu = new EditorSlotMenu(core, owner, menuBuilder, true);
			menuBuilder.addMenu(editorSlotMenu);
			editorSlotMenu.open();
			return EditorClickType.NEUTRAL;
		});
		
		// Sound
		ItemStack soundItem = ItemUtil.createItem(CompatibleMaterial.MUSIC_DISC_STRAD, Message.EDITOR_MAIN_MENU_SET_SOUND);
		EditorLore.updateSoundItemDescription(soundItem, targetHat);
		setButton(23, soundItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorSoundMenu editorSoundMenu = new EditorSoundMenu(core, owner, menuBuilder, (sound) ->
				{
					targetHat.setSound(sound);
					EditorLore.updateSoundItemDescription(getItem(23), targetHat);
					menuBuilder.goBack();
				});
				menuBuilder.addMenu(editorSoundMenu);
				editorSoundMenu.open();
			}
			
			else if (event.isShiftRightClick()) 
			{
				targetHat.removeSound();
				EditorLore.updateSoundItemDescription(getItem(23), targetHat);
			}
			return EditorClickType.NEUTRAL;
		});
		
		// Icon
		ItemStack iconItem = targetHat.getItem();//ItemUtil.createItem(targetHat.getMaterial(), Message.EDITOR_MAIN_MENU_SET_ICON, Message.EDITOR_MAIN_MENU_ICON_DESCRIPTION);
		ItemUtil.setNameAndDescription(iconItem, Message.EDITOR_MAIN_MENU_SET_ICON, Message.EDITOR_MAIN_MENU_ICON_DESCRIPTION);
		setButton(25, iconItem, (event, slot) ->
		{
			EditorIconOverviewMenu editorIconOverviewMenu = new EditorIconOverviewMenu(core, owner, menuBuilder, (item) ->
			{
				ItemUtil.setItemType(getItem(25), item);
			});
			menuBuilder.addMenu(editorIconOverviewMenu);
			editorIconOverviewMenu.open();
			return EditorClickType.NEUTRAL;
		});
	}

}
