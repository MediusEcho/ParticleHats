package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.PlayerState;

public class MetaCommand extends Command {

	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{
		if (!sender.hasPermission(Permission.COMMAND_EDIT) && !sender.hasPermission(Permission.COMMAND_EDIT_ALL))
		{
			sender.sendMessage(Message.COMMAND_ERROR_NO_PERMISSION);
			return false;
		}
		
		PlayerState playerState = core.getPlayerState(sender.getPlayerID());
		MenuBuilder menuBuilder = playerState.getMenuBuilder();
		
		if (menuBuilder == null)
		{
			sender.sendMessage(Message.META_ERROR);
			return false;
		}
		
		if (args.size() == 0)
		{
			sender.sendMessage(Message.COMMAND_ERROR_ARGUMENTS);
			sender.sendMessage(Message.COMMAND_META_USAGE.replace("{1}", playerState.getMetaState().getSuggestion()));
			return false;
		}
		
		MetaState metaState = playerState.getMetaState();
		
		if (args.size() == 1 && args.get(0).equalsIgnoreCase("cancel")) {
			metaState.reopenEditor(menuBuilder);
		} else {
			metaState.onMetaSet(menuBuilder, sender.getPlayer(), args);
		}
		
		return true;
	}
	
	@Override
	public List<String> tabCompelete (ParticleHats core, Sender sender, String label, ArrayList<String> args)
	{
		if (sender.isPlayer())
		{
			PlayerState playerState = core.getPlayerState(sender.getPlayerID());
			return Arrays.asList(playerState.getMetaState().getSuggestion());
		}
		return Arrays.asList("");
	}

	@Override
	public String getName() {
		return "meta";
	}

	@Override
	public String getArgumentName() {
		return "meta";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_META_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_META_DESCRIPTION;
	}

	@Override
	public Permission getPermission() 
	{
		// We'll check permission on execute since we need multiple permissions
		return Permission.COMMAND_ALL;
	}

	@Override
	public boolean showInHelp() {
		return true;
	}

	@Override
	public boolean isPlayerOnly() {
		return true;
	}

}
