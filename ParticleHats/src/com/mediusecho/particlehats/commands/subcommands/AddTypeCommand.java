package com.mediusecho.particlehats.commands.subcommands;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.database.type.DatabaseType;
import com.mediusecho.particlehats.database.type.mysql.MySQLDatabase;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;

public class AddTypeCommand extends Command {

	@Override
	public boolean execute(Core core, Sender sender, String label, ArrayList<String> args) 
	{
		if (args.size() < 1)
		{
			sender.sendMessage(Message.COMMAND_ERROR_ARGUMENTS);
			sender.sendMessage(getUsage());
			return false;
		}
		
		if (core.getDatabaseType().equals(DatabaseType.YAML))
		{
			sender.sendMessage(Message.COMMAND_ADD_TYPE_ERROR);
			return false;
		}
		
		String imageName = args.get(0);
		if (core.getDatabase().getImages(false).containsKey(imageName))
		{
			sender.sendMessage(Message.COMMAND_ERROR_TYPE_EXISTS.replace("{1}", imageName));
			return false;
		}
		
		MySQLDatabase database = (MySQLDatabase)core.getDatabase();
		BufferedImage image = core.getResourceManager().getImages().get(imageName);
		
		if (database.insertImage(imageName, image))
		{
			sender.sendMessage(Message.COMMAND_ADD_TYPE_SUCCESS.replace("{1}", imageName));
			return true;
		}
		
		return false;
	}
	
	@Override
	public List<String> tabCompelete (Core core, Sender sender, String label, ArrayList<String> args)
	{
		if (args.size() == 1)
		{
			List<String> images = new ArrayList<String>();
			Map<String, BufferedImage> imageCache = core.getDatabase().getImages(false);
			
			for (Entry<String, BufferedImage> entry : core.getResourceManager().getImages().entrySet())
			{
				if (!imageCache.containsKey(entry.getKey())) {
					images.add(entry.getKey());
				}
			}
			
			return images;
		}
		return Arrays.asList("");
	}

	@Override
	public String getName() {
		return "add type";
	}
	
	@Override
	public String getArgumentName () {
		return "add";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_ADD_TYPE_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_ADD_TYPE_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_TYPE_ADD;
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
