package com.mediusecho.particlehats.util;

import com.mediusecho.particlehats.ParticleHats;
import org.bukkit.Sound;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class ResourceUtil {

	private final static ParticleHats core = ParticleHats.instance;

	private static final Map<Integer, Integer> particleMenuCompatibilityMap = new HashMap<>();

	static
	{
		particleMenuCompatibilityMap.put(8, 8);
		particleMenuCompatibilityMap.put(9, 9);
		particleMenuCompatibilityMap.put(10, 10);
		particleMenuCompatibilityMap.put(11, 11);
		particleMenuCompatibilityMap.put(12, 12);
		particleMenuCompatibilityMap.put(13, 13);
		particleMenuCompatibilityMap.put(14, 14);
	}

	public static BufferedImage getImage (String resourceName)
	{
		try
		{
			InputStream stream = core.getResource("types/" + resourceName);
			if (stream == null) {
				return null;
			}
			return ImageIO.read(stream);
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
		} catch (IOException ignored) {}
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
	
	public static InputStream getMostCompatibleParticlesMenu ()
	{
		int menuVersion = particleMenuCompatibilityMap.getOrDefault(Math.min((int) ParticleHats.serverVersion, 14), 8);
		return core.getResource("menus/particles_" + menuVersion + ".yml");
	}
}
