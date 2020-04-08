package com.mediusecho.particlehats.editor.menus;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.ui.menus.ListMenu;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.ui.properties.MenuContentRegion;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorNodeMenuOverview extends ListMenu {

	private final EditorMenuManager editorManager;
	private final Hat targetHat;
	private final String nodeTitle = Message.EDITOR_NODE_OVERVIEW_NODE_TITLE.getValue();
	
	private final ItemStack emptyItem = ItemUtil.createItem(CompatibleMaterial.BARRIER, Message.EDITOR_NODE_OVERVIEW_MENU_EMPTY);
	private final ItemStack addItem;
	
	public EditorNodeMenuOverview(ParticleHats core, EditorMenuManager menuManager, Player owner) 
	{
		super(core, menuManager, owner, MenuContentRegion.defaultLayout);
		
		this.editorManager = menuManager;
		this.targetHat = menuManager.getBaseHat();
		this.addItem = ItemUtil.createItem(CompatibleMaterial.TURTLE_HELMET, Message.EDITOR_NODE_OVERVIEW_MENU_ADD_NODE);
		
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
		setAction(49, backButtonAction);
		
		setAction(48, (event, slot) ->
		{
			currentPage--;
			open();
			return MenuClickResult.NEUTRAL;
		});
		
		setAction(50, (event, slot) ->
		{
			currentPage++;
			open();
			return MenuClickResult.NEUTRAL;
		});

		// Add
		setAction(52, (event, slot) ->
		{
			List<Hat> nodes = targetHat.getNodes();
			int size = nodes.size();
			
			Hat node = new Hat();
			node.setIndex(size > 0 ? nodes.get(size - 1).getIndex() + 1 : 0);
			node.setSlot(targetHat.getSlot());
			node.setParent(targetHat);
			nodes.add(node);
			
			addNode(node, size);
			return MenuClickResult.NEUTRAL;
		});
		
		final MenuAction editAction = (event, slot) ->
		{
			if (event.isLeftClick())
			{
				int index = contentRegion.getInclusiveIndex(currentPage, slot);
				Hat node = targetHat.getNode(index);
				
				if (node == null) {
					return MenuClickResult.NONE;
				}
				
				editorManager.setTargetNode(node);
				
				EditorNodeMainMenu editorNodeMainMenu = new EditorNodeMainMenu(core, editorManager, owner);
				editorManager.addMenu(editorNodeMainMenu);
				editorNodeMainMenu.open();
			}
			
			else if (event.isShiftRightClick()) {
				deleteItem(currentPage, slot);
			}
			return MenuClickResult.NEUTRAL;
		};
		contentRegion.fillRegion(this, editAction);
		
		int pages = contentRegion.getTotalPages(targetHat.getNodeCount());		
		for (int i = 0; i < pages; i++)
		{
			Inventory inventory = Bukkit.createInventory(null, 54, Message.EDITOR_NODE_OVERVIEW_MENU_TITLE.getValue());
			
			inventory.setItem(49, backButtonItem);
			inventory.setItem(52, addItem);
			
			if ((i + 1) < pages) {
				inventory.setItem(50, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
			}
			
			if ((i + 1) > 1) {
				inventory.setItem(48, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_PREVIOUS_PAGE));
			}
			
			setInventory(i, inventory);
		}
		
		for (int i = 0; i < targetHat.getNodeCount(); i++)
		{			
			String title = nodeTitle.replace("{1}", Integer.toString(i + 1));
			ItemStack item = ItemUtil.createItem(Material.LEATHER_HELMET, title, StringUtil.parseDescription(Message.EDITOR_NODE_OVERVIEW_MENU_NODE_DESCRIPTION.getValue()));
		
			EditorLore.updateHatDescription(item, targetHat.getNode(i), false);
			
			int slot = contentRegion.getNextSlot(i);
			int page = contentRegion.getPage(i);
			
			setItem(page, slot, item);
		}
	}

	@Override
	public void onClose(boolean forced) 
	{
		editorManager.setTargetNode(null);
	}
	
	@Override
	public void deleteItem (int page, int slot)
	{
		super.deleteItem(page, slot);
		
		int index = contentRegion.getInclusiveIndex(page, slot);
		Hat node = targetHat.getNodes().remove(index);
				
		core.getDatabase().deleteNode(editorManager.getMenuName(), node.getSlot(), node.getIndex());
		
		if (targetHat.getNodes().isEmpty()) 
		{
			setEmpty(true);
			return;
		}
		
		int totalSlots = contentRegion.getTotalSlots() * getTotalPages();
		for (int i = index; i <= totalSlots; i++)
		{
			int p = contentRegion.getPage(i);
			int s = contentRegion.getNextSlot(i);
						
			ItemStack item = this.getItem(p, s);
			if (item == null) {
				return;
			}
			ItemUtil.setItemName(item, nodeTitle.replace("{1}", Integer.toString(i + 1)));
		}
	}
	
	private void insertMenu (int page)
	{
		Inventory inventory = Bukkit.createInventory(null, 54, Message.EDITOR_NODE_OVERVIEW_MENU_TITLE.getValue());
		
		inventory.setItem(49, backButtonItem);
		inventory.setItem(52, addItem);
		
		if ((page + 1) > 1) {
			inventory.setItem(48, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_PREVIOUS_PAGE));
		}
		
		pages.put(page, inventory);
		
		for (int i = page - 1; i >= 0; i--)
		{
			Inventory inv = getInventory(i);
			if (inv.getItem(50) == null) {
				inv.setItem(50, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
			}
		}
	}
	
	private void addNode (Hat node, int number)
	{
		String title = nodeTitle.replace("{1}", Integer.toString(number + 1));
		ItemStack item = ItemUtil.createItem(Material.LEATHER_HELMET, title, StringUtil.parseDescription(Message.EDITOR_NODE_OVERVIEW_MENU_NODE_DESCRIPTION.getValue()));
		
		int size = targetHat.getNodeCount() - 1;
		int page = contentRegion.getPage(size);
		int index = contentRegion.getNextSlot(size);
		
		ParticleHats.debug("adding node to page: " + page);
		
		if (!pages.containsKey(page)) {
			insertMenu(page);
		}
		
		setEmpty(false);
		getInventory(page).setItem(index, item);
	}

}
