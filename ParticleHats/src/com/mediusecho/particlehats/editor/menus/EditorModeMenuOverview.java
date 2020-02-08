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
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.ParticleModes;
import com.mediusecho.particlehats.ui.AbstractStaticMenu;
import com.mediusecho.particlehats.ui.MenuManager;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;

public class EditorModeMenuOverview extends AbstractStaticMenu {

	private final Hat targetHat;
	private Map<Integer, Inventory> whitelistMenus;
	private Map<Integer, Inventory> blacklistMenus;
	
	private int currentWhitelistPage = 0;
	private int currentBlacklistPage = 0;
	
	private boolean isEditingWhitelist = true;
	
	public EditorModeMenuOverview(ParticleHats core, EditorMenuManager editorManager, Player owner) 
	{
		super(core, editorManager, owner);
		
		this.targetHat = editorManager.getTargetHat();
		this.inventory = Bukkit.createInventory(null, 54, Message.EDITOR_MODE_OVERVIEW_MENU_TITLE.getValue());
		this.whitelistMenus = new HashMap<Integer, Inventory>();
		this.blacklistMenus = new HashMap<Integer, Inventory>();
		
		build();
	}

	@Override
	protected void build() 
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

		generateWhitelistMenus(targetHat.getWhitelistedModes());
		generateBlacklistMenus(targetHat.getBlacklistedModes());
	}
	
	@Override
	public void open ()
	{
		Inventory inventory = whitelistMenus.get(0);
		open(inventory);
	}
	
	@Override
	public boolean hasInventory (Inventory inventory)
	{
		if (whitelistMenus.containsValue(inventory)) {
			return true;
		}
		
		if (blacklistMenus.containsValue(inventory)) {
			return true;
		}
		
		return false;
	}
	
	private void open (Inventory inventory)
	{
		menuManager.isOpeningMenu(this);
		owner.openInventory(inventory);
	}

	@Override
	public void onClose(boolean forced) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTick(int ticks) {
		// TODO Auto-generated method stub
		
	}
	
	private void generateWhitelistMenus (List<ParticleModes> whitelist)
	{
		generateMenus(whitelist, whitelistMenus, Message.EDITOR_MODE_OVERVIEW_MENU_WHITELISTED_TITLE.getValue(), true);
	}
	
	private void generateBlacklistMenus (List<ParticleModes> blacklist)
	{
		generateMenus(blacklist, blacklistMenus, Message.EDITOR_MODE_OVERVIEW_MENU_BLACKLIST_TITLE.getValue(), false);
	}
	
	private void generateMenus (List<ParticleModes> modes, Map<Integer, Inventory> menus, String title, boolean whitelist)
	{
		int pages = MathUtil.calculatePageCount(modes.size(), 27);
		for (int i = 0; i < pages; i++)
		{
			Inventory inventory = Bukkit.createInventory(null, 54, title);
			
			inventory.setItem(49, backButtonItem);
			
			if (whitelist)
			{
				inventory.setItem(45, ItemUtil.createItem(CompatibleMaterial.MUSHROOM_STEW, Message.EDITOR_MODE_OVERVIEW_MENU_EDIT_WHITELIST));
				inventory.setItem(46, ItemUtil.createItem(Material.BOWL, Message.EDITOR_MODE_OVERVIEW_MENU_EDIT_BLACKLIST));
			}
			
			else 
			{
				inventory.setItem(45, ItemUtil.createItem(Material.BOWL, Message.EDITOR_MODE_OVERVIEW_MENU_EDIT_WHITELIST));
				inventory.setItem(46, ItemUtil.createItem(CompatibleMaterial.MUSHROOM_STEW, Message.EDITOR_MODE_OVERVIEW_MENU_EDIT_BLACKLIST));
			}
			
			if ((i + 1) < pages) {
				inventory.setItem(50, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
			}
			
			if ((i + 1) > 1) {
				inventory.setItem(48, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_PREVIOUS_PAGE));
			}
			
			menus.put(i, inventory);
		}
	}

}
