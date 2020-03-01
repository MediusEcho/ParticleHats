package com.mediusecho.particlehats.ui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.ui.menus.Menu;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.util.MathUtil;

public class StaticMenuManager extends MenuManager {

	private Map<String, Menu> menuCache;
	private Menu currentOpenMenu;
	private Menu previousOpenMenu;
	
	private final Sound sound;
	private final float soundVolume;
	private final float soundPitch;
	
	public StaticMenuManager(ParticleHats core, Player owner) 
	{
		super(core, owner);
		
		this.menuCache = new HashMap<String, Menu>(); 
		this.sound = SettingsManager.MENU_SOUND_ID.getSound();
		this.soundVolume = (float) SettingsManager.MENU_SOUND_VOLUME.getDouble();
		this.soundPitch = (float) SettingsManager.MENU_SOUND_PITCH.getDouble();
	}
	
	@Override
	public void onClick (InventoryClickEvent event, boolean inMenu) {
		super.onClick(event, inMenu, currentOpenMenu);
	}
	
	@Override
	public void addMenu (Menu menu) {
		menuCache.put(menu.getName(), menu);
	}

	@Override
	public void open() { }
	
	@Override
	public void isOpeningMenu (Menu menu)
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
	
	/**
	 * Get a AbstractMenu with the given name from the menu cache
	 * @param name
	 * @return
	 */
	public Menu getMenuFromCache (String name)
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
	public Menu getPreviousOpenMenu () {
		return previousOpenMenu;
	}
}
