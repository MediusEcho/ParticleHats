package com.mediusecho.particlehats.tasks;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.GuiState;

public class MenuTask extends BukkitRunnable {

	private final Core core;
	private static int ticks = 0;
	
	public MenuTask (final Core core)
	{
		this.core = core;
	}
	
	@Override
	public void run() 
	{
		Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
		if (onlinePlayers.size() > 0)
		{
			ticks++;
			for (Player player : onlinePlayers)
			{
				PlayerState playerState = core.getPlayerState(player.getUniqueId());
				GuiState guiState = playerState.getGuiState();
				
				// Skip this player if they don't have a menu open
				if (guiState == GuiState.INNACTIVE) {
					continue;
				}
				
				guiState.onTick(playerState, ticks);
			}
			
			if (ticks < 0) {
				ticks = 0;
			}
		}
	}

}
