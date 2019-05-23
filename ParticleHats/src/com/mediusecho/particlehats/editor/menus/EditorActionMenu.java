package com.mediusecho.particlehats.editor.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.ui.GuiState;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorActionMenu extends EditorMenu {
	
	private final Map<Integer, Inventory> menus;
	private final List<ParticleAction> actions;
	private final int pages;
	private final Hat targetHat;
	
	private int currentPage = 0;
	private final boolean isLeftClick;
	private final boolean showHiddenActions;
	
	private final EditorAction selectAction;
	
	public EditorActionMenu (ParticleHats core, Player owner, MenuBuilder menuBuilder, boolean isLeftClick, boolean showHiddenActions, EditorActionCallback actionCallback)
	{
		super(core, owner, menuBuilder);
		
		targetHat = menuBuilder.getBaseHat();
		menus = new HashMap<Integer, Inventory>();
		actions = new ArrayList<ParticleAction>();
		pages = (int) Math.ceil(ParticleAction.values().length / 28D);
		this.isLeftClick = isLeftClick;
		this.showHiddenActions = showHiddenActions;
		
		selectAction = (event, slot) ->
		{
			int index = getClampedIndex(slot, 10, 2);
			ParticleAction action = actions.get(index);
			if (action == null) {
				action = ParticleAction.EQUIP;
			}
			
			actionCallback.onSelect(action);
			return EditorClickType.NEUTRAL;
		};
		
		build();
	}
	
	public EditorActionMenu (ParticleHats core, Player owner, MenuBuilder menuBuilder, boolean isLeftClick, EditorActionCallback actionCallback) 
	{
		this(core, owner, menuBuilder, isLeftClick, false, actionCallback);
	}
	
	@Override
	public void open ()
	{
		if (menus.containsKey(currentPage))
		{
			menuBuilder.setOwnerState(GuiState.SWITCHING_EDITOR);
			//menuBuilder.setOwnerState(MenuState.SWITCHING);
			owner.openInventory(menus.get(currentPage));
		}
	}

	@Override
	protected void build() 
	{		
		// Setup actions
		setAction(49, backAction);
		
		String menuTitle = Message.EDITOR_ACTION_MENU_TITLE.getValue();
		String[] leftClickInfo = StringUtil.parseValue(menuTitle, "1");
		String[] rightClickInfo = StringUtil.parseValue(menuTitle, "2");
		String leftClick = isLeftClick ? leftClickInfo[1] : "";
		String rightClick = !isLeftClick ? rightClickInfo[1] : "";
		
		// Create our pages
		for (int i = 0; i < pages; i++)
		{
			String title = menuTitle
					.replace(leftClickInfo[0], leftClick)
					.replace(rightClickInfo[0], rightClick)
					.replace("{3}", Integer.toString(i + 1)).replace("{4}", Integer.toString(pages));
			Inventory menu = Bukkit.createInventory(null, 54, title);
			
			menu.setItem(49, backButton);
			menus.put(i, menu);
		}
		
		// Fill in our actions
		for (int i = 0; i < 28; i++) {
			setAction(getNormalIndex(i, 10, 2), selectAction);
		}
		
		// Insert out actions
		int index = 0;
		int page = 0;
		for (ParticleAction action : ParticleAction.values())
		{
			// Skip the mimic action if we're selected a left click
			if (action == ParticleAction.MIMIC && isLeftClick) {
				continue;
			}
			
			if (showHiddenActions) 
			{
				if (!action.isHidden() && action != ParticleAction.DUMMY) {
					continue;
				}
			}
			
			else
			{
				if (action.isHidden()) {
					continue;
				}
			}
			
			ItemStack item = ItemUtil.createItem(CompatibleMaterial.FIREWORK_STAR, action.getDisplayName());
			String description = Message.EDITOR_ACTION_MENU_ACTION_DESCRIPTION.getValue();
			String[] selectedInfo = StringUtil.parseValue(description, "2");
			String[] selectInfo = StringUtil.parseValue(description, "3");
			
			ParticleAction currentAction = isLeftClick ? targetHat.getLeftClickAction() : targetHat.getRightClickAction();
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
			
			menus.get(page).setItem(getNormalIndex(index++, 10, 2), item);
			actions.add(action);
			
			if (index % 28 == 0)
			{
				index = 0;
				page++;
			}
		}
	}

}
