package com.mediusecho.particlehats.tasks;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.ParticleTag;
import com.mediusecho.particlehats.particles.properties.ParticleType;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.player.PlayerState.AFKState;
import com.mediusecho.particlehats.player.PlayerState.PVPState;

public class ParticleTask extends BukkitRunnable {

	private final ParticleHats core;
	
	private int ticks = 0;
	private int afkCooldown = SettingsManager.AFK_COOLDOWN.getInt() * 1000;
	private int pvpCooldown = SettingsManager.COMBAT_COOLDOWN.getInt() * 1000;
	
	private List<String> disabledWorlds;
	private boolean checkWorldPermission;
	private boolean essentialsVanishFlag;
	
	public ParticleTask (ParticleHats core)
	{
		this.core = core;
		disabledWorlds = SettingsManager.DISABLED_WORLDS.getList();
		checkWorldPermission = SettingsManager.CHECK_WORLD_PERMISSION.getBoolean();
		essentialsVanishFlag = SettingsManager.FLAG_ESSENTIALS_VANISH.getBoolean();
	}
	
	@Override
	public void run() 
	{
		Collection<EntityState> entityStates = core.getEntityStates();
		if (entityStates.size() > 0)
		{
			ticks++;
			
			for (EntityState entityState : entityStates)
			{
				Entity entity = entityState.getOwner();
				
				if (entity == null) {
					continue;
				}
				
				// Skip this entity if they don't have any hats
				if (entityState.getHatCount() == 0) {
					continue;
				}
				
				// Skip this world if it is disabled
				World world = entity.getWorld();
				if (disabledWorlds.contains(world.getName())) {
					continue;
				}
				
				// Handle player checks
				if (entityState instanceof PlayerState)
				{
					PlayerState playerState = (PlayerState)entityState;
					Player player = playerState.getOwner();
					
					// Make sure the player has permission for this world
					if (checkWorldPermission && !player.hasPermission("particlehats.world." + world.getName())) {
						continue;
					}
					
					// Skip if the player has a potion of invisibility
					if (essentialsVanishFlag && player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
						continue;
					}
				}
				
				UUID entityID = entity.getUniqueId();
				
				// Checks and updates the entities mode
				checkMode(entityID, entityState);
				
				List<Hat> activeHats = entityState.getActiveHats();
				for (int i = 0; i < activeHats.size(); i++)
				{
					Hat hat = activeHats.get(i);
					
					// Skip any hat that is hidden / vanished
					if (hat.isVanished() || hat.isHidden()) {
						continue;
					}
					
					// Update the hats demo count down
					if (!hat.isPermanent())
					{
						if (hat.onTick()) 
						{
							entityState.removeHat(i);
							continue;
						}
					}
					
					// Checks the hats against the entities mode
					checkHat(entityID, entityState, hat, true);
				}
			}
		}
	}
	
	public void onReload ()
	{
		afkCooldown = SettingsManager.AFK_COOLDOWN.getInt() * 1000;
		pvpCooldown = SettingsManager.COMBAT_COOLDOWN.getInt() * 1000;
		
		disabledWorlds.clear();
		disabledWorlds = SettingsManager.DISABLED_WORLDS.getList();
		
		checkWorldPermission = SettingsManager.CHECK_WORLD_PERMISSION.getBoolean();
		essentialsVanishFlag = SettingsManager.FLAG_ESSENTIALS_VANISH.getBoolean();
	}
	
