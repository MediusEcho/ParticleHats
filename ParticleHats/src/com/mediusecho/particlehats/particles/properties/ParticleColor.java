package com.mediusecho.particlehats.particles.properties;

import org.bukkit.Color;

public class ParticleColor {

	private Color color;
	private boolean isRandom = false;
	
	public ParticleColor (Color color, boolean isRandom)
	{
		this.color = color;
		this.isRandom = isRandom;
	}
	
	public ParticleColor (Color color)
	{
		this(color, false);
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
	public void setColor (Color color) {
		this.color = color;
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
	public void setRandom (boolean isRandom) {
		this.isRandom = isRandom;
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
}
