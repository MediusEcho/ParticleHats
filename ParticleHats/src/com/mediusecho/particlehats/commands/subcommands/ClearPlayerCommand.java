package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;

public class ClearPlayerCommand extends Command {

	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{		
		Player player = getPlayer(sender, args.get(0));
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
		return "clear player";
	}
	
	@Override
	public String getArgumentName () {
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
	public boolean hasWildcardPermission () {
		return true;
	}
	
	@Override
	public Permission getWildcardPermission () {
		return Permission.COMMAND_CLEAR_ALL;
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
