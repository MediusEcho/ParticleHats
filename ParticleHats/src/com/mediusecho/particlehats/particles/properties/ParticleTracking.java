package com.mediusecho.particlehats.particles.properties;

import java.util.HashMap;
import java.util.Map;

import com.mediusecho.particlehats.locale.Message;

public enum ParticleTracking {

	TRACK_NOTHING       (0),
	TRACK_HEAD_MOVEMENT (1),
	TRACK_BODY_ROTATION (2);
	
	private final int id;
	private static final Map<Integer, ParticleTracking> trackingID = new HashMap<Integer, ParticleTracking>();
	private static final Map<String, ParticleTracking> trackingName = new HashMap<String, ParticleTracking>();
	
	static
	{
		for (ParticleTracking pt : values()) 
		{
			trackingID.put(pt.id, pt);
			trackingName.put(pt.toString(), pt);
		}
	}
	
	private ParticleTracking (final int id)
	{
		this.id = id;
	}
	
	public int getID () {
		return id;
	}
	
	public String getName () {
		return this.toString().toLowerCase();
	}
	
	/**
	 * Get the name of this ParticleTracking
	 * @return The name of this tracking as defined in the current messages.yml file
	 */
	public String getDisplayName () 
	{
		final String key = toString() + "_NAME";
		try {
			return Message.valueOf(key).getValue();
		} catch (IllegalArgumentException e) {
			return "";
		}
	}
	
	/**
	 * Returns the ParticleTracking object that has this id
	 * @param id
	 * @return
	 */
	public static ParticleTracking fromID (int id)
	{
		if (trackingID.containsKey(id)) {
			return trackingID.get(id);
		}
		return TRACK_NOTHING;
	}
	
	public static ParticleTracking fromName (String name)
	{
		if (name == null) {
			return TRACK_NOTHING;
		}
		
		String tracking = name.toUpperCase();
		
		if (trackingName.containsKey(tracking)) {
			return trackingName.get(tracking);
		}
		return TRACK_NOTHING;
	}
}
