package com.mediusecho.particlehats.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;

import org.bukkit.Sound;

import com.mediusecho.particlehats.ParticleHats;

public class ResourceUtil {

	private final static ParticleHats core = ParticleHats.instance;
	
	public static BufferedImage getImage (String resourceName)
	{
		try
		{
			InputStream stream = core.getResource("types/" + resourceName);
			BufferedImage image = ImageIO.read(stream);
			
			return image;
		}
		
		catch (Exception e) {
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
	
	public static void copyImage (InputStream stream, String path)
	{
		if (stream == null) {
			return;
		}
		
		try {
			Files.copy(stream, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {}
	}
	
	/**
	 * Removes the fileNames extension
	 * @param fileName
	 * @return
	 */
	public static String removeExtension (String fileName) {
		return fileName.replaceFirst("[.][^.]+$", "");
	}
	
	/**
	 * Tries to find a matching Sound from the soundName
	 * @param soundName
	 * @param fallback
	 * @return
	 */
	public static Sound getSound (String soundName, String fallback)
	{
		try {
			return Sound.valueOf(soundName);
		}
		
		catch (IllegalArgumentException e)
		{
			try {
				return Sound.valueOf(fallback);
			}
			
			catch (IllegalArgumentException ex) {
				return null;
			}
		}
	}
}
