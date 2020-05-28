package com.mediusecho.particlehats.tasks;

import java.util.Collection;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.player.EntityState;
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
		Collection<EntityState> entityStates = core.getEntityStates();
		if (entityStates.size() > 0)
		{
			passes++;
			for (EntityState entityState : entityStates)
			{
				Entity entity = entityState.getOwner();
				if (!(entity instanceof Player)) {
					continue;
				}
				
				if (!(entityState instanceof PlayerState)) {
					continue;
				}
				
				PlayerState playerState = (PlayerState)entityState;
				MetaState metaState = playerState.getMetaState();
				
				if (metaState == MetaState.NONE) {
					continue;
				}
				
				int time = playerState.getMetaStateTime();
				if (time <= 0)
				{
					if (playerState.hasMenuManager()) {
						((EditorMenuManager)playerState.getMenuManager()).reopen();
					} else {
						playerState.setMetaState(MetaState.NONE);
					}
					continue;
				}
				
				Prompt prompt = core.getPrompt();
				if (prompt.canPrompt(passes)) {
					prompt.prompt((Player)entity, metaState);
				}
			}
		}
	}
}
