package com.mediusecho.particlehats.particles.properties;

public enum ParticleTag {

	CUSTOM (""),
	ARROWS ("arrows"),
	ARMOUR_STAND ("amour_stand");
	
	private final String name;
	
	private ParticleTag (final String name)
	{
		this.name = name;
	}
}
