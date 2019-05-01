package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.MenuInventory;

public class EditCommand extends Command {

	private final Database database;
	
	public EditCommand (final Core core)
	{
		database = core.getDatabase();
	}
	
	@Override
	public List<String> tabCompelete (Core core, Sender sender, String label, ArrayList<String> args)
	{
		if (args.size() == 1) 
		{
			Set<String> menus = database.getMenus(false).keySet();
			List<String> result = new ArrayList<String>();
			
			if (sender.hasPermission(getPermission()))
			{
				for (String menu : menus)
				{
					if (sender.hasPermission(getPermission().append(menu)) || sender.hasPermission(getPermission().append("all"))) {
						result.add(menu);
					}
				}
			}
			
			return result;
		}
		return Arrays.asList("");
	}

	@Override
	public boolean execute(Core core, Sender sender, String label, ArrayList<String> args) 
	{
		if (args.size() < 1) 
		{
			sender.sendMessage(Message.COMMAND_ERROR_ARGUMENTS);
			sender.sendMessage(Message.COMMAND_EDIT_USAGE);
			return false;
		}
		
		String menuName = (args.get(0).contains(".") ? args.get(0).split("\\.")[0] : args.get(0));
		if (!sender.hasPermission(getPermission().append(menuName)) && !sender.hasPermission(Permission.COMMAND_EDIT_ALL))
		{
			sender.sendMessage(Message.COMMAND_ERROR_NO_PERMISSION);
			return false;
		}
		
		if (menuName.equalsIgnoreCase("purchase"))
		{
			// TODO: edit purchase menu
		}
		
		if (!core.getDatabase().menuExists(menuName))
		{
			sender.sendMessage(Message.COMMAND_ERROR_UNKNOWN_MENU.replace("{1}", menuName));
			return false;
		}
		
		PlayerState playerState = core.getPlayerState(sender.getPlayerID());
		MenuBuilder menuBuilder = playerState.getMenuBuilder();
		MenuInventory inventory = database.loadInventory(menuName, playerState);
		
		if (inventory == null) {
			return false;
		}
		
		if (menuBuilder == null) 
		{
			menuBuilder = new MenuBuilder(core, sender.getPlayer(), playerState, inventory);
			playerState.setMenuBuilder(menuBuilder);
		}
		
		menuBuilder.startEditing();
		return false;
	}

	@Override
	public String getName() {
		return "edit menu";
	}
	
	@Override
	public String getArgumentName () {
		return "edit";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_EDIT_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_EDIT_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_EDIT;
	}
	
	@Override
	public boolean showInHelp() {
		return true;
	}
	
	@Override
	public boolean isPlayerOnly() {
		return true;
	}
}
