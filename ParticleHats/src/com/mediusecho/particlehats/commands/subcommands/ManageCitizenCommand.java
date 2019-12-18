package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.player.PlayerState;

public class ManageCitizenCommand extends Command {

	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{
//		if (core.getHookManager().getCitizensHook() == null)
//		{
//			sender.sendMessage(Message.COMMAND_NPC_SUPPORT_ERROR);
//			return false;
//		}
		
		EntityState entityState = core.getEntityState(sender.getPlayer());
		if (entityState instanceof PlayerState)
		{
			PlayerState playerState = (PlayerState)entityState;
			playerState.setMetaState(MetaState.NPC_MANAGE);
			core.prompt(sender.getPlayer(), MetaState.NPC_MANAGE);
			
			return true;
		}
		
		return false;
	}

	@Override
	public String getName() {
		return "manage npc";
	}

	@Override
	public String getArgumentName() {
		return "manage";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_MANAGE_NPC_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_MANAGE_NPC_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_NPC_MANAGE;
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
