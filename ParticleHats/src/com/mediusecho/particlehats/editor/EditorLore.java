package com.mediusecho.particlehats.editor;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.particles.ParticleEffect.ParticleProperty;
import com.mediusecho.particlehats.particles.effects.PixelEffect;
import com.mediusecho.particlehats.particles.properties.*;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class EditorLore {

	private static final ParticleHats core = ParticleHats.instance;
	
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
			PixelEffect customEffect = hat.getCustomEffect();
			if (customEffect != null) {
				custom = typeInfo[1] + Message.EDITOR_TYPE_MENU_TYPE_PREFIX.getValue().replace("{1}", StringUtil.capitalizeFirstLetter(customEffect.getImageName().toLowerCase()));
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
		List<ParticleMode> modes = ParticleMode.getSupportedModes();
		int index = modes.indexOf(mode);
		int size = modes.size();
		
		String s = description.getValue()
				.replace("{1}", modes.get(MathUtil.wrap(index - 1, size, 0)).getDisplayName())
				.replace("{2}", mode.getDisplayName())
				.replace("{3}", modes.get(MathUtil.wrap(index + 1, size, 0)).getDisplayName())
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
	
	public static void updateOffsetDescription (ItemStack item, Hat hat)
	{
		String description = Message.EDITOR_MAIN_MENU_OFFSET_DESCRIPTION.getValue();
		Vector offset = hat.getOffset();
		Vector randomOffset = hat.getRandomOffset();
		
		String s = description
				.replace("{1}", Double.toString(offset.getX()))
				.replace("{2}", Double.toString(offset.getY()))
				.replace("{3}", Double.toString(offset.getZ()))
				.replace("{4}", Double.toString(randomOffset.getX()))
				.replace("{5}", Double.toString(randomOffset.getY()))
				.replace("{6}", Double.toString(randomOffset.getZ()));
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
		String rs = Message.EDITOR_COLOUR_MENU_RANDOM_SUFFIX.getValue();
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
//		String c = price > 0 ? StringUtil.escapeSpecialCharacters(SettingsManager.CURRENCY.getString()) : "";
		String c = price > 0 ? SettingsManager.CURRENCY.getString() : "";
		
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
	 * Applies a description to this ItemStack using a double
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
			String formattedTime = StringUtil.getTimeFormat(time);
			
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
			String description = Message.EDITOR_META_MENU_DESCRIPTION_DESCRIPTION.getValue();
			
			String[] clearInfo = StringUtil.parseValue(description, "2");
			StringBuilder sb = new StringBuilder();

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
		String description = Message.EDITOR_META_MENU_PERMISSION_DENIED_DESCRIPTION.getValue();
		String[] deniedInfo = StringUtil.parseValue(description, "1");
		String[] clearInfo = StringUtil.parseValue(description, "2");
		
		String denied = permissionDenied.equals("") ? deniedInfo[1] : permissionDenied;
		String clear = !permissionDenied.equals("") ? clearInfo[1] : "";
		
		String s = description.replace(deniedInfo[0], denied)
				.replace(clearInfo[0], clear);
		
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	public static void updatePreviewDecription (ItemStack item, List<String> description, Hat hat)
	{
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
				
				line = StringUtil.parseString(line, hat);
				
				//sb.append("&7- ").append(prefix).append(line).append("/n");
				sb.append(prefix).append(line).append("/n");
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
	
	@SuppressWarnings("deprecation")
	public static void updateParticleDescription (ItemStack item, Hat hat, int particleIndex)
	{
		ParticleEffect particle = hat.getParticle(particleIndex);
		ParticleProperty property = particle.getProperty();
		String particleName = particle.getStrippedName();
		
		switch (property)
		{
			case COLOR:
			case DUST_OPTIONS:
			{
				ColorData color = hat.getParticleData(particleIndex).getColorData();
				if (color.isRandom())
				{
					String description = Message.EDITOR_PARTICLE_RANDOM_COLOUR_DESCRIPTION.getValue();
					String s = description
							.replace("{1}", particleName);
					ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
				}
				
				else
				{
					String description = Message.EDITOR_PARTICLE_RGB_COLOUR_DESCRIPTION.getValue();
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
				ItemStack i = isBlock ? hat.getParticleBlock(particleIndex) : hat.getParticleItem(particleIndex);
				
				String name = StringUtil.capitalizeFirstLetter(i.getType().toString().toLowerCase());
				if (ParticleHats.serverVersion < 13) {
					name += " [" + Short.toString(i.getDurability()) + "]";
				}
				
				String description = isBlock? Message.EDITOR_PARTICLE_BLOCK_DESCRIPTION.getValue() : Message.EDITOR_PARTICLE_ITEM_DESCRIPTION.getValue();
				//String name = isBlock ? hat.getParticleBlock(particleIndex).getType().toString() : hat.getParticleItem(particleIndex).getType().toString();
				String s = description
						.replace("{1}", particleName)
						.replace("{2}", StringUtil.capitalizeFirstLetter(name.toLowerCase()));
				ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
				break;
			}
			
			case ITEMSTACK_DATA:
			{
				String description = Message.EDITOR_PARTICLE_ITEMSTACK_DESCRIPTION.getValue();
				ItemStackData itemStackData = hat.getParticleData(particleIndex).getItemStackData();
				int items = itemStackData.getItems().size();
				
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
	
	public static void updateParticleItemDescription (ItemStack item, ParticleEffect particle, boolean isSelected)
	{
		String description = Message.EDITOR_PARTICLE_MENU_PARTICLE_DESCRIPTION.getValue();
		String[] descriptionInfo = StringUtil.parseValue(description, "1");
		String[] selectInfo = StringUtil.parseValue(description, "2");
		String[] selectedInfo = StringUtil.parseValue(description, "3");
		
		String desc = particle.getDescription().equals("") ? "" : particle.getDescription() + descriptionInfo[1];
		String select = isSelected ? "" : selectInfo[1];
		String selected = isSelected ? selectedInfo[1] : "";
		
		description = description
				.replace(selectInfo[0], select)
				.replace(selectedInfo[0], selected)
				.replace(descriptionInfo[0], desc);
		
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(description));
	}
	
	/**
	 * Applies a description to this ItemStack using boolean data
	 * @param item
	 * @param enabled
	 * @param message
	 */
	public static void updateBooleanDescription (ItemStack item, boolean enabled, Message message)
	{
		String description = message.getValue();
		String[] enabledInfo = StringUtil.parseValue(description, "1");
		String[] disabledInfo = StringUtil.parseValue(description, "2");
		
		String e = enabled ? enabledInfo[1] : "";
		String d = enabled ? "" : disabledInfo[1];
		
		String s = description
				.replace(enabledInfo[0], e).replace(disabledInfo[0], d);
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	/**
	 * Applies a description to this ItemStack using an alias
	 * @param item
	 * @param alias
	 * @param message
	 */
	public static void updateAliasDescription (ItemStack item, String alias)
	{
		String description = Message.EDITOR_SETTINGS_MENU_ALIAS_DESCRIPTION.getValue();
		String[] aliasInfo = StringUtil.parseValue(description, "1");
		String[] resetInfo = StringUtil.parseValue(description, "2");
		
		String a = alias != null ? alias : aliasInfo[1];
		String reset = alias != null ? resetInfo[1] : "";
		
		String s = description.replace(aliasInfo[0], a).replace(resetInfo[0], reset);
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	/**
	 * Applies a description to this ItemStack using Hat properties
	 * @param item
	 * @param hat
	 * @param isHat
	 */
	public static void updateHatDescription (ItemStack item, Hat hat, boolean isHat)
	{
		List<String> description = new ArrayList<String>();
		
		if (isHat)
		{
			String slotDesc = Message.EDITOR_HAT_SLOT_DESCRIPTION.getValue();
			description.add(slotDesc.replace("{1}", Integer.toString(hat.getSlot())));
		}
		
		if (hat.getLeftClickAction() == ParticleAction.EQUIP || hat.getRightClickAction() == ParticleAction.EQUIP)
		{
			String typeDesc = Message.EDITOR_HAT_TYPE_DESCRIPTION.getValue();
			String type = hat.getType().isCustom() ? hat.getCustomEffect().getImageDisplayName() : hat.getType().getStrippedName();
			description.add(typeDesc.replace("{1}", type));
			
			String locationDesc = Message.EDITOR_HAT_LOCATION_DESCRIPTION.getValue();
			description.add(locationDesc.replace("{1}", hat.getLocation().getStrippedName()));
			
			String modeDesc = Message.EDITOR_HAT_MODE_DESCRIPTION.getValue();
			description.add(modeDesc.replace("{1}", hat.getMode().getStrippedName()));
			
			String frequencyDesc = Message.EDITOR_HAT_FREQUENCY_DESCRIPTION.getValue();
			String[] tickInfo = StringUtil.parseValue(frequencyDesc, "2");
			String ticks = hat.getUpdateFrequency() > 1 ? tickInfo[1] : "";
			description.add(frequencyDesc.replace("{1}", Integer.toString(hat.getUpdateFrequency())).replace(tickInfo[0], ticks));
			
			String particleDesc = Message.EDITOR_HAT_PARTICLES_DESCRIPTION.getValue();
			String[] particleInfo = StringUtil.parseValue(particleDesc, "1");
			String particles = hat.hasParticles() ? Integer.toString(hat.getParticleCount()) : particleInfo[1];
			description.add(particleDesc.replace(particleInfo[0], particles));
			
			if (isHat)
			{
				String nodeDesc = Message.EDITOR_HAT_NODES_DESCRIPTION.getValue();
				description.add(nodeDesc.replace("{1}", Integer.toString(hat.getNodeCount())));
			}
		}
		
		if (isHat)
		{
			ParticleAction leftAction = hat.getLeftClickAction();
			String leftActionDesc = Message.EDITOR_HAT_LEFT_CLICK_DESCRIPTION.getValue();
			
			switch  (leftAction)
			{
				case COMMAND:
				{
					String command = Message.EDITOR_HAT_COMMAND_DESCRIPTION.getValue().replace("{1}", hat.getLeftClickArgument());
					description.add(leftActionDesc.replace("{1}", command));
				}
					break;
					
				case OPEN_MENU_PERMISSION:
				case OPEN_MENU:
				{
					String menu = Message.EDITOR_HAT_MENU_DESCRIPTION.getValue().replace("{1}", hat.getLeftClickArgument());
					description.add(leftActionDesc.replace("{1}", menu));
				}
					break;
					
				case DEMO:
				{
					String formattedTime = StringUtil.getTimeFormat(hat.getDemoDuration() / 20);
					String durationDesc = Message.EDITOR_HAT_DURATION_DESCRIPTION.getValue().replace("{1}", formattedTime);
					
					description.add(leftActionDesc.replace("{1}", durationDesc));
				}
					break;
					
				default:
					description.add(leftActionDesc.replace("{1}", leftAction.getStrippedName()));
			}
			
			ParticleAction rightAction = hat.getRightClickAction();
			String rightActionDesc = Message.EDITOR_HAT_RIGHT_CLICK_DESCRIPTION.getValue();
			
			switch  (rightAction)
			{
				case COMMAND:
				{
					String command = Message.EDITOR_HAT_COMMAND_DESCRIPTION.getValue().replace("{1}", hat.getRightClickArgument());
					description.add(rightActionDesc.replace("{1}", command));
				}
					break;
					
				case OPEN_MENU_PERMISSION:
				case OPEN_MENU:
				{
					String menu = Message.EDITOR_HAT_MENU_DESCRIPTION.getValue().replace("{1}", hat.getRightClickArgument());
					description.add(rightActionDesc.replace("{1}", menu));
				}
					break;
					
				case DEMO:
				{
					String formattedTime = StringUtil.getTimeFormat(hat.getDemoDuration() / 20);
					String durationDesc = Message.EDITOR_HAT_DURATION_DESCRIPTION.getValue().replace("{1}", formattedTime);
					
					description.add(rightActionDesc.replace("{1}", durationDesc));
				}
					break;
					
				default:
					description.add(rightActionDesc.replace("{1}", rightAction.getStrippedName()));
			}
		}
		
		description.add("");
		description.addAll(StringUtil.parseDescription(Message.EDITOR_HAT_FOOTER_DESCRIPTION.getValue()));
		
		ItemUtil.setItemDescription(item, StringUtil.colorize(description));
	}
	
	public static void updateActiveHatDescription (ItemStack item, Hat hat)
	{
		String description = Message.ACTIVE_PARTICLES_HAT_DESCRIPTION.getValue();
		
		String[] activeInfo = StringUtil.parseValue(description, "1");
		String[] hiddenInfo = StringUtil.parseValue(description, "2");
		
		String active = !hat.isHidden() ? activeInfo[1] : "";
		String hidden = hat.isHidden() ? hiddenInfo[1] : "";
		
		String s = description
				.replace(activeInfo[0], active)
				.replace(hiddenInfo[0], hidden);
		
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	/**
	 * Applies a description to an ItemStack using potion data
	 * @param item
	 * @param pe
	 */
	public static void updatePotionDescription (ItemStack item, PotionEffect pe)
	{
		String description = Message.EDITOR_MAIN_MENU_POTION_DESCRIPTION.getValue();
		String[] typeInfo = StringUtil.parseValue(description, "1");
		
		String name = pe != null ? StringUtil.capitalizeFirstLetter(pe.getType().getName().toLowerCase()) : typeInfo[1];
		String numeral = pe != null ? StringUtil.toRomanNumeral(pe.getAmplifier() + 1) : "";
		
		String s = description
				.replace(typeInfo[0], name)
				.replace("{2}", numeral);
		
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	/**
	 * Applies a description to an ItemStack using potion data
	 * @param item
	 * @param pe
	 */
	public static void updatePotionStrengthDescription (ItemStack item, PotionEffect pe)
	{
		String description = Message.EDITOR_POTION_MENU_STRENGTH_DESCRIPTION.getValue();
		String[] strengthInfo = StringUtil.parseValue(description, "1");
		
		String numerals = pe != null ? StringUtil.toRomanNumeral(pe.getAmplifier() + 1) : strengthInfo[1];
		
		String s = description.replace(strengthInfo[0], numerals);
		ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));
	}
	
	/**
	 * Returns a menu's title trimmed to find an inventories character limit
	 * @param title
	 * @param message
	 * @return
	 */
	public static String getTrimmedMenuTitle (String title, Message message)
	{
		final int charLimit = 28;
		String menuTitle = message.getValue();
		String[] extraInfo = StringUtil.parseValue(menuTitle, "1");
		
		int messageLength = menuTitle.length() - extraInfo[0].length();
		int totalLength = messageLength + title.length();
		
		if (totalLength > charLimit) 
		{
			int difference = totalLength - charLimit;
			title = title.substring(0, title.length() - difference) + extraInfo[1];
		}
		
		return ChatColor.translateAlternateColorCodes('&', menuTitle.replace(extraInfo[0], title));
	}
}
