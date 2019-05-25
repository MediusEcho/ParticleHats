package com.mediusecho.particlehats.prompt;

import org.bukkit.entity.Player;

import com.mediusecho.particlehats.editor.MetaState;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class SpigotPrompt extends BukkitPrompt {

	private boolean success = true;
	
	@Override
	public void prompt(Player player, String message) 
	{
		try
		{
			BaseComponent[] bc = TextComponent.fromLegacyText(message);
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, bc);
		}
		
		catch (NoSuchMethodError e) 
		{
			super.prompt(player, message);
			success = false;
		}
	}

	@Override
	public void prompt(Player player, MetaState state) {
		prompt(player, state.getDescription());
	}

	@Override
	public boolean canPrompt(int passes) {
		return success ? passes % 1 == 0 : super.canPrompt(passes);
	}

}
