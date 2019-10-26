package com.mediusecho.particlehats.editor.citizens;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.ui.AbstractMenu.MenuClickResult;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.ui.MenuManager;

public class CitizensManager extends MenuManager {

	protected final Entity citizenEntity;
	
	private final Sound sound;
	private final float soundVolume;
	private final float soundPitch;
	
	public CitizensManager(final ParticleHats core, final Player owner, final Entity citizenEntity) 
	{
		super(core, owner);
		
		this.citizenEntity = citizenEntity;
		
		this.sound = SettingsManager.EDITOR_SOUND_ID.getSound();
		this.soundVolume = (float) SettingsManager.EDITOR_SOUND_VOLUME.getDouble();
		this.soundPitch = (float) SettingsManager.EDITOR_SOUND_PITCH.getDouble();
		
		CitizensMainMenu mainMenu = new CitizensMainMenu(core, this, owner, citizenEntity);
		addMenu(mainMenu);
	}

	@Override
	public void open() {
		getCurrentMenu().open();
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
	
}

//public class CitizensManager {
//
//	protected final ParticleHats core;
//	protected final Player owner;
//	protected PlayerState ownerState;
//	
//	private Deque<AbstractMenu> activeMenus;
//	
//	public CitizensManager (final ParticleHats core, final Player owner, final Entity citizenEntity)
//	{
//		this.core = core;
//		this.owner = owner;
//		this.ownerState = core.getPlayerState(owner);
//		
//		activeMenus = new ArrayDeque<AbstractMenu>();
//		
//		CitizensMainMenu mainMenu = new CitizensMainMenu(core, owner, citizenEntity);
//		activeMenus.add(mainMenu);
//	}
//	
//	public void open ()
//	{
//		ownerState.setGuiState(GuiState.SWITCHING_MANAGER);
//		activeMenus.getLast().open();
//	}
//	
//	public void onClick(InventoryClickEvent event, final boolean inMenu)
//	{		
//		AbstractMenu menu = activeMenus.peekLast();
//		if (menu != null)
//		{
//			MenuClickResult result = menu.onClick(event, event.getRawSlot(), inMenu);
////			if (result != MenuClickResult.NONE) {
////				em.playSound(ct);
////			}
//		}
//	}
//	
//}
