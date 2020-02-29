package com.mediusecho.particlehats.editor.citizens;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.hooks.citizens.CitizensHook;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.ui.AbstractMenu;
import com.mediusecho.particlehats.ui.AbstractMenu.MenuClickResult;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.ui.MenuManager;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;

public class CitizensMenuManager extends MenuManager {

	private final EntityState citizenEntityState;
	protected final Entity citizenEntity;
	
	private final Sound sound;
	private final float soundVolume;
	private final float soundPitch;
	
	public CitizensMenuManager(final ParticleHats core, final Player owner, final EntityState citizenEntityState) 
	{
		super(core, owner);
		
		this.citizenEntityState = citizenEntityState;
		this.citizenEntity = citizenEntityState.getOwner();
		
		this.sound = SettingsManager.EDITOR_SOUND_ID.getSound();
		this.soundVolume = (float) SettingsManager.EDITOR_SOUND_VOLUME.getDouble();
		this.soundPitch = (float) SettingsManager.EDITOR_SOUND_PITCH.getDouble();
		
		CitizensMainMenu mainMenu = new CitizensMainMenu(core, this, owner, citizenEntity);
		addMenu(mainMenu);
	}
	
	@Override
	public void onClick (InventoryClickEvent event, boolean inMenu) {
		super.onClick(event, inMenu, getCurrentMenu());
	}

	@Override
	public void open() {
		getCurrentMenu().open();
	}
	
	@Override
	public void onTick (int ticks) 
	{
		AbstractMenu menu = getCurrentMenu();
		if (menu == null) {
			return;
		}
		
		menu.onTick(ticks);
	}

	@Override
	public void playSound(MenuClickResult result) 
	{
		if (SettingsManager.EDITOR_SOUND_ENABLED.getBoolean())
		{
			if (sound != null)
			{
				float p = (float) MathUtil.clamp(soundPitch + (float) result.getModifier(), 0, 2);	
				owner.playSound(owner.getLocation(), sound, soundVolume, p);
			}
		}	
	}
	
	@Override
	public void willUnregister ()
	{
		CitizensHook citizensHook = core.getHookManager().getCitizensHook();
		if (citizensHook == null) {
			return;
		}
		
		citizensHook.saveCitizenData(citizenEntity, citizenEntityState);
		
		super.willUnregister();
	}
	
}
