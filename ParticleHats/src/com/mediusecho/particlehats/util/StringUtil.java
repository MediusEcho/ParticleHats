package com.mediusecho.particlehats.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;

public class StringUtil {

	//private final static char[] chars = new char[] {'~','`','!','@','#','$','%','^','&','*','(',')','_','-','=','+','[',']','{','}',';','\'','"',':',',','<','>','.','/','?','|','\\'};
	private final static String[] chars = {"\\","^","$","{","}","[","]","(",")",".","*","+","?","|","<",">","-","&","%"};
	private final static TreeMap<Integer, String> romanNumerals = new TreeMap<Integer, String>();
	private final static String sanitizeRegex = "[\\@\\^\\$\\{\\}\\[\\]\\(\\)\\.\\,\\*\\+\\?\\|\\<\\>\\-\\&\\%\\#]";
	
	private static final String newLineCharacter = "/n";
	private static final Map<String, Pattern> patternCache = new HashMap<String, Pattern>();
	
	static
	{
		romanNumerals.put(40, "XL");
		romanNumerals.put(10, "X");
		romanNumerals.put(9, "IX");
		romanNumerals.put(5, "V");
		romanNumerals.put(4, "IV");
		romanNumerals.put(1, "I");
	}
	
	public static String getColorValue (String string) {
		return string.replaceAll(ChatColor.COLOR_CHAR + "", "&");
	}
	
	/**
	 * Returns a list of translated strings
	 * @param list
	 * @return
	 */
	public static List<String> getColorValue (List<String> list) {
		
		List<String> result = new ArrayList<String>();
		for (String s : list) {
			result.add(s.replaceAll(ChatColor.COLOR_CHAR + "", "&"));
		}
		return result;
	}
	
	/**
	 * Trims out any characters that aren't numbers
	 * @param s
	 * @return
	 */
	public static String getInteger (String s) 
	{	
		int i = 0;
		while (i < s.length() && !Character.isDigit(s.charAt(i))) i++;
		int j = i;
		while (j < s.length() && Character.isDigit(s.charAt(j))) j++;
		
		return s.substring(i, j).trim();
	}
	
	/**
	 * Returns the integer value of the given string, or a default value if the given string is not an integer
	 * @param s
	 * @param defaultValue
	 * @return
	 */
	public static int toInt (String s, int defaultValue)
	{
		try {
			return Integer.valueOf(s);
		} catch (NumberFormatException e) {}
		return defaultValue;
	}
	
	public static String stripColor (String s) {
		return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', s));
	}
	
	/**
	 * Returns a string with every special character escaped
	 * @param s
	 * @return
	 */
	public static String escapeSpecialCharacters (String s) {
		
		for (int i = 0; i < chars.length; i++)
		{
			if (s.contains(chars[i])) {
				s = s.replace(chars[i], "\\" + chars[i]);
			}
		}
		return s;
	}
	
	/**
	 * Returns a string with the fist letter capitalized
	 * @param s
	 * @return
	 */
	public static String capitalizeFirstLetter (String s)
	{
		String original = s.replaceAll("_", " ");
		String[] words = original.split(" ");
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < words.length; i++)
		{
			builder.append(Character.toUpperCase(words[i].charAt(0)));
			builder.append(words[i].substring(1));
			
			if (i < words.length - 1) {
				builder.append(' ');
			}
		}
		
