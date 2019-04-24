package com.mediusecho.particlehats.permission;

public enum Permission {

	COMMAND_ALL           ("command.all"),
	COMMAND_MAIN          ("command.h"),
	COMMAND_RELOAD        ("command.reload"),
	COMMAND_HELP          ("command.help"),
	COMMAND_EDIT          ("command.edit"),
	COMMAND_EDIT_ALL      ("command.edit.all"),
	COMMAND_CREATE        ("command.create"),
	COMMAND_CLEAR         ("command.clear"),
	COMMAND_CLEAR_PLAYER  ("command.clear.player"),
	COMMAND_SET           ("command.set"),
	COMMAND_OPEN          ("command.open"),
	COMMAND_OPEN_PLAYER   ("command.open.player"),
	COMMAND_PARTICLES     ("command.particles"),
	COMMAND_GROUP         ("command.group"),
	COMMAND_GROUP_ADD     ("command.group.add"),
	COMMAND_GROUP_REMOVE  ("command.group.remove"),
	COMMAND_GROUP_EDIT    ("command.group.edit"),
	COMMAND_TYPE          ("command.type"),
	COMMAND_TYPE_ADD      ("command.type.add"),
	COMMAND_TYPE_REMOVE   ("command.type.remove"),
	COMMAND_IMPORT        ("command.import"),
	
	WORLD     ("world"),
	WORLD_ALL ("world.all"),
	
	GROUP ("group"),
	
	PARTICLE     ("particle"),
	PARTICLE_ALL ("particle.all");
	
	private static final String ROOT = "particlehats.";
	private String permission;
	
	private Permission (final String permission)
	{
		this.permission = ROOT + permission;
	}
	
	public String getPermission () {
		return permission;
	}
	
	public String append (String permission) {
		return this.permission + "." + permission;
	}
}
