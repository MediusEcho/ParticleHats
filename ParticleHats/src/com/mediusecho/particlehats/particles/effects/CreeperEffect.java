package com.mediusecho.particlehats.particles.effects;

import java.util.Arrays;
import java.util.List;

import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;
import com.mediusecho.particlehats.util.ResourceUtil;

public class CreeperEffect extends PixelEffect {
	
	public CreeperEffect ()
	{
		super(ResourceUtil.getImage("creeper_face.png"), "creeper_face");
	}
	
	@Override
	public String getName() {
		return "creeper_hat";
	}

	@Override
	public String getDisplayName() {
		return Message.TYPE_CREEPER_HAT_NAME.getValue();
	}

	@Override
	public String getDescription() {
		return Message.TYPE_CREEPER_HAT_DESCRIPTION.getValue();
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
