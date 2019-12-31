package com.mediusecho.particlehats.particles;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.mediusecho.particlehats.particles.properties.ParticleData;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;
import com.mediusecho.particlehats.util.ResourceUtil;
import com.mediusecho.particlehats.util.StringUtil;

public abstract class SkinnableEffect extends Effect {

	protected final int IGNORED_COLOR = Color.fromRGB(255, 0, 255).asRGB();
	protected final Color BLEND_THRESHOLD = Color.fromRGB(245, 245, 245);
	
	protected final BufferedImage image;
	
	protected final String imageName;
	protected final String displayName;
	
	protected final List<PixelData> pixels;
	
	public SkinnableEffect (BufferedImage image, String imageName, String displayName)
	{
		this.image = image;
		this.imageName = imageName;
		this.displayName = displayName;
		this.pixels = new ArrayList<PixelData>();
	}
	
	public SkinnableEffect (BufferedImage image, String imageName) {
		this(image, imageName, StringUtil.capitalizeFirstLetter(imageName));
	}
	
	/**
	 * Creates a copy of this SkinnableEffect class
	 */
	public abstract SkinnableEffect clone();
	
	/**
	 * Checks whether this SkinnableEffect requires a fixed image size to work properly
	 * @return
	 */
	public abstract boolean requiresFixedImageSize();
	
	/**
	 * Get the required size of images this SkinnableEffect needs in order to work properly
	 * @return
	 */
	public abstract int[] getRequiredImageSize ();
	
	/**
	 * Get the name of this SkinnableEffect's image
	 * @return
	 */
	public String getImageName () {
		return imageName;
	}
	
	/**
	 * Get the name of this SkinnableEffects's image without file extensions
	 * @return
	 */
	public String getImageNameWithoutExtension () {
		return ResourceUtil.removeExtension(imageName);
	}
	
	@Override
	public String getName () {
		return "custom";
	}
	
	@Override
	public String getDisplayName () {
		return displayName;
	}
	
	@Override
	public int getParticlesSupported () {
		return 1;
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
		return false;
	}
	
	@Override
	public void build() 
	{
		if (image != null)
		{
			int width = image.getWidth();
			int height = image.getHeight();
			
			double scale = 0.2;
			double centerX = ((double) width / 2D) - 0.5;
			double centerY = ((double) height / 2D) - 0.5;
			
			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					int rgb = image.getRGB(x, y);
					int b = rgb & 0xff;
					int g = (rgb & 0xff00) >> 8;
					int r = (rgb & 0xff0000) >> 16;
					Color color = Color.fromRGB(r, g, b);
			        
					if (color.asRGB() != IGNORED_COLOR)
					{
						double xx = ((x - centerX) * -1) * scale;
						double yy = ((y - centerY) * -1) * scale;
				        
						pixels.add(new PixelData(new Vector(xx, yy, 0), color));
					}
				}
			}
		}
	}
	
	@Override
	public void display (int ticks, Entity entity, Hat hat)
	{
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
			
			for (PixelData pixelData : pixels)
			{
				Vector v = pixelData.getPosition().clone().multiply(hat.getScale());
				v = getAngleVector(angleXRad, angleYRad, angleZRad, v);
				
				Location clone = location.clone().add(offsetX, 0, offsetZ);		
				
				clone.add(getTrackingPosition(hat, v, location, cos, sin));
				displayParticle(clone, hat, pixelData.getColor());
			}
		}
	}

	private void displayParticle (Location location, Hat hat, Color color)
	{
		int speed = hat.getSpeed();
		int count = hat.getCount();
		World world = location.getWorld();
		
		ParticleEffect particleEffect = hat.getParticle(0);		
		if (particleEffect != ParticleEffect.NONE)
		{
			ParticleData data = hat.getParticleData(0);
			switch (particleEffect.getProperty())
			{
				case NO_DATA:
				{
					renderer.spawnParticle(world, particleEffect, location, count, 0, 0, 0, speed);
					break;
				}
				
				case COLOR:
				{
					//ParticleData data = hat.getParticleData(0);
					double scale = data.getScale();
					
					if (color.getRed() > BLEND_THRESHOLD.getRed()
							&& color.getGreen() > BLEND_THRESHOLD.getGreen()
							&& color.getBlue() > BLEND_THRESHOLD.getBlue())
					{
						Color c = data.getColorData().getColor();
						renderer.spawnParticleColor(world, particleEffect, location, count, 0, 0, 0, speed, c, scale);
					}
					
					else 
					{
						renderer.spawnParticleColor(world, particleEffect, location, count, 0, 0, 0, speed, color, scale);
					}
					break;
				}
				
				case BLOCK_DATA:
				{
					renderer.spawnParticleBlockData(world, particleEffect, location, count, 0, 0, 0, speed, data);
					break;
				}
				
				case ITEM_DATA:
				{
					renderer.spawnParticleItemData(world, particleEffect, location, count, 0, 0, 0, speed, data);
					break;
				}
				
				case ITEMSTACK_DATA:
				{
					hat.getParticleData(0).getItemStackData().dropItem(world, location, hat);
					break;
				}
			}
		}
	}
	
	public class PixelData {
		
		private final Vector position;
		private final Color color;
		
		public PixelData (Vector position, Color color)
		{
			this.position = position;
			this.color = color;
		}
		
		/**
		 * Get this pixels position
		 * @return
		 */
		public Vector getPosition () {
			return position;
		}
		
		/**
		 * Get this pixels color
		 * @return
		 */
		public Color getColor () {
			return color;
		}
	}
}
