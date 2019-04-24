package com.mediusecho.particlehats.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.commands.MainCommand;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.subcommands.BukkitHelpCommand;
import com.mediusecho.particlehats.commands.subcommands.ClearCommand;
import com.mediusecho.particlehats.commands.subcommands.CreateCommand;
import com.mediusecho.particlehats.commands.subcommands.DebugCommand;
import com.mediusecho.particlehats.commands.subcommands.DebugDeleteMenu;
import com.mediusecho.particlehats.commands.subcommands.EditCommand;
import com.mediusecho.particlehats.commands.subcommands.GroupsCommand;
import com.mediusecho.particlehats.commands.subcommands.ImportCommand;
import com.mediusecho.particlehats.commands.subcommands.MetaCommand;
import com.mediusecho.particlehats.commands.subcommands.OpenCommand;
import com.mediusecho.particlehats.commands.subcommands.ParticlesCommand;
import com.mediusecho.particlehats.commands.subcommands.ReloadCommand;
import com.mediusecho.particlehats.commands.subcommands.SetCommand;
import com.mediusecho.particlehats.commands.subcommands.SpigotHelpCommand;
import com.mediusecho.particlehats.commands.subcommands.TypeCommand;

public class CommandManager implements CommandExecutor, TabCompleter {

	private final Core core;
	
	private final MainCommand mainCommand;
	
	public CommandManager (final Core core, final String command)
	{
		this.core = core;
		
		mainCommand = new MainCommand();
		mainCommand.register(new ReloadCommand());
		mainCommand.register(new OpenCommand(core));
		mainCommand.register(new EditCommand(core));
		mainCommand.register(new CreateCommand());
		mainCommand.register(new DebugDeleteMenu());
		mainCommand.register(new DebugCommand());
		mainCommand.register(new ClearCommand());
		mainCommand.register(new SetCommand());
		mainCommand.register(new ParticlesCommand());
		mainCommand.register(new GroupsCommand());
		mainCommand.register(new TypeCommand());
		mainCommand.register(new ImportCommand());
		mainCommand.register(new MetaCommand());
		
		if (core.canUseBungee()) {
			mainCommand.register(new SpigotHelpCommand(core, this));
		} else {
			mainCommand.register(new BukkitHelpCommand(core, this));
		}
		
		// Register our command executor
		core.getCommand(command).setExecutor(this);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command cmd, String label, String[] args) 
	{
		Sender sender = new Sender(commandSender);
		
		List<String> arguments = mainCommand.tabCompelete(core, sender, label, new ArrayList<String>(Arrays.asList(args)));
		String currentCommand = args[args.length - 1];
		
		return sortCommandSuggestions(arguments, currentCommand);
	}

	@Override
	public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command cmd, String label, String[] args) 
	{
		Sender sender = new Sender(commandSender);
		return mainCommand.execute(core, sender, label, new ArrayList<String>(Arrays.asList(args)));
	}
	
	/**
	 * Get all registered commands
	 * @return
	 */
	public Map<String, Command> getCommands ()
	{
		LinkedHashMap<String, Command> commands = new LinkedHashMap<String, Command>();
		mainCommand.getSubCommands(commands);
		
		return commands;
	}
	
	/**
	 * Returns a list of commands matching the currentCommand
	 * @param commands List of the commands the player can execute
	 * @param currentCommand Command the player is currently typing
	 * @return
	 */
	private List<String> sortCommandSuggestions (List<String> commands, String currentCommand)
	{
		if (currentCommand.equals("")) {
			return commands;
		}
		
		List<String> matchingCommands = new ArrayList<String>();
		commandLoop:
		for (String s : commands)
		{
			for (int i = 0; i < s.length(); i++)
			{
				if (i < currentCommand.length())
				{
					if (s.charAt(i) != currentCommand.charAt(i)) {
						continue commandLoop;
					}
				}
			}
			matchingCommands.add(s);
		}
		return matchingCommands;
	}
}
