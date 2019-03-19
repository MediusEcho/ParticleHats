package com.mediusecho.particlehats.particles.effects;

import java.util.Arrays;
import java.util.List;

import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;
import com.mediusecho.particlehats.util.ResourceUtil;

public class AngelWingsEffect extends PixelEffect {
	
	public AngelWingsEffect ()
	{
		super(ResourceUtil.getImage("angel_wings.png"), "angel_wings", 0.2D);
	}
	
	@Override
	public String getName() {
		return "angel_wings";
	}

	@Override
	public String getDisplayName() {
		return Message.TYPE_ANGEL_WINGS_NAME.getValue();
	}

	@Override
	public String getDescription() {
		return Message.TYPE_ANGEL_WINGS_DESCRIPTION.getValue();
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

