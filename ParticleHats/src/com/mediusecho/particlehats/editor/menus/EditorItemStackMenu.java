package com.mediusecho.particlehats.editor.menus;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.database.Database.DataType;
import com.mediusecho.particlehats.editor.EditorListMenu;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.ItemStackData;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorItemStackMenu extends EditorListMenu {

	private final Hat targetHat;
	private final int particleIndex;
	
	private boolean dataModified = false;
	private boolean itemModified = false;
	
	private final Message iconTitle = Message.EDITOR_ICON_MENU_ITEM_TITLE;
	private final Message iconName = Message.EDITOR_ICON_MENU_ITEM_INFO;
	private final Message iconDescription = Message.EDITOR_ICON_MENU_ITEM_DESCRIPTION;
	
	public EditorItemStackMenu(Core core, Player owner, MenuBuilder menuBuilder, final int particleIndex) 
	{
		super(core, owner, menuBuilder);
		targetHat = menuBuilder.getTargetHat();
		this.particleIndex = particleIndex;
		
		editAction = (event, slot) ->
		{
			if (event.isLeftClick())
			{				
				EditorIconMenu editorIconMenu = new EditorIconMenu(core, owner, menuBuilder, iconTitle, iconName, iconDescription, (item) ->
				{
					Material material = item.getType();
					String displayName = Message.EDITOR_ICON_MENU_ITEM_PREFIX.getValue() + StringUtil.getMaterialName(material);
					ItemStack i = getItem(slot);
				
					i.setType(material);
					ItemUtil.setItemName(i, displayName);
					
					targetHat.getItemStackData(particleIndex).updateItem(getClampedIndex(slot, 10, 2), item);
					itemModified = true;
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
		
		inventory = Bukkit.createInventory(null, 54, Message.EDITOR_ITEMSTACK_MENU_TITLE.getValue());
		build();
	}
	
	private void onVelocityChange ()
	{
		dataModified = true;
		EditorLore.updateVectorDescription(getItem(48), targetHat.getItemStackData(particleIndex).getVelocity(), Message.EDITOR_ITEMSTACK_MENU_VELOCITY_DESCRIPTION);
	}
	
	@Override
	public void onClose (boolean forced)
	{
		String name = menuBuilder.getEditingMenu().getName();
		
		if (dataModified) {
			core.getDatabase().saveParticleData(name, targetHat, particleIndex);
		}
		
		if (itemModified) {
			core.getDatabase().saveMetaData(name, targetHat, DataType.ITEMSTACK, particleIndex);
		}
	}
	
	public void onAdd (int slot, ItemStack item)
	{
		int size = targetHat.getItemStackData(particleIndex).getItems().size();
		if (size <= 27)
		{
			Material material = item.getType();
			String displayName = Message.EDITOR_ICON_MENU_ITEM_PREFIX.getValue() + StringUtil.getMaterialName(material);
			ItemStack i = ItemUtil.createItem(material, displayName, StringUtil.parseDescription(Message.EDITOR_ICON_MENU_ICON_DESCRIPTION.getValue()));
			
			targetHat.getItemStackData(particleIndex).addItem(item);
			setItem(getNormalIndex(size, 10, 2), i);
			
			itemModified = true;
		}
	}
	
	@Override
	public void onDelete (int slot)
	{
		super.onDelete(slot);
		
		targetHat.getItemStackData(particleIndex).removeItem(getClampedIndex(slot, 10, 2));
		itemModified = true;
	}

	@Override
	public void build ()
	{
		super.build();
		setButton(46, backButton, backAction);
		
		ItemStack velocityItem = ItemUtil.createItem(Material.ARROW, Message.EDITOR_ITEMSTACK_MENU_SET_VELOCITY);
		EditorLore.updateVectorDescription(velocityItem, targetHat.getItemStackData(particleIndex).getVelocity(), Message.EDITOR_ITEMSTACK_MENU_VELOCITY_DESCRIPTION);
		setButton(48, velocityItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorVelocityMenu editorVelocityMenu = new EditorVelocityMenu(core, owner, menuBuilder, particleIndex, () ->
				{
					onVelocityChange();
				});
				menuBuilder.addMenu(editorVelocityMenu);
				editorVelocityMenu.open();
			}
			
			else if (event.isShiftRightClick())
			{
				targetHat.getItemStackData(particleIndex).setVelocity(0, 0, 0);
				onVelocityChange();
			}
			return EditorClickType.NEUTRAL;
		});
		
		ItemStack gravityItem = ItemUtil.createItem(Material.LEATHER_BOOTS, Message.EDITOR_ITEMSTACK_MENU_TOGGLE_GRAVITY);
		EditorLore.updateGravityDescription(gravityItem, targetHat.getItemStackData(particleIndex).hasGravity());
		setButton(49, gravityItem, (event, slot) ->
		{
			ItemStackData data = targetHat.getItemStackData(particleIndex);
			data.setGravity(!data.hasGravity());
			dataModified = true;
			
			EditorLore.updateGravityDescription(getItem(49), data.hasGravity());
			return EditorClickType.NEUTRAL;
		});
		
		ItemStack durationItem = ItemUtil.createItem(Material.FIREWORK_STAR, Message.EDITOR_ITEMSTACK_MENU_SET_DURATION);
		EditorLore.updateDurationDescription(durationItem, targetHat.getItemStackData(particleIndex).getDuration(), Message.EDITOR_ITEMSTACK_MENU_DURATION_DESCRIPTION);
		setButton(50, durationItem, (event, slot) ->
		{
			int normalClick    = event.isLeftClick() ? 20 : -20;
			int shiftClick     = event.isShiftClick() ? 30 : 1;
			int modifier       = normalClick * shiftClick;
			
			ItemStackData data = targetHat.getItemStackData(particleIndex);
			int duration = data.getDuration() + modifier;
			data.setDuration(duration);
			dataModified = true;
			
			EditorLore.updateDurationDescription(getItem(50), data.getDuration(), Message.EDITOR_ITEMSTACK_MENU_DURATION_DESCRIPTION);
			return EditorClickType.NEUTRAL;
		});
		
		ItemStack addItem = ItemUtil.createItem(Material.TURTLE_HELMET, Message.EDITOR_ITEMSTACK_MENU_ADD_ITEM);
		setButton(52, addItem, (event, slot) ->
		{
			EditorIconMenu editorIconMenu = new EditorIconMenu(core, owner, menuBuilder, iconTitle, iconName, iconDescription, (item) ->
			{
				onAdd(slot, item);
			});
			menuBuilder.addMenu(editorIconMenu);
			editorIconMenu.open();
			return EditorClickType.NEUTRAL;
		});
		
		List<ItemStack> items = targetHat.getItemStackData(particleIndex).getItems();
		for (int i = 0; i < items.size(); i++)
		{
			ItemStack item = items.get(i);
			String displayName = Message.EDITOR_ICON_MENU_ITEM_PREFIX.getValue() + StringUtil.getMaterialName(item.getType());
			
			ItemUtil.setNameAndDescription(item, displayName, StringUtil.parseDescription(Message.EDITOR_ICON_MENU_ICON_DESCRIPTION.getValue()));
			setItem(getNormalIndex(i, 10, 2), item);
		}
	}
}
