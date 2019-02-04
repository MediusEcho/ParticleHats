package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.CommandPermission;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.locale.Message;

public class CreateCommand extends Command {
	
	@Override
	public boolean execute(Core core, Sender sender, String label, ArrayList<String> args) 
	{
		if (args.size() == 1)
		{
			String menuName = (args.get(0).contains(".") ? args.get(0).split("\\.")[0] : args.get(0));
			Database database = core.getDatabase();
			database.createEmptyMenu(menuName);
		}
		return false;
	}

	@Override
	public String getName() {
		return "create";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_CREATE_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_CREATE_DESCRIPTION;
	}

	@Override
	public CommandPermission getPermission() {
		return CommandPermission.ALL;
	}

}
