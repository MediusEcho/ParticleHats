package com.mediusecho.particlehats.particles.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;

import com.mediusecho.particlehats.util.MathUtil;

public class IconData {

	private final Random random = new Random();
	
	private IconDisplayMode displayMode = IconDisplayMode.DISPLAY_IN_ORDER;
	private List<Material> materials;
	private Material previousMaterial;
	
	private int index = 0;
	private int updateFrequency = 1;
	
	public IconData ()
	{
		materials = new ArrayList<Material>();
		materials.add(Material.SUNFLOWER);
	}
	
	/**
	 * Set this IconData's IconDisplayMode
	 * @param displayMode
	 */
	public void setDisplayMode (IconDisplayMode displayMode) {
		this.displayMode = displayMode;
	}
	
	public void setMainMaterial (Material material) 
	{
		if (materials.size() == 0) {
			materials.add(material);
		} else {
			materials.set(0, material);
		}
	}
	
	/**
	 * Add a new material to the list
	 * @param material
	 */
	public void addMaterial (Material material) {
		materials.add(material);
	}
	
	/**
	 * Remove the material found at the index
	 * @param index
	 */
	public void removeMaterial (int index) {
		materials.remove(index);
	}
	
	/**
	 * Removes all materials
	 */
	public void clearMaterials () {
		materials.clear();
	}
	
	/**
	 * Replaces the Material found at this index with a new Material
	 * @param index
	 * @param material
	 */
	public void updateMaterial (int index, Material material) {
		materials.set(index, material);
	}
	
	/**
	 * Get all materials
	 * @return
	 */
	public List<Material> getMaterials ()
	{
		final List<Material> mats = new ArrayList<Material>(materials);
		return mats;
	}
	
	/**
	 * Get all materials as their string version
	 * @return
	 */
	public List<String> getMaterialsAsStringList ()
	{
		final List<String> mats = new ArrayList<String>();
		for (Material m : materials) {
			mats.add(m.toString());
		}
		return mats;
	}
	
	/**
	 * Returns the next material in the list according to the IconDisplayMode
	 * @return
	 */
	public Material getNextMaterial (int ticks)
	{		
		if (materials.size() == 1) {
			return materials.get(0);
		}
		
		if (ticks % updateFrequency != 0) {
			return previousMaterial;
		}
		
		switch (displayMode)
		{
			default: return previousMaterial;
			case DISPLAY_RANDOMLY:
			{
				int attempts = 0;
				Material nextMaterial = materials.get(random.nextInt(materials.size()));
				while (nextMaterial == previousMaterial && attempts < 50) 
				{
					nextMaterial = materials.get(random.nextInt(materials.size()));
					attempts++;
				}
				previousMaterial = nextMaterial;
				return nextMaterial;
			}
		
			case DISPLAY_IN_ORDER:
			{
				previousMaterial = materials.get(MathUtil.wrap(index++, materials.size(), 0));
				return previousMaterial;
			}		
		}
		
//		if (materials.size() > 1)
//		{
//			if (ticks % updateFrequency == 0)
//			{
//				switch (displayMode)
//				{
//				case DISPLAY_RANDOMLY:
//				{
//					int attempts = 0;
//					Material nextMaterial = materials.get(random.nextInt(materials.size()));
//					while (nextMaterial == previousMaterial && attempts < 50) 
//					{
//						nextMaterial = materials.get(random.nextInt(materials.size()));
//						attempts++;
//					}
//					previousMaterial = nextMaterial;
//					return nextMaterial;
//				}
//					
//				case DISPLAY_IN_ORDER:
//				{
//					previousMaterial = materials.get(MathUtil.wrap(index++, materials.size(), 0));
//					return previousMaterial;
//				}
//				
//				default: return previousMaterial;
//				}
//			} 
//			
//			else {
//				return previousMaterial;
//			}
//		}
//		
//		else {
//			return materials.get(0);
//		}
	}
	
	/**
	 * Check to see if this IconData can change icons
	 * @return True if there are more than 1 icon to change to
	 */
	public boolean isLive () {
		return materials.size() > 1;
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
	
	public void reset () {
		index = 0;
	}
}
