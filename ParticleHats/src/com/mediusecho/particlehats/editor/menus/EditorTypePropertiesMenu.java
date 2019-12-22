package com.mediusecho.particlehats.editor.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.ui.AbstractStaticMenu;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorTypePropertiesMenu extends AbstractStaticMenu {

	private final MenuCallback callback;
	private final Hat targetHat;
	
	public EditorTypePropertiesMenu(ParticleHats core, EditorMenuManager menuManager, Player owner, MenuCallback callback) 
	{
		super(core, menuManager, owner);
		
		this.callback = callback;
		this.targetHat = menuManager.getTargetHat();
		this.inventory = Bukkit.createInventory(null, 36, "Type Properties");
		
		build();
	}

	@Override
	protected void build() 
	{
		setButton(31, backButtonItem, backButtonAction);
		
		setButton(10, ItemUtil.createItem(Material.LIME_DYE, "Disable Animations"), (event, slot) ->
		{
			return MenuClickResult.NEUTRAL;
		});
		
		setButton(11, ItemUtil.createItem(Material.LIME_DYE, "Change Animation Direction"), (event, slot) ->
		{
			targetHat.setAnimationDirection(targetHat.getAnimationDirection() * -1);
			ParticleHats.debug(targetHat.getAnimationDirection());
			return MenuClickResult.NEUTRAL;
		});
	}

	@Override
	public void onClose(boolean forced) 
	{
		callback.onCallback();
	}

	@Override
	public void onTick(int ticks) {}

}
