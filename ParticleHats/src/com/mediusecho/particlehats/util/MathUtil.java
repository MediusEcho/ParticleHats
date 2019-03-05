package com.mediusecho.particlehats.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil {

	/**
	 * Clamps a value keeping it within min and max
	 * @param value Current value
	 * @param min Minimum value that can be returned
	 * @param max Maximum value that can be returned
	 * @return
	 */
	public static double clamp (double value, double min, double max) {
		return Math.min(Math.max(value, min), max);
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
}
