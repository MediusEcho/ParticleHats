package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;

public class DeleteGroupCommand extends Command {

	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{
		if (args.size() < 1)
		{
			sender.sendMessage(Message.COMMAND_ERROR_ARGUMENTS);
			sender.sendMessage(getUsage());
			return false;
		}
		
		String groupName = args.get(0);
		if (!core.getDatabase().getGroups(false).containsKey(groupName))
		{
			sender.sendMessage(Message.COMMAND_ERROR_UNKNOWN_GROUP.replace("{1}", groupName));
			return false;
		}
		
		core.getDatabase().deleteGroup(groupName);
		
		sender.sendMessage(Message.COMMAND_REMOVE_GROUP_SUCCESS.replace("{1}", groupName));
		return true;
	}
	
	@Override
	public List<String> tabCompelete (ParticleHats core, Sender sender, String label, ArrayList<String> args)
	{
		if (args.size() == 1)
		{
			return new ArrayList<String>(core.getDatabase().getGroups(false).keySet());
		}
		return Arrays.asList("");
	}

	@Override
	public String getName() {
		return "remove group";
	}
	
	@Override
	public String getArgumentName () {
		return "remove";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_REMOVE_GROUP_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_REMOVE_GROUP_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_GROUP_REMOVE;
	}
	
	@Override
	public boolean hasWildcardPermission () {
		return true;
	}
	
	@Override
	public Permission getWildcardPermission () {
		return Permission.COMMAND_GROUP_ALL;
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
