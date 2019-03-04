package com.mediusecho.particlehats.editor.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorSettingsMenu extends EditorMenu {

	public EditorSettingsMenu(Core core, Player owner, MenuBuilder menuBuilder) 
	{
		super(core, owner, menuBuilder);
		
		inventory = Bukkit.createInventory(null, 54, Message.EDITOR_SETTINGS_MENU_TITLE.getValue());
		build();
	}
	
	@Override
	public void open ()
	{
		String titleDescription = Message.EDITOR_SETTINGS_MENU_TITLE_DESCRIPTION.getValue();
		String title = titleDescription.replace("{1}", menuBuilder.getEditingMenu().getTitle());
		
		ItemStack titleItem = getItem(10);
		if (titleItem != null) {
			ItemUtil.setItemDescription(titleItem, StringUtil.parseDescription(title));
		}
		
		super.open();
	}

	@Override
	protected void build() 
	{
		// Back
		setButton(49, backButton, backAction);
		
		// Set Title
		ItemStack titleItem = ItemUtil.createItem(Material.SIGN, Message.EDITOR_SETTINGS_MENU_SET_TITLE);
		setButton(10, titleItem, (event, slot) ->
		{
			menuBuilder.setOwnerState(MetaState.MENU_TITLE);
			owner.closeInventory();
			return EditorClickType.NEUTRAL;
		});
		
		// Set Size
		ItemStack sizeItem = ItemUtil.createItem(Material.COMPARATOR, Message.EDITOR_SETTINGS_MENU_SET_SIZE);
		setButton(12, sizeItem, (event, slot) ->
		{
			EditorResizeMenu editorResizeMenu = new EditorResizeMenu(core, owner, menuBuilder);
			menuBuilder.addMenu(editorResizeMenu);
			editorResizeMenu.open();
			return EditorClickType.NEUTRAL;
		});
		
		// Set Purchase Menu
		ItemStack purchaseItem = ItemUtil.createItem(Material.GOLD_NUGGET, Message.EDITOR_SETTINGS_MENU_SET_PURCHASE_MENU);
		setButton(14, purchaseItem, (event, slot) ->
		{
			return EditorClickType.NEUTRAL;
		});
		
		// Delete
		ItemStack deleteItem = ItemUtil.createItem(Material.TNT, Message.EDITOR_SETTINGS_MENU_DELETE);
		setButton(16, deleteItem, (event, slot) ->
		{
			return EditorClickType.NEUTRAL;
		});
		
		// Toggle Live Updates
		ItemStack liveItem = ItemUtil.createItem(Material.LEVER, Message.EDITOR_SETTINGS_MENU_TOGGLE_LIVE_MENU);
		setButton(30, liveItem, (event, slot) ->
		{
			return EditorClickType.NEUTRAL;
		});
		
		// Sync Icons
		ItemStack syncItem = ItemUtil.createItem(Material.CONDUIT, Message.EDITOR_SETTINGS_MENU_SYNC_ICONS);
		setButton(32, syncItem, (event, slot) ->
		{
			return EditorClickType.NEUTRAL;
		});
	}

}
