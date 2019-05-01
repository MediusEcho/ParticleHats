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
	 * Compares two BufferedImages
	 * @param a
	 * @param b
	 * @return True if both images are the same
	 */
	public static boolean compareImages (BufferedImage a, BufferedImage b)
	{
		if (a == null || b == null) return false;
		if (a == b) return true;
		
		if (a.getWidth() != b.getWidth() || a.getHeight() != b.getHeight()) {
			return false;
		}
		
		int width = a.getWidth();
		int height = a.getHeight();
		
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				if (a.getRGB(x, y) != b.getRGB(x, y)) {
					return false;
				}
			}
		}
		
		return true;
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
	
	public static String removeExtension (String fileName) {
		return fileName.replaceFirst("[.][^.]+$", "");
	}
}
