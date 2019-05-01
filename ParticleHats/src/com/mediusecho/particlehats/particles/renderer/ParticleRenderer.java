package com.mediusecho.particlehats.particles.renderer;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;

import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.particles.properties.ParticleData;

public interface ParticleRenderer {

	public void spawnParticle (World world, ParticleEffect particle, Location location, int count, 
			double offsetX, double offsetY, double offsetZ, double extra);
	
	public void spawnParticleBlockData (World world, ParticleEffect particle, Location location, int count, 
			double offsetX, double offsetY, double offsetZ, double extra, ParticleData  data);
	
	public void spawnParticleItemData (World world, ParticleEffect particle, Location location, int count,
			double offsetX, double offsetY, double offsetZ, double extra, ParticleData data);
	
	public void spawnParticleColor (World world, ParticleEffect particle, Location location, int count, 
			double offsetX, double offsetY, double offsetZ, double extra, Color color, double scale);
	
}
