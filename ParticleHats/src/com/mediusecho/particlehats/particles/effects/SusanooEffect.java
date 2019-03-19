package com.mediusecho.particlehats.particles.effects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.util.Vector;

import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Effect;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;

public class SusanooEffect extends Effect {

	@Override
	public String getName() {
		return "susanoo";
	}

	@Override
	public String getDisplayName() {
		return Message.TYPE_SUSANOO_NAME.getValue();
	}

	@Override
	public String getDescription() {
		return Message.TYPE_SUSANOO_DESCRIPTION.getValue();
	}

	@Override
	public int getParticlesSupported() {
		return 4;
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
		List<Vector> frame1 = new ArrayList<Vector>();
		List<Vector> frame2 = new ArrayList<Vector>();
		List<Vector> frame3 = new ArrayList<Vector>();
		List<Vector> frame4 = new ArrayList<Vector>();
		
		double points = 20;
		double dist = 360.0f / points;
		double radius = 1;
		
		for (int y = 0; y < 3; y++)
		{
			for (double i = (dist * 2); i < (360 - dist); i += dist)
			{
				double angle = Math.toRadians(i + 90);
				double x = radius * Math.cos(angle);
				double z = radius * Math.sin(angle);
				
				switch (y)
				{
				case 0:
					frame3.add(new Vector(x, 0.4, z));
					break;
					
				case 1:
					frame2.add(new Vector(x, 1.2, z));
					break;
					
				case 2:
					frame1.add(new Vector(x, 2, z));
					break;
				}
			}
		}
		
		for (double y = 0.4; y < 2; y+= 0.2)
		{
			double angle = Math.toRadians(-90);
			double x = radius * Math.cos(angle);
			double z = radius * Math.sin(angle);
			
			frame4.add(new Vector(x, y, z));
		}
		
		List<List<Vector>> frames = createEmptyFrames();
		frames.add(frame1);
		frames.add(frame2);
		frames.add(frame3);
		frames.add(frame4);
		
		setFrames(frames);
	}

}
