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

	private ParticleData parent;
	
	private int duration = 20;
	private Vector velocity;
	private List<ItemStack> items;
	private boolean hasGravity = true;
	private boolean hasDirectionalVelocity = false;
	
	private static final ItemStack defaultItem = new ItemStack(Material.STONE);
	
	private Random random;
	
	public ItemStackData (final ParticleData parent) 
	{
		this.parent = parent;
		
		velocity = new Vector();
		items = new ArrayList<ItemStack>();
		random = new Random();
	}
	
	/**
	 * Set how long items stay spawned in the world<br>
	 * duration is clamped to a range of 0 - 6000 (5 minutes max)
	 * @param duration
	 */
	public void setDuration (int duration) 
	{
		this.duration = MathUtil.clamp(duration, 20, 6000);
		parent.setProperty("duration", Integer.toString(this.duration));
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
		setVelocity(velocity.getX(), velocity.getY(), velocity.getZ());
	}
	
	/**
	 * Set the velocity of items when they're spawned into the world
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setVelocity (double x, double y, double z) 
	{
		velocity.setX(MathUtil.clamp(x, -20, 20));
		velocity.setY(MathUtil.clamp(y, -20, 20));
		velocity.setZ(MathUtil.clamp(z, -20, 20));
		
		parent.setProperty("velocity_x", Double.toString(velocity.getX()));
		parent.setProperty("velocity_y", Double.toString(velocity.getY()));
		parent.setProperty("velocity_z", Double.toString(velocity.getZ()));
	}
	
	/**
	 * Set the velocity x value of items when they're spawned in the world
	 * @param x
	 */
	public void setVelocityX (double x) {
		setVelocity(x, velocity.getY(), velocity.getZ());
	}
	
	/**
	 * Set the velocity y value of items when they're spawned in the world
	 * @param y
	 */
	public void setVelocityY (double y) {
		setVelocity(velocity.getX(), y, velocity.getZ());
	}
	
	/**
	 * Set the velocity z value of items when they're spawned in the world
	 * @param z
	 */
	public void setVelocityZ (double z) {
		setVelocity(velocity.getX(), velocity.getY(), z);
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
	
	public void setItems (List<ItemStack> items) {
		this.items = items;
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
	 * Get all item names
	 * @return
	 */
	public List<String> getItemNames ()
	{
		List<String> items = new ArrayList<String>();
		for (ItemStack item : this.items) {
			items.add(item.getType().toString());
		}
		return items;
	}
 	
	/**
	 * Set whether items obey gravity when spawned
	 * @param gravity
	 */
	public void setGravity (boolean gravity) 
	{
		this.hasGravity = gravity;
		parent.setProperty("gravity", Boolean.toString(gravity));
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
	
	/**
	 * Drops an item into the world
	 * @param world
	 * @param location
	 * @param hat
	 */
	public void dropItem (World world, Location location, Hat hat)
	{
		try
		{
			Vector randomOffset = hat.getRandomOffset();
			double rx = randomOffset.getX();
			double ry = randomOffset.getY();
			double rz = randomOffset.getZ();
			
			double x = (random.nextDouble() * (rx * 2)) - rx;
			double y = (random.nextDouble() * (ry * 2)) - ry;
			double z = (random.nextDouble() * (rz * 2)) - rz;
			
			Item item = world.dropItem(location.add(x, y, z), getRandomItem());
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
	
	public ItemStackData clone (ParticleData parent)
	{
		ItemStackData data = new ItemStackData(parent);
		
		data.duration = duration;
		data.velocity = velocity.clone();
		data.hasGravity = hasGravity;
		data.hasDirectionalVelocity = hasDirectionalVelocity;
		data.items = new ArrayList<ItemStack>(items);
		
		return data;
	}
	
	@Override
	public boolean equals (Object o)
	{
		if (this == o) return true;
		if (o == null) return false;
		if (!(o instanceof ItemStackData)) return false;
		
		ItemStackData data = (ItemStackData)o;
		
		if (data.duration != duration) return false;
		if (!data.velocity.equals(velocity)) return false;
		if (!data.items.equals(items)) return false;
		if (data.hasGravity != hasGravity) return false;
		if (data.hasDirectionalVelocity != hasDirectionalVelocity) return false;
		
		return true;
	}
}
