package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;

public class RemoveTypeCommand extends Command {

	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{
		if (args.size() < 1)
		{
			sender.sendMessage(Message.COMMAND_ERROR_ARGUMENTS);
			sender.sendMessage(getUsage());
			return false;
		}
		
		String imageName = args.get(0);
		if (!core.getDatabase().getImages(false).containsKey(imageName))
		{
			sender.sendMessage(Message.COMMAND_ERROR_UNKNOWN_TYPE.replace("{1}", imageName));
			return false;
		}
		
		if (core.getDatabase().deleteImage(imageName))
		{
			sender.sendMessage(Message.COMMAND_REMOVE_TYPE_SUCCESS.replace("{1}", imageName));
			return true;
		}
		
		return false;
	}
	
	@Override
	public List<String> tabCompelete (ParticleHats core, Sender sender, String label, ArrayList<String> args)
	{
		if (args.size() == 1) {
			return new ArrayList<String>(core.getDatabase().getImages(false).keySet());
		}
		return Arrays.asList("");
	}

	@Override
	public String getName() {
		return "remove type";
	}
	
	@Override
	public String getArgumentName () {
		return "remove";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_REMOVE_TYPE_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_REMOVE_TYPE_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_TYPE_REMOVE;
	}
	
	@Override
	public boolean hasWildcardPermission () {
		return true;
	}
	
	@Override
	public Permission getWildcardPermission () {
		return Permission.COMMAND_TYPE_ALL;
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
