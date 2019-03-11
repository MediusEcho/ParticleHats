package com.mediusecho.particlehats.editor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.ui.MenuState;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;

public abstract class EditorMenu {

	protected final Core core;
	protected final Player owner;
	protected final UUID ownerID;
	protected final MenuBuilder menuBuilder;
	
	protected final static EditorAction emptyAction = (event, slot) -> { Core.debug("empty action"); return EditorClickType.NONE; };
	private final ItemStack emptyItem = new ItemStack(Material.STONE);
	
	protected final Map<Integer, EditorAction> actions;
	protected final Map<Integer, Hat> hats;
	protected Inventory inventory;
	
	protected final ItemStack mainMenuButton;
	protected final ItemStack backButton;
	protected final EditorAction backAction;
	
	private long buildTime = 0L;
	private boolean buildLogged = false;
	
	public EditorMenu (Core core, Player owner, final MenuBuilder menuBuilder)
	{
		buildTime = System.nanoTime();
		
		this.core = core;
		this.owner = owner;
		this.ownerID = owner.getUniqueId();
		this.menuBuilder = menuBuilder;	
		
		actions = new HashMap<Integer, EditorAction>();
		hats = new HashMap<Integer, Hat>();
		
		mainMenuButton = ItemUtil.createItem(Material.NETHER_STAR, Message.EDITOR_MISC_MAIN_MENU.getValue());
		backButton = ItemUtil.createItem(Material.NETHER_STAR, Message.EDITOR_MISC_GO_BACK.getValue());
		
		backAction = (event, slot) ->
		{
			menuBuilder.goBack();
			return EditorClickType.NEUTRAL;
		};
	}

	/**
	 * Handle clicking inside this menu
	 * @param event
	 * @param clickedName
	 * @param inMenu
	 * @return
	 */
	public EditorClickType onClick (InventoryClickEvent event, final int slot, final boolean inMenu)
	{
		if (inMenu) {
			return getAction(slot).onClick(new EditorClickEvent(event), slot);
		}
		
		else {
			return onClickOutside(event, slot);
		}
	}
	
	/**
	 * Gets called any time the player is clicking outside of a menu
	 * @param event
	 * @param slot
	 * @return
	 */
	public EditorClickType onClickOutside (InventoryClickEvent event, final int slot) { return EditorClickType.NONE; }
	
	/**
	 * Builds this menu
	 */
	protected abstract void build ();
	
	/**
	 * Optional<br>
	 * Called before the menu is closed
	 * @param forced Set to true when all menus are forced to close
	 */
	protected void onClose (boolean forced) {}
	
	/**
	 * Optional<br>
	 * Update this menu
	 */
	protected void onTick (int ticks) {}
	
	/**
	 * Returns the EditorAction in this slot
	 * @param slot
	 * @return
	 */
	protected EditorAction getAction (int slot) 
	{
		if (actionExists(slot)) {
			return actions.get(slot);
		}
		return emptyAction;
	}
	
	/**
	 * Set the EditorAction for this slot
	 * @param slot
	 * @param action
	 */
	protected void setAction (int slot, EditorAction action) {
		actions.put(slot, action);
	}
	
	/**
	 * Returns true if an action exists in the current slot
	 * @param slot
	 */
	protected boolean actionExists (int slot) {
		return actions.containsKey(slot);
	}
	
	/**
	 * Returns the item found at this slot
	 * @param slot
	 * @return
	 */
	protected ItemStack getItem (int slot) 
	{
		if (itemExists(slot)) {
			return inventory.getItem(slot);
		}
		return emptyItem;
	}
	
	/**
	 * Checks to see if an item exists in this slot
	 * @param slot
	 * @return
	 */
	protected boolean itemExists (int slot) {
		return inventory.getItem(slot) != null;
	}
	
	/**
	 *
	 * @param slot
	 * @param item
	 */
	protected void setItem (int slot, ItemStack item) {
		inventory.setItem(slot, item);
	}
	
	/**
	 * Sets this menus action and item for the given slot
	 * @param slot
	 * @param action
	 * @param item
	 */
	protected void setButton (int slot, ItemStack item, EditorAction action)
	{
		setAction(slot, action);
		setItem(slot, item);
	}
	
	/**
	 * Returns the Hat object found at this slot
	 * @param slot
	 * @return
	 */
	public Hat getHat (int slot) {
		return hats.get(slot);
	}
	
	/**
	 * Sets the hat that belongs in this slot
	 * @param slot
	 * @param hat
	 */
	public void setHat (int slot, Hat hat) {
		hats.put(slot, hat);
	}
	
	/**
	 * Opens this menu
	 */
	public void open ()
	{
		menuBuilder.setOwnerState(MenuState.SWITCHING);
		owner.openInventory(inventory);
		
		logBuildTime();
	}
	
