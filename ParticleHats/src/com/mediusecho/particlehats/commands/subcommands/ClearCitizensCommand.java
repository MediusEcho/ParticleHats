package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Entity;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.hooks.citizens.CitizensHook;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.util.StringUtil;

public class ClearCitizensCommand extends Command {

	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{
		if (args.size() != 1)
		{
			sender.sendMessage(Message.COMMAND_ERROR_ARGUMENTS);
			sender.sendMessage(getUsage());
			return false;
		}
		
		int citizenID = StringUtil.toInt(args.get(0), -1);
		if (citizenID == -1) 
		{
			sender.sendMessage(Message.COMMAND_ERROR_UNKNOWN_NPC);
			return false;
		}
		
		CitizensHook citizensHook = core.getHookManager().getCitizensHook();
		Entity npc = citizensHook.getNPCEntity(citizenID);
		
		if (npc == null)
		{
			sender.sendMessage(Message.COMMAND_ERROR_UNKNOWN_NPC);
			return false;
		}
		
		EntityState entityState = core.getEntityState(npc);
		entityState.clearActiveHats();
		
		String name = citizensHook.getNPCName(citizenID);
		sender.sendMessage(Message.COMMAND_NPC_CLEAR_SUCCESS.getValue().replace("{1}", name));
		
		return true;
	}
	
	@Override
	public List<String> tabComplete (ParticleHats core, Sender sender, String label, ArrayList<String> args)
	{
		switch (args.size())
		{
			case 1:
			{
				return core.getHookManager().getCitizensHook().getNPCIds();
			}
		}
		return Arrays.asList("");
	}

	@Override
	public String getName() {
		return "clear npc";
	}

	@Override
	public String getArgumentName() {
		return "clear";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_NPC_CLEAR_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_NPC_CLEAR_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_NPC_CLEAR;
	}

	@Override
	public boolean showInHelp() {
		return true;
	}

	@Override
	public boolean isPlayerOnly() {
		return false;
	}

}
