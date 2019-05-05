package com.mediusecho.particlehats.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.GuiState;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.ui.StaticMenu;

public class CommandListener implements Listener {

	private final Core core;
	
	public CommandListener (final Core core)
	{
		this.core = core;
		core.getServer().getPluginManager().registerEvents(this, core);
	}
	
	@EventHandler
	public void onCommand (PlayerCommandPreprocessEvent event)
	{
		if (event.getMessage().contains(" ")) {
			return;
		}
	
		String cmd = event.getMessage().replaceFirst("/", "");
		Player player = event.getPlayer();
		PlayerState playerState = core.getPlayerState(player.getUniqueId());
		MenuInventory inventory = core.getDatabase().getInventoryFromAlias(cmd, playerState);
		
		if (inventory != null)
		{
			event.setCancelled(true);
			
			StaticMenu menu = new StaticMenu(core, event.getPlayer(), inventory);
			
			playerState.setGuiState(GuiState.SWITCHING_MENU);
			playerState.setOpenMenu(menu);
			menu.open();
		}
	}
}
