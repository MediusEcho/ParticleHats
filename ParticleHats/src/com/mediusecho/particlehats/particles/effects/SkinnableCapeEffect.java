package com.mediusecho.particlehats.particles.effects;

import java.awt.image.BufferedImage;

import org.bukkit.Color;
import org.bukkit.util.Vector;

import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.SkinnableEffect;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.util.ResourceUtil;

public class SkinnableCapeEffect extends SkinnableEffect {

	public SkinnableCapeEffect(BufferedImage image, String imageName) 
	{
		super(image, imageName);
	}
	
	public SkinnableCapeEffect () {
		super(ResourceUtil.getImage("default_cape.png"), "cape", Message.TYPE_CAPE_NAME.getValue());
	}

	@Override
	public SkinnableEffect clone() {
		return new SkinnableCapeEffect(image, imageName);
	}

	@Override
	public boolean requiresFixedImageSize() {
		return true;
	}

	@Override
	public int[] getRequiredImageSize() {
		return new int[] {5, 6};
	}

	@Override
	public String getDescription() {
		return Message.TYPE_CAPE_DESCRIPTION.getValue();
	}

	@Override
	public ParticleLocation getDefaultLocation() {
		return ParticleLocation.CHEST;
	}

	@Override
	public boolean isCustom() {
		return false;
	}
	
	@Override
	public void build ()
	{
		if (image == null) {
			return;
		}
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		if (width != 5) {
			return;
		}
		
		if (height != 6) {
			return;
		}
		
		double zoffset = 0.1;
		double xoffset = 0.02;
		double[] xpoints = new double[] {-0.32, -0.16, 0, 0.16, 0.32};
		
		for (int y = 0; y < 6; y++)
		{
			for (int x = 0; x < 5; x++)
			{
				int rgb = image.getRGB(x, y);
				int b = rgb & 0xff;
				int g = (rgb & 0xff00) >> 8;
				int r = (rgb & 0xff0000) >> 16;
				Color color = Color.fromRGB(r, g, b);
						
				if (color.asRGB() != IGNORED_COLOR)
				{
					double xx = xpoints[x] - (x * xoffset);
					double yy = -y * 0.18;
					double zz = -0.28 - (y * zoffset);
					
					pixels.add(new PixelData(new Vector(xx, yy, zz), color));
				}
			}
		}
	}

}
