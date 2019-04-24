package com.mediusecho.particlehats.ui;

import java.util.UUID;

import org.bukkit.event.inventory.InventoryClickEvent;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.player.PlayerState;

public enum GuiState {

	NONE,
	INNACTIVE,
	ACTIVE,
	EDITOR,
	SWITCHING_MENU,
	SWITCHING_EDITOR;
	
	public void onClick (InventoryClickEvent event, PlayerState playerState)
	{
		switch (this)
		{
			case ACTIVE:
			{
				if (playerState.hasMenuOpen())
				{
					event.setCancelled(true);
					playerState.getOpenMenu().onClick(event);
				}
				break;
			}
			
			case EDITOR:
			{
				if (playerState.isEditing())
				{
					MenuBuilder menuBuilder = playerState.getMenuBuilder();
					
					event.setCancelled(true);
					boolean inMenu = event.getRawSlot() < event.getInventory().getSize();
					menuBuilder.onClick(event, inMenu);
				}
				break;
			}
			
			default:
				break;
		}
	}
	
	public void onClose (PlayerState playerState)
	{
		switch (this)
		{
			case ACTIVE:
			{
				Core.debug("setting GuiState to INNACTIVE");
				playerState.setGuiState(GuiState.INNACTIVE);
				playerState.closeOpenMenu();
				playerState.clearMenuCache();
				break;
			}
			
			case EDITOR:
			{
				if (playerState.isEditing())
				{
					if (playerState.getMetaState() == MetaState.NONE)
					{
						Core.debug("settings GuiState to INNACTIVE from editor");
						playerState.getMenuBuilder().onClose();
						playerState.setMenuBuilder(null);
						playerState.setGuiState(GuiState.INNACTIVE);
					}
				}
				break;
			}
			
			default:
				break;
		}
	}
	
	public void onTick (PlayerState playerState, int ticks)
	{
		switch (this)
		{
			case ACTIVE:
			{
				UUID id = playerState.getOwnerID();
				if (playerState.hasMenuOpen())
				{
					Menu menu = playerState.getOpenMenu();
					
					if (menu.getOwnerID().equals(id)) {
						menu.onTick(ticks);
					}
				}
				break;
			}
		
			case EDITOR:
			{
				MenuBuilder menuBuilder = playerState.getMenuBuilder();
				if (menuBuilder != null) {
					menuBuilder.onTick(ticks);
				}
			}
			break;
		
			default: 
				break;
		}
	}
}
