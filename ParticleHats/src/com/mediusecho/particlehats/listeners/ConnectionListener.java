package com.mediusecho.particlehats.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.HatReference;
import com.mediusecho.particlehats.player.PlayerState;

public class ConnectionListener implements Listener {

	private final Core core;
	
	public ConnectionListener (final Core core)
	{
		this.core = core;
		core.getServer().getPluginManager().registerEvents(this, core);
	}
	
	@EventHandler
	public void onPlayerJoin (PlayerJoinEvent event)
	{
		UUID id = event.getPlayer().getUniqueId();
		PlayerState playerState = core.getPlayerState(id);
		
		// Load equipped hats
		core.getDatabase().loadPlayerEquippedHats(id, (loadedHats) ->
		{
			if (loadedHats instanceof List)
			{
				@SuppressWarnings("unchecked")
				List<Hat> hats = (ArrayList<Hat>)loadedHats;				
				for (Hat hat : hats) {
					playerState.addHat(hat);
				}
			}
		});
		
		// Load purchased hats
		core.getDatabase().loadPlayerPurchasedHats(id, (purchasedHats) ->
		{
			if (purchasedHats instanceof List)
			{
				@SuppressWarnings("unchecked")
				List<HatReference> hats = (ArrayList<HatReference>)purchasedHats;
				for (HatReference hat : hats) {
					playerState.addPurchasedHat(hat);
				}
			}
		});
	}
	
	@EventHandler
	public void onPlayerQuit (PlayerQuitEvent event)
	{
		UUID id = event.getPlayer().getUniqueId();
		PlayerState playerState = core.getPlayerState(id);
		List<Hat> activeHats = playerState.getActiveHats();
		
		if (!activeHats.isEmpty()) 
		{

			core.getDatabase().savePlayerEquippedHats(id, new ArrayList<Hat>(activeHats));
			playerState.clearActiveHats();
		}
		
		core.removePlayerState(id);
	}
}
