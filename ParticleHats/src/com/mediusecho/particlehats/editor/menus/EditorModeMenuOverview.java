package com.mediusecho.particlehats.editor.menus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.ParticleModes;
import com.mediusecho.particlehats.ui.menus.ListMenuImpl;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.ui.properties.MenuContentRegion;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;

public class EditorModeMenuOverview extends ListMenuImpl {

	private final Hat targetHat;
	private final EditorMenuManager editorManager;
	private final MenuCallback callback;
	
	private final ItemStack emptyWhitelistItem = ItemUtil.createItem(CompatibleMaterial.BARRIER, Message.EDITOR_MODE_OVERVIEW_MENU_WHITELIST_EMPTY);
	private final ItemStack emptyBlacklistItem = ItemUtil.createItem(CompatibleMaterial.BARRIER, Message.EDITOR_MODE_OVERVIEW_MENU_BLACKLIST_EMPTY);
	
	private Map<Integer, Inventory> whitelistMenus;
	private Map<Integer, Inventory> blacklistMenus;
	
	private int currentWhitelistPage = 0;
	private int currentBlacklistPage = 0;
	
	private boolean isEditingWhitelist = true;
	private boolean whitelistModified = false;
	private boolean blacklistModified = false;
	
	public EditorModeMenuOverview(ParticleHats core, EditorMenuManager editorManager, Player owner, MenuCallback callback) 
	{
		super(core, editorManager, owner, new MenuContentRegion(10, 43));
		
		this.editorManager = editorManager;
		this.targetHat = editorManager.getTargetHat();
		this.callback = callback;
		this.setInventory(0, Bukkit.createInventory(null, 54, Message.EDITOR_MODE_OVERVIEW_MENU_TITLE.getValue()));
		this.whitelistMenus = new HashMap<Integer, Inventory>();
		this.blacklistMenus = new HashMap<Integer, Inventory>();
		
		build();
	}
	
	@Override
	public void onClose (boolean forced)
	{
		if (whitelistModified || blacklistModified)
		{
			String menuName = editorManager.getMenuName();
			Database database = core.getDatabase();
			
			if (whitelistModified) {
				database.saveMetaData(menuName, targetHat, DataType.MODE_WHITELIST, 0);
			}
			
			if (blacklistModified) {
				database.saveMetaData(menuName, targetHat, DataType.MODE_BLACKLIST, 0);
			}
		}
		
		if (!forced) {
			callback.onCallback();
		}
	}
	
	@Override
	public void open () {
		open(getCurrentMenu(-1));
	}
	
	private void open (Inventory inventory)
	{
		if (inventory == null) {
			return;
		}
		
		editorManager.isOpeningMenu(this);
		owner.openInventory(inventory);
	}
	
	@Override
	public boolean hasInventory(Inventory inventory) 
	{
		if (whitelistMenus.containsValue(inventory)) {
			return true;
		}
		return blacklistMenus.containsValue(inventory);
	}

	@Override
	public void insertEmptyItem() {
		getInventory(0).setItem(22, isEditingWhitelist ? emptyWhitelistItem : emptyBlacklistItem);
	}

	@Override
	public void removeEmptyItem() {
		getInventory(0).setItem(22, null);
		
	}
	
	@Override
	public void setEmpty(boolean empty) 
	{		
		if (empty) {
			insertEmptyItem();
		} else {
			removeEmptyItem();
		}
	}
	
	@Override
	public int getTotalPages () {
		return isEditingWhitelist ? whitelistMenus.size() : blacklistMenus.size();
	}
	
	@Override
	public void setItem (int page, int slot, ItemStack item) {
		getInventory(page).setItem(slot, item);
	}
	
	@Override
	public Inventory getInventory (int page) {
		return isEditingWhitelist ? whitelistMenus.get(page) : blacklistMenus.get(page);
	}
	
	@Override
	public void deleteItem (int page, int slot)
	{
		super.deleteItem(page, slot);
		
		List<ParticleModes> modes = isEditingWhitelist ? targetHat.getWhitelistedModes() : targetHat.getBlacklistedModes();
		int clampedSlot = contentRegion.getClampedIndex(slot) + (page * contentRegion.getTotalSlots());
		
		modes.remove(clampedSlot);
		if (modes.isEmpty()) {
			setEmpty(true);
		}
		
		if (isEditingWhitelist) {
			whitelistModified = true;
		} else {
			blacklistModified = true;
		}
	}

