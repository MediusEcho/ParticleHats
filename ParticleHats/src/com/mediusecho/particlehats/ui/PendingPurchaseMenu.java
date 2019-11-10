package com.mediusecho.particlehats.ui;

import org.bukkit.Material;

import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.util.ItemUtil;

public class PendingPurchaseMenu {

	public static MenuInventory defaultPendingPurchaseInventory;
	
	static 
	{
		defaultPendingPurchaseInventory = new MenuInventory("", Message.PURCHASE_MENU_TITLE.getValue(), 5, null);
		
		Hat confirm = new Hat();
		confirm.setLeftClickAction(ParticleAction.PURCHASE_CONFIRM);
		confirm.setLoaded(true);
		defaultPendingPurchaseInventory.setHat(30, confirm);
		defaultPendingPurchaseInventory.setItem(30, ItemUtil.createItem(Material.DIAMOND, Message.PURCHASE_MENU_CONFIRM));
		
		Hat cancel = new Hat();
		cancel.setLeftClickAction(ParticleAction.PURCHASE_DENY);
		cancel.setLoaded(true);
		defaultPendingPurchaseInventory.setHat(32, cancel);
		defaultPendingPurchaseInventory.setItem(32, ItemUtil.createItem(Material.COAL, Message.PURCHASE_MENU_CANCEL));
		
		Hat pending = new Hat();
		pending.setLeftClickAction(ParticleAction.PURCHASE_ITEM);
		pending.setLoaded(true);
		defaultPendingPurchaseInventory.setItem(13, pending.getMenuItem());
	}

}
