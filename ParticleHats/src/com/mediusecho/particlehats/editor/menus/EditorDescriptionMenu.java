package com.mediusecho.particlehats.editor.menus;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.database.Database.DataType;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.ui.AbstractListMenu;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorDescriptionMenu extends AbstractListMenu {

	private final EditorMenuManager editorManager;
	private final boolean isEditingDescription;
	private final Hat targetHat;
	
	private final ItemStack emptyItem = ItemUtil.createItem(CompatibleMaterial.BARRIER, Message.EDITOR_DESCRIPTION_MENU_EMPTY);
	
	private final String lineTitle = Message.EDITOR_DESCIPRION_LINE_TITLE.getValue();
	private final String lineDescription = Message.EDITOR_DESCRIPTION_MENU_LINE_DESCRIPTION.getValue();
	private final String[] descriptionInfo = StringUtil.parseValue(lineDescription, "1");
	
	private int editingLine = -1;
	private boolean isModified = false;
	
	public EditorDescriptionMenu(ParticleHats core, EditorMenuManager menuManager, Player owner, boolean isEditingDescription) 
	{
		super(core, menuManager, owner, true);
		
		this.editorManager = menuManager;
		this.isEditingDescription = isEditingDescription;
		this.targetHat = menuManager.getBaseHat();
		this.totalPages = 1;
		
		setMenu(0, Bukkit.createInventory(null, 54, Message.EDITOR_DESCRIPTION_MENU_TITLE.getValue()));
		
		build();
	}

	@Override
	public void insertEmptyItem() {
		setButton(0, 22, emptyItem, emptyAction);
	}

	@Override
	public void removeEmptyItem() {
		setButton(0, 22, null, emptyAction);
	}
	
	@Override
	public void open ()
	{
		if (editingLine != -1)
		{
			List<String> description = getDescription();
			String line = description.get(editingLine);
			ItemStack item = getItem(0, getNormalIndex(editingLine, 10, 2));
			
			setLineDescription(item, line);
			
			editingLine = -1;
			EditorLore.updatePreviewDecription(getItem(0, 49), description, targetHat);
		}
		
		super.open();
	}

	@Override
	protected void build() 
	{
		setButton(0, 46, backButtonItem, backButtonAction);
		
		// Preview
		ItemStack previewItem = ItemUtil.createItem(CompatibleMaterial.WRITABLE_BOOK, Message.EDITOR_DESCRIPTION_MENU_PREVIEW);
		EditorLore.updatePreviewDecription(previewItem, getDescription(), targetHat);
		setButton(0, 49, previewItem, (event, slot) ->
		{
			if (event.isShiftRightClick())
			{
				getDescription().clear();
				for (int i = 0; i <= 27; i++) {
					setItem(0, getNormalIndex(i, 10, 2), null);
				}
				setEmpty(true);
				
				EditorLore.updatePreviewDecription(getItem(0, 49), getDescription(), targetHat);
				isModified = true;
				
				return MenuClickResult.NEGATIVE;
			}
			return MenuClickResult.NONE;
		});
		
		// Add Line
		ItemStack addItem = ItemUtil.createItem(CompatibleMaterial.TURTLE_HELMET, Message.EDITOR_DESCRIPTION_MENU_ADD_LINE);
		setButton(0, 52, addItem, (event, slot) ->
		{
			List<String> description = getDescription();
			int size = description.size();
			
			if (size <= 27)
			{
				ItemStack item = ItemUtil.createItem(Material.PAPER, lineTitle.replace("{1}", Integer.toString(size + 1)));
				
				description.add("");
				EditorLore.updatePreviewDecription(getItem(0, 49), getDescription(), targetHat);
				
				setLineDescription(item, "");
				setItem(0, getNormalIndex(size, 10, 2), item);
			}
			
			setEmpty(false);
			isModified = true;
			
			return MenuClickResult.NEUTRAL;
		});
		
		final MenuAction editAction = (event, slot) ->
		{
			if (event.isLeftClick())
			{
				if (event.isShiftClick())
				{
					onInsert(slot);
					return MenuClickResult.NEUTRAL;
				}
				
				editingLine = getClampedIndex(slot, 10, 2);
				editorManager.getOwnerState().setMetaDescriptionLine(editingLine);
				
				MetaState metaState = isEditingDescription ? MetaState.HAT_DESCRIPTION : MetaState.HAT_PERMISSION_DESCRIPTION;
				
				editorManager.getOwnerState().setMetaState(metaState);
				core.prompt(owner, metaState);
				owner.closeInventory();
				isModified = true;
			}
			
			else if (event.isShiftRightClick())
			{
				deleteSlot(0, slot);
				isModified = true;
				return MenuClickResult.NEGATIVE;
			}
			
			return MenuClickResult.NEUTRAL;
		};
		
		for (int i = 0; i < 28; i++) {
			setAction(getNormalIndex(i, 10, 2), editAction);
		}
		
		// Description Lines
		List<String> description = getDescription();
		
		if (description.size() == 0) 
		{
			setEmpty(true);
			return;
		}
		
		for (int i = 0; i < description.size(); i++)
		{
			ItemStack lineItem = ItemUtil.createItem(Material.PAPER, lineTitle.replace("{1}", Integer.toString(i + 1)));
			String line = description.get(i);
			
			setLineDescription(lineItem, line);
			setItem(0, getNormalIndex(i, 10, 2), lineItem);
		}
	}

	@Override
	public void onClose(boolean forced) 
	{
		if (isModified)
		{
			Database database = core.getDatabase();
			DataType type = isEditingDescription ? DataType.DESCRIPTION : DataType.PERMISSION_DESCRIPTION;
			String menuName = editorManager.getMenuName();
			
			database.saveMetaData(menuName, targetHat, type, 0);
		}
	}
	
	@Override
	public void deleteSlot (int page, int slot)
	{
		super.deleteSlot(page, slot);
		
		int clampedIndex = getClampedIndex(slot, 10, 2);
		getDescription().remove(clampedIndex);
		EditorLore.updatePreviewDecription(getItem(0, 49), getDescription(), targetHat);
		
		for (int i = clampedIndex; i <= 27; i++)
		{			
			ItemStack item = getItem(0, getNormalIndex(i, 10, 2));
			if (item == null) {
				continue;
			}
			ItemUtil.setItemName(item, lineTitle.replace("{1}", Integer.toString(i + 1)));
		}
		
		if (getDescription().size() == 0) {
			setEmpty(true);
		}
	}
	
	/**
	 * Get the hat's description depending on the <b>isEditingDescription</b> value
	 * @return
	 */
	private List<String> getDescription () {
		return isEditingDescription ? targetHat.getDescription() : targetHat.getPermissionDescription();
	}
	
	/**
	 * Adds a line to this item's description
	 * @param item
	 * @param line
	 */
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

	private void onInsert (int slot)
	{
		List<String> description = getDescription();
		int size = description.size();
		
		if (size <= 27)
		{
			int index = getClampedIndex(slot, 10, 2) + 1;
			description.add(index, "");
			
			for (int i = size - 1; i >= index; i--)
			{
				int fromSlot = getNormalIndex(i, 10, 2);
				int toSlot = getNormalIndex(i + 1, 10, 2);
				
				ItemStack item = getItem(0, fromSlot);
				ItemUtil.setItemName(item, lineTitle.replace("{1}", Integer.toString(i + 2)));
				
				setItem(0, fromSlot, null);
				setItem(0, toSlot, item);
			}
			
			ItemStack item = ItemUtil.createItem(Material.PAPER, lineTitle.replace("{1}", Integer.toString(index + 1)));
			EditorLore.updatePreviewDecription(getItem(0, 49), getDescription(), targetHat);
			
			setLineDescription(item, "");
			setItem(0, getNormalIndex(index, 10, 2), item);
		}
	}
	
}
