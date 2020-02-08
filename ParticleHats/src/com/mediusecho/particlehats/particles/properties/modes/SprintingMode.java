package com.mediusecho.particlehats.particles.properties.modes;

import com.mediusecho.particlehats.particles.properties.Mode;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.player.PlayerState;

public class SprintingMode implements Mode {
	
	@Override
	public boolean isValid(EntityState entityState) 
	{
		if (entityState instanceof PlayerState) {
			return ((PlayerState)entityState).getOwner().isSprinting();
		}
		return false;
	}

	
	
}
