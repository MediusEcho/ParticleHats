package com.mediusecho.particlehats.editor.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.ui.AbstractStaticMenu;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorSettingsMenu extends AbstractStaticMenu {

	private final EditorMenuManager editorManager;
	private final EditorBaseMenu editorBaseMenu;
	private final MenuInventory menuInventory;
	
	public EditorSettingsMenu(ParticleHats core, EditorMenuManager menuManager, Player owner, EditorBaseMenu editorBaseMenu) 
	{
		super(core, menuManager, owner);
		
		this.editorManager = menuManager;
		this.editorBaseMenu = editorBaseMenu;
		this.menuInventory = editorBaseMenu.getMenuInventory();
		this.inventory = Bukkit.createInventory(null, 54, Message.EDITOR_SETTINGS_MENU_TITLE.getValue());
		build();
	}

	@Override
	protected void build() 
	{
		// Back
		setButton(49, backButtonItem, backButtonAction);
		
		// Set Title
		ItemStack titleItem = ItemUtil.createItem(Material.SIGN, Message.EDITOR_SETTINGS_MENU_SET_TITLE);
		setButton(11, titleItem, (event, slot) ->
		{
			editorManager.getOwnerState().setMetaState(MetaState.MENU_TITLE);
			core.prompt(owner, MetaState.MENU_TITLE);
			menuManager.closeInventory();
			return MenuClickResult.NEUTRAL;
		});
		
		// Set Alias
		ItemStack aliasItem = ItemUtil.createItem(Material.NAME_TAG, Message.EDITOR_SETTINGS_MENU_SET_ALIAS);
		setButton(13, aliasItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				editorManager.getOwnerState().setMetaState(MetaState.MENU_ALIAS);
				core.prompt(owner, MetaState.MENU_ALIAS);
				menuManager.closeInventory();
			}
			
			else if (event.isShiftRightClick())
			{
				editorBaseMenu.getMenuInventory().resetAlias();
				core.getDatabase().saveMenuAlias(menuInventory.getName(), "NULL");
				EditorLore.updateAliasDescription(inventory.getItem(13), menuInventory.getAlias());
			}
			return MenuClickResult.NEUTRAL;
		});
		
		// Set Size
		ItemStack sizeItem = ItemUtil.createItem(CompatibleMaterial.COMPARATOR, Message.EDITOR_SETTINGS_MENU_SET_SIZE);
		setButton(15, sizeItem, (event, slot) ->
		{
			EditorResizeMenu editorResizeMenu = new EditorResizeMenu(core, menuManager, owner, editorBaseMenu);
			menuManager.addMenu(editorResizeMenu);
			editorResizeMenu.open();
			return MenuClickResult.NEUTRAL;
		});
		
		// Toggle Live Updates
		ItemStack liveItem = ItemUtil.createItem(Material.LEVER, Message.EDITOR_SETTINGS_MENU_TOGGLE_LIVE_MENU);
		EditorLore.updateBooleanDescription(liveItem, editorBaseMenu.canUpdate(), Message.EDITOR_SETTINGS_MENU_ANIMATION_DESCRIPTION);
		setButton(29, liveItem, (event, slot) ->
		{
			editorBaseMenu.toggleUpdates();
			EditorLore.updateBooleanDescription(getItem(29), editorBaseMenu.canUpdate(), Message.EDITOR_SETTINGS_MENU_ANIMATION_DESCRIPTION);
			
			return MenuClickResult.NEUTRAL;
		});
		
		// Sync Icons
		ItemStack syncItem = ItemUtil.createItem(CompatibleMaterial.CONDUIT, Message.EDITOR_SETTINGS_MENU_SYNC_ICONS, Message.EDITOR_SETTINGS_SYNC_DESCRIPTION);
		setButton(31, syncItem, (event, slot) ->
		{
			editorBaseMenu.syncItems();
			return MenuClickResult.NEUTRAL;
		});
		
		// Delete
		ItemStack deleteItem = ItemUtil.createItem(Material.TNT, Message.EDITOR_SETTINGS_MENU_DELETE);
		setButton(33, deleteItem, (event, slot) ->
		{
			EditorDeleteMenu editorDeleteMenu = new EditorDeleteMenu(core, menuManager, owner, editorBaseMenu);
			menuManager.addMenu(editorDeleteMenu);
			editorDeleteMenu.open();
			return MenuClickResult.NEUTRAL;
		});
	}
	
	@Override
	public void open ()
	{
		String titleDescription = Message.EDITOR_SETTINGS_MENU_TITLE_DESCRIPTION.getValue();
		String title = titleDescription.replace("{1}", menuInventory.getTitle());
		
		ItemStack titleItem = getItem(11);
		if (titleItem != null) {
			ItemUtil.setItemDescription(titleItem, StringUtil.parseDescription(title));
		}
		
		ItemStack aliasItem = getItem(13);
		if (aliasItem != null) {
			EditorLore.updateAliasDescription(aliasItem, menuInventory.getAlias());
		}
		
		super.open();
	}

	@Override
	public void onClose(boolean forced) {
		
	}

	@Override
	public void onTick(int ticks) {
		
	}

}
