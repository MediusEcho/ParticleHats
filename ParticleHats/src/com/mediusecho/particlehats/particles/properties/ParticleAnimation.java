package com.mediusecho.particlehats.particles.properties;

import java.util.HashMap;
import java.util.Map;

import com.mediusecho.particlehats.locale.Message;

import net.md_5.bungee.api.ChatColor;

public enum ParticleAnimation {

	STATIC (0, "static"),
	ANIMATED (1, "animated");
	
	private final int id;
	private final String name;
	
	private static final Map<Integer, ParticleAnimation> animationID = new HashMap<Integer, ParticleAnimation>();
	private static final Map<String, ParticleAnimation> animationName = new HashMap<String, ParticleAnimation>();
	
	static
	{
		for (ParticleAnimation a : values())
		{
			animationID.put(a.id, a);
			animationName.put(a.name, a);
		}
	}
	
	private ParticleAnimation (final int id, final String name)
	{
		this.id = id;
		this.name = name;
	}
	
	/**
	 * Get the id of this ParticleAnimation
	 * @return
	 */
	public int getID () {
		return id;
	}
	
	/**
	 * Get the name of this ParticleAnimation
	 * @return
	 */
	public String getName () {
		return name;
	}
	
	/**
	 * Get the display name of this ParticleAnimation
	 * @return
	 */
	public String getDisplayName () 
	{
		final String key = "ANIMATION_" + toString() + "_NAME";
		try {
			return Message.valueOf(key).getValue();
		} catch (IllegalArgumentException e) {
			return "";
		}
	}
	
	/**
	 * Get the display name of this ParticleAnimation without color codes
	 * @return
	 */
	public String getStrippedName () {
		return ChatColor.stripColor(getDisplayName());
	}
	
	/**
	 * Get the description of this ParticleAnimation
	 * @return
	 */
	public String getDescription ()
	{
		final String key = "ANIMATION_" + toString() + "_DESCRIPTION";
		try {
			return Message.valueOf(key).getValue();
		} catch (IllegalArgumentException e) {
			return "";
		}
	}
	
	/**
	 * Get the ParticleAnimation value that matches this id<br>
	 * returns STATIC if no match is found
	 * @param id
	 * @return
	 */
	public static ParticleAnimation fromID (int id)
	{
		if (animationID.containsKey(id)) {
			return animationID.get(id);
		}
		return ParticleAnimation.STATIC;
	}
	
	/**
	 * Get the ParticleAnimation value that matches this name<br>
	 * returns STATIC if no match is found
	 * @param name
	 * @return
	 */
	public static ParticleAnimation fromName (String name)
	{
		if (animationName.containsKey(name)) {
			return animationName.get(name);
		}
		return ParticleAnimation.STATIC;
	}
}
