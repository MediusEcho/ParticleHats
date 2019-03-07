package com.mediusecho.particlehats.editor;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.particles.ParticleEffect.ParticleProperty;
import com.mediusecho.particlehats.particles.effects.CustomEffect;
import com.mediusecho.particlehats.particles.properties.IconDisplayMode;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.particles.properties.ParticleAnimation;
import com.mediusecho.particlehats.particles.properties.ParticleColor;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleMode;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;
import com.mediusecho.particlehats.particles.properties.ParticleType;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorLore {

	private static final Core core = Core.instance;
	
	/**
	 * Applies a generic description without any special properties to an item
	 * @param item
	 * @param description
	 */
	public static void updateGenericDescription (ItemStack item, Message description) {
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(description.getValue()));
	}
	
	public static void updateTypeDescription (ItemStack item, Hat hat)
	{
		//"/n&8Current:/n&8» {1=/n&8»}{2}/n/n&3Left Click to Change Type/n{3=&cShift Click to Change Animation}"
		ParticleType type = hat.getType();
		
		String description = Message.EDITOR_MAIN_MENU_TYPE_DESCRIPTION.getValue();
		String[] typeInfo = StringUtil.parseValue(description, "1");
		String[] toggleInfo = StringUtil.parseValue(description, "3");
		
		String animationDescription = "";
		if (type.supportsAnimation())
		{
			ParticleAnimation animation = hat.getAnimation();
			animationDescription = Message.EDITOR_MAIN_MENU_ANIMATION_DESCRIPTION.getValue();
			animationDescription = animationDescription
					.replace("{1}", animation.getStrippedName())
					.replace("{2}", animation.getDescription());
		}
		
		String custom = "";
		String toggle = type.supportsAnimation() ? toggleInfo[1] : "";
		
		if (type.isCustom())
		{
			CustomEffect customEffect = hat.getCustomEffect();
			if (customEffect != null) {
				custom = typeInfo[1] + Message.EDITOR_TYPE_MENU_TYPE_PREFIX.getValue() + StringUtil.capitalizeFirstLetter(customEffect.getImageName().toLowerCase());
			}
			
			else {
				custom = typeInfo[1] + Message.EDITOR_MAIN_MENU_CUSTOM_TYPE_ERROR.getValue();
			}
		}
		
		description = description
				.replace(typeInfo[0], type.getStrippedName() + custom)
				.replace("{2}", animationDescription)
				.replace(toggleInfo[0], toggle);
		
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(description));
	}
	
	public static void updateTypeItemDescription (ItemStack item, ParticleType type, boolean isSelected)
	{
		//"{1=/n/n}&8Supports &3{2} &8Particle{3=s}/n/n{4=&3Click to Select}{5=&3Selected}"
		String description = Message.EDITOR_TYPE_MENU_TYPE_DESCRIPTION.getValue();
		String[] descriptionInfo = StringUtil.parseValue(description, "1");
		String[] suffixInfo = StringUtil.parseValue(description, "3");
		String[] selectInfo = StringUtil.parseValue(description, "4");
		String[] selectedInfo = StringUtil.parseValue(description, "5");
		
		String typeDescription = type.getDescription().isEmpty() ? "" : type.getDescription() + descriptionInfo[1];
		String suffix = type.getParticlesSupported() != 1 ? suffixInfo[1] : "";
		String select = isSelected ? "" : selectInfo[1];
		String selected = isSelected ? selectedInfo[1] : "";
		
		description = description
				.replace(descriptionInfo[0], typeDescription)
				.replace(suffixInfo[0], suffix)
				.replace(selectInfo[0], select)
				.replace(selectedInfo[0], selected)
				.replace("{2}", Integer.toString(type.getParticlesSupported()));
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(description));
		
