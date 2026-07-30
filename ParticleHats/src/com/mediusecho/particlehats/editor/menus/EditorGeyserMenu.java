package com.mediusecho.particlehats.editor.menus;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.ParticleData;
import com.mediusecho.particlehats.ui.AbstractStaticMenu;
import com.mediusecho.particlehats.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EditorGeyserMenu extends AbstractStaticMenu {

    protected final EditorMenuManager editorManager;
    protected final MenuCallback callback;
    protected final int particleIndex;

    public EditorGeyserMenu (ParticleHats core, EditorMenuManager menuManager, Player owner, int particleIndex, MenuCallback callback) {
        super(core, menuManager, owner);

        this.editorManager = menuManager;
        this.particleIndex = particleIndex;
        this.callback = callback;
        this.inventory = Bukkit.createInventory(null, 45, Message.EDITOR_GEYSER_MENU_TITLE.getValue());

        build();
    }

    @Override
    protected void build () {
        Hat targetHat = editorManager.getTargetHat();
        ParticleData data = targetHat.getParticleData(particleIndex);

        // Back Button
        setButton(40, backButtonItem, backButtonAction);

        // Water Blocks
        ItemStack blocksItem = ItemUtil.createItem(CompatibleMaterial.REPEATER, Message.EDITOR_GEYSER_MENU_SET_WATER_BLOCKS);
        EditorLore.updateIntegerDescription(blocksItem, data.getGeyserWaterBlocks(), Message.EDITOR_GEYSER_MENU_WATER_BLOCKS_DESCRIPTION);
        setButton(21, blocksItem, (event, slot) -> {
            final int increment = event.isLeftClick() ? 1 : -1;
            final int amount = data.getGeyserWaterBlocks() + increment;

            data.setGeyserWaterBlocks(!event.isMiddleClick() ? amount : 3);
            EditorLore.updateIntegerDescription(getItem(21), data.getGeyserWaterBlocks(), Message.EDITOR_GEYSER_MENU_WATER_BLOCKS_DESCRIPTION);
            return event.isLeftClick() ? MenuClickResult.POSITIVE : MenuClickResult.NEGATIVE;
        });

        // Impulse.hasPropertyChanges())
        ItemStack impulseItem = ItemUtil.createItem(CompatibleMaterial.COMPARATOR, Message.EDITOR_GEYSER_MENU_SET_IMPULSE);
        EditorLore.updateDoubleDescription(impulseItem, data.getGeyserBurstImpulse(), Message.EDITOR_GEYSER_MENU_IMPULSE_DESCRIPTION);
        setButton(23, impulseItem, (event, slot) -> {
            final double normalClick = event.isLeftClick() ? 0.1f : -0.1f;
            final double shiftClick = event.isShiftClick() ? 10 : 1;
            final double modifier = normalClick * shiftClick;
            final double amount = data.getGeyserBurstImpulse() + modifier;

            data.setGeyserBurstImpulse(!event.isMiddleClick() ? (float) amount : 1.0f);
            EditorLore.updateDoubleDescription(getItem(23), data.getGeyserBurstImpulse(), Message.EDITOR_GEYSER_MENU_IMPULSE_DESCRIPTION);
            return event.isLeftClick() ? MenuClickResult.POSITIVE : MenuClickResult.NEGATIVE;
        });
    }

    @Override
    public void onClose (boolean forced) {
        if (!forced) {
            callback.onCallback();
        }
    }

    @Override
    public void onTick (int ticks) {}
}
