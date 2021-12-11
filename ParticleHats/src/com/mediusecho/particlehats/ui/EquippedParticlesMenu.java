package com.mediusecho.particlehats.ui;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EquippedParticlesMenu extends AbstractListMenu {

	final boolean fromMenu;
	final PlayerState playerState;
	final MenuAction hatAction;
	
	private final ItemStack emptyItem = ItemUtil.createItem(CompatibleMaterial.BARRIER, Message.ACTIVE_PARTICLES_EMPTY);
	
	public EquippedParticlesMenu(ParticleHats core, MenuManager menuManager, Player owner, boolean fromMenu) 
	{
		super(core, menuManager, owner, true);
		
		this.fromMenu = fromMenu;
		this.playerState = core.getPlayerState(owner);
		this.totalPages = 1;
		this.setMenu(0, Bukkit.createInventory(null, 54, Message.ACTIVE_PARTICLES_MENU_TITLE.getValue()));
		
		hatAction = (event, slot) -> 
		{	
			int index = getClampedIndex(slot, 10, 2);
			if (index >= playerState.getHatCount()) {
				return MenuClickResult.NONE;
			}
			
			Hat hat = playerState.getActiveHats().get(index);
			if (hat == null) {
				return MenuClickResult.NONE;
			}
			
			if (event.isLeftClick())
			{
				hat.setHidden(!hat.isHidden());
				
				ItemStack item = menus.get(currentPage).getItem(slot);
				EditorLore.updateActiveHatDescription(item, hat);
				
				if (hat.isHidden()) 
				{
					ItemUtil.stripHighlight(item);
					hat.unequip(owner);
				} 
				
				else {
					ItemUtil.highlightItem(item);
				}
			}
			
			else if (event.isShiftRightClick()) 
			{
				playerState.removeHat(index);
				deleteSlot(currentPage, slot);

				// Temporary solution until 5.0
				// Removes the equipped item's tint & description from the menu.
				if (fromMenu)
				{
					StaticMenuManager staticManager = (StaticMenuManager)playerState.getMenuManager();
					if (staticManager == null) {
						return MenuClickResult.NEUTRAL;
					}

					AbstractMenu menu = staticManager.getMenuFromCache(hat.getMenu());
					if (menu instanceof StaticMenu) {
						((StaticMenu)menu).unequipHat(hat);
					}
				}
			}
			
			return MenuClickResult.NEUTRAL;
		};
		
		build();
	}
	
	@Override
	public void insertEmptyItem () {
		setButton(0, 22, emptyItem, emptyAction);
	}
	
	@Override
	public void removeEmptyItem () {
		setButton(0, 22, null, hatAction);
	}

	@Override
	protected void build() 
	{				
		for (int i = 0; i < 28; i++) {
			setAction(getNormalIndex(i, 10, 2), hatAction);
		}
		
		if (fromMenu)
		{
			setButton(0, 49, ItemUtil.createItem(Material.NETHER_STAR, Message.EDITOR_MISC_GO_BACK), (event, slot) -> 
			{
				StaticMenuManager staticManager = (StaticMenuManager)playerState.getMenuManager();
				if (staticManager == null) {
					return MenuClickResult.NONE;
				}

				AbstractMenu previousMenu = staticManager.getPreviousOpenMenu();
				if (previousMenu == null) {
					return MenuClickResult.NONE;
				}

				previousMenu.open();
				return MenuClickResult.NEUTRAL;
			});
		}
		
		else
		{
			setButton(0, 49, ItemUtil.createItem(Material.NETHER_STAR, Message.EDITOR_MISC_CLOSE), (event, slot) ->
			{
				menuManager.closeInventory();
				return MenuClickResult.NEUTRAL;
			});
		}
		
		List<Hat> equippedHats = playerState.getActiveHats();
		if (equippedHats.size() == 0) 
		{
			setEmpty(true);
			return;
		}
		
		int index = 0;
		for (Hat hat : equippedHats)
		{
			ItemStack item = hat.getItem();
			EditorLore.updateActiveHatDescription(item, hat);
			
			if (!hat.isHidden()) {
				ItemUtil.highlightItem(item);
			} else {
				ItemUtil.stripHighlight(item);
			}
			
			setItem(0, getNormalIndex(index++, 10, 2), item);
		}
	}

	@Override
	public void onClose(boolean forced) {}

	@Override
	public void onTick(int ticks) {}

	@Override
	public String getName () {
		return "EquippedParticles";
	}
	
	@Override
	public void deleteSlot(int page, int slot)
	{
		super.deleteSlot(page, slot);
		
		if (playerState.getActiveHats().size() == 0) {
			setEmpty(true);
		}
	}
}
