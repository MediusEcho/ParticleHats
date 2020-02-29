package com.mediusecho.particlehats.particles;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.util.ItemUtil;

public enum ParticleEffect {

	NONE                   (0,  0, -1, CompatibleMaterial.SCUTE),
	BARRIER                (1,  35, 8, "barrier", CompatibleMaterial.BARRIER),
	BLOCK_CRACK            (2,  37, -1, "blockcrack", Material.DIAMOND_PICKAXE, ParticleProperty.BLOCK_DATA),
	BLOCK_DUST             (3,  38, 7, "blockdust", CompatibleMaterial.GUNPOWDER, ParticleProperty.BLOCK_DATA),
	BUBBLE_COLUMN_UP       (4,  -1, 13, CompatibleMaterial.HORN_CORAL),
	BUBBLE_POP             (5,  -1, 13, CompatibleMaterial.PURPLE_DYE),
	CLOUD                  (6,  29, -1, "cloud", CompatibleMaterial.BONE_MEAL),
	CRIT                   (7,  9, -1, "crit", Material.DIAMOND_CHESTPLATE),
	CRIT_MAGIC             (8,  10, -1, "magicCrit", Material.DIAMOND_SWORD),
	CURRENT_DOWN           (9,  -1, 13, CompatibleMaterial.LAPIS_LAZULI),
	DAMAGE_INDICATOR       (10, 44, 9, "damageIndicator", Material.IRON_SWORD),
	DRAGON_BREATH          (11, 42, 9, "dragonbreath", CompatibleMaterial.DRAGON_HEAD), 
	DRIP_LAVA              (12, 19, -1, "dripLava", Material.LAVA_BUCKET),
	DRIP_WATER             (13, 18, -1, "dripWater", Material.WATER_BUCKET),
	DOLPHIN                (14, -1, 13, CompatibleMaterial.PRISMARINE_SHARD),
	ENCHANTMENT_TABLE      (15, 25, -1, "enchantmenttable", CompatibleMaterial.ENCHANTING_TABLE),
	END_ROD                (16, 43, 9, "endRod", CompatibleMaterial.END_ROD),
	EXPLOSION_HUGE         (17, 2, -1, "hugeexplosion", Material.TNT),
	EXPLOSION_LARGE        (18, 1, -1, "largeexplode", Material.TNT),
	EXPLOSION_NORMAL       (19, 0, -1, "explode", Material.TNT),
	FALLING_DUST           (20, 46, 10, "fallingdust", Material.SAND, ParticleProperty.BLOCK_DATA),
	FIREWORKS_SPARK        (21, 3, -1, "fireworksSpark", CompatibleMaterial.FIREWORK_ROCKET),
	FLAME                  (22, 26, -1, "flame", Material.TORCH),
	HEART                  (23, 34, -1, "heart", Material.REDSTONE_BLOCK),
	ITEM_CRACK             (24, 36, -1, "iconcrack", Material.APPLE, ParticleProperty.ITEM_DATA),
	LAVA                   (25, 27, -1, "lava", CompatibleMaterial.MAGMA_BLOCK),
	MOB_APPEARANCE         (26, 41, 8, "mobappearance", CompatibleMaterial.PLAYER_HEAD),
	NAUTILUS               (27, -1, 13, CompatibleMaterial.CONDUIT),
	NOTE                   (28, 23, -1, "note", Material.NOTE_BLOCK),
	PORTAL                 (29, 24, -1, "portal", Material.SOUL_SAND),
	REDSTONE               (30, 30, -1, "reddust", Material.REDSTONE, ParticleProperty.COLOR),
	SLIME                  (31, 33, -1, "slime", Material.SLIME_BALL),
	SMOKE_LARGE            (32, 12, -1, "largesmoke", CompatibleMaterial.BONE_MEAL),
	SMOKE_NORMAL           (33, 11, -1, "smoke", Material.FLINT_AND_STEEL),
	SNOW_SHOVEL            (34, 32, -1, Material.SNOW_BLOCK),
	SNOWBALL               (35, 31, -1, "snowballpoof", CompatibleMaterial.SNOWBALL),
	SPELL                  (36, 13, -1, "spell", Material.POTION),
	SPELL_INSTANT          (37, 14, -1, "instantSpell", CompatibleMaterial.SPLASH_POTION),
	SPELL_MOB              (38, 15, -1, "mobSpell", CompatibleMaterial.ZOMBIE_HEAD),
	SPELL_MOB_AMBIENT      (39, 16, -1, "mobSpellAmbient", Material.POTION),
	SPELL_WITCH            (40, 17, -1, "witchMagic", CompatibleMaterial.SPLASH_POTION),
	SPIT                   (41, 48, 11, "spit", CompatibleMaterial.BONE_MEAL),
	SQUID_INK              (42, -1, 13, CompatibleMaterial.INK_SAC),
	SUSPENDED              (43, 7, -1, "suspended", Material.BEDROCK),
	SUSPENDED_DEPTH        (44, 8, -1, "depthSuspend", Material.BEDROCK),
	SWEEP_ATTACK           (45, 45, 9, "sweepAttack", Material.DIAMOND_SWORD),
	TOTEM                  (46, 47, 11, "totem", CompatibleMaterial.TOTEM_OF_UNDYING),
	TOWN_AURA              (47, 22, -1, "townaura", Material.BEACON),
	VILLAGER_ANGRY         (48, 20, -1, "angryVillager", CompatibleMaterial.WITHER_SKELETON_SKULL),
	VILLAGER_HAPPY         (49, 21, -1, "happyVillager", CompatibleMaterial.PLAYER_HEAD),
	WATER_BUBBLE           (50, 4, -1, "bubble", Material.FISHING_ROD),
	WATER_DROP             (51, 39, 8, Material.WATER_BUCKET),
	WATER_SPLASH           (52, 5, -1, "splash", CompatibleMaterial.BIRCH_BOAT),
	WATER_WAKE             (53, 6, 7, "wake", CompatibleMaterial.OAK_BOAT),
	ITEMSTACK              (54, -1, -1, Material.DIAMOND, ParticleProperty.ITEMSTACK_DATA),
	CAMPFIRE_COSY_SMOKE    (55, -1, 14, CompatibleMaterial.CAMPFIRE),
	CAMPFIRE_SIGNAL_SMOKE  (56, -1, 14, CompatibleMaterial.CAMPFIRE),
	COMPOSTER              (57, -1, 14, CompatibleMaterial.COMPOSTER),
	FALLING_LAVA           (58, -1, 14, Material.LAVA_BUCKET),
	FALLING_WATER          (59, -1, 14, Material.WATER_BUCKET),
	FLASH                  (60, -1, 14, CompatibleMaterial.LANTERN),
	SNEEZE                 (61, -1, 14, CompatibleMaterial.GRAY_DYE),
	EMPTY_SPACE            (62, -1, -1, CompatibleMaterial.BARRIER),
	LANDING_LAVA           (63, -1, 14, Material.MAGMA_CREAM),
	DRIPPING_HONEY         (64, -1, 15, CompatibleMaterial.HONEY_BOTTLE),
	FALLING_HONEY          (65, -1, 15, CompatibleMaterial.HONEYCOMB),
	FALLING_NECTAR         (66, -1, 15, CompatibleMaterial.DANDELION),
	LANDING_HONEY          (67, -1, 15, CompatibleMaterial.HONEY_BOTTLE),
	
