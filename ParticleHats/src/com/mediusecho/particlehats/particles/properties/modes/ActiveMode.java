package com.mediusecho.particlehats.particles.properties.modes;

import org.bukkit.Material;

import com.mediusecho.particlehats.particles.properties.Mode;
import com.mediusecho.particlehats.player.EntityState;

public class ActiveMode implements Mode {

	@Override
	public boolean isValid(EntityState entityState) {
		return true;
	}

	@Override
	public Material getMenuItem() {
		return Material.CYAN_DYE;
	}

}
