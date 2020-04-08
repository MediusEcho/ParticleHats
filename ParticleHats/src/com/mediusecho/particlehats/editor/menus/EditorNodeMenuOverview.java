package com.mediusecho.particlehats.editor.menus;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.ui.AbstractListMenu;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorNodeMenuOverview extends AbstractListMenu {

	private final EditorMenuManager editorManager;
	private final Hat targetHat;
	private final String nodeTitle = Message.EDITOR_NODE_OVERVIEW_NODE_TITLE.getValue();
	
	private final ItemStack emptyItem = ItemUtil.createItem(CompatibleMaterial.BARRIER, Message.EDITOR_NODE_OVERVIEW_MENU_EMPTY);
	
	public EditorNodeMenuOverview(ParticleHats core, EditorMenuManager menuManager, Player owner) 
	{
		super(core, menuManager, owner, true);
		
		this.editorManager = menuManager;
		this.targetHat = menuManager.getBaseHat();
		this.totalPages = 1;
		
		setMenu(0, Bukkit.createInventory(null, 54, Message.EDITOR_NODE_OVERVIEW_MENU_TITLE.getValue()));
		build();
	}

	@Override
	public void insertEmptyItem () {
		setButton(0, 22, emptyItem, emptyAction);
	}
	
	@Override
	public void removeEmptyItem () {
		setButton(0, 22, null, emptyAction);
	}

	@Override
	protected void build() 
	{
		setButton(0, 46, backButtonItem, backButtonAction);
		
		ItemStack addItem = ItemUtil.createItem(CompatibleMaterial.TURTLE_HELMET, Message.EDITOR_NODE_OVERVIEW_MENU_ADD_NODE);
		setButton(0, 52, addItem, (event, slot) ->
		{
			List<Hat> nodes = targetHat.getNodes();
			int size = nodes.size();
			
			if (size >= 28) {
				return MenuClickResult.NONE;
			}
			
			int index = size > 0 ? nodes.get(size - 1).getIndex() + 1 : 0;
			Hat node = new Hat();
			
			node.setIndex(index);
			node.setSlot(targetHat.getSlot());
			node.setParent(targetHat);
			nodes.add(node);
			
			String title = nodeTitle.replace("{1}", Integer.toString(size + 1));
			ItemStack item = ItemUtil.createItem(Material.LEATHER_HELMET, title, StringUtil.parseDescription(Message.EDITOR_NODE_OVERVIEW_MENU_NODE_DESCRIPTION.getValue()));
		
			setItem(0, getNormalIndex(size, 10, 2), item);
			setEmpty(false);
			
			return MenuClickResult.NEUTRAL;
		});
		
		MenuAction editAction = (event, slot) ->
		{
			if (event.isLeftClick())
			{
				int index = getClampedIndex(slot, 10, 2);
				Hat node = targetHat.getNode(index);
				
				if (node == null) {
					return MenuClickResult.NONE;
				}
				
				editorManager.setTargetNode(node);
				
				EditorNodeMainMenu editorNodeMainMenu = new EditorNodeMainMenu(core, editorManager, owner);
				menuManager.addMenu(editorNodeMainMenu);
				editorNodeMainMenu.open();
			}
			
			else if (event.isShiftRightClick()) {
				deleteSlot(0, slot);
			}
			
			return MenuClickResult.NEUTRAL;
		};
		
		for (int i = 0; i < 28; i++) {
			setAction(getNormalIndex(i, 10, 2), editAction);
		}
		
		List<Hat> hatNodes = targetHat.getNodes();
		
		if (hatNodes.size() == 0)
		{
			setEmpty(true);
			return;
		}
		
		for (int i = 0; i < hatNodes.size(); i++)
		{			
			String title = nodeTitle.replace("{1}", Integer.toString(i + 1));
			ItemStack item = ItemUtil.createItem(Material.LEATHER_HELMET, title, StringUtil.parseDescription(Message.EDITOR_NODE_OVERVIEW_MENU_NODE_DESCRIPTION.getValue()));
		
			EditorLore.updateHatDescription(item, hatNodes.get(i), false);
			
			setItem(0, getNormalIndex(i, 10, 2), item);
		}
	}

	@Override
	public void onClose(boolean forced) 
	{
		editorManager.setTargetNode(null);
	}

	@Override
	public void onTick(int ticks) {}
	
	@Override
	public void deleteSlot (int page, int slot)
	{
		super.deleteSlot(page, slot);
		
		int index = contentRegion.getInclusiveIndex(page, slot); //getClampedIndex(slot, 10, 2);
		Hat node = targetHat.getNodes().remove(index);
		
		core.getDatabase().deleteNode(editorManager.getMenuName(), node.getSlot(), node.getIndex());
		
		for (int i = index; i <= 27; i++)
		{
			ItemStack item = getItem(0, getNormalIndex(i, 10, 2));
			if (item == null) {
				continue;
			}
			ItemUtil.setItemName(item, nodeTitle.replace("{1}", Integer.toString(i + 1)));
		}
		
		if (targetHat.getNodes().size() == 0) {
			setEmpty(true);
		}
	}

}
