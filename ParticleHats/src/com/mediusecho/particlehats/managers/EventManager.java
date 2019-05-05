package com.mediusecho.particlehats.managers;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.listeners.ChatListener;
import com.mediusecho.particlehats.listeners.CommandListener;
import com.mediusecho.particlehats.listeners.ConnectionListener;
import com.mediusecho.particlehats.listeners.EntityListener;
import com.mediusecho.particlehats.listeners.InteractListener;
import com.mediusecho.particlehats.listeners.InventoryListener;
import com.mediusecho.particlehats.listeners.MovementListener;

@SuppressWarnings("unused")
public class EventManager {

	private final Core core;
	
	// Events
	private final InventoryListener  inventoryListener;
	private final ChatListener       chatListener;
	private final MovementListener   movementListener;
	private final EntityListener     entityListener;
	private final ConnectionListener connectionListener;
	private final InteractListener   interactListener;
	private final CommandListener    commandListener;
	
	public EventManager (final Core core)
	{
		this.core = core;
		
		inventoryListener  = new InventoryListener(core);
		chatListener       = new ChatListener(core);
		movementListener   = new MovementListener(core);
		entityListener     = new EntityListener(core);
		connectionListener = new ConnectionListener(core);
		interactListener   = new InteractListener(core);
		commandListener    = new CommandListener(core);
	}
}
