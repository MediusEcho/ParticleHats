package com.mediusecho.particlehats.editor.purchase;

import org.bukkit.entity.Player;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.editor.purchase.menus.EditorPurchaseMainMenu;
import com.mediusecho.particlehats.editor.purchase.menus.EditorPurchaseSettingsMenu;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.MenuInventory;

public class PurchaseMenuBuilder extends MenuBuilder {

	public PurchaseMenuBuilder(ParticleHats core, Player owner, PlayerState ownerState, MenuInventory inventory) 
	{
		super(core, owner, ownerState, inventory);
	}
	
	@Override
	public void onClose ()
	{
		
	}
	
	@Override
	public void openMainMenu (Player owner)
	{
		EditorPurchaseMainMenu editorPurchaseMainMenu = new EditorPurchaseMainMenu(core, owner, this);
		addMenu(editorPurchaseMainMenu);
		editorPurchaseMainMenu.open();
	}
	
	@Override
	public void openSettingsMenu (Player owner)
	{
		EditorPurchaseSettingsMenu editorPurchaseSettingsMenu = new EditorPurchaseSettingsMenu(core, owner, this);
		addMenu(editorPurchaseSettingsMenu);
		editorPurchaseSettingsMenu.open();
	}
}
