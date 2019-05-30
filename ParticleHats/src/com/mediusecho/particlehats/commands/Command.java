package com.mediusecho.particlehats.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.mediusecho.particlehats.ParticleHats;
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
	public abstract boolean execute (ParticleHats core, Sender sender, String label, ArrayList<String> args);
	
	/**
	 * Handles this commands onCommand
	 * Checks permission before passing command along
	 * @param core
	 * @param sender
	 * @param label
	 * @param args
	 * @return
	 */
	public boolean onCommand (ParticleHats core, Sender sender, String label, ArrayList<String> args)
	{
		if (!sender.isPlayer() && isPlayerOnly())
		{
			sender.sendMessage(Message.COMMAND_ERROR_PLAYER_ONLY);
			return false;
		}
		
		String argument = "";
		if (args.size() >= 1) {
			argument = args.get(0);
		}
		
		if (!hasPermission(sender, argument))
		{
			sender.sendMessage(Message.COMMAND_ERROR_NO_PERMISSION);
			return false;
		}
		
		return execute(core, sender, label, args);
	}
	
	/**
	 * Generic tab complete method
	 * @param plugin
	 * @param sender
	 * @param label
	 * @param args
	 * @return
	 */
	public List<String> tabCompelete (ParticleHats core, Sender sender, String label, ArrayList<String> args)
	{	
		if (hasPermission(sender))
		{
			if (args.size() == 1)
			{
				List<String> arguments = new ArrayList<String>();
				for (Entry<String, Command> entry : subCommands.entrySet())
				{
					if (entry.getValue().hasPermission(sender)) {
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
	 * Checks to see if this command has a permission value
	 * @return
	 */
	public boolean hasPermission () {
		return true;
	}
	
	/**
	 * Checks to see if this command has a wild card permission
	 * @return
	 */
	public boolean hasWildcardPermission () {
		return false;
	}
	
	/**
	 * Returns this command's wild card permission
	 * @return
	 */
	public Permission getWildcardPermission () {
		return Permission.NONE;
	}
	
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
	
	/**
	 * Checks to see if the player has permission to execute this command
	 * @param sender
	 * @return
	 */
	public boolean hasPermission (Sender sender) {
		return hasPermission(sender, "");
	}
	
	/**
	 * Checks to see if the player has permission to execute this command
	 * @param sender
	 * @param arg
	 * @return
	 */
	public boolean hasPermission (Sender sender, String arg)
	{
		if (!sender.isPlayer()) {
			return true;
		}
		
		// If this command doesn't have a permission of its own, then
		// we'll check the sub commands
		if (!hasPermission())
		{
			for (Command command : subCommands.values()) {
				if (command.hasPermission(sender)) {
					return true;
				}
			}
		}
		
		// /h wild card check
		if (Permission.COMMAND_ALL.hasPermission(sender)) {
			return true;
		}
		
		// Specific command wild card check
		if (hasWildcardPermission())
		{
			if (getWildcardPermission().hasPermission(sender)) {
				return true;
			}
		}
		
		// Regular permission check
		if (getPermission().hasPermission(sender)) {
			return true;
		}
		
		return false;
	}
	
	public Player getPlayer (Sender sender, String selector)
	{
		CommandSender commandSender = sender.getCommandSender();
		if (Permission.COMMAND_SELECTORS.hasPermission(sender))
		{
			switch (selector)
			{
				case "@p":
				{
					Location location = null;
					if (sender.isPlayer()) {
						location = sender.getPlayer().getLocation();
					} else if (commandSender instanceof BlockCommandSender) {
						location = ((BlockCommandSender)commandSender).getBlock().getLocation();
					}
					
					if (location != null) {
						return getNearestPlayer(location);
					}
				}
				break;
				
				case "@r":
				{
					Collection<? extends Player> players = Bukkit.getOnlinePlayers();
					Optional<? extends Player> player = players.stream().skip((int) (players.size() * Math.random())).findFirst();
					
					if (player.isPresent()) {
						return player.get();
					}
				}
				break;
			}
		}
		
		return Bukkit.getPlayer(selector);
	}
	
	private Player getNearestPlayer (Location location)
	{
		double maxDistance = 100;
		Player targetPlayer = null;
		Collection<? extends Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, 25, 25, 25);
		
		for (Entity e : nearbyEntities)
		{
			if (!(e instanceof Player)) {
				continue;
			}
			
			Player player = (Player)e;
			double distance = player.getLocation().distanceSquared(location);
			if (distance < maxDistance)
			{
				targetPlayer = player;
				maxDistance = distance;
			}
		}
		
		return targetPlayer;
	}
}
