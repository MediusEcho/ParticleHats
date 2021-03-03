package com.mediusecho.particlehats.util;

import com.mediusecho.particlehats.ParticleHats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class PlayerUtil {

    /**
     * Closes the players inventory on the next tick.
     *
     * @param player
     *      The player whose inventory will be closed.
     */
    public static void closeInventory (@NotNull Player player) {
        runNextTick(player::closeInventory);
    }

    /**
     * Opens this inventory for the player on the next tick.
     *
     * @param player
     *      The player opening this inventory.
     * @param inventory
     *      The inventory to open.
     */
    public static void openInventory (Player player, Inventory inventory) {
        runNextTick(() -> player.openInventory(inventory));
    }

    /**
     * Runs a task on the next tick.
     *
     * @param runnable
     *      The task to run.
     */
    public static void runNextTick (Runnable runnable) {
        Bukkit.getScheduler().runTask(ParticleHats.instance, runnable);
    }

}
