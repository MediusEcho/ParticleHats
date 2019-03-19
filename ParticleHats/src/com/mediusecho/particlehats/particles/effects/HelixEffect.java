package com.mediusecho.particlehats.particles.effects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.util.Vector;

import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Effect;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;

public class HelixEffect extends Effect {

	@Override
	public String getName() {
		return "helix";
	}

	@Override
	public String getDisplayName() {
		return Message.TYPE_HELIX_NAME.getValue();
	}

	@Override
	public String getDescription() {
		return Message.TYPE_HELIX_DESCRIPTION.getValue();
	}

	@Override
	public int getParticlesSupported() {
		return 3;
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
		double points = 16;
		double dist = 360.0f / points;
		double radius = 1;
		double offset = 2 / points;
		double rings = 3;
		double angleOffset = 360.0f / rings;
		double angleStart = 0;
		
		List<List<Vector>> frames = createEmptyFrames();
		
		for (int i = 0; i < rings; i++)
		{
			List<Vector> frame = new ArrayList<Vector>();
			double y = 0;
			
			for (double j = angleStart; j < (angleStart + 360.0f); j+= dist)
			{
				double angle = (j * Math.PI / 180);
				double x = radius * Math.cos(angle);
				double z = radius * Math.sin(angle);
				
				frame.add(new Vector(x, y, z));
				y += offset;
			}
			
			angleStart += angleOffset;
			frames.add(frame);
		}
		
		setFrames(frames);
	}

}