	@Override
	public void build() 
	{
		setAction(49, backButtonAction);
		
		setAction(45, (event, slot) ->
		{
			open(whitelistMenus.get(currentWhitelistPage));
			isEditingWhitelist = true;
			return MenuClickResult.NEUTRAL;
		});
		
		setAction(46, (event, slot) ->
		{
			open(blacklistMenus.get(currentBlacklistPage));
			isEditingWhitelist = false;
			return MenuClickResult.NEUTRAL;
		});
		
		setAction(48, (event, slot) ->
		{
			if (isEditingWhitelist) {
				open(whitelistMenus.get(--currentWhitelistPage));
			} else {
				open(blacklistMenus.get(--currentBlacklistPage));
			}
			return MenuClickResult.NEUTRAL;
		});
		
		setAction(50, (event, slot) ->
		{
			if (isEditingWhitelist) {
				open(whitelistMenus.get(++currentWhitelistPage));
			} else {
				open(blacklistMenus.get(++currentBlacklistPage));
			}
			return MenuClickResult.NEUTRAL;
		});
		
		final MenuAction editAction = (event, slot) ->
		{
			if (event.isShiftRightClick()) 
			{
				int page = isEditingWhitelist ? currentWhitelistPage : currentBlacklistPage;
				this.deleteItem(page, slot);
				
				return MenuClickResult.NEGATIVE;
			}
			return MenuClickResult.NONE;
		};
		
		for (int i = 0; i < contentRegion.getTotalSlots(); i++) {
			setAction(contentRegion.getNormalIndex(i), editAction);
		}
		
		setAction(53, (event, slot) ->
		{
			EditorModeMenu editorModeMenu = new EditorModeMenu(core, editorManager, owner, isEditingWhitelist, (mode) -> 
			{
				if (mode == null) {
					return;
				}
				
				ParticleModes m = (ParticleModes)mode;
				
				if (isEditingWhitelist) 
				{
					addWhitelistedMode(m);
					targetHat.addWhitelistedMode(m);
				}
				
				else 
				{
					addBlacklistedMode(m);
					targetHat.addBlacklistedMode(m);
				}
				
				menuManager.closeCurrentMenu();
			});
			menuManager.addMenu(editorModeMenu);
			editorModeMenu.open();
			return MenuClickResult.NEUTRAL;
		});
		
		generateWhitelistMenus();
		generateBlacklistMenus();
	}
	
	private Inventory getCurrentMenu (int page)
	{
		if (isEditingWhitelist) {
			return whitelistMenus.get(page == -1 ? currentWhitelistPage : page);
		}
		return blacklistMenus.get(page == -1 ? currentBlacklistPage : page);
	}
	
	private void generateWhitelistMenus ()
	{
		generateMenus(targetHat.getWhitelistedModes(), whitelistMenus, Message.EDITOR_MODE_OVERVIEW_MENU_WHITELIST_TITLE.getValue(), true);
		
		int index = 0;
		int page = 0;
		
		List<ParticleModes> modes = targetHat.getWhitelistedModes();
		if (modes.isEmpty())
		{
			whitelistMenus.get(0).setItem(22, emptyWhitelistItem);
			return;
		}
		
		for (ParticleModes mode : modes)
		{
			page = contentRegion.getPage(index);
			
			ItemStack item = ItemUtil.createItem(mode.getMode().getMenuItem(), mode.getDisplayName());
			EditorLore.updateModeItemDescription(item, mode);
			
			whitelistMenus.get(page).setItem(contentRegion.getNextSlot(index++), item);
		}
	}
	
	private void addWhitelistedMode (ParticleModes mode)
	{
		ItemStack item = ItemUtil.createItem(mode.getMode().getMenuItem(), mode.getDisplayName());
		EditorLore.updateModeItemDescription(item, mode);
		
		int size = targetHat.getWhitelistedModes().size();
		int page = contentRegion.getPage(size);
		int index = contentRegion.getNextSlot(size);
		
		if (!whitelistMenus.containsKey(page)) {
			insertMenu(whitelistMenus, page, Message.EDITOR_MODE_OVERVIEW_MENU_WHITELIST_TITLE.getValue(), true);
		}
		
		setEmpty(false);
		whitelistMenus.get(page).setItem(index, item);
		whitelistModified = true;
	}
	
