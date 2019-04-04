package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.CommandPermission;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.locale.Message;

public class ClearCommand extends Command {

	private final ClearPlayerCommand clearPlayerCommand;
	
	public ClearCommand ()
	{
		clearPlayerCommand = new ClearPlayerCommand();
		register(clearPlayerCommand);
	}
	
	@Override
	public boolean execute(Core core, Sender sender, String label, ArrayList<String> args) 
	{
		// Self
		if (args.size() == 0)
		{
			if (!sender.isPlayer())
			{
				sender.sendMessage(Message.COMMAND_ERROR_PLAYER_ONLY);
				return false;
			}
			
			if (!sender.hasPermission(getPermission()))
			{
				sender.sendMessage(Message.COMMAND_ERROR_NO_PERMISSION);
				return false;
			}
			
			core.getPlayerState(sender.getPlayerID()).clearActiveHats();
			sender.sendMessage(Message.COMMAND_CLEAR_SUCCESS);
			return true;
		}
		
		else {
			return clearPlayerCommand.execute(core, sender, label, args);
		}
	}
	
	@Override
	public List<String> tabCompelete (Core core, Sender sender, String label, ArrayList<String> args)
	{
		if (args.size() == 1) 
		{
			if (sender.hasPermission(clearPlayerCommand.getPermission()))
			{
				List<String> players = new ArrayList<String>();
				for (Player p : Bukkit.getOnlinePlayers()) {
					players.add(p.getName());
				}
				return players;
			}
		}
		return Arrays.asList("");
	}

	@Override
	public String getName() {
		return "clear";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_CLEAR_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_CLEAR_DESCRIPTION;
	}

	@Override
	public CommandPermission getPermission() {
		return CommandPermission.CLEAR;
	}
	
	@Override
	public boolean showInHelp() {
		return true;
	}

}