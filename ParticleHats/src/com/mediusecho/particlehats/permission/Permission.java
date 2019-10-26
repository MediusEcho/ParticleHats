package com.mediusecho.particlehats.permission;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import com.mediusecho.particlehats.commands.Sender;

public enum Permission {

	NONE (""),
	
	COMMAND_ALL           ("command.all", "command.*"),
	COMMAND_MAIN          ("command.h"),
	COMMAND_RELOAD        ("command.reload"),
	COMMAND_HELP          ("command.help"),
	COMMAND_EDIT          ("command.edit"),
	COMMAND_EDIT_ALL      ("command.edit.all", "command.edit.*"),
	COMMAND_CREATE        ("command.create"),
	COMMAND_CLEAR         ("command.clear"),
	COMMAND_CLEAR_PLAYER  ("command.clear.player"),
	COMMAND_CLEAR_ALL     ("command.clear.*"),
	COMMAND_SET           ("command.set"),
	COMMAND_OPEN          ("command.open"),
	COMMAND_OPEN_PLAYER   ("command.open.player"),
	COMMAND_OPEN_ALL      ("command.open.all", "command.open.*"),
	COMMAND_PARTICLES     ("command.particles"),
	COMMAND_GROUP         ("command.group"),
	COMMAND_GROUP_ADD     ("command.group.add"),
	COMMAND_GROUP_REMOVE  ("command.group.remove"),
	COMMAND_GROUP_EDIT    ("command.group.edit"),
	COMMAND_GROUP_INFO    ("command.group.info"),
	COMMAND_GROUP_ALL     ("command.group.all", "command.group.*"),
	COMMAND_TYPE          ("command.type"),
	COMMAND_TYPE_ADD      ("command.type.add"),
	COMMAND_TYPE_REMOVE   ("command.type.remove"),
	COMMAND_TYPE_ALL      ("command.type.*"),
	COMMAND_IMPORT        ("command.import"),
	COMMAND_SELECTORS     ("command.selectors", "command.*"),
	COMMAND_NPC           ("command.npc"),
	COMMAND_NPC_CLEAR     ("command.npc.clear"),
	COMMAND_NPC_MANAGE    ("command.npc.manage"),
	
	WORLD     ("world"),
	WORLD_ALL ("world.all"),
	
	GROUP ("group"),
	
	PARTICLE     ("particle"),
	PARTICLE_ALL ("particle.all", "particle.*");
	
	private static final String ROOT = "particlehats.";
	private final String permission;
	
	private final List<String> aliases;
	
	private Permission (final String permission)
	{
		this(permission, "")
;	}
	
	private Permission (final String permission, String... aliases)
	{
		this.permission = ROOT + permission;
		this.aliases = Arrays.asList(aliases);
	}
	
	public String getPermission () {
		return permission;
	}
	
	public String append (String permission) {
		return this.permission + "." + permission;
	}
	
	/**
	 * Checks to see if the player has this permission value set
	 * @param player
	 * @return
	 */
	public boolean hasPermission (Player player)
	{
		if (player.hasPermission(permission)) {
			return true;
		}
		
		for (String alias : aliases) {
			if (player.hasPermission(ROOT + alias)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks to see if the player has this permission value set
	 * @param player
	 * @return
	 */
	public boolean hasPermission (Sender sender)
	{
		if (!sender.isPlayer()) {
			return true;
		}
		return hasPermission(sender.getPlayer());
	}
}
