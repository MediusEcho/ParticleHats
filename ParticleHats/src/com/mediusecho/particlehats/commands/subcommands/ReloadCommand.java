package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.CommandPermission;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.locale.Message;

public class ReloadCommand extends Command {

	@Override
	public boolean execute(Core core, Sender sender, String label, ArrayList<String> args) 
	{
		if (!sender.hasPermission(getPermission()))
		{
			sender.sendMessage(Message.COMMAND_ERROR_NO_PERMISSION);
			return false;
		}
		
		core.onReload();
		sender.sendMessage(Message.COMMAND_RELOAD_SUCCESS);
		return true;
	}

	@Override
	public String getName() {
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
	public CommandPermission getPermission() {
		return CommandPermission.RELOAD;
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