	/**
	 * Logs how long this menu took to open
	 */
	protected void logBuildTime () 
	{
		if (!buildLogged)
		{
			Core.debug("menu took " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - buildTime) + "ms to open");
			buildLogged = true;
		}
	}
	
	/**
	 * Returns an index relative to 0 starting at the startingIndex<br>
	 * eg: (10, 10, 2) -> 0, (11, 10, 2) -> 1, (17, 10, 2) -> 7
	 * @param slot Slot in inventory
	 * @param startingIndex Where to start clamping in the inventory
	 * @param offset How many slots to ignore in each row
	 * @return
	 */
	protected int getClampedIndex (int slot, int startingIndex, int offset) {
		return Math.max((slot - (((slot / 9) - 1) * offset) - startingIndex), 0);
	}
	
	/**
	 * Returns an inventory slot relative to 0 starting at startingIndex<br>
	 * eg: (0, 10, 2) -> 10, (7, 10, 2) -> 19
	 * @param slot Clamped index
	 * @param startingIndex Which slot 0 is relative to
	 * @param offset How many slots are ignored in each row
	 * @return
	 */
	protected int getNormalIndex (int slot, int startingIndex, int offset) {
		return (slot + ((slot / (9 - offset)) * offset) + startingIndex);
	}
	
	protected int getWrappedIndex (int slot, int startingIndex, int offset) {
		return getNormalIndex(getClampedIndex(slot, startingIndex, offset), startingIndex, offset);
	}
	
	/**
	 * Plays the EDITOR_SOUND_ID found in config.yml
	 */
	public void playSound (EditorClickType clickType)
	{
		if (SettingsManager.EDITOR_SOUND_ENABLED.getBoolean())
		{
			Sound sound = SettingsManager.EDITOR_SOUND_ID.getSound();
			if (sound != null)
			{
				float volume = SettingsManager.EDITOR_SOUND_VOLUME.getFloat();
				float pitch = SettingsManager.EDITOR_SOUND_PITCH.getFloat();
				float modifier = clickType.getModifier();
				float p = (float) MathUtil.clamp(pitch + modifier, 0, 2);
				
				owner.playSound(owner.getLocation(), sound, volume, p);
			}
		}
	}
	
	/**
	 * Action to perform when clicking on this item inside an EditorMenu
	 * @author MediusEcho
	 *
	 */
	@FunctionalInterface
	protected interface EditorAction {
		public EditorClickType onClick (EditorClickEvent event, int slot);
	}
	
	/**
	 * Action to perform when selecting an item
	 * @author MediusEcho
	 *
	 */
	@FunctionalInterface
	protected interface EditorItemCallback {
		public void onSelect (ItemStack item);
	}
	
	/**
	 * Action to perform when selecting a ParticleAction
	 * @author MediusEcho
	 *
	 */
	@FunctionalInterface
	protected interface EditorActionCallback {
		public void onSelect (ParticleAction action);
	}
	
	/**
	 * Action to perform when selecting a Sound
	 * @author MediusEcho
	 *
	 */
	@FunctionalInterface
	protected interface EditorSoundCallback {
		public void onSelect (Sound sound);
	}
	
	/**
	 * Action to perform when selected a String
	 * @author MediusEcho
	 *
	 */
	@FunctionalInterface
	protected interface EditorStringCallback {
		public void onSelect (String string);
	}
	
	/**
	 * Action to perform when selecting a ParticleEffect
	 * @author MediusEcho
	 *
	 */
	@FunctionalInterface
	protected interface EditorParticleCallback {
		public void onSelect (ParticleEffect effect);
	}
	
	/**
	 * Action to perform when selecting an Object
	 * @author MediusEcho
	 *
	 */
	@FunctionalInterface
	protected interface EditorObjectCallback {
		public void onSelect (Object obj);
	}
	
	/**
	 * Action to perform when a menu has performed an action
	 * @author MediusEcho
	 *
	 */
	@FunctionalInterface
	protected interface EditorGenericCallback {
		public void onExecute ();
	}
	
	/**
	 * Adds some shortcut methods for determining click types
	 * @author MediusEcho
	 *
	 */
	protected class EditorClickEvent
	{
		private final InventoryClickEvent event;
		
		private final boolean isMiddleClick;
		private final boolean isShiftLeftClick;
		private final boolean isShiftRightClick;
		
		public EditorClickEvent (final InventoryClickEvent event)
		{
			this.event = event;
			
			isShiftLeftClick  = event.isLeftClick() && event.isShiftClick();
			isShiftRightClick = event.isRightClick() && event.isShiftClick();
			isMiddleClick     = event.getClick().equals(ClickType.MIDDLE);
		}
		
		/**
		 * Get this EditorClickEvent's InventoryClickEvent
		 * @return
		 */
		public InventoryClickEvent getEvent () {
			return event;
		}
		
		/**
		 * Returns true if this event is a left click
		 * @return
		 */
		public boolean isLeftClick () {
			return event.isLeftClick();
		}
		
		/**
		 * Returns true if this event is a right click
		 * @return
		 */
		public boolean isRightClick () {
			return event.isRightClick();
		}
		
		/**
		 * Returns true if this event is a shift click
		 * @return
		 */
		public boolean isShiftClick () {
			return event.isShiftClick();
		}
		
		/**
		 * Returns true if this event is a left shift click
		 * @return
		 */
		public boolean isShiftLeftClick () {
			return isShiftLeftClick;
		}
		
		/**
		 * Returns true if this event is a right shift click
		 * @return
		 */
		public boolean isShiftRightClick () {
			return isShiftRightClick;
		}
		
		/**
		 * Returns true if this event is a middle click
		 * @return
		 */
		public boolean isMiddleClick () {
			return isMiddleClick;
		}
	}
	
	/**
	 * Used to provide feedback for when a player is editing properties
	 * @author MediusEcho
	 *
	 */
	protected enum EditorClickType {
		
		/**
		 * The player is not clicking inside a menu
		 */
		NONE,
		
		/**
		 * The player is clicking inside a menu
		 */
		NEUTRAL,
		
		/**
		 * The player is increasing a value inside a menu
		 */
		POSITIVE,
		
		/**
		 * The player is decreasing a value inside a menu
		 */
		NEGATIVE;
		
		public float getModifier ()
		{
			if (this == NONE || this == NEUTRAL) {
				return 0;
			}
			
			float mod = SettingsManager.EDITOR_SOUND_MODIFIER.getFloat();
			return mod * (this == POSITIVE ? 1f : -1f);
		}
	}
}
