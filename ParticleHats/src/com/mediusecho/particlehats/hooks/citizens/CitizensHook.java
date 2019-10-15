package com.mediusecho.particlehats.hooks.citizens;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.particles.Hat;

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
		NPC npc = event.getNPC();
		ParticleHats.log("NPCRightClickEvent called, id: " + npc.getUniqueId().toString());
		
		Hat hat = plugin.getDatabase().getHatFromLabel("npc_test");
		if (hat != null) {
			plugin.getEntityState(npc.getEntity()).addHat(hat);
		}
	}
	
}
