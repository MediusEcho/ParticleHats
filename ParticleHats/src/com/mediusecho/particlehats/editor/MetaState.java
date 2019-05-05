package com.mediusecho.particlehats.editor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.util.StringUtil;

public enum MetaState {

	NONE,
	HAT_NAME,
	HAT_LABEL,
	HAT_COMMAND,
	HAT_DESCRIPTION,
	HAT_PERMISSION,
	HAT_PERMISSION_DESCRIPTION,
	HAT_PERMISSION_MESSAGE,
	HAT_EQUIP_MESSAGE,
	HAT_TAG,
	MENU_TITLE,
	MENU_ALIAS,
	NEW_MENU;
	
	private final Core core = Core.instance;
	
	/**
	 * Get the name of this MetaState
	 * @return The name of this mode as defined in the current messages.yml file
	 */
	public String getUsage () 
	{
		final String key = "META_" + toString() + "_USAGE";
		try {
			return Message.valueOf(key).getValue();
		} catch (IllegalArgumentException e) {
			return "";
		}
	}
	
	/**
	 * Get the description of this MetaState
	 * @return The description of this mode as defined in the current messages.yml file
	 */
	public String getDescription ()
	{
		final String key = "META_" + toString() + "_DESCRIPTION";
		try {
			return Message.valueOf(key).getValue();
		} catch (IllegalArgumentException e) {
			return "";
		}
	}
	
	/**
	 * Get the suggested value for this MetaState
	 * @return
	 */
	public String getSuggestion ()
	{
		final String key = "META_" + toString() + "_SUGGESTION";
		try {
			return Message.valueOf(key).getValue();
		} catch (IllegalArgumentException e) {
			return "";
		}
	}
	
	public void onMetaSet (MenuBuilder menuBuilder, Player player, List<String> args)
	{
		// Stick all arguments together into one string
		StringBuilder sb = new StringBuilder();
		for (String s : args) {
			sb.append(' ').append(s);
		} 
		sb.deleteCharAt(0);
		
		String value = sb.toString();
		String rawString = ChatColor.stripColor(value);
		
		Hat targetHat = menuBuilder.getBaseHat();
		
		/**
		 * Solution for adding empty spaces:
		 * Find any %_% value and replace all _'s inside with " "
		 */
		Pattern pattern = StringUtil.getPattern("%(.*?)%");
		Matcher matcher = pattern.matcher(sb.toString());
		while (matcher.find()) {
			value = value.replace(matcher.group(0), matcher.group(1).replace("_", " "));
		}
		
		switch (this)
		{
			default: break;
			case MENU_TITLE:
			{
				String title = value.length() <= 40 ? value : value.substring(0, 40);
				menuBuilder.getEditingMenu().setTitle(title);
				reopenEditor(menuBuilder);
			}
			break;
			
			case MENU_ALIAS:
			{
				menuBuilder.getEditingMenu().setAlias(ChatColor.stripColor(args.get(0)));
				reopenEditor(menuBuilder);
			}
			break;
			
			case NEW_MENU:
			{
				String menuName = (rawString.contains(".") ? rawString.split("\\.")[0] : rawString).replaceAll(" ", "_");
				Database database = core.getDatabase();
				
				
				if (database.menuExists(menuName)) 
				{
					player.sendMessage(Message.COMMAND_ERROR_MENU_EXISTS.getValue().replace("{1}", menuName));
					return;
				}
				
				database.createMenu(menuName);
				reopenEditor(menuBuilder);
			}
			break;
			
			case HAT_NAME:
			{
				targetHat.setName(value);
				menuBuilder.onHatNameChange();
				reopenEditor(menuBuilder);
			}
			break;
			
			case HAT_PERMISSION:
			{
				String permission = rawString.replace(" ", "_");
				targetHat.setPermission(permission.replace("particlehats.particle.", ""));
				reopenEditor(menuBuilder);
			}
			break;
			
			case HAT_LABEL:
			{
				String label = rawString.replace(" ", "_");
				targetHat.setLabel(label);
				reopenEditor(menuBuilder);
			}
			break;
			
			case HAT_EQUIP_MESSAGE:
			{
				targetHat.setEquipMessage(value);
				reopenEditor(menuBuilder);
			}
			break;
			
			case HAT_PERMISSION_MESSAGE:
			{
				targetHat.setPermissionDeniedMessage(value);
				reopenEditor(menuBuilder);
			}
			break;
			
			case HAT_DESCRIPTION:
			case HAT_PERMISSION_DESCRIPTION:
			{
				int line = menuBuilder.getOwnerState().getMetaDescriptionLine();
				List<String> description = this == HAT_DESCRIPTION ? targetHat.getDescription() : targetHat.getPermissionDescription();
				
				if (line < description.size()) {
					description.set(line, value);
				}
				
				reopenEditor(menuBuilder);
			}
			break;
			
			case HAT_COMMAND:
			{
				String command = rawString;
				if (rawString.charAt(0) == '/') {
					command = rawString.substring(1);
				}
				
				targetHat.setArgument(command);
				reopenEditor(menuBuilder);
			}
			break;
		}
	}
	
	public void reopenEditor (MenuBuilder menuBuilder)
	{
		if (menuBuilder != null)
		{
			menuBuilder.setOwnerState(MetaState.NONE);
			menuBuilder.openCurrentMenu();
		}
	}
}
