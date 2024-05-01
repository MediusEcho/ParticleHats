package com.mediusecho.particlehats.particles;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public enum ParticleEffect {

	NONE                   (0,  0, (v) -> true, CompatibleMaterial.SCUTE),
	BARRIER                (1,  35, (v) -> v >= 8, "barrier", CompatibleMaterial.BARRIER),
	BLOCK_CRACK            (2,  37, (v) -> true, "blockcrack", Material.DIAMOND_PICKAXE, ParticleProperty.BLOCK_DATA, "block"),
	BLOCK_DUST             (3,  38, (v) -> v >= 7, "blockdust", CompatibleMaterial.GUNPOWDER, ParticleProperty.BLOCK_DATA),
	BUBBLE_COLUMN_UP       (4,  -1, (v) -> v >= 13, CompatibleMaterial.HORN_CORAL),
	BUBBLE_POP             (5,  -1, (v) -> v >= 13, CompatibleMaterial.PURPLE_DYE),
	CLOUD                  (6,  29, (v) -> true, "cloud", CompatibleMaterial.BONE_MEAL),
	CRIT                   (7,  9, (v) -> true, "crit", Material.DIAMOND_CHESTPLATE),
	CRIT_MAGIC             (8,  10, (v) -> true, "magicCrit", Material.DIAMOND_SWORD, "enchanted_hit"),
	CURRENT_DOWN           (9,  -1, (v) -> v >= 13, CompatibleMaterial.LAPIS_LAZULI),
	DAMAGE_INDICATOR       (10, 44, (v) -> v >= 9, "damageIndicator", Material.IRON_SWORD),
	DRAGON_BREATH          (11, 42, (v) -> v >= 9, "dragonbreath", CompatibleMaterial.DRAGON_HEAD),
	DRIP_LAVA              (12, 19, (v) -> true, "dripLava", Material.LAVA_BUCKET, "dripping_lava"),
	DRIP_WATER             (13, 18, (v) -> true, "dripWater", Material.WATER_BUCKET, "dripping_water"),
	DOLPHIN                (14, -1, (v) -> v >= 13, CompatibleMaterial.PRISMARINE_SHARD),
	ENCHANTMENT_TABLE      (15, 25, (v) -> true, "enchantmenttable", CompatibleMaterial.ENCHANTING_TABLE, "enchant"),
	END_ROD                (16, 43, (v) -> v >= 9, "endRod", CompatibleMaterial.END_ROD),
	EXPLOSION_HUGE         (17, 2, (v) -> true, "hugeexplosion", Material.TNT, "explosion_emitter"),
	EXPLOSION_LARGE        (18, 1, (v) -> true, "largeexplode", Material.TNT, "explosion"),
	EXPLOSION_NORMAL       (19, 0, (v) -> true, "explode", Material.TNT, "poof"),
	FALLING_DUST           (20, 46, (v) -> v >= 10, "fallingdust", Material.SAND, ParticleProperty.BLOCK_DATA),
	FIREWORKS_SPARK        (21, 3, (v) -> true, "fireworksSpark", CompatibleMaterial.FIREWORK_ROCKET, "firework"),
	FLAME                  (22, 26, (v) -> true, "flame", Material.TORCH),
	HEART                  (23, 34, (v) -> true, "heart", Material.REDSTONE_BLOCK),
	ITEM_CRACK             (24, 36, (v) -> true, "iconcrack", Material.APPLE, ParticleProperty.ITEM_DATA, "item"),
	LAVA                   (25, 27, (v) -> true, "lava", CompatibleMaterial.MAGMA_BLOCK),
	MOB_APPEARANCE         (26, 41, (v) -> v >= 8, "mobappearance", CompatibleMaterial.PLAYER_HEAD, "elder_guardian"),
	NAUTILUS               (27, -1, (v) -> v >= 13, CompatibleMaterial.CONDUIT),
	NOTE                   (28, 23, (v) -> true, "note", Material.NOTE_BLOCK),
	PORTAL                 (29, 24, (v) -> true, "portal", Material.SOUL_SAND),
	REDSTONE               (30, 30, (v) -> true, "reddust", Material.REDSTONE, ParticleProperty.DUST_OPTIONS, "dust"),
	SLIME                  (31, 33, (v) -> true, "slime", Material.SLIME_BALL, "item_slime"),
	SMOKE_LARGE            (32, 12, (v) -> true, "largesmoke", CompatibleMaterial.BONE_MEAL, "large_smoke"),
	SMOKE_NORMAL           (33, 11, (v) -> true, "smoke", Material.FLINT_AND_STEEL, "smoke"),
	SNOW_SHOVEL            (34, 32, (v) -> true, Material.SNOW_BLOCK),
	SNOWBALL               (35, 31, (v) -> true, "snowballpoof", CompatibleMaterial.SNOWBALL, "item_snowball"),
	SPELL                  (36, 13, (v) -> true, "spell", Material.POTION, "effect"),
	SPELL_INSTANT          (37, 14, (v) -> true, "instantSpell", CompatibleMaterial.SPLASH_POTION, "instant_effect"),
	SPELL_MOB              (38, 15, (v) -> v <= 20.4, "mobSpell", CompatibleMaterial.ZOMBIE_HEAD),
	SPELL_MOB_AMBIENT      (39, 16, (v) -> true, "mobSpellAmbient", Material.POTION),
	SPELL_WITCH            (40, 17, (v) -> true, "witchMagic", CompatibleMaterial.SPLASH_POTION, "witch"),
	SPIT                   (41, 48, (v) -> v >= 11, "spit", CompatibleMaterial.BONE_MEAL),
	SQUID_INK              (42, -1, (v) -> v >= 13, CompatibleMaterial.INK_SAC),
	SUSPENDED              (43, 7, (v) -> true, "suspended", Material.BEDROCK, "underwater"),
	SUSPENDED_DEPTH        (44, 8, (v) -> true, "depthSuspend", Material.BEDROCK),
	SWEEP_ATTACK           (45, 45, (v) -> v >= 9, "sweepAttack", Material.DIAMOND_SWORD),
	TOTEM                  (46, 47, (v) -> v >= 11, "totem", CompatibleMaterial.TOTEM_OF_UNDYING, "totem_of_undying"),
	TOWN_AURA              (47, 22, (v) -> true, "townaura", Material.BEACON, "mycelium"),
	VILLAGER_ANGRY         (48, 20, (v) -> true, "angryVillager", CompatibleMaterial.WITHER_SKELETON_SKULL, "angry_villager"),
	VILLAGER_HAPPY         (49, 21, (v) -> true, "happyVillager", CompatibleMaterial.PLAYER_HEAD, "happy_villager"),
	WATER_BUBBLE           (50, 4, (v) -> true, "bubble", Material.FISHING_ROD, "bubble"),
	WATER_DROP             (51, 39, (v) -> v >= 8, Material.WATER_BUCKET, "rain"),
	WATER_SPLASH           (52, 5, (v) -> true, "splash", CompatibleMaterial.BIRCH_BOAT, "splash"),
	WATER_WAKE             (53, 6, (v) -> v >= 7, "wake", CompatibleMaterial.OAK_BOAT, "fishing"),
	ITEMSTACK              (54, -1, (v) -> true, Material.DIAMOND, ParticleProperty.ITEMSTACK_DATA),
	CAMPFIRE_COSY_SMOKE    (55, -1, (v) -> v >= 14, CompatibleMaterial.CAMPFIRE),
	CAMPFIRE_SIGNAL_SMOKE  (56, -1, (v) -> v >= 14, CompatibleMaterial.CAMPFIRE),
	COMPOSTER              (57, -1, (v) -> v >= 14, CompatibleMaterial.COMPOSTER),
	FALLING_LAVA           (58, -1, (v) -> v >= 14, Material.LAVA_BUCKET),
	FALLING_WATER          (59, -1, (v) -> v >= 14, Material.WATER_BUCKET),
	FLASH                  (60, -1, (v) -> v >= 14, CompatibleMaterial.LANTERN),
	SNEEZE                 (61, -1, (v) -> v >= 14, CompatibleMaterial.GRAY_DYE),
	EMPTY_SPACE            (62, -1, (v) -> true, CompatibleMaterial.BARRIER),
	LANDING_LAVA           (63, -1, (v) -> v >= 14, Material.MAGMA_CREAM),
	DRIPPING_HONEY         (64, -1, (v) -> v >= 15, CompatibleMaterial.HONEY_BOTTLE),
	FALLING_HONEY          (65, -1, (v) -> v >= 15, CompatibleMaterial.HONEYCOMB),
	FALLING_NECTAR         (66, -1, (v) -> v >= 15, CompatibleMaterial.DANDELION),
	LANDING_HONEY          (67, -1, (v) -> v >= 15, CompatibleMaterial.HONEY_BOTTLE),
	SOUL_FIRE_FLAME        (68, -1, (v) -> v >= 16, CompatibleMaterial.SOUL_CAMPFIRE),
	ASH                    (69, -1, (v) -> v >= 16, CompatibleMaterial.BLACK_DYE),
	CRIMSON_SPORE          (70, -1, (v) -> v >= 16, CompatibleMaterial.CRIMSON_FUNGUS),
	WARPED_SPORE           (71, -1, (v) -> v >= 16, CompatibleMaterial.WARPED_FUNGUS),
	SOUL                   (72, -1, (v) -> v >= 16, CompatibleMaterial.SOUL_LANTERN),
	DRIPPING_OBSIDIAN_TEAR (73, -1, (v) -> v >= 16, CompatibleMaterial.CRYING_OBSIDIAN),
	FALLING_OBSIDIAN_TEAR  (74, -1, (v) -> v >= 16, CompatibleMaterial.CRYING_OBSIDIAN),
	LANDING_OBSIDIAN_TEAR  (75, -1, (v) -> v >= 16, CompatibleMaterial.CRYING_OBSIDIAN),
	REVERSE_PORTAL         (76, -1, (v) -> v >= 16, Material.OBSIDIAN),
	WHITE_ASH              (77, -1, (v) -> v >= 16, CompatibleMaterial.WHITE_DYE),
	LIGHT (78, -1, (v) -> v >= 17, CompatibleMaterial.LIGHT),

	DUST_COLOR_TRANSITION (79, -1, (v) -> v >= 17, Material.REDSTONE, ParticleProperty.COLOR_TRANSITION),

	// TODO: Implement way for storing vibration particle data
	//VIBRATION (80, -1, 17, Material.BEDROCK),

	FALLING_SPORE_BLOSSOM (81, -1, (v) -> v >= 17, CompatibleMaterial.SPORE_BLOSSOM),
	SPORE_BLOSSOM_AIR (82, -1, (v) -> v >= 17, CompatibleMaterial.SPORE_BLOSSOM),
	SMALL_FLAME (83, -1, (v) -> v >= 17, CompatibleMaterial.CYAN_CANDLE),
	SNOWFLAKE (84, -1, (v) -> v >= 17, CompatibleMaterial.POWDER_SNOW_BUCKET),
	DRIPPING_DRIPSTONE_LAVA (85, -1, (v) -> v >= 17, CompatibleMaterial.POINTED_DRIPSTONE),
	FALLING_DRIPSTONE_LAVA (86, -1, (v) -> v >= 17, CompatibleMaterial.DRIPSTONE_BLOCK),
	DRIPPING_DRIPSTONE_WATER (87, -1, (v) -> v >= 17, CompatibleMaterial.POINTED_DRIPSTONE),
	FALLING_DRIPSTONE_WATER (88, -1, (v) -> v >= 17, CompatibleMaterial.DRIPSTONE_BLOCK),
	GLOW_SQUID_INK (89, -1, (v) -> v >= 17, CompatibleMaterial.GLOW_INK_SAC),
	GLOW (90, -1, (v) -> v >= 17, CompatibleMaterial.GLOW_INK_SAC),
	WAX_ON (91, -1, (v) -> v >= 17, CompatibleMaterial.COPPER_BLOCK),
	WAX_OFF (92, -1, (v) -> v >= 17, CompatibleMaterial.WEATHERED_COPPER),
	ELECTRIC_SPARK (93, -1, (v) -> v >= 17, CompatibleMaterial.LIGHTNING_ROD),
	SCRAPE (94, -1, (v) -> v >= 17, CompatibleMaterial.OXIDIZED_COPPER),

	BLOCK_MARKER (95, -1, (v) -> v >= 18, Material.ITEM_FRAME, ParticleProperty.BLOCK_DATA),

	SONIC_BOOM (96, -1, (v) -> v >= 19, CompatibleMaterial.SCULK),
	SCULK_SOUL (97, -1, (v) -> v >= 19, CompatibleMaterial.SCULK_CATALYST),
	SCULK_CHARGE (98, -1, (v) -> v >= 19, CompatibleMaterial.SCULK_SENSOR, ParticleProperty.FLOAT),
	SCULK_CHARGE_POP (99, -1, (v) -> v >= 19, CompatibleMaterial.SCULK_VEIN),
	SHRIEK (100, -1, (v) -> v >= 19, CompatibleMaterial.SCULK_SHRIEKER, ParticleProperty.INTEGER),

	CHERRY_LEAVES(101, -1, (v) -> v >= 20, CompatibleMaterial.CHERRY_LEAVES),

	// Changes in 1.20.5
	ENTITY_EFFECT (102, -1, (v) -> v >= 20.5, CompatibleMaterial.ZOMBIE_HEAD, ParticleProperty.COLOR),
	EGG_CRACK (103, -1, (v) -> v >= 20.5, Material.EGG),
	DUST_PLUME (104, -1, (v) -> v >= 20.5, Material.REDSTONE),
	WHITE_SMOKE (105, -1, (v) -> v >= 20.5, CompatibleMaterial.WHITE_DYE),
	;
	
	private static final Map<String, ParticleEffect> particleNames   = new HashMap<String, ParticleEffect>();
	private static final Map<String, ParticleEffect> particleLegacyNames  = new HashMap<String, ParticleEffect>();
	private static final Map<Integer, ParticleEffect> particleIDs    = new HashMap<Integer, ParticleEffect>();
	
	private final int id;
	private final int legacyID;
	private final Predicate<Double> predicate;
	private final String legacyName;
	private final ItemStack item;
	private final ParticleProperty property;
	public final String[] aliases;
	
	static
	{
		for (ParticleEffect pe : values())
		{
			particleNames.put(pe.toString(), pe);
			particleLegacyNames.put(pe.legacyName, pe);
			particleIDs.put(pe.id, pe);
		}
}
	
	private ParticleEffect (final int id, final int legacyID, final Predicate<Double> predicate, String legacyName, final ItemStack item, final ParticleProperty property, String... aliases)
	{
		this.id = id;
		this.legacyID = legacyID;
		this.predicate = predicate;
		this.legacyName = legacyName;
		this.item = item;
		this.property = property;
		this.aliases = aliases;
	}
	
	private ParticleEffect (final int id, final int legacyID, final Predicate<Double> predicate, String legacyName, final Material material, final ParticleProperty property, String... aliases) {
		this(id, legacyID, predicate, legacyName, ItemUtil.createItem(material, 1), property, aliases);
	}
	
	private ParticleEffect (final int id, final int legacyID, final Predicate<Double> predicate, final String legacyName, final CompatibleMaterial material, final ParticleProperty property, String... aliases) {
		this(id, legacyID, predicate, legacyName, material.getItem(), property, aliases);
	}
	
	private ParticleEffect (final int id, final int legacyID, final Predicate<Double> predicate, final String legacyName, final Material material, String... aliases) {
		this(id, legacyID, predicate, legacyName, ItemUtil.createItem(material, 1), ParticleProperty.NO_DATA, aliases);
	}
	
	private ParticleEffect (final int id, final int legacyID, final Predicate<Double> predicate, final String legacyName, final CompatibleMaterial material, String... aliases) {
		this(id, legacyID, predicate, legacyName, material.getItem(), ParticleProperty.NO_DATA, aliases);
	}
	
	private ParticleEffect (final int id, final int legacyID, final Predicate<Double> predicate, final CompatibleMaterial material, final ParticleProperty property, String... aliases) {
		this(id, legacyID, predicate, "", material.getItem(), property, aliases);
	}
	
	private ParticleEffect (final int id, final int legacyID, final Predicate<Double> predicate, final Material material, final ParticleProperty property, String... aliases) {
		this(id, legacyID, predicate, "", ItemUtil.createItem(material, 1), property, aliases);
	}
	
	private ParticleEffect (final int id, final int legacyID, final Predicate<Double> predicate, final CompatibleMaterial material, String... aliases) {
		this(id, legacyID, predicate, "", material.getItem(), ParticleProperty.NO_DATA, aliases);
	}
	
	private ParticleEffect (final int id, final int legacyID, final Predicate<Double> predicate, final Material material, String... aliases) {
		this(id, legacyID, predicate, "", ItemUtil.createItem(material, 1), ParticleProperty.NO_DATA, aliases);
	}
	
	/**
	 * Check to see if this ParticleEffect uses color data
	 * @return True if this ParticleEffect uses color data
	 */
	public boolean hasColorData () {
		return property.equals(ParticleProperty.DUST_OPTIONS) || property.equals(ParticleProperty.COLOR_TRANSITION) || property.equals(ParticleProperty.COLOR);
	}
	
	/**
	 * Check to see if this ParticleEffect uses block data
	 * @return True if this ParticleEffect uses block data
	 */
	public boolean hasBlockData () {
		return property.equals(ParticleProperty.BLOCK_DATA);
	}
	
	/**
	 * Check to see if this ParticleEffect uses item data
	 * @return True if this ParticleEffect uses item data
	 */
	public boolean hasItemData () {
		return property.equals(ParticleProperty.ITEM_DATA);
	}
	
	/**
	 * Check to see if this ParticleEffect uses block, item, or itemstack data
	 * @return True if this ParticleEffect uses block, item, or itemstack data
	 */
	public boolean hasData () {
		return hasBlockData() || hasItemData() || property.equals(ParticleProperty.ITEMSTACK_DATA);
	}
	
	/**
	 * Get the id of this ParticleEffect
	 * @return
	 */
	public int getID () {
		return id;
	}
	
	/**
	 * Get the id of this ParticleEffect used on <= 1.12 servers
	 * @return
	 */
	public int getLegacyID () {
		return legacyID;
	}
	
	/**
	 * Get the string name of this enum
	 * @return
	 */
	public String getName () {
		return this.toString();
	}
	
	/**
	 * Get this ParticleEffect's legacy name
	 * @return
	 */
	public String getLegacyName () {
		return legacyName;
	}
	
	/**
	 * Get this ParticleEffect's name
	 * @return The name of this effect as defined in the current messages.yml file
	 */
	public String getDisplayName ()
	{
		String key = "PARTICLE_" + this.toString() + "_NAME";
		try {
			return Message.valueOf(key).getValue();
		} catch (IllegalArgumentException e) {
			return "";
		}
	}
	
	/**
	 * Get this ParticleEffect's name without color codes
	 * @return
	 */
	public String getStrippedName () {
		return ChatColor.stripColor(getDisplayName());
	}
	
	/**
	 * Get this ParticleEffect's description
	 * @return The description of this effect as defined in the current messages.yml file
	 */
	public String getDescription ()
	{
		String key = "PARTICLE_" + this.toString() + "_DESCRIPTION";
		try {
			return Message.valueOf(key).getValue();
		} catch (IllegalArgumentException e) {
			return "";
		}
	}
	
	public ItemStack getItem () {
		return item;
	}
	
	/**
	 * Check to see if this ParticleEffect can display particles
	 * @return
	 */
	public boolean canDisplay () {
		return this != NONE && this != EMPTY_SPACE;
	}
	
	/**
	 * Get this ParticleEffects ParticleProperty
	 * @return
	 */
	public ParticleProperty getProperty () {
		return property;
	}
	
	public boolean isSupported () {
		return predicate.test(ParticleHats.serverVersion);
	}
	
	/**
	 * Get how many particles are supported on this server
	 * @return
	 */
	public static int getParticlesSupported ()
	{
		int count = 0;
		for (ParticleEffect pe : values()) 
		{
			if (pe.isSupported()) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Returns the <b>ParticleEffect</b> with the given name, or <b>NONE</b> if there is no match
	 * @param name
	 * @return
	 */
	public static ParticleEffect fromName (String name)
	{
		if (name == null) {
			return NONE;
		}
		
		if (particleNames.containsKey(name)) {
			return particleNames.get(name);
		}
		
		if (particleLegacyNames.containsKey(name)) {
			return particleLegacyNames.get(name);
		}
		
		return NONE;
	}
	
	/**
	 * Returns the <b>ParticleEffect</b> with the given name, or <b>NONE</b> if there is no match
	 * @param name
	 * @return
	 */
	public static ParticleEffect fromDisplayName (String name)
	{
		if (particleNames.containsKey(name))
		{
			ParticleEffect particle = particleNames.get(name);
			if (particle.isSupported()) {
				return particle;
			}
		}
		return NONE;
	}
	
	/**
	 * Returns the <b>ParticleEffect</b> with this id, or <b>NONE</b> if there is no match
	 * @param id
	 * @return
	 */
	public static ParticleEffect fromID (int id)
	{
		if (particleIDs.containsKey(id)) {
			return particleIDs.get(id);
		}
		return NONE;
	}
	
	/**
	 * 
	 * @author MediusEcho
	 *
	 */
	public static enum ParticleProperty
	{
		NO_DATA,
		DUST_OPTIONS,
		COLOR_TRANSITION,
		COLOR,
		BLOCK_DATA,
		ITEM_DATA,
		ITEMSTACK_DATA,
		INTEGER,
		FLOAT
	}
}
