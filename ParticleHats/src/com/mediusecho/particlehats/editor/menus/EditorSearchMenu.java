package com.mediusecho.particlehats.editor.menus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.ui.AbstractListMenu;
import com.mediusecho.particlehats.ui.MenuManager;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;

public class EditorSearchMenu extends AbstractListMenu {

	private final List<Material> matchingResults;
	private final List<Material> blacklist = Arrays.asList(Material.AIR);
	
	private final MenuAction selectAction;
	
	private final String menuTitle;
	
	public EditorSearchMenu(ParticleHats core, MenuManager menuManager, Player owner, String searchQuery, MenuObjectCallback callback) 
	{
		super(core, menuManager, owner, false);
		
		String query = searchQuery.toLowerCase();
		String[] queries = query.split(",");
		
		this.matchingResults = new ArrayList<Material>();
		this.menuTitle = EditorLore.getTrimmedMenuTitle(query, Message.EDITOR_SEARCH_MENU_TITLE);
		
		for (Material material : Material.values())
		{
			if (blacklist.contains(material)) {
				continue;
			}
			
			if (!ItemUtil.isItem(material)) {
				continue;
			}
			
			String materialName = material.toString().toLowerCase();
			for (String q : queries)
			{
				if (materialName.contains(q)) {
					matchingResults.add(material);
				}
			}
		}
		
		this.totalPages = MathUtil.calculatePageCount(matchingResults.size(), 45);
		this.selectAction = (event, slot) ->
		{
			int index = slot + (currentPage * 45);
			if (index < matchingResults.size()) {
				callback.onSelect(new ItemStack(matchingResults.get(index)));
			}
			return MenuClickResult.NEUTRAL;
		};
		
		build();
	}
	
	@Override
	public void insertEmptyItem () {
		setItem(0, 22, ItemUtil.createItem(CompatibleMaterial.BARRIER, Message.EDITOR_SEARCH_MENU_NO_RESULTS));
	}
	
	@Override
	public void removeEmptyItem () {}

	@Override
	protected void build() 
	{
		// Back
		setAction(49, backButtonAction);
		
		// Previous Page
		setAction(48, (event, slot) ->
		{
			currentPage--;
			open();
			return MenuClickResult.NEUTRAL;
		});
		
		// Next Page
		setAction(50, (event, slot) ->
		{
			currentPage++;
			open();
			return MenuClickResult.NEUTRAL;
		});
		
		for (int i = 0; i < 45; i++) {
			setAction(i, selectAction);
		}
		
		for (int i = 0; i < totalPages; i++)
		{
			Inventory inventory = Bukkit.createInventory(null, 54, menuTitle);
			
			inventory.setItem(49, backButtonItem);
			
			if ((i + 1) < totalPages) {
				inventory.setItem(50, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
			}
			
			if ((i + 1) > 1) {
				inventory.setItem(48, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_PREVIOUS_PAGE));
			}
			
			setMenu(i, inventory);
		}
		
		if (matchingResults.isEmpty())
		{
			setEmpty(true);
			return;
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

	@Override
	public void onClose(boolean forced) {}

	@Override
	public void onTick(int ticks) {}

}
