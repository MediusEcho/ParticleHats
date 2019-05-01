package com.mediusecho.particlehats.particles;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.locale.Message;

public enum ParticleEffect {

	// TODO: Update items to reflect their particle for all versions
	NONE                   (0,  0, -1, CompatibleMaterial.SCUTE),
	BARRIER                (1,  35, 8, "barrier", CompatibleMaterial.BARRIER),
	BLOCK_CRACK            (2,  37, -1, "blockcrack", Material.COBBLESTONE, ParticleProperty.BLOCK_DATA),
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
	FIREWORKS_SPARK        (21, 3, -1, "fireworksspark", CompatibleMaterial.FIREWORK_ROCKET),
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
	CAMPFIRE_COSY_SMOKE    (55, -1, 14, Material.STONE),
	CAMPFIRE_SIGNAL_SMOKE  (56, -1, 14, Material.STONE),
	COMPOSTER              (57, -1, 14, CompatibleMaterial.PLAYER_HEAD),
	FALLING_LAVA           (58, -1, 14, Material.LAVA_BUCKET),
	FALLING_WATER          (59, -1, 14, Material.WATER_BUCKET),
	FLASH                  (60, -1, 14, CompatibleMaterial.FIREWORK_STAR),
	SNEEZE                 (61, -1, 14, CompatibleMaterial.GRAY_DYE);
	
	private static final Map<String, ParticleEffect> NAMES   = new HashMap<String, ParticleEffect>();
	private static final Map<Integer, ParticleEffect> IDS    = new HashMap<Integer, ParticleEffect>();
	private static final Map<String, ParticleEffect> LEGACY  = new HashMap<String, ParticleEffect>();
	
	private final int id;
	private final int legacyID;
	private final int version;
	//private final Particle particle;
	private final String legacyName;
	private final Material material;
	private final ParticleProperty property;
	
	static
	{
		for (ParticleEffect pe : values())
		{
			NAMES.put(pe.toString(), pe);
			IDS.put(pe.id, pe);
			LEGACY.put(pe.legacyName, pe);
		}
	}
	
	private ParticleEffect (final int id, final int legacyID, final int version, String legacyName, final Material material, final ParticleProperty property)
	{
		this.id = id;
		this.legacyID = legacyID;
		this.version = version;
		this.legacyName = legacyName;
		this.material = material;
		this.property = property;
		//particle = getParticle(toString());
	}
	
	private ParticleEffect (final int id, final int legacyID, final int version, final String legacyName, final CompatibleMaterial material, final ParticleProperty property) {
		this(id, legacyID, version, legacyName, material.getMaterial(), property);
	}
	
	private ParticleEffect (final int id, final int legacyID, final int version, final String legacyName, final Material material) {
		this(id, legacyID, version, legacyName, material, ParticleProperty.NO_DATA);
	}
	
	private ParticleEffect (final int id, final int legacyID, final int version, final String legacyName, final CompatibleMaterial material) {
		this(id, legacyID, version, legacyName, material.getMaterial());
	}
	
	private ParticleEffect (final int id, final int legacyID, final int version, final CompatibleMaterial material, final ParticleProperty property) {
		this(id, legacyID, version, "", material.getMaterial(), property);
	}
	
	private ParticleEffect (final int id, final int legacyID, final int version, final Material material, final ParticleProperty property) {
		this(id, legacyID, version, "", material, property);
	}
	
	private ParticleEffect (final int id, final int legacyID, final int version, final CompatibleMaterial material) {
		this(id, legacyID, version, "", material.getMaterial(), ParticleProperty.NO_DATA);
	}
	
	private ParticleEffect (final int id, final int legacyID, final int version, final Material material) {
		this(id, legacyID, version, "", material, ParticleProperty.NO_DATA);
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
	
//	/**
//	 * Get the Particle value of this ParticleEffect
//	 * @return
//	 */
//	public Particle getParticle () {
//		return particle;
//	}
	
	/**
	 * Get the Material of this ParticleEffect
	 * @return The Material of this ParticleEffect for use in menus
	 */
	public Material getMaterial () {
		return material;
	}
	
	public ItemStack getItem () {
		return new ItemStack(material, 1);
	}
	
	/**
	 * Check to see if this ParticleEffect can display particles
	 * @return
	 */
	public boolean canDisplay () {
		return this != NONE && this != ITEMSTACK;
	}
	
	/**
	 * Get the Particle that is associated with this ParticleEffect
	 * @param value
	 * @return
	 */
//	private Particle getParticle (String value)
//	{
//		try {
//			return Particle.valueOf(value);
//		} catch (IllegalArgumentException e) {
//			return null;
//		}
//	}
	
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
		return Core.serverVersion >= version;
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
		for (Entry<String, ParticleEffect> entry : NAMES.entrySet())
		{
			if (!entry.getKey().equalsIgnoreCase(name)) {
				continue;
			}
			return entry.getValue();
		}
		return NONE;
	}
	
	/**
	 * Returns the <b>ParticleEffect</b> with the given name, or <b>NONE</b> if there is no match
	 * @param name
	 * @return
	 */
	public static ParticleEffect fromLegacyName (String name)
	{
		for (Entry<String, ParticleEffect> entry : LEGACY.entrySet())
		{
			if (!entry.getKey().equalsIgnoreCase(name)) {
				continue;
			}
			return entry.getValue();
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
		for (Entry<String, ParticleEffect> entry : NAMES.entrySet())
		{
			if (!entry.getValue().getDisplayName().equalsIgnoreCase(name)) {
				continue;
			}
			return entry.getValue();
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
		if (IDS.containsKey(id)) {
			return IDS.get(id);
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
