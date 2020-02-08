package com.mediusecho.particlehats.particles.properties.modes;

import com.mediusecho.particlehats.particles.properties.Mode;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.player.PlayerState;

public class FlyingMode implements Mode {

	@Override
	public boolean isValid(EntityState entityState) 
	{
		if (entityState instanceof PlayerState) {
			return ((PlayerState)entityState).getOwner().isFlying();
		}
		return false;
	}

}
