package com.mediusecho.particlehats.managers;

import org.bukkit.plugin.PluginManager;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.hooks.CurrencyHook;
import com.mediusecho.particlehats.hooks.VanishHook;
import com.mediusecho.particlehats.hooks.economy.PlayerPointsHook;
import com.mediusecho.particlehats.hooks.economy.VaultHook;
import com.mediusecho.particlehats.hooks.vanish.SuperVanishHook;
import com.mediusecho.particlehats.hooks.vanish.VanishNoPacketHook;

public class HookManager {

	private final ParticleHats core;
	
	private CurrencyHook currencyHook;
	private VanishHook vanishHook;
	
	public HookManager (final ParticleHats core)
	{
		this.core = core;
		loadHooks();
	}
	
	public void onReload ()
	{
		loadHooks();
	}
	
	/**
	 * Get this plugin's CurrencyHook
	 * @return
	 */
	public CurrencyHook getCurrencyHook () {
		return currencyHook;
	}
	
	/**
	 * Get this plugin's VanishHook
	 * @return
	 */
	public VanishHook getVanishHook () {
		return vanishHook;
	}
	
	private void loadHooks ()
	{
		PluginManager pluginManager = core.getServer().getPluginManager();
		
		// Vault Hook
		if (SettingsManager.FLAG_VAULT.getBoolean())
		{
			if (currencyHook != null && currencyHook instanceof VaultHook) {
				return;
			}
			
			if (pluginManager.isPluginEnabled("Vault"))
			{
				currencyHook = new VaultHook(core);
				ParticleHats.log("hooking into Vault");
			}
			
			else 
			{
				ParticleHats.log("Could not find Vault, disabling economy support");
				SettingsManager.FLAG_VAULT.addOverride(false);
				currencyHook = null;
			}
		}
		
		// PlayerPoints Hook
		else if (SettingsManager.FLAG_PLAYERPOINTS.getBoolean())
		{
			if (currencyHook != null && currencyHook instanceof PlayerPointsHook) {
				return;
			}
			
			if (pluginManager.isPluginEnabled("PlayerPoints"))
			{
				currencyHook = new PlayerPointsHook();
				ParticleHats.log("hooking into PlayerPoints");
			}
			
			else
			{
				ParticleHats.log("Could not find PlayerPoints, disabling economy support");
				SettingsManager.FLAG_PLAYERPOINTS.addOverride(false);
				currencyHook = null;
			}
		}
		
		// Vanish Hooks
		if (vanishHook == null && SettingsManager.FLAG_VANISH.getBoolean())
		{
			// SuperVanish
			if (pluginManager.isPluginEnabled("SuperVanish"))
			{
				vanishHook = new SuperVanishHook(core);
				ParticleHats.log("hooking into SuperVanish");
			}
			
			// PremiumVanish
			else if (pluginManager.isPluginEnabled("PremiumVanish"))
			{
				vanishHook = new SuperVanishHook(core);
				ParticleHats.log("hooking into PremiumVanish");
			}
			
			// VanishNoPacket
			else if (pluginManager.isPluginEnabled("VanishNoPacket"))
			{
				vanishHook = new VanishNoPacketHook(core);
				ParticleHats.log("hooking into VanishNoPacket");
			}
			
			else
			{
				ParticleHats.log("Unable to find a supported vanish plugin, disabling vanish support");
				SettingsManager.FLAG_VANISH.addOverride(false);
			}
		}
		
		else
		{
			if (vanishHook != null && !SettingsManager.FLAG_VANISH.getBoolean())
			{
				vanishHook.unregister();
				vanishHook = null;
			}
		}
	}
}
