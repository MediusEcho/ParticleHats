package com.mediusecho.particlehats.ui;

import org.bukkit.entity.Player;

import com.mediusecho.particlehats.Core;

public class StaticMenu extends Menu {

	public StaticMenu(Core core, Player owner) 
	{
		super(core, owner);
	}
	
	public StaticMenu(Core core, Player owner, MenuInventory inventory)
	{
		super(core, owner, inventory);
	}

	@Override
	public void onTick() 
	{
		
	}

}
