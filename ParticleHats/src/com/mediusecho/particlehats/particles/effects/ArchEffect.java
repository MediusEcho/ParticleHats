package com.mediusecho.particlehats.particles.effects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.util.Vector;

import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Effect;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;

public class ArchEffect extends Effect {

	@Override
	public String getName() {
		return "arch";
	}

	@Override
	public String getDisplayName() {
		return Message.TYPE_ARCH_NAME.getValue();
	}

	@Override
	public String getDescription() {
		return Message.TYPE_ARCH_DESCRIPTION.getValue();
	}

	@Override
	public int getParticlesSupported() {
		return 5;
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
		return true;
	}

	@Override
	public boolean isCustom() {
		return false;
	}

	@Override
	public void build() 
	{
		List<Vector> l1 = new ArrayList<Vector>();
		List<Vector> l2 = new ArrayList<Vector>();
		List<Vector> l3 = new ArrayList<Vector>();
		List<Vector> l4 = new ArrayList<Vector>();
		List<Vector> l5 = new ArrayList<Vector>();
		
		double count = 50;
		double distance = 360.0f / count;
		double radius = 2.3;
		
		for (double i = 0; i < (180 + distance); i+= distance)
		{
			double angle = (i * Math.PI / 180);
			double x = radius * Math.cos(angle);
			double y = radius * Math.sin(angle);
			
			l1.add(new Vector(x, y, 0.6));
			l2.add(new Vector(x, y, 0.3));
			l3.add(new Vector(x, y, 0));
			l4.add(new Vector(x, y, -0.3));
			l5.add(new Vector(x, y, -0.6));
		}
		
		List<List<Vector>> frames = createEmptyFrames();
		frames.addAll(Arrays.asList(l1, l2, l3, l4, l5));
		
		setFrames(frames);
	}

}
