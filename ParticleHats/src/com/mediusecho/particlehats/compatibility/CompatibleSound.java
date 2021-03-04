package com.mediusecho.particlehats.compatibility;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public enum CompatibleSound {

    ENTITY_VILLAGER_NO ("VILLAGER_NO");

    private final Sound sound;

    CompatibleSound (String... aliases)
    {
        sound = getCompatibleSound(toString(), aliases);
    }

    public void play (Player player, float volume, float pitch)
    {
        if (sound == null) {
            return;
        }
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    @Nullable
    private Sound getCompatibleSound (String name, String[] aliases)
    {
        Sound sound = getSound(name);
        if (sound != null) {
            return sound;
        }

        for (String alias : aliases)
        {
            Sound soundAlias = getSound(alias);
            if (alias != null) {
                return soundAlias;
            }
        }

        return null;
    }

    @Nullable
    private Sound getSound (String name)
    {
        try {
            return Sound.valueOf(name);
        } catch (Exception ignored) {
            return null;
        }
    }

}
