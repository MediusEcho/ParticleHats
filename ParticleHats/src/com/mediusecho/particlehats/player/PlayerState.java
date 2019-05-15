package com.mediusecho.particlehats.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.HatReference;
import com.mediusecho.particlehats.ui.ActiveParticlesMenu;
import com.mediusecho.particlehats.ui.GuiState;
import com.mediusecho.particlehats.ui.Menu;

public class PlayerState {
	
	private final Player owner;
	private final UUID ownerID;
	
	private MenuBuilder menuBuilder;

	private ActiveParticlesMenu activeParticlesMenu;
	private Menu purchaseMenu;
	
	private Menu openMenu;
	private Menu previousOpenMenu;
	private Map<String, Menu> openMenuCache;
	
	private MetaState metaState = MetaState.NONE;
	
	private GuiState guiState = GuiState.INNACTIVE;
	private GuiState previousGuiState = GuiState.NONE;
	
	private int metaStateTime = 15;
	private int metaDescriptionLine = 0;
	
	private long lastMoveTime = 0L;
	private long lastCombatTime = 0L;
	
	private AFKState afkState = AFKState.ACTIVE;
	private PVPState pvpState = PVPState.PEACEFUL;
	
	private Location afkLocation;
	
	private Hat pendingPurchaseHat;
	
	private List<Hat> activeHats;
	private List<HatReference> purchasedHats;
	private List<String> legacyPurchasedHats;
	
	public PlayerState (final Player owner)
	{
		this.owner = owner;
		this.ownerID = owner.getUniqueId();
		
		activeHats = new ArrayList<Hat>();
		purchasedHats = new ArrayList<HatReference>();
		legacyPurchasedHats = new ArrayList<String>();
		
		openMenuCache = new HashMap<String, Menu>();
	}
	
	/**
	 * Get the owner of this PlayerState class
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
	 * Set this players menu builder class
	 * @param menuBuidler
	 */
	public void setMenuBuilder (MenuBuilder menuBuilder) {
		this.menuBuilder = menuBuilder;
	}
	
	/**
	 * Returns this players menu builder class
	 * @return
	 */
	public MenuBuilder getMenuBuilder () {
		return menuBuilder;
	}
	
	public boolean isEditing () {
		return menuBuilder != null;
	}
	
	/**
	 * Set this players active particles menu
	 * @param activeParticlesMenu
	 */
	public void setActiveParticlesMenu (ActiveParticlesMenu activeParticlesMenu) {
		this.activeParticlesMenu = activeParticlesMenu;
	}
	
	/**
	 * Get this players active particles menu
	 * @return
	 */
	public ActiveParticlesMenu getActiveParticlesMenu () {
		return activeParticlesMenu;
	}
	
	/**
	 * Sets this players active particle menu as null
	 */
	public void removeActiveParticlesMenu () {
		activeParticlesMenu = null;
	}
	
	/**
	 * Set this players purchase menu
	 * @param purchaseMenu
	 */
	public void setPurchaseMenu (Menu purchaseMenu) {
		this.purchaseMenu = purchaseMenu;
	}
	
	/**
	 * Get this players purchase menu
	 * @return
	 */
	public Menu getPurchaseMenu () {
		return purchaseMenu;
	}
	
	/**
	 * Remove this players purchase menu
	 */
	public void removePurchaseMenu () {
		purchaseMenu = null;
	}
	
	public void setOpenMenu (Menu menu, boolean cacheMenu)
	{
		if (menu != null)
		{
			previousOpenMenu = openMenu;
			openMenu = menu;
			
			if (cacheMenu) 
			{
				if (!openMenuCache.containsKey(menu.getName())) {
					openMenuCache.put(menu.getName(), menu);
				}
			}
		}
	}
	
	/**
	 * Set the players current open menu
	 * @param menu
	 */
	public void setOpenMenu (Menu menu) {
		setOpenMenu(menu, true);
	}
	
	/**
	 * Gets the players current open menu
	 * @return
	 */
	public Menu getOpenMenu () {
		return openMenu;
	}
	
	/**
	 * Get the players previously open menu
	 * @return
	 */
	public Menu getPreviousOpenMenu () {
		return previousOpenMenu;
	}
	
	/**
	 * Gets a cached menu the player has recently opened
	 * @param menuName
	 * @return
	 */
	public Menu getOpenMenu (String menuName)
	{
		if (openMenuCache.containsKey(menuName)) {
			return openMenuCache.get(menuName);
		}
		return null;
	}
	
	/**
	 * Resets the players current open menu
	 */
	public void closeOpenMenu () {
		openMenu = null;
	}
	
	/**
	 * Clears the players menu cache
	 */
	public void clearMenuCache () {
		openMenuCache.clear();
	}
	
	/**
	 * Checks to see if the player has a menu open
	 * @return
	 */
	public boolean hasMenuOpen () {
		return openMenu != null;
	}
	
	/**
	 * Set which description line is being edited
	 * @param line
	 */
	public void setMetaDescriptionLine (int line) {
		this.metaDescriptionLine = line;
	}
	
	/**
	 * Get which description line is being edited
	 * @return
	 */
	public int getMetaDescriptionLine () {
		return metaDescriptionLine;
	}
	
	/**
	 * Set the time this player last moved
	 * @param time
	 */
	public void setLastMoveTime (long time) {
		lastMoveTime = time;
	}
	
