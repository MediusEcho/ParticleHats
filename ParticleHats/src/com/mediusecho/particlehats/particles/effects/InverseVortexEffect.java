package com.mediusecho.particlehats.particles.effects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.util.Vector;

import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Effect;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;

public class InverseVortexEffect extends Effect {

	@Override
	public String getName() {
		return "inverse_vortex";
	}

	@Override
	public String getDisplayName() {
		return Message.TYPE_INVERSE_VORTEX_NAME.getValue();
	}

	@Override
	public String getDescription() {
		return Message.TYPE_INVERSE_VORTEX_DESCRIPTION.getValue();
	}

	@Override
	public int getParticlesSupported() {
		return 2;
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
	public void build() 
	{
		List<Vector> frame1 = new ArrayList<Vector>();
		List<Vector> frame2 = new ArrayList<Vector>();
		
		double radius = 5;
		
		for (double y = 0; y <= 3; y += 0.1)
		{
			radius = y / 3;
			double x = radius * Math.cos(3 * y);
			double z = radius * Math.sin(3 * y);
			double yy = (y - 3) + 3;
			
			frame1.add(new Vector(x, yy, z));
			frame2.add(new Vector(-x, yy, -z));
		}
		
		List<List<Vector>> frames = createEmptyFrames();
		frames.add(frame1);
		frames.add(frame2);
		
		setFrames(frames);
	}

}
