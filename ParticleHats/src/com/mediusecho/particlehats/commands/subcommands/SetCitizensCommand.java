package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;

import org.bukkit.entity.Entity;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.hooks.citizens.CitizensHook;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.permission.Permission;
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
			
			return false;
		}
		
		String hatLabel = args.get(1);
		Hat hat = core.getDatabase().getHatFromLabel(hatLabel);
		if (hat == null)
		{
			sender.sendMessage(Message.COMMAND_SET_LABEL_ERROR.getValue().replace("{1}", hatLabel));
			return false;
		}
		
		CitizensHook citizensHook = core.getHookManager().getCitizensHook();
		Entity npc = citizensHook.getNPCEntity(citizenID);
		
		if (npc == null)
		{
			sender.sendMessage(Message.COMMAND_NPC_SET_ERROR);
			return false;
		}
		
		
		
		return false;
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
