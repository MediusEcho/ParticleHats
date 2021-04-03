package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Entity;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.hooks.citizens.CitizensHook;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.util.StringUtil;

public class SetCitizensCommand extends Command {

	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{
		if (args.size() < 2 || args.size() > 4)
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
		
		String hatLabel = args.get(1);
		
		CitizensHook citizensHook = core.getHookManager().getCitizensHook();
		Entity npc = citizensHook.getNPCEntity(citizenID);
		
		if (npc == null)
		{
			sender.sendMessage(Message.COMMAND_ERROR_UNKNOWN_NPC);
			return false;
		}
		
		EntityState entityState = core.getEntityState(npc);
		String npcName = citizensHook.getNPCName(citizenID);
		
		Hat hat = core.getDatabase().getHatFromLabel(hatLabel);
		if (hat == null)
		{
			sender.sendMessage(Message.COMMAND_SET_LABEL_ERROR.getValue().replace("{1}", hatLabel));
			return false;
		}
		
		if (entityState.isEquipped(label))
		{
			sender.sendMessage(Message.COMMAND_SET_ALREADY_SET.getValue().replace("{1}", npcName));
			return false;
		}
		
		entityState.addHat(hat);
		citizensHook.saveCitizenData(npc, entityState);
		
		sender.sendMessage(Message.COMMAND_NPC_SET_SUCCESS.getValue().replace("{1}", npcName).replace("{2}", hat.getDisplayName()));
		return true;
	}
	
	@Override
	public List<String> tabComplete (ParticleHats core, Sender sender, String label, ArrayList<String> args)
	{
		switch (args.size())
		{
			case 1:
			{
				CitizensHook citizensHook = core.getHookManager().getCitizensHook();
				if (citizensHook == null) {
					return Collections.singletonList("");
				}
				
				return citizensHook.getNPCIds();
			}
			
			case 2:
			{
				return core.getDatabase().getLabels(false);
			}
		}
		return Arrays.asList("");
	}

	@Override
	public String getName() {
		return  "set npc";
	}

	@Override
	public String getArgumentName() {
		return "set";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_NPC_SET_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_NPC_SET_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_NPC_SET;
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
