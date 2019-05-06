package com.mediusecho.particlehats.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mediusecho.particlehats.particles.Hat;

public class HatEquipEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	
	private final Player player;
	private final Hat hat;
	
	private boolean cancelled = false;
	
	public HatEquipEvent (final Player player, final Hat hat)
	{
		this.player = player;
		this.hat = hat;
	}
	
	public Player getPlayer () {
		return player;
	}
	
	public Hat getHat () {
		return hat;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
