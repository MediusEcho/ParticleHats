package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.EquippedParticlesMenu;
import com.mediusecho.particlehats.ui.StaticMenuManager;

public class ParticlesCommand extends Command {

	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{		
		PlayerState playerState = core.getPlayerState(sender.getPlayer());
		
		if (playerState.hasEditorOpen()) 
		{
			sender.sendMessage(Message.COMMAND_ERROR_ALREADY_EDITING);
			return false;
		}
		
		StaticMenuManager staticManager = core.getMenuManagerFactory().getStaticMenuManager(playerState);
		EquippedParticlesMenu particlesMenu = new EquippedParticlesMenu(core, staticManager, sender.getPlayer(), false);
		
		staticManager.addMenu(particlesMenu);
		particlesMenu.open();
		return true;
	}

	@Override
	public String getName() {
		return "particles";
	}
	
	@Override
	public String getArgumentName () {
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
