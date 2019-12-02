package com.mediusecho.particlehats.tasks;

import java.util.Collection;

import org.bukkit.scheduler.BukkitRunnable;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.player.PlayerState;

public class MenuTask extends BukkitRunnable {

	private final ParticleHats core;
	private static int ticks = 0;
	
	public MenuTask (final ParticleHats core)
	{
		this.core = core;
	}
	
	@Override
	public void run() 
	{
		Collection<EntityState> entityStates = core.getEntityStates();
		if (entityStates.size() > 0)
		{
			ticks++;
			for (EntityState entityState : entityStates)
			{
				// Skip entities since they're not using menus
				if (!(entityState instanceof PlayerState)) {
					continue;
				}
				
				PlayerState playerState = (PlayerState)entityState;
				if (!playerState.hasMenuManager()) {
					continue;
				}
				
				playerState.getMenuManager().onTick(ticks);
			}
		}
	}

}
