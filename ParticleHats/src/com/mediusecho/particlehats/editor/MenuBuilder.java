package com.mediusecho.particlehats.editor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.database.type.DatabaseType;
import com.mediusecho.particlehats.database.type.mysql.MySQLDatabase;
import com.mediusecho.particlehats.editor.EditorMenu.EditorClickType;
import com.mediusecho.particlehats.editor.menus.EditorBaseMenu;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.ui.MenuState;

public class MenuBuilder {

	private final Core core;
	private final Player owner;
	private final UUID ownerID;
	private PlayerState ownerState;
	
	private Deque<EditorMenu> activeMenus;
	private EditorBaseMenu editorMenu;
	
	// Keep track of the current hat and slot we are editing
	private Hat targetHat;
	private Hat targetNodeHat;
	private int targetSlot;
	
	public MenuBuilder (final Core core,  final Player owner, final PlayerState ownerState, MenuInventory inventory)
	{
		this.core = core;
		this.owner = owner;
		this.ownerID = owner.getUniqueId();
		this.ownerState = ownerState;
		
		activeMenus = new ArrayDeque<EditorMenu>();
		
		editorMenu = new EditorBaseMenu(core, owner, this, inventory);
		addMenu(editorMenu);
	}
	
	public void onClick(InventoryClickEvent event, final boolean inMenu)
	{
		EditorMenu em = activeMenus.peekLast();
		if (em != null)
		{
			EditorClickType ct = em.onClick(event, event.getRawSlot(), inMenu);
			if (ct != EditorClickType.NONE) {
				em.playSound(ct);
			}
		}
	}
	
	public void onTick (int ticks)
	{
		EditorMenu em = activeMenus.peekLast();
		if (em != null) {
			em.onTick(ticks);
		}
	}
		
	public void onClose ()
	{
		DatabaseType databaseType = core.getDatabaseType();
		if (databaseType == DatabaseType.MYSQL)
		{
			MySQLDatabase mysqlDatabase = (MySQLDatabase)core.getDatabase();
			
			for (Entry<Integer, Hat> hats : editorMenu.getHats().entrySet())
			{
				Hat hat = hats.getValue();
				if (hat.isModified())
				{
					String sqlQuery = hat.getSQLUpdateQuery();
					Core.debug("saving hat with query: " + sqlQuery);
					
					mysqlDatabase.saveIncremental(getEditingMenu().getName(), hats.getKey(), hat.getSQLUpdateQuery());
					hat.clearPropertyChanges();
				}
			}
		}
		
		// Forces every open menu to close and save changes
		for (EditorMenu em : activeMenus) {
			em.onClose(true);
		}
	}
	
	public void startEditing ()
	{
		ownerState.setMenuState(MenuState.BUILDING);
		
		setTargetSlot(-1);
		setTargetHat(null);
		
		editorMenu.open();
	}
	
	/**
	 * Sets the owners <b>MenuState</b>
	 * @param state
	 */
	public void setOwnerState (MenuState state) {
		ownerState.setMenuState(state);
	}
	
	/**
	 * Set the owners <b>MetaState</b>
	 * @param state
	 */
	public void setOwnerState (MetaState state) {
		ownerState.setMetaState(state);
	}
	
	/**
	 * Returns the PlayerState object belonging to the menu builder owner
	 * @return
	 */
	public PlayerState getOwnerState () {
		return ownerState;
	}
	
	/**
	 * Set this MenuBuilders target hat
	 * @param hat
	 */
	public void setTargetHat (Hat hat) {
		this.targetHat = hat;
	}
	
	/**
	 * Hat hierarchy: Hat (Base Hat) -> Node Hat -> Node Hat (Target Hat)
	 * 
	 * Returns this MenuBuilders target hat
	 * @return
	 */
	public Hat getTargetHat () 
	{
		if (targetNodeHat == null) {
			return targetHat;
		}
		return targetNodeHat;
	}
	
	/** 
	 * Returns the base target hat for this MenuBuilder
	 * @return
	 */
	public Hat getBaseHat () {
		return targetHat;
	}
	
	/**
	 * Set this MenuBuilders target slot
	 * @param slot
	 */
	public void setTargetSlot (int slot) {
		this.targetSlot = slot;
	}
	
	/**
	 * Sets this MenuBuilders target node hat
	 * @param hat
	 */
	public void setTargetNodeHat (Hat hat) {
		targetNodeHat = hat;
	}
	
	/**
	 * Returns this MenuBuilders target slot
	 * @return
	 */
	public int getTargetSlot () {
		return targetSlot;
	}
	
	/**
	 * Adds a new EditorMenu to our menu stack
	 * @param e
	 */
	public void addMenu (EditorMenu e) {
		activeMenus.add(e);
	}
	
	/**
	 * Returns the player to the previous menu
	 */
	public void goBack ()
	{
		if (activeMenus.size() > 1) {
			activeMenus.pollLast().onClose(false);
		}
		activeMenus.getLast().open();
	}
	
	/**
	 * Opens the most current menu in our queue
	 */
	public void openCurrentMenu () {
		activeMenus.getLast().open();
	}
	
	/**
	 * Returns the EditorBaseMenu class
	 * @return
	 */
	public EditorBaseMenu getEditingMenu () {
		return editorMenu;
	}
	
	public void onHatNameChange () {
		editorMenu.onHatNameChange(targetHat, targetSlot);
	}
}
