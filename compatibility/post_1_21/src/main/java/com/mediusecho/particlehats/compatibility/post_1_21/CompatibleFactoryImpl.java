package com.mediusecho.particlehats.compatibility.post_1_21;

import com.mediusecho.particlehats.compatibility.CompatibleFactory;
import com.mediusecho.particlehats.compatibility.CompatibleSound;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.Sound;

public class CompatibleFactoryImpl implements CompatibleFactory {

    @Override
    public CompatibleSound getCompatibleSound (String name) {
        return new CompatibleSoundImpl(name);
    }

    @Override
    public String[] getSounds () {
        Registry<Sound> registry = Bukkit.getRegistry(Sound.class);
        if (registry != null) {
            return registry.stream().map(Object::toString).toArray(String[]::new);
        }
        return new String[0];
    }

    @Override
    public CompatibleSound getVillagerNoSound () {
        return getCompatibleSound(Sound.ENTITY_VILLAGER_NO.toString());
    }
}
