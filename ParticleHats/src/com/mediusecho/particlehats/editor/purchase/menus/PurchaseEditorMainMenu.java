package com.mediusecho.particlehats.editor.purchase.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.database.Database.DataType;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.MetaState;
import com.mediusecho.particlehats.editor.menus.EditorActionMenu;
import com.mediusecho.particlehats.editor.menus.EditorBaseMenu;
import com.mediusecho.particlehats.editor.menus.EditorDescriptionMenu;
import com.mediusecho.particlehats.editor.menus.EditorIconMenuOverview;
import com.mediusecho.particlehats.editor.menus.EditorSlotMenu;
import com.mediusecho.particlehats.editor.menus.EditorSoundMenu;
import com.mediusecho.particlehats.editor.purchase.PurchaseMenuManager;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.ui.AbstractStaticMenu;
import com.mediusecho.particlehats.util.ItemUtil;

public class PurchaseEditorMainMenu extends AbstractStaticMenu {

	private final PurchaseMenuManager purchaseManager;
	private final EditorBaseMenu editorBaseMenu;
	private Hat targetHat;
	
	public PurchaseEditorMainMenu(ParticleHats core, PurchaseMenuManager menuManager, Player owner)
	{
		super(core, menuManager, owner);
		
		this.purchaseManager = menuManager;
		this.editorBaseMenu = menuManager.getEditingMenu();
		this.targetHat = menuManager.getBaseHat();
		this.inventory = Bukkit.createInventory(null, 36, Message.EDITOR_MAIN_MENU_TITLE.getValue());
		
		build();
	}

	@Override
	protected void build() 
	{
		setButton(31, backButtonItem, backButtonAction);
		
		// Action
		ItemStack actionItem = ItemUtil.createItem(CompatibleMaterial.GUNPOWDER, Message.EDITOR_MAIN_MENU_SET_ACTION);
		EditorLore.updateSpecificActionDescription(actionItem, targetHat, targetHat.getLeftClickAction(), targetHat.getLeftClickArgument());
		setButton(11, actionItem, (event, slot) ->
		{
			EditorActionMenu editorActionMenu = new EditorActionMenu(core, purchaseManager, owner, true, true, (action) ->
			{
				if (action == null) {
					return;
				}
				
				ParticleAction pa = (ParticleAction)action;
				
				targetHat.setLeftClickAction(pa);
				EditorLore.updateSpecificActionDescription(getItem(11), targetHat, targetHat.getLeftClickAction(), targetHat.getLeftClickArgument());
				
				menuManager.closeCurrentMenu();
			});
			
			menuManager.addMenu(editorActionMenu);
			editorActionMenu.open();
			
			return MenuClickResult.NEUTRAL;
		});
		
		// Name
		ItemStack nameItem = ItemUtil.createItem(CompatibleMaterial.PLAYER_HEAD, Message.EDITOR_META_MENU_SET_NAME);
		setButton(13, nameItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				purchaseManager.getOwnerState().setMetaState(MetaState.HAT_NAME);
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
		setButton(15, descriptionItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorDescriptionMenu editorDescriptionMenu = new EditorDescriptionMenu(core, purchaseManager, owner, true);
				menuManager.addMenu(editorDescriptionMenu);
				editorDescriptionMenu.open();
			}
			
			else if (event.isShiftRightClick())
			{
				if (!targetHat.getDescription().isEmpty())
				{
					targetHat.getDescription().clear();
					
					Database database = core.getDatabase();
					String menuName = purchaseManager.getMenuName();
					database.saveMetaData(menuName, targetHat, DataType.DESCRIPTION, 0);
					
					EditorLore.updateDescriptionDescription(getItem(15), targetHat.getDescription());
				}
			}
			return MenuClickResult.NEUTRAL;
		});
		
		// Slot
		ItemStack slotItem = ItemUtil.createItem(Material.ITEM_FRAME, Message.EDITOR_MAIN_MENU_SET_SLOT, Message.EDITOR_MAIN_MENU_SLOT_DESCRIPTION);
		setButton(19, slotItem, (event, slot) ->
		{
			EditorSlotMenu editorSlotMenu = new EditorSlotMenu(core, purchaseManager, owner, editorBaseMenu, false);
			menuManager.addMenu(editorSlotMenu);
			editorSlotMenu.open();
			return MenuClickResult.NEUTRAL;
		});
		
		// Clone
		ItemStack cloneItem = ItemUtil.createItem(CompatibleMaterial.PRISMARINE_SHARD, Message.EDITOR_MAIN_MENU_CLONE, Message.EDITOR_MAIN_MENU_CLONE_DESCRIPTION);
		setButton(21, cloneItem, (event, slot) ->
		{
			if (targetHat.isModified())
			{
				int targetSlot = purchaseManager.getTargetSlot();
				core.getDatabase().saveHat(editorBaseMenu.getMenuInventory().getName(), targetSlot, targetHat);
				targetHat.clearPropertyChanges();
			}
			
			EditorSlotMenu editorSlotMenu = new EditorSlotMenu(core, purchaseManager, owner, editorBaseMenu, true);
			menuManager.addMenu(editorSlotMenu);
			editorSlotMenu.open();
			return MenuClickResult.NEUTRAL;
		});
		
		// Sound
		ItemStack soundItem = ItemUtil.createItem(CompatibleMaterial.MUSIC_DISC_STRAD, Message.EDITOR_MAIN_MENU_SET_SOUND);
		EditorLore.updateSoundItemDescription(soundItem, targetHat);
		setButton(23, soundItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorSoundMenu editorSoundMenu = new EditorSoundMenu(core, purchaseManager, owner, (sound) ->
				{
					menuManager.closeCurrentMenu();
					
					if (sound == null) {
						return;
					}
					
					Sound s = (Sound)sound;
					
					targetHat.setSound(s);
					EditorLore.updateSoundItemDescription(getItem(23), targetHat);
				});
				
				menuManager.addMenu(editorSoundMenu);
				editorSoundMenu.open();
			}
			
			else if (event.isShiftRightClick())
			{
				targetHat.removeSound();
				EditorLore.updateSoundItemDescription(getItem(23), targetHat);
			}
			
			return MenuClickResult.NEUTRAL;
		});
		
		// Icon
		ItemStack iconItem = targetHat.getItem();//ItemUtil.createItem(targetHat.getMaterial(), Message.EDITOR_MAIN_MENU_SET_ICON, Message.EDITOR_MAIN_MENU_ICON_DESCRIPTION);
		ItemUtil.setNameAndDescription(iconItem, Message.EDITOR_MAIN_MENU_SET_ICON, Message.EDITOR_MAIN_MENU_ICON_DESCRIPTION);
		setButton(25, iconItem, (event, slot) ->
		{
			EditorIconMenuOverview editorIconMenuOverview = new EditorIconMenuOverview(core, purchaseManager, owner, (mainItem) ->
			{
				if (mainItem == null) {
					return;
				}
				
				ItemStack item = (ItemStack)mainItem;
				ItemUtil.setItemType(getItem(25), item);
			});
			
			menuManager.addMenu(editorIconMenuOverview);
			editorIconMenuOverview.open();
			
			return MenuClickResult.NEUTRAL;
		});
	}

	@Override
	public void onClose(boolean forced) {}
	
	@Override
	public void open ()
	{
		EditorLore.updateNameDescription(getItem(13), targetHat);
		EditorLore.updateDescriptionDescription(getItem(15), targetHat.getDescription());
		
		super.open();
	}

	@Override
	public void onTick(int ticks) {}

}
