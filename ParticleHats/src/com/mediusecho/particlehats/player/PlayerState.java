package com.mediusecho.particlehats.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.HatReference;
import com.mediusecho.particlehats.ui.MenuManager;

public class PlayerState extends EntityState {
	
	private final Player owner;
	
	private MenuManager menuManager;
	
	private MetaState metaState = MetaState.NONE;
	
	private int metaStateTime = 15;
	private int metaDescriptionLine = 0;
	
	private Hat pendingPurchaseHat;
	
	private List<HatReference> purchasedHats;
	private List<String> legacyPurchasedHats;
	
	private List<ItemStack> recentItems;
	
	public PlayerState (final Player owner)
	{
		super(owner);
		
		this.owner = owner;
		purchasedHats = new ArrayList<HatReference>();
		legacyPurchasedHats = new ArrayList<String>();
		
		recentItems = new ArrayList<ItemStack>();
	}
	
	/**
	 * Get the owner of this PlayerState class
	 * @return
	 */
	public Player getOwner () {
		return owner;
	}
	
	/**
	 * Set this players MenuManager class
	 * @param menuManager
	 */
	public void setMenuManager (MenuManager menuManager) {
		this.menuManager = menuManager;
	}
	
	/**
	 * Returns this players MenuManager class
	 * @return
	 */
	public MenuManager getMenuManager () {
		return menuManager;
	}
	
	/**
	 * Returns true if this player has a MenuManager class
	 * @return
	 */
	public boolean hasMenuManager () {
		return menuManager != null;
	}
	
	/**
	 * Check to see if this player has the editor open
	 * @return
	 */
	public boolean hasEditorOpen () 
	{
		if (hasMenuManager() && menuManager instanceof EditorMenuManager) {
			return true;
		}
		return false;
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
	 * Add this item to the players list of recent items
	 * @param item
	 */
	public void addRecentItem (ItemStack item)
	{
		if (recentItems.contains(item)) {
			return;
		}
		
		if (recentItems.size() >= 20) {
			recentItems.remove(0);
		}
		
		recentItems.add(item);
	}
	
	/**
	 * Get a list of items the player recently used when editing
	 * @return
	 */
	public List<ItemStack> getRecentItems () {
		return recentItems;
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
