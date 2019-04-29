package com.mediusecho.particlehats.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.api.exceptions.InvalidHatException;
import com.mediusecho.particlehats.api.exceptions.InvalidLabelException;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;

public class HatAPI {

	private static Core core = Core.instance;
	
	/**
	 * Checks to see if the given label exists
	 * @param label
	 * @return
	 */
	public static boolean labelExists (String label)
	{
		return core.getDatabase().getLabels(false).contains(label);
	}
	
	/**
	 * Creates a new hat from the given label and equips it
	 * @param player The player this new hat will be given to
	 * @param label The unique label that defines this hat
	 * @param tellPlayer Shows the player the HAT_EQUIPPED message
	 * @param permanent You can decide whether to equip this hat temporarily or permanently
	 */
	public static void equipHatFromLabel (Player player, String label, boolean tellPlayer, boolean permanent) throws InvalidLabelException
	{
		Database database = core.getDatabase();
		Hat hat = database.getHatFromLabel(label);
		
		if (hat == null) {
			throw new InvalidLabelException("Unable to find valid label: " + label);
		}
		
		hat.setPermanent(permanent);
		core.getParticleManager().equipHat(player.getUniqueId(), hat);
		
		if (tellPlayer) {
			player.sendMessage(Message.COMMAND_SET_SUCCESS.getValue().replace("{1}", hat.getDisplayName()));
		}
	}
	
	/**
	 * Creates a new hat from the given label and equips it
	 * @param player The player this new hat will be given to
	 * @param label The unique label that defines this hat
	 * @param tellPlayer Shows the player the HAT_EQUIPPED message
	 * @throws InvalidLabelException
	 */
	public static void equipHatFromLabel (Player player, String label, boolean tellPlayer) throws InvalidLabelException
	{
		equipHatFromLabel(player, label, tellPlayer, true);
	}
	
	/**
	 * Creates a new hat from the given label and equips it
	 * @param player The player this new hat will be given to
	 * @param label The unique label that defines this hat
	 * @throws InvalidLabelException
	 */
	public static void equipHatFromLabel (Player player, String label) throws InvalidLabelException
	{
		equipHatFromLabel(player, label, false, true);
	}
	
	/**
	 * Get how many hats this player is wearing
	 * @param player
	 * @return
	 */
	public static int getHatCount (Player player)
	{
		return core.getPlayerState(player.getUniqueId()).getHatCount();
	}
	
	/**
	 * Get a list of all hats this player is wearing
	 * @param player
	 * @return
	 */
	public List<Hat> getHatsWorn (Player player)
	{
		final List<Hat> hats = new ArrayList<Hat>(core.getPlayerState(player.getUniqueId()).getActiveHats());
		return hats;
	}
	
	/**
	 * Toggles a players active hat
	 * @param player The player we want to toggle hats for
	 * @param index The hat
	 * @param toggleStatus
	 * @throws InvalidHatException
	 */
	public static void toggleHat (Player player, int index, boolean toggleStatus) throws IndexOutOfBoundsException
	{
		Hat hat = core.getPlayerState(player.getUniqueId()).getActiveHats().get(index);
		hat.setHidden(toggleStatus);
	}
	
	/***
	 * Toggles all active hats for the given player
	 * @param player
	 * @param toggleStatus
	 */
	public static void toggleAllHats (Player player, boolean toggleStatus)
	{
		for (Hat hat : core.getPlayerState(player.getUniqueId()).getActiveHats()) {
			hat.setHidden(toggleStatus);
		}
	}
}
