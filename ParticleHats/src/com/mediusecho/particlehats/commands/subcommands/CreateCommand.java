package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.CommandPermission;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.MenuInventory;

public class CreateCommand extends Command {
	
	@Override
	public boolean execute(Core core, Sender sender, String label, ArrayList<String> args) 
	{
		if (args.size() == 1)
		{
			String menuName = (args.get(0).contains(".") ? args.get(0).split("\\.")[0] : args.get(0));
			Database database = core.getDatabase();
			
			if (database.menuExists(menuName))
			{
				sender.sendMessage(Message.COMMAND_ERROR_MENU_EXISTS);
				return false;
			}
			database.createEmptyMenu(menuName);
			
			PlayerState playerState = core.getPlayerState(sender.getPlayerID());
			MenuBuilder menuBuilder = playerState.getMenuBuilder();
			MenuInventory inventory = new MenuInventory(menuName, menuName, 6);
			
			if (menuBuilder == null) 
			{
				menuBuilder = new MenuBuilder(core, sender.getPlayer(), playerState, inventory);
				playerState.setMenuBuilder(menuBuilder);
			}
			
			menuBuilder.startEditing();
		}
		return false;
	}

	@Override
	public String getName() {
		return "create";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_CREATE_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_CREATE_DESCRIPTION;
	}

	// TOOD: Change permission
	@Override
	public CommandPermission getPermission() {
		return CommandPermission.CREATE;
	}
	
	@Override
	public boolean showInHelp() {
		return true;
	}

}
