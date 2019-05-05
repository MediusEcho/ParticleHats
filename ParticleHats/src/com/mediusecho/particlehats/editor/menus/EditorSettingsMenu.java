package com.mediusecho.particlehats.editor.menus;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorSettingsMenu extends EditorMenu {

	public EditorSettingsMenu(ParticleHats core, Player owner, MenuBuilder menuBuilder) 
	{
		super(core, owner, menuBuilder);
		
		inventory = Bukkit.createInventory(null, 54, Message.EDITOR_SETTINGS_MENU_TITLE.getValue());
		build();
	}
	
	@Override
	public void open ()
	{
		String titleDescription = Message.EDITOR_SETTINGS_MENU_TITLE_DESCRIPTION.getValue();
		String title = titleDescription.replace("{1}", menuBuilder.getEditingMenu().getTitle());
		
		ItemStack titleItem = getItem(11);
		if (titleItem != null) {
			ItemUtil.setItemDescription(titleItem, StringUtil.parseDescription(title));
		}
		
		ItemStack aliasItem = getItem(13);
		if (aliasItem != null) {
			EditorLore.updateAliasDescription(aliasItem, menuBuilder.getEditingMenu().getAlias(), Message.EDITOR_SETTINGS_MENU_ALIAS_DESCRIPTION);
		}
		
		super.open();
	}

	@Override
	protected void build() 
	{
		// Back
		setButton(49, backButton, backAction);
		
		// Set Title
		ItemStack titleItem = ItemUtil.createItem(Material.SIGN, Message.EDITOR_SETTINGS_MENU_SET_TITLE);
		setButton(11, titleItem, (event, slot) ->
		{
			menuBuilder.setOwnerState(MetaState.MENU_TITLE);
			core.prompt(owner, MetaState.MENU_TITLE);
			owner.closeInventory();
			return EditorClickType.NEUTRAL;
		});
		
		// Set Alias
		ItemStack aliasItem = ItemUtil.createItem(Material.NAME_TAG, Message.EDITOR_SETTINGS_MENU_SET_ALIAS);
		setButton(13, aliasItem, (event, slot) ->
		{
			menuBuilder.setOwnerState(MetaState.MENU_ALIAS);
			core.prompt(owner, MetaState.MENU_ALIAS);
			owner.closeInventory();
			return EditorClickType.NEUTRAL;
		});
		
		// Set Size
		ItemStack sizeItem = ItemUtil.createItem(CompatibleMaterial.COMPARATOR, Message.EDITOR_SETTINGS_MENU_SET_SIZE);
		setButton(15, sizeItem, (event, slot) ->
		{
			EditorResizeMenu editorResizeMenu = new EditorResizeMenu(core, owner, menuBuilder);
			menuBuilder.addMenu(editorResizeMenu);
			editorResizeMenu.open();
			return EditorClickType.NEUTRAL;
		});
		
		// Toggle Live Updates
		ItemStack liveItem = ItemUtil.createItem(Material.LEVER, Message.EDITOR_SETTINGS_MENU_TOGGLE_LIVE_MENU);
		EditorLore.updateBooleanDescription(liveItem, menuBuilder.getEditingMenu().isLive(), Message.EDITOR_SETTINGS_MENU_ANIMATION_DESCRIPTION);
		setButton(29, liveItem, (event, slot) ->
		{
			EditorBaseMenu baseMenu = menuBuilder.getEditingMenu();
			
			baseMenu.toggleLive();
			EditorLore.updateBooleanDescription(getItem(29), baseMenu.isLive(), Message.EDITOR_SETTINGS_MENU_ANIMATION_DESCRIPTION);
			
			return EditorClickType.NEUTRAL;
		});
		
		// Sync Icons
		ItemStack syncItem = ItemUtil.createItem(CompatibleMaterial.CONDUIT, Message.EDITOR_SETTINGS_MENU_SYNC_ICONS, Message.EDITOR_SETTINGS_SYNC_DESCRIPTION);
		setButton(31, syncItem, (event, slot) ->
		{
			for (Entry<Integer, Hat> hats : menuBuilder.getEditingMenu().getHats().entrySet())
			{
				Hat hat = hats.getValue();
				hat.getIconData().reset();
			}
			menuBuilder.goBack();
			return EditorClickType.NEUTRAL;
		});
		
		// Delete
		ItemStack deleteItem = ItemUtil.createItem(Material.TNT, Message.EDITOR_SETTINGS_MENU_DELETE);
		setButton(33, deleteItem, (event, slot) ->
		{
			EditorDeleteMenu editorDeleteMenu = new EditorDeleteMenu(core, owner, menuBuilder);
			menuBuilder.addMenu(editorDeleteMenu);
			editorDeleteMenu.open();
			return EditorClickType.NEUTRAL;
		});
	}

}
