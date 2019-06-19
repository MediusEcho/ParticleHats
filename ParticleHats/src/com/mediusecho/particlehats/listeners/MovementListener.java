package com.mediusecho.particlehats.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.player.PlayerState.AFKState;

public class MovementListener implements Listener {

	private final ParticleHats core;
	
	public MovementListener (final ParticleHats core)
	{
		this.core = core;
		core.getServer().getPluginManager().registerEvents(this, core);
	}
	
	@EventHandler
	public void onPlayerMove (PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		PlayerState playerState = core.getPlayerState(player);
		
		// We only need to check modes if this player has a hat equipped
		if (playerState.getHatCount() > 0)
		{
			// Set our movement time
			playerState.setLastMoveTime(System.currentTimeMillis());
			
			// Reset our afk state
			if (playerState.getAFKState() == AFKState.AFK)
			{
				final Location location = playerState.getAFKLocation();
				if (location != null)
				{
					final Location toLocation = event.getTo();
					if (toLocation != null)
					{
						// Make sure both locations are in the same world
						if (toLocation.getWorld().equals(location.getWorld()))
						{
							double distance = location.distanceSquared(toLocation);
							if (distance > 6) {
								playerState.setAFKState(AFKState.ACTIVE);
							}
						}
						
						else {
							playerState.setAFKState(AFKState.ACTIVE);
						}
					}
				}
			}
		}
	}
}
