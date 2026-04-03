package com.mediusecho.particlehats.compatibility.pre_1_21;

import com.mediusecho.particlehats.compatibility.CompatibleFactory;
import com.mediusecho.particlehats.compatibility.CompatibleSound;
import org.bukkit.Sound;

import java.util.Arrays;

public class CompatibleFactoryImpl implements CompatibleFactory {

    @Override
    public CompatibleSound getCompatibleSound (String name) {
        return new CompatibleSoundImpl(name);
    }

    @Override
    public String[] getSounds () {
        return Arrays.stream(Sound.values()).map(Enum::toString).toArray(String[]::new);
    }

    @Override
    public CompatibleSound getVillagerNoSound() {
        return new CompatibleSoundImpl("VILLAGER_NO");
    }
}
