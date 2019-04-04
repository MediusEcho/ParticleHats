package com.mediusecho.particlehats.hooks;

import org.bukkit.entity.Player;

public interface VanishHook {

	/**
	 * Checks to see if this player is currently vanished
	 * @param player
	 * @return
	 */
	public boolean isVanished (Player player);
	
	/**
	 * Unregisters this hook
	 */
	public void unregister ();
}
