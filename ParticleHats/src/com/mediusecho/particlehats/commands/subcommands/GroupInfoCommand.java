package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.database.properties.Group;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;

public class GroupInfoCommand extends Command {

	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{
		List<Group> groups = core.getDatabase().getGroups(true);
		String infoTemplate = Message.COMMAND_GROUP_INFO.getValue();
		
		sender.sendMessage("&f> " + Message.COMMAND_GROUP_INFO_TIP.getValue());
		for (Group g : groups) {
			sender.sendMessage("&f> " + infoTemplate.replace("{1}", g.getName()).replace("{2}", g.getDefaultMenu()).replace("{3}", Integer.toString(g.getWeight())));
		}
		
		return false;
	}

	@Override
	public String getName() {
		return "group info";
	}

	@Override
	public String getArgumentName() {
		return "info";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_GROUP_INFO_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_GROUP_INFO_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_GROUP_INFO;
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
