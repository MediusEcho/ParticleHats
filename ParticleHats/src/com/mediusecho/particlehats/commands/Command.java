package com.mediusecho.particlehats.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;

public abstract class Command {
	
	/**
	 * Determine if we can include this command in the help menu
	 */
	protected boolean visible = true;
	
	/**
	 * Keep track of which sub commands belong to this command
	 */
	protected Map<String, Command> subCommands;
	
	public Command ()
	{		
		subCommands = new LinkedHashMap<String, Command>();
	}
	
	/**
	 * Generic command execute method
	 * @param plugin
	 * @param sender
	 * @param label
	 * @param args
	 * @return
	 */
	public abstract boolean execute (Core core, Sender sender, String label, ArrayList<String> args);
	
	/**
	 * Generic tab complete method
	 * @param plugin
	 * @param sender
	 * @param label
	 * @param args
	 * @return
	 */
	public List<String> tabCompelete (Core core, Sender sender, String label, ArrayList<String> args)
	{
		if (args.size() == 1)
		{
			List<String> arguments = new ArrayList<String>();
			for (Entry<String, Command> entry : subCommands.entrySet())
			{
				if (sender.hasPermission(entry.getValue().getPermission())) {
					arguments.add(entry.getKey());
				}
			}
			return arguments;
		}
		
		else
		{
			String cmd = args.get(0);
			if (subCommands.containsKey(cmd))
			{
				args.remove(0);
				return subCommands.get(cmd).tabCompelete(core, sender, label, args);
			}
		}
		return Arrays.asList("");
	}
	
	/**
	 * Return this commands name
	 * @return
	 */
	public abstract String getName ();
	
	/**
	 * Returns the argument name this command starts off with
	 * @return
	 */
	public abstract String getArgumentName ();
	
	/**
	 * Returns this commands arguments
	 * @return
	 */
	public abstract Message getUsage ();
	
	/**
	 * Returns a brief description of what this command does
	 * @return
	 */
	public abstract Message getDescription ();
	
	/**
	 * Returns this commands permission
	 * @return
	 */
	public abstract Permission getPermission ();
	
	/**
	 * Returns true if this command will appear in the help menu
	 * @return
	 */
	public abstract boolean showInHelp ();
	
	/**
	 * Checks to see if consoles can run this command
	 * @return
	 */
	public abstract boolean isPlayerOnly();
	
	/**
	 * Registers a sub-command under this command
	 * @param command
	 */
	public void register (Command command) {
		subCommands.put(command.getArgumentName(), command);
	}
	
	/**
	 * Returns a map of all sub-commands registered under this command
	 * @return
	 */
	public Map<String, Command> getSubCommands ()
	{
		final Map<String, Command> commands = new LinkedHashMap<String, Command>(subCommands);
		return commands;
	}
	
	/**
	 * Recursively adds all sub-commands under this command to the Map
	 * @param commands
	 */
	public void getSubCommands (LinkedHashMap<String, Command> commands)
	{
		for (Entry<String, Command> entry : subCommands.entrySet())
		{
			Command cmd = entry.getValue();
			
			if (cmd.showInHelp()) {
				commands.put(cmd.getName(), cmd);
			}
			
			cmd.getSubCommands(commands);
		}
	}
}
