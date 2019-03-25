package com.mediusecho.particlehats.tasks;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.util.StringUtil;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class PromptTask extends BukkitRunnable {

	private final Core core;
	
	public PromptTask (final Core core)
	{
		this.core = core;
	}
	
	@Override
	public void run() 
	{
		Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
		if (onlinePlayers.size() > 0)
		{
			for (Player player : onlinePlayers)
			{
				PlayerState playerState = core.getPlayerState(player.getUniqueId());
				MetaState metaState = playerState.getMetaState();
				
				if (metaState == MetaState.NONE) {
					continue;
				}
				
				if (core.canUseBungee() && SettingsManager.EDITOR_USE_ACTION_BAR.getBoolean())
				{
					int time = playerState.getMetaStateTime();
					if (time <= 0) 
					{
						metaState.reopenEditor(playerState.getMenuBuilder());
						continue;
					}
					
					String description = metaState.getDescription() + StringUtil.colorize(" &f(" + time + ")");
					prompt(player, description);
					
//					BaseComponent[] bc = TextComponent.fromLegacyText(description);
//					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, bc);
				}
			}
		}
	}

	/**
	 * Sends the player a message using their Action Bar
	 * @param player
	 * @param message
	 */
	public void prompt (Player player, String message)
	{
		BaseComponent[] bc = TextComponent.fromLegacyText(message);
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, bc);
	}
}
