package com.mediusecho.particlehats.compatibility;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
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

	BARRIER ("INK_SACK", 8),
	BIRCH_BOAT ("BOAT_BIRCH", 0),
	BLACK_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 15),
	BLUE_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 11),
	BONE_MEAL ("INK_SACK", 15),
	BROWN_DYE ("INK_SACK", 3),
	BROWN_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 12),
	
	CACTUS_GREEN (2, "INK_SACK", "GREEN_DYE"),
	CAMPFIRE ("TORCH", 0),
	CAULDRON ("CAULDRON_ITEM", 0),
	COMPARATOR ("REDSTONE_COMPARATOR", 0),
	CONDUIT ("CLAY_BALL", 0),
	CYAN_DYE ("INK_SACK", 6),
	CYAN_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 9),
	
	DRAGON_HEAD ("SKULL_ITEM", 5),
	
	EXPERIENCE_BOTTLE ("EXP_BOTTLE", 0),
	ENCHANTING_TABLE ("ENCHANTMENT_TABLE", 0),
	END_ROD,
	
	FIRE_CHARGE ("FIREBALL", 0),
	FIREWORK_ROCKET ("FIREWORK_CHARGE", 0),
	FIREWORK_STAR ("FIREWORK_CHARGE", 0),
	
	GRAY_DYE ("INK_SACK", 8),
	GRAY_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 7),
	GREEN_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 13),
	GUNPOWDER ("SULPHUR", 0),
	
	HORN_CORAL ("ROSE_RED", 8),
	
	INK_SAC (0, "INK_SACK"),
	
	LANTERN (0, "TORCH"),
	LAPIS_LAZULI ("INK_SACK", 4),
	LIGHT_BLUE_DYE ("INK_SACK", 12),
	LIGHT_BLUE_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 3),
	LIGHT_GRAY_DYE ("INK_SACK", 7),
	LIGHT_GRAY_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 8),
	LIME_DYE ("INK_SACK", 10),
	LIME_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 5),

	MAGENTA_DYE ("INK_SACK", 13),
	MAGENTA_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 2),
	MAGMA_BLOCK ("MAGMA", 0),
	MAP ("EMPTY_MAP", 0),
	MUSHROOM_STEW ("MUSHROOM_SOUP", 0),
	MUSIC_DISC_CAT ("GREEN_RECORD",  0),
	MUSIC_DISC_BLOCKS ("RECORD_3", 0),
	MUSIC_DISC_FAR ("RECORD_5", 0),
	MUSIC_DISC_STRAD ("RECORD_9", 0),
	
	OAK_BOAT ("BOAT", 0),
	ORANGE_DYE ("INK_SACK", 14),
	ORANGE_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 1),
	
	PINK_DYE ("INK_SACK", 9),
	PINK_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 6),
	PLAYER_HEAD ("SKULL_ITEM", 3),
	PRISMARINE_SHARD ("RAW_FISH", 0),
	PUFFERFISH ("RAW_FISH", 3),
	PURPLE_DYE ("INK_SACK", 5),
	PURPLE_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 10),
	
	REDSTONE_TORCH ("REDSTONE_TORCH_ON", 0),
	RED_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 14),
	REPEATER ("DIODE", 0),
	ROSE_RED (1, "INK_SACK", "RED_DYE"),

	SCUTE ("BOWL", 0),
	SNOWBALL ("SNOW_BALL", 0),
	SPLASH_POTION ("POTION", 0),
	SUNFLOWER ("DOUBLE_PLANT", 0),
	
	TOTEM_OF_UNDYING ("TOTEM", 0),
	TURTLE_HELMET ("LEATHER_HELMET", 0),
	
	WHEAT_SEEDS ("SEEDS", 0),
	WHITE_DYE ("INK_SACK", 15),
	WHITE_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 0),
	WITHER_SKELETON_SKULL ("SKULL_ITEM", 1),
	WRITABLE_BOOK ("BOOK_AND_QUILL", 0),
	
	YELLOW_DYE ("INK_SACK", 11),
	YELLOW_STAINED_GLASS_PANE ("STAINED_GLASS_PANE", 4),
	
	ZOMBIE_HEAD ("SKULL_ITEM", 2);
	
	private final Material material;
	private final int durability;
	
	/**
	 * 
	 * @param durability
	 * @param aliases
	 */
	private CompatibleMaterial (int durability, String... aliases)
	{
		this.durability = durability;
		this.material = findClosestMatch(toString(), aliases);
	}
	
	private CompatibleMaterial (String fallback, int durability)
	{
		this.material = findClosestMatch(toString(), fallback);
		this.durability = durability;
	}
	
	private CompatibleMaterial () 
	{
		this.material = findClosestMatch(toString(), "STONE");
		this.durability = 0;
	}
	
	/**
	 * Get this CompatibleMaterial's compatible Material
	 * @return
	 */
	public Material getMaterial () {
		return material;
	}
	
	/**
	 * Get this CompatibleMaterial's durability
	 * @return
	 */
	public int getDurability () {
		return durability;
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @return
	 */
	public ItemStack getItem () {
		return getItem(1);
	}
	
	/**
	 * Creates and returns a new ItemStack
	 * @return
	 */
	public ItemStack getItem (int quantity)
	{
		if (ParticleHats.serverVersion >= 13) {
			return ItemUtil.createItem(material, 1);
		} else {
			return ItemUtil.createItem(material, (short) durability);
		}
	}
	
	/**
	 * Get the CompatibleMaterial that matches this name, or fallback if none are found
	 * @param name
	 * @param fallback
	 * @return
	 */
	public static CompatibleMaterial fromName (String name, CompatibleMaterial fallback)
	{
		try {
			return CompatibleMaterial.valueOf(name);
		} catch (IllegalArgumentException e) {
			return fallback;
		}
	}
	
	/**
	 * Finds the closest material value for the given server version
	 * @param material
	 * @param aliases List of alternate names this material could have
	 * @return
	 */
	private Material findClosestMatch (String material, String... aliases)
	{
		Material originalMaterial = Material.getMaterial(material);
		if (originalMaterial != null) {
			return originalMaterial;
		}
		
		for (String alias : aliases)
		{
			Material aliasMaterial = Material.getMaterial(alias);
			if (aliasMaterial != null) {
				return aliasMaterial;
			}
		}
		
		return Material.STONE;
	}
}
