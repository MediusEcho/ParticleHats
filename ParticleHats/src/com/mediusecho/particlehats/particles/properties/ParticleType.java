package com.mediusecho.particlehats.particles.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.util.Vector;

import com.mediusecho.particlehats.particles.Effect;
import com.mediusecho.particlehats.particles.effects.AngelWingsEffect;
import com.mediusecho.particlehats.particles.effects.ArchEffect;
import com.mediusecho.particlehats.particles.effects.AtomEffect;
import com.mediusecho.particlehats.particles.effects.CapeEffect;
import com.mediusecho.particlehats.particles.effects.CleanTrailEffect;
import com.mediusecho.particlehats.particles.effects.CreeperEffect;
import com.mediusecho.particlehats.particles.effects.CrystalEffect;
import com.mediusecho.particlehats.particles.effects.Debug5x5Effect;
import com.mediusecho.particlehats.particles.effects.Debug6x6Effect;
import com.mediusecho.particlehats.particles.effects.PixelEffect;
import com.mediusecho.particlehats.particles.effects.HaloEffect;
import com.mediusecho.particlehats.particles.effects.HelixEffect;
import com.mediusecho.particlehats.particles.effects.HoopEffect;
import com.mediusecho.particlehats.particles.effects.InverseVortexEffect;
import com.mediusecho.particlehats.particles.effects.PlaceholderEffect;
import com.mediusecho.particlehats.particles.effects.SphereEffect;
import com.mediusecho.particlehats.particles.effects.SusanooEffect;
import com.mediusecho.particlehats.particles.effects.TornadoEffect;
import com.mediusecho.particlehats.particles.effects.TrailEffect;
import com.mediusecho.particlehats.particles.effects.VortexEffect;
import com.mediusecho.particlehats.particles.effects.WingsEffect;

public enum ParticleType {

	NONE           (0,  new PlaceholderEffect()),
	HALO           (1,  new HaloEffect()),
	TRAIL          (2,  new TrailEffect()),
	CAPE           (3,  new CapeEffect()),
	WINGS          (4,  new WingsEffect()),
	VORTEX         (5,  new VortexEffect()),
	ARCH           (6,  new ArchEffect()),
	ATOM           (7,  new AtomEffect()),
	SPHERE         (8,  new SphereEffect()),
	CRYSTAL        (9,  new CrystalEffect()),
	HELIX          (10, new HelixEffect()),
	INVERSE_VORTEX (11, new InverseVortexEffect()),
	HOOP           (12, new HoopEffect()),
	SUSANOO        (13, new SusanooEffect()),
	ANGEL_WINGS    (14, new AngelWingsEffect()),
	CREEPER_HAT    (15, new CreeperEffect()),
	CLEAN_TRAIL    (16, new CleanTrailEffect()),
	TORNADO        (17, new TornadoEffect()),
	CUSTOM         (18, new PixelEffect()),
	DEBUG_5X5      (-1, new Debug5x5Effect(), true),
	DEBUG_6X6      (-2, new Debug6x6Effect(), true);
	
	private final int id;
	private final Effect effect;
	
	private final boolean debug;
	
	private static final Map<String, ParticleType> typeName = new HashMap<String, ParticleType>();
	private static final Map<Integer, ParticleType> typeID = new HashMap<Integer, ParticleType>();
	private static final Map<Integer, List<List<Vector>>> frames = new HashMap<Integer, List<List<Vector>>>();
	static
	{
		for (ParticleType pt : values())
		{
			typeName.put(pt.getName(), pt);
			typeID.put(pt.id, pt);
			frames.put(pt.id, new ArrayList<List<Vector>>());
			
			if (pt.effect != null) {
				pt.effect.build();
			}
		}
	}
	
	private ParticleType (final int id, final Effect effect, final boolean debug)
	{
		this.id = id;
		this.effect = effect;
		this.debug = debug;
	}
	
	private ParticleType (final int id, final Effect effect)
	{
		this(id, effect, false);
	}
	
	/**
	 * Get this ParticleTypes Effect
	 * @return
	 */
	public Effect getEffect () {
		return effect;
	}
	
	/**
	 * Set all particle location data for this type
	 * @param f
	 */
	public void setFrames (List<List<Vector>> f) {
		frames.put(id, f);
	}
	
	/**
	 * Get all particle location data for this type
	 * @return
	 */
	public List<List<Vector>> getFrames () {
		return frames.get(id);
	}
	
	/**
	 * Get this effects name
	 * @return
	 */
	public String getName () {
		return effect.getName();
	}
	
	/**
	 * Get this effects display name
	 * @return
	 */
	public String getDisplayName () {
		return effect.getDisplayName();
	}
	
	/**
	 * Get this effects display name without color
	 * @return
	 */
	public String getStrippedName () {
		return ChatColor.stripColor(effect.getDisplayName());
	}
	
	/**
	 * Get this effects description
	 * @return
	 */
	public String getDescription () {
		return effect.getDescription();
	}
	
	/**
	 * Get how many particles this effect can support
	 * @return
	 */
	public int getParticlesSupported () {
		return effect.getParticlesSupported();
	}
	
	/**
	 * Get the id of this effect
	 * @return
	 */
	public int getID () {
		return id;
	}
	
	/**
	 * Returns true if this Type if meant for debugging
	 * @return
	 */
	public boolean isDebug () {
		return debug;
	}
	
	/**
	 * Check to see if this effect is a custom type
	 * @return
	 */
	public boolean isCustom () {
		return effect.isCustom();
	}
	
	/**
	 * Check to see if this effect supports animations
	 * @return
	 */
	public boolean supportsAnimation () {
		return effect.supportsAnimation();
	}
	
	/**
	 * Returns the ParticleType value that matches this nane, or NONE
	 * @param name
	 * @return
	 */
	public static ParticleType fromName (String name)
	{
		if (typeName.containsKey(name)) {
			return typeName.get(name);
		}
		return ParticleType.NONE;
	}
	
	/**
	 * Returns the ParticleType value that matches this id, or NONE
	 * @param id
	 * @return
	 */
	public static ParticleType fromID (int id)
	{
		if (typeID.containsKey(id)) {
			return typeID.get(id);
		}
		return ParticleType.NONE;
	}
}
