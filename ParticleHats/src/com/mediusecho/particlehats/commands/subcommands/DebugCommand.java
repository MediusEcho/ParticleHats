package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.database.properties.Group;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;

public class DebugCommand extends Command {

	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{
		List<Group> groups = new ArrayList<Group>();
		groups.add(new Group("test", "hello", 5));
		groups.add(new Group("test_1", "world", 1));
		groups.add(new Group("default", "particles", 100));
		
		Collections.sort(groups, (g1, g2) -> {
			return g1.getWeight() > g2.getWeight() ? 1 : -1;
		});
		
		for (Group g : groups) {
			ParticleHats.debug("name: " + g.getName() + " menu: " + g.getDefaultMenu() + " weight: " + g.getWeight());
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
