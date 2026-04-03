package com.mediusecho.particlehats.compatibility;

public interface CompatibleFactory {

    CompatibleSound getCompatibleSound(String name);

    String[] getSounds();

    CompatibleSound getVillagerNoSound();
}
