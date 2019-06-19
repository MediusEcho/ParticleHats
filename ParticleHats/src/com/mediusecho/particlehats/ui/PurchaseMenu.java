package com.mediusecho.particlehats.ui;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.util.ItemUtil;

public class PurchaseMenu extends StaticMenu {

	public PurchaseMenu(ParticleHats core, Player owner) 
	{
		super(core, owner);
		
		this.inventory = new MenuInventory("", Message.PURCHASE_MENU_TITLE.getValue(), 5, null);
		
		Hat confirm = new Hat();
		confirm.setLeftClickAction(ParticleAction.PURCHASE_CONFIRM);
		confirm.setLoaded(true);
		inventory.setHat(30, confirm);
		inventory.setItem(30, ItemUtil.createItem(Material.DIAMOND, Message.PURCHASE_MENU_CONFIRM));
		
		Hat cancel = new Hat();
		cancel.setLeftClickAction(ParticleAction.PURCHASE_DENY);
		cancel.setLoaded(true);
		inventory.setHat(32, cancel);
		inventory.setItem(32, ItemUtil.createItem(Material.COAL, Message.PURCHASE_MENU_CANCEL));
		
		Hat pending = core.getPlayerState(owner).getPendingPurchase().clone();
		pending.setLeftClickAction(ParticleAction.PURCHASE_ITEM);
		pending.setLoaded(true);
		inventory.setItem(13, pending.getMenuItem());
	}

}
