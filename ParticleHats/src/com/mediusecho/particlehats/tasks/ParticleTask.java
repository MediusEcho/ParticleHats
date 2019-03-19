package com.mediusecho.particlehats.tasks;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.effects.PixelEffect;
import com.mediusecho.particlehats.particles.properties.ParticleType;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.player.PlayerState.AFKState;
import com.mediusecho.particlehats.player.PlayerState.PVPState;

public class ParticleTask extends BukkitRunnable {

	private final Core core;
	
	private int ticks = 0;
	private int afkCooldown = SettingsManager.AFK_COOLDOWN.getInt();
	private int pvpCooldown = SettingsManager.COMBAT_COOLDOWN.getInt();
	
	private List<String> disabledWorlds;
	private boolean checkWorldPermission;
	
	public ParticleTask (Core core)
	{
		this.core = core;
		disabledWorlds = SettingsManager.DISABLED_WORLDS.getList();
		checkWorldPermission = SettingsManager.CHECK_WORLD_PERMISSION.getBoolean();
	}
	
	@Override
	public void run() 
	{
		Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
		if (onlinePlayers.size() > 0)
		{
			ticks++;
			
			for (Player player : onlinePlayers)
			{
				// Skip this world if it is disabled
				World world = player.getWorld();
				if (disabledWorlds.contains(world.getName())) {
					continue;
				}
				
				// Make sure the player has permission for this world
				if (checkWorldPermission && !player.hasPermission("particlehats.world." + world.getName())) {
					continue;
				}
				
				// Skip if the player has a potion of invisibility
				if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
					continue;
				}
				
				UUID id = player.getUniqueId();
				PlayerState playerState = core.getPlayerState(id);
				
				// Loop through each of this players active hats
				List<Hat> activeHats = playerState.getActiveHats();
				for (int i = 0; i < activeHats.size(); i++)
				{
					Hat hat = activeHats.get(i);
					
					// Skip hats that are vanished
					if (hat.isVanished()) {
						continue;
					}
					
					if (!hat.isPermanent()) 
					{
						if (hat.onTick()) {
							playerState.removeHat(i);
						}
					}
					
					// Checks and updates the players mode (afk, combat, etc)
					checkMode(id, playerState, hat);
					
					// Checks the hat against the players mode
					checkHat(id, playerState, hat, true);
				}
			}
		}
	}
	
	public void onReload ()
	{
		afkCooldown = SettingsManager.AFK_COOLDOWN.getInt();
		pvpCooldown = SettingsManager.COMBAT_COOLDOWN.getInt();
		
		disabledWorlds.clear();
		disabledWorlds = SettingsManager.DISABLED_WORLDS.getList();
		
		checkWorldPermission = SettingsManager.CHECK_WORLD_PERMISSION.getBoolean();
	}
	
	private void checkMode (UUID id, PlayerState playerState, Hat hat)
	{
		Player player = playerState.getOwner();
		AFKState afkState = playerState.getAFKState();
		PVPState pvpState = playerState.getPVPState();
		
		if (afkState == AFKState.ACTIVE)
		{
			final long timeAFK = System.currentTimeMillis() - playerState.getLastMoveTime();
			if (timeAFK > afkCooldown)
			{
				//Core.debug("setting player as AFK");
				playerState.setAFKState(AFKState.AFK);
				playerState.setAFKLocation(player.getLocation());
			}
		}
		
		if (pvpState == PVPState.ENGAGED)
		{
			final long timeEngaged = System.currentTimeMillis() - playerState.getLastCombatTime();
			if (timeEngaged > pvpCooldown) {
				playerState.setPVPState(PVPState.PEACEFUL);
			}
		}
	}
	
	private void checkHat (UUID id, PlayerState playerState, Hat hat, boolean checkNode)
	{
		Player player = playerState.getOwner();
		AFKState afkState = playerState.getAFKState();
		PVPState pvpState = playerState.getPVPState();
		
		switch (hat.getMode())
		{
			case ACTIVE:
			{
				displayHat(player, hat);
				break;
			}
			
			case WHEN_MOVING:
			{
				if (afkState == AFKState.ACTIVE) {
					displayHat(player, hat);
				}
				break;
			}
			
			case WHEN_AFK:
			{
				if (afkState == AFKState.AFK) {
					displayHat(player, hat);
				}
				break;
			}
			
			case WHEN_PEACEFUL:
			{
				if (pvpState == PVPState.PEACEFUL) {
					displayHat(player, hat);
				}
				break;
			}
			
			case WHEN_GLIDING:
			{
				if (player.isGliding()) {
					displayHat(player, hat);
				}
				break;
			}
			
			case WHEN_SPRINTING:
			{
				if (player.isSprinting()) {
					displayHat(player, hat);
				}
				break;
			}
			
			case WHEN_SWIMMING:
			{
				if (player.isSwimming()) {
					displayHat(player, hat);
				}
				break;
			}
		}
		
		// Loop through and check each node hat
		if (checkNode)
		{
			for (Hat node : hat.getNodes()) {
				checkHat(id, playerState, node, false);
			}
		}
	}

	private void displayHat (Player player, Hat hat)
	{
		ParticleType type = hat.getType();
		if (type != ParticleType.NONE)
		{
			if (!type.equals(ParticleType.CUSTOM)) {
				type.getEffect().display(ticks, player, hat);
			}
			
			else
			{
				PixelEffect customEffect = hat.getCustomEffect();
				if (customEffect != null) {
					customEffect.display(ticks, player, hat);
				}
			}
			
			PotionEffect potion = hat.getPotion();
			if (potion != null) {
				player.addPotionEffect(potion, true);
			}
		}
	}
	
	// TODO: Easter Eggs? (tags)
}
