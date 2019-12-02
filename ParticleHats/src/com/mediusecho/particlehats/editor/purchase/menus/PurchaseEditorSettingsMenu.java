package com.mediusecho.particlehats.editor.purchase.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.editor.menus.EditorBaseMenu;
import com.mediusecho.particlehats.editor.menus.EditorResizeMenu;
import com.mediusecho.particlehats.editor.purchase.PurchaseMenuManager;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.ui.AbstractStaticMenu;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class PurchaseEditorSettingsMenu extends AbstractStaticMenu {

	private final PurchaseMenuManager purchaseManager;
	private final EditorBaseMenu editorBaseMenu;
	private final MenuInventory menuInventory;
	
	public PurchaseEditorSettingsMenu(ParticleHats core, PurchaseMenuManager menuManager, Player owner) 
	{
		super(core, menuManager, owner);
	
		this.purchaseManager = menuManager;
		this.editorBaseMenu = menuManager.getEditingMenu();
		this.menuInventory = editorBaseMenu.getMenuInventory();
		this.inventory = Bukkit.createInventory(null, 36, Message.EDITOR_SETTINGS_MENU_TITLE.getValue());
		
		build();
	}

	@Override
	protected void build() 
	{
		// Back
		setButton(31, backButtonItem, backButtonAction);
		
		// Set Title
		ItemStack titleItem = ItemUtil.createItem(Material.SIGN, Message.EDITOR_SETTINGS_MENU_SET_TITLE);
		setButton(10, titleItem, (event, slot) ->
		{
			purchaseManager.getOwnerState().setMetaState(MetaState.MENU_TITLE);
			core.prompt(owner, MetaState.MENU_TITLE);
			owner.closeInventory();
			return MenuClickResult.NEUTRAL;
		});
		
		// Set Size
		ItemStack sizeItem = ItemUtil.createItem(CompatibleMaterial.COMPARATOR, Message.EDITOR_SETTINGS_MENU_SET_SIZE);
		setButton(12, sizeItem, (event, slot) ->
		{
			EditorResizeMenu editorResizeMenu = new EditorResizeMenu(core, purchaseManager, owner, editorBaseMenu);
			menuManager.addMenu(editorResizeMenu);
			editorResizeMenu.open();
			return MenuClickResult.NEUTRAL;
		});
		
		// Toggle Live Updates
		ItemStack liveItem = ItemUtil.createItem(Material.LEVER, Message.EDITOR_SETTINGS_MENU_TOGGLE_LIVE_MENU);
		EditorLore.updateBooleanDescription(liveItem, editorBaseMenu.canUpdate(), Message.EDITOR_SETTINGS_MENU_ANIMATION_DESCRIPTION);
		setButton(14, liveItem, (event, slot) ->
		{
			editorBaseMenu.toggleUpdates();
			EditorLore.updateBooleanDescription(getItem(14), editorBaseMenu.canUpdate(), Message.EDITOR_SETTINGS_MENU_ANIMATION_DESCRIPTION);
			
			return MenuClickResult.NEUTRAL;
		});
		
		// Sync Icons
		ItemStack syncItem = ItemUtil.createItem(CompatibleMaterial.CONDUIT, Message.EDITOR_SETTINGS_MENU_SYNC_ICONS, Message.EDITOR_SETTINGS_SYNC_DESCRIPTION);
		setButton(16, syncItem, (event, slot) ->
		{
			editorBaseMenu.syncItems();
			return MenuClickResult.NEUTRAL;
		});
	}

	@Override
	public void onClose(boolean forced) {}

	@Override
	public void open ()
	{
		String titleDescription = Message.EDITOR_SETTINGS_MENU_TITLE_DESCRIPTION.getValue();
		String title = titleDescription.replace("{1}", menuInventory.getTitle());
		
		ItemStack titleItem = getItem(10);
		if (titleItem != null) {
			ItemUtil.setItemDescription(titleItem, StringUtil.parseDescription(title));
		}
		
		super.open();
	}
	
	@Override
	public void onTick(int ticks) {}

}
