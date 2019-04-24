package com.mediusecho.particlehats.particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.effects.CustomEffect;
import com.mediusecho.particlehats.particles.properties.IconData;
import com.mediusecho.particlehats.particles.properties.IconDisplayMode;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.particles.properties.ParticleAnimation;
import com.mediusecho.particlehats.particles.properties.ParticleData;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleMode;
import com.mediusecho.particlehats.particles.properties.ParticleTag;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;
import com.mediusecho.particlehats.particles.properties.ParticleType;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class Hat {

	private Map<String, String> modifiedProperties;
	
	private String name                    = Message.EDITOR_MISC_NEW_PARTICLE.getRawValue();
	private String displayName             = StringUtil.colorize(name);
	private String permission              = "all";
	private String permissionDeniedMessage = "";
	private String equipMessage            = "";
	private String leftClickArgument       = "";
	private String rightClickArgument      = "";
	private String label                   = "";
	private String menu                    = "";
	
	private ParticleLocation location       = ParticleLocation.HEAD;
	private ParticleAction leftAction       = ParticleAction.EQUIP;
	private ParticleAction rightAction      = ParticleAction.MIMIC;
	private ParticleMode mode               = ParticleMode.ACTIVE;
	private ParticleType type               = ParticleType.NONE;
	private ParticleAnimation animation     = ParticleAnimation.STATIC;
	private ParticleTracking trackingMethod = ParticleTracking.TRACK_NOTHING;
	
	private CustomEffect customEffect;
	
	private boolean isVanished      = false;
	private boolean isPermanent     = true;
	private boolean isLoaded        = false;
	
	//private int referenceID         = 0;
	private int updateFrequency     = 2;
	private int price               = 0;
	private int speed               = 0;
	private int count               = 1;
	private int slot                = -1;
	private int demoDuration        = 200; // (10 Seconds in ticks)
	private int editingAction       = -1;
	
	private List<String> normalDescription;
	private List<String> permissionDescription;
	
	private List<ParticleTag> tags;
	
	private Map<Integer, ParticleData> particleData;
	
	private Sound sound;
	private double volume = 1D;
	private double pitch  = 1D;
	
	private Material material = Material.SUNFLOWER;
	private IconData iconData;
	
	// Particle Size
	//private double particleScale = 1f;
	
	private Vector offset;
	private Vector angle;
	
	public Hat ()
	{
		modifiedProperties    = new HashMap<String, String>();
		offset                = new Vector();
		angle                 = new Vector();
		iconData              = new IconData();
		normalDescription     = new ArrayList<String>();
		permissionDescription = new ArrayList<String>();
		tags                  = new ArrayList<ParticleTag>();
		nodes                 = new ArrayList<Hat>();
		particleData          = new HashMap<Integer, ParticleData>();
		animationIndex        = new HashMap<Integer, Integer>();
	}
	
	public boolean onTick ()
	{
		if (demoDuration > 0)
		{
			demoDuration--;
			return false;
		}
		return true;
	}
	
	/**
	 * Returns true if this hat was modified and needs to be saved
	 * @return
	 */
	public boolean isModified () {
		return modifiedProperties.size() > 0;
	}
	
	/**
	 * Set this hats name
	 * @param name The new name of this hat including color codes '&'
	 */
	public void setName (String name)
	{
		this.name = name;
		this.displayName = StringUtil.colorize(name);
		
		setProperty("title", "'" + name + "'");
	}
	
	/**
	 * Get this hats name
	 * @return The name used when saving
	 */
	public String getName () {
		return name;
	}
	
	/**
	 * Get this hats display name
	 * @return The name displayed in menus
	 */
	public String getDisplayName () {
		return displayName;
	}
	
	/**
	 * Set this hats permission. Only include the last section of the permission value.
	 * @param permission particlehats.particle.<b>permission</b>
	 */
	public void setPermission (String permission) 
	{
		if (permission != null)
		{
			this.permission = permission;	
			setProperty("permission", "'" + permission + "'");
		}
	}
	
	/**
	 * Get the permission value only, not the whole permission
	 * @return particlehats.particle.<b>permission</b>
	 */
	public String getPermission () {
		return permission;
	}
	
	/**
	 * Get the whole permission value
	 * @return permission formatted as <b>particlehats.particle.[permission]></b>
	 */
	public String getFullPermission () {
		return "particlehats.particle." + permission;
	}
	
	/**
	 * Set the message players see when they don't have permission to use this hat
	 * @param permissionDeniedMessage
	 */
	public void setPermissionDeniedMessage (String permissionDeniedMessage) 
	{
		if (permissionDeniedMessage != null)
		{
			this.permissionDeniedMessage = permissionDeniedMessage;
			setProperty("permission_denied", "'" + permissionDeniedMessage + "'");
		}
	}
	
	/**
	 * Get this hats permission denied message
	 * @return message players will receive when they don't have permission to use this hat
	 */
	public String getPermissionDeniedMessage () {
		return permissionDeniedMessage;
	}
	
	/**
	 * Get this hats permission denied message with color codes translated
	 * @return
	 */
	public String getPermissionDeniedDisplayMessage () {
		return StringUtil.colorize(permissionDeniedMessage);
	}
	
	/**
	 * Removes this hats permission denied message
	 */
	public void removePermissionDeniedMessage () 
	{
		this.permissionDeniedMessage = "";
		setProperty("permission_denied", "NULL");
	}
	
	/**
	 * Set the message players will see when they use this hat
	 * @param equipMessage
	 */
	public void setEquipMessage (String equipMessage) 
	{
		if (equipMessage != null)
		{
			this.equipMessage = equipMessage;
			setProperty("equip_message", "'" + equipMessage + "'");
		}
	}
	
	/**
	 * Get this hats equip message
	 * @return message players will see when they use this hat
	 */
	public String getEquipMessage () {
		return equipMessage;
	}
	
	/**
	 * Get this hats equip message with color codes translated
	 * @return
	 */
	public String getEquipDisplayMessage () {
		return StringUtil.colorize(equipMessage);
	}
	
	/**
	 * Removes this hats equip message
	 */
	public void removeEquipMessage ()
	{
		this.equipMessage = "";
		setProperty("equip_message", "NULL");
	}
	
	/**
	 * Set this hats left click argument
	 * @param leftClickArgument
	 */
	public void setLeftClickArgument (String leftClickArgument)
	{
		if (leftClickArgument != null)
		{
			this.leftClickArgument = leftClickArgument;
			setProperty("left_argument", "'" + leftClickArgument + "'");
		}
	}
	
	/**
	 * Get this hats left click argument
	 * @return
	 */
	public String getLeftClickArgument () {
		return leftClickArgument;
	}
	
	/**
	 * Set this hats right click argument
	 * @param leftClickArgument
	 */
	public void setRightClickArgument (String rightClickArgument)
	{
		if (rightClickArgument != null)
		{
			this.rightClickArgument = rightClickArgument;
			setProperty("right_argument", "'" + rightClickArgument + "'");
		}
	}
	
	/**
	 * Get this hats right click argument
	 * @return
	 */
	public String getRightClickArgument () {
		return rightClickArgument;
	}
	
	/**
	 * Set the left / right click action argument depending on the editing action value
	 * @param argument
	 */
	public void setArgument (String argument)
	{
		switch (editingAction)
		{
		case 1:
			setLeftClickArgument(argument);
			break;
		case 2:
			setRightClickArgument(argument);
			break;
			default: break;
		}
		editingAction = -1;
	}
	
	/**
	 * Set this hats label
	 * @param label
	 */
	public void setLabel (String label)
	{
		if (label != null)
		{
			this.label = label;
			setProperty("label", "'" + label + "'");
		}
	}
	
	/**
	 * Get this hats label<br>
	 * Labels are used in commands to reference this hat
	 * @return
	 */
	public String getLabel () {
		return label;
	}
	
	public void removeLabel ()
	{
		this.label = "";
		setProperty("label", "NULL");
	}
	
	/**
	 * Set which menu this hat belongs to
	 * @param menu
	 */
	public void setMenu (String menu) {
		this.menu = menu;
	}
	
	/**
	 * Get which menu this hat belongs to
	 * @return
	 */
	public String getMenu () {
		return menu;
	}
	
	/**
	 * Set this hats ParticleLocation
	 * @param location
	 */
	public void setLocation (ParticleLocation location) 
	{
		if (location != null)
		{
			this.location = location;
			setProperty("location", Integer.toString(location.getID()));
		}
	}
	
	/**
	 * Get this hats ParticleLocation
	 * @return The ParticleLocation of this hat
	 */
	public ParticleLocation getLocation () {
		return location;
	}
	
	/**
	 * Set this hats left click ParticleAction
	 * @param action
	 */
	public void setLeftClickAction (ParticleAction action)
	{
		if (leftAction != null)
		{
			this.leftAction = action;
			setProperty("left_action", Integer.toString(action.getID()));
		}
	}
	
	/**
	 * Get this hats left click ParticleAction
	 * @return
	 */
	public ParticleAction getLeftClickAction () {
		return leftAction;
	}
	
	/**
	 * Set this hats right click ParticleAction
	 * @param action
	 */
	public void setRightClickAction (ParticleAction action)
	{
		if (rightAction != null)
		{
			this.rightAction = action;
			setProperty("right_action", Integer.toString(action.getID()));
		}
	}
	
	/**
	 * Get this hats right click ParticleAction
	 * @return
	 */
	public ParticleAction getRightClickAction () {
		return rightAction;
	}
	
	/**
	 * Set this hats ParticleMode
	 * @param mode
	 */
	public void setMode (ParticleMode mode)
	{
		if (mode != null)
		{
			this.mode = mode;
			setProperty("mode", Integer.toString(mode.getID()));
		}
	}
	
	/**
	 * Get this hats ParticleMode
	 * @return The ParticleMode of this hat
	 */
	public ParticleMode getMode () {
		return mode;
	}
	
	/**
	 * Set this hats ParticleType
	 * @param type
	 */
	public void setType (ParticleType type)
	{
		if (type != null)
		{
			this.type = type;
			setProperty("type", Integer.toString(type.getID()));
		}
	}
	
	/**
	 * Get this hats ParticleType
	 * @return
	 */
	public ParticleType getType () {
		return type;
	}
	
	/**
	 * Gets this Hats ParticleType effect
	 * @return
	 */
	public Effect getEffect () {
		return type.getEffect();
	}
	
	/**
	 * Set this hats ParticleAnimation value
	 * @param animation
	 */
	public void setAnimation (ParticleAnimation animation)
	{
		if (animation != null)
		{
			this.animation = animation;
			setProperty("animation", Integer.toString(animation.getID()));
		}
	}
	
	/**
	 * Get this hats ParticleAnimation value
	 * @return
	 */
	public ParticleAnimation getAnimation () {
		return animation;
	}
	
	/**
	 * Set this hats ParticleTracking method
	 * @param trackingMethod
	 */
	public void setTrackingMethod (ParticleTracking trackingMethod)
	{
		this.trackingMethod = trackingMethod;
		setProperty("tracking", Integer.toString(trackingMethod.getID()));
	}
	
	/**
	 * Get this hats ParticleTracking method
	 * @return A tracking method compatible with this hats Type
	 */
	public ParticleTracking getTrackingMethod () 
	{
		List<ParticleTracking> methods = getEffect().getSupportedTrackingMethods();
		return methods.contains(trackingMethod) ? trackingMethod : getEffect().getDefaultTrackingMethod();
	}
	
	/**
	 * Get this hats ParticleTracking method
	 * @return A tracking method that may or may not be compatible with this hats Type
	 */
	public ParticleTracking getSavedTrackingMethod () {
		return trackingMethod;
	}
	
	/**
	 * Set this hats custom effect
	 * @param customEffect
	 */
	public void setCustomType (PixelEffect customEffect)
	{
		if (customEffect != null)
		{
			this.customEffect = customEffect;
			setProperty("custom_type", "'" + customEffect.getImageName() + "'");
		}
	}
	
	/**
	 * Get this hats custom effect, null if nothing is set
	 * @return
	 */
	@Nullable
	public PixelEffect getCustomEffect () {
		return customEffect;
	}
	
	/**
	 * Set this hats IconDisplayMode
	 * @param displayMode
	 */
	public void setDisplayMode (IconDisplayMode displayMode)
	{
		iconData.setDisplayMode(displayMode);
		setProperty("display_mode", Integer.toString(displayMode.getID()));
	}
	
	/**
	 * Get this hats IconDisplayMode
	 * @return
	 */
	public IconDisplayMode getDisplayMode () {
		return iconData.getDisplayMode();
	}
	
	/**
	 * Set whether this hat should display particles
	 * @param isVanished
	 */
	public void setVanished (boolean isVanished) 
	{
		this.isVanished = isVanished;
		setProperty("isVanished", Boolean.toString(isVanished));
	}
	
	/**
	 * Returns whether this has is vanished
	 * @return True if hat is vanished
	 */
	public boolean isVanished () {
		return isVanished;
	}
	
	/**
	 * Set whether this hat will un-equip itself
	 * @param isPermanent
	 */
	public void setPermanent (boolean isPermanent) 
	{
		this.isPermanent = isPermanent;
		setProperty("isPermanent", Boolean.toString(isPermanent));
	}
	
	/**
	 * Returns whether this hat can unequip automatically.<br>
	 * Hats that are not permanent will unequip themselves after a given time
	 * @return
	 */
	public boolean isPermanent () {
		return isPermanent;
	}
	
	/**
	 * Set whether this hat was loaded
	 * @param isLoaded
	 */
	public void setLoaded (boolean isLoaded) {
		this.isLoaded = isLoaded;
	}
	
	/**
	 * Returns whether this hat was loaded
	 * Lets us know if we need to load this hat from our database
	 * @return
	 */
	public boolean isLoaded () {
		return isLoaded;
	}
	
//	/**
//	 * Set this hats referenceID<br>
//	 * Used in the MySQL database
//	 * @param referenceID
//	 */
//	public void setReferenceID (int referenceID) {
//		this.referenceID = referenceID;
//	}
//	
//	/**
//	 * Get this hats referenceID<br>
//	 * Used in the MySQL database
//	 * @return
//	 */
//	public int getReferenceID () {
//		return referenceID;
//	}
	
	/**
	 * Set how often this hat displays particles
	 * @param updateFrequency How often this hat displays particles, <B>1 = fastest</b>
	 */
	public void setUpdateFrequency (int updateFrequency) 
	{
		this.updateFrequency = MathUtil.clamp(updateFrequency, 1, 100); // 5 seconds max delay
		
		// Update our potion timer
		if (potion != null) {
			setPotion(potion.getType(), potion.getAmplifier());
		}
		setProperty("update_frequency", Integer.toString(this.updateFrequency));
	}
	
	/**
	 * Get how often this hat displays particles
	 * @return How often then hat will update, every (X) ticks
	 */
	public int getUpdateFrequency () {
		return updateFrequency;
	}
	
	/**
	 * Check to see if we can display another frame of this hats ParticleType
	 * @param ticks
	 * @return
	 */
	public boolean canDisplay (int ticks) {
		return ticks % updateFrequency == 0;
	}
	
	/**
	 * Set how often this hat changes icons
	 * @param iconUpdateFrequency
	 */
	public void setIconUpdateFrequency (int iconUpdateFrequency)
	{
		iconData.setUpdateFrequency(iconUpdateFrequency);
		setProperty("icon_update_frequency", Integer.toString(iconUpdateFrequency));
	}
	
	/**
	 * Get how often this hat changes icons
	 * @return
	 */
	public int getIconUpdateFrequency () {
		return iconData.getUpdateFrequency();
	}
	
	/**
	 * Set the scale particles are displayed at<br>
	 * Only certain particles obey this value
	 * @param particleScale
	 */
	public void setParticleScale (int index, double scale) {
		getParticleData(index).setScale(scale);
		//this.particleScale = particleScale;
		//setProperty("particle_scale", Double.toString(particleScale));
	}
	
	/**
	 * Get how large particles are displayed at
	 * @return
	 */
	public double getParticleScale (int index) {
		return getParticleData(index).getScale();
		//return particleScale;
	}
	
	/**
	 * Set how much this hat costs
	 * @param price
	 */
	public void setPrice (int price) 
	{
		this.price = price;
		setProperty("price", Integer.toString(price));
	}
	
	/**
	 * Get how much this hat costs to purchase
	 * @return
	 */
	public int getPrice () {
		return this.price;
	}
	
	/**
	 * Set how much speed this hats particles should have.<br>
	 * Anything higher than 1 will look weird
	 * @param speed
	 */
	public void setSpeed (int speed)
	{
		this.speed = speed;
		setProperty("speed", Integer.toString(speed));
	}
	
	/**
	 * Get how fast this hats particles should be
	 * @return
	 */
	public int getSpeed () {
		return speed;
	}
	
	/**
	 * Set how many particles this hat should display
	 * @param count
	 */
	public void setCount (int count)
	{
		this.count = count;
		setProperty("count", Integer.toString(count));
	}
	
	/**
	 * Get how many particles this hat will display
	 * @return
	 */
	public int getCount () {
		return count;
	}
	
	/**
	 * Set the slot this hat belongs in
	 * @param slot
	 */
	public void setSlot (int slot) {
		this.slot = slot;
	}
	
	/**
	 * Get which slot this hat belongs in
	 * @return
	 */
	public int getSlot () {
		return slot;
	}
	
	/**
	 * Set how many ticks this hat has before being unequipped
	 * @param demoDuration
	 */
	public void setDemoDuration (int demoDuration)
	{
		this.demoDuration = MathUtil.clamp(demoDuration, 1, 36000);
		setProperty("duration", Integer.toString(demoDuration));
	}
	
	/**
	 * Get how many ticks this hat has before being unequipped
	 * @return
	 */
	public int getDemoDuration () {
		return demoDuration;
	}
	
	/**
	 * Set which actions argument will be edited (1 = left click, 2 = right click)
	 * @param editingAction
	 */
	public void setEditingAction (int editingAction) {
		this.editingAction = editingAction;
	}
	
	/**
	 * Set this hats description
	 * @param description
	 */
	public void setDescription (List<String> description) {
		this.normalDescription = description;
	}
	
	/**
	 * Get this hats description
	 * @return
	 */
	public List<String> getDescription () {
		return normalDescription;
	}
	
	/**
	 * Set the description players will see if they don't have permission to equip this hat
	 * @param description
	 */
	public void setPermissionDescription (List<String> description) {
		this.permissionDescription = description;
	}
	
	/**
	 * Get the description players will see if they don't have permission to equip this hat
	 * @return
	 */
	public List<String> getPermissionDescription () {
		return permissionDescription;
	}
	
	/**
	 * Set this hats tags
	 * @param tags
	 */
	public void setTags (List<ParticleTag> tags) {
		this.tags = tags;
	}
	
	/**
	 * Adds a ParticleTag to this hat
	 * @param tag
	 */
	public void addTag (ParticleTag tag) {
		tags.add(tag);
	}
	
	/**
	 * Get all ParticleTags that belong to this hat
	 * @return
	 */
	public List<ParticleTag> getTags () {
		return tags;
	}
	
	/**
	 * Adds a node to this hats node list
	 * @param node
	 */
	public void addNode (Hat node) {
		nodes.add(node);
	}
	
	/**
	 * Removes the node at index
	 * @param index
	 */
	public void removeNode (int index) 
	{
		if (index < nodes.size()) {
			nodes.remove(index);
		}
	}
	
	/**
	 * Gets a node that exists at this index
	 * @param index
	 * @return
	 */
	public Hat getNode (int index) 
	{
		if (index < nodes.size()) {
			return nodes.get(index);
		}
		return null;
	}
	
	/**
	 * Compares each nodes index value to the given index value and returns the matching node<br>
	 * Returns null if no match is found
	 * @param index
	 * @return
	 */
	public Hat getNodeAtIndex (int index)
	{
		for (Hat node : nodes)
		{
			if (node.getIndex() == index) {
				return node;
			}
		}
		return null;
	}
	
	/**
	 * Get a list of all nodes in this hat
	 * @return
	 */
	public List<Hat> getNodes () {
		return nodes;
	}
	
	/**
	 * Get how many nodes this hat has
	 * @return
	 */
	public int getNodeCount () {
		return isLoaded ? nodes.size() : nodeCountPreview;
	}
	 * Get the ParticleData found at this index
	 * @param index
	 * @return
	 */
	public ParticleData getParticleData (int index)
	{
		if (particleData.containsKey(index)) {
			return particleData.get(index);
		}
		
		ParticleData data = new ParticleData();
		particleData.put(index, data);
		
		return data;
	}
	
	/**
	 * Assign a particle to an index
	 * @param index
	 * @param particle
	 */
	public void setParticle (int index, ParticleEffect particle) {
		getParticleData(index).setParticle(particle);
	}
	
	/**
	 * Get the ParticleEffect that exists at this index
	 * @param index
	 * @return
	 */
	public ParticleEffect getParticle (int index) {
		return getParticleData(index).getParticle();
	}
	
	/**
	 * Get all particles the hat currently has
	 * @return
	 */
	public Map<Integer, ParticleEffect> getParticles ()
	{
		final Map<Integer, ParticleEffect> particles = new HashMap<Integer, ParticleEffect>();
		for (Entry<Integer, ParticleData> data : particleData.entrySet()) {
			particles.put(data.getKey(), data.getValue().getParticle());
		}
		return particles;
	}
	
	/**
	 * Check to see if this hat has any particles
	 * @return
	 */
	public boolean hasParticles () {
		return particleData.size() > 0;
	}
	
	/**
	 * Set this hats particle item data
	 * @param index
	 * @param item
	 */
	public void setParticleItem (int index, ItemStack item) {
		getParticleData(index).setItem(item);
	}
	
	/**
	 * Get this hats particle icon data 
	 * @param index
	 * @return
	 */
	public ItemStack getParticleItem (int index) {
		return getParticleData(index).getItem();
	}
	
	/**
	 * Set this hats particle block data
	 * @param index
	 * @param block
	 */
	public void setParticleBlock (int index, BlockData block) {
		getParticleData(index).setBlock(block);
	}
	
	/**
	 * Set this hats particle block data
	 * @param index
	 * @param block
	 */
	public void setParticleBlock (int index, Material block) {
		getParticleData(index).setBlock(block);
	}
	
	/**
	 * Get this hats particle block data
	 * @param index
	 * @return
	 */
	public BlockData getParticleBlock (int index) {
		return getParticleData(index).getBlock();
	}
	
	/**
	 * Set the sound this hat will play when clicked
	 * @param sound
	 */
	public void setSound (Sound sound)
	{
		this.sound = sound;
		setProperty("sound", "'" + sound.toString() + "'");
	}
	
	public void removeSound () 
	{
		this.sound = null;
		setProperty("sound", "NULL");
	}
	
	/**
	 * Get the sound this hat should make when clicked
	 * @return
	 */
	public Sound getSound () {
		return sound;
	}
	
	/**
	 * Set this hats sound volume
	 * @param volume
	 */
	public void setSoundVolume (double volume)
	{
		this.volume = volume;
		setProperty("volume", Double.toString(volume));
	}
	
	/**
	 * Get this hats sound volume
	 * @return
	 */
	public double getSoundVolume () {
		return volume;
	}
	
	/**
	 * Set this hats sound pitch
	 * @param pitch
	 */
	public void setSoundPitch (double pitch) 
	{
		this.pitch = pitch;
		setProperty("pitch", Double.toString(pitch));
	}
	
	/**
	 * Get this hats sound pitch
	 * @return
	 */
	public double getSoundPitch () {
		return pitch;
	}
	
	/**
	 * Set the Material that will appear in menus
	 * @param material
	 */
	public void setMaterial (Material material)
	{
		this.material = material;
		iconData.setMainMaterial(material);
		setProperty("id", "'" + material.toString() + "'");
	}
	
	/**
	 * Get this hats material
	 * @return The Material that appears in menus
	 */
	public Material getMaterial () {
		return material;
	}
	
	public IconData getIconData () {
		return iconData;
	}
	
	/**
	 * Set this hats x offset value
	 * @param x
	 */
	public void setOffsetX (double x) {
		setOffset(x, offset.getY(), offset.getZ());
	}
	
	/**
	 * Set this hats y offset value
	 * @param y
	 */
	public void setOffsetY (double y) {
		setOffset(offset.getX(), y, offset.getZ());
	}
	
	/**
	 * Set this hats z offset value
	 * @param z
	 */
	public void setOffsetZ (double z) {
		setOffset(offset.getX(), offset.getY(), z);
	}
	
	/**
	 * Set this hats offset
	 * @param offset
	 */
	public void setOffset (Vector offset) {
		setOffset(offset.getX(), offset.getY(), offset.getZ());
	}
	
	/**
	 * Set this hats offset
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setOffset (double x, double y, double z) 
	{			
		offset.setX(MathUtil.clamp(x, -20, 20));
		offset.setY(MathUtil.clamp(y, -20, 20));
		offset.setZ(MathUtil.clamp(z, -20, 20));
		
		setProperty("offset_x", Double.toString(offset.getX()));
		setProperty("offset_y", Double.toString(offset.getY()));
		setProperty("offset_z", Double.toString(offset.getZ()));
	}
	
	/**
	 * Returns this hats offset
	 * @return
	 */
	public Vector getOffset () {
		return offset;
	}
	
	/**
	 * Returns this hats offset including location offset
	 * @return
	 */
	public Vector getTotalOffset ()
	{
		double y = 0;
		switch (location)
		{
		case HEAD:
			y = 2.3;
			break;
		case WAIST:
			y = 0.9f;
			break;
		default:
			y = 0;
			break;
		}
		
		return new Vector(offset.getX(), offset.getY() + y, offset.getZ());
	}
	
	/**
	 * Set this hats x angle value
	 * @param x
	 */
	public void setAngleX (double x) {
		setAngle(x, angle.getY(), angle.getZ());
	}
	
	/**
	 * Set this hats y angle value
	 * @param y
	 */
	public void setAngleY (double y) {
		setAngle(angle.getX(), y, angle.getZ());
	}
	
	/**
	 * Set this hats x angle value
	 * @param z
	 */
	public void setAngleZ (double z) {
		setAngle(angle.getX(), angle.getY(), z);
	}
	
	/**
	 * Set this hats angle
	 * @param angle
	 */
	public void setAngle (Vector angle) {
		setAngle(angle.getX(), angle.getY(), angle.getZ());
	}
	
	/**
	 * Set this hats angle
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setAngle (double x, double y, double z) 
	{
		angle.setX(MathUtil.wrap(x, 360, 0));
		angle.setY(MathUtil.wrap(y, 360, 0));
		angle.setZ(MathUtil.wrap(z, 360, 0));
		
		setProperty("angle_x", Double.toString(angle.getX()));
		setProperty("angle_y", Double.toString(angle.getY()));
		setProperty("angle_z", Double.toString(angle.getZ()));
	}
	
	/**
	 * Returns this hats angle
	 * @return
	 */
	public Vector getAngle () {
		return angle;
	}
	
	/**
	 * Returns an SQL statement to update this hat
	 * @return
	 */
	public String getSQLUpdateQuery ()
	{
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SET ");
		
		for (Entry<String, String> entry : modifiedProperties.entrySet())
		{
			String varName = entry.getKey();
			String value = entry.getValue();
			//queryBuilder.append(varName + " = " + value + ",");
			queryBuilder.append(varName).append("=").append(value).append(",");
		}
		
		queryBuilder.deleteCharAt(queryBuilder.length() - 1);
		
		return queryBuilder.toString();
	}
	
	/**
	 * Returns an SQL statement to insert this hat into the database
	 * @return
	 */
	public String getSQLInsertQuery ()
	{
		return "";
	}
	
	/**
	 * Clears the recently modified properties list
	 */
	public void clearPropertyChanges () {
		modifiedProperties.clear();
	}
	
	/**
	 * Returns a copy of this object with only data necessary for displaying particles
	 * @return
	 */
	public Hat essentialCopy ()
	{
		Hat hat = new Hat();
		
		hat.setLocation(location);
		hat.setMode(mode);
		hat.setType(type);
		hat.setAnimation(animation);
		hat.setTrackingMethod(trackingMethod);
		
		hat.setUpdateFrequency(updateFrequency);
		hat.setCount(count);
		hat.setSpeed(speed);
		hat.setOffset(offset);
		hat.setAngle(angle);
		
		hat.clearPropertyChanges();
		return hat;
	}
	
	/**
	 * Returns a copy of this hat with only data necessary for a gui
	 * @return
	 */
	public Hat visualCopy ()
	{
		Hat hat = new Hat();
		
		hat.setMaterial(material);
		hat.setIconUpdateFrequency(iconData.getUpdateFrequency());
		hat.setDisplayMode(iconData.getDisplayMode());
		hat.getIconData().setMaterials(new ArrayList<Material>(getIconData().getMaterials()));
		
		hat.clearPropertyChanges();
		return hat;
	}
	
	/**
	 * Adds a modified property for reference when saving this hat
	 * @param key
	 * @param value
	 */
	private void setProperty (String key, String value) {
		modifiedProperties.put(key, value);
	}
}
