package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.util.StringUtil;

public class EditGroupCommand extends Command {

	@Override
	public boolean execute(Core core, Sender sender, String label, ArrayList<String> args) 
	{
		if (args.size() < 2)
		{
			sender.sendMessage(Message.COMMAND_ERROR_ARGUMENTS);
			sender.sendMessage(getUsage());
			return false;
		}
		
		Database database = core.getDatabase();
		
		int weight = -1;
		String groupName = args.get(0);
		String menuName = args.get(1);
		
		if (!database.getGroups(false).containsKey(groupName))
		{
			sender.sendMessage(Message.COMMAND_ERROR_UNKNOWN_GROUP.replace("{1}", groupName));
			return false;
		}
		
		if (!database.getMenus(false).containsKey(menuName))
		{
			sender.sendMessage(Message.COMMAND_ERROR_UNKNOWN_MENU.replace("{1}", menuName));
			return false;
		}
		
		if (args.size() >= 3) {
			weight = StringUtil.toInt(args.get(2), -1);
		}
		
		database.editGroup(groupName, menuName, weight);
		sender.sendMessage(Message.COMMAND_EDIT_GROUP_SUCCESS.replace("{1}", groupName));
		
		return false;
	}
	
	@Override
	public List<String> tabCompelete (Core core, Sender sender, String label, ArrayList<String> args)
	{
		switch (args.size())
		{
		case 1:
			return new ArrayList<String>(core.getDatabase().getGroups(false).keySet());
			
		case 2:
			return new ArrayList<String>(core.getDatabase().getMenus(false).keySet());
			
		case 3:
			return Arrays.asList("weight");
		}
		return Arrays.asList("");
	}

	@Override
	public String getName() {
		return "edit";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_EDIT_GROUP_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_EDIT_GROUP_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_GROUP_EDIT;
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
