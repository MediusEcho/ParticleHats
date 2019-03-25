package com.mediusecho.particlehats.editor.menus;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.database.Database.DataType;
import com.mediusecho.particlehats.editor.EditorListMenu;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorDescriptionMenu extends EditorListMenu {

	private final boolean editingDescription;
	private final Hat targetHat;
	
	private final String lineTitle = Message.EDITOR_DESCIPRION_LINE_TITLE.getValue();
	private final String lineDescription = Message.EDITOR_DESCRIPTION_MENU_LINE_DESCRIPTION.getValue();
	private final String[] descriptionInfo = StringUtil.parseValue(lineDescription, "1");
	
	private int editingLine = -1;
	private boolean isModified = false;
	
	public EditorDescriptionMenu(Core core, Player owner, MenuBuilder menuBuilder, boolean editingDescription) 
	{
		super(core, owner, menuBuilder);
		this.editingDescription = editingDescription;
		this.targetHat = menuBuilder.getBaseHat();
		
		addItem = ItemUtil.createItem(Material.TURTLE_HELMET, Message.EDITOR_DESCRIPTION_MENU_ADD_LINE);
		editAction = (event, slot) ->
		{
			if (event.isLeftClick())
			{
				editingLine = getClampedIndex(slot, 10, 2);
				menuBuilder.getOwnerState().setMetaDescriptionLine(editingLine);
				
				MetaState metaState = editingDescription ? MetaState.HAT_DESCRIPTION : MetaState.HAT_PERMISSION_DESCRIPTION;
				
				menuBuilder.setOwnerState(metaState);
				core.prompt(owner, metaState);
				owner.closeInventory();
				
				isModified = true;
			}

			else if (event.isShiftRightClick())
			{
				onDelete(slot);
				isModified = true;
				return EditorClickType.NEGATIVE;
			}
			
			return EditorClickType.NEUTRAL;
		};
		
		inventory = Bukkit.createInventory(null, 54, Message.EDITOR_DESCRIPTION_MENU_TITLE.getValue());
		build();
	}
	
	@Override
	public void open ()
	{
		if (editingLine != -1)
		{
			List<String> description = getDescription();
			String line = description.get(editingLine);
			ItemStack item = getItem(getNormalIndex(editingLine, 10, 2));
			
			setLineDescription(item, line);
			
			editingLine = -1;
			EditorLore.updatePreviewDecription(getItem(49), description);
		}
		
		super.open();
	}
	
	@Override
	public void onClose (boolean forced)
	{
		if (isModified)
		{
			Database database = core.getDatabase();
			DataType type = editingDescription ? DataType.DESCRIPTION : DataType.PERMISSION_DESCRIPTION;
			String menuName = menuBuilder.getEditingMenu().getName();
			
			database.saveMetaData(menuName, targetHat, type, 0);
		}
	}

	private List<String> getDescription () {
		return editingDescription ? targetHat.getDescription() : targetHat.getPermissionDescription();
	}
	
	private void setLineDescription (ItemStack item, String line)
	{
		String prefix = "";
		if (!line.isEmpty() && line.charAt(0) != '&') {
			prefix = "&5&o";
		}
		
		String s = line.equals("") ? descriptionInfo[1] : prefix + line;
		String d = lineDescription.replace(descriptionInfo[0], s);
		
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(d));
	}
	
	//@Override
	protected void onAdd ()
	{		
		List<String> description = getDescription();
		int size = description.size();
		
		if (size <= 27)
		{
			ItemStack item = ItemUtil.createItem(Material.PAPER, lineTitle.replace("{1}", Integer.toString(size + 1)));
			
			description.add("");
			EditorLore.updatePreviewDecription(getItem(49), getDescription());
			
			setLineDescription(item, "");
			setItem(getNormalIndex(size, 10, 2), item);
		}
		
		if (isEmpty)
		{
			isEmpty = false;
			removeEmptyItem();
		}
		
		isModified = true;
	}
	
	@Override
	public void onDelete (int slot)
	{
		super.onDelete(slot);
		
		int clampedIndex = getClampedIndex(slot, 10, 2);
		getDescription().remove(clampedIndex);
		EditorLore.updatePreviewDecription(getItem(49), getDescription());
		
		for (int i = clampedIndex; i <= 27; i++)
		{
			EditorAction action = getAction(i);
			if (action == addAction) {
				break;
			}
			
			ItemStack item = getItem(getNormalIndex(i, 10, 2));
			ItemUtil.setItemName(item, lineTitle.replace("{1}", Integer.toString(i + 1)));
		}
		
		isEmpty = getDescription().size() == 0;
		if (isEmpty) {
			insertEmptyItem();
		}
	}
	
	@Override
	protected void build() 
	{
		super.build();
		
		setButton(46, backButton, backAction);
		
		ItemStack previewItem = ItemUtil.createItem(Material.WRITABLE_BOOK, Message.EDITOR_DESCRIPTION_MENU_PREVIEW);
		EditorLore.updatePreviewDecription(previewItem, getDescription());
		setButton(49, previewItem, (event, slot) ->
		{
			if (event.isShiftRightClick())
			{
				getDescription().clear();
				for (int i = 0; i <= 27; i++) {
					setItem(getNormalIndex(i, 10, 2), null);
				}
				
				EditorLore.updatePreviewDecription(getItem(49), getDescription());
				insertEmptyItem();
				isModified = true;
				
				return EditorClickType.NEGATIVE;
			}
			return EditorClickType.NONE;
		});
		
		setButton(52, addItem, (event, slot) ->
		{
			onAdd();
			return EditorClickType.NEUTRAL;
		});
		
		List<String> description = getDescription();
		for (int i = 0; i < description.size(); i++)
		{
			ItemStack lineItem = ItemUtil.createItem(Material.PAPER, lineTitle.replace("{1}", Integer.toString(i + 1)));
			String line = description.get(i);
			
			setLineDescription(lineItem, line);
			setItem(getNormalIndex(i, 10, 2), lineItem);
		}
		
		isEmpty = description.size() == 0;
		if (isEmpty) {
			insertEmptyItem();
		}
	}
}
