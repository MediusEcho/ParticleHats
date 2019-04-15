package com.mediusecho.particlehats.hooks.vanish;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.vanish.event.VanishStatusChangeEvent;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.hooks.VanishHook;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.player.PlayerState;

public class VanishNoPacketHook implements VanishHook, Listener {

	private final Core core;
	
	public VanishNoPacketHook (final Core core)
	{
		this.core = core;
		core.getServer().getPluginManager().registerEvents(this, core);
	}
	
	@Override
	public boolean isVanished(Player player) 
	{
		if (player.hasMetadata("vanished")) {
			return player.getMetadata("vanished").get(0).asBoolean();
		}
		return false;
	}

	@Override
	public void unregister() {
		VanishStatusChangeEvent.getHandlerList().unregister(this);
	}

	@EventHandler
	public void onVanishToggle (VanishStatusChangeEvent event)
	{
		PlayerState playerState = core.getPlayerState(event.getPlayer().getUniqueId());
		boolean vanished = event.isVanishing();
		
		for (Hat hat : playerState.getActiveHats()) {
			hat.setVanished(vanished);
		}
	}
}
