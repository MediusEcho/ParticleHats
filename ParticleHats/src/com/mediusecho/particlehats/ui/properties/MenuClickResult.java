package com.mediusecho.particlehats.ui.properties;

import com.mediusecho.particlehats.managers.SettingsManager;

/**
 * Represents the result of a button click
 * @author MediusEcho
 *
 */
public enum MenuClickResult {

	/**
	 * The player is not clicking inside a menu
	 */
	NONE,
	
	/**
	 * The player is clicking inside a menu
	 */
	NEUTRAL,
	
	/**
	 * The player is increasing a value inside a menu
	 */
	POSITIVE,
	
	/**
	 * The player is decreasing a value inside a menu
	 */
	NEGATIVE;
	
	public double getModifier ()
	{
		if (this == NONE || this == NEUTRAL) {
			return 0;
		}
		
		double mod = SettingsManager.EDITOR_SOUND_MODIFIER.getDouble();
		return mod * (this == POSITIVE ? 1f : -1f);
	}
}
