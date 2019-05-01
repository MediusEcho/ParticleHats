package com.mediusecho.particlehats.particles;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.particles.properties.ParticleAnimation;
import com.mediusecho.particlehats.particles.properties.ParticleData;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;
import com.mediusecho.particlehats.particles.renderer.ParticleRenderer;
import com.mediusecho.particlehats.util.MathUtil;

public abstract class Effect {

	private List<List<Vector>> frames;
	protected ParticleRenderer renderer = Core.instance.getParticleRenderer();
	
	public Effect ()
	{
		frames = new ArrayList<List<Vector>>();
	}
	
	protected void setFrames (List<List<Vector>> frames) {
		this.frames = frames;
	}
	
	public abstract String getName();
	
	public abstract String getDisplayName();
	
	public abstract String getDescription();
	
	public abstract int getParticlesSupported();
	
	public abstract ParticleLocation getDefaultLocation();
	
	public abstract List<ParticleTracking> getSupportedTrackingMethods();
	
	public abstract ParticleTracking getDefaultTrackingMethod();
	
	public abstract boolean supportsAnimation();
	
	public abstract boolean isCustom();
	
	public abstract void build();
	
	/**
	 * Creates and returns an empty List for all particle location data
	 * @return
	 */
	protected List<List<Vector>> createEmptyFrames () {
		return new ArrayList<List<Vector>>();
	}
	
	protected Vector getTrackingPosition (Hat hat, Vector target, Location location, double cos, double sin)
	{
		ParticleTracking trackingMethod = hat.getTrackingMethod();
		double x = 0;
		double y = 0;
		double z = 0;
		double offsetY = hat.getTotalOffset().getY();
		
		switch (trackingMethod)
		{
			case TRACK_NOTHING:
			{
				x = target.getX();
				y = target.getY() + offsetY;
				z = target.getZ();
				break;
			}
			
			case TRACK_BODY_ROTATION:
			{
				double targetX = target.getX();
				double targetZ = target.getZ();
				
				x = ((targetX * cos) - (targetZ * sin));
				y = target.getY() + offsetY;
				z = ((targetX * sin) + (targetZ * cos));
				break;
			}
			
			case TRACK_HEAD_MOVEMENT:
			{
				Vector v = new Vector(target.getX(), target.getY() + offsetY, target.getZ());
				Vector result = MathUtil.rotateVector(v, location);
				
				x = result.getX();
				y = result.getY();
				z = result.getZ();
				break;
			}
		}
		
		return new Vector(x, y, z);
	}
	
	protected Vector getAngleVector (double x, double y, double z, Vector target)
	{
		if (Math.abs(z) > 0) {
			target = MathUtil.rotateXAxis(target, z);
		}
		
		if (Math.abs(y) > 0) {
			target = MathUtil.rotateYAxis(target, y);
		}
		
		if (Math.abs(x) > 0) {
			target = MathUtil.rotateZAxis(target, -x);
		}
		
		return target;
	}
	
	/**
	 * Display this effect for an entity
	 * @param ticks
	 * @param entity
	 * @param hat
	 */
	public void display (int ticks, Entity entity, Hat hat)
	{
		int loops = 0;
		int particleIndex = 0;
		
		if (ticks % hat.getUpdateFrequency() == 0)
		{
			Location location = entity.getLocation();
			if (hat.getTrackingMethod() == ParticleTracking.TRACK_HEAD_MOVEMENT && entity instanceof Player) {
				location = ((Player)entity).getEyeLocation();
			}
			
			double yaw = Math.toRadians(location.getYaw());
			double cos = Math.cos(yaw);
			double sin = Math.sin(yaw);
			
			Vector offset = hat.getOffset();
			double offsetX = ((offset.getX() * cos) - (offset.getZ() * sin));
			double offsetZ = ((offset.getX() * sin) + (offset.getZ() * cos));
			
			Vector angle = hat.getAngle();
			double angleXRad = Math.toRadians(angle.getX());
			double angleYRad = Math.toRadians(angle.getY());
			double angleZRad = Math.toRadians(angle.getZ());
			
			for (List<Vector> frame : frames)
			{
				particleIndex = loops++;
				
				// Display as an animation
				if (supportsAnimation() && hat.getAnimation() == ParticleAnimation.ANIMATED)
				{
					int size = frame.size() - 1;
					int frameIndex = frames.indexOf(frame);
					int index = MathUtil.clamp(hat.getAnimationIndex(frameIndex), 0, size);
					
					Location clone = location.clone();
					Vector target = frame.get(index).clone();
					
					target.multiply(hat.getScale());
					
					target.add(new Vector(offsetX, 0, offsetZ));
					target = getAngleVector(angleXRad, angleYRad, angleZRad, target);
					
					clone.add(getTrackingPosition(hat, target, location, cos, sin));
					displayParticle(clone, hat, particleIndex);
					hat.setAnimationIndex(frameIndex, MathUtil.wrap(++index, size, 0));
				}
				
				// Display statically
				else
				{
					for (Vector target : frame)
					{
						Vector v = target.clone();
						v.multiply(hat.getScale());
						v = getAngleVector(angleXRad, angleYRad, angleZRad, v);
						
						//Vector v = getAngleVector(angleXRad, angleYRad, angleZRad, target.clone());
						Location clone = location.clone().add(offsetX, 0, offsetZ);		
						
						clone.add(getTrackingPosition(hat, v, location, cos, sin));
						displayParticle(clone, hat, particleIndex);
					}
				}
			}
		}
	}
	
	public void displayParticle (Location location, Hat hat, int index)
	{
		int speed = hat.getSpeed();
		int count = hat.getCount();
		World world = location.getWorld();
		
		ParticleEffect particleEffect = hat.getParticle(index);
		if (particleEffect == ParticleEffect.NONE) 
		{
			particleEffect = hat.getParticle(0); 
			index = 0;
		}
		
		if (particleEffect != ParticleEffect.NONE)
		{
			Vector randomOffset = hat.getRandomOffset();
			double rxo = randomOffset.getX();
			double ryo = randomOffset.getY();
			double rzo = randomOffset.getZ();
			
			ParticleData data = hat.getParticleData(index);
			
			switch (particleEffect.getProperty())
			{
				case NO_DATA:
				{
					renderer.spawnParticle(world, particleEffect, location, count, rxo, ryo, rzo, speed);
					break;
				}
				
				case COLOR:
				{
					renderer.spawnParticleColor(world, particleEffect, location, count, rxo, ryo, rzo, speed, data.getColorData().getColor(), data.getScale());
					break;
				}
				
				case BLOCK_DATA:
				{
					renderer.spawnParticleBlockData(world, particleEffect, location, count, rxo, ryo, rzo, speed, data);
					break;
				}
				
				case ITEM_DATA:
				{
					renderer.spawnParticleItemData(world, particleEffect, location, count, rxo, ryo, rzo, speed, data);
					break;
				}
				
				case ITEMSTACK_DATA:
				{
					hat.getParticleData(index).getItemStackData().dropItem(world, location, hat);
					break;
				}
			}
		}
	}
}