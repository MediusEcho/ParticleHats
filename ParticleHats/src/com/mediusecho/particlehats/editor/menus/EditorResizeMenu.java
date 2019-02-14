package com.mediusecho.particlehats.editor.menus;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorResizeMenu extends EditorMenu {

	public EditorResizeMenu(Core core, Player owner, MenuBuilder menuBuilder) 
	{
		super(core, owner, menuBuilder);
		
		inventory = Bukkit.createInventory(null, 27, Message.EDITOR_RESIZE_MENU_TITLE.getValue());
		buildMenu();
	}

	@Override
	protected void buildMenu() 
	{
		final EditorAction setRowAction = (event, slot) ->
		{
			int size = (slot - 10) + (slot < 13 ? 1 : 0);
			menuBuilder.getEditingMenu().resize(size);
			menuBuilder.goBack();
			return EditorClickType.NEUTRAL;
		};
		
		String title = Message.EDITOR_RESIZE_MENU_SET_ROW_SIZE.getValue();
		List<String> description = StringUtil.parseDescription(Message.EDITOR_RESIZE_MENU_SET_ROW_DESCRIPTION.getValue());
		String suffixInfo[] = StringUtil.parseValue(title, "2");
		
		setButton(13, backButton, backAction);
		for (int i = 0; i < 7; i++)
		{
			if (i == 3) {
				continue;
			}
			
			String t = title.replace("{1}", Integer.toString((i + 1) - (i > 3 ? 1 : 0)))
				.replace(suffixInfo[0], i == 0 ? "" : suffixInfo[1]);
			
			ItemStack row = ItemUtil.createItem(Material.GRAY_DYE, t, description);
			setButton(i + 10, row, setRowAction);
		}
		
		int currentRows = menuBuilder.getEditingMenu().getRowCount();
		ItemStack row = getItem(currentRows + 10 - (currentRows < 3 ? 1 : 0));

		row.setType(Material.LIME_DYE);
		ItemUtil.highlightItem(row);
	}

}
