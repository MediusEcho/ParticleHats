package com.mediusecho.particlehats.util;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mediusecho.particlehats.locale.Message;

public class ItemUtil {

	/**
	 * Tries to return the Material enum that matches the name provided.<br>
	 * Returns fallback if nothing is found
	 * @param material The name of the material
	 * @param fallback Material returned if no match is found
	 * @return
	 */
	public static Material getMaterial(String material, Material fallback)
	{
		if (material == null) {
			return fallback;
		}
		
		try {
			return Material.valueOf(material);
		} catch (IllegalArgumentException e) {
			return fallback;
		}
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (Material material, String title, String... description) {
		return ItemUtil.createItem(material, 1, title, description);
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param quantity
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (Material material, int quantity, String title, String... description)
	{
		ItemStack item = new ItemStack(material, quantity);
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setDisplayName(StringUtil.colorize(title));
		itemMeta.setLore(StringUtil.colorize(Arrays.asList(description)));
		itemMeta.addItemFlags(ItemFlag.values());
		
		item.setItemMeta(itemMeta);
		return item;
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param quantity
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (Material material, String title, List<String> description)
	{
		ItemStack item = new ItemStack(material);
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setDisplayName(StringUtil.colorize(title));
		itemMeta.setLore(StringUtil.colorize(description));
		itemMeta.addItemFlags(ItemFlag.values());
		
		item.setItemMeta(itemMeta);
		return item;
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param quantity
	 * @return
	 */
	public static ItemStack createItem (Material material, int quantity)
	{
		ItemStack item = new ItemStack(material, quantity);
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.addItemFlags(ItemFlag.values());
		
		item.setItemMeta(itemMeta);
		return item;
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param quantity
	 * @param title
	 * @return
	 */
	public static ItemStack createItem (Material material, int quantity, String title)
	{
		ItemStack item = new ItemStack(material, quantity);
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setDisplayName(StringUtil.colorize(title));
		itemMeta.addItemFlags(ItemFlag.values());
		
		item.setItemMeta(itemMeta);
		return item;
	}
	
	/**
	 * Creates a new ItemStack
	 * @param material
	 * @param title
	 * @return
	 */
	public static ItemStack createItem (Material material, String title) {
		return ItemUtil.createItem(material, 1, title);
	}
	
	/**
	 * Creates a new ItemStack
	 * @param material
	 * @param title
	 * @return
	 */
	public static ItemStack createItem (Material material, Message title) {
		return createItem(material, 1, title.getValue());
	}
	
	/**
	 * 
	 * @param material
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (Material material, Message title, Message description) {
		return createItem(material, title.getValue(), StringUtil.parseDescription(description.getValue()));
	}
	
	/**
	 * 
	 * @param material
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (Material material, String title, Message description) {
		return createItem(material, title, StringUtil.parseDescription(description.getValue()));
	}
	
	/**
	 * Sets this items decription
	 * @param item
	 * @param description
	 */
	public static void setItemDescription (ItemStack item, String... description)
	{
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setLore(StringUtil.colorize(Arrays.asList(description)));
		item.setItemMeta(itemMeta);
	}
	
	/**
	 * Sets this items decription
	 * @param item
	 * @param description
	 */
	public static void setItemDescription (ItemStack item, List<String> description)
	{
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setLore(StringUtil.colorize(description));
		item.setItemMeta(itemMeta);
	}
	
	/**
	 * Sets this items displayName
	 * @param item
	 * @param name
	 */
	public static void setItemName (ItemStack item, String name)
	{
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setDisplayName(StringUtil.colorize(name));
		item.setItemMeta(itemMeta);
	}
	
	public static void setNameAndDescription (ItemStack item, String name, List<String> description)
	{
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setDisplayName(StringUtil.colorize(name));
		itemMeta.setLore(description);
		itemMeta.addItemFlags(ItemFlag.values());
		
		item.setItemMeta(itemMeta);
	}
	
	public static void setNameAndDescription (ItemStack item, String name, String... description) {
		setNameAndDescription(item, name, Arrays.asList(description));
	}
	
	/**
	 * Gives this item a shiny glimmer
	 * @param item
	 */
	public static void highlightItem (ItemStack item)
	{
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.addItemFlags(ItemFlag.values());
		itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
		
		item.setItemMeta(itemMeta);
	}
	
	public static void stripHighlight (ItemStack item)
	{
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.removeEnchant(Enchantment.ARROW_DAMAGE);
		
		item.setItemMeta(itemMeta);
	}
	
	/**
	 * Returns a Material matching the materialName, or the fallback material if that fails
	 * @param materialName
	 * @param fallbackMaterial
	 * @return
	 */
	public static Material materialFromString (String materialName, Material fallbackMaterial)
	{
		if (materialName == null) {
			return fallbackMaterial;
		}
		
		try {
			return Material.valueOf(materialName);
		} catch (IllegalArgumentException e) {
			return fallbackMaterial;
		}
	}
}
