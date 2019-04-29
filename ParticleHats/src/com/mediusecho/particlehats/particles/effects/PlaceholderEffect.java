package com.mediusecho.particlehats.particles.effects;

import java.util.Arrays;
import java.util.List;

import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Effect;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;

public class PlaceholderEffect extends Effect {

	@Override
	public String getName() {
		return "none";
	}

	@Override
	public String getDisplayName() {
		return Message.TYPE_NONE_NAME.getValue();
	}

	@Override
	public String getDescription() {
		return Message.TYPE_NONE_DESCRIPTION.getValue();
	}

	@Override
	public int getParticlesSupported() {
		return 0;
	}

	@Override
	public ParticleLocation getDefaultLocation() {
		return ParticleLocation.HEAD;
	}

	@Override
	public List<ParticleTracking> getSupportedTrackingMethods() {
		return Arrays.asList(ParticleTracking.TRACK_NOTHING);
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

	@Override
	public void build() {
		// Nothing to do :o
	}

}
