package com.mediusecho.particlehats.managers;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.particles.ParticleEffect;

public class ParticleManager 
{
	private final Core core;
	private final Deque<ParticleEffect> emptyRecents = new ArrayDeque<ParticleEffect>();
	
	private Map<UUID, Deque<ParticleEffect>> recentParticles;
	
	public ParticleManager (Core core)
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
}
