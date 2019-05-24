package com.mediusecho.particlehats.prompt;

import org.bukkit.entity.Player;

import com.mediusecho.particlehats.editor.MetaState;

public class BukkitPrompt implements Prompt {

	@Override
	public void prompt(Player player, String message) {
		player.sendMessage(message);
	}

	@Override
	public void prompt(Player player, MetaState state) {
		prompt(player, state.getUsage());
	}

	@Override
	public boolean canPrompt(int passes) {
		return passes % 18 == 0;
	}

}
