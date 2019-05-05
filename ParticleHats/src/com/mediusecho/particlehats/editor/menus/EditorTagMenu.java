package com.mediusecho.particlehats.editor.menus;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.properties.ParticleTag;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorTagMenu extends EditorMenu {

	private final EditorAction selectAction;
	private final Map<Integer, ParticleTag> tags;
	
	private final String tagTitle = Message.EDITOR_TAG_MENU_TAG_TITLE.getValue();
	
	public EditorTagMenu(ParticleHats core, Player owner, MenuBuilder menuBuilder, EditorObjectCallback callback) 
	{
		super(core, owner, menuBuilder);
		tags = new HashMap<Integer, ParticleTag>();
		
		selectAction = (event, slot) ->
		{
			int index = getClampedIndex(slot, 10, 2);
			if (tags.containsKey(index)) 
			{
				callback.onSelect(tags.get(index));
				menuBuilder.goBack();
				return EditorClickType.NEUTRAL;
			}
			return EditorClickType.NONE;
		};
		
		inventory = Bukkit.createInventory(null, 54, Message.EDITOR_TAG_MENU_TITLE.getValue());
		build();
	}

	@Override
	protected void build() 
	{
		setButton(49, backButton, backAction);
		
		int index = 0;
		for (ParticleTag tag : ParticleTag.values())
		{
			if (tag == ParticleTag.NONE || tag == ParticleTag.CUSTOM) {
				continue;
			}
			
			ItemStack tagItem = ItemUtil.createItem(CompatibleMaterial.MUSHROOM_STEW, tagTitle.replace("{1}", tag.getDisplayName()), StringUtil.parseDescription(tag.getDescription()));			
			setButton(getNormalIndex(index, 10, 2), tagItem, selectAction);
			
			tags.put(index++, tag);
		}
	}
}
