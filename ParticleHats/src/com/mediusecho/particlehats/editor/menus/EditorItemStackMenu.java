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
import com.mediusecho.particlehats.particles.properties.ItemStackData;
import com.mediusecho.particlehats.ui.AbstractListMenu;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorItemStackMenu extends AbstractListMenu {

	private final Message iconTitle = Message.EDITOR_ICON_MENU_ITEM_TITLE;
	private final Message iconName = Message.EDITOR_ICON_MENU_ITEM_INFO;
	private final Message iconDescription = Message.EDITOR_ICON_MENU_ITEM_DESCRIPTION;
	
	private final ItemStack emptyItem = ItemUtil.createItem(CompatibleMaterial.BARRIER, Message.EDITOR_MISC_EMPTY_MENU);
	
	private final EditorMenuManager editorManager;
	private final MenuCallback callback;
	private final int particleIndex;
	private final Hat targetHat;
	private final MenuAction itemAction;
	
	private boolean dataModified = false;
	private boolean itemModified = false;
	
	public EditorItemStackMenu(ParticleHats core, EditorMenuManager menuManager, Player owner, int particleIndex, MenuCallback callback) 
	{
		super(core, menuManager, owner, true);
		
		this.editorManager = menuManager;
		this.callback = callback;
		this.particleIndex = particleIndex;
		this.targetHat = menuManager.getTargetHat();
		this.totalPages = 1;
		this.itemAction = (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorItemPromptMenu editorItemMenu = new EditorItemPromptMenu(core, editorManager, owner, iconTitle, iconName, iconDescription, (item) ->
				{
					menuManager.closeCurrentMenu();
					
					if (item == null) {
						return;
					}
					
					ItemStack i = (ItemStack)item;
					ItemStack currentItem = getItem(0, slot);
					
					Material material = i.getType();
					String displayName = Message.EDITOR_ICON_MENU_ITEM_PREFIX.getValue() + StringUtil.getMaterialName(material);
					
					ItemUtil.setItemType(currentItem, i);
					ItemUtil.setItemName(currentItem, displayName);
					
					ItemStackData itemStackData = targetHat.getParticleData(particleIndex).getItemStackData();
					itemStackData.updateItem(getClampedIndex(slot, 10, 2), i);
					
					itemModified = true;
				});
				
				
				menuManager.addMenu(editorItemMenu);
				editorItemMenu.open();
			}
			
			else if (event.isShiftRightClick())
			{
				deleteSlot(0, slot);
				return MenuClickResult.NEGATIVE;
			}
			return MenuClickResult.NEUTRAL;
		};
		
		setMenu(0, Bukkit.createInventory(null, 54, Message.EDITOR_ITEMSTACK_MENU_TITLE.getValue()));
		
		build();
	}
	
	@Override
	public void insertEmptyItem () {
		setButton(0, 22, emptyItem, emptyAction);
	}
	
	@Override
	public void removeEmptyItem () {
		setButton(0, 22, null, itemAction);
	}

	@Override
	protected void build() 
	{
		ItemStackData itemStackData = targetHat.getParticleData(particleIndex).getItemStackData();
		
		setButton(0, 46, backButtonItem, backButtonAction);
		
		ItemStack velocityItem = ItemUtil.createItem(Material.ARROW, Message.EDITOR_ITEMSTACK_MENU_SET_VELOCITY);
		EditorLore.updateVectorDescription(velocityItem, itemStackData.getVelocity(), Message.EDITOR_ITEMSTACK_MENU_VELOCITY_DESCRIPTION);
		setButton(0, 48, velocityItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorVelocityMenu editorVelocityMenu = new EditorVelocityMenu(core, editorManager, owner, particleIndex, () ->
				{
					onVelocityChange();
				});
				menuManager.addMenu(editorVelocityMenu);
				editorVelocityMenu.open();
			}
			
			else if (event.isShiftRightClick())
			{
				ItemStackData data = targetHat.getParticleData(particleIndex).getItemStackData();
				data.setVelocity(0, 0, 0);
				onVelocityChange();
			}
			return MenuClickResult.NEUTRAL;
		});
		
		ItemStack gravityItem = ItemUtil.createItem(Material.LEATHER_BOOTS, Message.EDITOR_ITEMSTACK_MENU_TOGGLE_GRAVITY);
		EditorLore.updateBooleanDescription(gravityItem, itemStackData.hasGravity(), Message.EDITOR_ITEMSTACK_MENU_GRAVITY_DESCRIPTION);
		setButton(0, 49, gravityItem, (event, slot) ->
		{
			ItemStackData data = targetHat.getParticleData(particleIndex).getItemStackData();
			data.setGravity(!data.hasGravity());
			dataModified = true;
			
			EditorLore.updateBooleanDescription(getItem(0, 49), data.hasGravity(), Message.EDITOR_ITEMSTACK_MENU_GRAVITY_DESCRIPTION);
			return MenuClickResult.NEUTRAL;
		});
		
		ItemStack durationItem = ItemUtil.createItem(CompatibleMaterial.FIREWORK_STAR, Message.EDITOR_ITEMSTACK_MENU_SET_DURATION);
		EditorLore.updateDurationDescription(durationItem, itemStackData.getDuration(), Message.EDITOR_ITEMSTACK_MENU_DURATION_DESCRIPTION);
		setButton(0, 50, durationItem, (event, slot) ->
		{
			int normalClick    = event.isLeftClick() ? 20 : -20;
			int shiftClick     = event.isShiftClick() ? 30 : 1;
			int modifier       = normalClick * shiftClick;
			
			ItemStackData data = targetHat.getParticleData(particleIndex).getItemStackData();
			int duration = data.getDuration() + modifier;
			data.setDuration(duration);
			dataModified = true;
			
			EditorLore.updateDurationDescription(getItem(0, 50), data.getDuration(), Message.EDITOR_ITEMSTACK_MENU_DURATION_DESCRIPTION);
			return MenuClickResult.NEUTRAL;
		});
		
		ItemStack addItem = ItemUtil.createItem(CompatibleMaterial.TURTLE_HELMET, Message.EDITOR_ITEMSTACK_MENU_ADD_ITEM);
		setButton(0, 52, addItem, (event, slot) ->
		{
			EditorItemPromptMenu editorItemMenu = new EditorItemPromptMenu(core, editorManager, owner, iconTitle, iconName, iconDescription, (item) ->
			{
				menuManager.closeCurrentMenu();
				
				if (item == null) {
					return;
				}
				
				ItemStack i = (ItemStack)item;
				addItem(slot, i);
			});
			
			
			menuManager.addMenu(editorItemMenu);
			editorItemMenu.open();
			
			return MenuClickResult.NEUTRAL;
		});
		
		for (int i = 0; i < 28; i++) {
			setAction(getClampedIndex(i, 10, 2), itemAction);
		}
		
		List<ItemStack> items = itemStackData.getItems();
		if (items.isEmpty()) 
		{
			setEmpty(true);
			return;
		}
		
		for (int i = 0; i < items.size(); i++)
		{
			ItemStack item = items.get(i);
			String displayName = Message.EDITOR_ICON_MENU_ITEM_PREFIX.getValue() + StringUtil.getMaterialName(item.getType());
			
			ItemUtil.setNameAndDescription(item, displayName, StringUtil.parseDescription(Message.EDITOR_ICON_MENU_ICON_DESCRIPTION.getValue()));
			setItem(0, getNormalIndex(i, 10, 2), item);
		}
	}

	@Override
	public void onClose(boolean forced) 
	{
		String name = editorManager.getMenuName();
		
		if (dataModified) {
			core.getDatabase().saveParticleData(name, targetHat, particleIndex);
		}
		
		if (itemModified) 
		{
			core.getDatabase().saveMetaData(name, targetHat, DataType.ITEMSTACK, particleIndex);
			callback.onCallback();
		}
	}

	@Override
	public void onTick(int ticks) {}
	
	private void addItem (int slot, ItemStack item)
	{
		setEmpty(false);
		
		ItemStackData itemStackData = targetHat.getParticleData(particleIndex).getItemStackData();
		int size = itemStackData.getItems().size();
		
		if (size > 27) {
			return;
		}
		
		Material material = item.getType();
		String displayName = Message.EDITOR_ICON_MENU_ITEM_PREFIX.getValue() + StringUtil.getMaterialName(material);
		ItemStack i = ItemUtil.createItem(material, displayName, StringUtil.parseDescription(Message.EDITOR_ICON_MENU_ICON_DESCRIPTION.getValue()));
		
		itemStackData.addItem(item);
		setItem(0, getNormalIndex(size, 10, 2), i);
		
		itemModified = true;
	}
	
	@Override
	public void deleteSlot(int page, int slot)
	{
		super.deleteSlot(page, slot);
		
		ItemStackData itemStackData = targetHat.getParticleData(particleIndex).getItemStackData();
		itemStackData.removeItem(getClampedIndex(slot, 10, 2));
		itemModified = true;
		
		if (itemStackData.getItems().isEmpty()) {
			setEmpty(true);
		}
	}
	
	private void onVelocityChange ()
	{
		dataModified = true;
		
		ItemStackData itemStackData = targetHat.getParticleData(particleIndex).getItemStackData();
		EditorLore.updateVectorDescription(getItem(0, 48), itemStackData.getVelocity(), Message.EDITOR_ITEMSTACK_MENU_VELOCITY_DESCRIPTION);
	}
}
