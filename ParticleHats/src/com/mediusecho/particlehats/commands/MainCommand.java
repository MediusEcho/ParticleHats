package com.mediusecho.particlehats.commands;

import java.util.ArrayList;
import java.util.List;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.database.properties.Group;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.GuiState;
import com.mediusecho.particlehats.ui.Menu;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.ui.StaticMenu;

public class MainCommand extends Command {

	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{
		// Execute this command
		if (args.size() == 0)
		{
			if (!sender.isPlayer())
			{
				sender.sendMessage(Message.COMMAND_ERROR_PLAYER_ONLY);
				return false;
			}
			
			List<Group> groups = core.getDatabase().getGroups(true);
			String defaultMenu = SettingsManager.DEFAULT_MENU.getString();
			
			for (Group g : groups)
			{
				if (sender.hasPermission(Permission.GROUP.append(g.getName()))) {
					defaultMenu = g.getDefaultMenu();
				}
			}
			
			String menuName = defaultMenu.contains(".") ? defaultMenu.split("\\.")[0] : defaultMenu;
			Database database = core.getDatabase();
			PlayerState playerState = core.getPlayerState(sender.getPlayer());
			MenuInventory inventory = database.loadInventory(menuName, playerState);
			
			if (inventory == null)
			{
				sender.sendMessage(Message.COMMAND_ERROR_UNKNOWN_MENU.getValue().replace("{1}", menuName));
				return false;
			}
			
			Menu menu = new StaticMenu(core, sender.getPlayer(), inventory);
			
			playerState.setGuiState(GuiState.SWITCHING_MENU);
			playerState.setOpenMenu(menu);
			menu.open();
			
			return true;
		}
		
		// Find and execute our sub command
		else 
		{
			String cmd = args.get(0);
			if (!subCommands.containsKey(cmd))
			{
				sender.sendMessage(Message.COMMAND_ERROR_UNKNOWN);
				return false;
			}
			
			args.remove(0);
			return subCommands.get(cmd).onCommand(core, sender, label, args);
		}
	}

	@Override
	public String getName() {
		return "main";
	}
	
	@Override
	public String getArgumentName () {
		return "h";
	}
	
	@Override 
	public Message getUsage () {
		return Message.COMMAND_MAIN_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_MAIN_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_MAIN;
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