		return builder.toString();
	}
	
	public static String getMaterialName (Material material) {
		return capitalizeFirstLetter(material.toString().toLowerCase());
	}
	
	/**
	 * Replaces any &'s with their respective ChatColor
	 * @param s
	 * @return
	 */
	public static String colorize (String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	/**
	 * Replaces any &'s with their respective ChatColor
	 * @param list
	 * @return
	 */
	public static List<String> colorize (List<String> list)
	{
		List<String> result = new ArrayList<String>();
		for (String s : list) {
			result.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		return result;
	}
	
	/**
	 * Compares 2 server versions
	 * @param version1
	 * @param version2
	 * @return
	 */
	public static int compareServerVersions (String version1, String version2)
	{
		String[] v1 = version1.split("\\.");
		String[] v2 = version2.split("\\.");
		int length = Math.max(v1.length, v2.length);
		
		for (int i = 0; i < length; i++)
		{
			Integer ver1 = i < v1.length ? Integer.parseInt(v1[i]) : 0;
			Integer ver2 = i < v2.length ? Integer.parseInt(v2[i]) : 0;
			int result = ver1.compareTo(ver2);
			
			if (result != 0) {
				return result;
			}
		}
		
		return 0;
	}
	
	/**
	 * Returns the Roman Numeral value of this number
	 * @param number
	 * @return
	 */
	public final static String toRomanNumeral (int number)
	{
		if (number <= 0) {
			return "";
		}
		
		int n = romanNumerals.floorKey(number);
		if (number == n) {
			return romanNumerals.get(number);
		}
		return romanNumerals.get(n) + toRomanNumeral(number - n);
	}
	
	/**
	 * Checks to see if the given String exists in this list
	 * @param string
	 * @param list
	 * @return
	 */
	public static boolean stringContainsFromList (String string, List<String> list)
	{
		for (String s : list) 
		{
			if (string.contains(s)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Splits a String at '/n'
	 * @param description
	 * @return A List of Strings with their color codes translated
	 */
	public static List<String> parseDescription (String description)
	{
		List<String> desc = new ArrayList<String>();
		String[] values = description.split(newLineCharacter);
		
		for (String s : values) {
			desc.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		
		return desc;
	}
	
	public static List<String> parseDescription (Hat hat, List<String> description)
	{
		List<String> desc = new ArrayList<String>();
		for (String line : description) {
			desc.add(ChatColor.translateAlternateColorCodes('&', parseString(line, hat)));
		}
		return desc;
	}
	
	public static String parseString (String string, Hat hat)
	{
		return string
				.replace("{TYPE}", hat.getType().getStrippedName())
				.replace("{LOCATION}", hat.getLocation().getStrippedName())
				.replace("{MODE}", hat.getMode().getStrippedName())
				.replace("{PRICE}", Integer.toString(hat.getPrice()))
				.replace("{CURRENCY}", SettingsManager.CURRENCY.getString())
						
				.replace("<locked>", "").replace("</locked>", "");
	}
	
	public static String parseRegex (String string, String regex)
	{
		String begin = "<%>".replace("%", regex);
		String end = "</%>".replace("%", regex);
		
		if (string.contains(begin) && string.contains(end))
		{
			String r = "(?<=<%>).*?(?=</%>)".replaceAll("%", regex);
			return string.replaceAll(r, "");
			
		}
		return string;
	}
	
	public static String getParseValue (String description, String regex) {		
		return parseValue(description, regex)[1];
	}
	
	/**
	 * Parses a string for any {#=}'s and returns {#=} and its contents in an array<br>
	 * <b>parseValue("Hello {1=World}", "1") -> [0]{1=World} [1]World</b>
	 * @param string
	 * @param regex
	 * @return
	 */
	public static String[] parseValue (String string, String regex)
	{
		Pattern pattern;
		if (patternCache.containsKey(regex)) {
			pattern = patternCache.get(regex);
		} 
		
		else 
		{
			pattern = Pattern.compile("\\{" + regex + "=(.*?)\\}");
			patternCache.put(regex, pattern);
		}
		
		Matcher matcher = pattern.matcher(string);
		if (matcher.find()) {
			return new String[] {matcher.group(0), matcher.group(1)};
		}
		return new String[]{"", ""};
	}
	
	/**
	 * Returns a compiled pattern from this regex
	 * @param regex
	 * @return
	 */
	public static Pattern getPattern (String regex) 
	{
		if (patternCache.containsKey(regex)) {
			return patternCache.get(regex);
		}
		
		Pattern pattern = Pattern.compile(regex);
		patternCache.put(regex, pattern);
		
		return pattern;
	}
	
	public static List<String> translateFormatting (List<String> list, String regex)
	{	
		List<String> formattedList = new ArrayList<String>();
		for (String s : list)
		{
			String formatted = s.replaceAll("(?<=<" + regex + ">).*?(?=</" + regex + ">)", "");
			if (ChatColor.stripColor(formatted).length() == 0) {
				continue;
			}
			formattedList.add(formatted);
		}
		return formattedList;
	}
	
	/**
	 * Get the time formatted as mm:ss from an int
	 * @param time
	 * @return
	 */
	public static String getTimeFormat (int time)
	{
		int remainder = time % 3600; // get the rest in seconds
		int minutes = remainder / 60; // get the amount of minutes from the rest
		int seconds = remainder % 60; // get the new rest
		String disMinu = (minutes < 10 ? "0" : "") + minutes; // get minutes and add "0" before if lower than 10
		String disSec = (seconds < 10 ? "0" : "") + seconds; // get seconds and add "0" before if lower than 10
		
		return disMinu + ":" + disSec; //get the whole time
	}
	
	public static String sanitizeString (String s) {
		return s.replaceAll(sanitizeRegex, "");
	}
}
