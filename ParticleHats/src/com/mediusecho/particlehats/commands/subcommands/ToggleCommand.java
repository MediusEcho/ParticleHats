package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.util.StringUtil;

public class ToggleCommand extends Command {

	private final TogglePlayerCommand togglePlayerCommand;
	
	public ToggleCommand ()
	{
		togglePlayerCommand = new TogglePlayerCommand();
		register(togglePlayerCommand);
	}
	
	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{
		if (args.size() < 1)
		{
			sender.sendMessage(Message.COMMAND_ERROR_ARGUMENTS);
			sender.sendMessage(getUsage());
			return false;
		}
		
		if (args.size() > 1) {
			return togglePlayerCommand.onCommand(core, sender, label, args);
		}
		
		boolean toggleStatus = StringUtil.getToggleValue(args.get(0));
		EntityState entityState = core.getPlayerState(sender.getPlayer());
		
		for (Hat hat : entityState.getActiveHats()) {
			hat.setHidden(!toggleStatus);
		}
		
		if (toggleStatus) {
			sender.sendMessage(Message.COMMAND_TOGGLE_ON);
		} else {
			sender.sendMessage(Message.COMMAND_TOGGLE_OFF);
		}
		
		return true;
	}
	
	@Override
	public List<String> tabComplete (ParticleHats core, Sender sender, String label, ArrayList<String> args)
	{
		if (args.size() == 2) {
			return togglePlayerCommand.onTabComplete(core, sender, label, args);
		}
		return Arrays.asList("on", "off");
	}
	
	@Override
	public boolean onCommand (ParticleHats core, Sender sender, String label, ArrayList<String> args)
	{
		if (args.size() == 2) {
			return togglePlayerCommand.onCommand(core, sender, label, args);
		}
		return super.onCommand(core, sender, label, args);
	}

	@Override
	public String getName() {
		return "Toggle";
	}

	@Override
	public String getArgumentName() {
		return "toggle";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_TOGGLE_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_TOGGLE_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_TOGGLE;
	}

	@Override
	public boolean showInHelp() {
		return true;
	}

	@Override
	public boolean isPlayerOnly() {
		return true;
	}
}
