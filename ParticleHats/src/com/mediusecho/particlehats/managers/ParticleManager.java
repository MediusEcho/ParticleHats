package com.mediusecho.particlehats.managers;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.hooks.VanishHook;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.player.PlayerState;

public class ParticleManager 
{
	private final ParticleHats core;
	private final Deque<ParticleEffect> emptyRecents = new ArrayDeque<ParticleEffect>();
	
	private Map<UUID, Deque<ParticleEffect>> recentParticles;
	
	public ParticleManager (ParticleHats core)
	{
		this.core = core;
		
		recentParticles = new HashMap<UUID, Deque<ParticleEffect>>();
	}
	
	/**
	 * Adds this particle to the list of recently used particles
	 * @param id
	 * @param particle
	 */
	public void addParticleToRecents (UUID id, ParticleEffect particle)
	{
		Deque<ParticleEffect> particles = recentParticles.get(id);
		if (particles == null) 
		{
			particles = new ArrayDeque<ParticleEffect>();
			recentParticles.put(id, particles);
		}
		
		// Only add unique particles, no duplicates
		if (!particles.contains(particle)) {
			particles.addFirst(particle);
		}
		
		// we're only showing one menu worth of particles, so limit it to 21 slots
		if (particles.size() > 21) {
			particles.removeLast();
		}
	}
	
	/**
	 * Gets all particles recently used by the player
	 * @param id
	 * @return
	 */
	public Deque<ParticleEffect> getRecentlyUsedParticles (UUID id)
	{
		if (recentParticles.containsKey(id)) {
			return recentParticles.get(id);
		}
		return emptyRecents;
	}
	
	public void equipHat (Player player, Hat hat) {
		equipHat(player, hat, true);
	}
	
	public void equipHat (Player player, Hat hat, boolean showEquipMessage)
	{
		//Player player = Bukkit.getPlayer(id);
		PlayerState playerState = core.getPlayerState(player);
		
		if (playerState.canEquip())
		{
			boolean isVanished = false;
			
			if (SettingsManager.FLAG_VANISH.getBoolean())
			{
				VanishHook vanishHook = core.getHookManager().getVanishHook();
				if (vanishHook != null) {
					isVanished = vanishHook.isVanished(player);
				}
			}
			
			hat.setVanished(isVanished);
			playerState.addHat(hat);
			
			if (showEquipMessage)
			{
				String equipMessage = hat.getEquipDisplayMessage();
				
				if (!equipMessage.equals("")) {
					player.sendMessage(equipMessage);
				} 
				
				else 
				{
					Message defaultMessage = hat.isVanished() ? Message.HAT_EQUIPPED_VANISHED : Message.HAT_EQUIPPED;
					player.sendMessage(defaultMessage.replace("{1}", hat.getDisplayName()));
				}
			}
		}
	}
}
