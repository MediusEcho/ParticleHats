package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.GuiState;
import com.mediusecho.particlehats.ui.Menu;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.ui.StaticMenu;

public class OpenCommand extends Command {

	private final ParticleHats core;
	private final Database database;
	
	private OpenPlayerCommand openPlayerCommand;
	
	public OpenCommand (final ParticleHats core)
	{			
		this.core = core;
		database = core.getDatabase();
		
		openPlayerCommand = new OpenPlayerCommand(this);
		register(openPlayerCommand);
	}
	
	@Override
	public List<String> tabComplete (ParticleHats core, Sender sender, String label, ArrayList<String> args)
	{
		if (args.size() == 1) 
		{
			Set<String> menus = database.getMenus(false).keySet();
			List<String> result = new ArrayList<String>();

			for (String menu : menus)
			{
				if (hasPermission(sender, menu)) {
					result.add(menu);
				}
			}
			
			return result;
		}
		
		else if (args.size() == 2) {
			return openPlayerCommand.onTabComplete(core, sender, label, args);
		}
		return Arrays.asList("");
	}
	
	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{		
		// No argument
		if (args.size() == 0)
		{
			sender.sendMessage(Message.COMMAND_ERROR_ARGUMENTS);
			sender.sendMessage(Message.COMMAND_OPEN_USAGE);
			return false;
		}
		
		if (args.size() == 1)
		{
			if (!sender.isPlayer())
			{
				sender.sendMessage(Message.COMMAND_ERROR_PLAYER_ONLY);
				return false;
			}
			
			PlayerState playerState = core.getPlayerState(sender.getPlayer());
			
			if (playerState.isEditing()) 
			{
				sender.sendMessage(Message.COMMAND_ERROR_ALREADY_EDITING);
				return false;
			}
			
			Menu menu = getRequestedMenu(playerState, args.get(0), sender);
			if (menu == null) {
				return false;
			}
			
			playerState.setGuiState(GuiState.SWITCHING_MENU);
			playerState.setOpenMenu(menu);
			menu.open();
			
			return true;
		}
		
		else {
			return openPlayerCommand.onCommand(core, sender, label, args);
		}
	}

	@Override
	public String getName() {
		return "open menu";
	}
	
	@Override
	public String getArgumentName () {
		return "open";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_OPEN_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_OPEN_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_OPEN;
	}
	
	@Override
	public boolean hasWildcardPermission () {
		return true;
	}
	
	@Override
	public Permission getWildcardPermission () {
		return Permission.COMMAND_OPEN_ALL;
	}
	
	@Override
	public boolean showInHelp() {
		return true;
	}

	@Override
	public boolean isPlayerOnly() {
		return false;
	}
	
	@Override
	public boolean hasPermission (Sender sender)
	{
		if (!sender.isPlayer()) {
			return true;
		}
		
		// /h wild card check
		if (Permission.COMMAND_ALL.hasPermission(sender)) {
			return true;
		}
		
		// Specific command wild card check
		if (hasWildcardPermission())
		{
			if (getWildcardPermission().hasPermission(sender)) {
				return true;
			}
		}
		
		// Check for individual menu permissions
		for (String menu : core.getDatabase().getMenus(false).keySet())
		{
			if (sender.hasPermission(getPermission().append(menu))) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean hasPermission (Sender sender, String arg)
	{
		if (!sender.isPlayer()) {
			return true;
		}
		
		// /h wild card check
		if (Permission.COMMAND_ALL.hasPermission(sender)) {
			return true;
		}
				
		// Specific command wild card check
		if (hasWildcardPermission())
		{
			if (getWildcardPermission().hasPermission(sender)) {
				return true;
			}
		}
		
		if (arg != null && !arg.equals(""))
		{
			if (sender.hasPermission(getPermission().append(arg))) {
				return true;
			}
		}
		
		return false;
	}
	
	public Menu getRequestedMenu (PlayerState playerState, String requestedMenuName, Sender sender)
	{
		// Grab the name without any extensions
		String menuName = (requestedMenuName.contains(".") ? requestedMenuName.split("\\.")[0] : requestedMenuName);
		
		if (menuName.equals("purchase")) 
		{
			sender.sendMessage(Message.COMMAND_OPEN_ERROR.replace("{1}", menuName));
			return null;
		}
		
		Menu menu = playerState.getOpenMenu(menuName);
		if (menu == null)
		{
			ParticleHats.debug("cache didnt exist, loading menu " + menuName);
			MenuInventory inventory = core.getDatabase().loadInventory(menuName, playerState);
			
			if (inventory == null)
			{
				sender.sendMessage(Message.COMMAND_ERROR_UNKNOWN_MENU.replace("{1}", menuName));
				return null;
			}
			
			menu = new StaticMenu(core, playerState.getOwner(), inventory);
		}
		
		return menu;
	}
}
