package com.mediusecho.particlehats.particles.properties;

import java.util.HashMap;
import java.util.Map;

public enum ParticleTag {

	NONE ("", "", ""),
	CUSTOM ("custom", "", "Custom"),
	ARROWS ("arrows", "arrow", "Arrows"),
	ARMOUR_STAND ("armour_stand", "devtest", "Armour Stand");
	
	private final String name;
	private final String legacy;
	private final String displayName;
	
	private final static Map<String, ParticleTag> legacyName = new HashMap<String, ParticleTag>();
	private final static Map<String, ParticleTag> names = new HashMap<String, ParticleTag>();
	
	static
	{
		for (ParticleTag tag : ParticleTag.values())
		{
			legacyName.put(tag.legacy, tag);
			names.put(tag.name, tag);
		}
	}
	
	private ParticleTag (final String name, final String legacy, final String displayName)
	{
		this.name = name;
		this.legacy = legacy;
		this.displayName = displayName;
	}
	
	public String getDisplayName () {
		return displayName;
	}
	
	public String getName () {
		return name;
	}
	
	public static ParticleTag fromLegacy (String legacy)
	{
		if (legacyName.containsKey(legacy)) {
			return legacyName.get(legacy);
		}
		return NONE;
	}
	
	public static ParticleTag fromName (String name)
	{
		if (names.containsKey(name)) {
			return names.get(name);
		}
		return NONE;
	}
}
