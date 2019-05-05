package com.mediusecho.particlehats.editor.menus;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.ui.GuiState;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorMenuSelectionMenu extends EditorMenu {

	private final String title = Message.EDITOR_MENU_SELECTION_TITLE.getValue();
	
	private final Map<Integer, Inventory> menus;
	private final EditorAction selectAction;
	private final boolean transfering;
	
	private Map<String, String> loadedMenus;
	private Map<Integer, String> storedMenus;
	private int pages;
	private int currentPage = 0;
	private boolean addedMenu = false;
		
	public EditorMenuSelectionMenu(ParticleHats core, Player owner, MenuBuilder menuBuilder, boolean transfering, EditorStringCallback callback) 
	{
		super(core, owner, menuBuilder);
		this.transfering = transfering;
		
		menus = new HashMap<Integer, Inventory>();
		loadedMenus = core.getDatabase().getMenus(false);
		storedMenus = new HashMap<Integer, String>();
		pages = (int) Math.max(Math.ceil((double) (loadedMenus.size() - 1) / 28D), 1);
		
		selectAction = (event, slot) ->
		{
			int menuSlot = getClampedIndex(slot, 10, 2) + (28 * currentPage);			
			if (storedMenus.containsKey(menuSlot))
			{				
				callback.onSelect(storedMenus.get(menuSlot));
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
			if (addedMenu) {
				rebuild();
			}
			
			menuBuilder.setOwnerState(GuiState.SWITCHING_EDITOR);
			//menuBuilder.setOwnerState(MenuState.SWITCHING);
			owner.openInventory(menus.get(currentPage));
		}
	}
	
	private void rebuild ()
	{
		loadedMenus = core.getDatabase().getMenus(false);
		
		int pages = (int) Math.max(Math.ceil((double) (loadedMenus.size() - 1) / 28D), 1);
		ParticleHats.debug(pages);
		if (pages > this.pages)
		{
			this.pages = pages;
			menus.put(pages-1, createMenu(pages-1));
			
			Inventory menu = menus.get(pages-2);
			if (menu != null) {
				menu.setItem(50, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
			}
		}
		
		addedMenu = false;
		// TODO: Rebuild menu selection menu
	}
	
	private Inventory createMenu (int index)
	{
		String menuTitle = title
				.replace("{1}", Integer.toString(index + 1)).replace("{2}", Integer.toString(pages));
		
		Inventory menu = Bukkit.createInventory(null, 54, menuTitle);
		menu.setItem(49, backButton);
		
		// Next Page
		if ((index + 1) < pages) {
			menu.setItem(50, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
		}
		
		// Previous Page
		if ((index + 1) > 1) {
			menu.setItem(48, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_PREVIOUS_PAGE));
		}
		
		// Create Menu
		if (!transfering && owner.hasPermission(Permission.COMMAND_CREATE.getPermission())) {
			menu.setItem(52, ItemUtil.createItem(CompatibleMaterial.TURTLE_HELMET, Message.EDITOR_MENU_SELECTION_CREATE));
		}
		
		return menu;
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
		
		setAction(52, (event, slot) ->
		{
			menuBuilder.setOwnerState(MetaState.NEW_MENU);
			core.prompt(owner, MetaState.HAT_NAME);
			owner.closeInventory();
			
			addedMenu = true;
			return EditorClickType.NEUTRAL;
		});
		
		// Create our menus
		for (int i = 0; i < pages; i++) {			
			menus.put(i, createMenu(i));
		}
		
		int index = 0;
		int globalIndex = 0;
		int page = 0;
		String currentMenu = menuBuilder.getEditingMenu().getName();
		
		if (loadedMenus.size() > 0)
		{
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

}
