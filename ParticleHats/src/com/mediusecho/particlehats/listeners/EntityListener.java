package com.mediusecho.particlehats.listeners;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.player.PlayerState.PVPState;

public class EntityListener implements Listener {

	private final ParticleHats core;
	
	private boolean checkPlayers = SettingsManager.COMBAT_CHECK_PLAYERS.getBoolean();
	private boolean checkAnimals = SettingsManager.COMBAT_CHECK_ANIMALSS.getBoolean();
	private boolean checkMonsters = SettingsManager.COMBAT_CHECK_MONSTERS.getBoolean();
	private boolean checkNPC = SettingsManager.COMBAT_CHECK_NPC.getBoolean();
	
	public EntityListener (final ParticleHats core)
	{
		this.core = core;
		core.getServer().getPluginManager().registerEvents(this, core);
	}
	
	public void onReload ()
	{
		checkPlayers = SettingsManager.COMBAT_CHECK_PLAYERS.getBoolean();
		checkAnimals = SettingsManager.COMBAT_CHECK_ANIMALSS.getBoolean();
		checkMonsters = SettingsManager.COMBAT_CHECK_MONSTERS.getBoolean();
		checkNPC = SettingsManager.COMBAT_CHECK_NPC.getBoolean();
	}
	
	/**
	 * Prevents item from being picked up if they were created from the ItemStack particle effect
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemPickup (InventoryPickupItemEvent event)
	{
		Item item = event.getItem();
		event.setCancelled(item.hasMetadata("PH_DroppedItem"));
	}
	
	@EventHandler
	public void onEntityDamage (EntityDamageByEntityEvent event)
	{
		Entity attacker = event.getDamager();
		Entity victim = event.getEntity();
		
		if (victim instanceof Player && !victim.hasMetadata("NPC"))
		{
			if (checkCombat(attacker)) 
			{
				handleCombat((Player)victim);
				return;
			}
		}
		
		if (attacker instanceof Player  && !attacker.hasMetadata("NPC"))
		{
			if (checkCombat(victim))
			{
				handleCombat((Player)attacker);
				return;
			}
		}
		
		else if (attacker instanceof Arrow)
		{
			Arrow arrow = (Arrow)attacker;
			if (arrow.getShooter() != null)
			{
				ProjectileSource shooter = arrow.getShooter();
				if (shooter instanceof Player)
				{
					Player p = (Player)shooter;
					if (checkCombat(victim)) {
						handleCombat(p);
					}
				}
			}
		}
	}
	
	private boolean checkCombat (Entity e)
	{
		if (checkPlayers && e instanceof Player) {
			return true;
		}
		
		if (checkAnimals && e instanceof Animals) {
			return true;
		}
		
		if (checkMonsters && e instanceof Monster || e instanceof Slime) {
			return true;
		}
		
		if (checkNPC && e instanceof NPC) {
			return true;
		}
		
		return false;
	}
	
	private void handleCombat (Player player)
	{
		PlayerState playerState = core.getPlayerState(player);
		
		playerState.setLastCombatTime(System.currentTimeMillis());
		playerState.setPVPState(PVPState.ENGAGED);
	}
}