	// 1.16
	ASH                    (68, -1, 16, CompatibleMaterial.SOUL_SOIL),
	CRIMSON_SPORE          (69, -1, 16, CompatibleMaterial.CRIMSON_FUNGI),
	SOUL_FIRE_FLAME        (70, -1, 16, CompatibleMaterial.SOUL_FIRE_LANTERN),
	WARPED_SPORE           (71, -1, 16, CompatibleMaterial.WARPED_FUNGI),
	DRIPPING_OBSIDIAN_TEAR (72, -1, 16, CompatibleMaterial.CRYING_OBSIDIAN),
	FALLING_OBSIDIAN_TEAR  (73, -1, 16, CompatibleMaterial.CRYING_OBSIDIAN),
	LANDING_OBSIDIAN_TEAR  (74, -1, 16, CompatibleMaterial.CRYING_OBSIDIAN);
	
	private static final Map<String, ParticleEffect> particleNames   = new HashMap<String, ParticleEffect>();
	private static final Map<String, ParticleEffect> particleLegacyNames  = new HashMap<String, ParticleEffect>();
	private static final Map<Integer, ParticleEffect> particleIDs    = new HashMap<Integer, ParticleEffect>();
	
	private final int id;
	private final int legacyID;
	private final int version;
	private final String legacyName;
	private final ItemStack item;
	private final ParticleProperty property;
	
