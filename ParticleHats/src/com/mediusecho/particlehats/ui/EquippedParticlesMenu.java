package com.mediusecho.particlehats.ui;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.menus.ListMenu;
import com.mediusecho.particlehats.ui.menus.Menu;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.ui.properties.MenuContentRegion;

public class EquippedParticlesMenu extends ListMenu {

	final boolean fromMenu;
	final PlayerState playerState;
	final MenuAction hatAction;
	
	private final ItemStack emptyItem = ItemUtil.createItem(CompatibleMaterial.BARRIER, Message.ACTIVE_PARTICLES_EMPTY);
	
	public EquippedParticlesMenu(ParticleHats core, MenuManager menuManager, Player owner, boolean fromMenu) 
	{
		super(core, menuManager, owner, new MenuContentRegion(10, 43));
		
		this.fromMenu = fromMenu;
		this.playerState = core.getPlayerState(owner);
		this.setInventory(0, Bukkit.createInventory(null, 54, Message.ACTIVE_PARTICLES_MENU_TITLE.getValue()));
		
		hatAction = (event, slot) -> 
		{	
			int index = (currentPage * contentRegion.getTotalSlots()) + contentRegion.getClampedIndex(slot);
			
			//int index = getClampedIndex(slot, 10, 2);
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
				
				ItemStack item = getInventory(currentPage).getItem(slot);//menus.get(currentPage).getItem(slot);
				EditorLore.updateActiveHatDescription(item, hat);
				
				if (hat.isHidden()) 
				{
					ItemUtil.stripHighlight(item);
					hat.unequip(owner);
				} 
				
				else 
				{
					ItemUtil.highlightItem(item);
					hat.equip(owner);
				}
			}
			
			else if (event.isShiftRightClick()) 
			{
				playerState.removeHat(index);
				deleteItem(currentPage, slot);
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
	public void build() 
	{		
		setAction(49, (event, slot) ->
		{
			if (fromMenu)
			{
				StaticMenuManager staticManager = core.getMenuManagerFactory().getStaticMenuManager(playerState);
				Menu previousMenu = staticManager.getPreviousOpenMenu();
				
				if (previousMenu == null) {
					return MenuClickResult.NONE;
				}
				
				previousMenu.open();
			}
			
			else {
				owner.closeInventory();
			}
			return MenuClickResult.NEUTRAL;
		});
		
		setAction(50, (event, slot) ->
		{
			currentPage++;
			open();
			return MenuClickResult.NEUTRAL;
		});
		
		setAction(48, (event, slot) ->
		{
			currentPage--;
			open();
			return MenuClickResult.NEUTRAL;
		});
		
		for (int i = 0; i < contentRegion.getTotalSlots(); i++) {
			setAction(contentRegion.getNextSlot(i), hatAction);
		}
		
		Message backButtonTitle = fromMenu ? Message.EDITOR_MISC_GO_BACK : Message.EDITOR_MISC_CLOSE;
		int totalPages = MathUtil.calculatePageCount(playerState.getHatCount(), contentRegion.getTotalSlots());
		
		for (int i = 0; i < totalPages; i++)
		{
			Inventory inventory = Bukkit.createInventory(null, 54, Message.ACTIVE_PARTICLES_MENU_TITLE.getValue());
			
			inventory.setItem(49, ItemUtil.createItem(Material.NETHER_STAR, backButtonTitle));
			
			if ((i + 1) < totalPages) {
				inventory.setItem(50, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
			}
			
			if ((i + 1) > 1) {
				inventory.setItem(48, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_PREVIOUS_PAGE));
			}
			
			setInventory(i, inventory);
		}
		
		List<Hat> equippedHats = playerState.getActiveHats();
		if (equippedHats.size() == 0) 
		{
			setEmpty(true);
			return;
		}
		
		for (int i = 0; i < equippedHats.size(); i++)
		{
			Hat hat = equippedHats.get(i);
			ItemStack item = hat.getItem();
			
			EditorLore.updateActiveHatDescription(item, hat);
			
			if (hat.isHidden()) {
				ItemUtil.stripHighlight(item);
			} else {
				ItemUtil.highlightItem(item);
			}
			
			int page = contentRegion.getPage(i);
			int slot = contentRegion.getNextSlot(i);
			
			setItem(page, slot, item);
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
	public void deleteItem(int page, int slot)
	{
		super.deleteItem(page, slot);
		
		if (playerState.getActiveHats().size() == 0) {
			setEmpty(true);
		}
	}
}
