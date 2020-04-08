package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.editor.purchase.PurchaseMenuManager;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.properties.MenuInventory;

public class EditCommand extends Command {

	private final ParticleHats core;
	
	public EditCommand (final ParticleHats core)
	{
		this.core = core;
	}
	
	@Override
	public List<String> tabComplete (ParticleHats core, Sender sender, String label, ArrayList<String> args)
	{
		if (args.size() == 1) 
		{
			List<String> menus = new ArrayList<String>(core.getDatabase().getMenus(false).keySet());
			List<String> result = new ArrayList<String>();
			
			menus.add("purchase");
			
			for (String menu : menus)
			{
				if (hasPermission(sender, menu)) {
					result.add(menu);
				}
			}
			
			return result;
		}
		return Arrays.asList("");
	}

	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{		
		if (args.size() < 1) 
		{
			sender.sendMessage(Message.COMMAND_ERROR_ARGUMENTS);
			sender.sendMessage(Message.COMMAND_EDIT_USAGE);
			return false;
		}
			
		PlayerState playerState = core.getPlayerState(sender.getPlayer());	
		Database database = core.getDatabase();
		
		if (playerState.hasEditorOpen()) 
		{
			sender.sendMessage(Message.COMMAND_ERROR_ALREADY_EDITING);
			return false;
		}
		
		String menuName = (args.get(0).contains(".") ? args.get(0).split("\\.")[0] : args.get(0));	
		
		if (menuName.equalsIgnoreCase("purchase"))
		{
			MenuInventory inventory = database.getPurchaseMenu(playerState);
			
			if (inventory != null)
			{
				PurchaseMenuManager purchaseManager = core.getMenuManagerFactory().getPurchaseMenuManager(playerState);
				purchaseManager.setEditingMenu(inventory);
				purchaseManager.open();
			}
			
			return false;
		}
		
		if (!database.menuExists(menuName))
		{
			sender.sendMessage(Message.COMMAND_ERROR_UNKNOWN_MENU.replace("{1}", menuName));
			return false;
		}
		
		MenuInventory inventory = database.loadInventory(menuName, playerState);
		
		if (inventory == null) {
			return false;
		}
		
		EditorMenuManager editorManager = core.getMenuManagerFactory().getEditorMenuManager(playerState);
		editorManager.setEditingMenu(inventory);
		editorManager.open();
		
		return true;
	}

	@Override
	public String getName() {
		return "edit menu";
	}
	
	@Override
	public String getArgumentName () {
		return "edit";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_EDIT_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_EDIT_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_EDIT;
	}
	
	@Override
	public boolean hasWildcardPermission () {
		return true;
	}
	
	@Override
	public Permission getWildcardPermission () {
		return Permission.COMMAND_EDIT_ALL;
	}
	
	@Override
	public boolean showInHelp() {
		return true;
	}
	
	@Override
	public boolean isPlayerOnly() {
		return true;
	}
	
	@Override
	public boolean hasPermission (Sender sender)
	{
		if (!sender.isPlayer()) {
			return true;
		}
		
		// /h wild card check
		if (Permission.COMMAND_ALL.hasPermission(sender)) {
			return true;
		}
		
		// Specific command wild card check
		if (hasWildcardPermission())
		{
			if (getWildcardPermission().hasPermission(sender)) {
				return true;
			}
		}
		
		// Check for individual menu permissions
		List<String> menus = new ArrayList<String>(core.getDatabase().getMenus(false).keySet());
		menus.add("purchase");
		
		for (String menu : menus)
		{
			if (sender.hasPermission(getPermission().append(menu))) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean hasPermission (Sender sender, String arg)
	{		
		if (!sender.isPlayer()) {
			return true;
		}
		
		// /h wild card check
		if (Permission.COMMAND_ALL.hasPermission(sender)) {
			return true;
		}
		
		// Specific command wild card check
		if (hasWildcardPermission())
		{
			if (getWildcardPermission().hasPermission(sender)) {
				return true;
			}
		}
		
		if (arg != null && !arg.equals(""))
		{
			if (sender.hasPermission(getPermission().append(arg))) {
				return true;
			}
		}
		
		return false;
	}
}
