package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.configuration.CustomConfig;
import com.mediusecho.particlehats.database.type.DatabaseType;
import com.mediusecho.particlehats.database.type.mysql.MySQLDatabase;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;

public class ImportCommand extends Command {

	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{
		if (args.size() < 1)
		{
			sender.sendMessage(Message.COMMAND_ERROR_ARGUMENTS);
			sender.sendMessage(Message.COMMAND_IMPORT_USAGE);
			return false;
		}
		
		if (core.getDatabaseType().equals(DatabaseType.YAML))
		{
			sender.sendMessage(Message.COMMAND_ADD_TYPE_ERROR);
			return false;
		}
		
		String menuName = args.get(0);
		if (core.getDatabase().getMenus(false).containsKey(menuName))
		{
			sender.sendMessage(Message.COMMAND_ERROR_MENU_EXISTS.replace("{1}", menuName));
			return false;
		}
		
		MySQLDatabase database = (MySQLDatabase)core.getDatabase();
		CustomConfig config = core.getResourceManager().getConfig(menuName);
		
		if (config == null)
		{
			sender.sendMessage(Message.COMMAND_ERROR_UNKNOWN_MENU.replace("{1}", menuName));
			return false;
		}
		
		database.importMenu(sender, config);
		
		//sender.sendMessage(Message.COMMAND_IMPORT_SUCCESS.replace("{1}", menuName));
		return true;
	}
	
	@Override
	public List<String> tabCompelete (ParticleHats core, Sender sender, String label, ArrayList<String> args)
	{
		if (args.size() == 1)
		{
			List<String> menus = new ArrayList<String>();
			for (String menu : core.getResourceManager().getMenus()) {
				menus.add(menu);
			}
			return menus;
		}
		return Arrays.asList("");
	}

	@Override
	public String getName() {
		return "import menu";
	}

	@Override
	public String getArgumentName() {
		return "import";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_IMPORT_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_IMPORT_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_IMPORT;
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
