package com.mediusecho.particlehats.editor.menus;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.effects.PixelEffect;
import com.mediusecho.particlehats.particles.properties.ParticleType;
import com.mediusecho.particlehats.ui.MenuState;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorTypeMenu extends EditorMenu {

	private final Hat targetHat;
	private final EditorGenericCallback callback;
	private final EditorAction setTypeAction;
	private final NamespacedKey key;
	
	private final String title = Message.EDITOR_TYPE_MENU_TITLE.getValue();
	private final String typePrefix = Message.EDITOR_TYPE_MENU_TYPE_PREFIX.getValue();
	
	private Map<String, BufferedImage> customTypes;
	
	private Map<Integer, Inventory> includedTypeMenus;
	private Map<Integer, Inventory> customTypeMenus;
	
	private Map<Integer, String> includedTypeData;
	private Map<Integer, String> customTypeData;
	
	private final int includedTypePages;
	private final int customTypePages;
	
	private int includedTypeCurrentPage;
	private int customTypeCurrentPage;
	
	public EditorTypeMenu(Core core, Player owner, MenuBuilder menuBuilder, EditorGenericCallback callback)
	{
		super(core, owner, menuBuilder);
		this.callback = callback;
		
		targetHat = menuBuilder.getTargetHat();
		
		includedTypeMenus = new HashMap<Integer, Inventory>();
		customTypeMenus = new HashMap<Integer, Inventory>();
		
		includedTypeData = new HashMap<Integer, String>();
		customTypeData = new HashMap<Integer, String>();
		
		customTypes = core.getDatabase().getImages(false);
		
		includedTypePages = (int) Math.ceil((double) ParticleType.values().length / 28D);
		customTypePages = (int) Math.max(Math.ceil((double) customTypes.size() / 28D), 1);
		
		includedTypeCurrentPage = 0;
		customTypeCurrentPage = 0;
		
		key = new NamespacedKey(core, "types");
		setTypeAction = (event, slot) ->
		{
			ItemMeta meta = event.getEvent().getCurrentItem().getItemMeta();
			if (meta != null)
			{
				CustomItemTagContainer container = meta.getCustomTagContainer();
				
				// Built-in types
				if (container.hasCustomTag(key, ItemTagType.INTEGER)) 
				{
					int id = container.getCustomTag(key, ItemTagType.INTEGER);
					ParticleType type = ParticleType.fromID(id);
					targetHat.setType(type);
					
					menuBuilder.goBack();
				}
				
				// User generated types
				else if (container.hasCustomTag(key, ItemTagType.STRING))
				{
					String name = container.getCustomTag(key, ItemTagType.STRING);
					BufferedImage image = customTypes.get(name);
					
					targetHat.setType(ParticleType.CUSTOM);
					targetHat.setCustomType(new PixelEffect(image, name, 0.2D));
					
					menuBuilder.goBack();
				}
			}
			return EditorClickType.NEUTRAL;
		};
		
		build();
	}
	
	@Override
	public void open ()
	{
		openMenu(includedTypeMenus, includedTypeCurrentPage);
		logBuildTime();
	}
	
	@Override
	public void onClose (boolean forced)
	{
		if (!forced) {
			callback.onExecute();
		}
	}
	
	private void openMenu (Map<Integer, Inventory> menus, int page)
	{
		menuBuilder.setOwnerState(MenuState.SWITCHING);
		owner.openInventory(menus.get(page));
	}

	@Override
	protected void build() 
	{
		setAction(49, backAction);
		for (int i = 0; i < 28; i++) {
			setAction(getNormalIndex(i, 10, 2), setTypeAction);
		}
		
		setAction(46, (event, slot) ->
		{
			openMenu(includedTypeMenus, includedTypeCurrentPage);
			return EditorClickType.NEUTRAL;
		});
		
		setAction(47, (event, slot) ->
		{
			openMenu(customTypeMenus, customTypeCurrentPage);
			return EditorClickType.NEUTRAL;
		});
		
		// Create our included type menus
		generateIncludedTypeMenus();
		
		// Create our custom type menus
		generateCustomTypeMenus();
	}

	private void generateIncludedTypeMenus ()
	{		
		generateMenus(includedTypeMenus, includedTypePages, 0);
		
		ParticleType currentType = targetHat.getType();
		int page = 0;
		int index = 0;
		int dataIndex = 0;
		
		for (ParticleType type : ParticleType.values())
		{
			if (type.isCustom()) {
				continue;
			}
			
			boolean selected = currentType.equals(type);
			
			String title = typePrefix + type.getDisplayName();
			ItemStack item = ItemUtil.createItem(Material.FIREWORK_STAR, StringUtil.colorize(title));
			EditorLore.updateTypeItemDescription(item, type, selected);
			
			item.getItemMeta().getCustomTagContainer().setCustomTag(key, ItemTagType.INTEGER, type.getID());
			
			if (selected) 
			{
				item.setType(Material.CYAN_DYE);
				ItemUtil.highlightItem(item);
			}
			
			includedTypeMenus.get(page).setItem(getNormalIndex(index++, 10, 2), item);
			includedTypeData.put(dataIndex++, type.getName());
			
			if (index % 28 == 0) 
			{
				index = 0;
				page++;
			}
		}
	}

	private void generateCustomTypeMenus ()
	{	
		generateMenus(customTypeMenus, customTypePages, 1);
		
		String description = Message.EDITOR_TYPE_MENU_CUSTOM_TYPE_DESCRIPTION.getValue();
		String[] selectInfo = StringUtil.parseValue(description, "1");
		String[] selectedInfo = StringUtil.parseValue(description, "2");
		
		if (customTypes.size() == 0) {
			customTypeMenus.get(0).setItem(22, ItemUtil.createItem(Material.BARRIER, Message.EDITOR_TYPE_MENU_NO_CUSTOM_TYPES));
		}
		
		else
		{
			String currentEffectName = "";
			if (targetHat.getType().isCustom())
			{
				PixelEffect customEffect = targetHat.getCustomEffect();
				if (customEffect != null) {
					currentEffectName = customEffect.getImageName();
				}
			}
			
			int page = 0;
			int index = 0;
			int dataIndex = 0;
			for (Entry<String, BufferedImage> types : customTypes.entrySet())
			{
				String name = types.getKey();
				boolean isSelected = name.equals(currentEffectName);
				
				String title = typePrefix + StringUtil.capitalizeFirstLetter(name.toLowerCase());
				ItemStack item = ItemUtil.createItem(Material.FIRE_CHARGE, StringUtil.colorize(title));
					
				String select = isSelected ? "" : selectInfo[1];
				String selected = isSelected ? selectedInfo[1] : "";
				String s = description
						.replace(selectInfo[0], select)
						.replace(selectedInfo[0], selected);
				ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
				
				item.getItemMeta().getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, name);
				
				if (isSelected) 
				{
					item.setType(Material.CYAN_DYE);
					ItemUtil.highlightItem(item);
				}
				
				customTypeMenus.get(page).setItem(getNormalIndex(index++, 10, 2), item);
				customTypeData.put(dataIndex++, name);
				
				if (index % 28 == 0) 
				{
					index = 0;
					page++;
				}
			}
		}
	}
	
	private void generateMenus (Map<Integer, Inventory> menus, int pages, int category)
	{	
		for (int i = 0; i < pages; i++)
		{
			String menuTitle = title.replace("{1}", Integer.toString(i + 1)).replace("{2}", Integer.toString(pages));
			Inventory menu = Bukkit.createInventory(null, 54, menuTitle);
			
			menu.setItem(49, backButton);
			
			// Next Page
			if ((i + 1) < pages) {
				menu.setItem(50, ItemUtil.createItem(Material.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
			}
			
			// Previous Page
			if ((i + 1) > 1) {
				menu.setItem(48, ItemUtil.createItem(Material.LIME_DYE, Message.EDITOR_MISC_PREVIOUS_PAGE));
			}
			
			menu.setItem(46, ItemUtil.createItem(Material.BOWL, Message.EDITOR_TYPE_MENU_INCLUDED_FILTER));
			menu.setItem(47, ItemUtil.createItem(Material.BOWL, Message.EDITOR_TYPE_MENU_CUSTOM_FILTER));
			
			switch (category)
			{
			case 0: menu.getItem(46).setType(Material.MUSHROOM_STEW); break;
			case 1: menu.getItem(47).setType(Material.MUSHROOM_STEW); break;
			}
			
			menus.put(i, menu);
		}
	}
}
