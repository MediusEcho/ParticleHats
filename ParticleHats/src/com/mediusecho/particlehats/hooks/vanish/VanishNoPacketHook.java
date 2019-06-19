package com.mediusecho.particlehats.hooks.vanish;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.kitteh.vanish.event.VanishStatusChangeEvent;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.hooks.VanishHook;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.player.PlayerState;

public class VanishNoPacketHook implements VanishHook, Listener {

	private final ParticleHats core;
	
	public VanishNoPacketHook (final ParticleHats core)
	{
		this.core = core;
		core.getServer().getPluginManager().registerEvents(this, core);
	}
	
	@Override
	public boolean isVanished(Player player) 
	{
		if (player.hasMetadata("vanished")) 
		{
			try {
				return player.getMetadata("vanished").get(0).asBoolean();
			} catch (IndexOutOfBoundsException e) {}
		}
		return false;
	}

	@Override
	public void unregister() {
		VanishStatusChangeEvent.getHandlerList().unregister(this);
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onVanishToggle (VanishStatusChangeEvent event)
	{
		PlayerState playerState = core.getPlayerState(event.getPlayer());
		boolean vanished = event.isVanishing();
		
		for (Hat hat : playerState.getActiveHats()) {
			hat.setVanished(vanished);
		}
	}
}
