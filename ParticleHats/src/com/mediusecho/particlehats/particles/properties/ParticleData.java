package com.mediusecho.particlehats.particles.properties;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.particles.ParticleEffect;

public class ParticleData {

	private Map<String, String> modifiedProperties;
	
	private ParticleEffect particle;
	private ColorData color;
	private ItemStack item;
	private BlockData block;
	private double scale;
	private ItemStackData stackData;
	
	public ParticleData ()
	{
		modifiedProperties    = new HashMap<String, String>();
		
		particle = ParticleEffect.NONE;
		color = new ColorData(this, Color.WHITE, true);
		item = new ItemStack(Material.APPLE);
		block = Material.STONE.createBlockData();
		scale = 1;
		stackData = new ItemStackData(this);
	}
	
	/**
	 * Set the particle for this ParticleData class
	 * @param particle
	 */
	public void setParticle (ParticleEffect particle) 
	{
		this.particle = particle;
		setProperty("particle_id", Integer.toString(particle.getID()));
	}
	
	/**
	 * Get the ParticleEffect value from this ParticleData class
	 * @return
	 */
	public ParticleEffect getParticle () {
		return particle;
	}
	
	/**
	 * Get the particle color for this ParticleData class
	 * @return
	 */
	public ColorData getColorData() {
		return color;
	}
	
	/**
	 * Set the ItemStack for this ParticleData class
	 * @param item
	 */
	public void setItem (ItemStack item) 
	{
		this.item = item;
		setProperty("item_data", "'" + item.getType().toString() + "'");
	}
	
	/**
	 * Get the ItemStack for this ParticleData class
	 * @return
	 */
	public ItemStack getItem () {
		return item;
	}
	
	/**
	 * Set the BlockData for this ParticleData class
	 * @param block
	 */
	public void setBlock (BlockData block) 
	{
		this.block = block;
		setProperty("block_data", "'" + block.getMaterial().toString() + "'");
	}
	
	/**
	 * Set the BlockData for this ParticleData class
	 * @param block
	 */
	public void setBlock (Material block) 
	{
		this.block = block.createBlockData();
		setProperty("block_data", "'" + block.toString() + "'");
	}
	
	/**
	 * Get the BlockData for this ParticleData class
	 * @return
	 */
	public BlockData getBlock () {
		return block;
	}
	
	/**
	 * Set the scale for this ParticleData class
	 * @param scale
	 */
	public void setScale (double scale) 
	{
		this.scale = scale;
		setProperty("scale", Double.toString(scale));
	}
	
	/**
	 * Get the scale for this ParticleData class
	 * @return
	 */
	public double getScale () {
		return scale;
	}
	
	/**
	 * Get the ItemStackData for this ParticleData class
	 * @return
	 */
	public ItemStackData getItemStackData () {
		return stackData;
	}
	
	/**
	 * Clears the recently modified properties list
	 */
	public void clearPropertyChanges () {
		modifiedProperties.clear();
	}
	
	/**
	 * Checks to see if any properties have been changed
	 * @return
	 */
	public boolean hasPropertyChanges () {
		return modifiedProperties.size() > 0;
	}
	
	/**
	 * Get a list of all changes made<br>
	 * @return
	 */
	public Map<String, String> getPropertyChanges () 
	{
		final Map<String, String> changes = new HashMap<String, String>(modifiedProperties);
		return changes;
	}
	
	/**
	 * Adds a modified property for reference when saving this hat
	 * @param key
	 * @param value
	 */
	public void setProperty (String key, String value) {
		modifiedProperties.put(key, value);
	}
	
	public ParticleData clone ()
	{
		ParticleData data = new ParticleData();
		
		data.particle = particle;
		data.item = item;
		data.block = block;
		data.scale = scale;
		data.color = color.clone(this);
		data.stackData = stackData.clone(this);
		
		return data;
	}
}
