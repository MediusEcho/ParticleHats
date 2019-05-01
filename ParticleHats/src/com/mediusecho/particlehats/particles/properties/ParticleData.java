package com.mediusecho.particlehats.particles.properties;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.util.MathUtil;

// TODO: Removed unused code after checking
public class ParticleData {

	private Map<String, String> modifiedProperties;
	
	private ParticleEffect particle;
	private ColorData color;
	private ItemStack item;
	private ItemStack block;
	private double scale;
	private ItemStackData stackData;
	
	private int[] legacyItemPacketData;
	private int[] legacyBlockPacketData;
	
	@SuppressWarnings("deprecation")
	public ParticleData ()
	{
		modifiedProperties    = new HashMap<String, String>();
		
		particle = ParticleEffect.NONE;
		color = new ColorData(this, Color.WHITE, true);
		item = new ItemStack(Material.APPLE);
		block = new ItemStack(Material.STONE);
		scale = 1;
		stackData = new ItemStackData(this);
		
		legacyItemPacketData = new int[] {Material.APPLE.getId(), 0};
		legacyBlockPacketData = new int[] {Material.STONE.getId(), (byte) 0};
	}
	
	/**
	 * Set the particle for this ParticleData class
	 * @param particle
	 */
	public void setParticle (ParticleEffect particle) 
	{
		if (particle != null)
		{
			this.particle = particle;
			setProperty("particle_id", Integer.toString(particle.getID()));
		}
	}
	
	/**
	 * Get the ParticleEffect value from this ParticleData class
	 * @return
	 */
	public ParticleEffect getParticle () {
		return particle;
	}
	
	/**
	 * Set this ParticleData ColorData value
	 * @param color
	 */
	public void setColorData (ColorData color) {
		this.color = color;
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
	@SuppressWarnings("deprecation")
	public void setItem (ItemStack item) 
	{
		this.item = item;
		setProperty("item_data", "'" + item.getType().toString() + "'");
		
		legacyItemPacketData = new int[] {item.getType().getId(), (byte) item.getDurability()};
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
	 * 
	 * @deprecated Use {@link #setBlock(Material)}
	 */
//	@Deprecated
//	public void setBlock (BlockData block) 
//	{
//		//this.block = block;
//		//setProperty("block_data", "'" + block.getMaterial().toString() + "'");
//	}
	
	/**
	 * Set the BlockData for this ParticleData class
	 * @param block
	 */
	@SuppressWarnings("deprecation")
	public void setBlock (ItemStack block) 
	{
		this.block = block;
		setProperty("block_data", "'" + block.toString() + "'");
		
		legacyBlockPacketData = new int[] {block.getType().getId(), (byte) block.getDurability()};
	}
	
//	/**
//	 * Get the BlockData for this ParticleData class
//	 * @return
//	 */
//	public BlockData getBlock () {
//		return block.createBlockData();
//	}
	
	public ItemStack getBlock () {
		return block;
	}
	
	public Material getBlockMaterial () {
		return block.getType();
	}
	
	/**
	 * Set the scale for this ParticleData class
	 * @param scale
	 */
	public void setScale (double scale) 
	{
		this.scale = MathUtil.clamp(MathUtil.round(scale, 2), 0.1, 10.0);
		setProperty("scale", Double.toString(this.scale));
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
	 * Set this data's ItemStackData
	 * @param stackData
	 */
	public void setItemStackData (ItemStackData stackData) {
		this.stackData = stackData;
	}
	
	public int[] getLegacyPacketData () 
	{
		if (particle.hasItemData()) {
			return legacyItemPacketData;
		}
		return legacyBlockPacketData;
	}
	
	public String getLegacyPacketDataString () 
	{
		if (particle.hasItemData()) {
			return "_" + legacyItemPacketData[0] + "_" + legacyItemPacketData[1];
		}
		return "_" + legacyBlockPacketData[0] + "_" + legacyBlockPacketData[1];
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
		data.legacyItemPacketData = legacyItemPacketData.clone();
		data.legacyBlockPacketData = legacyBlockPacketData.clone();
		
		return data;
	}
	
	@Override
	public boolean equals (Object o)
	{
		if (this == o) return true;
		if (o == null) return false;
		if (!(o instanceof ParticleData)) return false;
		
		ParticleData data = (ParticleData)o;
		
		if (!data.particle.equals(particle)) return false;
		if (!data.item.equals(item)) return false;
		if (!data.block.equals(block)) return false;
		if (data.scale != scale) return false;
		
		if (!data.color.equals(color)) return false;
		if (!data.stackData.equals(stackData)) return false;
		
		return true;
	}
}
