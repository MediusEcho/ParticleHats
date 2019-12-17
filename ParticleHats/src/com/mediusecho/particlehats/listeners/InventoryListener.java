package com.mediusecho.particlehats.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.player.PlayerState;

public class InventoryListener implements Listener {

	private final ParticleHats core;
	
	public InventoryListener (final ParticleHats core)
	{
		this.core = core;
		core.getServer().getPluginManager().registerEvents(this, core);
	}
	
	@EventHandler
	public void onInventoryClick (InventoryClickEvent event)
	{
		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}
		
		ItemStack item = event.getCurrentItem();
		if (item != null && item.getType() != Material.AIR)
		{
			Player player = (Player)event.getWhoClicked();
			PlayerState playerState = core.getPlayerState(player);
			
			if (playerState.hasMenuManager())
			{
				boolean inMenu = event.getRawSlot() < event.getInventory().getSize();
				playerState.getMenuManager().onClick(event, inMenu);
			}
		}
	}
	
	@EventHandler
	public void onInventoryClose (InventoryCloseEvent event)
	{
		if (!(event.getPlayer() instanceof Player)) {
			return;
		}
		
		Player player = (Player)event.getPlayer();
		if (player.hasMetadata("NPC")) {
			return;
		}
		
		PlayerState playerState = core.getPlayerState(player);
		
		if (playerState.hasMenuManager()) {
			playerState.getMenuManager().onInventoryClose(event);
		}
	}
	
	@EventHandler
	public void onInventoryOpen (InventoryOpenEvent event)
	{
		if (!(event.getPlayer() instanceof Player)) {
			return;
		}
		
		Player player = (Player)event.getPlayer();
		PlayerState playerState = core.getPlayerState(player);
		
		if (playerState.hasMenuManager()) {
			playerState.getMenuManager().onInventoryOpen(event);
		}
	}
}
