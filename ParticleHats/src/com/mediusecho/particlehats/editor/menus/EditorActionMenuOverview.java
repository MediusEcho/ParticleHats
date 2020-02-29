package com.mediusecho.particlehats.editor.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.ui.AbstractStaticMenu;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorActionMenuOverview extends AbstractStaticMenu {

	private final EditorMenuManager editorManager;
	private final MenuCallback callback;
	private final Hat targetHat;
	
	private ActionClickType editingClickType = ActionClickType.NONE;
	
	public EditorActionMenuOverview(ParticleHats core, EditorMenuManager menuManager, Player owner, MenuCallback callback) 
	{
		super(core, menuManager, owner);
		
		this.editorManager = menuManager;
		this.callback = callback;
		this.targetHat = menuManager.getBaseHat();
		this.inventory = Bukkit.createInventory(null, 27, Message.EDITOR_ACTION_OVERVIEW_MENU_TITlE.getValue());
		
		build();
	}
	
	@Override
	public void open ()
	{
		if (editingClickType != ActionClickType.NONE)
		{
			onActionChange(editingClickType == ActionClickType.LEFT ? true : false);
			editingClickType = ActionClickType.NONE;
		}
		
		super.open();
	}

	@Override
	protected void build() 
	{
		setButton(10, backButtonItem, backButtonAction);
		
		// Left Click
		ItemStack leftActionItem = ItemUtil.createItem(CompatibleMaterial.GUNPOWDER, Message.EDITOR_ACTION_OVERVIEW_MENU_SET_LEFT_CLICK);
		EditorLore.updateSpecificActionDescription(leftActionItem, targetHat, targetHat.getLeftClickAction(), targetHat.getLeftClickArgument());
		setButton(14, leftActionItem, (event, slot) ->
		{
			if (event.isLeftClick()) {
				openActionMenu(true);
			} else if (event.isRightClick()) {
				openPropertiesMenu(true);
			}
			return MenuClickResult.NEUTRAL;
		});
		
		// Right Click
		ItemStack rightActionItem = ItemUtil.createItem(CompatibleMaterial.GUNPOWDER, Message.EDITOR_ACTION_OVERVIEW_MENU_SET_RIGHT_CLICK);
		EditorLore.updateSpecificActionDescription(rightActionItem, targetHat, targetHat.getRightClickAction(), targetHat.getRightClickArgument());
		setButton(16, rightActionItem, (event, slot) ->
		{
			if (event.isLeftClick()) {
				openActionMenu(false);
			} else if (event.isRightClick()) {
				openPropertiesMenu(false);
			}
			return MenuClickResult.NEUTRAL;
		});
	}

	@Override
	public void onClose(boolean forced) {
		
	}

	@Override
	public void onTick(int ticks) {}
	
	private void openActionMenu (boolean isLeftClick)
	{
		EditorActionMenu editorActionMenu = new EditorActionMenu(core, editorManager, owner, isLeftClick, (action) ->
		{
			if (action == null) {
				return;
			}
			
			ParticleAction pa = (ParticleAction)action;
			
			if (isLeftClick) {
				targetHat.setLeftClickAction(pa);
			} else {
				targetHat.setRightClickAction(pa);
			}
			
			onActionChange(isLeftClick);
			menuManager.closeCurrentMenu();
		});
		
		menuManager.addMenu(editorActionMenu);
		editorActionMenu.open();
	}
	
	private void openPropertiesMenu (boolean isLeftClick)
	{
		ParticleAction action = isLeftClick ? targetHat.getLeftClickAction() : targetHat.getRightClickAction();
		switch (action)
		{
		case OPEN_MENU_PERMISSION:
		case OPEN_MENU:
		{
			EditorMenuSelectionMenu editorMenuSelectionMenu = new EditorMenuSelectionMenu(core, editorManager, owner, false, (menuName) ->
			{
				if (menuName == null) {
					return;
				}
				
				String argument = (String)menuName;
				
				if (isLeftClick) {
					targetHat.setLeftClickArgument(argument);
				} else {
					targetHat.setRightClickArgument(argument);
				}
				
				menuManager.closeCurrentMenu();
				onActionChange(isLeftClick);
			});
			
			menuManager.addMenu(editorMenuSelectionMenu);
			editorMenuSelectionMenu.open();
		}
		break;
			
		case COMMAND:
		{
			if (isLeftClick) {
				editingClickType = ActionClickType.LEFT;
			} else {
				editingClickType = ActionClickType.RIGHT;
			}
			
			targetHat.setEditingAction(isLeftClick ? 1 : 2);
			editorManager.getOwnerState().setMetaState(MetaState.HAT_COMMAND);
			core.prompt(owner, MetaState.HAT_COMMAND);
			
			owner.closeInventory();
		}
		break;
			
		case DEMO:
		{
			EditorDurationMenu editorDurationMenu = new EditorDurationMenu(core, editorManager, owner, () ->
			{
				onActionChange(isLeftClick);
			});
			
			menuManager.addMenu(editorDurationMenu);
			editorDurationMenu.open();
		}
		break;
			
		default:
			break;
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
		callback.onCallback();
	}
	
	private enum ActionClickType {
		
		NONE,
		LEFT,
		RIGHT;
		
	}
	
}
