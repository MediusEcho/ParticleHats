package com.mediusecho.particlehats.api;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.api.exceptions.InvalidLabelException;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;

public class HatAPI extends ParticleHatsAPI {
	
	public HatAPI (final ParticleHats core)
	{
		super(core);
	}
	
	@Override
	public boolean labelExists (String label) {
		return core.getDatabase().getLabels(false).contains(label);
	}
	
	@Override
	public void equipHatFromLabel (Player player, String label, boolean tellPlayer, boolean permanent) throws InvalidLabelException, IllegalArgumentException
	{
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null!");
		}
		
		if (label == null) {
			throw new InvalidLabelException("label cannot be null!");
		}
		
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
	
	@Override
	public void equipHatFromLabel (Player player, String label, boolean tellPlayer) throws InvalidLabelException, IllegalArgumentException {
		equipHatFromLabel(player, label, tellPlayer, true);
	}
	
	@Override
	public void equipHatFromLabel (Player player, String label) throws InvalidLabelException, IllegalArgumentException {
		equipHatFromLabel(player, label, false, true);
	}
	
	@Override
	public int getHatCount (Player player) {
		return core.getPlayerState(player.getUniqueId()).getHatCount();
	}
	
	@Override
	public void toggleHat (Player player, int index, boolean toggleStatus) throws IndexOutOfBoundsException
	{
		Hat hat = core.getPlayerState(player.getUniqueId()).getActiveHats().get(index);
		hat.setHidden(toggleStatus);
	}
	
	@Override
	public void toggleAllHats (Player player, boolean toggleStatus)
	{
		for (Hat hat : core.getPlayerState(player.getUniqueId()).getActiveHats()) {
			hat.setHidden(toggleStatus);
		}
	}
	
	@Override
	@Deprecated
	public boolean equipFromLabel (UUID playerID, String label, boolean tellPlayer)
	{
		if (playerID == null) {
			return false;
		}
		
		try 
		{
			Player player = Bukkit.getPlayer(playerID);
			equipHatFromLabel(player, label, tellPlayer);
			
			return true;
		} 
		
		catch (Exception e) {
			return false;
		}
	}
	
	@Override
	@Deprecated
	public boolean equipFromLabel (UUID playerID, String label) {
		return equipFromLabel(playerID, label, false);
	}
	
	@Override
	@Deprecated
	public void toggleHat (UUID playerID, boolean vanished) 
	{
		Player player = Bukkit.getPlayer(playerID);
		if (player != null) {
			toggleHat(player, 0, vanished);
		}
	}
	
	@Override
	@Deprecated
	public void toggleHat (UUID playerID)
	{
		Hat hat = core.getPlayerState(playerID).getActiveHats().get(0);
		hat.setHidden(!hat.isHidden());
	}
	
	@Override
	@Deprecated
	public boolean isWearingHat (UUID playerID)
	{
		Player player = Bukkit.getPlayer(playerID);
		if (player != null) {
			return getHatCount(player) > 0;
		}
		return false;
	}
}
