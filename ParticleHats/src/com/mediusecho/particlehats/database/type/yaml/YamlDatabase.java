package com.mediusecho.particlehats.database.type.yaml;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.ui.MenuInventory;

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
	public List<String> getMenus(boolean forceUpdate) 
	{
		return null;
	}

	@Override
	public void createEmptyMenu(String menuName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createHat(String menuName, int slot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteHat(String menuName, int slot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadHatData(String menuName, int slot, Hat hat) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean menuExists(String menuName) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Map<String, BufferedImage> getImages (boolean forceUpdate)
	{
		return null;
	}

	@Override
	public void changeSlot(String menuName, int previousSlot, int newSlot, boolean swapping) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteMenu(String menuName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveMenuTitle(String menuName, String title) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveMenuSize(String menuName, int rows) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean labelExists(String menuName, String label) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void saveMetaData(String menuName, Hat hat, DataType type) {
		// TODO Auto-generated method stub
		
	}

}
