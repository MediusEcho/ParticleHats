package com.mediusecho.particlehats.editor.menus;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.database.Database.DataType;
import com.mediusecho.particlehats.editor.EditorListMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.ParticleTag;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorTagOverviewMenu extends EditorListMenu {

	private final Hat targetHat;
	private final String tagTitle = Message.EDITOR_TAG_OVERVIEW_MENU_TAG_TITLE.getValue();
	
	private boolean modified = false;
	
	public EditorTagOverviewMenu(Core core, Player owner, MenuBuilder menuBuilder) 
	{
		super(core, owner, menuBuilder);
		targetHat = menuBuilder.getBaseHat();
		
		addItem = ItemUtil.createItem(CompatibleMaterial.TURTLE_HELMET, Message.EDITOR_TAG_OVERVIEW_MENU_ADD_TAG);
		editAction = (event, slot) ->
		{
			if (event.isShiftRightClick())
			{
				onDelete(slot);
			}
			return EditorClickType.NEUTRAL;
		};
		
		inventory = Bukkit.createInventory(null, 54, Message.EDITOR_TAG_OVERVIEW_MENU_TITLE.getValue());
		build();
	}
	
	private void onAdd (int slot, ParticleTag tag)
	{
		List<ParticleTag> tags = targetHat.getTags();
		int size = tags.size();
		
		if (tags.contains(tag)) {
			return;
		}
		
		if (size <= 27)
		{
			ItemStack tagItem = ItemUtil.createItem(CompatibleMaterial.MUSHROOM_STEW, tagTitle.replace("{1}", tag.getDisplayName()), Message.EDITOR_TAG_OVERVIEW_MENU_TAG_DESCRIPTION);
			setItem(getNormalIndex(size, 10, 2), tagItem);
			
			tags.add(tag);
		}
		
		if (isEmpty)
		{
			isEmpty = false;
			removeEmptyItem();
		}
		
		modified = true;
	}
	
	@Override
	public void onDelete (int slot)
	{
		super.onDelete(slot);
		
		int clampedIndex = getClampedIndex(slot, 10, 2);
		List<ParticleTag> tags = targetHat.getTags();
		
		tags.remove(clampedIndex);
			
		isEmpty = tags.size() == 0;
		if (isEmpty) {
			insertEmptyItem();
		}
		
		modified = true;
	}
	
	@Override
	public void onClose (boolean forced)
	{
		if (modified) {
			core.getDatabase().saveMetaData(menuBuilder.getMenuName(), targetHat, DataType.TAGS, -1);
		}
	}

	@Override
	public void build ()
	{
		super.build();
		
		setButton(46, backButton, backAction);
		setItem(49, ItemUtil.createItem(CompatibleMaterial.REDSTONE_TORCH, Message.EDITOR_TAG_OVERVIEW_MENU_INFO_TITLE, Message.EDITOR_TAG_OVERVIEW_MENU_INFO));
		
		setButton(52, addItem, (event, slot) ->
		{
			EditorTagMenu editorTagMenu = new EditorTagMenu(core, owner, menuBuilder, (tag) ->
			{
				onAdd(slot, (ParticleTag)tag);
			});
			menuBuilder.addMenu(editorTagMenu);
			editorTagMenu.open();
			return EditorClickType.NEUTRAL;
		});
		
		List<ParticleTag> tags = targetHat.getTags();
		for (int i = 0; i < tags.size(); i++)
		{
			ParticleTag tag = tags.get(i);
			ItemStack tagItem = ItemUtil.createItem(CompatibleMaterial.MUSHROOM_STEW, tagTitle.replace("{1}", tag.getDisplayName()), Message.EDITOR_TAG_OVERVIEW_MENU_TAG_DESCRIPTION);
			
			setItem(getNormalIndex(i, 10, 2), tagItem);
		}
		
		isEmpty = tags.size() == 0;
		if (isEmpty) {
			insertEmptyItem();
		}
	}
}
