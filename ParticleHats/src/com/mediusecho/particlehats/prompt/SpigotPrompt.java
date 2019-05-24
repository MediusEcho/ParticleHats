package com.mediusecho.particlehats.prompt;

import org.bukkit.entity.Player;

import com.mediusecho.particlehats.editor.MetaState;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class SpigotPrompt implements Prompt {

	@Override
	public void prompt(Player player, String message) 
	{
		BaseComponent[] bc = TextComponent.fromLegacyText(message);
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, bc);
	}

	@Override
	public void prompt(Player player, MetaState state) {
		prompt(player, state.getDescription());
	}

	@Override
	public boolean canPrompt(int passes) {
		return passes % 1 == 0;
	}

}
