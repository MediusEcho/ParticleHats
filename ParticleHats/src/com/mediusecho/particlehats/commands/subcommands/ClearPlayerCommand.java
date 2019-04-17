package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.CommandPermission;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.locale.Message;

public class ClearPlayerCommand extends Command {

	@Override
	public boolean execute(Core core, Sender sender, String label, ArrayList<String> args) 
	{
		if (!sender.hasPermission(getPermission()))
		{
			sender.sendMessage(Message.COMMAND_ERROR_NO_PERMISSION);
			return false;
		}
		
		Player player = Bukkit.getPlayer(args.get(0));
		if (player == null)
		{
			sender.sendMessage(Message.COMMAND_ERROR_UNKNOWN_PLAYER.getValue().replace("{1}", args.get(0)));
			return false;
		}
		
		if (!player.isOnline())
		{
			sender.sendMessage(Message.COMMAND_ERROR_OFFLINE_PLAYER.getValue().replace("{1}", player.getName()));
			return false;
		}
		
		core.getPlayerState(player.getUniqueId()).clearActiveHats();
		sender.sendMessage(Message.COMMAND_CLEAR_PLAYER_SUCCESS.getValue().replace("{1}", player.getName()));
		return true;
	}

	@Override
	public String getName() {
		return "player";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_CLEAR_PLAYER_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_CLEAR_PLAYER_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_CLEAR_PLAYER;
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
