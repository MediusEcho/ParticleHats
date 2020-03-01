package com.mediusecho.particlehats.editor.menus;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.database.Database.DataType;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.ParticleTag;
import com.mediusecho.particlehats.ui.menus.ListMenu;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.ui.properties.MenuContentRegion;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorTagMenuOverview extends ListMenu {

	private final EditorMenuManager editorManager;
	private final Hat targetHat;
	private final String tagTitle = Message.EDITOR_TAG_OVERVIEW_MENU_TAG_TITLE.getValue();
	private final ItemStack emptyItem = ItemUtil.createItem(CompatibleMaterial.BARRIER, Message.EDITOR_TAG_OVERVIEW_MENU_EMPTY);
	
	private boolean isModified = false;
	
	public EditorTagMenuOverview(ParticleHats core, EditorMenuManager menuManager, Player owner) 
	{
		super(core, menuManager, owner, MenuContentRegion.defaultLayout);
		
		this.editorManager = menuManager;
		this.targetHat = menuManager.getBaseHat();
		
		setInventory(0, Bukkit.createInventory(null, 54, Message.EDITOR_TAG_OVERVIEW_MENU_TITLE.getValue()));
		
		build();
	}

	@Override
	public void insertEmptyItem() {
		setItem(0, 22, emptyItem);
	}

	@Override
	public void removeEmptyItem() {
		setItem(0, 22, null);
	}

	@Override
	protected void build() 
	{
		setButton(0, 46, backButtonItem, backButtonAction);
		setItem(0, 49, ItemUtil.createItem(CompatibleMaterial.REDSTONE_TORCH, Message.EDITOR_TAG_OVERVIEW_MENU_INFO_TITLE, Message.EDITOR_TAG_OVERVIEW_MENU_INFO));

		// Add Tag
		ItemStack addItem = ItemUtil.createItem(CompatibleMaterial.TURTLE_HELMET, Message.EDITOR_TAG_OVERVIEW_MENU_ADD_TAG);
		setButton(0, 52, addItem, (event, slot) ->
		{
			EditorTagMenu editorTagMenu = new EditorTagMenu(core, editorManager, owner, (tagName) ->
			{
				if (tagName == null) {
					return;
				}
				
				ParticleTag tag = (ParticleTag)tagName;
				List<ParticleTag> tags = targetHat.getTags();
				
				if (tags.contains(tag)) 
				{
					menuManager.closeCurrentMenu();
					return;
				}
				
				int size = tags.size();
				if (size < 28)
				{
					ItemStack tagItem = ItemUtil.createItem(CompatibleMaterial.MUSHROOM_STEW, tagTitle.replace("{1}", tag.getDisplayName()), Message.EDITOR_TAG_OVERVIEW_MENU_TAG_DESCRIPTION);
					setItem(0, getNormalIndex(size, 10, 2), tagItem);
					
					tags.add(tag);
				}
				
				if (isEmpty) {
					setEmpty(false);
				}
				
				isModified = true;
				menuManager.closeCurrentMenu();
			});
			
			menuManager.addMenu(editorTagMenu);
			editorTagMenu.open();
			return MenuClickResult.NEUTRAL;
		});
		
		// Edit Action
		final MenuAction editAction = (event, slot) ->
		{
			if (event.isShiftRightClick())
			{
				deleteItem(0, slot);
				return MenuClickResult.NEGATIVE;
			}
			return MenuClickResult.NONE;
		};
		
		contentRegion.fillRegion(this, editAction);
		
		// Tags
		List<ParticleTag> tags = targetHat.getTags();
		
		if (tags.isEmpty()) 
		{
			setEmpty(true);
			return;
		}
		
		for (int i = 0; i < tags.size(); i++)
		{
			ParticleTag tag = tags.get(i);
			ItemStack tagItem = ItemUtil.createItem(CompatibleMaterial.MUSHROOM_STEW, tagTitle.replace("{1}", tag.getDisplayName()), Message.EDITOR_TAG_OVERVIEW_MENU_TAG_DESCRIPTION);
			
			setItem(0, contentRegion.getNormalIndex(i), tagItem);
		}
	}

	@Override
	public void onClose(boolean forced) 
	{
		if (isModified) {
			core.getDatabase().saveMetaData(editorManager.getMenuName(), targetHat, DataType.TAGS, -1);
		}
	}
	
	@Override
	public void deleteItem (int page, int slot)
	{
		super.deleteItem(page, slot);
		
		int clampedIndex = contentRegion.getClampedIndex(slot);
		List<ParticleTag> tags = targetHat.getTags();
		
		tags.remove(clampedIndex);
		
		if (tags.isEmpty()) {
			setEmpty(true);
		}
		
		isModified = true;
	}

}
