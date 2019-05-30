package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.CommandManager;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class BukkitHelpCommand extends Command {

	protected final ParticleHats core;
	protected int pages;
	
	private Map<Integer, String> commands;
	
	public BukkitHelpCommand (ParticleHats core, CommandManager commandManager)
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
				String entry = builder.append("&7> &3").append(cmd.getUsage().getRawValue()).append(" &7").append(cmd.getDescription().getRawValue()).toString();
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
		sender.sendMessage("&f> &6ParticleHats v" + core.getDescription().getVersion());
		for (Entry<Integer, String> entry : commands.entrySet()) {
			sender.sendMessage(entry.getValue());
		}
	}
	
	/**
	 * Prints out all commands to the console
	 * @param sender
	 */
	protected void readPage (CommandSender sender)
	{
		sender.sendMessage(StringUtil.colorize("&f> &6ParticleHats v" + core.getDescription().getVersion()));
		for (Entry<Integer, String> cmd : commands.entrySet()) {
			sender.sendMessage(cmd.getValue());
		}
	}
	
	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
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
	public String getName() {
		return "help";
	}
	
	@Override
	public String getArgumentName () {
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
	public Permission getPermission() {
		return Permission.COMMAND_HELP;
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
