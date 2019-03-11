package com.mediusecho.particlehats.editor.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorDeleteMenu extends EditorMenu {

	public EditorDeleteMenu(Core core, Player owner, MenuBuilder menuBuilder) 
	{
		super(core, owner, menuBuilder);
		
		inventory = Bukkit.createInventory(null, 27, Message.EDITOR_DELETE_MENU_TITLE.getValue());
		build();
	}

	@Override
	protected void build()
	{
		ItemStack yesItem = ItemUtil.createItem(Material.ROSE_RED, Message.EDITOR_DELETE_MENU_YES);
		setButton(12, yesItem, (event, slot) ->
		{
			core.getDatabase().deleteMenu(menuBuilder.getMenuName());
			owner.closeInventory();
			return EditorClickType.NEUTRAL;
		});
		
		ItemStack noItem = ItemUtil.createItem(Material.COAL, Message.EDITOR_DELETE_MENU_NO);
		setButton(14, noItem, (event, slot) ->
		{
			menuBuilder.goBack();
			return EditorClickType.NEUTRAL;
		});
	}

}
