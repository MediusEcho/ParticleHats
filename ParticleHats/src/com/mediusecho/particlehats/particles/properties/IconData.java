package com.mediusecho.particlehats.particles.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;

public class IconData {

	private final Random random = new Random();
	
	private IconDisplayMode displayMode = IconDisplayMode.DISPLAY_IN_ORDER;
	private List<ItemStackTemplate> items;
	private ItemStackTemplate previousItem;
	
	private int index = 0;
	private int updateFrequency = 2;
	
	private static final ItemStack tempMainItem = ItemUtil.createItem(CompatibleMaterial.SUNFLOWER, 1);
	
	public IconData ()
	{
		items = new ArrayList<ItemStackTemplate>();
		setMainItem(tempMainItem);
	}
	
	/**
	 * Set this IconData's IconDisplayMode
	 * @param displayMode
	 */
	public void setDisplayMode (IconDisplayMode displayMode) {
		this.displayMode = displayMode;
	}
	
	/**
	 * Set the main item displayed in menus
	 * @param item
	 */
	public void setMainItem (ItemStack item)
	{
		if (items.size() == 0) {
			items.add(new ItemStackTemplate(item));
		} else {
			items.get(0).set(item);
		}
		
		if (previousItem == null) {
			previousItem = items.get(0);
		}
	}
	
	/**
	 * Set all items for this IconData class
	 * @param items
	 */
	public void setItems (List<ItemStackTemplate> items) {
		this.items = items;
	}
	
	/**
	 * Adds a new item to the list
	 * @param item
	 */
	@SuppressWarnings("deprecation")
	public void addItem (ItemStack item) 
	{
		if (ParticleHats.serverVersion < 13) {
			items.add(new ItemStackTemplate(item.getType(), item.getDurability()));
		} else {
			items.add(new ItemStackTemplate(item.getType()));
		}
	}
	
	/**
	 * Removes the item found at the index
	 * @param index
	 */
	public void removeItem (int index) {
		items.remove(index);
	}
	
	public void updateItem (int index, ItemStack item) {
		items.get(index).set(item);
	}
	
	/**
	 * Get all items
	 * @return
	 */
	public List<ItemStackTemplate> getItems ()
	{
		final List<ItemStackTemplate> i = new ArrayList<ItemStackTemplate>(items);
		return i;
	}
	
	/**
	 * Get all material names
	 * @return
	 */
	public List<String> getItemNames ()
	{
		List<String> itemNames = new ArrayList<String>();
		boolean legacy = ParticleHats.serverVersion < 13;
		
		for (ItemStackTemplate item : items)
		{
			if (legacy) {
				itemNames.add(item.getMaterial().toString() + ":" + item.getDurability());
			} else {
				itemNames.add(item.getMaterial().toString());
			}
		}
		
		return itemNames;
	}
	
	/**
	 * Gets the next ItemStack according to the IconDisplayMode
	 * @param ticks
	 * @return
	 */
	public ItemStackTemplate getNextItem (int ticks)
	{
		if (items.size() == 1) {
			return items.get(0);
		}
		
		if (ticks % updateFrequency != 0) {
			return previousItem;
		}
		
		switch (displayMode)
		{
			default: return previousItem;
			case DISPLAY_RANDOMLY:
			{
				int attempts = 0;
				ItemStackTemplate nextItem = items.get(random.nextInt(items.size()));
				
				while (nextItem == previousItem && attempts < 50) 
				{
					nextItem = items.get(random.nextInt(items.size()));
					attempts++;
				}
				previousItem = nextItem;
				return nextItem;
			}
		
			case DISPLAY_IN_ORDER:
			{
				previousItem = items.get(MathUtil.wrap(index++, items.size(), 0));
				return previousItem;
			}		
		}
	}
	
	/**
	 * Check to see if this IconData can change icons
	 * @return True if there are more than 1 icon to change to
	 */
	public boolean isLive () {
		return items.size() > 1;
	}
	
	/**
	 * Get how often materials change for this IconData object
	 * @return
	 */
	public int getUpdateFrequency () {
		return updateFrequency;
	}
	
	/**
	 * Set how often materials change for this IconData object
	 * @return
	 */
	public void setUpdateFrequency (int updateFrequency) {
		this.updateFrequency = updateFrequency;
	}
	
	/**
	 * Returns this IconData's IconDisplayMode
	 * @return
	 */
	public IconDisplayMode getDisplayMode () {
		return displayMode;
	}
	
	/**
	 * Resets the icon animation index to 0
	 */
	public void reset () {
		index = 0;
	}
	
	public IconData clone ()
	{
		IconData iconData = new IconData();
		
		iconData.displayMode = displayMode;
		iconData.items = new ArrayList<ItemStackTemplate>(items);
		iconData.index = index;
		iconData.updateFrequency = updateFrequency;
		
		return iconData;
	}
	
	/**
	 * Represents a stripped down version of an ItemStack, containing only the material and durability
	 * 
	 * @author MediusEcho
	 *
	 */
	public class ItemStackTemplate {
		
		private Material material;
		private short durability;
		
		public ItemStackTemplate (Material material, short durability)
		{
			this.material = material;
			this.durability = durability;
		}
		
		public ItemStackTemplate (Material material) {
			this(material, (short) 0);
		}
		
		public ItemStackTemplate (ItemStack item) {
			set(item);
		}
		
		public Material getMaterial () {
			return material;
		}
		
		public short getDurability () {
			return durability;
		}
		
		@SuppressWarnings("deprecation")
		public void set (ItemStack item)
		{
			this.material = item.getType();
			if (ParticleHats.serverVersion < 13) {
				this.durability = item.getDurability();
			}
		}
	}
}
