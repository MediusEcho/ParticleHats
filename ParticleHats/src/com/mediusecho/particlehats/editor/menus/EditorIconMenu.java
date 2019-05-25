package com.mediusecho.particlehats.editor.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorIconMenu extends EditorMenu {

	private final EditorItemCallback itemCallback;
	
	private final Message itemName;
	private final Message itemDescription;
	private boolean searching = false;
	
	public EditorIconMenu(ParticleHats core, Player owner, MenuBuilder menuBuilder, Message title, Message name, Message description, EditorItemCallback itemCallback) 
	{
		super(core, owner, menuBuilder);
		this.itemName = name;
		this.itemDescription = description;
		this.itemCallback = itemCallback;
		
		inventory = Bukkit.createInventory(null, 27, title.getValue());
		build();
	}
	
	/**
	 * Called any time we click outside of this menu
	 */
	@Override
	public EditorClickType onClickOutside (InventoryClickEvent event, final int slot)
	{
		itemCallback.onSelect(event.getCurrentItem());	
		menuBuilder.goBack();
		return EditorClickType.NEUTRAL;
	}
	
	@Override
	public void open ()
	{
		super.open();
		
		if (searching)
		{
			String blockName = menuBuilder.getMetaArgument();
			if (!blockName.equals("")) 
			{
				menuBuilder.resetMetaArgument();
				EditorSearchMenu editorSearchMenu = new EditorSearchMenu(core, owner, menuBuilder, blockName, (item) ->
				{
					itemCallback.onSelect(item);
					menuBuilder.goBack();
				});
				menuBuilder.addMenu(editorSearchMenu);
				editorSearchMenu.open();
			}
			
			searching = false;
		}
	}

	@Override
	protected void build() 
	{
		ItemStack info = ItemUtil.createItem(CompatibleMaterial.LIGHT_GRAY_STAINED_GLASS_PANE, itemName);
		ItemUtil.setItemDescription(info, StringUtil.parseDescription(itemDescription.getValue()));
		
		for (int i = 0; i < inventory.getSize(); i++)
		{
			if (i == 13) {
				continue;
			}
			setItem(i, info);
		}
		
		setButton(26, ItemUtil.createItem(Material.SIGN, Message.EDITOR_MISC_SEARCH), (event, slot) ->
		{
			searching = true;
			menuBuilder.setOwnerState(MetaState.BLOCK_SEARCH);
			core.prompt(owner, MetaState.BLOCK_SEARCH);
			owner.closeInventory();
			
			return EditorClickType.NEUTRAL;
		});
		
		setButton(13, backButton, backAction);
	}

}
