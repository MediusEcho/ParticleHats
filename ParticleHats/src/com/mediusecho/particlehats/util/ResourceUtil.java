package com.mediusecho.particlehats.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
	
	/**
	 * Copies the file provided
	 * @param File to copy
	 * @param File to copy to
	 * @throws IOException 
	 */
	public static void copyFile (InputStream in, File file) throws IOException  
	{	
		OutputStream out = new FileOutputStream(file);
		byte[] buf = new byte[1024];
		
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		
		out.close();
		in.close();
	}
}
