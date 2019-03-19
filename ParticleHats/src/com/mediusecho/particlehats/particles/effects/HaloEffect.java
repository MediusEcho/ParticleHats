package com.mediusecho.particlehats.particles.effects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.util.Vector;

import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Effect;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;

public class HaloEffect extends Effect {

	@Override
	public String getName() {
		return "halo";
	}

	@Override
	public String getDisplayName() {
		return Message.TYPE_HALO_NAME.getValue();
	}

	@Override
	public String getDescription() {
		return Message.TYPE_HALO_DESCRIPTION.getValue();
	}

	@Override
	public int getParticlesSupported() {
		return 1;
	}

	@Override
	public ParticleLocation getDefaultLocation() {
		return ParticleLocation.HEAD;
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
		return true;
	}

	@Override
	public boolean isCustom() {
		return false;
	}

	@Override
	public void build ()
	{
		final float count = 12;
		final float distance = 360.0f / count;
		float radius = 0.8f;
		
		List<Vector> points = new ArrayList<Vector>();
		
		for (float i = 0.0f; i < 360.0f; i += distance)
		{
			double angle = (i * Math.PI / 180);
			double x = radius * Math.cos(angle);
			double z = radius * Math.sin(angle);
			
			points.add(new Vector(x, 0, z));
		}
		
		List<List<Vector>> frames = createEmptyFrames();
		frames.add(points);
		
		setFrames(frames);
	}
}
