package com.mediusecho.particlehats.editor.menus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.properties.ParticleModes;
import com.mediusecho.particlehats.ui.AbstractListMenu;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;

public class EditorModeMenu extends AbstractListMenu {

	private final MenuAction selectAction;
	private final List<ParticleModes> activeModes;
	
	private Map<Integer, ParticleModes> modes;
	
	public EditorModeMenu(ParticleHats core, EditorMenuManager menuManager, Player owner, boolean isEditingWhitelist, MenuObjectCallback callback) 
	{
		super(core, menuManager, owner, false);
		
		this.totalPages = MathUtil.calculatePageCount(ParticleModes.values().length, 27);
		this.modes = new HashMap<Integer, ParticleModes>();
		this.activeModes = isEditingWhitelist ? menuManager.getTargetHat().getWhitelistedModes() : menuManager.getTargetHat().getBlacklistedModes();
		
		this.selectAction = (event, slot) ->
		{
			int index = getClampedIndex(slot, 10, 2) + (currentPage * 45);
			if (modes.containsKey(index)) {
				callback.onSelect(modes.get(index));
			}
			return MenuClickResult.NEUTRAL;
		};
		
		build();
	}

	@Override
	public void insertEmptyItem() {}

	@Override
	public void removeEmptyItem() {}

	@Override
	protected void build() 
	{
		setAction(49, backButtonAction);
		
		for (int i = 0; i < 27; i++) {
			setAction(getNormalIndex(i, 10, 2), selectAction);
		}
		
		for (int i = 0; i < totalPages; i++)
		{
			Inventory inventory = Bukkit.createInventory(null, 54, Message.EDITOR_MODE_MENU_TITLE.getValue());
			
			inventory.setItem(49, backButtonItem);
			
			if ((i + 1) < totalPages) {
				inventory.setItem(50, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
			}
			
			if ((i + 1) > 1) {
				inventory.setItem(48, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_PREVIOUS_PAGE));
			}
			
			setMenu(i, inventory);
		}
		
		
		int index = 0;
		int page = 0;
		int globalIndex = 0;
		
		for (ParticleModes pm : ParticleModes.values())
		{
			if (!pm.isSupported()) {
				continue;
			}
			
			ItemStack item = ItemUtil.createItem(pm.getMode().getMenuItem(), pm.getDisplayName());
			
			boolean isSelected = activeModes.contains(pm);
			if (isSelected) {
				ItemUtil.highlightItem(item);
			}
			
			EditorLore.updateModeItemDescription(item, pm, isSelected);
			
			menus.get(page).setItem(getNormalIndex(index++, 10, 2), item);
			modes.put(globalIndex++, pm);
			
			if (index % 45 == 0) 
			{
				page++;
				index = 0;
			}
		}
	}

	@Override
	public void onClose(boolean forced) {}

	@Override
	public void onTick(int ticks) {}

}
