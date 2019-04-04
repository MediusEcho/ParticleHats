package com.mediusecho.particlehats.hooks;

import org.bukkit.entity.Player;

public interface CurrencyHook {

	/**
	 * Gets the players current economy balance
	 * @param id
	 * @return
	 */
	public int getBalance (Player player);
	
	/**
	 * Withdraws the provided amount from the players economy balance
	 * @param id
	 * @param amount
	 * @return
	 */
	public boolean withdraw (Player player, int amount);
	
	/**
	 * Checks to see if this hook has been loaded
	 * @return
	 */
	public boolean isEnabled ();
}
