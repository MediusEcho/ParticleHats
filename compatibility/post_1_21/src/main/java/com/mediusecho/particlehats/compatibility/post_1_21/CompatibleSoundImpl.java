package com.mediusecho.particlehats.compatibility.post_1_21;

import com.mediusecho.particlehats.compatibility.CompatibleSound;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Locale;

public class CompatibleSoundImpl implements CompatibleSound {

    private Sound sound;

    public CompatibleSoundImpl(String soundName) {
        NamespacedKey key = NamespacedKey.fromString(soundName.toLowerCase(Locale.ROOT));
        if (key != null) {
            Registry<Sound> registry = Bukkit.getRegistry(Sound.class);
            if (registry != null) {
                sound = registry.get(key);
            }
        }

        // Try to fall back to deprecated call
        if (sound == null) {
            sound = Sound.valueOf(soundName);
        }
    }

    @Override
    public void play (Player player, float volume, float pitch) {
        if (sound != null) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    @Override
    public void stop (Player player) {
        if (sound != null) {
            player.stopSound(sound);
        }
    }

    @Override
    public String toString () {
        if (sound != null) {
            return sound.toString();
        }
        return super.toString();
    }
}