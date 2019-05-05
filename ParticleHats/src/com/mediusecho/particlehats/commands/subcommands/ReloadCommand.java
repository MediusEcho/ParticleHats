package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;

public class ReloadCommand extends Command {

	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{		
		core.onReload();
		sender.sendMessage(Message.COMMAND_RELOAD_SUCCESS);
		return true;
	}

	@Override
	public String getName() {
		return "reload";
	}
	
	@Override
	public String getArgumentName () {
		return "reload";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_RELOAD_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_RELOAD_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_RELOAD;
	}
	
	@Override
	public boolean showInHelp() {
		return true;
	}
	
	@Override
	public boolean isPlayerOnly() {
		return false;
	}

}
