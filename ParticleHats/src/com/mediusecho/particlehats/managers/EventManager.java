package com.mediusecho.particlehats.managers;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.listeners.ChatListener;
import com.mediusecho.particlehats.listeners.EntityListener;
import com.mediusecho.particlehats.listeners.InventoryListener;
import com.mediusecho.particlehats.listeners.MovementListener;

@SuppressWarnings("unused")
public class EventManager {

	private final Core core;
	
	// Events
	private InventoryListener inventoryListener;
	private ChatListener      chatListener;
	private MovementListener  movementListener;
	private EntityListener    entityListener;
	
	public EventManager (final Core core)
	{
		this.core = core;
		
		inventoryListener = new InventoryListener(core);
		chatListener      = new ChatListener(core);
		movementListener  = new MovementListener(core);
		entityListener    = new EntityListener(core);
	}
}
