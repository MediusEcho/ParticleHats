package com.mediusecho.particlehats.commands.subcommands;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UnsetCommand extends Command {

	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{		
		if (args.size() < 2 || args.size() > 3)
		{
			sender.sendMessage(Message.COMMAND_ERROR_ARGUMENTS);
			sender.sendMessage(Message.COMMAND_UNSET_USAGE);
			return false;
		}
		
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

		boolean tellPlayer = true;
		if (args.size() >= 3) {
			tellPlayer = Boolean.valueOf(args.get(2));
		}

		String hatLabel = args.get(1);
		
		// Check to see if this player is wearing a hat with this label
		PlayerState playerState = core.getPlayerState(player.getPlayer());
		for (Hat h : playerState.getActiveHats())
		{
			if (!h.getLabel().equalsIgnoreCase(hatLabel))
			{
				sender.sendMessage(Message.COMMAND_UNSET_NOT_WEARING.getValue().replace("{1}", player.getName()));
				return false;
			}
		}
		
		Database database = core.getDatabase();
		Hat hat = database.getHatFromLabel(hatLabel);
		
		if (hat == null)
		{
			sender.sendMessage(Message.COMMAND_UNSET_LABEL_ERROR.getValue().replace("{1}", hatLabel));
			return false;
		}

		core.getPlayerState(player).removeHat(hat);
		if (tellPlayer) {
			player.sendMessage(Message.COMMAND_UNSET_SUCCESS.getValue().replace("{1}", hat.getDisplayName()));
		}
		
		return true;
	}
	
	@Override
	public List<String> tabComplete (ParticleHats core, Sender sender, String label, ArrayList<String> args)
	{
		switch (args.size())
		{
			case 1:
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
			
			case 2:
			{
				List<String> labels = new ArrayList<String>();
				for (Hat h : core.getPlayerState(sender.getPlayer()).getActiveHats()) {
					labels.add(h.getLabel());
				}
				return labels;
			}
			
			case 3:
			{
				return Arrays.asList("true", "false");
			}
		}
		return Collections.singletonList("");
	}

	@Override
	public String getName() {
		return "unset";
	}
	
	@Override
	public String getArgumentName () {
		return "unset";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_UNSET_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_UNSET_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_UNSET;
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
