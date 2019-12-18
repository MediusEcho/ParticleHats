package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.util.StringUtil;

public class TogglePlayerCommand extends Command {

	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{
		if (args.size() != 2)
		{
			sender.sendMessage(Message.COMMAND_ERROR_ARGUMENTS);
			sender.sendMessage(getUsage());
			return false;
		}
		
		Player player = getPlayer(sender, args.get(1));
		if (player == null)
		{
			sender.sendMessage(Message.COMMAND_ERROR_UNKNOWN_PLAYER.getValue().replace("{1}", args.get(1)));
			return false;
		}
		
		if (!player.isOnline())
		{
			sender.sendMessage(Message.COMMAND_ERROR_OFFLINE_PLAYER.getValue().replace("{1}", player.getName()));
			return false;
		}
		
		boolean toggleStatus = StringUtil.getToggleValue(args.get(0));
		EntityState entityState = core.getPlayerState(player);
		
		for (Hat hat : entityState.getActiveHats()) {
			hat.setHidden(!toggleStatus);
		}
		
		if (toggleStatus) {
			sender.sendMessage(Message.COMMAND_TOGGLE_PLAYER_ON.getValue().replace("{1}", player.getName()));
		} else {
			sender.sendMessage(Message.COMMAND_TOGGLE_PLAYER_OFF.getValue().replace("{1}", player.getName()));
		}
		
		return true;
	}
	
	@Override
	public List<String> tabComplete (ParticleHats core, Sender sender, String label, ArrayList<String> args)
	{
		if (args.size() == 2)
		{
			List<String> players = new ArrayList<String>();
			for (Player p : Bukkit.getOnlinePlayers()) {
				players.add(p.getName());
			}
			
			if (Permission.COMMAND_SELECTORS.hasPermission(sender))
			{
				players.add("@p");
				players.add("@r");
			}
			
			return players;
		}
		return Arrays.asList("");
	}

	@Override
	public String getName() {
		return "toggle player";
	}

	@Override
	public String getArgumentName() {
		return "player";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_TOGGLE_PLAYER_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_TOGGLE_PLAYER_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_TOGGLE_PLAYER;
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
