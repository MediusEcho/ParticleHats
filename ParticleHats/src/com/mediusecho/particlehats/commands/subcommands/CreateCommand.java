package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.MenuInventory;

public class CreateCommand extends Command {
	
	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{
		if (args.size() == 1)
		{
			PlayerState playerState = core.getPlayerState(sender.getPlayerID());
			
			if (playerState.isEditing()) 
			{
				sender.sendMessage(Message.COMMAND_ERROR_ALREADY_EDITING);
				return false;
			}
			
			String menuName = (args.get(0).contains(".") ? args.get(0).split("\\.")[0] : args.get(0));
			Database database = core.getDatabase();
			
			// "purchase" is a reserved menu name, used for the plugin's purchase menu
			if (database.menuExists(menuName) || menuName.equalsIgnoreCase("purchase"))
			{
				sender.sendMessage(Message.COMMAND_ERROR_MENU_EXISTS.getValue().replace("{1}", menuName));
				return false;
			}
			database.createMenu(menuName);
			
			if (!sender.isPlayer())
			{
				sender.sendMessage(Message.COMMAND_CREATE_SUCCESS.replace("{1}", menuName));
				return true;
			}
			
			MenuBuilder menuBuilder = playerState.getMenuBuilder();
			MenuInventory inventory = new MenuInventory(menuName, menuName, 6, null);
			
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
		return "create menu";
	}
	
	@Override
	public String getArgumentName () {
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
	public Permission getPermission() {
		return Permission.COMMAND_CREATE;
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
