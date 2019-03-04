package com.mediusecho.particlehats.editor.menus;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.ui.MenuState;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorMenuSelectionMenu extends EditorMenu {

	private final String title = Message.EDITOR_MENU_SELECTION_TITLE.getValue();
	
	private final Map<Integer, Inventory> menus;
	
	private final EditorAction selectAction;
	
	private Map<String, String> loadedMenus;
	private Map<Integer, String> storedMenus;
	private int pages;
	private int currentPage = 0;
	
	public EditorMenuSelectionMenu(Core core, Player owner, MenuBuilder menuBuilder, EditorStringCallback callback) 
	{
		super(core, owner, menuBuilder);
		
		menus = new HashMap<Integer, Inventory>();
		loadedMenus = core.getDatabase().getMenus(false);
		storedMenus = new HashMap<Integer, String>();
		pages = (int) Math.ceil((double) (loadedMenus.size() - 1) / 28D);
		
		selectAction = (event, slot) ->
		{
			int menuSlot = getClampedIndex(slot, 10, 2) + (28 * currentPage);			
			if (storedMenus.containsKey(menuSlot))
			{				
				callback.onSelect(storedMenus.get(menuSlot));
				menuBuilder.goBack();
				return EditorClickType.NEUTRAL;
			}
			return EditorClickType.NONE;
		};
		
		build();
	}
	
	@Override
	public void open ()
	{
		if (menus.containsKey(currentPage))
		{
			menuBuilder.setOwnerState(MenuState.SWITCHING);
			owner.openInventory(menus.get(currentPage));
		}
	}

	@Override
	protected void build() 
	{
		setAction(49, backAction);
		for (int i = 0; i <= 27; i++) {
			setAction(getNormalIndex(i, 10, 2), selectAction);
		}
		
		setAction(48, (event, slot) ->
		{
			currentPage--;
			open();
			return EditorClickType.NEUTRAL;
		});
		
		setAction(50, (event, slot) ->
		{
			currentPage++;
			open();
			return EditorClickType.NEUTRAL;
		});
		
		// Create our menus
		for (int i = 0; i < pages; i++)
		{
			String menuTitle = title
					.replace("{1}", Integer.toString(i + 1)).replace("{2}", Integer.toString(pages));
			
			Inventory menu = Bukkit.createInventory(null, 54, menuTitle);
			menu.setItem(49, backButton);
			
			// Next Page
			if ((i + 1) < pages) {
				menu.setItem(50, ItemUtil.createItem(Material.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
			}
			
			// Previous Page
			if ((i + 1) > 1) {
				menu.setItem(48, ItemUtil.createItem(Material.LIME_DYE, Message.EDITOR_MISC_PREVIOUS_PAGE));
			}
			
			menus.put(i, menu);
		}
		
		int index = 0;
		int globalIndex = 0;
		int page = 0;
		String currentMenu = menuBuilder.getEditingMenu().getName();
		
		for (Entry<String, String> menu : loadedMenus.entrySet())
		{
			if (menu.getKey().equals(currentMenu)) {			
				continue;
			}
			
			String name = Message.EDITOR_MENU_SELECTION_MENU_PREFIX.getValue() + menu.getKey();
			String title = menu.getValue();
			
			ItemStack item = ItemUtil.createItem(Material.BOOK, name);
			ItemUtil.setItemDescription(item, Message.EDITOR_MENU_SELECTION_MENU_DESCRIPTION.getValue().replace("{1}", title));
			
			menus.get(page).setItem(getNormalIndex(index++, 10, 2), item);
			storedMenus.put(globalIndex++, menu.getKey());
			
			if (index % 28 == 0)
			{
				index = 0;
				page++;
			}
		}
	}

}
