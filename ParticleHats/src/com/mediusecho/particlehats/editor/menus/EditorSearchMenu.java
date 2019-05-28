package com.mediusecho.particlehats.editor.menus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.ui.GuiState;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorSearchMenu extends EditorMenu {

	private final String menuTitle;
	private final List<Material> matchingResults;
	private final Map<Integer, Inventory> menus;
	private final int pages;
	private final EditorAction selectAction;
	
	private int currentPage = 0;
	
	private final List<Material> blacklist = Arrays.asList(
			Material.AIR);
	
	public EditorSearchMenu(ParticleHats core, Player owner, MenuBuilder menuBuilder, final String searchQuery, EditorItemCallback itemCallback) 
	{
		super(core, owner, menuBuilder);
		
		this.matchingResults = new ArrayList<Material>();
		this.menus = new HashMap<Integer, Inventory>();
		
		String query = searchQuery.toLowerCase();
		this.menuTitle = EditorLore.getTrimmedMenuTitle(query, Message.EDITOR_SEARCH_MENU_TITLE);
		
		String[] queries = query.split(",");
		
		for (Material material : Material.values())
		{
			if (blacklist.contains(material)) {
				continue;
			}
			
			if (ItemUtil.isItem(material))
			{
				String materialName = material.toString().toLowerCase();
				for (String q : queries) {
					if (materialName.contains(q)) {
						matchingResults.add(material);
					}
				}
			}
		}	
		pages = (int) Math.ceil((double) matchingResults.size() / 45D);
		
		selectAction = (event, slot) ->
		{
			int index = slot + (currentPage * 45);
			if (index < matchingResults.size()) 
			{
				itemCallback.onSelect(new ItemStack(matchingResults.get(index)));
				menuBuilder.goBack();
			}
			return EditorClickType.NEUTRAL;
		};
		
		inventory = Bukkit.createInventory(null, 54, menuTitle);
		build();
	}
	
	@Override
	public void open ()
	{
		if (menus.containsKey(currentPage))
		{
			menuBuilder.setOwnerState(GuiState.SWITCHING_EDITOR);
			owner.openInventory(menus.get(currentPage));
		}
		
		else {
			super.open();
		}
	}

	@Override
	protected void build() 
	{
		setButton(49, backButton, backAction);
		setItem(22, ItemUtil.createItem(CompatibleMaterial.BARRIER, Message.EDITOR_SEARCH_MENU_NO_RESULTS));
		
		for (int i = 0; i < 45; i++) {
			setAction(i, selectAction);
		}
		
		setAction(48, (event, slot) ->
		{
			currentPage--;
			open();
			return EditorClickType.NEUTRAL;
		});
		
		setAction(50, (event, slot) ->
		{
			currentPage++;
			open();
			return EditorClickType.NEUTRAL;
		});
		
		for (int i = 0; i < pages; i++)
		{
			Inventory menu = Bukkit.createInventory(null, 54, menuTitle);
			
			menu.setItem(49, backButton);
			
			if ((i + 1) < pages) {
				menu.setItem(50, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
			}
			
			if ((i + 1) > 1) {
				menu.setItem(48, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_PREVIOUS_PAGE));
			}
			
			menus.put(i, menu);
		}
		
		int index = 0;
		int page = 0;
		for (Material material : matchingResults)
		{
			menus.get(page).setItem(index++, ItemUtil.createItem(material, 1));
			if (index % 45 == 0)
			{
				index = 0;
				page++;
			}
		}
	}

}
