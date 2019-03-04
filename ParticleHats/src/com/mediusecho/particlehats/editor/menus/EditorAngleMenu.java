package com.mediusecho.particlehats.editor.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;

public class EditorAngleMenu extends EditorOffsetMenu {
	
	public EditorAngleMenu(Core core, Player owner, MenuBuilder menuBuilder, EditorGenericCallback callback)
	{
		super(core, owner, menuBuilder, callback);

		inventory = Bukkit.createInventory(null, 27, Message.EDITOR_ANGLE_MENU_TITLE.getValue());
		build();
	}
	
	private EditorClickType updateAngle (EditorClickEvent event, Hat hat, VectorAxis axis)
	{
		final double normalClick    = event.isLeftClick() ? 0.1f : -0.1f;
		final double shiftClick     = event.isShiftClick() ? 10 : 1;
		final double modifier       = normalClick * shiftClick;
		final boolean isMiddleClick = event.isMiddleClick();
		
		Vector angle = hat.getAngle();
		switch (axis)
		{
		case X:
			double xa = !isMiddleClick ? MathUtil.round(MathUtil.clamp(angle.getX() + modifier, -20, 20), 2) : 0;
			hat.setAngleX(xa);
			break;
		case Y:
			double ya = !isMiddleClick ? MathUtil.round(MathUtil.clamp(angle.getY() + modifier, -20, 20), 2) : 0;
			hat.setAngleY(ya);
			break;
		case Z:
			double za = !isMiddleClick ? MathUtil.round(MathUtil.clamp(angle.getZ() + modifier, -20, 20), 2) : 0;
			hat.setAngleZ(za);
			break;
		}
		
		EditorLore.updateVectorDescription(getItem(14), angle, Message.EDITOR_ANGLE_MENU_ANGLE_X_DESCRIPTION);
		EditorLore.updateVectorDescription(getItem(15), angle, Message.EDITOR_ANGLE_MENU_ANGLE_Y_DESCRIPTION);
		EditorLore.updateVectorDescription(getItem(16), angle, Message.EDITOR_ANGLE_MENU_ANGLE_Z_DESCRIPTION);
		
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
		
		// X Angle
		ItemStack xItem = ItemUtil.createItem(Material.REPEATER, Message.EDITOR_ANGLE_MENU_SET_ANGLE_X);
		EditorLore.updateVectorDescription(xItem, targetHat.getAngle(), Message.EDITOR_ANGLE_MENU_ANGLE_X_DESCRIPTION);
		setButton(14, xItem, (event, slot) ->
		{
			return updateAngle(event, targetHat, VectorAxis.X);
		});
		
		// Y Angle
		ItemStack yItem = ItemUtil.createItem(Material.REPEATER, Message.EDITOR_ANGLE_MENU_SET_ANGLE_Y);
		EditorLore.updateVectorDescription(yItem, targetHat.getAngle(), Message.EDITOR_ANGLE_MENU_ANGLE_Y_DESCRIPTION);
		setButton(15, yItem, (event, slot) ->
		{
			return updateAngle(event, targetHat, VectorAxis.Y);
		});
		
		// Z Angle
		ItemStack zItem = ItemUtil.createItem(Material.REPEATER, Message.EDITOR_ANGLE_MENU_SET_ANGLE_Z);
		EditorLore.updateVectorDescription(zItem, targetHat.getAngle(), Message.EDITOR_ANGLE_MENU_ANGLE_Z_DESCRIPTION);
		setButton(16, zItem, (event, slot) ->
		{
			return updateAngle(event, targetHat, VectorAxis.Z);
		});
		
		// Back
		setButton(10, backButton, backAction);
	}
}
