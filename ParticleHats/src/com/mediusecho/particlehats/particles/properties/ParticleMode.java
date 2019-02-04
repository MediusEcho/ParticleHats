package com.mediusecho.particlehats.particles.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.mediusecho.particlehats.locale.Message;

public enum ParticleMode {

	ACTIVE         (0),
	WHEN_MOVING    (1),
	WHEN_AFK       (2),
	WHEN_PEACEFUL  (3),
	WHEN_GLIDING   (4),
	WHEN_SPRINTING (5),
	WHEN_SWIMMING  (6);
	
	private final int id;
	private static final Map<Integer, ParticleMode> modeID = new HashMap<Integer, ParticleMode>();
	
	static
	{
		for (ParticleMode pm : values()) {
			modeID.put(pm.id, pm);
		}
	}
	
	private ParticleMode (final int id)
	{
		this.id = id;
	}
	
	/**
	 * Get the id of this ParticleMode
	 * @return
	 */
	public int getID () {
		return id;
	}
	
	/**
	 * Get the name of this ParticleMode
	 * @return The name of this mode as defined in the current messages.yml file
	 */
	public String getDisplayName () 
	{
		final String key = "MODE_" + toString() + "_NAME";
		try {
			return Message.valueOf(key).getValue();
		} catch (IllegalArgumentException e) {
			return "";
		}
	}
	
	/**
	 * Get the description of this ParticleMode
	 * @return The description of this mode as defined in the current messages.yml file
	 */
	public String getDescription ()
	{
		final String key = "MODE_" + toString() + "_DESCRIPTION";
		try {
			return Message.valueOf(key).getValue();
		} catch (IllegalArgumentException e) {
			return "";
		}
	}
	
	/**
	 * Returns the ParticleModes associated with this id
	 * @param id
	 * @return
	 */
	public static ParticleMode fromId (int id) 
	{
		for (Entry<Integer, ParticleMode> entry : modeID.entrySet()) {
			if (entry.getKey() != id) {
				continue;
			}
			return entry.getValue();
		}
		return ACTIVE;
	}
}
