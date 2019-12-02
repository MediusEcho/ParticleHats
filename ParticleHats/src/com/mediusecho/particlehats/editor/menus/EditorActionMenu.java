package com.mediusecho.particlehats.editor.menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.ui.AbstractListMenu;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorActionMenu extends AbstractListMenu {

	private final boolean isLeftClickAction;
	private final boolean showHiddenActions;
	private final MenuAction selectAction;
	private final Hat targetHat;
	
	private final List<ParticleAction> actions;
	
	public EditorActionMenu(ParticleHats core, EditorMenuManager menuManager, Player owner, boolean isLeftClickAction, boolean showHiddenActions, MenuObjectCallback callback) 
	{
		super(core, menuManager, owner, false);
		
		this.isLeftClickAction = isLeftClickAction;
		this.showHiddenActions = showHiddenActions;
		this.actions = new ArrayList<ParticleAction>();
		this.targetHat = menuManager.getBaseHat();
		this.totalPages = MathUtil.calculatePageCount(ParticleAction.values().length, 28);
		
		this.selectAction = (event, slot) ->
		{
			int index = getClampedIndex(slot, 10, 2);
			ParticleAction action = actions.get(index);
			
			if (action == null) {
				action = ParticleAction.EQUIP;
			}
			
			callback.onSelect(action);
			return MenuClickResult.NEUTRAL;
		};
		
		build();
	}
	
	public EditorActionMenu(ParticleHats core, EditorMenuManager menuManager, Player owner, boolean isLeftClickAction, MenuObjectCallback callback)
	{
		this(core, menuManager, owner, isLeftClickAction, false, callback);
	}

	@Override
	public void insertEmptyItem() {}

	@Override
	public void removeEmptyItem() {}

	@Override
	protected void build() 
	{
		setAction(49, backButtonAction);
		
		// Previous Page
		setAction(48, (event, slot) ->
		{
			currentPage--;
			open();
			return MenuClickResult.NEUTRAL;
		});
		
		// Next Page
		setAction(50, (event, slot) ->
		{
			currentPage++;
			open();
			return MenuClickResult.NEUTRAL;
		});
		
		// Fill in our actions
		for (int i = 0; i < 28; i++) {
			setAction(getNormalIndex(i, 10, 2), selectAction);
		}
		
		// Create our pages
		String menuTitle = Message.EDITOR_ACTION_MENU_TITLE.getValue();
		String[] leftClickInfo = StringUtil.parseValue(menuTitle, "1");
		String[] rightClickInfo = StringUtil.parseValue(menuTitle, "2");
		String leftClick = isLeftClickAction ? leftClickInfo[1] : "";
		String rightClick = !isLeftClickAction ? rightClickInfo[1] : "";
		
		for (int i = 0; i < totalPages; i++)
		{
			String title = menuTitle
					.replace(leftClickInfo[0], leftClick)
					.replace(rightClickInfo[0], rightClick)
					.replace("{3}", Integer.toString(i + 1))
					.replace("{4}", Integer.toString(totalPages));
			
			Inventory menu = Bukkit.createInventory(null, 54, title);
			
			menu.setItem(49, backButtonItem);
			
			// Next Page
			if ((i + 1) < totalPages) {
				menu.setItem(50, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
			}
			
			// Previous Page
			if ((i + 1) > 1) {
				menu.setItem(48, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_PREVIOUS_PAGE));
			}
			
			setMenu(i, menu);
		}
		
		// Insert our actions
		int index = 0;
		int page = 0;
		
		for (ParticleAction action : ParticleAction.values())
		{
			// Skip the mimic action if we're selecting a left click action
			if (action == ParticleAction.MIMIC && isLeftClickAction) {
				continue;
			}
			
			if (showHiddenActions)
			{
				if (!action.isHidden() && action != ParticleAction.DUMMY) {
					continue;
				}
			}
			
			else {
				if (action.isHidden()) {
					continue;
				}
			}
			
			ItemStack item = ItemUtil.createItem(CompatibleMaterial.FIREWORK_STAR, action.getDisplayName());
			String description = Message.EDITOR_ACTION_MENU_ACTION_DESCRIPTION.getValue();
			String[] selectedInfo = StringUtil.parseValue(description, "2");
			String[] selectInfo = StringUtil.parseValue(description, "3");
			
			ParticleAction currentAction = isLeftClickAction ? targetHat.getLeftClickAction() : targetHat.getRightClickAction();
			if (currentAction.equals(action)) 
			{
				ItemUtil.setItemType(item, CompatibleMaterial.GUNPOWDER);
				ItemUtil.highlightItem(item);
				
				description = description.replace(selectedInfo[0], selectedInfo[1]).replace(selectInfo[0], "");
			} 
			
			else {
				description = description.replace(selectInfo[0], selectInfo[1]).replace(selectedInfo[0], "");
			}
			
			description = description.replace("{1}", action.getDescription());
			ItemUtil.setItemDescription(item, StringUtil.parseDescription(description));
			
			setItem(page, getNormalIndex(index++, 10, 2), item);
			actions.add(action);
			
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
