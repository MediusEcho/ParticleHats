package com.mediusecho.particlehats.particles.properties.modes;

import org.bukkit.GameMode;
import org.bukkit.Material;

import com.mediusecho.particlehats.particles.properties.Mode;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.player.PlayerState;

public class SurvivalMode implements Mode {

	@Override
	public boolean isValid(EntityState entityState) 
	{
		if (entityState instanceof PlayerState) {
			return ((PlayerState)entityState).getOwner().getGameMode().equals(GameMode.SURVIVAL);
		}
		return false;
	}

	@Override
	public Material getMenuItem() {
		return Material.COOKED_BEEF;
	}

}
