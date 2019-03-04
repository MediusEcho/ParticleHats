package com.mediusecho.particlehats.particles.effects;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.util.Vector;

import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Effect;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;

public class CustomEffect extends Effect {

	private final int IGNORED_COLOR = Color.fromRGB(255, 0, 255).asRGB();
	private final Color BLEND_THRESHOLD = Color.fromRGB(245, 245, 245);
			
	private final BufferedImage image;
	private final String name;
	private double scale;
	
	private final List<PixelData> pixels;
	
	public CustomEffect (BufferedImage image, final String name, final double scale)
	{
		this.image = image;
		this.name = name;
		this.scale = scale;
		
		pixels = new ArrayList<PixelData>();
	}
	
	public CustomEffect ()
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
