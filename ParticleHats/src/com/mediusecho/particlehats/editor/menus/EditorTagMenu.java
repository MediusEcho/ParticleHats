package com.mediusecho.particlehats.editor.menus;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.properties.ParticleTag;
import com.mediusecho.particlehats.ui.AbstractListMenu;
import com.mediusecho.particlehats.ui.MenuManager;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorTagMenu extends AbstractListMenu {
	
	private final String tagTitle = Message.EDITOR_TAG_MENU_TAG_TITLE.getValue();
	private final MenuAction selectAction;
	
	private Map<Integer, ParticleTag> storedTags;
	
	public EditorTagMenu(ParticleHats core, MenuManager menuManager, Player owner, MenuObjectCallback callback) 
	{
		super(core, menuManager, owner, false);
		
		this.storedTags = new HashMap<Integer, ParticleTag>();
		this.totalPages = MathUtil.calculatePageCount(ParticleTag.values().length, 28);
		
		this.selectAction = (event, slot) ->
		{
			int index = getClampedIndex(slot, 10, 2);

			if (storedTags.containsKey(index)) 
			{
				callback.onSelect(storedTags.get(index));
				return MenuClickResult.NEUTRAL;
			}

			return MenuClickResult.NONE;
		};
		
		build();
	}

	@Override
	public void insertEmptyItem() {
		
	}

	@Override
	public void removeEmptyItem() {
		
	}

	@Override
	protected void build() 
	{
		String title = Message.EDITOR_TAG_MENU_TITLE.getValue();
		
		for (int i = 0; i < totalPages; i++)
		{
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
		
		setAction(49, backButtonAction);
		
		int index = 0;
		int page = 0;
		
		for (ParticleTag tag : ParticleTag.values())
		{
			if (tag == ParticleTag.NONE || tag == ParticleTag.CUSTOM) {
				continue;
			}
			
			ItemStack tagItem = ItemUtil.createItem(CompatibleMaterial.MUSHROOM_STEW, tagTitle.replace("{1}", tag.getDisplayName()), StringUtil.parseDescription(tag.getDescription()));			
			setItem(page, getNormalIndex(index, 10, 2), tagItem);
			
			storedTags.put(index++, tag);
			
			if (index % 28 == 0)
			{
				index = 0;
				page++;
			}
		}
		
		for (int i = 0; i < 28; i++) {
			setAction(getNormalIndex(i, 10, 2), selectAction);
		}
	}

	@Override
	public void onClose(boolean forced) {
		
	}

	@Override
	public void onTick(int ticks) {
		
	}

}
