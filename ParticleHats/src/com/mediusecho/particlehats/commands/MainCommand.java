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
import com.mediusecho.particlehats.ui.StaticMenu;
import com.mediusecho.particlehats.ui.StaticMenuManager;
import com.mediusecho.particlehats.ui.menus.Menu;
import com.mediusecho.particlehats.ui.properties.MenuInventory;

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
			String defaultMenu = "";
			boolean usingGroupMenu = false;
			
			// Check for a players group menu first
			for (Group g : groups)
			{
				if (sender.hasPermission(Permission.GROUP.append(g.getName()))) 
				{
					usingGroupMenu = true;
					defaultMenu = g.getDefaultMenu();
				}
			}
			
			// Use default menu if nothing was found
			if (defaultMenu.equals("")) {
				defaultMenu = SettingsManager.DEFAULT_MENU.getString();
			}
			
			String menuName = defaultMenu.contains(".") ? defaultMenu.split("\\.")[0] : defaultMenu;
			Database database = core.getDatabase();
			PlayerState playerState = core.getPlayerState(sender.getPlayer());
			MenuInventory inventory = database.loadInventory(menuName, playerState);
			
			if (inventory == null)
			{
				if (usingGroupMenu) {
					sender.sendMessage(Message.COMMAND_ERROR_UNKNOWN_GROUP_MENU.getValue().replace("{1}", menuName));
				} else {
					sender.sendMessage(Message.COMMAND_ERROR_UNKNOWN_MENU.getValue().replace("{1}", menuName));
				}
				return false;
			}
			
			StaticMenuManager staticManager = core.getMenuManagerFactory().getStaticMenuManager(playerState);
			Menu menu = new StaticMenu(core, staticManager, sender.getPlayer(), inventory);
			
			staticManager.addMenu(menu);
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
