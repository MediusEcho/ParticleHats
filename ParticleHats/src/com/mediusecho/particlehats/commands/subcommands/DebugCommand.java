package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.CommandPermission;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.locale.Message;

public class DebugCommand extends Command {

	@Override
	public boolean execute(Core core, Sender sender, String label, ArrayList<String> args) 
	{
		if (args.size() == 3)
		{
			String menuName = args.get(0);
			int currentSlot = Integer.valueOf(args.get(1));
			int newSlot = Integer.valueOf(args.get(2));
			
			core.getDatabase().cloneHatData(menuName, currentSlot, newSlot);
		}
		return false;
	}

	@Override
	public String getName() {
		return "debug";
	}
	
	@Override
	public String getArgumentName () {
		return "debug";
	}

	@Override
	public Message getUsage() {
		return Message.UNKNOWN;
	}

	@Override
	public Message getDescription() {
		return Message.UNKNOWN;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_ALL;
	}
	
	@Override
	public boolean showInHelp() {
		return false;
	}
	
	@Override
	public boolean isPlayerOnly() {
		return true;
	}

}
