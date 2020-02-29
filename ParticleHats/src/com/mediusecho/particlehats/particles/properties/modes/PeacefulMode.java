package com.mediusecho.particlehats.particles.properties.modes;

import org.bukkit.Material;

import com.mediusecho.particlehats.particles.properties.Mode;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.player.PlayerState.PVPState;

public class PeacefulMode implements Mode {

	@Override
	public boolean isValid(EntityState entityState) {
		return entityState.getPVPState().equals(PVPState.PEACEFUL);
	}

	@Override
	public Material getMenuItem() {
		return Material.OXEYE_DAISY;
	}

}
