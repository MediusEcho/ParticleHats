package com.mediusecho.particlehats.editor.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorActionOverviewMenu extends EditorMenu {

	private final EditorGenericCallback callback;
	//private final EditorMainMenu editorMainMenu;
	private final Hat targetHat;
	
//	public EditorActionOverviewMenu(Core core, Player owner, MenuBuilder menuBuilder, EditorMainMenu editorMainMenu) 
	public EditorActionOverviewMenu(Core core, Player owner, MenuBuilder menuBuilder, EditorGenericCallback callback) 
	{
		super(core, owner, menuBuilder);
		this.callback = callback;
		//this.editorMainMenu = editorMainMenu;
		this.targetHat = menuBuilder.getBaseHat();
		
		inventory = Bukkit.createInventory(null, 27, Message.EDITOR_ACTION_OVERVIEW_MENU_TITlE.getValue());
		build();
	}

	@Override
	protected void build() 
	{
		setButton(10, backButton, backAction);
		
		// Left Click
		ItemStack leftActionItem = ItemUtil.createItem(Material.GUNPOWDER, Message.EDITOR_ACTION_OVERVIEW_MENU_SET_LEFT_CLICK);
		EditorLore.updateSpecificActionDescription(leftActionItem, targetHat, targetHat.getLeftClickAction(), targetHat.getLeftClickArgument());
		setButton(14, leftActionItem, (event, slot) ->
		{
			if (event.isLeftClick()) {
				openActionMenu(true);
			} else if (event.isRightClick()) {
				openPropertiesMenu(true);
			}
			return EditorClickType.NEUTRAL;
		});
		
		// Right Click
		ItemStack rightActionItem = ItemUtil.createItem(Material.GUNPOWDER, Message.EDITOR_ACTION_OVERVIEW_MENU_SET_RIGHT_CLICK);
		EditorLore.updateSpecificActionDescription(rightActionItem, targetHat, targetHat.getRightClickAction(), targetHat.getRightClickArgument());
		setButton(16, rightActionItem, (event, slot) ->
		{
			if (event.isLeftClick()) {
				openActionMenu(false);
			} else if (event.isRightClick()) {
				openPropertiesMenu(false);
			}
			return EditorClickType.NEUTRAL;
		});
	}
	
	private void openActionMenu (boolean leftClick)
	{
		EditorActionMenu editorActionMenu = new EditorActionMenu(core, owner, menuBuilder, leftClick, (action) ->
		{
			if (leftClick) {
				targetHat.setLeftClickAction(action);
			} else {
				targetHat.setRightClickAction(action);
			}
			
			onActionChange(leftClick);
			menuBuilder.goBack();
		});
		menuBuilder.addMenu(editorActionMenu);
		editorActionMenu.open();
	}
	
	private void openPropertiesMenu (boolean leftClick)
	{
		ParticleAction action = leftClick ? targetHat.getLeftClickAction() : targetHat.getRightClickAction();
		switch (action)
		{
		case OPEN_MENU_PERMISSION:
		case OPEN_MENU:
		{
			EditorMenuSelectionMenu editorMenuSelectionMenu = new EditorMenuSelectionMenu(core, owner, menuBuilder, (string) ->
			{
				Core.log(string);
				if (leftClick) {
					targetHat.setLeftClickArgument(string);
				} else {
					targetHat.setRightClickArgument(string);
				}
				onActionChange(leftClick);
			});
			menuBuilder.addMenu(editorMenuSelectionMenu);
			editorMenuSelectionMenu.open();
		}
		break;
		
		case COMMAND:
		{
			targetHat.setEditingAction(leftClick ? 1 : 2);
			menuBuilder.setOwnerState(MetaState.HAT_COMMAND);
			owner.closeInventory();
		}
		break;
		
		case DEMO:
		{
			EditorDurationMenu editorDurationMenu = new EditorDurationMenu(core, owner, menuBuilder, this, leftClick);
			menuBuilder.addMenu(editorDurationMenu);
			editorDurationMenu.open();
		}
		break;
		default: break;
		
		}
	}
	
	/**
	 * Called any time an action is changed
	 * @param isLeftClick
	 */
	public void onActionChange (boolean isLeftClick)
	{
		ParticleAction action = isLeftClick ? targetHat.getLeftClickAction() : targetHat.getRightClickAction();
		String argument = isLeftClick ? targetHat.getLeftClickArgument() : targetHat.getRightClickArgument();
		ItemStack item = isLeftClick ? getItem(14) : getItem(16);
		
		EditorLore.updateSpecificActionDescription(item, targetHat, action, argument);
		callback.onExecute();
	}
}
