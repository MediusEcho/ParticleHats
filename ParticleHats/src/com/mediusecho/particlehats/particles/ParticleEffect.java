package com.mediusecho.particlehats.particles;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.locale.Message;

public enum ParticleEffect {

	NONE              (0, -1, "NONE", "", Material.SCUTE),
	BARRIER           (1, 8, "BARRIER", "barrier", Material.BARRIER),
	BLOCK_CRACK       (2, -1, "BLOCK_CRACK", "blockcrack", Material.COBBLESTONE),
	BLOCK_DUST        (3, 7, "BLOCK_DUST", "blockdust", Material.GUNPOWDER),
	BUBBLE_COLUMN_UP  (4, 13, "BUBBLE_COLUMN_UP", "", Material.HORN_CORAL),
	BUBBLE_POP        (5, 13, "BUBBLE_POP", "", Material.PURPLE_DYE),
	CLOUD             (6, -1, "CLOUD", "cloud", Material.WHITE_WOOL),
	CRIT              (7, -1, "CRIT", "crit", Material.DIAMOND_CHESTPLATE),
	CRIT_MAGIC        (8, -1, "CRIT_MAGIC", "magicCrit", Material.DIAMOND_SWORD),
	CURRENT_DOWN      (9, 13, "CURRENT_DOWN", "", Material.LAPIS_LAZULI),
	DAMAGE_INDICATOR  (10, 9, "DAMAGE_INDICATOR", "damageIndicator", Material.IRON_SWORD),
	DRAGON_BREATH     (11, 9, "DRAGON_BREATH", "dragonbreath", Material.DRAGON_HEAD), 
	DRIP_LAVA         (12, -1, "DRIP_LAVA", "dripLava", Material.LAVA_BUCKET),
	DRIP_WATER        (13, -1, "DRIP_WATER", "dripWater", Material.WATER_BUCKET),
	DOLPHIN           (14, 13, "DOLPHIN", "", Material.PRISMARINE_SHARD),
	ENCHANTMENT_TABLE (15, -1, "ENCHANTMENT_TABLE", "enchantmenttable", Material.ENCHANTING_TABLE),
	END_ROD           (16, 9, "END_ROD", "endRod", Material.END_ROD),
	EXPLOSION_HUGE    (17, -1, "EXPLOSION_HUGE", "hugeexplosion", Material.TNT),
	EXPLOSION_LARGE   (18, -1, "EXPLOSION_LARGE", "largeexplode", Material.TNT),
	EXPLOSION_NORMAL  (19, -1, "EXPLOSION_NORMAL", "explode", Material.TNT),
	FALLING_DUST      (20, 10, "FALLING_DUST", "fallingdust", Material.SAND),
	FIREWORKS_SPARK   (21, -1, "FIREWORKS_SPARK", "fireworksspark", Material.FIREWORK_ROCKET),
	FLAME             (22, -1, "FLAME", "flame", Material.TORCH),
	HEART             (23, -1, "HEART", "heart", Material.REDSTONE_BLOCK),
	ITEM_CRACK        (24, -1, "ITEM_CRACK", "itemcrack", Material.APPLE),
	LAVA              (25, -1, "LAVA", "lava", Material.MAGMA_BLOCK),
	MOB_APPEARANCE    (26, 8, "MOB_APPEARANCE", "mobappearance", Material.PLAYER_HEAD),
	NAUTILUS          (27, 13, "NAUTILUS", "", Material.CONDUIT),
	NOTE              (28, -1, "NOTE", "note", Material.NOTE_BLOCK),
	PORTAL            (29, -1, "PORTAL", "portal", Material.SOUL_SAND),
	REDSTONE          (30, -1, "REDSTONE", "reddust", Material.REDSTONE),
	SLIME             (31, -1, "SLIME", "slime", Material.SLIME_BALL),
	SMOKE_LARGE       (32, -1, "SMOKE_LARGE", "largesmoke", Material.BONE_MEAL),
	SMOKE_NORMAL      (33, -1, "SMOKE_NORMAL", "smoke", Material.FLINT_AND_STEEL),
	SNOW_SHOVEL       (34, -1, "SNOW_SHOVEL", "", Material.SNOW_BLOCK),
	SNOWBALL          (35, -1, "SNOWBALL", "snowballpoof", Material.SNOWBALL),
	SPELL             (36, -1, "SPELL", "spell", Material.POTION),
	SPELL_INSTANT     (37, -1, "SPELL_INSTANT", "instantSpell", Material.SPLASH_POTION),
	SPELL_MOB         (38, -1, "SPELL_MOB", "mobSpell", Material.ZOMBIE_HEAD),
	SPELL_MOB_AMBIENT (39, -1, "SPELL_MOB_AMBIENT", "mobSpellAmbient", Material.POTION),
	SPELL_WITCH       (40, -1, "SPELL_WITCH", "witchMagic", Material.SPLASH_POTION),
	SPIT              (41, 11, "SPIT", "spit", Material.BONE_MEAL),
	SQUID_INK         (42, 13, "SQUID_INK", "", Material.INK_SAC),
	SUSPENDED         (43, -1, "SUSPENDED", "suspended", Material.BEDROCK),
	SUSPENDED_DEPTH   (44, -1, "SUSPENDED_DEPTH", "depthSuspend", Material.BEDROCK),
	SWEEP_ATTACK      (45, 9, "SWEEP_ATTACK", "sweepAttack", Material.DIAMOND_SWORD),
	TOTEM             (46, 11, "TOTEM", "totem", Material.TOTEM_OF_UNDYING),
	TOWN_AURA         (47, -1, "TOWN_AURA", "townaura", Material.BEACON),
	VILLAGER_ANGRY    (48, -1, "VILLAGER_ANGRY", "angryVillager", Material.WITHER_SKELETON_SKULL),
	VILLAGER_HAPPY    (49, -1, "VILLAGER_HAPPY", "happyVillager", Material.PLAYER_HEAD),
	WATER_BUBBLE      (50, -1, "WATER_BUBBLE", "bubble", Material.FISHING_ROD),
	WATER_DROP        (51, 8, "WATER_DROP", "", Material.WATER_BUCKET),
	WATER_SPLASH      (52, -1, "WATER_SPLASH", "splash", Material.BIRCH_BOAT),
	WATER_WAKE        (53, 7, "WATER_WAKE", "wake", Material.OAK_BOAT),
	ITEMSTACK         (54, -1, "", "", Material.DIAMOND);
	
