package com.mediusecho.particlehats.particles.properties.modes;

import com.mediusecho.particlehats.particles.properties.Mode;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.player.PlayerState.AFKState;

public class AfkMode implements Mode {

	@Override
	public boolean isValid(EntityState entityState) {
		return entityState.getAFKState().equals(AFKState.AFK);
	}

}