	static
	{
		for (ParticleEffect pe : values())
		{
			particleNames.put(pe.toString(), pe);
			particleLegacyNames.put(pe.legacyName, pe);
			particleIDs.put(pe.id, pe);
		}
	}
	
	private ParticleEffect (final int id, final int legacyID, final int version, String legacyName, final ItemStack item, final ParticleProperty property)
	{
		this.id = id;
		this.legacyID = legacyID;
		this.version = version;
		this.legacyName = legacyName;
		this.item = item;
		this.property = property;
	}
	
	private ParticleEffect (final int id, final int legacyID, final int version, String legacyName, final Material material, final ParticleProperty property) {
		this(id, legacyID, version, legacyName, ItemUtil.createItem(material, 1), property);
	}
	
	private ParticleEffect (final int id, final int legacyID, final int version, final String legacyName, final CompatibleMaterial material, final ParticleProperty property) {
		this(id, legacyID, version, legacyName, material.getItem(), property);
	}
	
	private ParticleEffect (final int id, final int legacyID, final int version, final String legacyName, final Material material) {
		this(id, legacyID, version, legacyName, ItemUtil.createItem(material, 1), ParticleProperty.NO_DATA);
	}
	
	private ParticleEffect (final int id, final int legacyID, final int version, final String legacyName, final CompatibleMaterial material) {
		this(id, legacyID, version, legacyName, material.getItem(), ParticleProperty.NO_DATA);
	}
	
	private ParticleEffect (final int id, final int legacyID, final int version, final CompatibleMaterial material, final ParticleProperty property) {
		this(id, legacyID, version, "", material.getItem(), property);
	}
	
	private ParticleEffect (final int id, final int legacyID, final int version, final Material material, final ParticleProperty property) {
		this(id, legacyID, version, "", ItemUtil.createItem(material, 1), property);
	}
	
	private ParticleEffect (final int id, final int legacyID, final int version, final CompatibleMaterial material) {
		this(id, legacyID, version, "", material.getItem(), ParticleProperty.NO_DATA);
	}
	
	private ParticleEffect (final int id, final int legacyID, final int version, final Material material) {
		this(id, legacyID, version, "", ItemUtil.createItem(material, 1), ParticleProperty.NO_DATA);
	}
	
	/**
	 * Check to see if this ParticleEffect uses color data
	 * @return True if this ParticleEffect uses color data
	 */
	public boolean hasColorData () {
		return property.equals(ParticleProperty.COLOR);
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
	 * Returns the server version required to use this particle (1.x)
	 * @return
	 */
	public int getRequiredVersion () {
		return version;
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
	
	public boolean isSupported ()
	{
		if (version == -1) {
			return true;
		}
		return ParticleHats.serverVersion >= version;
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
		
		//String particle = name.toLowerCase();
		
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
		COLOR,
		BLOCK_DATA,
		ITEM_DATA,
		ITEMSTACK_DATA;
	}
}
