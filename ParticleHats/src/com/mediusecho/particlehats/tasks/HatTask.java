package com.mediusecho.particlehats.tasks;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.ParticleTag;
import com.mediusecho.particlehats.particles.properties.ParticleType;
import com.mediusecho.particlehats.player.EntityState;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.util.PlayerUtil;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Callable;

public class HatTask extends BukkitRunnable {

    private final ParticleHats plugin;
    private final Entity entity;
    private final Hat hat;

    private List<String> disabledWorlds;
    private boolean checkWorldPermission;
    private boolean essentialsVanishFlag;

    private int ticks = 0;

    public HatTask (ParticleHats plugin, Entity entity, @NotNull Hat hat)
    {
        this.plugin = plugin;
        this.entity = entity;
        this.hat = hat;

        disabledWorlds = SettingsManager.DISABLED_WORLDS.getList();
        checkWorldPermission = SettingsManager.CHECK_WORLD_PERMISSION.getBoolean();
        essentialsVanishFlag = SettingsManager.FLAG_ESSENTIALS_VANISH.getBoolean();

        runTaskTimerAsynchronously(plugin, 0, 1L);
        ParticleHats.log("Starting asynchronous particle task");
    }

    public Hat getHat () {
        return hat;
    }

    public void reload ()
    {
        disabledWorlds = SettingsManager.DISABLED_WORLDS.getList();
        checkWorldPermission = SettingsManager.CHECK_WORLD_PERMISSION.getBoolean();
        essentialsVanishFlag = SettingsManager.FLAG_ESSENTIALS_VANISH.getBoolean();
    }

    @Override
    public void run()
    {
        // Skip this world if it is disabled
        World world = entity.getWorld();
        if (disabledWorlds.contains(world.getName())) {
            return;
        }

        EntityState entityState = plugin.getEntityState(entity);

        // Handle player checks
        if (entityState instanceof PlayerState)
        {
            PlayerState playerState = (PlayerState)entityState;
            Player player = playerState.getOwner();

            // Make sure the player has permission for this world
            if (checkWorldPermission && !player.hasPermission("particlehats.world." + world.getName())) {
                return;
            }

            // Skip if the player has a potion of invisibility
            if (essentialsVanishFlag && player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                return;
            }
        }

        if (hat.isVanished() || hat.isHidden()) {
            return;
        }

        if (!hat.isPermanent())
        {
            if (hat.onTick())
            {
                // Stop the task here and unequip
                entityState.removeHat(hat);
            }
        }

        ticks++;
        checkHat(entityState, hat, true);
    }

    public void stop ()
    {
        try {
            cancel();
            ParticleHats.log("Stopping asynchronous particle task");
        } catch (Exception ignored) {}
    }

    private void checkHat (@NotNull EntityState entityState, @NotNull Hat hat, boolean checkNode)
    {
        PlayerState.AFKState afkState = entityState.getAFKState();
        PlayerState.PVPState pvpState = entityState.getPVPState();

        switch (hat.getMode())
        {
            case ACTIVE:
                displayHat(entity, hat);
                break;

            case WHEN_MOVING:
                if (afkState == PlayerState.AFKState.ACTIVE) {
                    displayHat(entity, hat);
                }
                break;

            case WHEN_AFK:
                if (afkState == PlayerState.AFKState.AFK) {
                    displayHat(entity, hat);
                }
                break;

            case WHEN_PEACEFUL:
                if (pvpState == PlayerState.PVPState.PEACEFUL) {
                    displayHat(entity, hat);
                }
                break;

            case WHEN_GLIDING:
                if (entity instanceof Player)
                {
                    Player player = (Player)entity;
                    if (player.isGliding()) {
                        displayHat(entity, hat);
                    }
                }
                break;

            case WHEN_SPRINTING:
                if (entity instanceof Player)
                {
                    Player player = (Player)entity;
                    if (player.isSprinting()) {
                        displayHat(entity, hat);
                    }
                }
                break;

            case WHEN_SWIMMING:
                if (entity instanceof Player)
                {
                    Player player = (Player)entity;
                    if (player.isSwimming()) {
                        displayHat(entity, hat);
                    }
                }
                break;

            case WHEN_FLYING:
                if (entity instanceof Player)
                {
                    Player player = (Player)entity;
                    if (player.isFlying()) {
                        displayHat(entity, hat);
                    }
                }
                break;
        }

        // Loop through and check each node hat
        if (checkNode)
        {
            for (Hat node : hat.getNodes()) {
                checkHat(entityState, node, false);
            }
        }
    }

    private void displayHat (Entity entity, @NotNull Hat hat)
    {
        if (hat.getType() == ParticleType.NONE) {
            return;
        }

        if (!handleTags(entity, hat)) {
            return;
        }

        hat.displayType(ticks, entity);
    }

    private boolean handleTags (Entity entity, @NotNull Hat hat)
    {
        List<ParticleTag> tags = hat.getTags();

        if (tags.contains(ParticleTag.ARROWS))
        {
            for (Entity e : PlayerUtil.getNearbyEntitiesAsync(entity, 50, 50, 50))
            {
                if (e instanceof Arrow)
                {
                    Arrow arrow = (Arrow)e;
                    if (!arrow.isOnGround())
                    {
                        if (arrow.getShooter() instanceof Player)
                        {
                            Player player = (Player)arrow.getShooter();
                            if (player.equals(entity)) {
                                hat.displayType(ticks, arrow);
                            }
                        }
                    }
                }
            }
            return false;
        }

        if (tags.contains(ParticleTag.PICTURE_MODE) && entity instanceof Player)
        {
            displayToNearestEntity((Player)entity, hat, ArmorStand.class);
            return false;
        }

        return true;
    }

    private void displayToNearestEntity (@NotNull Player player, Hat hat, Class<?> entity)
    {
        Entity nearest = null;
        double maxDistance = 1000;

        for (Entity e : PlayerUtil.getNearbyEntitiesAsync(player, 50, 10, 50))
        {
            if (entity.isInstance(e))
            {
                double distance = e.getLocation().distanceSquared(player.getLocation());
                if (distance < maxDistance)
                {
                    maxDistance = distance;
                    nearest = e;
                }
            }
        }

        if (nearest != null) {
            hat.displayType(ticks, nearest);
        }
    }
}
