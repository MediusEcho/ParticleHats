package com.mediusecho.particlehats.editor.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.editor.menus.EditorOffsetMenu.VectorAxis;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.ui.AbstractStaticMenu;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorAngleMenu extends AbstractStaticMenu {

	private final EditorMenuManager editorManager;
	private final MenuCallback callback;
	
	public EditorAngleMenu(ParticleHats core, EditorMenuManager menuManager, Player owner, MenuCallback callback) 
	{
		super(core, menuManager, owner);
		
		this.editorManager = menuManager;
		this.callback = callback;
		this.inventory = Bukkit.createInventory(null, 27, Message.EDITOR_ANGLE_MENU_TITLE.getValue());
		
		build();
	}

	@Override
	public void build ()
	{
		Hat targetHat = editorManager.getTargetHat();
		
		// X Angle
		ItemStack xItem = ItemUtil.createItem(CompatibleMaterial.REPEATER, Message.EDITOR_ANGLE_MENU_SET_ANGLE_X);
		EditorLore.updateVectorDescription(xItem, targetHat.getAngle(), Message.EDITOR_ANGLE_MENU_ANGLE_X_DESCRIPTION);
		setButton(14, xItem, (event, slot) ->
		{
			return updateAngle(event, targetHat, VectorAxis.X);
		});
		
		// Y Angle
		ItemStack yItem = ItemUtil.createItem(CompatibleMaterial.REPEATER, Message.EDITOR_ANGLE_MENU_SET_ANGLE_Y);
		EditorLore.updateVectorDescription(yItem, targetHat.getAngle(), Message.EDITOR_ANGLE_MENU_ANGLE_Y_DESCRIPTION);
		setButton(15, yItem, (event, slot) ->
		{
			return updateAngle(event, targetHat, VectorAxis.Y);
		});
		
		// Z Angle
		ItemStack zItem = ItemUtil.createItem(CompatibleMaterial.REPEATER, Message.EDITOR_ANGLE_MENU_SET_ANGLE_Z);
		EditorLore.updateVectorDescription(zItem, targetHat.getAngle(), Message.EDITOR_ANGLE_MENU_ANGLE_Z_DESCRIPTION);
		setButton(16, zItem, (event, slot) ->
		{
			return updateAngle(event, targetHat, VectorAxis.Z);
		});
		
		// Back
		setButton(10, backButtonItem, backButtonAction);
	}
	
	@Override
	public void onClose(boolean forced) 
	{
		if (!forced) {
			callback.onCallback();
		}
	}

	@Override
	public void onTick(int ticks) {}
	
	private MenuClickResult updateAngle (MenuClickEvent event, Hat hat, VectorAxis axis)
	{
		final double normalClick    = event.isLeftClick() ? 1f : -1f;
		final double shiftClick     = event.isShiftClick() ? 10 : 1;
		final double modifier       = normalClick * shiftClick;
		final boolean isMiddleClick = event.isMiddleClick();
		
		Vector angle = hat.getAngle();
		switch (axis)
		{
		case X:
			double xa = !isMiddleClick ? angle.getX() + modifier : 0;
			hat.setAngleX(xa);
			break;
		case Y:
			double ya = !isMiddleClick ? angle.getY() + modifier : 0;
			hat.setAngleY(ya);
			break;
		case Z:
			double za = !isMiddleClick ? angle.getZ() + modifier : 0;
			hat.setAngleZ(za);
			break;
		}
		
		EditorLore.updateVectorDescription(getItem(14), angle, Message.EDITOR_ANGLE_MENU_ANGLE_X_DESCRIPTION);
		EditorLore.updateVectorDescription(getItem(15), angle, Message.EDITOR_ANGLE_MENU_ANGLE_Y_DESCRIPTION);
		EditorLore.updateVectorDescription(getItem(16), angle, Message.EDITOR_ANGLE_MENU_ANGLE_Z_DESCRIPTION);
		
		if (event.isMiddleClick()) {
			return MenuClickResult.NEUTRAL;
		}
		
		else {
			return event.isLeftClick() ? MenuClickResult.POSITIVE : MenuClickResult.NEGATIVE;
		}
	}
}
