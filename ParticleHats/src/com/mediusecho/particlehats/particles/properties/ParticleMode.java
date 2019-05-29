package com.mediusecho.particlehats.particles.properties;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.locale.Message;

public enum ParticleMode {

	ACTIVE         (0, -1),
	WHEN_MOVING    (1, -1),
	WHEN_AFK       (2, -1),
	WHEN_PEACEFUL  (3, -1),
	WHEN_GLIDING   (4, 9),
	WHEN_SPRINTING (5, -1),
	WHEN_SWIMMING  (6, 13),
	WHEN_FLYING    (7, -1);
	
	private final int id;
	private final int supportedVersion;
	
	private static final Map<Integer, ParticleMode> modeID = new HashMap<Integer, ParticleMode>();
	private static final Map<String, ParticleMode> modeName = new HashMap<String, ParticleMode>();
	
	static
	{
		for (ParticleMode pm : values()) 
		{
			modeID.put(pm.id, pm);
			modeName.put(pm.toString(), pm);
		}
	}
	
	private ParticleMode (final int id, final int supportedVersion)
	{
		this.id = id;
		this.supportedVersion = supportedVersion;
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
	 * @return
	 */
	public String getName () {
		return this.toString().toLowerCase();
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
	 * Get the name of this ParticleMode without color codes
	 * @return
	 */
	public String getStrippedName () {
		return ChatColor.stripColor(getDisplayName());
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
	 * Checks to see if the current server version supports this mode
	 * @return
	 */
	public boolean isSupported ()
	{
		if (supportedVersion == -1) {
			return true;
		}
		return ParticleHats.serverVersion >= supportedVersion;
	}
	
	/**
	 * Returns the ParticleModes associated with this id
	 * @param id
	 * @return
	 */
	public static ParticleMode fromId (int id) 
	{
		if (modeID.containsKey(id)) {
			return modeID.get(id);
		}
		return ACTIVE;
	}
	
	/**
	 * Returns the ParticleMode that matches this name
	 * @param name
	 * @return
	 */
	public static ParticleMode fromName (String name)
	{
		if (name == null) {
			return ACTIVE;
		}
		
		String mode = name.toUpperCase();
		
		if (modeName.containsKey(mode)) {
			return modeName.get(mode);
		}
		return ACTIVE;
	}
}
