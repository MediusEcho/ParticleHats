package com.mediusecho.particlehats.particles.effects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.util.Vector;

import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Effect;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;
import com.mediusecho.particlehats.util.MathUtil;

public class AtomEffect extends Effect {

	@Override
	public String getName() {
		return "atom";
	}

	@Override
	public String getDisplayName() {
		return Message.TYPE_ATOM_NAME.getValue();
	}

	@Override
	public String getDescription() {
		return Message.TYPE_ATOM_DESCRIPTION.getValue();
	}

	@Override
	public int getParticlesSupported() {
		return 3;
	}

	@Override
	public ParticleLocation getDefaultLocation() {
		// TODO Auto-generated method stub
		return null;
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
		double radius = 1.5;
		double points = 25;
		double dist = 360.0f / points;
		double rings = 3;
		double angleDist = 360.0f / rings;
		double angle = 90;
		
		Vector height = new Vector(0, radius, 0);
		List<List<Vector>> frames = createEmptyFrames();
		
		for (int i = 0; i < rings; i++)
		{
			List<Vector> list = new ArrayList<Vector>();
			for (float j = 0; j < 360.0f; j += dist)
			{
				double a = (j * Math.PI / 180);
				double x = radius * Math.cos(a);
				double z = radius * Math.sin(a);
				
				Vector v = MathUtil.rotateZAxis(new Vector(x, 0, z), Math.toRadians(angle));
				list.add(v.add(height));
			}
			angle += angleDist;
			frames.add(list);
		}
		
		setFrames(frames);
		
	}

}
