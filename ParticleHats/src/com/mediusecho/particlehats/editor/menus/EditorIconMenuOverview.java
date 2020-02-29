package com.mediusecho.particlehats.editor.menus;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.database.Database.DataType;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.IconData;
import com.mediusecho.particlehats.particles.properties.IconData.ItemStackTemplate;
import com.mediusecho.particlehats.particles.properties.IconDisplayMode;
import com.mediusecho.particlehats.ui.AbstractListMenu;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorIconMenuOverview extends AbstractListMenu {

	private final EditorMenuManager editorManager;
	private final MenuObjectCallback callback;
	private final Hat targetHat;
	
	private boolean isModified = false;
	private int editingIndex = 0;
	
	private final Message iconTitle = Message.EDITOR_ICON_MENU_ITEM_TITLE;
	private final Message iconName = Message.EDITOR_ICON_MENU_ITEM_INFO;
	private final Message iconDescription = Message.EDITOR_ICON_MENU_ITEM_DESCRIPTION;
	
	public EditorIconMenuOverview(ParticleHats core, EditorMenuManager menuManager, Player owner, MenuObjectCallback callback) 
	{
		super(core, menuManager, owner, true);
		
		this.editorManager = menuManager;
		this.callback = callback;
		this.targetHat = editorManager.getBaseHat();
		this.totalPages = 1;
		
		setMenu(0, Bukkit.createInventory(null, 54, Message.EDITOR_ICON_OVERVIEW_MENU_TITLE.getValue()));
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
		setButton(0, 46, backButtonItem, backButtonAction);
		
		// Add Item
		ItemStack addItem = ItemUtil.createItem(CompatibleMaterial.TURTLE_HELMET, Message.EDITOR_ICON_MENU_ADD_ICON);
		setButton(0, 52, addItem, (event, slot) ->
		{
			editingIndex = getClampedIndex(slot, 10, 2);
			
			EditorItemPromptMenu editorItemPromptMenu = new EditorItemPromptMenu(core, editorManager, owner, iconTitle, iconName, iconDescription, (item) ->
			{
				editorManager.closeCurrentMenu();
				
				if (item == null) {
					return;
				}
				
				int size = targetHat.getIconData().getItems().size();
				if (size > 27) {
					return;
				}
				
				ItemStack i = ((ItemStack)item).clone();
				
				i.setAmount(1);
				
				ItemUtil.setNameAndDescription(i,
						Message.EDITOR_ICON_MENU_ITEM_PREFIX.getValue() + StringUtil.getMaterialName(i.getType()),
						StringUtil.parseDescription(Message.EDITOR_ICON_MENU_ICON_DESCRIPTION.getValue()));
				//ItemUtil.setItemType(i, i);
				
				targetHat.getIconData().addItem(i);
				
				setItem(0, getNormalIndex(size, 10, 2), i);
				isModified = true;
			});
			
			menuManager.addMenu(editorItemPromptMenu);
			editorItemPromptMenu.open();
			
			return MenuClickResult.NEUTRAL;
		});
		
		// Set Main Icon
		ItemStack mainItem = targetHat.getItem();
		ItemUtil.setItemName(mainItem, Message.EDITOR_ICON_MENU_SET_MAIN_ICON);
		setButton(0, 10, mainItem, (event, slot) ->
		{
			editingIndex = 0;
			
			EditorItemPromptMenu editorItemPromptMenu = new EditorItemPromptMenu(core, editorManager, owner, iconTitle, iconName, iconDescription, (item) ->
			{
				editorManager.closeCurrentMenu();
				
				if (item == null) {
					return;
				}
				
				ItemStack i = ((ItemStack)item).clone();
				i.setAmount(1);
				
				editorManager.getEditingMenu().setItemType(editorManager.getTargetSlot(), i);
				ItemUtil.setItemType(getItem(0, 10), i);
				
				callback.onSelect(i);
				targetHat.setItem(i);
				
				isModified = true;
			});
			
			menuManager.addMenu(editorItemPromptMenu);
			editorItemPromptMenu.open();
			
			return MenuClickResult.NEUTRAL;
		});
		
		// Preview Icon
		setItem(0, 48, ItemUtil.createItem(CompatibleMaterial.SUNFLOWER, Message.EDITOR_ICON_MENU_PREVIEW));
		
		// Display Mode
		ItemStack displayItem = ItemUtil.createItem(CompatibleMaterial.ROSE_RED, Message.EDITOR_ICON_MENU_SET_DISPLAY_MODE);
		EditorLore.updateDisplayModeDescription(displayItem, targetHat.getIconData().getDisplayMode(), Message.EDITOR_ICON_MENU_DISPLAY_MODE_DESCRIPTION);
		setButton(0, 50, displayItem, (event, slot) ->
		{				
			final int increment = event.isLeftClick() ? 1 : -1;
			final int modeID = MathUtil.wrap(targetHat.getDisplayMode().getID() + increment, IconDisplayMode.values().length, 0);
			final IconDisplayMode mode = IconDisplayMode.fromId(modeID);
			
			targetHat.setDisplayMode(mode);
			EditorLore.updateDisplayModeDescription(getItem(0, 50), mode, Message.EDITOR_ICON_MENU_DISPLAY_MODE_DESCRIPTION);
			return MenuClickResult.NEUTRAL;
		});
		
		// Update Frequency
		ItemStack frequencyItem = ItemUtil.createItem(CompatibleMaterial.REPEATER, Message.EDITOR_ICON_MENU_SET_UPDATE_FREQUENCY);
		EditorLore.updateFrequencyDescription(frequencyItem, targetHat.getIconUpdateFrequency(), Message.EDITOR_ICON_MENU_UPDATE_FREQUENCY_DESCRIPTION);
		setButton(0, 49, frequencyItem, (event, slot) ->
		{
			final int increment = event.isLeftClick() ? 1 : -1;
			final int frequency = (int) MathUtil.clamp(targetHat.getIconUpdateFrequency() + increment, 1, 63);
			
			targetHat.setIconUpdateFrequency(frequency);
			EditorLore.updateFrequencyDescription(getItem(0, 49), frequency, Message.EDITOR_ICON_MENU_UPDATE_FREQUENCY_DESCRIPTION);
			return MenuClickResult.NEUTRAL;
		});
		
		// Edit Action
		MenuAction editAction = (event, slot) ->
		{
			editingIndex = getClampedIndex(slot, 10, 2);
			if (event.isLeftClick())
			{
				EditorItemPromptMenu editorItemPromptMenu = new EditorItemPromptMenu(core, editorManager, owner, iconTitle, iconName, iconDescription, (item) ->
				{
					editorManager.closeCurrentMenu();
					
					if (item == null) {
						return;
					}
					
					ItemStack i = ((ItemStack)item).clone();
					i.setAmount(1);
					
					Material material = i.getType();
					String displayName = Message.EDITOR_ICON_MENU_ITEM_PREFIX.getValue() + StringUtil.getMaterialName(material);
					
					ItemStack currentItem = getItem(0, slot);
					ItemUtil.setItemType(currentItem, i);
					ItemUtil.setItemName(currentItem, displayName);
					
					targetHat.getIconData().updateItem(editingIndex, currentItem);
					isModified = true;
				});
				
				menuManager.addMenu(editorItemPromptMenu);
				editorItemPromptMenu.open();
			}
			
			else if (event.isShiftRightClick())
			{
				deleteSlot(0, slot);
				return MenuClickResult.NEGATIVE;
			}
			
			return MenuClickResult.NEUTRAL;
		};
		
		for (int i = 1; i < 28; i++) {
			setAction(getNormalIndex(i, 10, 2), editAction);
		}
		
		// Current Items
		List<ItemStackTemplate> items = targetHat.getIconData().getItems();
		for (int i = 1; i < items.size(); i++)
		{
			ItemStackTemplate itemTemplate = items.get(i);
			String displayName = Message.EDITOR_ICON_MENU_ITEM_PREFIX.getValue() + StringUtil.capitalizeFirstLetter(itemTemplate.getMaterial().toString().toLowerCase());
			
			int index = getNormalIndex(i, 10, 2);
			ItemStack item = ItemUtil.createItem(itemTemplate.getMaterial(), itemTemplate.getDurability());
			ItemUtil.setNameAndDescription(item, displayName, StringUtil.parseDescription(Message.EDITOR_ICON_MENU_ICON_DESCRIPTION.getValue()));
			
			setItem(0, index, item);
		}
	}

	@Override
	public void onClose(boolean forced) 
	{
		if (isModified)
		{
			core.getDatabase().saveMetaData(editorManager.getMenuName(), targetHat, DataType.ICON, 0);
			isModified = false;
		}
	}

	@Override
	public void onTick(int ticks) 
	{
		IconData iconData = targetHat.getIconData();
		if (iconData == null) {
			return;
		}
		
		ItemStackTemplate itemTemplate = iconData.getNextItem(ticks);
		ItemUtil.setItemType(getItem(0, 48), itemTemplate.getMaterial(), itemTemplate.getDurability());
	}
	
	@Override
	public void deleteSlot(int page, int slot)
	{
		super.deleteSlot(page, slot);
		
		// Remove the material in this slot
		targetHat.getIconData().removeItem(getClampedIndex(slot, 10, 2));
		isModified = true;
	}

}
