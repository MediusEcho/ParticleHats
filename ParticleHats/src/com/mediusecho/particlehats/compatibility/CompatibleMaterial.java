package com.mediusecho.particlehats.compatibility;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.util.ItemUtil;

/**
 * Material compatibility
 * 
 * Finds the closest matching material to the enum value
 * 
 * @author MediusEcho
 *
 */
public enum CompatibleMaterial {

	BIRCH_BOAT ("BOAT_BIRCH", 0),
	OAK_BOAT ("BOAT", 0),
	
	INK_SAC ("INK_SACK", 0),
	ROSE_RED ("INK_SACK", 1),
	CACTUS_GREEN ("INK_SACK", 2),
	BROWN_DYE ("INK_SACK", 3),
	LAPIS_LAZULI ("INK_SACK", 4),
	PURPLE_DYE ("INK_SACK", 5),
	CYAN_DYE ("INK_SACK", 6),
	LIGHT_GRAY_DYE ("INK_SACK", 7),
	GRAY_DYE ("INK_SACK", 8),
	PINK_DYE ("INK_SACK", 9),
	LIME_DYE ("INK_SACK", 10),
	YELLOW_DYE ("INK_SACK", 11),
	LIGHT_BLUE_DYE ("INK_SACK", 12),
	MAGENTA_DYE ("INK_SACK", 13),
	ORANGE_DYE ("INK_SACK", 14),
	BONE_MEAL ("INK_SACK", 15),
	
	WHITE_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 0),
	ORANGE_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 1),
	MAGENTA_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 2),
	LIGHT_BLUE_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 3),
	YELLOW_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 4),
	LIME_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 5),
	PINK_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 6),
	GRAY_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 7),
	LIGHT_GRAY_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 8),
	CYAN_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 9),
	PURPLE_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 10),
	BLUE_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 11),
	BROWN_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 12),
	GREEN_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 13),
	RED_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 14),
	BLACK_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 15),
	
	PRISMARINE_SHARD ("RAW_FISH", 0),
	REDSTONE_TORCH ("REDSTONE_TORCH_ON", 0),
	WRITABLE_BOOK ("BOOK_AND_QUILL", 0),
	EXPERIENCE_BOTTLE ("EXP_BOTTLE", 0),
	BARRIER ("INK_SACK", 8),
	CAULDRON ("CAULDRON_ITEM", 0),
	COMPARATOR ("REDSTONE_COMPARATOR", 0),
	CONDUIT,
	DRAGON_HEAD,
	ENCHANTING_TABLE ("ENCHANTMENT_TABLE", 0),
	END_ROD,
	FIRE_CHARGE ("FIREBALL", 0),
	FIREWORK_ROCKET ("FIREWORK_CHARGE", 0),
	FIREWORK_STAR ("FIREWORK_CHARGE", 0),
	GUNPOWDER ("SULPHUR", 0),
	HORN_CORAL,
	MAGMA_BLOCK ("MAGMA", 0),
	MAP ("EMPTY_MAP", 0),
	MUSHROOM_STEW ("MUSHROOM_SOUP", 0),
	MUSIC_DISC_STRAD ("RECORD_9", 0),
	PLAYER_HEAD ("SKULL_ITEM", 3),
	PUFFERFISH ("RAW_FISH", 3),
	REPEATER ("DIODE", 0),
	SCUTE ("BOWL", 0),
	SNOWBALL ("SNOW_BALL", 0),
	SUNFLOWER ("DOUBLE_PLANT", 0),
	SPLASH_POTION ("POTION", 0),
	TOTEM_OF_UNDYING ("TOTEM", 0),
	TURTLE_HELMET ("LEATHER_HELMET", 0),
	WITHER_SKELETON_SKULL ("SKULL_ITEM", 1),
	WHEAT_SEEDS ("SEEDS", 0),
	WHITE_DYE,
	ZOMBIE_HEAD ("SKULL_ITEM", 2);
	
	private final Material material;
	private final int damage;
	
	private CompatibleMaterial (String fallback, int damage)
	{
		this.material = ItemUtil.getMaterial(this.toString(), fallback);
		this.damage = damage;
	}
	
	private CompatibleMaterial () 
	{
		this.material = ItemUtil.getMaterial(this.toString(), "STONE");
		this.damage = 0;
	}
	
	public Material getMaterial () {
		return material;
	}
	
	public int getDamage () {
		return damage;
	}
	
	public ItemStack getItem () {
		return getItem(1);
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack getItem (int quantity)
	{
		if (Core.serverVersion >= 13) {
			return new ItemStack(material, quantity);
		} else {
			return new ItemStack(material, quantity, (short) damage);
		}
	}
	
	public static CompatibleMaterial fromName (String name, CompatibleMaterial fallback)
	{
		try {
			return CompatibleMaterial.valueOf(name);
		} catch (IllegalArgumentException e) {
			return fallback;
		}
	}
}
