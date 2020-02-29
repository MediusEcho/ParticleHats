package com.mediusecho.particlehats.editor.menus;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.ui.AbstractStaticMenu;
import com.mediusecho.particlehats.ui.MenuManager;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorResizeMenu extends AbstractStaticMenu {

	private final EditorBaseMenu editorBaseMenu;
	
	public EditorResizeMenu(ParticleHats core, MenuManager menuManager, Player owner, EditorBaseMenu editorBaseMenu) 
	{
		super(core, menuManager, owner);
		
		this.editorBaseMenu = editorBaseMenu;
		this.inventory = Bukkit.createInventory(null, 27, Message.EDITOR_RESIZE_MENU_TITLE.getValue());
		
		build();
	}

	@Override
	protected void build() 
	{
		final MenuAction resizeAction = (event, slot) ->
		{
			int size = (slot - 10) + (slot < 13 ? 1 : 0);
			
			editorBaseMenu.resizeTo(size);
			menuManager.closeCurrentMenu();
			return MenuClickResult.NEUTRAL;
		};
		
		String title = Message.EDITOR_RESIZE_MENU_SET_ROW_SIZE.getValue();
		List<String> description = StringUtil.parseDescription(Message.EDITOR_RESIZE_MENU_SET_ROW_DESCRIPTION.getValue());
		String suffixInfo[] = StringUtil.parseValue(title, "2");
		
		setButton(13, backButtonItem, backButtonAction);
		for (int i = 0; i < 7; i++)
		{
			if (i == 3) {
				continue;
			}
			
			String t = title.replace("{1}", Integer.toString((i + 1) - (i > 3 ? 1 : 0)))
				.replace(suffixInfo[0], i == 0 ? "" : suffixInfo[1]);
			
			ItemStack row = ItemUtil.createItem(CompatibleMaterial.GRAY_DYE, t, description);
			setButton(i + 10, row, resizeAction);
		}
		
		int currentRows = editorBaseMenu.rows();
		ItemStack row = getItem(currentRows + 10 - (currentRows < 3 ? 1 : 0));

		ItemUtil.setItemType(row, CompatibleMaterial.LIME_DYE);
		ItemUtil.highlightItem(row);
	}

	@Override
	public void onClose(boolean forced) {}

	@Override
	public void onTick(int ticks) {}

}
