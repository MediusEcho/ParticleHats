package com.mediusecho.particlehats.hooks.economy;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.mediusecho.particlehats.hooks.CurrencyHook;

public class PlayerPointsHook implements CurrencyHook {

	private PlayerPoints playerPoints;
	
	public PlayerPointsHook ()
	{
		final Plugin playerPointsPlugin = Bukkit.getServer().getPluginManager().getPlugin("PlayerPoints");
		playerPoints = (PlayerPoints.class.cast(playerPointsPlugin));
	}
	
	@Override
	public int getBalance(Player player) {
		return playerPoints.getAPI().look(player.getUniqueId());
	}

	@Override
	public boolean withdraw(Player player, int amount) {
		return playerPoints.getAPI().take(player.getUniqueId(), amount);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
