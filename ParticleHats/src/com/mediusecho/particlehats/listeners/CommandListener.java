package com.mediusecho.particlehats.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.GuiState;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.ui.StaticMenu;

public class CommandListener implements Listener {

	private final ParticleHats core;
	
	public CommandListener (final ParticleHats core)
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
		
		Player player = event.getPlayer();
		PlayerState playerState = core.getPlayerState(player.getUniqueId());
		
		// Cancel any commands if this player is editing a meta property
		if (SettingsManager.EDITOR_RESTRICT_COMMANDS.getBoolean() && playerState.isEditing())
		{
			MetaState metaState = playerState.getMetaState();
			if (metaState != MetaState.NONE) 
			{
				player.sendMessage(Message.COMMAND_ERROR_ALREADY_EDITING.getValue());
				event.setCancelled(true);
			}
		}
		
		// Check for menu aliases
		else
		{
			String cmd = event.getMessage().replaceFirst("/", "");
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
}
