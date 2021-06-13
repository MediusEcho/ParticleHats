package com.mediusecho.particlehats.util;

import com.mediusecho.particlehats.ParticleHats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

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

    public static List<Entity> getNearbyEntitiesAsync (Entity entity, double x, double y, double z)
    {
        Future<List<Entity>> future = Bukkit.getScheduler().callSyncMethod(ParticleHats.instance, () -> entity.getNearbyEntities(x, y, z));
        try {
            return future.get();
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }

}
