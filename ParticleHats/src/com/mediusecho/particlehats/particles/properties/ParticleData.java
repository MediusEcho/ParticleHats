package com.mediusecho.particlehats.particles.properties;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.particles.ParticleEffect;

public class ParticleData {

	private ParticleEffect particle;
	private ParticleColor color;
	private ItemStack item;
	private BlockData block;
	private double scale;
	
	public ParticleData ()
	{
		particle = ParticleEffect.NONE;
		color = new ParticleColor(Color.WHITE, true);
		item = new ItemStack(Material.APPLE);
		block = Material.STONE.createBlockData();
		scale = 1;
	}
	
	/**
	 * Set the particle for this ParticleData class
	 * @param particle
	 */
	public void setParticle (ParticleEffect particle) {
		this.particle = particle;
	}
	
	/**
	 * Get the ParticleEffect value from this ParticleData class
	 * @return
	 */
	public ParticleEffect getParticle () {
		return particle;
	}
	
	/**
	 * Set the particle color for this ParticleData class
	 * @param color
	 */
	public void setColor (ParticleColor color) {
		this.color = color;
	}
	
	/**
	 * Get the particle color for this ParticleData class
	 * @return
	 */
	public ParticleColor getColor () {
		return color;
	}
	
	/**
	 * Set the ItemStack for this ParticleData class
	 * @param item
	 */
	public void setItem (ItemStack item) {
		this.item = item;
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
	public void setBlock (BlockData block) {
		this.block = block;
	}
	
	/**
	 * Set the BlockData for this ParticleData class
	 * @param block
	 */
	public void setBlock (Material block) {
		this.block = block.createBlockData();
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
	public void setScale (double scale) {
		this.scale = scale;
	}
	
	/**
	 * Get the scale for this ParticleData class
	 * @return
	 */
	public double getScale () {
		return scale;
	}
}
