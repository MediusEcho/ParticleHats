package com.mediusecho.particlehats.editor;

import java.util.Map.Entry;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.editor.menus.EditorBaseMenu;
import com.mediusecho.particlehats.editor.menus.EditorMainMenu;
import com.mediusecho.particlehats.editor.menus.EditorSettingsMenu;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.ui.AbstractMenu;
import com.mediusecho.particlehats.ui.AbstractMenu.MenuClickResult;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.ui.MenuManager;

public class EditorMenuManager extends MenuManager {
	
	protected EditorBaseMenu editorBaseMenu;
	
	protected Hat targetHat;
	protected Hat targetNode;
	
	protected int targetSlot;
	
	private String metaArgument = "";
	
	private Sound sound;
	private float soundVolume;
	private float soundPitch;
	
	public EditorMenuManager(ParticleHats core, Player owner) 
	{
		super(core, owner);
		
		this.sound = SettingsManager.EDITOR_SOUND_ID.getSound();
		this.soundVolume = (float) SettingsManager.EDITOR_SOUND_VOLUME.getDouble();
		this.soundPitch = (float) SettingsManager.EDITOR_SOUND_PITCH.getDouble();
	}
	
	@Override
	public void onClick (InventoryClickEvent event, boolean inMenu) {
		super.onClick(event, inMenu, getCurrentMenu());
	}

	@Override
	public void open() 
	{
		if (editorBaseMenu == null) {
			return;
		}
		getCurrentMenu().open();
	}

	@Override
	public void onTick(int ticks) 
	{
		AbstractMenu menu = openMenus.peekLast();
		if (menu != null) {
			menu.onTick(ticks);
		}
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
	public boolean canUnregister () {
		return !openingMenu && ownerState.getMetaState() == MetaState.NONE;
	}
	
	@Override
	public void willUnregister ()
	{
		Database database = core.getDatabase();
		MenuInventory menuInventory = editorBaseMenu.getMenuInventory();
		String menuName = menuInventory.getName();
		
		for (Entry<Integer, Hat> hats : menuInventory.getHats().entrySet())
		{
			Hat hat = hats.getValue();
			if (hat.isModified())
			{
				database.saveHat(menuName, hats.getKey(), hat);
				hat.clearPropertyChanges();
			}
			
			if (hat.getNodeCount() > 0)
			{
				for (Hat node : hat.getNodes())
				{
					if (node.isModified())
					{
						database.saveNode(menuName, node.getIndex(), node);
						node.clearPropertyChanges();
					}
				}
			}
		}
		
		// Forces every open menu to close and save changes
		for (AbstractMenu am : openMenus) {
			am.onClose(true);
		}
		
		super.unregister();
	}
	
	public void setEditingMenu (MenuInventory inventory) 
	{
		editorBaseMenu = new EditorBaseMenu(core, this, owner, inventory);
		addMenu(editorBaseMenu);	
	}
	
	/**
	 * Set the hat that will be edited
	 * @param hat
	 */
	public void setTargetHat (Hat hat) {
		targetHat = hat;
	}
	
	/**
	 * Get the current hat being edtied
	 * @return
	 */
	public Hat getTargetHat ()
	{
		if (targetNode == null) {
			return targetHat;
		}
		return targetNode;
	}
	
	/**
	 * Get the base hat being edited
	 * @return
	 */
	public Hat getBaseHat () {
		return targetHat;
	}

	/**
	 * Set the node hat that will be edited
	 * @param node
	 */
	public void setTargetNode (Hat node) {
		targetNode = node;
	}
	
	/**
	 * Get the current slot being edited
	 * @return
	 */
	public int getTargetSlot () {
		return targetSlot;
	}
	
	/**
	 * Set the current inventory slot being edited
	 * @param slot
	 */
	public void setTargetSlot (int slot) {
		targetSlot = slot;
	}
	
	/**
	 * Set the current meta argument
	 * @param argument
	 */
	public void setMetaArgument (String argument) {
		metaArgument = argument;
	}
	
	/**
	 * Get the current meta argument
	 * @return
	 */
	public String getMetaArgument () {
		return metaArgument;
	}
	
	/**
	 * Reset the meta argumnent to ""
	 */
	public void resetMetaArgument () {
		metaArgument = "";
	}
	
	/**
	 * Returns the name of the menu being edited
	 * @return
	 */
	public String getMenuName () {
		return editorBaseMenu.getMenuInventory().getName();
	}
	
	/**
	 * Clears the list of open menus and opens the base editing menu
	 */
	public void returnToBaseMenu ()
	{
		while (openMenus.size() > 1) {
			openMenus.pollLast().onClose(true);
		}
		
		setTargetHat(null);
		setTargetSlot(-1);
		
		openMenus.getLast().open();
	}
	
	/**
	 * Returns the EditorBaseMenu class that is currently being edited
	 * @return
	 */
	public EditorBaseMenu getEditingMenu () {
		return editorBaseMenu;
	}
	
	/**
	 * Opens the editor main menu
	 */
	public void openMainMenu ()
	{
		EditorMainMenu editorMainMenu = new EditorMainMenu(core, this, owner);
		addMenu(editorMainMenu);
		editorMainMenu.open();
	}
	
	/**
	 * Opens the editor settings menu
	 */
	public void openSettingsMenu ()
	{
		EditorSettingsMenu editorSettingsMenu = new EditorSettingsMenu(core, this, owner, editorBaseMenu);
		addMenu(editorSettingsMenu);
		editorSettingsMenu.open();
	}
}
