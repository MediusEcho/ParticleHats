package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.CommandPermission;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
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
		return database.getMenus(false);
	}

	@Override
	public boolean execute(Core core, Sender sender, String label, ArrayList<String> args) 
	{
		if (!sender.isPlayer())
		{
			return false;
		}
		
		if (args.size() != 1)
		{
			return false;
		}
		
		String menuName = (args.get(0).contains(".") ? args.get(0).split("\\.")[0] : args.get(0));
		if (!core.getDatabase().getMenus(false).contains(menuName))
		{
			sender.sendMessage("&cThis menu does not exist");
			return false;
		}
		
		PlayerState playerState = core.getPlayerState(sender.getPlayerID());
		MenuBuilder menuBuilder = playerState.getMenuBuilder();
		MenuInventory inventory = database.loadInventory(menuName);
		
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
	public CommandPermission getPermission() {
		return CommandPermission.EDIT;
	}
}
