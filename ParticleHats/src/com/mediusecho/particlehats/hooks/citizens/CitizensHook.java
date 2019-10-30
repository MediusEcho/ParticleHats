package com.mediusecho.particlehats.hooks.citizens;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.editor.citizens.CitizensMenuManager;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.player.PlayerState;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

public class CitizensHook implements Listener {

	private final ParticleHats plugin;
	
	public CitizensHook (final ParticleHats plugin)
	{
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onNPCRightClick (NPCRightClickEvent event)
	{
		Player player = event.getClicker();
		EntityState entityState = plugin.getEntityState(player);
		
		if (entityState instanceof PlayerState)
		{
			PlayerState playerState = (PlayerState)entityState;
			
			if (playerState.getMetaState() == MetaState.NPC_MANAGE)
			{	
				CitizensMenuManager citizensManager = new CitizensMenuManager(plugin, player, event.getNPC().getEntity());
				
				playerState.setMetaState(MetaState.NONE);
				playerState.setMenuManager(citizensManager);
				
				citizensManager.open();
			}
		}
	}
	
	@EventHandler
	public void onNPCDespawn (NPCDespawnEvent event)
	{
		Entity entity = event.getNPC().getEntity();
		plugin.removePlayerState(entity.getUniqueId());
	}
	
	public Entity getNPCEntity (int id)
	{
		NPC npc = CitizensAPI.getNPCRegistry().getById(id);
		if (npc == null) {
			return null;
		}
		return npc.getEntity();
	}
	
}
