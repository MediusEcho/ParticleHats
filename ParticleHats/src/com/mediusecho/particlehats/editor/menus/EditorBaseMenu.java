package com.mediusecho.particlehats.editor.menus;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.IconData;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorBaseMenu extends EditorMenu {

	private MenuInventory menuInventory;
	
	private boolean isModified = false;
	private boolean isLive = true;
	
	private final ItemStack emptyItem;
	
	private final EditorAction emptyParticleAction;
	private final EditorAction existingParticleAction;
	
	private int rows = 0;
	
	public EditorBaseMenu(Core core, Player owner, MenuBuilder menuBuilder, MenuInventory menuInventory) 
	{
		super(core, owner, menuBuilder);
		this.menuInventory = menuInventory;
		
		rows = menuInventory.getSize() / 9;
		emptyItem = ItemUtil.createItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, Message.EDITOR_EMPTY_SLOT_TITLE, Message.EDITOR_SLOT_DESCRIPTION);
		
		String title = EditorLore.getTrimmedMenuTitle(menuInventory.getTitle(), Message.EDITOR_BASE_MENU_TITLE);//ChatColor.translateAlternateColorCodes('&', StringUtil.getTrimmedMenuTitle("Editing (" + menuInventory.getTitle()));
		inventory = Bukkit.createInventory(null, menuInventory.getSize(), title);
		inventory.setContents(menuInventory.getContents());
		
		emptyParticleAction = (event, slot) ->
		{
			if (event.isLeftClick())
			{				
				menuBuilder.setTargetHat(createHat(slot));
				menuBuilder.setTargetSlot(slot);
				setModified();
				
				EditorMainMenu editorMainMenu = new EditorMainMenu(core, owner, menuBuilder);
				menuBuilder.addMenu(editorMainMenu);
				editorMainMenu.open();
			}
			
			else if (event.isRightClick()) {
				openSettings();
			}
			return EditorClickType.NEUTRAL;
		};
		
		existingParticleAction = (event, slot) ->
		{
			if (event.isLeftClick())
			{
				Hat clickedHat = getHat(slot);
				if (clickedHat != null)
				{
					if (!clickedHat.isLoaded()) {
						core.getDatabase().loadHatData(getName(), slot, clickedHat);
					}
					
					menuBuilder.setTargetHat(clickedHat);
					menuBuilder.setTargetSlot(slot);
					
					EditorMainMenu editorMainMenu = new EditorMainMenu(core, owner, menuBuilder);
					menuBuilder.addMenu(editorMainMenu);
					editorMainMenu.open();
				}
			}
			
			else if (event.isShiftRightClick()) 
			{
				deleteHat(slot);
				return EditorClickType.NEGATIVE;
			}
			
			else if (event.isRightClick()) {
				openSettings();
			}
			return EditorClickType.NEUTRAL;
		};
		
		build();
	}
	
	@Override
	public void onTick (int ticks)
	{		
		if (isLive)
		{
			for (Entry<Integer, Hat> set : menuInventory.getHats().entrySet())
			{
				int slot = set.getKey();
				Hat hat = set.getValue();
				
				if (hat != null)
				{
					IconData iconData = hat.getIconData();
					if (iconData.isLive()) {
						getItem(slot).setType(iconData.getNextMaterial(ticks));
					}
				}
			}
		}
	}
	
	/**
	 * Sets this menus modified flag to true
	 */
	public void setModified () {
		isModified = true;
	}
	
	/**
	 * Changes the Material type of the item in this slot
	 * @param slot Slot where the item exists
	 * @param material New material of this item
	 */
	public void setItemMaterial (int slot, Material material) {
		getItem(slot).setType(material);
	}
	
	/**
	 * Returns true if this menu has been modified and needs to be saved
	 * @return
	 */
	public boolean isModified () {
		return isModified;
	}
	
	/**
	 * Get the name used to save this menu
	 * @return
	 */
	public String getName () {
		return menuInventory.getName();
	}
	
	/**
	 * Returns every hat in this menu
	 * @return
	 */
	public Map<Integer, Hat> getHats () {
		return menuInventory.getHats();
	}
	
	/**
	 * Returns a Hat object from this slot
	 */
	@Override
	public Hat getHat (int slot) {
		return menuInventory.getHat(slot);
	}
	
	@Override
	public void setHat (int slot, Hat hat) {
		menuInventory.setHat(slot, hat);
	}
	
	/**
	 * Removes the hat at this slot
	 * @param slot
	 */
	public void removeHat (int slot) {
		menuInventory.removeHat(slot);
	}
	
	/**
	 * Removes the hat and item at this slot
	 * @param slot
	 */
	public void removeButton (int slot)
	{
		removeHat(slot);
		setButton(slot, emptyItem, emptyParticleAction);
	}
	
	/**
	 * Get this menus inventory
	 * @return
	 */
	public Inventory getInventory () {
		return inventory;
	}
	
	/**
	 * Set this menus title
	 * @param title
	 */
	public void setTitle (String title)
	{
		menuInventory.setTitle(title);
		String editingTitle =  EditorLore.getTrimmedMenuTitle(title, Message.EDITOR_BASE_MENU_TITLE);// ChatColor.translateAlternateColorCodes('&', StringUtil.getTrimmedMenuTitle("Editing (" + title));
		
		Inventory replacementInventory = Bukkit.createInventory(null, inventory.getSize(), editingTitle);
		replacementInventory.setContents(inventory.getContents());
		inventory = replacementInventory;
		
		core.getDatabase().saveMenuTitle(getName(), title);
	}
	
	/**
	 * Set this menus size
	 * @param rows How many rows this menu will have (chest = 3, double chest = 6)
	 */
	public void resize (int rows)
	{
		if (this.rows != rows)
		{
			Inventory replacementInventory = Bukkit.createInventory(null, 9 * rows, menuInventory.getTitle());
			
			// inventory.setContents() only works when resizing the menus to a smaller size. so we need to use a loop to account for the other option
			for (int i = 0; i < replacementInventory.getSize(); i++)
			{
				try {
					replacementInventory.setItem(i, inventory.getItem(i));
				} catch (ArrayIndexOutOfBoundsException e) {}
			}
			
			// Fill in any empty slots
			if (rows > this.rows)
			{
				for (int i = inventory.getSize(); i < replacementInventory.getSize(); i++) {
					replacementInventory.setItem(i, emptyItem);
				}
			}
			
			inventory = replacementInventory;
			this.rows = rows;
			core.getDatabase().saveMenuSize(getName(), rows);
		}
	}
	
	/**
	 * Get how many rows are in this menu
	 * @return
	 */
	public int getRowCount () {
		return rows;
	}
	
	public String getTitle ()  {
		return menuInventory.getTitle();
	}
	
	/**
	 * Set whether live updates are enabeld
	 */
	public void toggleLive () {
		isLive = !isLive;
	}
	
	/**
	 * Returns whether live updates are enabled
	 * @return
	 */
	public boolean isLive () {
		return isLive;
	}
	
	/**
	 * 
	 * @param currentSlot
	 * @param newSlot
	 * @param swapping
	 */
	public void changeSlots (int currentSlot, int newSlot, boolean swapping)
	{
		ItemStack currentItem = getItem(currentSlot);
		ItemStack swappingItem = getItem(newSlot);
		EditorAction currentAction = getAction(currentSlot);
		EditorAction swappingAction = getAction(newSlot);
		
		Hat currentHat = getHat(currentSlot);
		currentHat.setSlot(newSlot);
		
		if (swapping)
		{
			Hat swappingHat = getHat(newSlot);
			swappingHat.setSlot(currentSlot);
			setHat(currentSlot, swappingHat);
		}
		
		else {
			removeHat(currentSlot);
		}
		
		setButton(currentSlot, swappingItem, swappingAction);
		setButton(newSlot, currentItem, currentAction);
		setHat(newSlot, currentHat);
		
		menuBuilder.setTargetSlot(newSlot);
		
		core.getDatabase().changeSlot(getName(), currentSlot, newSlot, swapping);
	}
	
	public void cloneHat (int currentSlot, int newSlot)
	{
		Hat currentHat = getHat(currentSlot);
		Hat clonedHat = currentHat.visualCopy();
		
		setHat(newSlot, clonedHat);
		setButton(newSlot, new ItemStack(clonedHat.getMaterial()), existingParticleAction);
		core.getDatabase().cloneHatData(getName(), currentSlot, newSlot);
	}
	
	public void onHatNameChange (Hat hat, int slot) {
		ItemUtil.setItemName(getItem(slot), hat.getDisplayName());
	}
	
	/**
	 * Creates and returns a new hat object
	 * @param slot
	 * @return
	 */
	private Hat createHat (int slot)
	{
		ItemStack emptyItem = ItemUtil.createItem(Material.SUNFLOWER, Message.EDITOR_MISC_NEW_PARTICLE.getValue());
		setButton(slot, emptyItem, existingParticleAction);
		
		Hat hat = new Hat();
		hat.setSlot(slot);
		
		setHat(slot, hat);
		
		core.getDatabase().createHat(menuInventory.getName(), slot);
		return hat;
	}
	
	/**
	 * Deletes the hat in the current slot
	 * @param slot
	 */
	private void deleteHat (int slot)
	{
		setButton(slot, emptyItem, emptyParticleAction);
		menuInventory.removeHat(slot);

		core.getDatabase().deleteHat(menuInventory.getName(), slot);
	}
	
	private void openSettings ()
	{
		EditorSettingsMenu editorSettingsMenu = new EditorSettingsMenu(core, owner, menuBuilder);
		menuBuilder.addMenu(editorSettingsMenu);
		editorSettingsMenu.open();
	}
	
	@Override
	protected void build() 
	{
		int size = menuInventory.getSize();
		for (int i = 0; i < size; i++)
		{
			if (!itemExists(i)) {
				setButton(i, emptyItem, emptyParticleAction);
			}
			
			else {
				setAction(i, existingParticleAction);
			}
			
			Hat hat = menuInventory.getHat(i);
			if (hat != null) {
				setHat(i, hat);
			}
		}
	}
}
