package com.mediusecho.particlehats.particles.effects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.util.Vector;

import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Effect;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;

public class SphereEffect extends Effect {

	@Override
	public String getName() {
		return "sphere";
	}

	@Override
	public String getDisplayName() {
		return Message.TYPE_SPHERE_NAME.getValue();
	}

	@Override
	public String getDescription() {
		return Message.TYPE_SPHERE_DESCRIPTION.getValue();
	}

	@Override
	public int getParticlesSupported() {
		return 10;
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
		double phi = 0;
		double radius = 1.5;
		double angle = Math.PI / 10;
		
		List<List<Vector>> frames = createEmptyFrames();
		
		while (phi < Math.PI)
		{
			List<Vector> frame = new ArrayList<Vector>();
			phi += angle;
			
			for (double a = 0; a <= 2 * Math.PI; a += Math.PI / 20)
			{
				double x = radius * Math.cos(a) * Math.sin(phi);
				double y = radius * Math.cos(phi) + 1.5;
				double z = radius * Math.sin(a) * Math.sin(phi);
				
				frame.add(new Vector(x, y, z));
			}
			
			frames.add(frame);
		}
		
		setFrames(frames);
	}

}
