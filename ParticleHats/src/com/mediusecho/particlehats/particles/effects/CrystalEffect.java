package com.mediusecho.particlehats.particles.effects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.util.Vector;

import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Effect;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;

public class CrystalEffect extends Effect {

	@Override
	public String getName() {
		return "crystal";
	}

	@Override
	public String getDisplayName() {
		return Message.TYPE_CRYSTAL_NAME.getValue();
	}

	@Override
	public String getDescription() {
		return Message.TYPE_CRYSTAL_DESCRIPTION.getValue();
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
		return ParticleTracking.TRACK_HEAD_MOVEMENT;
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
		List<Vector> list = new ArrayList<Vector>();
		
		// Center
		list.add(new Vector(0, 0.7f, 0));
		list.add(new Vector(0, 1.0f, 0));
		list.add(new Vector(0, 1.3f, 0));
		list.add(new Vector(0, 1.6f, 0));
		list.add(new Vector(0, 1.9f, 0));
		
		// Left 1
		list.add(new Vector(0.2f, 1.0f, 0));
		list.add(new Vector(0.2f, 1.3f, 0));
		list.add(new Vector(0.2f, 1.6f, 0));
		
		// Left 2
		list.add(new Vector(0.4f, 1.3f, 0));
		
		// Right 1
		list.add(new Vector(-0.2f, 1.0f, 0));
		list.add(new Vector(-0.2f, 1.3f, 0));
		list.add(new Vector(-0.2f, 1.6f, 0));
		
		// Right 2
		list.add(new Vector(-0.4f, 1.3f, 0));
		
		// Forward 1
		list.add(new Vector(0, 1.0f, 0.2f));
		list.add(new Vector(0, 1.3f, 0.2f));
		list.add(new Vector(0, 1.6f, 0.2f));
		
		// Forward 2
		list.add(new Vector(0, 1.3f, 0.4f));
		
		// Back 1
		list.add(new Vector(0, 1.0f, -0.2f));
		list.add(new Vector(0, 1.0f, -0.2f));
		list.add(new Vector(0, 1.0f, -0.2f));
		
		// Back 2
		list.add(new Vector(0, 1.3f, -0.4f));
		
		List<List<Vector>> frames = createEmptyFrames();
		frames.add(list);
		
		setFrames(frames);
	}

}
