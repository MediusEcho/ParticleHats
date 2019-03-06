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
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.IconData;
import com.mediusecho.particlehats.particles.properties.IconDisplayMode;
import com.mediusecho.particlehats.particles.properties.ParticleMode;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorIconOverviewMenu extends EditorListMenu {

	private final Hat targetHat;
	private final EditorItemCallback callback;
	
	private boolean isModified = false;
	private int editingIndex = 0;
	
	private final Message iconTitle = Message.EDITOR_ICON_MENU_ITEM_TITLE;
	private final Message iconName = Message.EDITOR_ICON_MENU_ITEM_INFO;
	private final Message iconDescription = Message.EDITOR_ICON_MENU_ITEM_DESCRIPTION;
	
	public EditorIconOverviewMenu(Core core, Player owner, MenuBuilder menuBuilder, EditorItemCallback callback) 
	{
		super(core, owner, menuBuilder);
		this.callback = callback;
		targetHat = menuBuilder.getBaseHat();
		
		addItem = ItemUtil.createItem(Material.TURTLE_HELMET, Message.EDITOR_ICON_MENU_ADD_ICON);
		addAction = (event, slot) ->
		{
			editingIndex = getClampedIndex(slot, 10, 2);
			EditorIconMenu editorIconMenu = new EditorIconMenu(core, owner, menuBuilder, iconTitle, iconName, iconDescription, (item) ->
			{
				onAdd(slot, item);
			});
			
			menuBuilder.addMenu(editorIconMenu);
			editorIconMenu.open();
			return EditorClickType.NEUTRAL;
		};
		
		editAction = (event, slot) ->
		{
			editingIndex = getClampedIndex(slot, 10, 2);
			if (event.isLeftClick())
			{
				EditorIconMenu editorIconMenu = new EditorIconMenu(core, owner, menuBuilder, iconTitle, iconName, iconDescription, (item) ->
				{
					Material material = item.getType();
					String displayName = Message.EDITOR_ICON_MENU_ITEM_PREFIX.getValue() + StringUtil.getMaterialName(material);
					ItemStack i = getItem(slot);
				
					i.setType(material);
					ItemUtil.setItemName(i, displayName);
					
					targetHat.getIconData().updateMaterial(editingIndex, material);
					isModified = true;
				});
				menuBuilder.addMenu(editorIconMenu);
				editorIconMenu.open();
			}
			
			else if (event.isShiftRightClick()) 
			{
				onDelete(slot);
				return EditorClickType.NEGATIVE;
			}
			return EditorClickType.NEUTRAL;
		};
		
		inventory = Bukkit.createInventory(null, 54, Message.EDITOR_ICON_OVERVIEW_MENU_TITLE.getValue());
		build();
	}
	
	@Override
	public void onTick (int ticks)
	{
		IconData data = targetHat.getIconData();
		if (data != null)
		{
			Material material = data.getNextMaterial(ticks);
			if (material != null) {
				getItem(48).setType(material);
			}
		}
	}
	
	@Override
	public void onClose (boolean forced)
	{
		if (isModified)
		{
			Database database = core.getDatabase();
			String menuName = menuBuilder.getEditingMenu().getName();
			
			database.saveMetaData(menuName, targetHat, DataType.ICON);
		}
	}

	@Override
	protected void build() 
	{
		super.build();
		
		setButton(46, backButton, backAction);
		
		setButton(52, addItem, (event, slot) ->
		{
			editingIndex = getClampedIndex(slot, 10, 2);
			EditorIconMenu editorIconMenu = new EditorIconMenu(core, owner, menuBuilder, iconTitle, iconName, iconDescription, (item) ->
			{
				onAdd(slot, item);
			});
			
			menuBuilder.addMenu(editorIconMenu);
			editorIconMenu.open();
			return EditorClickType.NEUTRAL;
		});
		
		// Set Main Icon
		ItemStack mainItem = ItemUtil.createItem(targetHat.getMaterial(), Message.EDITOR_ICON_MENU_SET_MAIN_ICON);
		setButton(10, mainItem, (event, slot) ->
		{
			editingIndex = 0;
			EditorIconMenu editorIconMenu = new EditorIconMenu(core, owner, menuBuilder, iconTitle, iconName, iconDescription, (item) ->
			{
				Material material = item.getType();
				targetHat.setMaterial(material);
				menuBuilder.getEditingMenu().setItemMaterial(menuBuilder.getTargetSlot(), material);
				
				getItem(10).setType(material);
				callback.onSelect(item);
				targetHat.setMaterial(material);
			});
			menuBuilder.addMenu(editorIconMenu);
			editorIconMenu.open();
			return EditorClickType.NEUTRAL;
		});
		
		// Preview Icon
		setItem(48, ItemUtil.createItem(Material.SUNFLOWER, Message.EDITOR_ICON_MENU_PREVIEW));
		
		// Display Mode 48
		ItemStack displayItem = ItemUtil.createItem(Material.ROSE_RED, Message.EDITOR_ICON_MENU_SET_DISPLAY_MODE);
		EditorLore.updateDisplayModeDescription(displayItem, targetHat.getIconData().getDisplayMode(), Message.EDITOR_ICON_MENU_DISPLAY_MODE_DESCRIPTION);
		setButton(50, displayItem, (event, slot) ->
		{			
			final int increment = event.isLeftClick() ? 1 : -1;
			final int modeID = MathUtil.wrap(targetHat.getDisplayMode().getID() + increment, ParticleMode.values().length, 0);
			final IconDisplayMode mode = IconDisplayMode.fromId(modeID);
			
			targetHat.setDisplayMode(mode);
			EditorLore.updateDisplayModeDescription(getItem(50), mode, Message.EDITOR_ICON_MENU_DISPLAY_MODE_DESCRIPTION);
			return EditorClickType.NEUTRAL;
		});
		
		// Update Frequency
		ItemStack frequencyItem = ItemUtil.createItem(Material.REPEATER, Message.EDITOR_ICON_MENU_SET_UPDATE_FREQUENCY);
		EditorLore.updateFrequencyDescription(frequencyItem, targetHat.getIconUpdateFrequency(), Message.EDITOR_ICON_MENU_UPDATE_FREQUENCY_DESCRIPTION);
		setButton(49, frequencyItem, (event, slot) ->
		{
			final int increment = event.isLeftClick() ? 1 : -1;
			final int frequency = (int) MathUtil.clamp(targetHat.getIconUpdateFrequency() + increment, 1, 63);
			
			targetHat.setIconUpdateFrequency(frequency);
			EditorLore.updateFrequencyDescription(getItem(49), frequency, Message.EDITOR_ICON_MENU_UPDATE_FREQUENCY_DESCRIPTION);
			return EditorClickType.NEUTRAL;
		});
		
		// Add Item
		List<Material> materials = targetHat.getIconData().getMaterials();
		for (int i = 1; i < materials.size(); i++) 
		{
			Material material = materials.get(i);
			String displayName = Message.EDITOR_ICON_MENU_ITEM_PREFIX.getValue() + StringUtil.capitalizeFirstLetter(material.toString().toLowerCase());
			
			int index = getNormalIndex(i, 10, 2);
			setItem(index, ItemUtil.createItem(material, displayName, StringUtil.parseDescription(Message.EDITOR_ICON_MENU_ICON_DESCRIPTION.getValue())));
			//setAction(index, editAction);
		}
	}
	
	/**
	 * Adds a new material to our IconData
	 * @param slot
	 * @param material
	 */
	private void onAdd (int slot, ItemStack item)
	{		
		int size = targetHat.getIconData().getMaterials().size();
		if (size <= 27)
		{
			Material material = item.getType();
			String displayName = Message.EDITOR_ICON_MENU_ITEM_PREFIX.getValue() + StringUtil.getMaterialName(material);
			ItemStack i = ItemUtil.createItem(material, displayName, StringUtil.parseDescription(Message.EDITOR_ICON_MENU_ICON_DESCRIPTION.getValue()));
		
			targetHat.getIconData().addMaterial(material);
			setItem(getNormalIndex(size, 10, 2), i);
			
			isModified = true;
		}
	}
	
	/**
	 * Removes a material at the given slot
	 * @param slot
	 */
	@Override
	protected void onDelete (int slot)
	{
		super.onDelete(slot);
		
		// Remove the material in this slot
		targetHat.getIconData().removeMaterial(getClampedIndex(slot, 10, 2));
		isModified = true;
	}
}
