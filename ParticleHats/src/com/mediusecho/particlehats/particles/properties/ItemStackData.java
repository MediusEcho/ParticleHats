package com.mediusecho.particlehats.particles.properties;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.util.MathUtil;

public class ItemStackData {

	private int duration = 20;
	private Vector velocity;
	private List<ItemStack> items;
	private boolean hasGravity = true;
	private boolean hasDirectionalVelocity = false;
	
	private static final ItemStack defaultItem = new ItemStack(Material.STONE);
	
	private Random random;
	
	public ItemStackData () 
	{
		velocity = new Vector();
		items = new ArrayList<ItemStack>();
		random = new Random();
	}
	
	/**
	 * Set how long items stay spawned in the world<br>
	 * duration is clamped to a range of 0 - 6000 (5 minutes max)
	 * @param duration
	 */
	public void setDuration (int duration) {
		this.duration = MathUtil.clamp(duration, 20, 6000);
	}
	
	/**
	 * Get how long items stay spawned in the world
	 * @return
	 */
	public int getDuration () {
		return duration;
	}
	
	/**
	 * Get how long items stay spawned in this world
	 * @return Returns (6000 - duration) to approximate how long an item has been in the world
	 */
	public int getDurationLived () {
		return 6000 - duration;
	}
	
	/**
	 * Set the velocity of items when they're spawned into the world
	 * @param velocity
	 */
	public void setVelocity (Vector velocity) {
		this.velocity = velocity;
	}
	
	public void setVelocity (double x, double y, double z) 
	{
		velocity.setX(x);
		velocity.setY(y);
		velocity.setZ(z);
	}
	
	/**
	 * Set the velocity x value of items when they're spawned in the world
	 * @param x
	 */
	public void setVelocityX (double x) {
		velocity.setX(x);
	}
	
	/**
	 * Set the velocity y value of items when they're spawned in the world
	 * @param y
	 */
	public void setVelocityY (double y) {
		velocity.setY(y);
	}
	
	/**
	 * Set the velocity z value of items when they're spawned in the world
	 * @param z
	 */
	public void setVelocityZ (double z) {
		velocity.setZ(z);
	}
	
	/**
	 * Get the items velocity
	 * @return
	 */
	public Vector getVelocity () {
		return velocity;
	}
	
	/**
	 * Add an item to the list
	 * @param item
	 */
	public void addItem (ItemStack item) {
		items.add(item);
	}
	
	/**
	 * Update the item that exists at this index
	 * @param index
	 * @param item
	 */
	public void updateItem (int index, ItemStack item) {
		items.set(index, item);
	}
	
	/**
	 * Removes the item at this index
	 * @param index
	 */
	public void removeItem (int index) {
		items.remove(index);
	}
	
	/**
	 * Get the item stored at this index
	 * @param index
	 * @return
	 */
	public ItemStack getItem(int index) {
		return items.get(index);
	}
	
	/**
	 * Get a random item
	 * @return
	 */
	public ItemStack getRandomItem ()
	{
		if (items.size() > 0) {
			return items.get(random.nextInt(items.size()));
		}
		return defaultItem;
	}
	
	/**
	 * Get all items
	 * @return
	 */
	public List<ItemStack> getItems () 
	{
		final List<ItemStack> items = new ArrayList<ItemStack>(this.items);
		return items;
	}
	
	/**
	 * Set whether items obey gravity when spawned
	 * @param gravity
	 */
	public void setGravity (boolean gravity) {
		this.hasGravity = gravity;
	}
	
	/**
	 * Get whether items obey gravity when spawned
	 * @return
	 */
	public boolean hasGravity () {
		return hasGravity;
	}
	
	/**
	 * Set whether an items velocity is relative to the players looking direction
	 * @param directionalVelocity
	 */
	public void setDirectionalVelocity (boolean directionalVelocity) {
		this.hasDirectionalVelocity = directionalVelocity;
	}
	
	/**
	 * Get whether an items velocity is relative to the players looking direction
	 * @return
	 */
	public boolean hasDirectionalVelocity () {
		return hasDirectionalVelocity;
	}
	
	public void dropItem (World world, Location location, Hat hat)
	{
		try
		{
			Item item = world.dropItem(location, getRandomItem());
			item.setPickupDelay(36000); // 30 Minutes
			item.setGravity(hasGravity);
			
			// Give this item a unique metadata so we can cancel any pickup
			item.setMetadata("PH_DroppedItem", new FixedMetadataValue(Core.instance, ""));
			
			Vector velocity = this.velocity;
			item.setVelocity(velocity);
			
			Field itemField = item.getClass().getDeclaredField("item");
			itemField.setAccessible(true);
			
			Object entityItem = itemField.get(item);
			
			Field ageField = entityItem.getClass().getDeclaredField("age");
			ageField.setAccessible(true);
			ageField.set(entityItem, getDurationLived());
		}
		
		catch (NoSuchFieldException e) {
			Core.debug("NoSuchFieldException");
		}
		
		catch (IllegalAccessException e) {
			Core.debug("IllegalAccessException");
		}
	}
}
