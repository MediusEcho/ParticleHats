package com.mediusecho.particlehats.particles.properties;

import org.bukkit.Material;

import com.mediusecho.particlehats.player.EntityState;

public interface Mode {
	
	/**
	 * Checks to see if this mode is valid for the given PlayerState
	 * @param playerState
	 * @return
	 */
	boolean isValid (EntityState entityState);
	
	Material getMenuItem ();
}
