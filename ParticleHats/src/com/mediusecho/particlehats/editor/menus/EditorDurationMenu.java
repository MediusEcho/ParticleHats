package com.mediusecho.particlehats.editor.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorDurationMenu extends EditorMenu {

	private final EditorActionOverviewMenu editorActionOverviewMenu;
	private final boolean leftClick;
	
	private final Hat targetHat;
	
	public EditorDurationMenu(Core core, Player owner, MenuBuilder menuBuilder, EditorActionOverviewMenu editorActionOverviewMenu, boolean leftClick)
	{
		super(core, owner, menuBuilder);
		this.editorActionOverviewMenu = editorActionOverviewMenu;
		this.leftClick = leftClick;
		this.targetHat = menuBuilder.getBaseHat();
		
		inventory = Bukkit.createInventory(null, 27, Message.EDITOR_DURATION_MENU_TITLE.getValue());
		build();
	}
	
	@Override
	public void onClose (boolean forced)
	{
		if (!forced) {
			editorActionOverviewMenu.onActionChange(leftClick);
		}
	}

	@Override
	protected void build() 
	{
		setButton(12, backButton, backAction);
		
		ItemStack durationItem = ItemUtil.createItem(Material.MAP, Message.EDITOR_DURATION_MENU_SET_DURATION.getValue());
		EditorLore.updateDurationDescription(durationItem, targetHat.getDemoDuration(), Message.EDITOR_DURATION_MENU_DESCRIPTION);
		setButton(14, durationItem, (event, slot) ->
		{
			int normalClick    = event.isLeftClick() ? 20 : -20;
			int shiftClick     = event.isShiftClick() ? 30 : 1;
			int modifier       = normalClick * shiftClick;
			
			int duration = targetHat.getDemoDuration() + modifier;
			targetHat.setDemoDuration(duration);
			
			EditorLore.updateDurationDescription(getItem(14), targetHat.getDemoDuration(), Message.EDITOR_DURATION_MENU_DESCRIPTION);
			return event.isLeftClick() ? EditorClickType.POSITIVE : EditorClickType.NEGATIVE;
		});
	}

}
