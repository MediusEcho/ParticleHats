package com.mediusecho.particlehats.editor.menus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffectType;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.ui.MenuState;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorPotionMenu extends EditorMenu {

	private final String menuTitle = Message.EDITOR_POTION_MENU_TITLE.getValue();
	
	private final Map<Integer, Inventory> menus;
	private final Map<Integer, PotionEffectType> potions;
	
	private final Hat targetHat;
	
	private int currentPage = 0;
	private int pages = 0;
	
	private final List<PotionEffectType> potionBlacklist = Arrays.asList(
			PotionEffectType.CONFUSION,
			PotionEffectType.HARM,
			PotionEffectType.POISON,
			PotionEffectType.WEAKNESS,
			PotionEffectType.WITHER,
			PotionEffectType.UNLUCK,
			PotionEffectType.HUNGER);
	
	public EditorPotionMenu(Core core, Player owner, MenuBuilder menuBuilder) 
	{
		super(core, owner, menuBuilder);
		
		menus = new HashMap<Integer, Inventory>();
		potions = new HashMap<Integer, PotionEffectType>();
		
		targetHat = menuBuilder.getBaseHat();
		
		inventory = Bukkit.createInventory(null, 54);
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
	}

	@Override
	protected void build() 
	{
		int potionCount = 0;
		for (PotionEffectType potionType : PotionEffectType.values())
		{
			if (potionBlacklist.contains(potionType)) {
				continue;
			}
			potionCount++;
		}
		
		pages = Math.max((int) Math.ceil(potionCount / 28D), 1);
		for (int i = 0; i < pages; i++)
		{
			String title = menuTitle.replace("{1}", Integer.toString(i + 1)).replace("{2}", Integer.toString(pages));
			Inventory menu = Bukkit.createInventory(null, 54, title);
			
			menu.setItem(49, backButton);
			
			// Next Page
			if ((i + 1) < pages) {
				menu.setItem(50, ItemUtil.createItem(Material.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
			}
			
			// Previous Page
			if ((i + 1) > 1) {
				menu.setItem(48, ItemUtil.createItem(Material.LIME_DYE, Message.EDITOR_MISC_PREVIOUS_PAGE));
			}
			
			menus.put(i, menu);
		}
		
		setButton(49, backButton, backAction);
	}

}
