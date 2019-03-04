package com.mediusecho.particlehats.particles;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;

public abstract class Effect {

	public abstract String getName();
	
	public abstract String getDisplayName();
	
	public abstract String getDescription();
	
	public abstract int getParticlesSupported();
	
	public abstract ParticleLocation getDefaultLocation();
	
	public abstract List<ParticleTracking> getSupportedTrackingMethods();
	
	public abstract ParticleTracking getDefaultTrackingMethod();
	
	public abstract boolean supportsAnimation();
	
	public abstract boolean isCustom();
	
	public abstract void build();
	
	/**
	 * Creates and returns an empty List for all particle location data
	 * @return
	 */
	protected List<List<Vector>> createEmptyFrames () {
		return new ArrayList<List<Vector>>();
	}
	
	/**
	 * Display this effect for an entity
	 * @param ticks
	 * @param entity
	 * @param hat
	 */
	public void display (int ticks, Entity entity, Hat hat)
	{
		
	}
	
	public void displayParticle (Location location, Hat hat, int index)
	{
		
	}
}
