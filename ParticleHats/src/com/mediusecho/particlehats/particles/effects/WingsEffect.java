package com.mediusecho.particlehats.particles.effects;

import java.util.Arrays;
import java.util.List;

import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;
import com.mediusecho.particlehats.util.ResourceUtil;

public class WingsEffect extends PixelEffect {
	
	public WingsEffect ()
	{
		super(ResourceUtil.getImage("wings.png"), "wings");
	}
	
	@Override
	public String getName() {
		return "wings";
	}

	@Override
	public String getDisplayName() {
		return Message.TYPE_WINGS_NAME.getValue();
	}

	@Override
	public String getDescription() {
		return Message.TYPE_WINGS_DESCRIPTION.getValue();
	}

	@Override
	public int getParticlesSupported() {
		return 1;
	}

	@Override
	public ParticleLocation getDefaultLocation() {
		return ParticleLocation.FEET;
	}

	@Override
	public List<ParticleTracking> getSupportedTrackingMethods() {
		return Arrays.asList(ParticleTracking.values());
	}

	@Override
	public ParticleTracking getDefaultTrackingMethod() {
		return ParticleTracking.TRACK_NOTHING;
	}

	@Override
	public boolean supportsAnimation() {
		return false;
	}

	@Override
	public boolean isCustom() {
		return false;
	}
}

