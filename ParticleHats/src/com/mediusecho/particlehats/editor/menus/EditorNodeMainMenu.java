package com.mediusecho.particlehats.editor.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;

public class EditorNodeMainMenu extends EditorMainMenu {

	public EditorNodeMainMenu(Core core, Player owner, MenuBuilder menuBuilder) 
	{
		super(core, owner, menuBuilder);
		
		particleItemSlot = 13;
		trackingItemSlot = 19;
		countItemSlot    = 15;
		equipItemSlot    = 42;
		
		inventory = Bukkit.createInventory(null, 45, Message.EDITOR_MAIN_MENU_TITLE.getValue());
		build();
	}

	@Override
	protected void build() 
	{
		buildSection();
		
		setButton(38, backButton, backAction);
		
		// TODO: [?] Add option to move to a different hat?
	}
}
