package com.mediusecho.particlehats.editor.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.ui.MenuManager;
import com.mediusecho.particlehats.ui.menus.SingularMenu;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorDeleteMenu extends SingularMenu {

	private final EditorBaseMenu editorBaseMenu;
	
	public EditorDeleteMenu(ParticleHats core, MenuManager menuManager, Player owner, EditorBaseMenu editorBaseMenu) 
	{
		super(core, menuManager, owner);
		
		this.editorBaseMenu = editorBaseMenu;
		this.inventory = Bukkit.createInventory(null, 27, Message.EDITOR_DELETE_MENU_TITLE.getValue());
		
		build();
	}

	@Override
	protected void build()
	{
		ItemStack yesItem = ItemUtil.createItem(CompatibleMaterial.ROSE_RED, Message.EDITOR_DELETE_MENU_YES);
		setButton(12, yesItem, (event, slot) ->
		{
			core.getDatabase().deleteMenu(editorBaseMenu.getMenuInventory().getName());
			owner.closeInventory();
			return MenuClickResult.NEUTRAL;
		});
		
		ItemStack noItem = ItemUtil.createItem(Material.COAL, Message.EDITOR_DELETE_MENU_NO);
		setButton(14, noItem, (event, slot) ->
		{
			menuManager.closeCurrentMenu();
			return MenuClickResult.NEUTRAL;
		});
	}

	@Override
	public void onClose(boolean forced) {}

	@Override
	public void onTick(int ticks) {}

}
