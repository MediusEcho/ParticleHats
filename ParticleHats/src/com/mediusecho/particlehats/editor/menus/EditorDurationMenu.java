package com.mediusecho.particlehats.editor.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.ui.menus.SingularMenu;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorDurationMenu extends SingularMenu {

	private final Hat targetHat;
	private final MenuCallback callback;
	
	public EditorDurationMenu(ParticleHats core, EditorMenuManager menuManager, Player owner, MenuCallback callback) 
	{
		super(core, menuManager, owner);
		
		this.targetHat = menuManager.getBaseHat();
		this.callback = callback;
		this.inventory = Bukkit.createInventory(null, 27, Message.EDITOR_DURATION_MENU_TITLE.getValue());
		
		build();
	}

	@Override
	protected void build()
	{
		setButton(12, backButtonItem, backButtonAction);
		
		ItemStack durationItem = ItemUtil.createItem(Material.MAP, Message.EDITOR_DURATION_MENU_SET_DURATION.getValue());
		EditorLore.updateDurationDescription(durationItem, targetHat.getDemoDuration(), Message.EDITOR_DURATION_MENU_DESCRIPTION);
		setButton(14, durationItem, (event, slot) ->
		{
			int normalClick    = event.isLeftClick() ? 20 : -20;
			int shiftClick     = event.isShiftClick() ? 30 : 1;
			int modifier       = normalClick * shiftClick;
			
			int duration = targetHat.getDemoDuration() + modifier;
			targetHat.setDemoDuration(duration);
			
			EditorLore.updateDurationDescription(getItem(14), targetHat.getDemoDuration(), Message.EDITOR_DURATION_MENU_DESCRIPTION);
			return event.isLeftClick() ? MenuClickResult.POSITIVE : MenuClickResult.NEGATIVE;
		});
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

}
