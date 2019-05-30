package com.mediusecho.particlehats.database.properties;

public class Group {

	private final String name;
	private String defaultMenu;
	private int weight;
	
	public Group (final String name, String defaultMenu, int weight)
	{
		this.name = name;
		this.defaultMenu = defaultMenu;
		this.weight = weight;
	}
	
	/**
	 * Get this groups name
	 * @return
	 */
	public String getName () {
		return name;
	}
	
	/**
	 * Get the name of the menu this group will open
	 * @return
	 */
	public String getDefaultMenu () {
		return defaultMenu;
	}
	
	/**
	 * Set the menu this group will open
	 * @param defaultMenu
	 */
	public void setDefaultMenu (String defaultMenu) {
		this.defaultMenu = defaultMenu;
	}
	
	/**
	 * Get this groups importance
	 * @return
	 */
	public int getWeight () {
		return weight;
	}
	
	/**
	 * Set this groups importance
	 * @param weight
	 */
	public void setWeight (int weight) {
		this.weight = weight;
	}
}
