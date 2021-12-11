package com.mediusecho.particlehats.ui;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.ui.AbstractMenu.MenuClickResult;
import com.mediusecho.particlehats.util.MathUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;

public class StaticMenuManager extends MenuManager {

	private Map<String, AbstractMenu> menuCache;
	private AbstractMenu currentOpenMenu;
	private AbstractMenu previousOpenMenu;
	
	private final Sound sound;
	private final float soundVolume;
	private final float soundPitch;
	
	public StaticMenuManager(ParticleHats core, Player owner) 
	{
		super(core, owner);
		
		this.menuCache = new HashMap<String, AbstractMenu>(); 
		this.sound = SettingsManager.MENU_SOUND_ID.getSound();
		this.soundVolume = (float) SettingsManager.MENU_SOUND_VOLUME.getDouble();
		this.soundPitch = (float) SettingsManager.MENU_SOUND_PITCH.getDouble();
	}
	
	@Override
	public void onClick (InventoryClickEvent event, boolean inMenu) {
		super.onClick(event, inMenu, currentOpenMenu);
	}
	
	@Override
	public void addMenu (AbstractMenu menu) {
		menuCache.put(menu.getName(), menu);
	}

	@Override
	public void open() { }
	
	@Override
	public void isOpeningMenu (AbstractMenu menu)
	{
		previousOpenMenu = currentOpenMenu;
		currentOpenMenu = menu;
		super.isOpeningMenu(menu);
	}

	@Override
	public void onTick(int ticks) 
	{
		if (currentOpenMenu != null) {
			currentOpenMenu.onTick(ticks);
		}
	}

	@Override
	public void playSound(MenuClickResult result) 
	{
		if (SettingsManager.MENU_SOUND_ENABLED.getBoolean())
		{
			if (sound != null)
			{
				float p = (float) MathUtil.clamp(soundPitch + (float) result.getModifier(), 0, 2);	
				owner.playSound(owner.getLocation(), sound, soundVolume, p);
			}
		}	
	}

	@Override
	public AbstractMenu getCurrentMenu() {
		return currentOpenMenu;
	}

	/**
	 * Get a AbstractMenu with the given name from the menu cache
	 * @param name
	 * @return
	 */
	public AbstractMenu getMenuFromCache (String name)
	{
		if (menuCache.containsKey(name)) {
			return menuCache.get(name);
		}
		return null;
	}

	/**
	 * Get the previously opened AbstractMenu
	 * @return
	 */
	public AbstractMenu getPreviousOpenMenu () {
		return previousOpenMenu;
	}
}
