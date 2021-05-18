package com.mediusecho.particlehats.listeners;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.player.PlayerState.PVPState;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.projectiles.ProjectileSource;

public class EntityListener implements Listener {

	private final ParticleHats core;
	
	private boolean checkPlayers = false;
	private boolean checkAnimals = false;
	private boolean checkMonsters = false;
	private boolean checkNPC = false;
	
	public EntityListener (final ParticleHats core)
	{
		this.core = core;
		core.getServer().getPluginManager().registerEvents(this, core);
		onReload();
	}
	
	public void onReload ()
	{
		checkPlayers = SettingsManager.COMBAT_CHECK_PLAYERS.getBoolean();
		checkAnimals = SettingsManager.COMBAT_CHECK_ANIMALS.getBoolean();
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
		if (item.hasMetadata("PH_DroppedItem")) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityDamage (EntityDamageByEntityEvent event)
	{		
		if (event.isCancelled()) {
			return;
		}
		
		Entity attacker = event.getDamager();
		Entity victim = event.getEntity();
		
		if (checkCombat(victim)) {
			handleCombat(attacker);
		}
		
		if (checkCombat(attacker)) {
			handleCombat(victim);
		}
		
		if (attacker instanceof Arrow) 
		{
			Arrow arrow = (Arrow)attacker;
			if (arrow.getShooter() != null)
			{
				ProjectileSource pShooter = arrow.getShooter();
				if (pShooter instanceof Entity) 
				{
					Entity shooter = (Entity)pShooter;
					
					if (checkCombat(victim)) {
						handleCombat((Entity)shooter);
					}
					
					if (checkCombat(shooter)) {
						handleCombat(victim);
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
		
		if (checkNPC && (e instanceof NPC || e.hasMetadata("NPC"))) {
			return true;
		}
		
		return false;
	}
	
	private void handleCombat (Entity entity)
	{
		// We don't want to create a new EntityState if one doesn't exist for this entity
		if (!core.hasEntityState(entity)) {
			return;
		}
		
		EntityState entityState = core.getEntityState(entity);
		
		entityState.setLastCombatTime(System.currentTimeMillis());
		entityState.setPVPState(PVPState.ENGAGED);
	}
}
