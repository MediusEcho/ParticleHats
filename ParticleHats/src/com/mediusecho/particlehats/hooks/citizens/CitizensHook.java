package com.mediusecho.particlehats.hooks.citizens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.configuration.CustomConfig;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.editor.citizens.CitizensMenuManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.util.StringUtil;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.npc.NPC;

public class CitizensHook implements Listener {

	private final ParticleHats core;
	private final Database database;
	private final Map<Integer, List<String>> citizenHatStrings;
	
	private CustomConfig citizenConfig;
	
	public CitizensHook (final ParticleHats core)
	{
		this.core = core;
		this.database = core.getDatabase();
		this.citizenHatStrings = new HashMap<Integer, List<String>>();
		this.citizenConfig = new CustomConfig(core, "", "npcs.yml", false);		
		
		core.getServer().getPluginManager().registerEvents(this, core);
		
		loadCitizenData();
	}
	
	@EventHandler
	public void onNPCRightClick (NPCRightClickEvent event)
	{
		Player player = event.getClicker();
		EntityState entityState = core.getEntityState(player);
		
		if (entityState instanceof PlayerState)
		{
			PlayerState playerState = (PlayerState)entityState;
			
			if (playerState.getMetaState() == MetaState.NPC_MANAGE)
			{	
				NPC npc = event.getNPC();
				EntityState citizenEntityState = core.getEntityState(npc.getEntity(), npc.getId());
				
				CitizensMenuManager citizensManager = new CitizensMenuManager(core, player, citizenEntityState);
				
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
		DespawnReason reason = event.getReason();
		
		ParticleHats.debug(reason.toString() + " id: " + event.getNPC().getId());
		
		core.removePlayerState(entity.getUniqueId());
	}
	
	@EventHandler
	public void onNPCSpawn (NPCSpawnEvent event)
	{
		NPC npc = event.getNPC();
		int id = npc.getId();
		
		ParticleHats.debug("spawning npc: " + id);
		
		if (!npc.isSpawned()) {
			return;
		}
		
		if (!citizenHatStrings.containsKey(id)) {
			return;
		}
		
		List<String> hatStrings = citizenHatStrings.get(id);
		EntityState entityState = core.getEntityState(npc.getEntity(), npc.getId());
		
		for (String hatString : hatStrings)
		{
			String[] data = hatString.split(":");
			if (data.length != 2) {
				continue;
			}
			
			int slot = StringUtil.toInt(data[1], -1);
			if (slot == -1) {
				return;
			}
			
			Hat hat = new Hat();
			database.loadHat(data[0], slot, hat);
			
			entityState.addHat(hat);
		}
	}
	
	public Entity getNPCEntity (int id)
	{
		NPC npc = CitizensAPI.getNPCRegistry().getById(id);
		if (npc == null) {
			return null;
		}
		return npc.getEntity();
	}
	
	public void saveCitizenData (Entity entity, EntityState entityState)
	{
		if (entityState.getID() == -1) {
			return;
		}
		
		int id = entityState.getID();
		FileConfiguration config = citizenConfig.getConfig();
		String path = "npc-ids." + entityState.getID();
		
		if (entityState.getHatCount() == 0)
		{
			citizenHatStrings.remove(id);
			config.set(path, null);
			citizenConfig.save();
			citizenConfig.reload();
			
			return;
		}
		
		List<String> hatStrings = new ArrayList<String>();
		for (Hat hat : entityState.getActiveHats())
		{
			String hatString = hat.getMenu() + ":" + hat.getSlot();
			hatStrings.add(hatString);
		}
		
		config.set(path, hatStrings);
		citizenConfig.save();
		citizenConfig.reload();
	}
	
	private void loadCitizenData ()
	{
		FileConfiguration config = citizenConfig.getConfig();
		if (!config.isConfigurationSection("npc-ids")) {
			return;
		}
		
		Set<String> keys = config.getConfigurationSection("npc-ids").getKeys(false);
		for (String key : keys)
		{
			if (key == null) {
				continue;
			}
			
			int id = StringUtil.toInt(key, -1);
			if (id == -1) {
				continue;
			}
			
			String path = "npc-ids." + key;
			List<String> hatStrings = config.getStringList(path);
			
			citizenHatStrings.put(id, hatStrings);
		}
	}
	
}
