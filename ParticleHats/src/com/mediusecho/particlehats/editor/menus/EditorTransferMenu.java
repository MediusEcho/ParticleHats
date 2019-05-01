package com.mediusecho.particlehats.editor.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorTransferMenu extends EditorMenu {

	private final MenuInventory menuInventory;
	private final ItemStack emptyItem = ItemUtil.createItem(CompatibleMaterial.LIGHT_GRAY_STAINED_GLASS_PANE, Message.EDITOR_MOVE_MENU_MOVE, Message.EDITOR_MOVE_MENU_MOVE_DESCRIPTION);
	
	private final String menuName;
	
	public EditorTransferMenu(Core core, Player owner, MenuBuilder menuBuilder, String menuName) 
	{
		super(core, owner, menuBuilder);
		this.menuName = menuName;
		
		menuInventory = core.getDatabase().loadInventory(menuName, core.getPlayerState(ownerID));
		inventory = Bukkit.createInventory(null, menuInventory.getSize(), EditorLore.getTrimmedMenuTitle(menuInventory.getTitle(), Message.EDITOR_MOVE_MENU_TITLE));
		
		build();
	}

	@Override
	protected void build() 
	{
		final EditorAction moveAction = (event, slot) ->
		{
			if (event.isRightClick()) 
			{
				menuBuilder.goBack();
				return EditorClickType.NEUTRAL;
			}
			
			int currentSlot = menuBuilder.getBaseHat().getSlot();
			
			core.getDatabase().moveHat(null, menuBuilder.getBaseHat(), menuBuilder.getMenuName(), menuName, currentSlot, slot, false);
			menuBuilder.getEditingMenu().removeButton(currentSlot);
			menuBuilder.openEditingMenu();
			
			return EditorClickType.NEUTRAL;
		};
		
		final EditorAction cancelAction = (event, slot) ->
		{
			menuBuilder.goBack();
			return EditorClickType.NEUTRAL;
		};
		
		for (int i = 0; i < menuInventory.getSize(); i++)
		{
			ItemStack item = menuInventory.getItem(i);
			
			if (item == null) {
				setButton(i, emptyItem, moveAction);
			}
			
			else
			{
				ItemUtil.setNameAndDescription(item, Message.EDITOR_MOVE_MENU_OCCUPIED.getValue(), StringUtil.parseDescription(Message.EDITOR_MOVE_MENU_OCCUPIED_DESCRIPTION.getValue()));
				setButton(i, item, cancelAction);
			}
		}
	}

}
