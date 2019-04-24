package com.mediusecho.particlehats.editor.menus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.ui.GuiState;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorPotionMenu extends EditorMenu {

	private final String menuTitle = Message.EDITOR_POTION_MENU_TITLE.getValue();
	private final String potionTitle = Message.EDITOR_POTION_MENU_POTION_TITLE.getValue();
	private final String potionSelected = Message.EDITOR_POTION_MENU_POTION_DESCRIPTION.getValue();
	
	private final Map<Integer, Inventory> menus;
	private final Map<Integer, PotionEffectType> potions;
	
	private final Hat targetHat;
	private final EditorAction selectAction;
	
	private EditorGenericCallback callback;
	
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
	
	public EditorPotionMenu(Core core, Player owner, MenuBuilder menuBuilder, EditorGenericCallback callback) 
	{
		super(core, owner, menuBuilder);
		
		this.callback = callback;
		
		menus = new HashMap<Integer, Inventory>();
		potions = new HashMap<Integer, PotionEffectType>();
		
		targetHat = menuBuilder.getBaseHat();
		
		selectAction = (event, slot) ->
		{
			int index = getClampedIndex(slot, 10, 2);
			if (potions.containsKey(index)) 
			{				
				targetHat.setPotion(potions.get(index), targetHat.getPotionAmplifier());
				menuBuilder.goBack();
			}
			return EditorClickType.NEUTRAL;
		};
		
		inventory = Bukkit.createInventory(null, 54);
		build();
	}
	
	@Override
	public void onClose (boolean forced)
	{
		if (!forced) {
			callback.onExecute();
		}
	}
	
	@Override
	public void open ()
	{
		if (menus.containsKey(currentPage))
		{
			menuBuilder.setOwnerState(GuiState.SWITCHING_EDITOR);
			//menuBuilder.setOwnerState(MenuState.SWITCHING);
			owner.openInventory(menus.get(currentPage));
		}
	}

	@Override
	protected void build() 
	{	
		for (int i = 0; i < 28; i++) {
			setAction(getNormalIndex(i, 10, 2), selectAction);
		}
		
		setAction(52, (event, slot) ->
		{
			int strength = 1;
			PotionEffect pe = targetHat.getPotion();
			
			if (pe != null) {
				strength = pe.getAmplifier();
			}
			
			strength += event.isLeftClick() ? 1 : -1;
			targetHat.setPotionAmplifier(strength);
			
			ItemStack item = menus.get(currentPage).getItem(52);
			EditorLore.updatePotionStrengthDescription(item, targetHat.getPotionAmplifier());
			
			return event.isLeftClick() ? EditorClickType.POSITIVE : EditorClickType.NEGATIVE;
		});
		
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
			
			// Potion Strength
			menu.setItem(52, ItemUtil.createItem(Material.GHAST_TEAR, Message.EDITOR_POTION_MENU_SET_STRENGTH));
			EditorLore.updatePotionStrengthDescription(menu.getItem(52), targetHat.getPotionAmplifier());
			
			menus.put(i, menu);
		}
		
		PotionEffect currentPotion = targetHat.getPotion();
		PotionEffectType currentType = null;
		
		if (currentPotion != null) {
			currentType = currentPotion.getType();
		}
		
		String[] selectInfo = StringUtil.parseValue(potionSelected, "1");
		String[] selectedInfo = StringUtil.parseValue(potionSelected, "2");	
		
		int index = 0;
		int page = 0;
		
		for (PotionEffectType potionType : PotionEffectType.values())
		{
			if (potionBlacklist.contains(potionType)) {
				continue;
			}
			
			String name = StringUtil.capitalizeFirstLetter(potionType.getName().toLowerCase());
			ItemStack item = ItemUtil.createItem(Material.POTION, potionTitle.replace("{1}", name));
					
			if (currentType != null && currentType.equals(potionType))
			{
				ItemUtil.highlightItem(item);
				
				String description = potionSelected
						.replace(selectInfo[0], "")
						.replace(selectedInfo[0], selectedInfo[1]);
						
				ItemUtil.setItemDescription(item, StringUtil.parseDescription(description));
			}
			
			else
			{
				String description = potionSelected
						.replace(selectInfo[0], selectInfo[1])
						.replace(selectedInfo[0], "");
						
				ItemUtil.setItemDescription(item, StringUtil.parseDescription(description));
			}
			
			menus.get(page).setItem(getNormalIndex(index, 10, 2), item);
			potions.put(index, potionType);
			
			index++;
			if (index % 28 == 0)
			{
				index = 0;
				page++;
			}
		}
		
		setButton(49, backButton, backAction);
	}

}
