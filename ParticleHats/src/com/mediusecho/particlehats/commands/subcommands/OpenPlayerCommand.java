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
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.StaticMenuManager;
import com.mediusecho.particlehats.ui.menus.Menu;

public class OpenPlayerCommand extends Command {
	
	private final OpenCommand parent;
	
	public OpenPlayerCommand (final OpenCommand parent)
	{
		this.parent = parent;
	}

	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{
		if (args.size() <= 1)
		{
			sender.sendMessage(getUsage());
			return false;
		}
		
		Player targetPlayer = getPlayer(sender, args.get(1));
		if (targetPlayer == null)
		{
			sender.sendMessage(Message.COMMAND_ERROR_UNKNOWN_PLAYER.getValue().replace("{1}", args.get(1)));
			return false;
		}
		
		if (!targetPlayer.isOnline())
		{
			sender.sendMessage(Message.COMMAND_ERROR_OFFLINE_PLAYER.getValue().replace("{1}", targetPlayer.getName()));
			return false;
		}
		
		PlayerState playerState = core.getPlayerState(targetPlayer.getPlayer());
		if (playerState.hasEditorOpen()) 
		{
			sender.sendMessage(Message.COMMAND_OPEN_PLAYER_EDITING.replace("{1}", targetPlayer.getName()));
			return false;
		}
		
		Menu menu = parent.getRequestedMenu(playerState, args.get(0), sender);
		if (menu == null) {
			return false;
		}
		
		StaticMenuManager staticManager = (StaticMenuManager)playerState.getMenuManager();
		staticManager.addMenu(menu);
		
		menu.open();
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
		return "open menu for player";
	}

	@Override
	public String getArgumentName() {
		return "player";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_OPEN_PLAYER_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_OPEN_PLAYER_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_OPEN_PLAYER;
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
