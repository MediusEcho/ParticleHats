package com.mediusecho.particlehats.particles.effects;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Effect;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.particles.properties.ParticleData;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;

public class PixelEffect extends Effect {

	private final int IGNORED_COLOR = Color.fromRGB(255, 0, 255).asRGB();
	private final Color BLEND_THRESHOLD = Color.fromRGB(245, 245, 245);
			
	private final BufferedImage image;
	private final String name;
	private double scale;
	
	private final List<PixelData> pixels;
	
	public PixelEffect (BufferedImage image, final String name, final double scale)
	{
		this.image = image;
		this.name = name;
		this.scale = scale;
		
		pixels = new ArrayList<PixelData>();
		build();
	}
	
	public PixelEffect ()
	{
		this(null, "", 0);
	}
	
	public void rebuild (double scale)
	{
		this.scale = scale;
		pixels.clear();
		
		build();
	}
	
	/**
	 * Get this custom effects image name
	 * @return
	 */
	public String getImageName () {
		return name;
	}
	
	@Override
	public String getName() {
		return "custom";
	}

	@Override
	public String getDisplayName() {
		return Message.TYPE_CUSTOM_NAME.getValue();
	}

	@Override
	public String getDescription() {
		return Message.TYPE_CUSTOM_DESCRIPTION.getValue();
	}

	@Override
	public int getParticlesSupported() {
		return 1;
	}

	@Override
	public ParticleLocation getDefaultLocation() {
		return ParticleLocation.FEET;
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
	public boolean isCustom() {
		return true;
	}

	@Override
	public void build() 
	{
		if (image != null)
		{
			int width = image.getWidth();
			int height = image.getHeight();
			
			double xoffset = ((double) width / 2) - 0.5D;
			double yoffset = (double) height / 2D;
			
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
				        double xx = ((x - xoffset) * scale) * -1;
				        double yy = (((y - yoffset) * scale) - 1D) * -1;
				        
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
			
			if (((Player)entity).isSneaking()) {
				Core.debug("image is null? " + image == null);
			}
			
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
			Particle particle = particleEffect.getParticle();
			switch (particleEffect.getProperty())
			{
				case NO_DATA:
				{
					world.spawnParticle(particle, location, count, 0, 0, 0, speed);
					break;
				}
				
				case COLOR:
				{
					ParticleData data = hat.getParticleData(0);
					double scale = data.getScale();
					
					if (color.getRed() > BLEND_THRESHOLD.getRed()
							&& color.getGreen() > BLEND_THRESHOLD.getGreen()
							&& color.getBlue() > BLEND_THRESHOLD.getBlue())
					{
						DustOptions dustOptions = new DustOptions(data.getColorData().getColor(), (float) scale);
						world.spawnParticle(particle, location, count, 0, 0, 0, speed, dustOptions);
					}
					
					else 
					{
						DustOptions dustOptions = new DustOptions(color, (float) scale);
						world.spawnParticle(particle, location, count, 0, 0, 0, speed, dustOptions);
					}
					break;
				}
				
				case BLOCK_DATA:
				{
					BlockData blockData = hat.getParticleData(0).getBlock();
					world.spawnParticle(particle, location, count, 0, 0, 0, speed, blockData);
					break;
				}
				
				case ITEM_DATA:
				{
					ItemStack itemData = hat.getParticleData(0).getItem();
					world.spawnParticle(particle, location, count, 0, 0, 0, speed, itemData);
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
