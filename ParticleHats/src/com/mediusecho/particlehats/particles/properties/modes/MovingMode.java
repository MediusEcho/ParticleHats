package com.mediusecho.particlehats.particles.properties.modes;

import org.bukkit.Material;

import com.mediusecho.particlehats.particles.properties.Mode;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.player.PlayerState.AFKState;

public class MovingMode implements Mode {

	@Override
	public boolean isValid(EntityState entityState) {
		return entityState.getAFKState().equals(AFKState.ACTIVE);
	}

	@Override
	public Material getMenuItem() {
		return Material.IRON_BOOTS;
	}

}
