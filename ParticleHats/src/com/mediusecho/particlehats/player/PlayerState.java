package com.mediusecho.particlehats.player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.HatReference;
import com.mediusecho.particlehats.ui.ActiveParticlesMenu;
import com.mediusecho.particlehats.ui.MenuState;

public class PlayerState {
	
	private final Player owner;
	private final UUID ownerID;
	
	private MenuBuilder menuBuilder;
	private MenuState menuState         = MenuState.CLOSED;
	private MenuState previousMenuState = MenuState.CLOSED;

	private ActiveParticlesMenu activeParticlesMenu;
	
	private MetaState metaState = MetaState.NONE;
	private int metaStateTime = 15;
	private int metaDescriptionLine = 0;
	
	private long lastMoveTime = 0L;
	private long lastCombatTime = 0L;
	
	private AFKState afkState = AFKState.ACTIVE;
	private PVPState pvpState = PVPState.PEACEFUL;
	
	private Location afkLocation;
	
	private List<Hat> activeHats;
	private List<HatReference> purchasedHats;
	
	public PlayerState (final Player owner)
	{
		this.owner = owner;
		this.ownerID = owner.getUniqueId();
		
		activeHats = new ArrayList<Hat>();
		purchasedHats = new ArrayList<HatReference>();
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
	
	/**
	 * Sets this players menu state class
	 * @param menuState
	 */
	public void setMenuState (MenuState menuState) 
	{
		this.previousMenuState = this.menuState;
		this.menuState = menuState;
	}
	
	/**
	 * Returns this players menu state
	 * @return
	 */
	public MenuState getMenuState () {
		return menuState;
	}
	
	/**
	 * Returns this players previous menu state
	 * @return
	 */
	public MenuState getPreviousMenuState () {
		return previousMenuState;
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
	@Nullable
	public Location getAFKLocation () {
		return afkLocation;
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
		return activeHats.size() < 28;
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
	 * Checks to see if the player has purchased this hat
	 * @param hat
	 * @return
	 */
	public boolean hasPurchased (Hat hat) {
		return purchasedHats.contains(hat);
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
