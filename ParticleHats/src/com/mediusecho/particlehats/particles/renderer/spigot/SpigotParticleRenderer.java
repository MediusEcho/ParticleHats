package com.mediusecho.particlehats.particles.renderer.spigot;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.particles.properties.ParticleData;
import com.mediusecho.particlehats.particles.renderer.ParticleRenderer;
import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Particle.DustTransition;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

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

		else if (ParticleHats.serverVersion >= 18 &&
				(particle == ParticleEffect.BARRIER || particle == ParticleEffect.LIGHT))
		{
			ParticleData data = new ParticleData();
			data.setBlock(new ItemStack(particle == ParticleEffect.BARRIER ? Material.BARRIER : Material.LIGHT));
			world.spawnParticle(particleCache.get(ParticleEffect.BLOCK_MARKER), location, count, offsetX, offsetY, offsetZ, extra, data.getBlockMaterial().createBlockData());
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

	@Override
	public void spawnParticleColorTransition(World world, ParticleEffect particle, Location location, int count,
											 double offsetX, double offsetY, double offsetZ, double extra,
											 Color fromColor, Color toColor, double scale)
	{
		if (ParticleHats.serverVersion < 17) {
			return;
		}

		if (particleCache.containsKey(particle))
		{
			DustTransition dustTransition = new DustTransition(fromColor, toColor, (float) scale);
			world.spawnParticle(particleCache.get(particle), location, count, offsetX, offsetY, offsetZ, extra, dustTransition);
		}
	}
}
