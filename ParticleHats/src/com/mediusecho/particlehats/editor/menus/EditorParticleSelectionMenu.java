package com.mediusecho.particlehats.editor.menus;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.ui.MenuState;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorParticleSelectionMenu extends EditorMenu {

	private final Map<Integer, Inventory> menus;
	
	private Inventory colorFilterMenu;
	private Inventory dataFilterMenu;
	private Inventory recentFilterMenu;
	
	private final NamespacedKey key;
	
	private final int totalPages;
	private int currentPage = 0;
	
	public EditorParticleSelectionMenu(Core core, Player owner, MenuBuilder menuBuilder) 
	{
		super(core, owner, menuBuilder);
		
		menus = new HashMap<Integer, Inventory>();
		totalPages = (int) Math.ceil((double) ParticleEffect.values().length / 45D);
		
		key = new NamespacedKey(core, "particleID");
		
		colorFilterMenu  = Bukkit.createInventory(null, 54, Message.EDITOR_PARTICLE_COLOR_FILTER_TITLE.getValue());
		dataFilterMenu   = Bukkit.createInventory(null, 54, Message.EDITOR_PARTICLE_DATA_FILTER_TITLE.getValue());
		recentFilterMenu = Bukkit.createInventory(null, 54, Message.EDITOR_PARTICLE_RECENT_FILTER_TITLE.getValue());
		
		build();
	}
	
	@Override
	public void open ()
	{
		if (menus.containsKey(currentPage))
		{
			menuBuilder.setOwnerState(MenuState.SWITCHING);
			owner.openInventory(menus.get(currentPage));
		}
//		Inventory menu = menus.get(currentPage);
//		if (menu != null)
//		{
//			menuBuilder.setOwnerState(MenuState.SWITCHING);
//			owner.openInventory(menu);
//		}
	}

	@Override
	protected void build() 
	{		
		// Create our main menus
		for (int i = 0; i < totalPages; i++)
		{
			String menuTitle = Message.EDITOR_PARTICLE_MENU_TITLE.getValue()
					.replace("{1}", "" + (i + 1)).replace("{2}", "" + totalPages);
			
			Inventory menu = Bukkit.createInventory(null, 54, menuTitle);
			
			menu.setItem(49, backButton);
			menu.setItem(45, ItemUtil.createItem(Material.MUSHROOM_STEW, Message.EDITOR_PARTICLE_NORMAL_FILTER));
			menu.setItem(46, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_COLOR_FILTER));
			menu.setItem(47, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_DATA_FILTER));
			menu.setItem(53, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_RECENT_FILTER));
			
			if ((i + 1) < totalPages) {
				menu.setItem(50, ItemUtil.createItem(Material.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
			}
			
			if ((i + 1) > 1) {
				menu.setItem(48, ItemUtil.createItem(Material.LIME_DYE, Message.EDITOR_MISC_PREVIOUS_PAGE));
			}
			
			menus.put(i, menu);
		}
		
		// Populate our filter menus
		colorFilterMenu.setItem(49, backButton);
		colorFilterMenu.setItem(45, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_NORMAL_FILTER));
		colorFilterMenu.setItem(46, ItemUtil.createItem(Material.MUSHROOM_STEW, Message.EDITOR_PARTICLE_COLOR_FILTER));
		colorFilterMenu.setItem(47, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_DATA_FILTER));
		colorFilterMenu.setItem(53, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_RECENT_FILTER));
		
		dataFilterMenu.setItem(49, backButton);
		dataFilterMenu.setItem(45, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_NORMAL_FILTER));
		dataFilterMenu.setItem(46, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_COLOR_FILTER));
		dataFilterMenu.setItem(47, ItemUtil.createItem(Material.MUSHROOM_STEW, Message.EDITOR_PARTICLE_DATA_FILTER));
		dataFilterMenu.setItem(53, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_RECENT_FILTER));
		
		recentFilterMenu.setItem(49, backButton);
		recentFilterMenu.setItem(45, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_NORMAL_FILTER));
		recentFilterMenu.setItem(46, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_COLOR_FILTER));
		recentFilterMenu.setItem(47, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_DATA_FILTER));
		recentFilterMenu.setItem(53, ItemUtil.createItem(Material.MUSHROOM_STEW, Message.EDITOR_PARTICLE_RECENT_FILTER));
		
		setAction(49, (clickEvent, slot) ->
		{
			menuBuilder.goBack();
			return EditorClickType.NEUTRAL;
		});
		
		// All Particles
		setAction(45, (clickEvent, slot) ->
		{
			menuBuilder.setOwnerState(MenuState.SWITCHING);
			owner.openInventory(menus.get(currentPage));
			return EditorClickType.NEUTRAL;
		});
		
		// Color Filter
		setAction(46, (clickEvent, slot) ->
		{
			menuBuilder.setOwnerState(MenuState.SWITCHING);
			owner.openInventory(colorFilterMenu);
			return EditorClickType.NEUTRAL;
		});
		
		// Data Filter
		setAction(47, (clickEvent, slot) ->
		{
			menuBuilder.setOwnerState(MenuState.SWITCHING);
			owner.openInventory(dataFilterMenu);
			return EditorClickType.NEUTRAL;
		});
		
		// Recent Filter
		setAction(53, (clickEvent, slot) ->
		{
			menuBuilder.setOwnerState(MenuState.SWITCHING);
			owner.openInventory(recentFilterMenu);
			return EditorClickType.NEUTRAL;
		});
		
		// Previous Page
		setAction(48, (clickEvent, slot) ->
		{
			currentPage--;
			open();
			return EditorClickType.NEUTRAL;
		});
		
		// Next Page
		setAction(50, (clickEvent, slot) ->
		{
			currentPage++;
			open();
			return EditorClickType.NEUTRAL;
		});
		
		EditorAction particleAction = (event, slot) ->
		{
			ItemMeta meta = event.getEvent().getCurrentItem().getItemMeta();
			if (meta != null)
			{
				CustomItemTagContainer container = meta.getCustomTagContainer();
				
				if (container.hasCustomTag(key, ItemTagType.INTEGER)) 
				{
					int id = container.getCustomTag(key, ItemTagType.INTEGER);
					ParticleEffect pe = ParticleEffect.fromID(id);
					
					Core.log("Clicking on " + pe.getName());
				}
			}
			return EditorClickType.NEUTRAL;
		};
		
		for (int i = 0; i < 45; i++) {
			setAction(i, particleAction);
		}
		
		// Add each particle that this server supports
		int index = 0;
		int page = 0;
		int colorIndex = 0;
		int dataIndex = 0;
		
		for (ParticleEffect pe : ParticleEffect.values())
		{
			Material material = pe.getMaterial();
			String name = pe.getName();
			ItemStack item = ItemUtil.createItem(material, name);
			item.getItemMeta().getCustomTagContainer().setCustomTag(key, ItemTagType.INTEGER, pe.getID());
			
			if (pe.hasColorData()) {
				colorFilterMenu.setItem(colorIndex++, item);
			}
			
			if (pe.hasData()) {
				dataFilterMenu.setItem(dataIndex++, item);
			}
			
			menus.get(page).setItem(index, item);
			index++;
			
			if (index % 45 == 0)
			{
				index = 0;
				page++;
			}
		}
	}

}
