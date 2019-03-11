package com.mediusecho.particlehats.database.type.yaml;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.ui.MenuInventory;

// TODO: Implement yml database
public class YamlDatabase implements Database {

	@Override
	public void onDisable() 
	{
		
	}
	
	@Override
	public MenuInventory loadInventory (String menuName) 
	{
		return null;
	}

	@Override
	public Map<String, String> getMenus(boolean forceUpdate) 
	{
		return null;
	}

	@Override
	public void createEmptyMenu(String menuName) {
		
	}

	@Override
	public void createHat(String menuName, int slot) {
		
	}

	@Override
	public void deleteHat(String menuName, int slot) {
		
	}

	@Override
	public void loadHatData(String menuName, int slot, Hat hat) {
		
	}

	@Override
	public boolean menuExists(String menuName) {
		return false;
	}
	
	@Override
	public Map<String, BufferedImage> getImages (boolean forceUpdate)
	{
		return null;
	}

	@Override
	public void changeSlot(String menuName, int previousSlot, int newSlot, boolean swapping) {
		
	}

	@Override
	public void deleteMenu(String menuName) {
		
	}

	@Override
	public void saveMenuTitle(String menuName, String title) {
		
	}

	@Override
	public void saveMenuSize(String menuName, int rows) {
		
	}

	@Override
	public boolean labelExists(String menuName, String label) {
		return false;
	}

	@Override
	public void saveMetaData(String menuName, Hat hat, DataType type, int index) {
		
	}

	@Override
	public void saveParticleData(String menuName, Hat hat, int index) {
		
	}

	@Override
	public void cloneHatData(String menuName, int currentSlot, int newSlot) {
		
	}

	@Override
	public void moveHatData(String fromMenu, String toMenu, int fromSlot, int toSlot) {
		
	}

}