	/**
	 * Get the time since this player has moved
	 * @return
	 */
	public long getLastMoveTime () {
		return lastMoveTime;
	}
	
	/**
	 * Set the time this player attacked
	 * @param time
	 */
	public void setLastCombatTime (long time) {
		lastCombatTime = time;
	}
	
	/**
	 * Get the time since this player has attacked
	 * @return
	 */
	public long getLastCombatTime () {
		return lastCombatTime;
	}
	
	/**
	 * Set the players AFKState
	 * @param state
	 */
	public void setAFKState (AFKState state) {
		afkState = state;
	}
	
	/**
	 * Get the players AFKState
	 * @return
	 */
	public AFKState getAFKState () {
		return afkState;
	}
	
	/**
	 * Set the players PVPState
	 * @param state
	 */
	public void setPVPState (PVPState state) {
		pvpState = state;
	}
	
	/**
	 * Get the players PVPState
	 * @return
	 */
	public PVPState getPVPState () {
		return pvpState;
	}
	
	/**
	 * Set the location this player is afk at
	 * @param location
	 */
	public void setAFKLocation (Location location) {
		afkLocation = location;
	}
	
	/**
	 * Get the location this player went afk at
	 * @return
	 */
	public Location getAFKLocation () {
		return afkLocation;
	}
	
	/**
	 * Set which hat this player is trying to purchase
	 * @param hat
	 */
	public void setPendingPurchase (Hat hat) {
		pendingPurchaseHat = hat;
	}
	
	/**
	 * Gets the hat this player is trying to purchase
	 * @return
	 */
	public Hat getPendingPurchase () {
		return pendingPurchaseHat;
	}
	
	/**
	 * Set this players MetaState
	 * @param metaState
	 */
	public void setMetaState (MetaState metaState) 
	{
		this.metaState = metaState;
		metaStateTime = SettingsManager.EDITOR_META_TIME_LIMIT.getInt();
	}
	
	/**
	 * Get this players MetaState
	 * @return
	 */
	public MetaState getMetaState () {
		return metaState;
	}
	
	/**
	 * Get the current time left for the MetaState
	 * @return
	 */
	public int getMetaStateTime () {
		return metaStateTime--;
	}
	
	/**
	 * Adds a hat this this players active hat list
	 * @param hat
	 */
	public void addHat (Hat hat) {
		activeHats.add(hat);
	}
	
	/**
	 * Gets all active hats
	 * @return
	 */
	public List<Hat> getActiveHats () {
		return activeHats;
	}
	
	/**
	 * Get how many hats this player has equipped
	 * @return
	 */
	public int getHatCount () {
		return activeHats.size();
	}
	
	/**
	 * Checks to see if the player can equip a hat
	 * @return
	 */
	public boolean canEquip () {
		return activeHats.size() < SettingsManager.MAXIMUM_HAT_LIMIT.getInt();
	}
	
	/**
	 * Removes all active hats
	 */
	public void clearActiveHats () {
		activeHats.clear();
	}
	
	/**
	 * Remove the hat at index
	 * @param index
	 */
	public void removeHat (int index) {
		activeHats.remove(index);
	}
	
	/**
	 * Removes this hat from the players active hats list
	 * @param hat
	 */
	public void removeHat (Hat hat) {
		activeHats.remove(hat);
	}
	
	/**
	 * Adds a new purchased hat to the list
	 * @param hat
	 */
	public void addPurchasedHat (Hat hat) {
		purchasedHats.add(new HatReference(hat.getMenu(), hat.getSlot()));
	}
	
	/**
	 * Adds a new purchased hat to the list
	 * @param hat
	 */
	public void addPurchasedHat (HatReference hat) {
		purchasedHats.add(hat);
	}
	
	/**
	 * Gets a list of all purchased hats
	 * @return
	 */
	public List<HatReference> getPurchasedHats () {
		return purchasedHats;
	}
	
	/**
	 * Removes all purchased hats
	 */
	public void clearPurchases () {
		purchasedHats.clear();
	}
	
	/**
	 * Adds a legacy purchased hat
	 * @param legacyPath
	 */
	public void addLegacyPurchasedHat (String legacyPath) 
	{
		if (!legacyPurchasedHats.contains(legacyPath)) {
			legacyPurchasedHats.add(legacyPath);
		}
	}
	
	/**
	 * Checks to see if the player has purchased this hat
	 * @param hat
	 * @return
	 */
	public boolean hasPurchased (Hat hat) 
	{
		HatReference reference = new HatReference(hat.getMenu(), hat.getSlot());
		if (purchasedHats.contains(reference)) {
			return true;
		}
		
		if (SettingsManager.CHECK_AGAINST_LEGACY_PURCHASES.getBoolean())
		{
			if (legacyPurchasedHats.contains(hat.getLegacyPurchaseID())) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Set this players GuiState
	 * @param guiState
	 */
	public void setGuiState (GuiState guiState) {
		this.guiState = guiState;
	}
	
	/**
	 * Get this players GuiState
	 * @return
	 */
	public GuiState getGuiState () {
		return guiState;
	}
	
	/**
	 * Get this players previous GuiState
	 * @return
	 */
	public GuiState getPreviousGuiState () {
		return previousGuiState;
	}
	
	public enum AFKState
	{
		ACTIVE,
		AFK;
	}
	
	public enum PVPState
	{
		PEACEFUL,
		ENGAGED;
	}
}
