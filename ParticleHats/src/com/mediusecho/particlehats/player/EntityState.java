package com.mediusecho.particlehats.player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Entity;

import com.mediusecho.particlehats.particles.Hat;

public class EntityState {

	private final Entity owner;
	private final UUID ownerID;
	
	private List<Hat> activeHats;
	
	public EntityState (Entity entity)
	{
		owner = entity;
		ownerID = entity.getUniqueId();
		
		activeHats = new ArrayList<Hat>();
	}
	
	public Entity getOwner () {
		return owner;
	}
	
	public UUID getOwnerID () {
		return ownerID;
	}
	
	/**
	 * Gets all active hats
	 * @return
	 */
	public List<Hat> getActiveHats () {
		return activeHats;
	}
	
	/**
	 * Adds a hat this this players active hat list
	 * @param hat
	 */
	public void addHat (Hat hat) {
		activeHats.add(hat);
	}
}
