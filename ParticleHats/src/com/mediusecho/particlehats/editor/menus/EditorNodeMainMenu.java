package com.mediusecho.particlehats.editor.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.locale.Message;

public class EditorNodeMainMenu extends EditorMainMenu {

	public EditorNodeMainMenu(ParticleHats core, EditorMenuManager menuManager, Player owner) 
	{
		super(core, menuManager, owner);
	}
	
	@Override
	public void build ()
	{
		this.inventory = Bukkit.createInventory(null, 45, Message.EDITOR_MAIN_MENU_TITLE.getValue());
		this.particleButtonSlot = 13;
		this.trackingButtonSlot = 19;
		this.countButtonSlot = 15;
		this.equipButtonSlot = 42;
		
		buildSection();
		
		setButton(38, backButtonItem, backButtonAction);
	}

}
