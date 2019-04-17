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
			String cmd = args.get(0);
			if (subCommands.containsKey(cmd))
			{
				args.remove(0);
				Command subCommand = subCommands.get(cmd);
				
				if (!sender.hasPermission(subCommand.getPermission()))
				{
					sender.sendMessage(Message.COMMAND_ERROR_NO_PERMISSION);
					return false;
				}
				
				if (!sender.isPlayer() && subCommand.isPlayerOnly())
				{
					sender.sendMessage(Message.COMMAND_ERROR_PLAYER_ONLY);
					return false;
				}
				
				subCommand.execute(core, sender, label, args);
			}
			
			else
			{
				sender.sendMessage(Message.COMMAND_ERROR_UNKNOWN);
				return false;
			}
		}
		
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
	public Permission getPermission() {
		return Permission.COMMAND_MAIN;
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
