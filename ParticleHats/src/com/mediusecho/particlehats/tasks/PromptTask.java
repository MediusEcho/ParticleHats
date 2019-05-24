package com.mediusecho.particlehats.tasks;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.prompt.Prompt;

public class PromptTask extends BukkitRunnable {

	private final ParticleHats core;
	private int passes = 0;
	
	public PromptTask (final ParticleHats core)
	{
		this.core = core;
	}
	
	@Override
	public void run() 
	{
		Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
		if (onlinePlayers.size() > 0)
		{
			passes++;
			for (Player player : onlinePlayers)
			{
				PlayerState playerState = core.getPlayerState(player.getUniqueId());
				MetaState metaState = playerState.getMetaState();
				
				if (metaState == MetaState.NONE) {
					continue;
				}
				
				Prompt prompt = core.getPrompt();
				if (prompt.canPrompt(passes)) {
					prompt.prompt(player, metaState);
				}
			}
		}
	}
}
