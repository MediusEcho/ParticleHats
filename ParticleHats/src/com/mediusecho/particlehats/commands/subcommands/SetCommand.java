package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.PlayerState;

public class SetCommand extends Command {

	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{		
		if (args.size() < 2 || args.size() > 4)
		{
			sender.sendMessage(Message.COMMAND_ERROR_ARGUMENTS);
			sender.sendMessage(Message.COMMAND_SET_USAGE);
			return false;
		}
		
		Player player = getPlayer(sender, args.get(0));//Bukkit.getPlayer(args.get(0));
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
		
		boolean permanent = true;
		if (args.size() >= 3) {
			permanent = Boolean.valueOf(args.get(2));
		}
		
		boolean tellPlayer = true;
		if (args.size() >= 4) {
			tellPlayer = Boolean.valueOf(args.get(3));
		}
		
		boolean logError = true;
		if (args.size() >= 5) {
			logError = Boolean.valueOf(args.get(4));
		}
		
		String hatLabel = args.get(1);
		
		// Check to see if this player is already wearing a hat with this label
		PlayerState playerState = core.getPlayerState(player.getUniqueId());
		for (Hat h : playerState.getActiveHats())
		{
			if (h.getLabel().equalsIgnoreCase(hatLabel))
			{
				if (logError) {
					sender.sendMessage(Message.COMMAND_SET_ALREADY_SET.getValue().replace("{1}", player.getName()));
				}
				return false;
			}
		}
		
		Database database = core.getDatabase();
		Hat hat = database.getHatFromLabel(hatLabel);
		
		if (hat == null)
		{
			sender.sendMessage(Message.COMMAND_SET_LABEL_ERROR.getValue().replace("{1}", hatLabel));
			return false;
		}
		
		hat.setPermanent(permanent);
		core.getParticleManager().equipHat(player.getUniqueId(), hat, false);
		
		if (tellPlayer) {
			player.sendMessage(Message.COMMAND_SET_SUCCESS.getValue().replace("{1}", hat.getDisplayName()));
		}
		
		return true;
	}
	
	@Override
	public List<String> tabCompelete (ParticleHats core, Sender sender, String label, ArrayList<String> args)
	{
		switch (args.size())
		{
			case 1:
			{
				List<String> players = new ArrayList<String>();
				for (Player p : Bukkit.getOnlinePlayers()) {
					players.add(p.getName());
				}
				players.add("@p");
				
				return players;
			}
			
			case 2:
			{
				return core.getDatabase().getLabels(false);
			}
			
			case 3:
			case 4:
			case 5:
			{
				return Arrays.asList("true", "false");
			}
		}
		return Arrays.asList("");
	}

	@Override
	public String getName() {
		return "set";
	}
	
	@Override
	public String getArgumentName () {
		return "set";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_SET_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_SET_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_SET;
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
