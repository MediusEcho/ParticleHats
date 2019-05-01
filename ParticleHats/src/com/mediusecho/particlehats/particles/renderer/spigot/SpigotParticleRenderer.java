package com.mediusecho.particlehats.particles.renderer.spigot;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;

import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.particles.properties.ParticleData;
import com.mediusecho.particlehats.particles.renderer.ParticleRenderer;

public class SpigotParticleRenderer implements ParticleRenderer {

	// Keep a cache of Particles since our ParticleEffect values don't change
	private Map<ParticleEffect, Particle> particleCache;
	
	public SpigotParticleRenderer ()
	{
		particleCache = new HashMap<ParticleEffect, Particle>();
		for (ParticleEffect pe : ParticleEffect.values()) 
		{
			try {
				particleCache.put(pe, Particle.valueOf(pe.toString()));
			} catch (IllegalArgumentException e) {}
		}
	}
	
	@Override
	public void spawnParticle (World world, ParticleEffect particle, Location location, int count, 
			double offsetX, double offsetY, double offsetZ, double extra) 
	{
		if (particleCache.containsKey(particle)) {
			world.spawnParticle(particleCache.get(particle), location, count, offsetX, offsetY, offsetZ, extra);
		}
	}
	
	@Override
	public void spawnParticleBlockData (World world, ParticleEffect particle, Location location, int count, 
			double offsetX, double offsetY, double offsetZ, double extra, ParticleData  data) 
	{
		if (particleCache.containsKey(particle)) {
			world.spawnParticle(particleCache.get(particle), location, count, offsetX, offsetY, offsetZ, extra, data.getBlockMaterial().createBlockData());
		}
	}
	
	@Override
	public void spawnParticleItemData (World world, ParticleEffect particle, Location location, int count,
			double offsetX, double offsetY, double offsetZ, double extra, ParticleData data) 
	{
		if (particleCache.containsKey(particle)) {
			world.spawnParticle(particleCache.get(particle), location, count, offsetX, offsetY, offsetZ, extra, data.getItem());
		}
	}
	
	@Override
	public void spawnParticleColor (World world, ParticleEffect particle, Location location, int count, 
			double offsetX, double offsetY, double offsetZ, double extra, Color color, double scale) 
	{
		if (particleCache.containsKey(particle)) 
		{
			DustOptions dustOptions = new DustOptions(color, (float) scale);
			world.spawnParticle(particleCache.get(particle), location, count, offsetX, offsetY, offsetZ, extra, dustOptions);
		}
	}
}
