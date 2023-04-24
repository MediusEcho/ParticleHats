package com.mediusecho.particlehats.compatibility;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.util.ItemUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Material compatibility
 * 
 * Finds the closest matching material to the enum value
 * 
 * @author MediusEcho
 *
 */
public enum CompatibleMaterial {

	BARRIER ("INK_SACK:8"),
	BIRCH_BOAT ("BOAT_BIRCH"),
	BLACK_DYE ("INK_SACK"),
	BLACK_STAINED_GLASS_PANE ("STAINED_GLASS_PANE:15"),
	BLUE_STAINED_GLASS_PANE ("STAINED_GLASS_PANE:11"),
	BONE_MEAL ("INK_SACK:15"),
	BROWN_DYE ("INK_SACK:3"),
	BROWN_STAINED_GLASS_PANE ("STAINED_GLASS_PANE:12"),
	CACTUS_GREEN ("INK_SACK:2", "GREEN_DYE"),
	CAMPFIRE ("TORCH"),
	CAULDRON ("CAULDRON_ITEM"),
	CHERRY_LEAVES,
	CHERRY_LOG,
	CHERRY_WOOD,
	COMPARATOR ("REDSTONE_COMPARATOR"),
	COMPOSTER ("COMPOSTER"),
	CONDUIT ("CLAY_BALL"),
	COPPER_BLOCK ("STONE"),
	CRIMSON_FUNGUS ("MUSHROOM"),
	CRYING_OBSIDIAN ("OBSIDIAN"),
	CYAN_CANDLE ("STONE"),
	CYAN_DYE ("INK_SACK:6"),
	CYAN_STAINED_GLASS_PANE ("STAINED_GLASS_PANE:9"),
	DANDELION ("FLOWER", "DANDELION"),
	DRAGON_HEAD ("SKULL_ITEM:5"),
	DRIPSTONE_BLOCK ("STONE"),
	EXPERIENCE_BOTTLE ("EXP_BOTTLE"),
	ENCHANTING_TABLE ("ENCHANTMENT_TABLE"),
	END_ROD,
	FIRE_CHARGE ("FIREBALL"),
	FIREWORK_ROCKET ("FIREWORK_CHARGE"),
	FIREWORK_STAR ("FIREWORK_CHARGE"),
	GLOW_INK_SAC("INK_SACK:8"),
	GRAY_DYE ("INK_SACK:8"),
	GRAY_STAINED_GLASS_PANE ("STAINED_GLASS_PANE:7"),
	GREEN_STAINED_GLASS_PANE ("STAINED_GLASS_PANE:13"),
	GUNPOWDER ("SULPHUR"),
	HORN_CORAL ("ROSE_RED:8"),
	HONEYCOMB ("HONEYCOMB", "POTION"),
	HONEY_BOTTLE ("HONEY_BOTTLE", "POTION"),
	INK_SAC ("INK_SACK"),
	LANTERN ("TORCH"),
	LAPIS_LAZULI ("INK_SACK:4"),
	LIGHT ("STONE"),
	LIGHTNING_ROD ("STONE"),
	LIGHT_BLUE_DYE ("INK_SACK:12"),
	LIGHT_BLUE_STAINED_GLASS_PANE ("STAINED_GLASS_PANE:3"),
	LIGHT_GRAY_DYE ("INK_SACK:7"),
	LIGHT_GRAY_STAINED_GLASS_PANE ("STAINED_GLASS_PANE:8"),
	LIME_DYE ("INK_SACK:10"),
	LIME_STAINED_GLASS_PANE ("STAINED_GLASS_PANE:5"),
	MAGENTA_DYE ("INK_SACK:13"),
	MAGENTA_STAINED_GLASS_PANE ("STAINED_GLASS_PANE:2"),
	MAGMA_BLOCK ("MAGMA", "MAGMA_CREAM"),
	MAP ("EMPTY_MAP"),
	MUSHROOM_STEW ("MUSHROOM_SOUP"),
	MUSIC_DISC_CAT ("GREEN_RECORD"),
	MUSIC_DISC_BLOCKS ("RECORD_3"),
	MUSIC_DISC_FAR ("RECORD_5"),
	MUSIC_DISC_STRAD ("RECORD_9"),
	OAK_BOAT ("BOAT"),
	ORANGE_DYE ("INK_SACK:14"),
	ORANGE_STAINED_GLASS_PANE ("STAINED_GLASS_PANE:1"),
	OXIDIZED_COPPER ("STONE"),
	PINK_DYE ("INK_SACK:9"),
	PINK_STAINED_GLASS_PANE ("STAINED_GLASS_PANE:6"),
	PLAYER_HEAD ("SKULL_ITEM:3"),
	PRISMARINE_SHARD ("RAW_FISH"),
	POINTED_DRIPSTONE ("STONE"),
	POWDER_SNOW_BUCKET ("BUCKET"),
	PUFFERFISH ("RAW_FISH:3"),
	PURPLE_DYE ("INK_SACK:5"),
	PURPLE_STAINED_GLASS_PANE ("STAINED_GLASS_PANE:10"),
	REDSTONE_TORCH ("REDSTONE_TORCH_ON"),
	RED_STAINED_GLASS_PANE ("STAINED_GLASS_PANE:14"),
	REPEATER ("DIODE"),
	ROSE_RED ("INK_SACK:1", "RED_DYE"),
	SCUTE ("BOWL"),
	SCULK ("STONE"),
	SCULK_SENSOR ("STONE"),
	SCULK_SHRIEKER ("STONE"),
	SCULK_CATALYST ("STONE"),
	SCULK_VEIN ("STONE"),
	SIGN ("SIGN", "OAK_SIGN"),
	SNOWBALL ("SNOW_BALL"),
	SOUL_CAMPFIRE ("CAMPFIRE", "TORCH"),
	SOUL_LANTERN ("LANTERN", "TORCH"),
	SPLASH_POTION ("POTION"),
	SPORE_BLOSSOM ("STONE"),
	SUNFLOWER ("DOUBLE_PLANT"),
	TOTEM_OF_UNDYING ("TOTEM"),
	TURTLE_HELMET ("LEATHER_HELMET"),
	WARPED_FUNGUS ("BROWN_MUSHROOM"),
	WEATHERED_COPPER ("STONE"),
	WHEAT_SEEDS ("SEEDS"),
	WHITE_DYE ("INK_SACK:15"),
	WHITE_STAINED_GLASS_PANE ("STAINED_GLASS_PANE"),
	WITHER_SKELETON_SKULL ("SKULL_ITEM:1"),
	WRITABLE_BOOK ("BOOK_AND_QUILL"),
	YELLOW_DYE ("INK_SACK:11"),
	YELLOW_STAINED_GLASS_PANE ("STAINED_GLASS_PANE:4"),
	ZOMBIE_HEAD ("SKULL_ITEM:2");
	
	private Material material = Material.STONE;
	private int durability = 0;

	CompatibleMaterial (@NotNull String... aliases)
	{
		Material material = Material.matchMaterial(this.toString());
		if (material != null)
		{
			this.material = material;
			return;
		}

		for (String alias : aliases)
		{
			// Look for a legacy material & damage value
			if (alias.contains(":"))
			{
				String[] aliasData = alias.split(":");
				durability = Integer.parseInt(aliasData[1]);
				material = Material.matchMaterial(aliasData[0]);
			}

			// Look for a 1.13+ material
			else {
				material = Material.matchMaterial(alias);
			}

			if (material != null)
			{
				this.material = material;
				return;
			}
		}
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
			return ItemUtil.createItem(material, quantity);
		} else {
			return ItemUtil.createItem(material, (short) durability, quantity);
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
}
