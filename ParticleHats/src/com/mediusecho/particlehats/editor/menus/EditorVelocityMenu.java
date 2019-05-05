package com.mediusecho.particlehats.editor.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.ItemStackData;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorVelocityMenu extends EditorOffsetMenu {

	private final int particleIndex;
	
	public EditorVelocityMenu(ParticleHats core, Player owner, MenuBuilder menuBuilder, int particleIndex, EditorGenericCallback callback) 
	{
		super(core, owner, menuBuilder, callback);
		this.particleIndex = particleIndex;
		
		inventory = Bukkit.createInventory(null, 27, Message.EDITOR_VELOCITY_MENU_TITLE.getValue());
		build();
	}

	public EditorClickType updateVelocity (EditorClickEvent event, Hat hat, VectorAxis axis)
	{
		double normalClick    = event.isLeftClick() ? 0.1f : -0.1f;
		double shiftClick     = event.isShiftClick() ? 10 : 1;
		double modifier       = normalClick * shiftClick;
		boolean isMiddleClick = event.isMiddleClick();
		
		ItemStackData data = hat.getParticleData(particleIndex).getItemStackData();
		Vector velocity = data.getVelocity();
		switch (axis)
		{
		case X:
			double vx = !isMiddleClick ? velocity.getX() + modifier : 0;
			data.setVelocityX(vx);
			break;
		case Y:
			double vy = !isMiddleClick ? velocity.getY() + modifier : 0;
			data.setVelocityY(vy);
			break;
		case Z:
			double vz = !isMiddleClick ? velocity.getZ() + modifier : 0;
			data.setVelocityZ(vz);
			break;
		}

		EditorLore.updateVectorDescription(getItem(14), velocity, Message.EDITOR_OFFSET_MENU_OFFSET_X_DESCRIPTION);
		EditorLore.updateVectorDescription(getItem(15), velocity, Message.EDITOR_OFFSET_MENU_OFFSET_Y_DESCRIPTION);
		EditorLore.updateVectorDescription(getItem(16), velocity, Message.EDITOR_OFFSET_MENU_OFFSET_Z_DESCRIPTION);
		
		if (event.isMiddleClick()) {
			return EditorClickType.NEUTRAL;
		}
		
		else {
			return event.isLeftClick() ? EditorClickType.POSITIVE : EditorClickType.NEGATIVE;
		}
	}
	
	@Override
	protected void build() 
	{
		Hat targetHat = menuBuilder.getTargetHat();
		ItemStackData itemStackData = targetHat.getParticleData(particleIndex).getItemStackData();
		Vector velocity = itemStackData.getVelocity();
		
		// X Offset
		ItemStack xItem = ItemUtil.createItem(CompatibleMaterial.REPEATER, Message.EDITOR_VELOCITY_MENU_SET_VELOCITY_X);
		EditorLore.updateVectorDescription(xItem, velocity, Message.EDITOR_OFFSET_MENU_OFFSET_X_DESCRIPTION);
		setButton(14, xItem, (event, slot) ->
		{
			return updateVelocity(event, targetHat, VectorAxis.X);
		});
		
		// Y Offset
		ItemStack yItem = ItemUtil.createItem(CompatibleMaterial.REPEATER, Message.EDITOR_VELOCITY_MENU_SET_VELOCITY_Y);
		EditorLore.updateVectorDescription(yItem, velocity, Message.EDITOR_OFFSET_MENU_OFFSET_Y_DESCRIPTION);
		setButton(15, yItem, (event, slot) ->
		{
			return updateVelocity(event, targetHat, VectorAxis.Y);
		});
		
		// Z Offset
		ItemStack zItem = ItemUtil.createItem(CompatibleMaterial.REPEATER, Message.EDITOR_VELOCITY_MENU_SET_VELOCITY_Z);
		EditorLore.updateVectorDescription(zItem, velocity, Message.EDITOR_OFFSET_MENU_OFFSET_Z_DESCRIPTION);
		setButton(16, zItem, (event, slot) ->
		{
			return updateVelocity(event, targetHat, VectorAxis.Z);
		});
		
		// Back
		setButton(10, backButton, backAction);
	}
}
