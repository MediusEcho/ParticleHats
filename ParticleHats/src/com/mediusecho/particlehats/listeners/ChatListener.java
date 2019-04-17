package com.mediusecho.particlehats.listeners;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.player.PlayerState;

/**
 * Listens for a player editing meta properties through the menu editor
 * @author MediusEcho
 *
 */
public class ChatListener implements Listener {

	private final Core core;
	
	public ChatListener (final Core core)
	{
		this.core = core;
		core.getServer().getPluginManager().registerEvents(this, core);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerChat (AsyncPlayerChatEvent event)
	{
		if (SettingsManager.EDITOR_USE_ACTION_BAR.getBoolean())
		{
			UUID id = event.getPlayer().getUniqueId();
			PlayerState playerState = core.getPlayerState(id);
			MenuBuilder menuBuilder = playerState.getMenuBuilder();
			
			if (menuBuilder != null)
			{
				MetaState metaState = playerState.getMetaState();
				if (!metaState.equals(MetaState.NONE))
				{
					if (ChatColor.stripColor(event.getMessage()).equalsIgnoreCase("cancel")) {
						metaState.reopenEditor(menuBuilder);
					}
					
					else
					{
						List<String> arguments = Arrays.asList(event.getMessage().split(" "));
						metaState.onMetaSet(menuBuilder, event.getPlayer(), arguments);
					}
					
					event.setCancelled(true);
				}
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
