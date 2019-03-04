package com.mediusecho.particlehats.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mediusecho.particlehats.locale.Message;

import net.md_5.bungee.api.ChatColor;

/**
 * Represents both a CommandSender and Player
 * @author MediusEcho
 *
 */
public class Sender {

	private final CommandSender commandSender;
	private final boolean isConsoleSender;
	
	private final Player playerSender;
	private final UUID playerID;
	
	public Sender (CommandSender sender)
	{
		boolean isPlayer = sender instanceof Player;
		
		playerSender = isPlayer ? (Player)sender : null;
		playerID = isPlayer ? playerSender.getUniqueId() : null;
		
		commandSender = sender;
		isConsoleSender = !isPlayer;
	}
	
	/**
	 * Returns true if this Sender is a Player
	 * @return
	 */
	public boolean isPlayer () {
		return !isConsoleSender;
	}
	
	/**
	 * Returns the Player executing this command
	 * @return
	 */
	public Player getPlayer () {
		return playerSender;
	}
	
	/**
	 * Returns the UUID of the Player executing this command
	 * @return
	 */
	public UUID getPlayerID () {
		return playerID;
	}
	
	/**
	 * Returns true if this Sender has permission
	 * @param permission
	 * @return
	 */
	public boolean hasPermission (String permission) {
		return isConsoleSender ? true : playerSender.hasPermission(permission);
	}
	
	/**
	 * Returns true if this Sender has permission
	 * @param permission
	 * @return
	 */
	public boolean hasPermission (CommandPermission permission) {
		return isConsoleSender ? true : playerSender.hasPermission(permission.value);
	}
	
	/**
	 * Sends this Sender a message
	 * @param message
	 */
	public void sendMessage (String message)
	{
		message = ChatColor.translateAlternateColorCodes('&', message);
		if (isConsoleSender) {
			commandSender.sendMessage(message);
		} else {
			playerSender.sendMessage(message);
		}
	}
	
	/**
	 * Sends this Sender a message
	 * @param message
	 */
	public void sendMessage (Message message) {
		sendMessage(message.getValue());
	}
	
	/**
	 * Sends this Sender a message
	 * @param message
	 */
	public void sendMessage (List<String> message)
	{
		for (String m : message) {
			sendMessage(m);
		}
	}
}
