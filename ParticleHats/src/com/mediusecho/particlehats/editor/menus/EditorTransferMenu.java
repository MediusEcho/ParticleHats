package com.mediusecho.particlehats.editor.menus;

import com.mediusecho.particlehats.compatibility.CompatibleSound;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.ui.AbstractStaticMenu;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorTransferMenu extends AbstractStaticMenu {

	private final EditorMenuManager editorManager;
	private final MenuInventory menuInventory;
	private final ItemStack emptyItem = ItemUtil.createItem(CompatibleMaterial.LIGHT_GRAY_STAINED_GLASS_PANE, Message.EDITOR_MOVE_MENU_MOVE, Message.EDITOR_MOVE_MENU_MOVE_DESCRIPTION);

	private final String menuName;
	
	public EditorTransferMenu(ParticleHats core, EditorMenuManager menuManager, Player owner, String menuName) 
	{
		super(core, menuManager, owner);
		
		this.editorManager = menuManager;
		this.menuName = menuName;
		this.menuInventory = core.getDatabase().loadInventory(menuName, core.getPlayerState(owner));
		this.inventory = Bukkit.createInventory(null, menuInventory.getSize(), EditorLore.getTrimmedMenuTitle(menuInventory.getTitle(), Message.EDITOR_MOVE_MENU_TITLE));
		
		build();
	}

	@Override
	protected void build() 
	{
		final MenuAction moveAction = (event, slot) ->
		{
			if (event.isRightClick())
			{
				menuManager.closeCurrentMenu();
				return MenuClickResult.NEUTRAL;
			}
			
			int currentSlot = editorManager.getBaseHat().getSlot();
			
			core.getDatabase().moveHat(null, editorManager.getBaseHat(), editorManager.getMenuName(), menuName, currentSlot, slot, false);
			editorManager.getEditingMenu().removeButton(currentSlot);
			editorManager.returnToBaseMenu();
			
			return MenuClickResult.NEUTRAL;
		};
		
		final MenuAction cancelAction = (event, slot) ->
		{
			if (event.isRightClick())
			{
				menuManager.closeCurrentMenu();
				return MenuClickResult.NEUTRAL;
			}
			
			else
			{
				CompatibleSound.ENTITY_VILLAGER_NO.play(owner, 1.0f, 1.0f);
				return MenuClickResult.NONE;
			}
		};
		
		for (int i = 0; i < menuInventory.getSize(); i++)
		{
			ItemStack item = menuInventory.getItem(i);
			
			if (item == null) {
				setButton(i, emptyItem, moveAction);
			}
			
			else
			{
				ItemUtil.setNameAndDescription(item, Message.EDITOR_MOVE_MENU_OCCUPIED.getValue(), StringUtil.parseDescription(Message.EDITOR_MOVE_MENU_OCCUPIED_DESCRIPTION.getValue()));
				setButton(i, item, cancelAction);
			}
		}
	}

	@Override
	public void onClose(boolean forced) {}

	@Override
	public void onTick(int ticks) {}

}