	private void generateBlacklistMenus ()
	{
		generateMenus(targetHat.getBlacklistedModes(), blacklistMenus, Message.EDITOR_MODE_OVERVIEW_MENU_BLACKLIST_TITLE.getValue(), false);
		
		int index = 0;
		int page = 0;
		
		List<ParticleModes> modes = targetHat.getBlacklistedModes();
		if (modes.isEmpty()) 
		{
			blacklistMenus.get(0).setItem(22, emptyBlacklistItem);
			return;
		}
		
		for (ParticleModes mode : modes)
		{
			page = contentRegion.getPage(index);
			
			ItemStack item = ItemUtil.createItem(mode.getMode().getMenuItem(), mode.getDisplayName());
			EditorLore.updateModeItemDescription(item, mode);
			
			blacklistMenus.get(page).setItem(contentRegion.getNextSlot(index++), item);
		}
	}
	
	private void addBlacklistedMode (ParticleModes mode)
	{
		ItemStack item = ItemUtil.createItem(mode.getMode().getMenuItem(), mode.getDisplayName());
		EditorLore.updateModeItemDescription(item, mode);
		
		int size = targetHat.getBlacklistedModes().size();
		int page = contentRegion.getPage(size);
		int index = contentRegion.getNextSlot(size);
		
		if (!blacklistMenus.containsKey(page)) {
			insertMenu(blacklistMenus, page, Message.EDITOR_MODE_OVERVIEW_MENU_BLACKLIST_TITLE.getValue(), true);
		}
		
		setEmpty(false);
		blacklistMenus.get(page).setItem(index, item);
		blacklistModified = true;
	}
	
	private void generateMenus (List<ParticleModes> modes, Map<Integer, Inventory> menus, String title, boolean whitelist)
	{
		int pages = MathUtil.calculatePageCount(modes.size(), contentRegion.getTotalSlots());		
		for (int i = 0; i < pages; i++)
		{
			Inventory inventory = createInventory(title, whitelist);
			
			if ((i + 1) < pages) {
				inventory.setItem(50, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
			}
			
			if ((i + 1) > 1) {
				inventory.setItem(48, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_PREVIOUS_PAGE));
			}
			
			menus.put(i, inventory);
		}
	}
	
	private void insertMenu (Map<Integer, Inventory> menus, int page, String title, boolean whitelist)
	{
		Inventory inventory = createInventory(title, whitelist);
		
		if ((page + 1) > 1) {
			inventory.setItem(48, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_PREVIOUS_PAGE));
		}
		
		menus.put(page, inventory);
		
		for (int i = page - 1; i >= 0; i--) 
		{
			Inventory inv = menus.get(i);
			if (inv.getItem(50) == null) {
				inv.setItem(50, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
			}
		}
	}
	
	private Inventory createInventory (String title, boolean whitelist)
	{
		Inventory inventory = Bukkit.createInventory(null, 54, title);
		inventory.setItem(49, backButtonItem);
		
		if (whitelist)
		{
			inventory.setItem(45, ItemUtil.createItem(CompatibleMaterial.MUSHROOM_STEW, Message.EDITOR_MODE_OVERVIEW_MENU_EDIT_WHITELIST));
			inventory.setItem(46, ItemUtil.createItem(Material.BOWL, Message.EDITOR_MODE_OVERVIEW_MENU_EDIT_BLACKLIST));
			
			inventory.setItem(52, ItemUtil.createItem(CompatibleMaterial.REDSTONE_TORCH, "NYI", Message.EDITOR_MODE_OVERVIEW_MENU_WHITELIST_INFO));
			inventory.setItem(53, ItemUtil.createItem(CompatibleMaterial.TURTLE_HELMET, Message.EDITOR_MODE_OVERVIEW_MENU_ADD_WHITELIST));
		}
		
		else 
		{
			inventory.setItem(45, ItemUtil.createItem(Material.BOWL, Message.EDITOR_MODE_OVERVIEW_MENU_EDIT_WHITELIST));
			inventory.setItem(46, ItemUtil.createItem(CompatibleMaterial.MUSHROOM_STEW, Message.EDITOR_MODE_OVERVIEW_MENU_EDIT_BLACKLIST));
			
			inventory.setItem(52, ItemUtil.createItem(CompatibleMaterial.REDSTONE_TORCH, "NYI", Message.EDITOR_MODE_OVERVIEW_MENU_BLACKLIST_INFO));
			inventory.setItem(53, ItemUtil.createItem(CompatibleMaterial.TURTLE_HELMET, Message.EDITOR_MODE_OVERVIEW_MENU_ADD_BLACKLIST));
		}
		
		return inventory;
	}

}
