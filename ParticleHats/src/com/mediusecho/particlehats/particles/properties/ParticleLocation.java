package com.mediusecho.particlehats.particles.properties;

import java.util.HashMap;
import java.util.Map;

import com.mediusecho.particlehats.locale.Message;

/**
 * Represents each location a particle can be displayed at
 * 
 * @author MediusEcho
 *
 */
public enum ParticleLocation
{
	HEAD  (0, "head"),
	FEET  (1, "feet"),
	CHEST (2, "chest", "waist");
	
	private final int id;
	private final String name;
	private final String legacyName;
	
	private static final Map<Integer, ParticleLocation> locationID = new HashMap<Integer, ParticleLocation>();
	private static final Map<String, ParticleLocation> locationName = new HashMap<String, ParticleLocation>();
	private static final Map<String, ParticleLocation> locationLegacyName = new HashMap<String, ParticleLocation>();
	
	static 
	{
		for (ParticleLocation location : values())
		{
			locationID.put(location.id, location);
			locationName.put(location.name, location);
			locationLegacyName.put(location.legacyName, location);
		}
	}
	
	private ParticleLocation (int id, String name, String legacyName)
	{
		this.id = id;
		this.name = name;
		this.legacyName = legacyName;
	}
	
	private ParticleLocation (int id, String name)
	{
		this(id, name, "");
	}
	
	/**
	 * Returns this ParticleLocations name
	 * @return
	 */
	public String getName () {
		return name;
	}
	
	/**
	 * Get this locations legacy name
	 * @return
	 */
	public String getLegacyName () {
		return legacyName.equals("") ? name : legacyName;
	}
	
	/**
	 * Get the name of this ParticleMode
	 * @return The name of this mode as defined in the current messages.yml file
	 */
	public String getDisplayName () 
	{
		final String key = "LOCATION_" + toString() + "_NAME";
		try {
			return Message.valueOf(key).getValue();
		} catch (IllegalArgumentException e) {
			return "";
		}
	}
	
	/**
	 * Returns this ParticleLocations id
	 * @return
	 */
	public int getID () {
		return id;
	}
	
	/**
	 * Returns the ParticleLocation associated with this id
	 * @param id
	 * @return
	 */
	public static ParticleLocation fromId (int id) 
	{
		if (locationID.containsKey(id)) {
			return locationID.get(id);
		}
		return HEAD;
	}
	
	/**
	 * Returns the ParticleAction associated with the name
	 * @param name
	 * @return
	 */
	public static ParticleLocation fromName (String name)
	{
		if (name == null) {
			return HEAD;
		}
		
		final String location = name.toLowerCase();
		
		if (locationName.containsKey(location)) {
			return locationName.get(location);
		}
		
		if (locationLegacyName.containsKey(location)) {
			return locationLegacyName.get(location);
		}
		
		return HEAD;
	}
}
