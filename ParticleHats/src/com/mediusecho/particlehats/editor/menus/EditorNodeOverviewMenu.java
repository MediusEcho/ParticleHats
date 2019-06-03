package com.mediusecho.particlehats.editor.menus;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorListMenu;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorNodeOverviewMenu extends EditorListMenu {

	private final Hat targetHat;
	private final String nodeTitle = Message.EDITOR_NODE_OVERVIEW_NODE_TITLE.getValue();
	
	public EditorNodeOverviewMenu(ParticleHats core, Player owner, MenuBuilder menuBuilder) 
	{
		super(core, owner, menuBuilder);
		targetHat = menuBuilder.getBaseHat();
		
		editAction = (event, slot) ->
		{
			if (event.isLeftClick())
			{
				int index = getClampedIndex(slot, 10, 2);
				Hat node = targetHat.getNode(index);
				
				if (node != null)
				{
					menuBuilder.setTargetNodeHat(node);
					
					EditorNodeMainMenu editorNodeMainMenu = new EditorNodeMainMenu(core, owner, menuBuilder);
					menuBuilder.addMenu(editorNodeMainMenu);
					editorNodeMainMenu.open();
				}
			}
			
			else if (event.isShiftRightClick()) {
				onDelete(slot);
			}
			return EditorClickType.NEUTRAL;
		};
		
		inventory = Bukkit.createInventory(null, 54, Message.EDITOR_NODE_OVERVIEW_MENU_TITLE.getValue());
		build();
	}
	
	private void onAdd (int slot)
	{
		List<Hat> nodes = targetHat.getNodes();
		int size = nodes.size();		
		
		if (size <= 27)
		{
			int index = size > 0 ? nodes.get(size - 1).getIndex() + 1 : 0;
			Hat node = new Hat();
			
			node.setIndex(index);
			node.setSlot(targetHat.getSlot());
			node.setParent(targetHat);
			nodes.add(node);
			
			String title = nodeTitle.replace("{1}", Integer.toString(size + 1));
			ItemStack item = ItemUtil.createItem(Material.LEATHER_HELMET, title, StringUtil.parseDescription(Message.EDITOR_NODE_OVERVIEW_MENU_NODE_DESCRIPTION.getValue()));
		
			setItem(getNormalIndex(size, 10, 2), item);
		}
		
		if (isEmpty)
		{
			isEmpty = false;
			removeEmptyItem();
		}
	}
	
	@Override
	protected void onDelete (int slot)
	{
		super.onDelete(slot);
		
		int index = getClampedIndex(slot, 10, 2);
		Hat node = targetHat.getNodes().remove(index);
		
		core.getDatabase().deleteNode(menuBuilder.getMenuName(), node.getSlot(), node.getIndex());
		
		for (int i = index; i <= 27; i++)
		{
			EditorAction action = getAction(i);
			if (action == addAction) {
				break;
			}
			
			ItemStack item = getItem(getNormalIndex(i, 10, 2));
			ItemUtil.setItemName(item, nodeTitle.replace("{1}", Integer.toString(i + 1)));
		}
		
		isEmpty = targetHat.getNodes().size() == 0;
		if (isEmpty) {
			insertEmptyItem();
		}
	}

	@Override
	public void build ()
	{
		super.build();
		
		setButton(46, backButton, (event, slot) ->
		{
			menuBuilder.setTargetNodeHat(null);
			menuBuilder.goBack();
			return EditorClickType.NEUTRAL;
		});
		
		ItemStack addItem = ItemUtil.createItem(CompatibleMaterial.TURTLE_HELMET, Message.EDITOR_NODE_OVERVIEW_MENU_ADD_NODE);
		setButton(52, addItem, (event, slot) ->
		{
			onAdd(slot);
			return EditorClickType.NEUTRAL;
		});
		
		List<Hat> hatNodes = targetHat.getNodes();
		for (int i = 0; i < hatNodes.size(); i++)
		{			
			String title = nodeTitle.replace("{1}", Integer.toString(i + 1));
			ItemStack item = ItemUtil.createItem(Material.LEATHER_HELMET, title, StringUtil.parseDescription(Message.EDITOR_NODE_OVERVIEW_MENU_NODE_DESCRIPTION.getValue()));
		
			EditorLore.updateHatDescription(item, hatNodes.get(i), false);
			
			setItem(getNormalIndex(i, 10, 2), item);
		}
		
		isEmpty = hatNodes.size() == 0;
		if (isEmpty) {
			insertEmptyItem();
		}
	}
}
