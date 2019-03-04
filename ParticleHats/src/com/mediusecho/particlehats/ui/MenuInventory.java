package com.mediusecho.particlehats.ui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.util.StringUtil;

public class MenuInventory {

	private Map<Integer, Hat> hats;
	private Inventory inventory;
	
	private String name;
	private String title;
	
	public MenuInventory (String name, String title, int rows)
	{
		this.name = name;
		this.title = title;
		
		inventory = Bukkit.createInventory(null, rows * 9, StringUtil.colorize(title));
		hats = new HashMap<Integer, Hat>();
	}
	
	/**
	 * Returns the ItemStack found in this slot
	 * @param slot
	 * @return
	 */
	public ItemStack getItem (int slot) {
		return inventory.getItem(slot);
	}
	
	/**
	 * Set the ItemStack that belongs in this slot
	 * @param slot
	 * @param item
	 */
	public void setItem (int slot, ItemStack item) 
	{
		if (slot < inventory.getSize()) {
			inventory.setItem(slot, item);
		}
	}
	
	/**
	 * Returns this inventory's content;
	 * @return
	 */
	public ItemStack[] getContents () {
		return inventory.getContents();
	}
	
	/**
	 * Get the name used to save this menu
	 * @return
	 */
	public String getName () {
		return name;
	}
	
	/**
	 * Returns the title of the inventory
	 * @return
	 */
	public String getTitle () {
		return title;
	}
	
	/**
	 * Set this menus inventory title
	 * @param title
	 */
	public void setTitle (String title) {
		this.title = title;
	}
	
	/**
	 * Returns the size of this inventory
	 * @return
	 */
	public int getSize () {
		return inventory.getSize();
	}
	
	/**
	 * Returns the hat found in this slot
	 * @param slot
	 * @return
	 */
	public Hat getHat (int slot) {
		return hats.get(slot);
	}
	
	/**
	 * Set the hat the belong in this slot
	 * @param slot
	 * @param hat
	 */
	public void setHat (int slot, Hat hat) {
		hats.put(slot, hat);
	}
	
	/**
	 * Removes a hat from this slot
	 * @param slot
	 */
	public void removeHat (int slot) {
		hats.remove(slot);
	}
	
	/**
	 * Returns every hat in this menu
	 * @return
	 */
	public Map<Integer, Hat> getHats ()
	{
		final Map<Integer, Hat> h = new HashMap<Integer, Hat>(hats);
		return h;
	}
	
	/**
	 * Opens this inventory for the player
	 * @param player
	 */
	public void open (Player player) {
		player.openInventory(inventory);
	}
}
