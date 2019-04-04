package com.mediusecho.particlehats.commands.subcommands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.managers.CommandManager;
import com.mediusecho.particlehats.util.StringUtil;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class SpigotHelpCommand extends BukkitHelpCommand {

	// TODO: Make command tooltip look better
	private Map<Integer, BaseComponent[]> commands;
	
	public SpigotHelpCommand(Core core, CommandManager commandManager) 
	{
		super(core, commandManager);
		
		commands = new HashMap<Integer, BaseComponent[]>();
		
		int commandIndex = 0;
		StringBuilder builder = new StringBuilder();
		
		for (Entry<String, Command> cmds : commandManager.getCommands().entrySet())
		{
			Command command = cmds.getValue();
			if (command != null)
			{
				builder.append(command.getName()).append("\n");
				builder.append(StringUtil.colorize("&3Description:\n"));
				
				List<String> description = StringUtil.parseDescription(command.getDescription().getValue());
				for (String s : description) {
					builder.append(s).append("\n");
				}
				
				//builder.append("\n").append(StringUtil.colorize(s))
				
				HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(builder.toString()).create());
				ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command.getUsage().getValue());
				BaseComponent[] component = new ComponentBuilder(StringUtil.colorize("&3") + command.getUsage().getValue()).event(hoverEvent).event(clickEvent).create();
				
				commands.put(commandIndex++, component);
				builder.setLength(0);
			}
		}
	}
	
	@Override
	protected void readPage (Sender sender, int page)
	{
		sender.sendMessage(">- &6ParticleHats v" + core.getDescription().getVersion());
		sender.sendMessage(">- &7Hover over a command for more info");
		
		int range = page * 9;
		for (int i = range; i < (range + 9); i++)
		{
			if (commands.containsKey(i))
			{
				BaseComponent[] component = commands.get(i);
				sender.getPlayer().spigot().sendMessage(component);
			}
		}
		
		sender.sendMessage(">- &6" + (page + 1) + "&7/&6" + pages);
	}

}
