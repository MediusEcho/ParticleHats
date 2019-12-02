package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.player.PlayerState;

public class MetaCommand extends EditCommand {

	public MetaCommand (final ParticleHats core)
	{
		super(core);
	}
	
	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{		
		PlayerState playerState = core.getPlayerState(sender.getPlayer());
		
		if (!playerState.hasEditorOpen())
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
		
		EditorMenuManager editorManager = core.getMenuManagerFactory().getEditorMenuManager(playerState);
		MetaState metaState = playerState.getMetaState();
		
		if (args.size() == 1 && args.get(0).equalsIgnoreCase("cancel")) {
			editorManager.reopen();
		} else {
			metaState.onMetaSet(editorManager, sender.getPlayer(), args);
		}
		
		return true;
	}
	
	@Override
	public List<String> tabComplete (ParticleHats core, Sender sender, String label, ArrayList<String> args)
	{
		if (sender.isPlayer())
		{
			PlayerState playerState = core.getPlayerState(sender.getPlayer());
			
			if (args.size() == 1) {
				return Arrays.asList(playerState.getMetaState().getSuggestion(), "cancel");
			} else {
				return Arrays.asList(playerState.getMetaState().getSuggestion());
			}
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
}
