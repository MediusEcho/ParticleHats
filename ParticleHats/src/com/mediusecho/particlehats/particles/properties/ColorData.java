package com.mediusecho.particlehats.particles.properties;

import org.bukkit.Color;

public class ColorData {

	private final ParticleData parent;
	
	private Color color;
	private boolean isRandom = false;
	
	public ColorData (final ParticleData parent, Color color, boolean isRandom)
	{
		this.parent = parent;
		this.color = color;
		this.isRandom = isRandom;
	}
	
	public ColorData (final ParticleData parent, Color color)
	{
		this(parent, color, false);
	}
	
	/**
	 * Get the color of this Particle
	 * @return
	 */
	public Color getColor () {
		return isRandom ? getRandomColor() : color;
	}
	
	/**
	 * Get the color of this Particle ignoring random state
	 * @return
	 */
	public Color getStoredColor () {
		return color;
	}
	
	/**
	 * Set the color of this Particle
	 * @param color
	 */
	public void setColor (Color color) 
	{
		this.color = color;
		parent.setProperty("color", Integer.toString(color.asRGB()));
		
		setRandom(false);
	}
	
	/**
	 * Check to see if this ParticleColor is random
	 * @return
	 */
	public boolean isRandom () {
		return isRandom;
	}
	
	/**
	 * Set whether this Color is random
	 * @param isRandom
	 */
	public void setRandom (boolean isRandom) 
	{
		this.isRandom = isRandom;
		parent.setProperty("random", Boolean.toString(isRandom));
	}
	
	/**
	 * Get a random color
	 * @return
	 */
	private Color getRandomColor ()
	{
		int r = (int) Math.round(Math.random() * 255);
		int g = (int) Math.round(Math.random() * 255);
		int b = (int) Math.round(Math.random() * 255);
		return Color.fromRGB(r, g, b);
	}
	
	/**
	 * Get a cloned copy of this object
	 * @param parent
	 * @return
	 */
	public ColorData clone (ParticleData parent) {
		return new ColorData(parent, color, isRandom);
	}
	
	@Override
	public boolean equals (Object o)
	{
		if (this == o) return true;
		if (o == null) return false;
		if (!(o instanceof ColorData)) return false;
		
		ColorData data = (ColorData)o;
		
		if (!data.color.equals(color)) return false;
		if (data.isRandom != isRandom) return false;
		
		return true;
 	}
}
