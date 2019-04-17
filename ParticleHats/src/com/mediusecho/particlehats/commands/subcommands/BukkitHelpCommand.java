package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.CommandPermission;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.CommandManager;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class BukkitHelpCommand extends Command {

	protected final Core core;
	protected int pages;
	
	private Map<Integer, String> commands;
	
	public BukkitHelpCommand (Core core, CommandManager commandManager)
	{
		this.core = core;
		commands = new HashMap<Integer, String>();
		
		int commandIndex = 0;	
		StringBuilder builder = new StringBuilder();
		
		for (Entry<String, Command> cmds : commandManager.getCommands().entrySet())
		{
			Command cmd = cmds.getValue();
			if (cmd != null) 
			{
				String entry = builder.append("&3").append(cmd.getUsage().getRawValue()).append(" &7").append(cmd.getDescription().getRawValue()).toString();
				commands.put(commandIndex++, StringUtil.colorize(entry));
				
				builder.setLength(0);
			}
		}
		pages = (int) Math.ceil((double) commandIndex / 9D);
	}
	
	/***
	 * 
	 * @param sender
	 * @param page
	 */
	protected void readPage (Sender sender, int page)
	{
		sender.sendMessage(">- &6ParticleHats v" + core.getDescription().getVersion());
		
		int range = page * 9;
		for (int i = range; i < (range + 9); i++)
		{
			if (commands.containsKey(i)) {
				sender.sendMessage(commands.get(i));
			}
		}
		
		sender.sendMessage(">- &6" + (page + 1) + "&7/&6" + pages);
	}
	
	/**
	 * Prints out all commands to the console
	 * @param sender
	 */
	protected void readPage (CommandSender sender)
	{
		sender.sendMessage(StringUtil.colorize(">- &6ParticleHats v" + core.getDescription().getVersion()));
		for (Entry<Integer, String> cmd : commands.entrySet()) {
			sender.sendMessage(cmd.getValue());
		}
	}
	
	@Override
	public boolean execute(Core core, Sender sender, String label, ArrayList<String> args) 
	{
		if (!sender.hasPermission(getPermission()))
		{
			sender.sendMessage(Message.COMMAND_ERROR_NO_PERMISSION);
			return false;
		}
		
		int targetPage = 0;
		if (args.size() >= 1) {
			targetPage = MathUtil.valueOf(args.get(0));
		}
		
		if (sender.isPlayer()) {
			readPage(sender, targetPage);
		} else {
			readPage(sender.getCommandSender());
		}
		
		return true;
	}
	
	@Override
	public List<String> tabCompelete (Core core, Sender sender, String label, ArrayList<String> args)
	{
		if (args.size() == 1) {
			return Arrays.asList("page");
		}
		return Arrays.asList("");
	}

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_HELP_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_HELP_DESCRIPTION;
	}

	@Override
	public CommandPermission getPermission() {
		return CommandPermission.HELP;
	}

	@Override
	public boolean showInHelp() {
		return false;
	}
	
	@Override
	public boolean isPlayerOnly() {
		return false;
	}

}
