package com.mediusecho.particlehats.editor.menus;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.ui.GuiState;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorParticleSelectionMenu extends EditorMenu {

	private final Map<Integer, Inventory> menus;
	
	private Inventory colorFilterMenu;
	private Inventory dataFilterMenu;
	private Inventory recentFilterMenu;
	
	private Map<Integer, ParticleEffect> particleData;
	private final int colorStartingIndex = -45;
	private final int dataStartingIndex = -90;
	private final int recentStartingIndex = -135;
	
	private MenuType menuType = MenuType.PARTICLES;
	
	private final EditorAction particleSelectAction;
	private final int particleIndex;
	private final Hat targetHat;
	
	private final int totalPages;
	private int currentPage = 0;
	
	public EditorParticleSelectionMenu(Core core, Player owner, MenuBuilder menuBuilder, int particleIndex, EditorParticleCallback callback) 
	{
		super(core, owner, menuBuilder);
		this.particleIndex = particleIndex;
		this.targetHat = menuBuilder.getTargetHat();
		
		menus = new HashMap<Integer, Inventory>();
		particleData = new HashMap<Integer, ParticleEffect>();
		
		totalPages = (int) Math.ceil((double) ParticleEffect.getParticlesSupported() / 45D);
		
		particleSelectAction = (event, slot) ->
		{
			int index = slot;
			switch (menuType)
			{
				case PARTICLES:
					index = slot + (currentPage * 45);
					break;
					
				case COLOR:
					index = colorStartingIndex + slot;
					break;
					
				case DATA:
					index = dataStartingIndex + slot;
					break;
					
				case RECENTS:
					index = recentStartingIndex + getClampedIndex(slot, 10, 2);
			}
			
			if (particleData.containsKey(index)) {
				callback.onSelect(particleData.get(index));
			}
			return EditorClickType.NEUTRAL;
		};
		
		colorFilterMenu  = Bukkit.createInventory(null, 54, Message.EDITOR_PARTICLE_MENU_COLOUR_FILTER_TITLE.getValue());
		dataFilterMenu   = Bukkit.createInventory(null, 54, Message.EDITOR_PARTICLE_MENU_DATA_FILTER_TITLE.getValue());
		recentFilterMenu = Bukkit.createInventory(null, 54, Message.EDITOR_PARTICLE_MENU_RECENT_FILTER_TITLE.getValue());
		
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
			menu.setItem(45, ItemUtil.createItem(CompatibleMaterial.MUSHROOM_STEW, Message.EDITOR_PARTICLE_MENU_NORMAL_FILTER));
			menu.setItem(46, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_MENU_COLOUR_FILTER));
			menu.setItem(47, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_MENU_DATA_FILTER));
			menu.setItem(53, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_MENU_RECENT_FILTER));
			
			if ((i + 1) < totalPages) {
				menu.setItem(50, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
			}
			
			if ((i + 1) > 1) {
				menu.setItem(48, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_PREVIOUS_PAGE));
			}
			
			menus.put(i, menu);
		}
		
		// Populate our filter menus
		colorFilterMenu.setItem(49, backButton);
		colorFilterMenu.setItem(45, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_MENU_NORMAL_FILTER));
		colorFilterMenu.setItem(46, ItemUtil.createItem(CompatibleMaterial.MUSHROOM_STEW, Message.EDITOR_PARTICLE_MENU_COLOUR_FILTER));
		colorFilterMenu.setItem(47, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_MENU_DATA_FILTER));
		colorFilterMenu.setItem(53, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_MENU_RECENT_FILTER));
		
		dataFilterMenu.setItem(49, backButton);
		dataFilterMenu.setItem(45, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_MENU_NORMAL_FILTER));
		dataFilterMenu.setItem(46, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_MENU_COLOUR_FILTER));
		dataFilterMenu.setItem(47, ItemUtil.createItem(CompatibleMaterial.MUSHROOM_STEW, Message.EDITOR_PARTICLE_MENU_DATA_FILTER));
		dataFilterMenu.setItem(53, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_MENU_RECENT_FILTER));
		
		recentFilterMenu.setItem(49, backButton);
		recentFilterMenu.setItem(45, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_MENU_NORMAL_FILTER));
		recentFilterMenu.setItem(46, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_MENU_COLOUR_FILTER));
		recentFilterMenu.setItem(47, ItemUtil.createItem(Material.BOWL, Message.EDITOR_PARTICLE_MENU_DATA_FILTER));
		recentFilterMenu.setItem(53, ItemUtil.createItem(CompatibleMaterial.MUSHROOM_STEW, Message.EDITOR_PARTICLE_MENU_RECENT_FILTER));
		
		setAction(49, (clickEvent, slot) ->
		{
			menuBuilder.goBack();
			return EditorClickType.NEUTRAL;
		});
		
		// All Particles
		setAction(45, (clickEvent, slot) ->
		{
			menuType = MenuType.PARTICLES;
			menuBuilder.setOwnerState(GuiState.SWITCHING_EDITOR);
			//menuBuilder.setOwnerState(MenuState.SWITCHING);
			owner.openInventory(menus.get(currentPage));
			return EditorClickType.NEUTRAL;
		});
		
		// Color Filter
		setAction(46, (clickEvent, slot) ->
		{
			menuType = MenuType.COLOR;
			menuBuilder.setOwnerState(GuiState.SWITCHING_EDITOR);
			//menuBuilder.setOwnerState(MenuState.SWITCHING);
			owner.openInventory(colorFilterMenu);
			return EditorClickType.NEUTRAL;
		});
		
		// Data Filter
		setAction(47, (clickEvent, slot) ->
		{
			menuType = MenuType.DATA;
			menuBuilder.setOwnerState(GuiState.SWITCHING_EDITOR);
			//menuBuilder.setOwnerState(MenuState.SWITCHING);
			owner.openInventory(dataFilterMenu);
			return EditorClickType.NEUTRAL;
		});
		
		// Recent Filter
		setAction(53, (clickEvent, slot) ->
		{
			menuType = MenuType.RECENTS;
			menuBuilder.setOwnerState(GuiState.SWITCHING_EDITOR);
			//menuBuilder.setOwnerState(MenuState.SWITCHING);
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
		
		for (int i = 0; i < 45; i++) {
			setAction(i, particleSelectAction);
		}
		
		// Add each particle that this server supports
		int index = 0;
		int page = 0;
		int colorIndex = 0;
		int dataIndex = 0;
		
		ParticleEffect currentEffect = targetHat.getParticle(particleIndex);

		int defaultIndex = 0;
		int colorFilterIndex = colorStartingIndex;
		int dataFilterIndex = dataStartingIndex;
		
		for (ParticleEffect pe : ParticleEffect.values())
		{
			if (!pe.isSupported()) {
				continue;
			}
			
			ItemStack item = pe.getItem().clone();
			//Material material = pe.getMaterial();
			String name = pe.getDisplayName();
			ItemUtil.setItemName(item, name);
			//ItemStack item = ItemUtil.createItem(material, name);
			
			boolean selected = false;
			if (pe.equals(currentEffect)) 
			{
				ItemUtil.highlightItem(item);
				selected = true;
			}
			
			EditorLore.updateParticleItemDescription(item, pe, selected);
			
			if (pe.hasColorData()) 
			{
				colorFilterMenu.setItem(colorIndex++, item);
				particleData.put(colorFilterIndex++, pe);
			}
			
			if (pe.hasData()) 
			{
				dataFilterMenu.setItem(dataIndex++, item);
				particleData.put(dataFilterIndex++, pe);
			}
			
			menus.get(page).setItem(index, item);
			particleData.put(defaultIndex++, pe);
			index++;
			
			if (index % 45 == 0)
			{
				index = 0;
				page++;
			}
		}
		
		// Recently Used. Stored locally on each server
		int recentIndex = recentStartingIndex;
		index = 0;
		for (ParticleEffect pe : core.getParticleManager().getRecentlyUsedParticles(ownerID))
		{
			ItemStack item = pe.getItem().clone();
			//Material material = pe.getMaterial();
			String name = pe.getDisplayName();
			ItemUtil.setItemName(item, name);
			//ItemStack item = ItemUtil.createItem(material, name);
			
			boolean selected = false;
			if (pe.equals(currentEffect)) 
			{
				ItemUtil.highlightItem(item);
				selected = true;
			}
			
			EditorLore.updateParticleItemDescription(item, pe, selected);
			
			particleData.put(recentIndex++, pe);
			recentFilterMenu.setItem(getNormalIndex(index++, 10, 2), item);
		}
	}

	private enum MenuType
	{
		PARTICLES,
		COLOR,
		DATA,
		RECENTS;
	}
}
