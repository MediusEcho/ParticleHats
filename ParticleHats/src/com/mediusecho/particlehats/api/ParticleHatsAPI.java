package com.mediusecho.particlehats.api;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.api.exceptions.InvalidHatException;
import com.mediusecho.particlehats.api.exceptions.InvalidLabelException;

public abstract class ParticleHatsAPI {

	protected final ParticleHats core;
	
	public ParticleHatsAPI (final ParticleHats core)
	{
		this.core = core;
	}
	
	/**
	 * Checks to see if the given label exists
	 * 
	 * @param label
	 * @return
	 */
	public abstract boolean labelExists (String label);
	
	/**
	 * Creates a new hat from the given label and equips it
	 * 
	 * @param player The player this new hat will be given to
	 * @param label The unique label that defines this hat
	 * @param tellPlayer Shows the player the HAT_EQUIPPED message
	 * @param permanent You can decide whether to equip this hat temporarily or permanently
	 * @throws InvalidLabelException If label doesn't not exist
	 * @throws IllegalArgumentException If any arguments are invalid
	 */
	public abstract void equipHatFromLabel (Player player, String label, boolean tellPlayer, boolean permanent) throws InvalidLabelException, IllegalArgumentException;
	
	/**
	 * Creates a new hat from the given label and equips it
	 * 
	 * @param player The player this new hat will be given to
	 * @param label The unique label that defines this hat
	 * @param tellPlayer Shows the player the HAT_EQUIPPED message
	 * @throws InvalidLabelException If label doesn't not exist
	 * @throws IllegalArgumentException If any arguments are invalid
	 */
	public abstract void equipHatFromLabel (Player player, String label, boolean tellPlayer) throws InvalidLabelException, IllegalArgumentException;
	
	/**
	 * Creates a new hat from the given label and equips it
	 * 
	 * @param player The player this new hat will be given to
	 * @param label The unique label that defines this hat
	 * @throws InvalidLabelException If label doesn't not exist
	 * @throws IllegalArgumentException If any arguments are invalid
	 */
	public abstract void equipHatFromLabel (Player player, String label) throws InvalidLabelException, IllegalArgumentException;
	
	/**
	 * Get how many hats this player is wearing
	 * 
	 * @param player
	 * @return
	 */
	public abstract int getHatCount (Player player);
	
	/**
	 * Toggles a players active hat
	 * 
	 * @param player The player we want to toggle hats for
	 * @param index The hat
	 * @param toggleStatus
	 * @throws InvalidHatException
	 */
	public abstract void toggleHat (Player player, int index, boolean toggleStatus) throws IndexOutOfBoundsException;
	
	/***
	 * Toggles all active hats for the given player
	 * 
	 * @param player
	 * @param toggleStatus
	 */
	public abstract void toggleAllHats (Player player, boolean toggleStatus);
	
	/**
	 * Creates a new hat from the given label and equips it
	 * 
	 * @param playerID
	 * @param label
	 * @return True if label was found
	 * @deprecated use {@link #equipHatFromLabel(Player, String, boolean)}
	 */
	@Deprecated
	public abstract boolean equipFromLabel (UUID playerID, String label, boolean tellPlayer);
	
	/**
	 * Creates a new hat from the given label and equips it
	 * 
	 * @param playerID
	 * @param label
	 * @return True if label was found
	 * @deprecated use {@link #equipHatFromLabel(Player, String)}
	 */
	@Deprecated
	public abstract boolean equipFromLabel (UUID playerID, String label);
	
	/**
	 * Toggles all players active particles
	 * 
	 * @param playerID
	 * @param vanished
	 * @deprecated use {@link #toggleHat(Player, int, boolean)}
	 */
	@Deprecated
	public abstract void toggleHat (UUID playerID, boolean vanished);
	
	/**
	 * Toggle all players active particles
	 * 
	 * @param playerID
	 * @deprecated use {@link #toggleAllHats(Player, boolean)}
	 */
	@Deprecated
	public abstract void toggleHat (UUID playerID);
	
	/**
	 * Checks to see if this player is wearing a hat
	 * 
	 * @param playerID
	 * @return
	 * @deprecated use {@link #getHatCount(Player)}
	 */
	@Deprecated
	public abstract boolean isWearingHat (UUID playerID);
	
}
