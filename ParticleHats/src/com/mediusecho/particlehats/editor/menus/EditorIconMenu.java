package com.mediusecho.particlehats.editor.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorIconMenu extends EditorMenu {

	private final EditorItemCallback itemCallback;
	private final Message description;
	
	public EditorIconMenu(Core core, Player owner, MenuBuilder menuBuilder, Message title, Message description, EditorItemCallback itemCallback) 
	{
		super(core, owner, menuBuilder);
		this.description = description;
		this.itemCallback = itemCallback;
		
		inventory = Bukkit.createInventory(null, 27, title.getValue());
		build();
	}
	
	/**
	 * Called any time we click outside of this menu
	 */
	@Override
	public EditorClickType onClickOutside (InventoryClickEvent event, final int slot)
	{
		itemCallback.onSelect(event.getCurrentItem());	
		menuBuilder.goBack();
		return EditorClickType.NEUTRAL;
	}

	@Override
	protected void build() 
	{
		ItemStack info = ItemUtil.createItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, Message.EDITOR_ICON_MENU_INFO_TITLE);
		ItemUtil.setItemDescription(info, StringUtil.parseDescription(description.getValue()));
		
		for (int i = 0; i < inventory.getSize(); i++)
		{
			if (i == 13) {
				continue;
			}
			setItem(i, info);
		}
		
		setButton(13, backButton, backAction);
	}

}
