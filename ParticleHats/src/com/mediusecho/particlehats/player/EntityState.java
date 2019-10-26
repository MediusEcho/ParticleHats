package com.mediusecho.particlehats.player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.player.PlayerState.AFKState;
import com.mediusecho.particlehats.player.PlayerState.PVPState;

public class EntityState {

	private final Entity owner;
	private final UUID ownerID;
	
	private AFKState afkState = AFKState.ACTIVE;
	private PVPState pvpState = PVPState.PEACEFUL;
	
	private Location afkLocation;
	
	private long lastMoveTime = 0L;
	private long lastCombatTime = 0L;
	
	protected List<Hat> activeHats;
	
	public EntityState (Entity entity)
	{
		owner = entity;
		ownerID = entity.getUniqueId();
		
		activeHats = new ArrayList<Hat>();
	}
	
	/**
	 * Get the Entity that belongs to this EntityState
	 * @return
	 */
	public Entity getOwner () {
		return owner;
	}
	
	/**
	 * Get the Entity's UUID that belongs to this EntityState
	 * @return
	 */
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
	 * Removes all active hats
	 */
	public void clearActiveHats () {
		activeHats.clear();
	}
	
	/**
	 * Get how many hats this player has equipped
	 * @return
	 */
	public int getHatCount () {
		return activeHats.size();
	}
	
	/**
	 * Adds a hat this this players active hat list
	 * @param hat
	 */
	public void addHat (Hat hat) {
		activeHats.add(hat);
	}
	
	/**
	 * Remove the hat at index
	 * @param index
	 */
	public void removeHat (int index) {
		activeHats.remove(index);
	}
	
	/**
	 * Removes this hat from the players active hats list
	 * @param hat
	 */
	public void removeHat (Hat hat) {
		activeHats.remove(hat);
	}
	
	/**
	 * Removes the oldest equipped hat
	 */
	public void removeLastHat ()
	{
		if (activeHats.size() > 0) {
			activeHats.remove(0);
		}
	}
	
	/**
	 * Checks to see if the player can equip a hat
	 * @return
	 */
	public boolean canEquip () {
		return activeHats.size() < SettingsManager.MAXIMUM_HAT_LIMIT.getInt();
	}
	
	 /**
	  * Checks to see if the player has too many hats equipped
	  * @return
	  */
	public boolean isEquipOverflowed () {
		return activeHats.size() >= SettingsManager.MAXIMUM_HAT_LIMIT.getInt();
	}
	
	/**
	 * Set the entities AFKState
	 * @param state
	 */
	public void setAFKState (AFKState state) {
		afkState = state;
	}
	
	/**
	 * Get the entities AFKState
	 * @return
	 */
	public AFKState getAFKState () {
		return afkState;
	}
	
	/**
	 * Set the entities PVPState
	 * @param state
	 */
	public void setPVPState (PVPState state) {
		pvpState = state;
	}
	
	/**
	 * Get the entities PVPState
	 * @return
	 */
	public PVPState getPVPState () {
		return pvpState;
	}
	
	/**
	 * Set the location this player is afk at
	 * @param location
	 */
	public void setAFKLocation (Location location) {
		afkLocation = location;
	}
	
	/**
	 * Get the location this player went afk at
	 * @return
	 */
	public Location getAFKLocation () {
		return afkLocation;
	}
	
	/**
	 * Set the time this player last moved
	 * @param time
	 */
	public void setLastMoveTime (long time) {
		lastMoveTime = time;
	}
	
	/**
	 * Get the time since this player has moved
	 * @return
	 */
	public long getLastMoveTime () {
		return lastMoveTime;
	}
	
	/**
	 * Set the time this player attacked
	 * @param time
	 */
	public void setLastCombatTime (long time) {
		lastCombatTime = time;
	}
	
	/**
	 * Get the time since this player has attacked
	 * @return
	 */
	public long getLastCombatTime () {
		return lastCombatTime;
	}
}
