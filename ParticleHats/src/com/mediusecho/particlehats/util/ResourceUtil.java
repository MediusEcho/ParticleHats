package com.mediusecho.particlehats.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.mediusecho.particlehats.Core;

public class ResourceUtil {

	private final static Core core = Core.instance;
	
	public static BufferedImage getImage (String resourceName)
	{
		try
		{
			InputStream stream = core.getResource("types/" + resourceName);
			BufferedImage image = ImageIO.read(stream);
			
			return image;
		}
		
		catch (IOException e) {
			return null;
		}
	}
}
