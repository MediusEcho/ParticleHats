package com.mediusecho.particlehats.editor.purchase;

import org.bukkit.entity.Player;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.editor.purchase.menus.PurchaseEditorMainMenu;
import com.mediusecho.particlehats.editor.purchase.menus.PurchaseEditorSettingsMenu;

public class PurchaseMenuManager extends EditorMenuManager {

	public PurchaseMenuManager(ParticleHats core, Player owner) 
	{
		super(core, owner);
	}
	
	@Override
	public void openMainMenu ()
	{
		PurchaseEditorMainMenu purchaseEditorMainMenu = new PurchaseEditorMainMenu(core, this, owner);
		addMenu(purchaseEditorMainMenu);
		purchaseEditorMainMenu.open();
	}
	
	@Override
	public void openSettingsMenu ()
	{
		PurchaseEditorSettingsMenu purchaseEditorSettingsMenu = new PurchaseEditorSettingsMenu(core, this, owner);
		addMenu(purchaseEditorSettingsMenu);
		purchaseEditorSettingsMenu.open();
	}

}
