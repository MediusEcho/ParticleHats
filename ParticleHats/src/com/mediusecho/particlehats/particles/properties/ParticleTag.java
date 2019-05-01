package com.mediusecho.particlehats.particles.properties;

import java.util.HashMap;
import java.util.Map;

public enum ParticleTag {

	NONE ("", ""),
	CUSTOM ("Custom", ""),
	ARROWS ("arrow", "Arrows", "&8Particles follow the arrows you shoot"),
	ARMOUR_STAND ("devtest", "Armour Stand", "&8Particles are equipped/n&8to the nearest Armour Stand");
	
	private final String name;
	private final String legacy;
	private final String displayName;
	private final String description;
	
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
	
	private ParticleTag (final String legacy, final String displayName, String description)
	{
		this.name = toString().toLowerCase();
		this.legacy = legacy;
		this.displayName = displayName;
		this.description = description;
	}
	
	private ParticleTag (final String displayName, String description) {
		this("", displayName, description);
	}
	
	public String getDisplayName () {
		return displayName;
	}
	
	public String getName () {
		return name;
	}
	
	public String getDescription () {
		return description;
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
