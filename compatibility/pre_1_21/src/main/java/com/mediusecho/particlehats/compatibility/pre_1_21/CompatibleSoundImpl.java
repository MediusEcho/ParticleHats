package com.mediusecho.particlehats.compatibility.pre_1_21;

import com.mediusecho.particlehats.compatibility.CompatibleSound;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class CompatibleSoundImpl implements CompatibleSound {

    private Sound sound;

    public CompatibleSoundImpl(String name) {
        sound = Sound.valueOf(name);
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
            try {
                player.stopSound(sound);
            } catch (NoSuchMethodError e) {}
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
