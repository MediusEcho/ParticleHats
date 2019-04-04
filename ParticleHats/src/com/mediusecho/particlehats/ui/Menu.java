package com.mediusecho.particlehats.ui;

import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.util.MathUtil;

public abstract class Menu {

	protected final Core core;
	
	protected Player owner;
	protected UUID ownerID;
	
	protected MenuInventory inventory;
	
	public Menu (Core core, final Player owner)
	{
		this.core = core;
		this.owner = owner;
		this.ownerID = owner.getUniqueId();
	}
	
	public Menu (Core core, final Player owner, final MenuInventory inventory)
	{
		this(core, owner);
		this.inventory = inventory;
	}
	
	/**
	 * Get the owner of this menu
	 * @return
	 */
	public Player getOwner () {
		return owner;
	}
	
	/**
	 * Get the owners UUID
	 * @return
	 */
	public UUID getOwnerID () {
		return ownerID;
	}
	
	/**
	 * Get this menus name
	 * @return
	 */
	public String getName () {
		return inventory.getName();
	}
	
	/**
	 * Opens this menu for the owning player
	 */
	public void open () {
		inventory.open(owner);
	}
	
	/**
	 * Set this menus MenuInventory
	 * @param inventory
	 */
	public void setInventory (MenuInventory inventory) {
		this.inventory = inventory;
	}
	
	/**
	 * Plays a sound for the menu owner<br>
	 * Sound is defined in config.yml
	 */
	public void playSound ()
	{
		if (SettingsManager.MENU_SOUND_ENABLED.getBoolean())
		{
			Sound sound = SettingsManager.MENU_SOUND_ID.getSound();
			if (sound != null)
			{
				float volume = (float) SettingsManager.MENU_SOUND_VOLUME.getDouble();
				float pitch = (float) SettingsManager.MENU_SOUND_PITCH.getDouble();
				float p = (float) MathUtil.clamp(pitch, 0, 2);
				
				owner.playSound(owner.getLocation(), sound, volume, p);
			}
		}
	}
	
	/**
	 * Lets this menu update dynamically
	 */
	public abstract void onTick (int ticks);

	/**
	 * Handle click events inside this menu
	 * @param event
	 */
	public abstract void onClick (InventoryClickEvent event);
	
	/**
	 * Creates a copy of this Menu object
	 */
	public Menu clone () {
		return new StaticMenu(core, owner, inventory.clone());
	}
	
	@Override
	public int hashCode () {
		return getName().hashCode();
	}
	
	@Override
	public boolean equals (Object o)
	{
		if (o instanceof String) {
			return getName().equals((String)o);
		}
		return false;
	}
}
