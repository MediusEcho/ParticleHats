package com.mediusecho.particlehats.editor.citizens;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.ui.AbstractListMenu;
import com.mediusecho.particlehats.ui.MenuManager;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;

public class CitizensMenuSelectionMenu extends AbstractListMenu {

	private Map<String, String> loadedMenus;
	private Map<Integer, String> storedMenus;
	
	private MenuAction openMenuAction;
	
	protected final MenuButton emptyHatButton = new MenuButton(ItemUtil.createItem(CompatibleMaterial.BARRIER, Message.EDITOR_MISC_EMPTY_MENU), (event, slot) ->
	{
		owner.playSound(owner.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
		return MenuClickResult.NONE;
	});
	
	public CitizensMenuSelectionMenu(ParticleHats core, MenuManager menuManager, Player owner, MenuObjectCallback callback) 
	{
		super(core, menuManager, owner, false);
		
		this.loadedMenus = core.getDatabase().getMenus(false);
		this.storedMenus = new HashMap<Integer, String>();
		this.totalPages = MathUtil.calculatePageCount((double) loadedMenus.size(), 28);
		
		openMenuAction = (event, slot) ->
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
	public void insertEmptyItem () {}
	
	@Override
	public void removeEmptyItem () {}

	@Override
	protected void build() 
	{
		// Main Menu
		setAction(49, (event, slot) ->
		{
			menuManager.closeCurrentMenu();
			return MenuClickResult.NEUTRAL;
		});
		
		// Previous Page
		setAction(48, (event, slot) ->
		{
			currentPage--;
			open();
			return MenuClickResult.NEUTRAL;
		});
		
		// Previous Page
		setAction(50, (event, slot) ->
		{
			currentPage++;
			open();
			return MenuClickResult.NEUTRAL;
		});
		
		final String title = Message.EDITOR_MENU_SELECTION_TITLE.getValue();
		final String pages = Integer.toString(totalPages);
		final ItemStack backButtonItem = ItemUtil.createItem(Material.NETHER_STAR, Message.EDITOR_MISC_MAIN_MENU);
		
		for (int i = 0; i < totalPages; i++)
		{
			String index = Integer.toString(i + 1);
			String menuTitle = title.replace("{1}", index).replace("{2}", pages);
			
			Inventory inventory = Bukkit.createInventory(null, 54, menuTitle);
			
			// Next Page
			if ((i + 1) < totalPages) {
				inventory.setItem(50, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
			}
			
			// Previous Page
			if ((i + 1) > 1) {
				inventory.setItem(48, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_PREVIOUS_PAGE));
			}
			
			inventory.setItem(49, backButtonItem);
			
			setMenu(i, inventory);
		}
		
		if (loadedMenus.size() == 0) 
		{
			setAction(22, emptyHatButton.getAction());
			setItem(0, 22, emptyHatButton.getItem());
			return;
		}
		
		for (int i = 0; i < 28; i++) {
			setAction(getNormalIndex(i, 10, 2), openMenuAction);
		}
		
		int index = 0;
		int globalIndex = 0;
		int page = 0;
		
		for (Entry<String, String> menu : loadedMenus.entrySet())
		{
			String key = menu.getKey();
			String value = menu.getValue();
			
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

	@Override
	public void onClose(boolean forced) {}

	@Override
	public void onTick(int ticks) {}

}
