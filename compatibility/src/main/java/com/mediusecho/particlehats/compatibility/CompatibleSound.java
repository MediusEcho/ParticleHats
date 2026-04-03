package com.mediusecho.particlehats.compatibility;

import org.bukkit.entity.Player;

public interface CompatibleSound {

    void play(Player player, float volume, float pitch);

    void stop(Player player);

    String toString();
}