//		String typeDescription = type.getDescription();
//		description = description.replace(descriptionInfo[0], typeDescription.isEmpty() ? "" : typeDescription + descriptionInfo[1]);
//		
//		if (selected) {
//			description = description.replace(selectInfo[0], "").replace(selectedInfo[0], selectedInfo[1]);
//		} else {
//			description = description.replace(selectedInfo[0], "").replace(selectInfo[0], selectInfo[1]);
//		}
//		
//		description = description.replace("{2}", Integer.toString(type.getParticlesSupported()));
		
		//ItemUtil.setItemDescription(item, StringUtil.parseDescription(description));
	}
	
	/**
	 * Applies a description to this ItemStack using ParticleLocation data
	 * @param item
	 * @param location
	 * @param description
	 */
	public static void updateLocationDescription (ItemStack item, ParticleLocation location, Message description)
	{
		final int length = ParticleLocation.values().length;
		final int id = location.getID();
		
		String s = description.getValue()
				.replace("{1}", ParticleLocation.fromId(MathUtil.wrap(id - 1, length, 0)).getDisplayName())
				.replace("{2}", location.getDisplayName())
				.replace("{3}", ParticleLocation.fromId(MathUtil.wrap(id + 1, length, 0)).getDisplayName());
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));		
	}
	
	/**
	 * Applies a description to this ItemStack using ParticleMode data
	 * @param item
	 * @param mode
	 * @param description
	 */
	public static void updateModeDescription (ItemStack item, ParticleMode mode, Message description)
	{
		final int length = ParticleMode.values().length;
		final int id = mode.getID();
		
		String s = description.getValue()
				.replace("{1}", ParticleMode.fromId(MathUtil.wrap(id - 1, length, 0)).getDisplayName())
				.replace("{2}", mode.getDisplayName())
				.replace("{3}", ParticleMode.fromId(MathUtil.wrap(id + 1, length, 0)).getDisplayName())
				.replace("{4}", mode.getDescription());
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));		
	}

	/**
	 * Applies a description to this ItemStack using IconDisplayMode data
	 * @param item
	 * @param displayMode
	 * @param description
	 */
	public static void updateDisplayModeDescription (ItemStack item, IconDisplayMode displayMode, Message description)
	{
		final int length = IconDisplayMode.values().length;
		final int id = displayMode.getID();
		
		String s = description.getValue()
				.replace("{1}", IconDisplayMode.fromId(MathUtil.wrap(id - 1, length, 0)).getDisplayName())
				.replace("{2}", displayMode.getDisplayName())
				.replace("{3}", displayMode.getDescription());
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));	
	}
	
	/**
	 * Applies a description to this ItemStack using Vector data
	 * @param item
	 * @param hat
	 * @param description
	 */
	public static void updateVectorDescription (ItemStack item, Vector vector, Message description)
	{
		String s = description.getValue()
			.replace("{1}", Double.toString(vector.getX()))
			.replace("{2}", Double.toString(vector.getY()))
			.replace("{3}", Double.toString(vector.getZ()));
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	/**
	 * Applies a description to this ItemStack using Color data
	 * @param item
	 * @param color
	 * @param description
	 */
	public static void updateColorDescription (ItemStack item, Color color, boolean random, Message description)
	{
		String rs = Message.EDITOR_COLOR_MENU_RANDOM_SUFFIX.getValue();
		String r = random ? rs : Integer.toString(color.getRed());
		String g = random ? rs : Integer.toString(color.getGreen());
		String b = random ? rs : Integer.toString(color.getBlue());
		
		String s = description.getValue()
				.replace("{1}", r)
				.replace("{2}", g)
				.replace("{3}", b);
			ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	/**
	 * Applies a description to this ItemStack using UpdateFrequency data (int)
	 * @param item
	 * @param updateFrequency
	 * @param description
	 */
	public static void updateFrequencyDescription (ItemStack item, int updateFrequency, Message description)
	{
		String pluralSuffix = StringUtil.getParseValue(description.getValue(), "2");
		String suffix = updateFrequency > 1 ? pluralSuffix : "";
		String s = description.getValue()
				.replace("{1}", Integer.toString(updateFrequency))
				.replaceAll("\\{2.*\\}", suffix);
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	/**
	 * Applies a description to this ItemStack using a price value
	 * @param item
	 * @param price
	 * @param description
	 */
	public static void updatePriceDescription (ItemStack item, int price, Message description)
	{
		String[] priceData = StringUtil.parseValue(description.getValue(), "1");
		String c = price > 0 ?  StringUtil.escapeSpecialCharacters(SettingsManager.CURRENCY.getString()) : "";
		
		String s = description.getValue()
				.replace(priceData[0], price == 0 ? priceData[1] : Integer.toString(price))
				.replace("{2}", c);
		
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	/**
	 * Applies a description to this ItemStack using a duration value
	 * @param item
	 * @param duration
	 * @param description
	 */
	public static void updateDurationDescription (ItemStack item, int duration, Message description)
	{
		int time = duration / 20;
		int remainder = time % 3600; // get the rest in seconds
		int minutes = remainder / 60; // get the amount of minutes from the rest
		int seconds = remainder % 60; // get the new rest
		String disMinu = (minutes < 10 ? "0" : "") + minutes; // get minutes and add "0" before if lower than 10
		String disSec = (seconds < 10 ? "0" : "") + seconds; // get seconds and add "0" before if lower than 10
		String formattedTime = disMinu + ":" + disSec; //get the whole time
		
		String desc = description.getValue();
		String s = desc.replace("{1}", formattedTime);
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
		
		//"/n&8» {1} second{2=s}/n/n&3Left Click to Add 1/nRight Click to Subtract 1/n&cShift Click to Adjust by 30"
		//DecimalFormat df = new DecimalFormat("#.#");
		//String[] suffixInfo = StringUtil.parseValue(description, "2");
		
		//SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
		
		//double time = duration / 20D;
		//String suffix = time == 1 ? "" : suffixInfo[1];
		
//		description = description.replace(suffixInfo[0], suffix)
//				.replace("{1}", formattedTime);
		
//		ItemUtil.setItemDescription(item, StringUtil.parseDescription(description));
	}
	
	/**
	 * Applies a description to this ItemStack using an int
	 * @param item
	 * @param speed
	 * @param description
	 */
	public static void updateIntegerDescription (ItemStack item, int value, Message description)
	{
		String s = description.getValue()
				.replace("{1}", Integer.toString(value));
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	/**
	 * Applies a description to this ItemStack using an int
	 * @param item
	 * @param speed
	 * @param description
	 */
	public static void updateDoubleDescription (ItemStack item, double value, Message description)
	{
		String s = description.getValue()
				.replace("{1}", Double.toString(value));
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	/**
	 * Applies a description to this ItemStack using Sound data
	 * @param item
	 * @param sound
	 * @param currentSound
	 * @param description
	 */
	public static void updateSoundDescription (ItemStack item, Sound sound, Sound currentSound, Message description)
	{
		String[] selectedParse = StringUtil.parseValue(description.getValue(), "1");
		String selectedSuffix = (currentSound != null && currentSound.equals(sound)) ? selectedParse[1] : "";
		String s = description.getValue()
				.replace(selectedParse[0], selectedSuffix);
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	public static void updateSoundItemDescription (ItemStack item, Hat hat)
	{
		String description = Message.EDITOR_MAIN_MENU_SOUND_DESCRIPTION.getValue();
		String soundInfo[] = StringUtil.parseValue(description, "1");
		String clearInfo[] = StringUtil.parseValue(description, "4");
		Sound sound = hat.getSound();
		
		boolean nullSound = sound == null;
		String soundName = !nullSound ? StringUtil.capitalizeFirstLetter(sound.toString().toLowerCase()) : soundInfo[1];
		String clear = !nullSound ? clearInfo[1] : "";
		
		String s = description.replace(soundInfo[0], soundName)
				.replace("{2}", Double.toString(hat.getSoundVolume()))
				.replace("{3}", Double.toString(hat.getSoundPitch()))
				.replace(clearInfo[0], clear);
		
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	/**
	 * Applies a description to this ItemStack using a ParticleAction
	 * @param item
	 * @param hat
	 * @param action
	 * @param argument
	 */
	public static void updateSpecificActionDescription (ItemStack item, Hat hat, ParticleAction action, String argument)
	{
		String description = Message.EDITOR_ACTION_OVERVIEW_MENU_ACTION_DESCRIPTION.getValue();
		String actionDescription = getParsedActionDescription(hat, action, argument);
		String[] dataInfo = StringUtil.parseValue(description, "2");
		
		description = description.replace("{1}", actionDescription)
			.replace(dataInfo[0], action.hasData() ? dataInfo[1] : "");

		ItemUtil.setItemDescription(item, StringUtil.parseDescription(description));
	}
	
	/**
	 * Applies a description to this ItemStack using ParticleAction data
	 * @param item
	 * @param hat
	 */
	public static void updateGenericActionDescription (ItemStack item, Hat hat)
	{
		ParticleAction leftClickAction  = hat.getLeftClickAction();
		ParticleAction rightClickAction = hat.getRightClickAction();
		
		String leftClickDescription = getParsedActionDescription(hat, leftClickAction, hat.getLeftClickArgument());
		String rightClickDescription = getParsedActionDescription(hat, rightClickAction, hat.getRightClickArgument());
		
		String description = Message.EDITOR_MAIN_MENU_ACTION_DESCRIPTION.getValue();
		description = description
				.replace("{1}", leftClickDescription)
				.replace("{2}", rightClickDescription);
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(description));
	}
	
	/**
	 * Translates the given action
	 * @param hat
	 * @param action
	 * @param argument
	 * @return
	 */
	private static String getParsedActionDescription (Hat hat, ParticleAction action, String argument)
	{
		switch (action)
		{
		case OPEN_MENU_PERMISSION:
		case OPEN_MENU:
		{
			String description = Message.EDITOR_ACTION_MENU_MENU_DESCRIPTION.getValue();
			String[] menuInfo = StringUtil.parseValue(description, "2");
			String menu = argument.equals("") || !core.getDatabase().menuExists(argument) ? menuInfo[1] : argument;
			
			description = description.replace("{1}", action.getStrippedName())
					.replace(menuInfo[0], menu);
			
			return description;
		}
			
		case COMMAND:
		{
			String description = Message.EDITOR_ACTION_MENU_COMMAND_DESCRIPTION.getValue();
			String[] commandInfo = StringUtil.parseValue(description, "2");
			String command = argument.equals("") ? commandInfo[1] : "/" + argument;
			
			description = description.replace("{1}", action.getStrippedName())
					.replace(commandInfo[0], command);
			
			return description;
		}
			
		case DEMO:
		{
			String description = Message.EDITOR_ACTION_MENU_DEMO_DESCRIPTION.getValue();
			
			int time = hat.getDemoDuration() / 20;
			int remainder = time % 3600; // get the rest in seconds
			int minutes = remainder / 60; // get the amount of minutes from the rest
			int seconds = remainder % 60; // get the new rest
			String disMinu = (minutes < 10 ? "0" : "") + minutes; // get minutes and add "0" before if lower than 10
			String disSec = (seconds < 10 ? "0" : "") + seconds; // get seconds and add "0" before if lower than 10
			String formattedTime = disMinu + ":" + disSec; //get the whole time
			
			String s = description
					.replace("{1}", action.getStrippedName())
					.replace("{2}", formattedTime);
			return s;
		}
		
		default:
		{
			String description = Message.EDITOR_ACTION_MENU_MISC_DESCRIPTION.getValue();
			description = description.replace("{1}", action.getStrippedName());
			return description;
		}
		}
	}
	
	public static void updateNameDescription (ItemStack item, Hat hat)
	{
		//"/n&8Current:/n&8» {1}/n/n&3Left Click to Change{2=/n&cShift Right Click to Reset}"
		String description = Message.EDITOR_META_MENU_NAME_DESCRIPTION.getValue();
		String[] clearInfo = StringUtil.parseValue(description, "2");
		String clear = hat.getName().equals(Message.EDITOR_MISC_NEW_PARTICLE.getRawValue()) ? "" : clearInfo[1];
		
		String s = description.replace("{1}", hat.getDisplayName())
				.replace(clearInfo[0], clear);
		
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	public static void updateDescriptionDescription (ItemStack item, List<String> desc)
	{
		if (desc.isEmpty())
		{
			String description = Message.EDITOR_META_MENU_EMPTY_DESCRIPTION.getValue();
			ItemUtil.setItemDescription(item, StringUtil.parseDescription(description));
		}
		
		else
		{
			//"/n&8Current:/n{1=&7{2} &8» &7{3}}/n/n&3Left Click to Change{4=/n/n&cShift Right Click to Clear}"
			String description = Message.EDITOR_META_MENU_DESCRIPTION_DESCRIPTION.getValue();
			
			String[] clearInfo = StringUtil.parseValue(description, "2");
			StringBuilder sb = new StringBuilder();

			//int index = 0;
			for (String s : desc) 
			{
				String prefix = "&r";
				if (!s.isEmpty() && s.charAt(0) != '&') {
					prefix = "&5&o";
				}
				sb.append("&7- " + prefix + s).append("/n");
			}
			
			description = description.replace("{1}", sb.toString())
				.replace(clearInfo[0], clearInfo[1]);
			
			ItemUtil.setItemDescription(item, StringUtil.parseDescription(description));
		}
	}
	
	/**
	 * Applies a description to this ItemStack using a hats permission
	 * @param item
	 * @param hat
	 */
	public static void updatePermissionDescription (ItemStack item, Hat hat)
	{
		String description = Message.EDITOR_META_MENU_PERMISSION_DESCRIPTION.getValue();
		String s = description.replace("{1}", hat.getPermission())
				.replace("{2}", hat.getFullPermission());
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	/**
	 * Applies a description to this ItemStack using a label
	 * @param item
	 * @param label
	 */
	public static void updateLabelDescription (ItemStack item, String label)
	{
		//"/n&8Labels allow you to use this hat/n&8in commands like: &7/h set <label>/n/n&8Current:/n&8» {1=&cNot Set}{2=/n/n&cShift Right Click to Clear}"
		String description = Message.EDITOR_META_MENU_LABEL_DESCRIPTION.getValue();
		String[] labelInfo = StringUtil.parseValue(description, "1");
		String[] clearInfo = StringUtil.parseValue(description, "2");
		
		String l = label.equals("") ? labelInfo[1] : label;
		String clear = !label.equals("") ? clearInfo[1] : "";
		
		String s = description.replace(labelInfo[0], l)
				.replace(clearInfo[0], clear);
		
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	/**
	 * Applies a description to this ItemStack using an equip message
	 * @param item
	 * @param equip
	 */
	public static void updateEquipDescription (ItemStack item, String equip)
	{
		//"/n&8Show the player this message when/n&8they equip this hat instead of the/n&8global equip message/n/n&8Current:/n&8» &7{1=&cNot Set}/n/n&3Left Click to Change{2=/n&cShift Right Click to Clear}"
		String description = Message.EDITOR_META_MENU_EQUIP_DESCRIPTION.getValue();
		String[] equipInfo = StringUtil.parseValue(description, "1");
		String[] clearInfo = StringUtil.parseValue(description, "2");
		
		String e = equip.equals("") ? equipInfo[1] : equip;
		String clear = !equip.equals("") ? clearInfo[1] : "";
		
		String s = description.replace(equipInfo[0], e)
				.replace(clearInfo[0], clear);
		
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	/**
	 * Applies a description to this ItemStack using a permission denied message
	 * @param item
	 * @param permissionDenied
	 */
	public static void updatePermissionDeniedDescription (ItemStack item, String permissionDenied)
	{
		//"/n&8Current:/n&8» &7{1=&cNot Set}/n/n&3Left Click to Change{2=/n&cShift Right Click to Clear}"
		String description = Message.EDITOR_META_MENU_PERMISSION_DENIED_DESCRIPTION.getValue();
		String[] deniedInfo = StringUtil.parseValue(description, "1");
		String[] clearInfo = StringUtil.parseValue(description, "2");
		
		String denied = permissionDenied.equals("") ? deniedInfo[1] : permissionDenied;
		String clear = !permissionDenied.equals("") ? clearInfo[1] : "";
		
		String s = description.replace(deniedInfo[0], denied)
				.replace(clearInfo[0], clear);
		
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	public static void updatePreviewDecription (ItemStack item, List<String> description)
	{
		//"/n{1=&cEmpty}{2=/n&cShift Right Click to Clear}"
		String desc = Message.EDITOR_DESCRIPTION_MENU_PREVIEW_DESCRIPTION.getValue();
		String[] emptyInfo = StringUtil.parseValue(desc, "1");
		String[] clearInfo = StringUtil.parseValue(desc, "2");
		
		String s;
		if (description.isEmpty()) {
			s = desc.replace(emptyInfo[0], emptyInfo[1]);
		}
		
		else
		{			
			StringBuilder sb = new StringBuilder();
			for (String line : description) 
			{
				String prefix = "&r";
				if (!line.isEmpty() && line.charAt(0) != '&') {
					prefix = "&5&o";
				}
				sb.append("&7- " + prefix + line).append("/n");
			}
			s = desc.replace(emptyInfo[0], sb.toString());
		}
		
		s = s.replace(clearInfo[0], description.isEmpty() ? "" : clearInfo[1]);
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	/**
	 * Applies a description to this ItemStack using a hats supported tracking methods
	 * @param item
	 * @param hat
	 */
	public static void updateTrackingDescription (ItemStack item, Hat hat)
	{
		String description = "";
		
		List<ParticleTracking> methods = hat.getEffect().getSupportedTrackingMethods();
		ParticleTracking method =  hat.getTrackingMethod();
		
		int index = methods.indexOf(method);
		int size = methods.size();
		
		if (size == 1)
		{
			String desc = Message.EDITOR_MAIN_MENU_TRACKING_METHOD_DESCRIPTION_SINGLE.getValue();
			description = desc.replace("{1}", method.getDisplayName());
		}
		
		else
		{
			String desc = Message.EDITOR_MAIN_MENU_TRACKING_METHOD_DESCRIPTION_MULTIPLE.getValue();
			description = desc
				.replace("{1}", methods.get(MathUtil.wrap(index - 1, size, 0)).getDisplayName())
				.replace("{2}", method.getDisplayName())
				.replace("{3}", methods.get(MathUtil.wrap(index + 1, size, 0)).getDisplayName());
		}
		
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(description));
	}
	
	public static void updateParticleDescription (ItemStack item, Hat hat, int particleIndex)
	{
		ParticleEffect particle = hat.getParticle(particleIndex);
		ParticleProperty property = particle.getProperty();
		String particleName = particle.getStrippedName();
		
		switch (property)
		{
			case COLOR:
			{
				ParticleColor color = hat.getParticleColor(particleIndex);
				if (color.isRandom())
				{
					String description = Message.EDITOR_PARTICLE_RANDOM_COLOR_DESCRIPTION.getValue();
					String s = description
							.replace("{1}", particleName);
					ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
				}
				
				else
				{
					String description = Message.EDITOR_PARTICLE_RGB_COLOR_DESCRIPTION.getValue();
					Color c = color.getColor();
					String s = description
							.replace("{1}", particleName)
							.replace("{2}", Integer.toString(c.getRed()))
							.replace("{3}", Integer.toString(c.getGreen()))
							.replace("{4}", Integer.toString(c.getBlue()));
					ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
				}
				break;
			}
			
			case ITEM_DATA:
			case BLOCK_DATA:
			{
				boolean isBlock = property == ParticleProperty.BLOCK_DATA;
				
				String description = isBlock? Message.EDITOR_PARTICLE_BLOCK_DESCRIPTION.getValue() : Message.EDITOR_PARTICLE_ITEM_DESCRIPTION.getValue();
				String name = isBlock ? hat.getParticleBlock(particleIndex).getMaterial().toString() : hat.getParticleItem(particleIndex).getType().toString();
				String s = description
						.replace("{1}", particleName)
						.replace("{2}", StringUtil.capitalizeFirstLetter(name.toLowerCase()));
				ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
				break;
			}
			
			case ITEMSTACK_DATA:
			{
				String description = Message.EDITOR_PARTICLE_ITEMSTACK_DESCRIPTION.getValue();
				int items = hat.getItemStackData(particleIndex).getItems().size();
				String s = description
						.replace("{1}", particleName)
						.replace("{2}", Integer.toString(items));
				ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
				break;
			}
			
			default:
			{
				String description = Message.EDITOR_PARTICLE_MISC_DESCRIPTION.getValue();
				String s = description.replace("{1}", particleName);
				ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
			}
		}
	}
	
	public static void updateGravityDescription (ItemStack item, boolean gravity)
	{
		//"/n&8Gravity:/n&8» {1=&aEnabled}{2=&cDisabled}/n/n&3Click to Toggle"
		String description = Message.EDITOR_ITEMSTACK_MENU_GRAVITY_DESCRIPTION.getValue();
		String[] enabledInfo = StringUtil.parseValue(description, "1");
		String[] disabledInfo = StringUtil.parseValue(description, "2");
		
		String enabled = gravity ? enabledInfo[1] : "";
		String disabled = gravity ? "" : disabledInfo[1];
		
		String s = description
				.replace(enabledInfo[0], enabled)
				.replace(disabledInfo[0], disabled);
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	public static void updateHatDescription (ItemStack item, Hat hat)
	{
		//"&7Slot &f{1}/n&7Type: &f{2}/n&7Location: &f{3}/n&7Mode: &f{4}/n&7Update: &f{5} &7tick{6=s}"
		String description = Message.EDITOR_HAT_GENERIC_DESCRIPTION.getValue();
		String s = 
				description.replace("{1}", String.valueOf(hat.getSlot()));
	}
}
