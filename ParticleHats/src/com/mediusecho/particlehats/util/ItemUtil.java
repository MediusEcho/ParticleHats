package com.mediusecho.particlehats.util;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
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
		Material originalMaterial = Material.getMaterial(material);
		if (material != null) {
			return originalMaterial;
		}
		return fallback;
//		if (material == null) {
//			return fallback;
//		}
//		
//		try {
//			return Material.valueOf(material);
//		} catch (IllegalArgumentException e) {
//			return fallback;
//		}
	}
	
	public static Material getMaterial (String material, String fallback)
	{
		Material originalMaterial = Material.getMaterial(material);
		if (originalMaterial != null) {
			return originalMaterial;
		}
		
		Material fallbackMaterial = Material.getMaterial(fallback);
		if (fallbackMaterial != null) {
			return fallbackMaterial;
		}
		
		return Material.STONE;
		
//		// Try to find the original material
//		try {
//			return Material.valueOf(material);
//		}
//		
//		catch (IllegalArgumentException e)
//		{
//			// Try to find the fallback material
//			try {
//				return Material.valueOf(fallback);
//			}
//			
//			// Try to find the closest match
//			catch (IllegalArgumentException ex)
//			{
//				for (Material mat : Material.values())
//				{
//					if (mat.toString().toLowerCase().contains(fallback.toLowerCase())) {
//						return mat;
//					}
//				}
//			}
//		}
//		
//		// Accept defeat
//		return Material.STONE;
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param durability
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static ItemStack createItem (Material material, short durability)
	{
		if (ParticleHats.serverVersion < 13) 
		{
			ItemStack item = new ItemStack(material, 1, durability);
			ItemMeta itemMeta = item.getItemMeta();
			
			addItemFlags(itemMeta);
			
			item.setItemMeta(itemMeta);
			return item;
		}
		return createItem(material, 1);
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param durability
	 * @return
	 */
	public static ItemStack createItem (String material, short durability)
	{
		Material itemMaterial = getMaterial(material, Material.STONE);
		return createItem(itemMaterial, durability);
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param quantity
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (Material material, int quantity, String title, List<String> description)
	{
		ItemStack item = new ItemStack(material, quantity);
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setDisplayName(StringUtil.colorize(title));
		itemMeta.setLore(StringUtil.colorize(description));
		addItemFlags(itemMeta);
		
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
		
		itemMeta.setDisplayName(title);
		addItemFlags(itemMeta);
		
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
		
		addItemFlags(itemMeta);
		
		item.setItemMeta(itemMeta);
		return item;
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (Material material, String title, String... description) {
		return ItemUtil.createItem(material, 1, title, Arrays.asList(description));
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param quantity
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (Material material, int quantity, String title, String... description) {
		return createItem(material, quantity, title, Arrays.asList(description));
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param quantity
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (Material material, String title, List<String> description) {
		return createItem(material, 1, title, description);
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param quantity
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (Material material, int quantity, Message title, Message description) {
		return createItem(material, quantity, title.getValue(), StringUtil.parseDescription(description.getValue()));
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (Material material, Message title, Message description) {
		return createItem(material, 1, title.getValue(), StringUtil.parseDescription(description.getValue()));
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (Material material, String title, Message description) {
		return createItem(material, 1, title, StringUtil.parseDescription(description.getValue()));
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (Material material, Message title, String... description) {
		return createItem(material, 1, title.getValue(), description);
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param title
	 * @return
	 */
	public static ItemStack createItem (Material material, String title) {
		return createItem(material, 1, title);
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param title
	 * @return
	 */
	public static ItemStack createItem (Material material, Message title) {
		return createItem(material, 1, title.getValue());
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param quantity
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (CompatibleMaterial material, int quantity, String title, List<String> description)
	{
		ItemStack item = material.getItem(quantity);
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setDisplayName(StringUtil.colorize(title));
		itemMeta.setLore(StringUtil.colorize(description));
		addItemFlags(itemMeta);
		
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
	public static ItemStack createItem (CompatibleMaterial material, int quantity, String title)
	{
		ItemStack item = material.getItem(quantity);
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setDisplayName(title);
		addItemFlags(itemMeta);
		
		item.setItemMeta(itemMeta);
		return item;
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param quantity
	 * @return
	 */
	public static ItemStack createItem (CompatibleMaterial material, int quantity)
	{
		ItemStack item = material.getItem(quantity);
		ItemMeta itemMeta = item.getItemMeta();
		
		addItemFlags(itemMeta);
		
		item.setItemMeta(itemMeta);
		return item;
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (CompatibleMaterial material, String title, String... description) {
		return createItem(material, 1, title, Arrays.asList(description));
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param quantity
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (CompatibleMaterial material, int quantity, String title, String... description) {
		return createItem(material, quantity, title, Arrays.asList(description));
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param quantity
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (CompatibleMaterial material, String title, List<String> description) {
		return createItem(material, 1, title, description);
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param quantity
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (CompatibleMaterial material, int quantity, Message title, Message description) {
		return createItem(material, quantity, title.getValue(), StringUtil.parseDescription(description.getValue()));
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (CompatibleMaterial material, Message title, Message description) {
		return createItem(material, 1, title.getValue(), StringUtil.parseDescription(description.getValue()));
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (CompatibleMaterial material, String title, Message description) {
		return createItem(material, 1, title, StringUtil.parseDescription(description.getValue()));
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param title
	 * @param description
	 * @return
	 */
	public static ItemStack createItem (CompatibleMaterial material, Message title, String... description) {
		return createItem(material, 1, title.getValue(), description);
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param title
	 * @return
	 */
	public static ItemStack createItem (CompatibleMaterial material, String title) {
		return createItem(material, 1, title);
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @param material
	 * @param title
	 * @return
	 */
	public static ItemStack createItem (CompatibleMaterial material, Message title) {
		return createItem(material, 1, title.getValue());
	}
	
	/**
	 * Sets this item's description
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
	 * Sets this item's description
	 * @param item
	 * @param description
	 */
	public static void setItemDescription (ItemStack item, String... description) {
		setItemDescription(item, Arrays.asList(description));
	}
	
	/**
	 * Sets this item's name
	 * @param item
	 * @param name
	 */
	public static void setItemName (ItemStack item, String name)
	{
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setDisplayName(StringUtil.colorize(name));
		item.setItemMeta(itemMeta);
	}
	
	/**
	 * Sets this item's name
	 * @param item
	 * @param name
	 */
	public static void setItemName (ItemStack item, Message name) {
		setItemName(item, name.getValue());
	}
	
	/**
	 * Sets this item's name and description
	 * @param item
	 * @param name
	 * @param description
	 */
	public static void setNameAndDescription (ItemStack item, String name, List<String> description)
	{
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setDisplayName(StringUtil.colorize(name));
		itemMeta.setLore(description);
		addItemFlags(itemMeta);
		
		item.setItemMeta(itemMeta);
	}
	
	/**
	 * Sets this item's name and description
	 * @param item
	 * @param name
	 * @param description
	 */
	public static void setNameAndDescription (ItemStack item, Message name, Message description) {
		setNameAndDescription(item, name.getValue(), StringUtil.parseDescription(description.getValue()));
	}
	
	/**
	 * Sets this item's name and description
	 * @param item
	 * @param name
	 * @param description
	 */
	public static void setNameAndDescription (ItemStack item, String name, String... description) {
		setNameAndDescription(item, name, Arrays.asList(description));
	}
	
	/**
	 * Gives this item a shiny glimmer
	 * @param item
	 */
	public static void highlightItem (ItemStack item)
	{
		if (ParticleHats.serverVersion >= 8) 
		{
			ItemMeta itemMeta = item.getItemMeta();
			addItemFlags(itemMeta);
			itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
			
			item.setItemMeta(itemMeta);
		}
	}
	
	/**
	 * Removes an items enchanted glow
	 * @param item
	 */
	public static void stripHighlight (ItemStack item)
	{
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.removeEnchant(Enchantment.ARROW_DAMAGE);
		
		item.setItemMeta(itemMeta);
	}
	
	/**
	 * Sets this item's material type and durability
	 * @param item
	 * @param material
	 * @param damage
	 */
	@SuppressWarnings("deprecation")
	public static void setItemType (ItemStack item, Material material, short damage) 
	{
		item.setType(material);
		
		if (ParticleHats.serverVersion < 13) {
			item.setDurability(damage);
		}
	}
	
	/**
	 * Sets this item's material type and durability
	 * @param item
	 * @param material
	 * @param damage
	 */
	public static void setItemType (ItemStack item, Material material, int damage) {
		setItemType(item, material, (short) damage);
	}
	
	/**
	 * Sets this item's material type and durability
	 * @param item
	 * @param material
	 */
	public static void setItemType (ItemStack item, CompatibleMaterial material) {
		setItemType(item, material.getMaterial(), material.getDurability());
	}
	
	/**
	 * Sets this item's material type and durability
	 * @param item
	 * @param i
	 */
	@SuppressWarnings("deprecation")
	public static void setItemType (ItemStack item, ItemStack i) 
	{
		if (ParticleHats.serverVersion < 13) {
			setItemType(item, i.getType(), i.getDurability());
		} else {
			item.setType(i.getType());
		}
	}
	
	/**
	 * Attempts to apply ItemFlag values to this ItemStack
	 * @param itemMeta
	 */
	private static void addItemFlags (ItemMeta itemMeta)
	{
		try {
			itemMeta.addItemFlags(ItemFlag.values());
		} catch (NoClassDefFoundError e) {}
	}
}
