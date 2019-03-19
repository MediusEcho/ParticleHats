package com.mediusecho.particlehats.particles.effects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.util.Vector;

import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Effect;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;

public class CapeEffect extends Effect {

	@Override
	public String getName() {
		return "cape";
	}

	@Override
	public String getDisplayName() {
		return Message.TYPE_CAPE_NAME.getValue();
	}

	@Override
	public String getDescription() {
		return Message.TYPE_CAPE_DESCRIPTION.getValue();
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
		return Arrays.asList(ParticleTracking.TRACK_NOTHING, ParticleTracking.TRACK_BODY_ROTATION);
	}

	@Override
	public ParticleTracking getDefaultTrackingMethod() {
		return ParticleTracking.TRACK_BODY_ROTATION;
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
	public void build() 
	{
		double zoffset = 0.1;
		double xoffset = 0.02;
		double[] xpoints = new double[] {-0.32, -0.16, 0, 0.16, 0.32};
		
		List<Vector> points = new ArrayList<Vector>();
		
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 5; j++) {
				points.add(new Vector(xpoints[j] - (i * xoffset), 1.4 - (i * 0.18), -0.28 - (i * zoffset)));
			}
		}
		
		List<List<Vector>> frames = createEmptyFrames();
		frames.add(points);
		
		setFrames(frames);
	}

}
