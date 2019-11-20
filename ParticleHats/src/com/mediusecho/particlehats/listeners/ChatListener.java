package com.mediusecho.particlehats.listeners;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.MenuManager;

/**
 * Listens for a player editing meta properties through the menu editor
 * @author MediusEcho
 *
 */
public class ChatListener implements Listener {

	private final ParticleHats core;
	
	public ChatListener (final ParticleHats core)
	{
		this.core = core;
		core.getServer().getPluginManager().registerEvents(this, core);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerChat (AsyncPlayerChatEvent event)
	{
		if (SettingsManager.EDITOR_USE_ACTION_BAR.getBoolean())
		{
			Player player = event.getPlayer();
			PlayerState playerState = core.getPlayerState(player);
			
			if (playerState.hasMenuManager())
			{
				MetaState metaState = playerState.getMetaState();
				if (metaState.equals(MetaState.NONE)) {
					return;
				}
				
				MenuManager menuManager = playerState.getMenuManager();
				if (!(menuManager instanceof EditorMenuManager)) {
					return;
				}
				
				EditorMenuManager editorManager = (EditorMenuManager)menuManager;
				event.setCancelled(true);
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(ParticleHats.instance, () ->
				{
					if (ChatColor.stripColor(event.getMessage()).equals("cancel")) 
					{
						editorManager.reopen();
						return;
					}
					
					List<String> arguments = Arrays.asList(event.getMessage().split(" "));
					metaState.onMetaSet(editorManager, player, arguments);
				});
			}
		}
	}
	
	/**
	 * Unregisters this AsyncPlayerChatEvent Listener
	 */
	public void unregister () {
		AsyncPlayerChatEvent.getHandlerList().unregister(this);
	}
}
