package com.mediusecho.particlehats.commands.subcommands;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.CommandManager;
import com.mediusecho.particlehats.util.StringUtil;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class SpigotHelpCommand extends BukkitHelpCommand {

	private Map<Command, BaseComponent[]> commands;

	public SpigotHelpCommand(ParticleHats core, CommandManager commandManager) 
	{
		super(core, commandManager);
		
		commands = new HashMap<Command, BaseComponent[]>();
		
		StringBuilder builder = new StringBuilder();
		for (Entry<String, Command> cmds : commandManager.getCommands().entrySet())
		{
			Command command = cmds.getValue();
			if (command != null)
			{
				builder.append(StringUtil.colorize("&3Command: &f")).append(StringUtil.capitalizeFirstLetter(command.getName())).append("\n");
				builder.append(StringUtil.colorize("&3Description: &8" + command.getDescription().getValue())).append("\n");
				builder.append(StringUtil.colorize("&3Usage: &8" + command.getUsage().getValue())).append("\n");
				builder.append(StringUtil.colorize("&3Permission: &8" + command.getPermission().getPermission()));
				
				HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(builder.toString()).create());
				ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command.getUsage().getValue());
				BaseComponent[] component = new ComponentBuilder(StringUtil.colorize("&7> &3") + command.getUsage().getValue()).event(hoverEvent).event(clickEvent).create();
				
				commands.put(command, component);
				builder.setLength(0);
			}
		}
	}
	
	@Override
	protected void readPage (Sender sender, int page)
	{
		sender.sendMessage("&f> &6ParticleHats v" + core.getDescription().getVersion());
		sender.sendMessage("&7> " + Message.COMMAND_HELP_TIP.getValue());
		for (Entry<Command, BaseComponent[]> entry : commands.entrySet())
		{
			if (entry.getKey().hasPermission(sender))
			{
				BaseComponent[] component = entry.getValue();
				sender.getPlayer().spigot().sendMessage(component);
			}
		}
	}

}