	private void checkMode (UUID id, EntityState entityState)
	{
		Entity entity = entityState.getOwner();
		AFKState afkState = entityState.getAFKState();
		PVPState pvpState = entityState.getPVPState();
		
		Location lastKnownLocation = entityState.getLastKnownLocation();
		Location currentLocation = entity.getLocation();
		
		// Make sure there is always a last known location
		if (lastKnownLocation == null || !(lastKnownLocation.getWorld().equals(currentLocation.getWorld()))) 
		{
			lastKnownLocation = currentLocation;
			entityState.setLastKnownLocation(lastKnownLocation);
		}
		
		// Update our last move time if the 2 locations are more than a block from each other
		double activeDistance = lastKnownLocation.distanceSquared(currentLocation);
		if (activeDistance >= 1) 
		{
			entityState.setLastMoveTime(System.currentTimeMillis());
			entityState.setLastKnownLocation(currentLocation);
		}
		
		if (afkState == AFKState.AFK)
		{
			Location afkLocation = entityState.getAFKLocation();
			if (afkLocation != null)
			{
				double distance = 7;
				if (afkLocation.getWorld().equals(currentLocation.getWorld())) {
					distance = afkLocation.distanceSquared(currentLocation);
				}
				
				if (distance > 6) 
				{
					entityState.setAFKState(AFKState.ACTIVE);
					entityState.setLastKnownLocation(currentLocation);
					entityState.setLastMoveTime(System.currentTimeMillis());
				}
			}
		}
		
		if (afkState == AFKState.ACTIVE)
		{
			final long timeAFK = System.currentTimeMillis() - entityState.getLastMoveTime();
			if (timeAFK > afkCooldown)
			{
				entityState.setAFKState(AFKState.AFK);
				entityState.setAFKLocation(currentLocation);
				entityState.setLastKnownLocation(currentLocation);
			}
		}
		
		if (pvpState == PVPState.ENGAGED)
		{
			final long timeEngaged = System.currentTimeMillis() - entityState.getLastCombatTime();
			if (timeEngaged > pvpCooldown) {
				entityState.setPVPState(PVPState.PEACEFUL);
			}
		}
	}
	
	private void checkHat (UUID id, EntityState entityState, Hat hat, boolean checkNode)
	{
		if (hat.canDisplay(entityState)) {
			displayHat(entityState.getOwner(), hat);
		}
		
		// Loop through and check each node hat
		if (checkNode)
		{
			for (Hat node : hat.getNodes()) {
				checkHat(id, entityState, node, false);
			}
		}
	}
	
	private void displayHat (Entity entity, Hat hat)
	{
		if (hat.getType() == ParticleType.NONE) {
			return;
		}
		
		if (!handleTags(entity, hat, ticks)) {
			return;
		}
		
		hat.displayType(ticks, entity);
		
		if (entity instanceof Player)
		{
			if (hat.hasPotionData()) {
				hat.applyPotions((Player) entity);
			}
		}
	}
	
	private boolean handleTags (Entity entity, Hat hat, int ticks)
	{
		List<ParticleTag> tags = hat.getTags();
		
		if (tags.contains(ParticleTag.ARROWS))
		{
			for (Entity e : entity.getNearbyEntities(50, 50, 50))
			{
				if (e instanceof Arrow)
				{
					Arrow arrow = (Arrow)e;
					if (!arrow.isOnGround())
					{
						if (arrow.getShooter() instanceof Player) {
							hat.displayType(ticks, arrow);
						}
					}
				}
			}
			return false;
		}
		
		if (tags.contains(ParticleTag.PICTURE_MODE) && entity instanceof Player)
		{
			displayToNearestEntity((Player)entity, hat, ticks, ArmorStand.class);
			return false;
		}
		
		return true;
	}
	
	private void displayToNearestEntity (Player player, Hat hat, int ticks, Class<?> entity)
	{
		Entity nearest = null;
		double maxDistance = 1000;
		
		for (Entity e : player.getNearbyEntities(50, 10, 50))
		{
			if (entity.isInstance(e))
			{
				double distance = e.getLocation().distanceSquared(player.getLocation());
				if (distance < maxDistance)
				{
					maxDistance = distance;
					nearest = e;
				}
			}
		}
		
		if (nearest != null) {
			hat.displayType(ticks, nearest);
		}
	}
}
