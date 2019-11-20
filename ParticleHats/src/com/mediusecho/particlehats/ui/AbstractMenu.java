package com.mediusecho.particlehats.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.util.ItemUtil;

/**
 * Basic foundation for all menus
 * @author MediusEcho
 *
 */
public abstract class AbstractMenu {

	protected final ParticleHats core;
	
	protected final MenuManager menuManager;
	
	protected final Player owner;
	protected final UUID ownerID;
	
	protected final Map<Integer, MenuAction> actions;
	protected final static MenuAction emptyAction = (event, slot) -> { return MenuClickResult.NONE; };
	
	protected final ItemStack mainMenuButtonItem;
	protected final ItemStack backButtonItem;
	protected final MenuAction backButtonAction;
	
	public AbstractMenu (final ParticleHats core, final MenuManager menuManager, final Player owner)
	{
		this.core = core;
		
		this.menuManager = menuManager;
		
		this.owner = owner;
		this.ownerID = owner.getUniqueId();
		
		this.mainMenuButtonItem = ItemUtil.createItem(Material.NETHER_STAR, Message.EDITOR_MISC_MAIN_MENU);
		this.backButtonItem = ItemUtil.createItem(Material.NETHER_STAR, Message.EDITOR_MISC_GO_BACK);
		this.backButtonAction = (event, slot) ->
		{
			menuManager.closeCurrentMenu();
			return MenuClickResult.NEUTRAL;
		};
		
		actions = new HashMap<Integer, MenuAction>();
	}
	
	/**
	 * Get the Player that owns this menu
	 * @return
	 */
	public Player getOwner () {
		return owner;
	}
	
	/**
	 * Get the UUID of the player that owns this menu
	 * @return
	 */
	public UUID getOwnerID () {
		return ownerID;
	}
	
	/**
	 * Respond to click events for this menu
	 * @param event
	 * @param slot Inventory slot the player is clicking on
	 * @param inMenu True if the player is clicking inside this menu
	 * @return
	 */
	public MenuClickResult onClick (InventoryClickEvent event, final int slot, final boolean inMenu)
	{
		if (inMenu) {
			return getAction(slot).onClick(new MenuClickEvent(event), slot);
		}
		
		else {
			return onClickOutside(event, slot);
		}
	}
	
	/**
	 * Respond to click events outside this menu
	 * @param event
	 * @param slot
	 * @return
	 */
	public MenuClickResult onClickOutside (InventoryClickEvent event, final int slot) { return MenuClickResult.NONE; }

	
	/**
	 * Assign a MenuAction to the given slot
	 * @param slot
	 * @param action
	 */
	protected void setAction (int slot, MenuAction action) {
		actions.put(slot, action);
	}
	
	/**
	 * Get the MenuAction at the given slot
	 * @param slot
	 * @return
	 */
	protected MenuAction getAction (int slot)
	{
		if (actionExists(slot)) {
			return actions.get(slot);
		}
		return emptyAction;
	}
	
	/**
	 * Check to see if a MenuAction exists at the given slot
	 * @param slot
	 * @return True if a MenuAction exists at the given slot
	 */
	protected boolean actionExists (int slot) {
		return actions.containsKey(slot);
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
	
	
	/**
	 * Open this menu
	 */
	public abstract void open ();
	
	/**
	 * Checks to see if the inventory belongs to this menu
	 * @param inventory
	 * @return
	 */
	public abstract boolean hasInventory (Inventory inventory);
	
	/**
	 * Build this menus contents
	 */
	protected abstract void build ();
	
	/**
	 * Called when the menu is closed
	 * @param forced True if this menu is being closed without the players involvement
	 */
	public abstract void onClose (boolean forced);
	
	/**
	 * Called every tick. Used to update the menus visual content
	 * @param ticks
	 */
	public abstract void onTick (int ticks);
	
	/**
	 * Get the name of this menu
	 */
	public abstract String getName ();
	
	/**
	 * Action to perform when clicking on an item
	 * @author MediusEcho
	 *
	 */
	@FunctionalInterface
	public interface MenuAction {
		public MenuClickResult onClick (MenuClickEvent event, int slot);
	}
	
	/**
	 * Action to perform when selecting an Object
	 * @author MediusEcho
	 *
	 */
	@FunctionalInterface
	protected interface MenuObjectCallback {
		public void onSelect (Object obj);
	}
	
	/**
	 * Action to perform when returning from a menu
	 * @author MediusEcho
	 *
	 */
	@FunctionalInterface
	protected interface MenuCallback {
		public void onCallback ();
	}
	
	protected class MenuClickEvent {

		private final InventoryClickEvent event;
		
		private final boolean isMiddleClick;
		private final boolean isShiftLeftClick;
		private final boolean isShiftRightClick;
		
		public MenuClickEvent (final InventoryClickEvent event)
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
	
	public enum MenuClickResult {

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
		
		public double getModifier ()
		{
			if (this == NONE || this == NEUTRAL) {
				return 0;
			}
			
			double mod = SettingsManager.EDITOR_SOUND_MODIFIER.getDouble();
			return mod * (this == POSITIVE ? 1f : -1f);
		}
	}
	
	public class MenuButton {
		
		private final ItemStack item;
		private final MenuAction action;
		
		public MenuButton (final ItemStack item, final MenuAction action)
		{
			this.item = item;
			this.action = action;
		}
		
		public ItemStack getItem () {
			return item;
		}
		
		public MenuAction getAction () {
			return action;
		}
		
	}
}