package com.mediusecho.particlehats.player;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.player.PlayerState.AFKState;
import com.mediusecho.particlehats.player.PlayerState.PVPState;
import com.mediusecho.particlehats.tasks.HatTask;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EntityState {

	private final Entity owner;
	private final UUID ownerID;
	private final int id;
	
	private AFKState afkState = AFKState.ACTIVE;
	private PVPState pvpState = PVPState.PEACEFUL;
	
	private Location afkLocation;
	private Location lastLocation;
	
	private long lastMoveTime = 0L;
	private long lastCombatTime = 0L;

	protected List<HatTask> activeHats;

	public EntityState (@NotNull Entity entity, int id)
	{
		owner = entity;
		ownerID = entity.getUniqueId();
		this.id = id;

		activeHats = new ArrayList<>();
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
	
	public int getID () {
		return id;
	}
	
	/**
	 * Gets all active hats
	 * @return
	 */
	public List<Hat> getActiveHats () {
		return activeHats.stream().map(HatTask::getHat).collect(Collectors.toList());
	}

	public void reload () {
		activeHats.forEach(HatTask::reload);
	}

	/**
	 * Removes all active hats
	 */
	public void clearActiveHats () 
	{
		Player player = null;
		if (owner instanceof Player) {
			player = (Player)owner;
		}

		for (HatTask hatTask : activeHats)
		{
			if (player != null) {
				hatTask.getHat().unequip(player);
			}
			hatTask.stop();
		}

		activeHats.clear();
	}

	/**
	 * Checks to see if this hat is already equipped.
	 *
	 * @return
	 * 		True if the hat is equipped.
	 */
	public boolean isEquipped (Hat hat) {
		return activeHats.stream().anyMatch(hatTask -> hatTask.getHat().equals(hat));
	}

	/**
	 * Checks to see if a hat is equipped that shares the given label.
	 *
	 * @return
	 * 		True if a hat is equipped that has the given label.
	 */
	public boolean isEquipped (String label) {
		return activeHats.stream().anyMatch(hatTask -> hatTask.getHat().getLabel().equals(label));
	}
	
	/**
	 * Get how many hats this player has equipped
	 * @return
	 */
	public int getHatCount () {
		return activeHats.size();
	}

	public void toggleHats (boolean toggleState)
	{
		Player player = null;
		if (owner instanceof Player) {
			player = (Player)owner;
		}

		for (Hat hat : getActiveHats())
		{
			hat.setHidden(toggleState);
			if (owner != null && hat.isDisplaying())
			{
				if (toggleState) {
					hat.unequip(player);
				} else {
					hat.equip(player);
				}
			}
		}
	}
	
	/**
	 * Adds a hat this this players active hat list
	 * @param hat
	 */
	public void addHat (Hat hat) {
		activeHats.add(new HatTask(ParticleHats.instance, owner, hat));
	}

	public void removeHat (int index)
	{
		HatTask hatTask = activeHats.remove(index);
		if (hatTask != null)
		{
			if (owner instanceof Player) {
				hatTask.getHat().unequip((Player)owner);
			}
			hatTask.stop();
		}
	}

	public void removeHat (Hat hat)
	{
		HatTask hatTask = activeHats.stream().filter(task -> task.getHat().equals(hat)).findAny().orElse(null);
		if (hatTask != null)
		{
			activeHats.remove(hatTask);
			if (owner instanceof Player) {
				hatTask.getHat().unequip((Player)owner);
			}
			hatTask.stop();
		}
	}
	
	/**
	 * Removes the oldest equipped hat
	 */
	public void removeLastHat ()
	{
		if (activeHats.size() > 0) {
			removeHat(0);
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
	 * Set this entity's last known location
	 * @param location
	 */
	public void setLastKnownLocation (Location location) {
		lastLocation = location;
	}
	
	/**
	 * Returns this entity's last known location
	 * @return
	 */
	public Location getLastKnownLocation () {
		return lastLocation;
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
