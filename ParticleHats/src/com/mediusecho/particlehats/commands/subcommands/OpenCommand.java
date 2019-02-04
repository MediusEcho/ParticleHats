package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.CommandPermission;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.ui.Menu;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.ui.StaticMenu;

public class OpenCommand extends Command {

	private final Database database;
	
	public OpenCommand (final Core core)
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
		// No argument
		if (args.size() == 0)
		{
			sender.sendMessage("&cMissing menu name");
			return false;
		}
		
		if (args.size() == 1)
		{
			// Just grab the name without any extensions and manually add the extension
			String menuName = (args.get(0).contains(".") ? args.get(0).split("\\.")[0] : args.get(0));
			
			Database database = core.getDatabase();
			MenuInventory inventory = database.loadInventory(menuName);
			
			if (inventory != null)
			{
				Menu menu = new StaticMenu(core, sender.getPlayer(), inventory);
				menu.open();
			}
			
			else
			{
				sender.sendMessage("Unable to find '" + menuName + "'");
			}
		}
		return false;
	}

	@Override
	public String getName() {
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
	public CommandPermission getPermission() {
		return CommandPermission.ALL;
	}

}
