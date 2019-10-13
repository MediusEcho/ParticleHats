package com.mediusecho.particlehats.particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.effects.PixelEffect;
import com.mediusecho.particlehats.particles.properties.IconData;
import com.mediusecho.particlehats.particles.properties.IconData.ItemStackTemplate;
import com.mediusecho.particlehats.particles.properties.IconDisplayMode;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.particles.properties.ParticleAnimation;
import com.mediusecho.particlehats.particles.properties.ParticleData;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleMode;
import com.mediusecho.particlehats.particles.properties.ParticleTag;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;
import com.mediusecho.particlehats.particles.properties.ParticleType;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.util.ItemUtil;
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
	
	private PixelEffect customEffect;
	
	private boolean isVanished  = false;
	private boolean isHidden    = false;
	private boolean isPermanent = true;
	private boolean isLoaded    = false;
	private boolean isDeleted   = false;
	private boolean isLocked    = false;
	
	private int updateFrequency     = 2;
	private int price               = 0;
	private int speed               = 0;
	private int count               = 1;
	private int slot                = -1;
	private int index               = -1;
	private int demoDuration        = 200; // (10 Seconds in ticks)
	private int editingAction       = -1;
	
	private double scale = 1;
	
	private List<String> normalDescription;
	private List<String> permissionDescription;
	private List<String> cachedDescription;
	
	private List<ParticleTag> tags;
	private List<Hat> nodes;
	private Hat parent;
	
	private PotionEffect potion;
	
	private Map<Integer, ParticleData> particleData;
	private Map<Integer, Integer> animationIndex;
	
	private Sound sound;
	private double volume = 1D;
	private double pitch  = 1D;
	
	private ItemStack item = ItemUtil.createItem(CompatibleMaterial.SUNFLOWER, 1);
	private IconData iconData;
	
	private Vector offset;
	private Vector randomOffset;
	private Vector angle;
	
	private ItemStack menuItem;
	
	public Hat ()
	{
		modifiedProperties    = new HashMap<String, String>();
		offset                = new Vector();
		randomOffset          = new Vector();
		angle                 = new Vector();
		iconData              = new IconData();
		normalDescription     = new ArrayList<String>();
		permissionDescription = new ArrayList<String>();
		cachedDescription     = new ArrayList<String>();
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
		return Permission.PARTICLE.append(permission);
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
	 * Displays this hats ParticleType
	 * @param ticks
	 * @param e
	 */
	public void displayType (int ticks, Entity e)
	{
		if (!type.equals(ParticleType.CUSTOM)) {
			type.getEffect().display(ticks, e, this);
		}
		
		else
		{
			if (customEffect != null) {
				customEffect.display(ticks, e, this);
			}
		}
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
	public void setVanished (boolean isVanished) {
		this.isVanished = isVanished;
	}
	
	/**
	 * Returns whether this has is vanished
	 * @return True if hat is vanished
	 */
	public boolean isVanished () {
		return isVanished;
	}
	
	/**
	 * Set wheter this hat is hidden
	 * @param hidden
	 */
	public void setHidden (boolean hidden) {
		isHidden = hidden;
	}
	
	/**
	 * Returns whether this hat is hidden and will not display particles
	 * @return
	 */
	public boolean isHidden () {
		return isHidden;
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
	
	/**
	 * Flags this hat for deletion
	 */
	public void delete () {
		isDeleted = true;
	}
	
	/**
	 * Check to see if this hat is flagged for deletion
	 * @return
	 */
	public boolean isDeleted () {
		return isDeleted;
	}
	
	/**
	 * Set whether this hat is locked or not
	 * @param locked
	 */
	public void setLocked (boolean locked) {
		isLocked = locked;
	}
	
	/**
	 * Get whether this hat can be locked
	 * @return
	 */
	public boolean canBeLocked () {
		return leftAction == ParticleAction.EQUIP || rightAction == ParticleAction.EQUIP;
	}
	
	/**
	 * Check to see if this hat is locked
	 * @return
	 */
	public boolean isLocked () {
		return isLocked;
	}
	
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
	}
	
	/**
	 * Get how large particles are displayed at
	 * @return
	 */
	public double getParticleScale (int index) {
		return getParticleData(index).getScale();
	}
	
	/**
	 * Set how much this hat costs
	 * @param price
	 */
	public void setPrice (int price) 
	{
		this.price = MathUtil.clamp(price, 0, 2000000000);
		setProperty("price", Integer.toString(this.price));
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
		this.speed = MathUtil.clamp(speed, 0, 10);
		setProperty("speed", Integer.toString(this.speed));
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
		this.count = MathUtil.clamp(count, 1, 15);
		setProperty("count", Integer.toString(this.count));
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
	public void setSlot (int slot) 
	{
		this.slot = slot;
		for (Hat node : nodes) {
			node.setSlot(slot);
		}
	}
	
	/**
	 * Get which slot this hat belongs in
	 * @return
	 */
	public int getSlot () {
		return slot;
	}
	
	/**
	 * Set this hats index value<br>
	 * Used for node lookups
	 * @param index
	 */
	public void setIndex (int index) {
		this.index = index;
	}
	
	/**
	 * Get this hats index value
	 * @return
	 */
	public int getIndex () {
		return index;
	}
	
	/**
	 * Set how many ticks this hat has before being removed
	 * @param demoDuration
	 */
	public void setDemoDuration (int demoDuration)
	{
		this.demoDuration = MathUtil.clamp(demoDuration, 1, 36000);
		setProperty("duration", Integer.toString(demoDuration));
	}
	
	/**
	 * Set how many seconds this hat has before being removed
	 * @param seconds
	 */
	public void setDuration (int seconds) 
	{
		this.demoDuration = MathUtil.clamp(seconds, 1, 1800) * 20;
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
	 * Get this hats scale
	 * @return
	 */
	public double getScale () {
		return scale;
	}
	
	/**
	 * Set this hats scale
	 * @param scale
	 */
	public void setScale (double scale)
	{
		this.scale = MathUtil.clamp(scale, 0.1, 10);
		setProperty("scale", Double.toString(this.scale));
	}
	
	/**
	 * Set this hats description
	 * @param description
	 */
	public void setDescription (List<String> description) 
	{
		this.normalDescription = description;
		cachedDescription = StringUtil.parseDescription(this, description);
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
	 * Get this menus cached description.  
	 * Cached description contains the raw description info before being translated
	 * @return
	 */
	public List<String> getCachedDescription () {
		return cachedDescription;
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
	public List<ParticleTag> getTags () 
	{
		if (parent == null) {
			return tags;
		}
		return parent.getTags();
	}
	
	/**
	 * Gets a list of all ParticleTag names that belong to this hat
	 * @return
	 */
	public List<String> getTagNames ()
	{
		List<String> tagNames = new ArrayList<String>();
		for (ParticleTag tag : tags) {
			tagNames.add(tag.getName());
		}
		return tagNames;
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
		return nodes.size();
	}
	
	/**
	 * Set this hat's parent hat
	 * @param parent
	 */
	public void setParent (Hat parent) {
		this.parent = parent;
	}
	
	/**
	 * Sets this hats potion effect
	 * @param potion
	 */
	public void setPotion (PotionEffect potion)
	{
		this.potion = potion;
		setProperty("potion", "'" + potion.getType().getName() + "'");
		setProperty("potion_strength", Integer.toString(potion.getAmplifier()));
	}
	
	/**
	 * Sets this hats potion effect
	 * @param type
	 * @param amplifier
	 */
	public void setPotion (PotionEffectType type, int amplifier) {
		setPotion(new PotionEffect(type, updateFrequency, amplifier, false, false));
	}
	
	/**
	 * Set this hats potion amplifier
	 * @param amplifier
	 */
	public void setPotionAmplifier (int amplifier) 
	{
		int amp = MathUtil.clamp(amplifier, 0, 9);
		setPotion(potion.getType(), amp);
	}
	
	/**
	 * Get this hats potion amplifier
	 * @return
	 */
	public int getPotionAmplifier ()
	{
		if (potion != null) {
			return potion.getAmplifier();
		}
		return 0;
	}
	
	/**
	 * Get this hats potion effect
	 * @return
	 */
	public PotionEffect getPotion () {
		return potion;
	}
	
	/**
	 * Removes this hats potion effect
	 */
	public void removePotion () 
	{
		potion = null;
		setProperty("potion", "NULL");
	}
	
	/**
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
	 * Gets all ParticleData from this hat
	 * @return
	 */
	public Map<Integer, ParticleData> getParticleData ()
	{
		final Map<Integer, ParticleData> data = new HashMap<Integer, ParticleData>(particleData);
		return data;
	}
	
	/**
	 * Set this hats particle data for the given index
	 * @param index
	 * @param data
	 */
	public void setParticleData (int index, ParticleData data) {
		particleData.put(index, data);
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
	 * Get how many particles this hat has
	 * @return
	 */
	public int getParticleCount () {
		return particleData.size();
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
	public void setParticleBlock (int index, ItemStack block) {
		getParticleData(index).setBlock(block);
	}
	
	/**
	 * Get this hats particle block data
	 * @param index
	 * @return
	 */
	public ItemStack getParticleBlock (int index) {
		return getParticleData(index).getBlock();
	}
	
	/**
	 * Set the current animation frame at index
	 * @param index
	 * @param frame
	 */
	public void setAnimationIndex (int index, int frame) {
		animationIndex.put(index, frame);
	}
	
	/**
	 * Get the current animation frame at index
	 * @param index
	 * @return
	 */
	public int getAnimationIndex (int index) {
		return animationIndex.containsKey(index) ? animationIndex.get(index) : 0;
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
	
	/**
	 * Removes this hat's sound
	 */
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
	 * Players this hat's sound for the player
	 * @param player
	 * @return
	 */
	public boolean playSound (Player player)
	{
		if (sound == null) {
			return false;
		}
		
		player.playSound(player.getLocation(), sound, (float) volume, (float) pitch);
		return true;
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
	@SuppressWarnings("deprecation")
	public void setItem (ItemStack item)
	{
		this.item = item.clone();
		iconData.setMainItem(item);
		
		setProperty("id", "'" + item.getType().toString() + "'");
		if (ParticleHats.serverVersion < 13) {
			setProperty("durability", Short.toString(item.getDurability()));
		}
	}
	
	/**
	 * Get this hat's item
	 * @return
	 */
	public ItemStack getItem () {
		return item;
	}
	
	/**
	 * Get this hats IconData
	 * @return
	 */
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
	 * Set this hats x random offset value
	 * @param x
	 */
	public void setRandomOffsetX (double x) {
		setRandomOffset(x, randomOffset.getY(), randomOffset.getZ());
	}
	
	/**
	 * Set this hats y random offset value
	 * @param y
	 */
	public void setRandomOffsetY (double y) {
		setRandomOffset(randomOffset.getX(), y, randomOffset.getZ());
	}
	
	/**
	 * Set this hats z random offset value
	 * @param z
	 */
	public void setRandomOffsetZ (double z) {
		setRandomOffset(randomOffset.getX(), randomOffset.getY(), z);
	}
	
	/**
	 * Set this hats offset
	 * @param offset
	 */
	public void setRandomOffset (Vector offset) {
		setRandomOffset(offset.getX(), offset.getY(), offset.getZ());
	}
	
	/**
	 * Set this hats random offset
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setRandomOffset (double x, double y, double z)
	{
		randomOffset.setX(MathUtil.clamp(x, -20, 20));
		randomOffset.setY(MathUtil.clamp(y, -20, 20));
		randomOffset.setZ(MathUtil.clamp(z, -20, 20));
		
		setProperty("random_offset_x", Double.toString(randomOffset.getX()));
		setProperty("random_offset_y", Double.toString(randomOffset.getY()));
		setProperty("random_offset_z", Double.toString(randomOffset.getZ()));
	}
	
	/**
	 * Get this hats random offset
	 * @return
	 */
	public Vector getRandomOffset () {
		return randomOffset;
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
		case CHEST:
			y = 1.3f;
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
		angle.setX(MathUtil.wrapAngle(x));
		angle.setY(MathUtil.wrapAngle(y));
		angle.setZ(MathUtil.wrapAngle(z));
		
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
	 * Set the item that is displayed in menus
	 * @param menuItem
	 */
	public void setMenuItem (ItemStack menuItem) {
		this.menuItem = menuItem;
	}
	
	/**
	 * Get this hats ItemStack that is displayed in menus
	 * @return
	 */
	public ItemStack getMenuItem ()
	{
		if (menuItem != null) {
			return menuItem;
		}
		
		menuItem = item.clone();
		ItemUtil.setNameAndDescription(menuItem, name, StringUtil.colorize(normalDescription));
		
		return menuItem;
	}
	
	/**
	 * Get this Hat's legacy purchase path<br>
	 * Used to check against a list of purchases the player has made
	 * @return
	 */
	public String getLegacyPurchaseID () 
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append("3:").append(this.getParticleData(0).getParticle().getLegacyName());
		builder.append(":").append(type.getID());
		builder.append(":").append(location.getID());
		builder.append(":").append(mode.getID());
		builder.append(":").append(animation == ParticleAnimation.ANIMATED);
		
		return builder.toString();
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
			queryBuilder.append(varName).append("=").append(value).append(",");
		}
		
		queryBuilder.deleteCharAt(queryBuilder.length() - 1);
		
		return queryBuilder.toString();
	}
	
	/**
	 * Clears the recently modified properties list
	 */
	public void clearPropertyChanges () {
		modifiedProperties.clear();
	}
	
	/**
	 * Get all properties changed
	 * @return
	 */
	public Map<String, String> getPropertyChanges () {
		return new HashMap<String, String>(modifiedProperties);
	}
	
	/**
	 * Returns a copy of this object with only data necessary for displaying particles
	 * @return
	 */
	public Hat equippableClone ()
	{
		Hat clone = new Hat();
		
		clone.menu = menu;
		
		clone.location = location;
		clone.animation = animation;
		clone.mode = mode;
		clone.type = type;
		clone.animation = animation;
		clone.trackingMethod = trackingMethod;
		clone.customEffect = customEffect;
		clone.potion = potion;
		
		clone.updateFrequency = updateFrequency;
		clone.count = count;
		clone.slot = slot;
		clone.speed = speed;
		clone.demoDuration = demoDuration;
		clone.offset = offset.clone();
		clone.randomOffset = randomOffset.clone();
		clone.angle = angle.clone();
		clone.scale = scale;
		clone.parent = parent;
		
		for (int i = 0; i < type.getParticlesSupported(); i++) {
			clone.setParticleData(i, getParticleData(i).clone());
		}
		
		for (Hat node : nodes) {
			clone.addNode(node.equippableClone());
		}
		
		clone.tags = new ArrayList<ParticleTag>(tags);
		clone.clearPropertyChanges();
		
		return clone;
	}
	
	/**
	 * Returns a copy of this hat with only data necessary for a menu
	 * @return
	 */
	public Hat visualClone ()
	{
		Hat hat = new Hat();
		
		//hat.setMaterial(material);
		hat.setItem(item);
		hat.setIconUpdateFrequency(iconData.getUpdateFrequency());
		hat.setDisplayMode(iconData.getDisplayMode());
		hat.getIconData().setItems(new ArrayList<ItemStackTemplate>(getIconData().getItems()));
		
		hat.clearPropertyChanges();
		return hat;
	}
	
	public Hat clone ()
	{
		Hat clone = new Hat();
		
		clone.menu = menu;
		clone.name = name;
		clone.displayName = displayName;
		clone.permission = permission;
		clone.permissionDeniedMessage = permissionDeniedMessage;
		clone.equipMessage = equipMessage;
		clone.leftClickArgument = leftClickArgument;
		clone.rightClickArgument = rightClickArgument;
		clone.location = location;
		clone.leftAction = leftAction;
		clone.rightAction = rightAction;
		clone.mode = mode;
		clone.type = type;
		clone.animation = animation;
		clone.trackingMethod = trackingMethod;
		clone.isPermanent = isPermanent;
		clone.updateFrequency = updateFrequency;
		clone.price = price;
		clone.speed = speed;
		clone.count = count;
		clone.slot = slot;
		clone.index = index;
		clone.demoDuration = demoDuration;
		clone.scale = scale;
		clone.normalDescription = new ArrayList<String>(normalDescription);
		clone.permissionDescription = new ArrayList<String>(permissionDescription);
		clone.tags = new ArrayList<ParticleTag>(tags);
		clone.potion = potion;
		clone.sound = sound;
		clone.volume = volume;
		clone.pitch = pitch;
		clone.item = item.clone();
		clone.offset = offset.clone();
		clone.randomOffset = randomOffset.clone();
		clone.angle = angle.clone();
		clone.iconData = iconData.clone();
		clone.isLoaded = isLoaded;
		
		if (customEffect != null) {
			clone.customEffect = customEffect.clone();
		}
		
		for (Hat node : nodes) {
			clone.addNode(node.equippableClone());
		}
		
		for (int i = 0; i < type.getParticlesSupported(); i++) {
			clone.setParticleData(i, getParticleData(i).clone());
		}
		
		return clone;
	}
	
	@Override
	public boolean equals (Object o)
	{
		if (this == o) return true;
		if (o == null) return false;
		if (!(o instanceof Hat)) return false;
		
		Hat hat = (Hat)o;
		
		if (!hat.menu.equals(menu)) return false;	
		if (hat.slot != slot) return false;
		if (!hat.permission.equals(permission)) return false;
		if (!hat.getType().equals(type)) return false;
		if (!hat.getLocation().equals(location)) return false;
		if (!hat.getMode().equals(mode)) return false;
		if (!hat.getAnimation().equals(animation)) return false;
		if (!hat.getTrackingMethod().equals(trackingMethod)) return false;
		if (!hat.getOffset().equals(offset)) return false;
		if (!hat.getRandomOffset().equals(randomOffset)) return false;
		if (!hat.getAngle().equals(angle)) return false; 
		
		if (hat.getCustomEffect() != null) {
			if (!hat.getCustomEffect().equals(customEffect)) return false;
		} else {
			if (customEffect != null) return false;
		}
		
		if (hat.getCount() != count) return false;
		if (hat.getSpeed() != speed) return false;
		if (!hat.getLabel().equals(label)) return false;
		if (!hat.getLeftClickAction().equals(leftAction)) return false;		
		if (!hat.getRightClickAction().equals(rightAction)) return false;
		
		if (!hat.getLeftClickArgument().equals(leftClickArgument)) return false;		
		if (!hat.getRightClickArgument().equals(rightClickArgument)) return false;
		
		if (hat.getPotion() != null) {
			if (!hat.getPotion().equals(potion)) return false;
		} else {
			if (potion != null) return false;
		}
		
		for (int i = 0; i < particleData.size(); i++) {
			if (!hat.getParticleData(i).equals(getParticleData(i))) return false;
		}
		
		for (int i = 0; i < nodes.size(); i++) 
		{
			Hat node = hat.getNode(i);
			Hat n = getNode(i);
			
			if (node != null) {
				if (!node.equals(n)) return false;
			} else {
				if (n != null) return false;
			}
		}
		
		return true;
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
