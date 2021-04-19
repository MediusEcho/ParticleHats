package com.mediusecho.particlehats.hooks.economy;

import com.mediusecho.particlehats.hooks.CurrencyHook;
import me.realized.tokenmanager.api.TokenManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.OptionalLong;

public class TokenManagerHook implements CurrencyHook {

    private final TokenManager api;

    public TokenManagerHook ()
    {
        api = (TokenManager) Bukkit.getServer().getPluginManager().getPlugin("TokenManager");
    }

    @Override
    public int getBalance(Player player)
    {
        OptionalLong balance = api.getTokens(player);
        return (int) balance.orElse(0);
    }

    @Override
    public boolean withdraw(Player player, int amount) {
        return api.removeTokens(player, amount);
    }

    @Override
    public boolean isEnabled() {
        return api != null;
    }
}
