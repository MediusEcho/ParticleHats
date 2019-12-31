package com.mediusecho.particlehats.particles.effects;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;
import com.mediusecho.particlehats.util.StringUtil;

public abstract class CommunityEffect extends PixelEffect {

	private final String displayName;
	private final String credit;
	
	public CommunityEffect (BufferedImage image, String name, String displayName, String credit)
	{
		super(image, name);
		this.displayName = StringUtil.colorize(displayName);
		this.credit = StringUtil.colorize("&8by " + credit);
	}
	
	@Override
	public String getName() {
		return displayName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getDescription() {
		return credit;
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
