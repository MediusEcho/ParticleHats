package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.CommandPermission;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.ActiveParticlesMenu;
import com.mediusecho.particlehats.ui.MenuState;

public class ParticlesCommand extends Command {

	@Override
	public boolean execute(Core core, Sender sender, String label, ArrayList<String> args) 
	{
		if (!sender.isPlayer())
		{
			sender.sendMessage(Message.COMMAND_ERROR_PLAYER_ONLY);
			return false;
		}
		
		if (!sender.hasPermission(getPermission()))
		{
			sender.sendMessage(Message.COMMAND_ERROR_NO_PERMISSION);
			return false;
		}
		
		//EditorActiveParticlesMenu activeParticlesMenu = new EditorActiveParticlesMenu(core, sender.getPlayer(), false);
		ActiveParticlesMenu activeParticlesMenu = new ActiveParticlesMenu(core, sender.getPlayer(), false);
		PlayerState playerState = core.getPlayerState(sender.getPlayerID());
		
		playerState.setActiveParticlesMenu(activeParticlesMenu);
		playerState.setMenuState(MenuState.ACTIVE_PARTICLES);
		
		activeParticlesMenu.open();
		return true;
	}

	@Override
	public String getName() {
		return "particles";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_PARTICLE_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_PARTICLE_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_PARTICLES;
	}

	@Override
	public boolean showInHelp() {
		return true;
	}
	
	@Override
	public boolean isPlayerOnly() {
		return true;
	}

}
