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
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.ui.AbstractListMenu;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;

public class EditorMenuSelectionMenu extends AbstractListMenu {

	private final String title = Message.EDITOR_MENU_SELECTION_TITLE.getValue();
	
	private final EditorMenuManager editorManager;
	private final boolean isTransferringHat;
	private final MenuAction selectAction;
	
	private Map<String, String> loadedMenus;
	private Map<Integer, String> storedMenus;
	
	private boolean isAddingMenu = false;
	
	public EditorMenuSelectionMenu(ParticleHats core, EditorMenuManager menuManager, Player owner, boolean transferringHat, MenuObjectCallback callback) 
	{
		super(core, menuManager, owner, false);
		
		this.editorManager = menuManager;
		this.isTransferringHat = transferringHat;
		this.loadedMenus = new HashMap<String, String>();
		this.storedMenus = new HashMap<Integer, String>();
		this.totalPages = MathUtil.calculatePageCount(core.getDatabase().getMenus(false).size(), 28);
		
		this.selectAction = (event, slot) ->
		{
			int index = getClampedIndex(slot, 10, 2) + (28 * currentPage);
			if (storedMenus.containsKey(index))
			{
				callback.onSelect(storedMenus.get(index));
				return MenuClickResult.NEUTRAL;
			}
			return MenuClickResult.NONE;
		};
		
		build();
	}

	@Override
	public void insertEmptyItem() {}

	@Override
	public void removeEmptyItem() {}

	@Override
	protected void build()
	{
		setAction(49, backButtonAction);
		for (int i = 0; i <= 27; i++) {
			setAction(getNormalIndex(i, 10, 2), selectAction);
		}
		
		setAction(48, (event, slot) ->
		{
			currentPage--;
			open();
			return MenuClickResult.NEUTRAL;
		});
		
		setAction(50, (event, slot) ->
		{
			currentPage++;
			open();
			return MenuClickResult.NEUTRAL;
		});
		
		setAction(52, (event, slot) ->
		{
			editorManager.getOwnerState().setMetaState(MetaState.NEW_MENU);
			core.prompt(owner, MetaState.HAT_NAME);
			owner.closeInventory();
			
			isAddingMenu = true;
			return MenuClickResult.NEUTRAL;
		});
		
		// Create our menus
		for (int i = 0; i < totalPages; i++) {			
			menus.put(i, createMenu(i));
		}
		
		loadMenus();
	}
	
	@Override
	public void open ()
	{
		if (isAddingMenu) {
			rebuild();
		}
		
		super.open();
	}

	@Override
	public void onClose(boolean forced) {}

	@Override
	public void onTick(int ticks) {}

	public void rebuild ()
	{
		int pages = MathUtil.calculatePageCount(core.getDatabase().getMenus(false).size(), 28);
		if (pages > totalPages)
		{
			totalPages = pages;
			setMenu(pages - 1, createMenu(pages - 1));
			
			Inventory menu = menus.get(pages - 2);
			if (menu == null) {
				return;
			}
			
			menu.setItem(50, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
		}
		
		loadMenus();
		isAddingMenu = false;
	}
	
	private Inventory createMenu (int index)
	{
		String menuTitle = title
				.replace("{1}", Integer.toString(index + 1)).replace("{2}", Integer.toString(totalPages));
		
		Inventory menu = Bukkit.createInventory(null, 54, menuTitle);
		menu.setItem(49, backButtonItem);
		
		// Next Page
		if ((index + 1) < totalPages) {
			menu.setItem(50, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
		}
		
		// Previous Page
		if ((index + 1) > 1) {
			menu.setItem(48, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_PREVIOUS_PAGE));
		}
		
		// Create Menu
		if (!isTransferringHat && owner.hasPermission(Permission.COMMAND_CREATE.getPermission())) {
			menu.setItem(52, ItemUtil.createItem(CompatibleMaterial.TURTLE_HELMET, Message.EDITOR_MENU_SELECTION_CREATE));
		}
		
		return menu;
	}
	
	private void loadMenus ()
	{
		loadedMenus = core.getDatabase().getMenus(false);
		if (loadedMenus.isEmpty()) {
			return;
		}
		
		int startingIndex = storedMenus.size();
		int globalIndex = startingIndex;
		int page = (startingIndex / 28);
		int index = MathUtil.wrap(startingIndex, 28, 0);
		String currentMenu = editorManager.getMenuName();
		
		for (Entry<String, String> entry : loadedMenus.entrySet())
		{
			String key = entry.getKey();
			String value = entry.getValue();
			
			// Skip the menu we're editing
			if (key.equals(currentMenu)) {
				continue;
			}
			
			// Skip over any menus already loaded
			if (storedMenus.containsValue(key)) {
				continue;
			}
			
			String name = Message.EDITOR_MENU_SELECTION_MENU_PREFIX.getValue() + key;
			
			ItemStack item = ItemUtil.createItem(Material.BOOK, name);
			ItemUtil.setItemDescription(item, Message.EDITOR_MENU_SELECTION_MENU_DESCRIPTION.getValue().replace("{1}", value));
			
			setItem(page, getNormalIndex(index++, 10, 2), item);
			storedMenus.put(globalIndex++, key);
			
			if (index % 28 == 0)
			{
				index = 0;
				page++;
			}
		}
	}
	
}