	private static final Map<String, ParticleEffect> NAMES   = new HashMap<String, ParticleEffect>();
	private static final Map<Integer, ParticleEffect> IDS    = new HashMap<Integer, ParticleEffect>();
	private static final Map<String, ParticleEffect> LEGACY  = new HashMap<String, ParticleEffect>();
	
	private final int id;
	private final int version;
	private final Particle particle;
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
	
	private ParticleEffect (final int id, final int version, final String particleName, String legacyName, Material material)
	{
		this.id = id;
		this.version = version;
		this.legacyName = legacyName;
		this.material = material;
		particle = getParticle(particleName);
		
		if (particle == null)
		{
			if (this.id == 54) {
				property = ParticleProperty.ITEMSTACK_DATA;
			} else {
				property = ParticleProperty.NO_DATA;
			}
		}
		
		else
		{
			Class<?> c = particle.getDataType();
			
			if (c.equals(DustOptions.class)) {
				property = ParticleProperty.COLOR;
			} else if (c.equals(BlockData.class)) {
				property = ParticleProperty.BLOCK_DATA;
			} else if (c.equals(ItemStack.class)) {
				property = ParticleProperty.ITEM_DATA;
			} else {
				property = ParticleProperty.NO_DATA;
			}
		}
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
	
	/**
	 * Get the Particle value of this ParticleEffect
	 * @return
	 */
	public Particle getParticle () {
		return particle;
	}
	
	/**
	 * Get the Material of this ParticleEffect
	 * @return The Material of this ParticleEffect for use in menus
	 */
	public Material getMaterial () {
		return material;
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
	private Particle getParticle (String value)
	{
		try {
			return Particle.valueOf(value);
		} catch (IllegalArgumentException e) {
			return null;
		}
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
		return Core.serverVersion >= version;
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
