package com.mediusecho.particlehats.editor.menus;

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
import com.mediusecho.particlehats.ui.menus.SingularMenu;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorMetaMenu extends SingularMenu {

	private final EditorMenuManager editorManager;
	private final Hat targetHat;
	
	public EditorMetaMenu(ParticleHats core, EditorMenuManager menuManager, Player owner) 
	{
		super(core, menuManager, owner);
		
		this.editorManager = menuManager;
		this.targetHat = menuManager.getBaseHat();
		this.inventory = Bukkit.createInventory(null, 54, Message.EDITOR_META_MENU_TITLE.getValue());
		
		build();
	}
	
	@Override
	public void open ()
	{
		EditorLore.updateNameDescription(getItem(13), targetHat);	
		EditorLore.updateDescriptionDescription(getItem(11), targetHat.getDescription());
		EditorLore.updateDescriptionDescription(getItem(19), targetHat.getPermissionDescription());
		EditorLore.updatePermissionDescription(getItem(15), targetHat);
		EditorLore.updateLabelDescription(getItem(29), targetHat.getLabel());
		EditorLore.updateEquipDescription(getItem(33), targetHat.getEquipDisplayMessage());
		EditorLore.updatePermissionDeniedDescription(getItem(25), targetHat.getPermissionDeniedDisplayMessage());
		
		super.open();
	}

	@Override
	protected void build() 
	{
		setButton(49, backButtonItem, backButtonAction);
		
		// Name
		ItemStack nameItem = ItemUtil.createItem(CompatibleMaterial.PLAYER_HEAD, Message.EDITOR_META_MENU_SET_NAME);
		setButton(13, nameItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				editorManager.getOwnerState().setMetaState(MetaState.HAT_NAME);
				core.prompt(owner, MetaState.HAT_NAME);
				owner.closeInventory();
			}
			
			else
			{
				targetHat.setName(Message.EDITOR_MISC_NEW_PARTICLE.getRawValue());
				EditorLore.updateNameDescription(getItem(13), targetHat);
			}
			return MenuClickResult.NEUTRAL;
		});
		
		// Description
		ItemStack descriptionItem = ItemUtil.createItem(CompatibleMaterial.WRITABLE_BOOK, Message.EDITOR_META_MENU_SET_DESCRIPTION);
		setButton(11, descriptionItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorDescriptionMenu editorDescriptionMenu = new EditorDescriptionMenu(core, editorManager, owner, true);
				menuManager.addMenu(editorDescriptionMenu);
				editorDescriptionMenu.open();
			}
			
			else if (event.isShiftRightClick())
			{
				if (!targetHat.getDescription().isEmpty())
				{
					targetHat.getDescription().clear();
					
					Database database = core.getDatabase();
					String menuName = editorManager.getMenuName();
					database.saveMetaData(menuName, targetHat, DataType.DESCRIPTION, 0);
					
					EditorLore.updateDescriptionDescription(getItem(11), targetHat.getDescription());
				}
			}
			return MenuClickResult.NEUTRAL;
		});
		
		// Permission Description
		ItemStack permissionDescriptionItem = ItemUtil.createItem(Material.BOOK, Message.EDITOR_META_MENU_SET_PERMISSION_DESCRIPTION);
		setButton(19, permissionDescriptionItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorDescriptionMenu editorDescriptionMenu = new EditorDescriptionMenu(core, editorManager, owner, false);
				menuManager.addMenu(editorDescriptionMenu);
				editorDescriptionMenu.open();
			}
			
			else if (event.isShiftRightClick())
			{
				if (!targetHat.getPermissionDescription().isEmpty())
				{
					targetHat.getPermissionDescription().clear();
					
					Database database = core.getDatabase();
					String menuName = editorManager.getMenuName();
					database.saveMetaData(menuName, targetHat, DataType.PERMISSION_DESCRIPTION, 0);
					
					EditorLore.updateDescriptionDescription(getItem(19), targetHat.getPermissionDescription());
				}
			}
			return MenuClickResult.NEUTRAL;
		});
		
		// Permission
		ItemStack permissionItem = ItemUtil.createItem(Material.PAPER, Message.EDITOR_META_MENU_SET_PERMISSION);
		setButton(15, permissionItem, (event, slot) ->
		{
			editorManager.getOwnerState().setMetaState(MetaState.HAT_PERMISSION);
			core.prompt(owner, MetaState.HAT_PERMISSION);
			owner.closeInventory();
			return MenuClickResult.NEUTRAL;
		});
		
		// Label
		ItemStack labelItem = ItemUtil.createItem(Material.NAME_TAG, Message.EDITOR_META_MENU_SET_LABEL);
		setButton(29, labelItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				editorManager.getOwnerState().setMetaState(MetaState.HAT_LABEL);
				core.prompt(owner, MetaState.HAT_LABEL);
				owner.closeInventory();
			}
			
			else if (event.isShiftRightClick())
			{
				core.getDatabase().onLabelChange(targetHat.getLabel(), null, null, -1);
				targetHat.removeLabel();
				EditorLore.updateLabelDescription(getItem(29), targetHat.getLabel());
			}
			
			return MenuClickResult.NEUTRAL;
		});
		
		// Equip
		ItemStack equipItem = ItemUtil.createItem(Material.LEATHER_HELMET, Message.EDITOR_META_MENU_SET_EQUIP_MESSAGE);
		setButton(33, equipItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				editorManager.getOwnerState().setMetaState(MetaState.HAT_EQUIP_MESSAGE);
				core.prompt(owner, MetaState.HAT_EQUIP_MESSAGE);
				owner.closeInventory();
			}
			
			else if (event.isShiftRightClick())
			{
				targetHat.removeEquipMessage();
				EditorLore.updateEquipDescription(getItem(33), targetHat.getEquipDisplayMessage());
			}
			return MenuClickResult.NEUTRAL;
		});
		
		// Permission Denied
		ItemStack permissionDeniedItem = ItemUtil.createItem(CompatibleMaterial.MAP, Message.EDITOR_META_MENU_SET_PERMISSION_MESSAGE);
		setButton(25, permissionDeniedItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				editorManager.getOwnerState().setMetaState(MetaState.HAT_PERMISSION_MESSAGE);
				core.prompt(owner, MetaState.HAT_PERMISSION_MESSAGE);
				owner.closeInventory();
			}
			
			else if (event.isShiftRightClick())
			{
				targetHat.removePermissionDeniedMessage();
				EditorLore.updatePermissionDeniedDescription(getItem(25), targetHat.getPermissionDeniedDisplayMessage());
			}
			return MenuClickResult.NEUTRAL;
		});
		
		// Tags
		ItemStack tagItem = ItemUtil.createItem(Material.BOWL, Message.EDITOR_META_MENU_SET_TAG);
		setButton(31, tagItem, (event, slot) ->
		{
			EditorTagMenuOverview editorTagMenuOverview = new EditorTagMenuOverview(core, editorManager, owner);
			menuManager.addMenu(editorTagMenuOverview);
			editorTagMenuOverview.open();
			return MenuClickResult.NEUTRAL;
		});
	}

	@Override
	public void onClose(boolean forced) {}

	@Override
	public void onTick(int ticks) {}

}
