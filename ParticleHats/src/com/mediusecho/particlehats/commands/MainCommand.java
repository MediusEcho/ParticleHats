package com.mediusecho.particlehats.commands;

import java.util.ArrayList;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.locale.Message;

public class MainCommand extends Command {

	@Override
	public boolean execute(Core core, Sender sender, String label, ArrayList<String> args) 
	{
		// Execute this command
		if (args.size() == 0)
		{
			Core.log("Hello World");
			Core.log("Looking up menus");	
		}
		
		// Find and execute our sub command
		else 
		{
			Command subCommand = subCommands.get(args.get(0));
			if (subCommand != null) 
			{
				args.remove(0);
				subCommand.execute(core, sender, label, args);
			}
			
			else
			{
				//sender.sendMessage(MessageManager.COMMAND_UNKNOWN);
				return false;
			}
		}
		
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		return "h";
	}
	
	@Override 
	public Message getUsage () {
		return Message.COMMAND_MAIN_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_MAIN_DESCRIPTION;
	}

	@Override
	public CommandPermission getPermission() {
		return CommandPermission.ALL;
	}
}
