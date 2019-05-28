package com.mediusecho.particlehats.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class MathUtil {

	/**
	 * Clamps a value keeping it within min and max
	 * @param value Current value
	 * @param min Minimum value that can be returned
	 * @param max Maximum value that can be returned
	 * @return
	 */
	public static double clamp (double value, double min, double max) {
		return round(Math.min(Math.max(value, min), max), 2);
	}
	
	/**
	 * Clamps a value keeping it within min and max
	 * @param value Current value
	 * @param min Minimum value that can be returned
	 * @param max Maximum value that can be returned
	 * @return
	 */
	public static int clamp (int value, int min, int max) {
		return Math.min(Math.max(value, min), max);
	}
	
	/**
	 * Wraps a value between the offset and length repeating<br>
	 * eg: (offset = 4, length = 10): value(3) = 9, value(4) = 0, (value(5) = 1
	 * @param value Value to wrap
	 * @param length How many increments before the value is wrapped
	 * @param offset Where to start wrapping
	 * @return
	 */
	public static int wrap (int value, int length, int offset) {
		return (((value - offset) % length) + length) % length;
	}
	
	/**
	 * Wraps a value between the offset and length repeating<br>
	 * eg: (offset = 4, length = 10): value(3) = 9, value(4) = 0, (value(5) = 1
	 * @param value Value to wrap
	 * @param length How many increments before the value is wrapped
	 * @param offset Where to start wrapping
	 * @return
	 */
	public static double wrap (double value, double length, double offset) 
	{
		double v = round((((value - offset) % length) + length) % length, 2);
		return v < length ? v : offset;
	}
	
	public static double wrapAngle (double value)
	{
		double min = -180;
		double max = 180;
		double v = value;
		
		while (v <= min) {
			v += 360D;
		}
		
		while (v > max) {
			v -= 360D;
		}
		
		return v;
	}
	
	/**
	 * Returns the Integer value of this String, or 0 if an Integer cannot be found
	 * @param s
	 * @return
	 */
	public static int valueOf (String s)
	{
		try {
			return Integer.valueOf(s);
		} catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * Rounds a double value to x places
	 * @param value Value to round
	 * @param places How many digits after the decimal
	 * @return
	 */
	public static double round (double value, int places)
	{
		if (places < 0) {
			return value;
		}
		BigDecimal bd = new BigDecimal(value);
		return bd.setScale(places, RoundingMode.HALF_UP).doubleValue();
	}
	
	/**
	 * Returns a vector rotated to the locations pitch/yaw
	 * @param v
	 * @param location
	 * @return
	 */
	public static Vector rotateVector (Vector v , Location location)
	{
		double yaw = location.getYaw() / 180.0 * Math.PI;
		double pitch = location.getPitch() / 180.0 * Math.PI;
		
		v = rotateXAxis(v, pitch);
		v = rotateYAxis(v, -yaw);
		return v;
	}
	
	/**
	 * Rotates a vector on its x axis
	 * @param v
	 * @param a
	 * @return
	 */
	public static Vector rotateXAxis (Vector v, double a) 
	{
		double y = Math.cos(a) * v.getY() - Math.sin(a) * v.getZ();
		double z = Math.sin(a) * v.getY() + Math.cos(a) * v.getZ();
		return v.setY(y).setZ(z);
	}
	
	/**
	 * Rotates a vector on its y axis
	 * @param v
	 * @param b
	 * @return
	 */
	public static Vector rotateYAxis (Vector v, double b) 
	{
		double x = Math.cos(b) * v.getX() + Math.sin(b) * v.getZ();
		double z = -Math.sin(b) * v.getX() + Math.cos(b) * v.getZ();
		return v.setX(x).setZ(z);
	}
	
	/**
	 * Rotates a vector on its z axis
	 * @param v
	 * @param c
	 * @return
	 */
	public static Vector rotateZAxis (Vector v, double c) 
	{
		double x = Math.cos(c) * v.getX() - Math.sin(c) * v.getY();
		double y = Math.sin(c) * v.getX() + Math.cos(c) * v.getY();
		return v.setX(x).setY(y);
	}
}
