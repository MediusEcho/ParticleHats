package com.mediusecho.particlehats.editor.menus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.ui.GuiState;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorSoundMenu extends EditorMenu {

	private Map<Integer, Inventory> menus;
	private Map<Integer, Inventory> blockMenus;
	private Map<Integer, Inventory> entityMenus;
	
	private List<Sound> miscSounds;
	private List<Sound> blockSounds;
	private List<Sound> entitySounds;
	
	private int currentMiscPage;
	private int currentBlockPage;
	private int currentEntityPage;
	
	private int currentFilter = 0;
	private Sound currentPlayingSound;
	private final Hat targetHat;
	
	private final ItemStack volumeItem;
	private final ItemStack pitchItem;
	
	private final EditorAction setSoundAction;
	
	// Disable sounds that are too long
	private final List<String> blacklist = Arrays.asList(
			"MUSIC_CREATIVE",
			"MUSIC_CREDITS",
			"MUSIC_DRAGON",
			"MUSIC_END",
			"MUSIC_GAME",
			"MUSIC_MENU",
			"MUSIC_NETHER",
			"MUSIC_UNDER_WATER",
			"AMBIENT_CAVE",
			"AMBIENT_UNDERWATER_ENTER",
			"AMBIENT_UNDERWATER_EXIT",
			"AMBIENT_UNDERWATER_LOOP",
			"AMBIENT_UNDERWATER_LOOP_ADDITIONS",
			"AMBIENT_UNDERWATER_LOOP_ADDITIONS_RARE",
			"AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE",
			"ITEM_ELYTRA_FLYING",
			"ENTITY_ENDER_DRAGON_DEATH",
			"MUSIC_DISC_11",
			"MUSIC_DISC_13",
			"MUSIC_DISC_BLOCKS",
			"MUSIC_DISC_CAT",
			"MUSIC_DISC_CHIRP",
			"MUSIC_DISC_FAR",
			"MUSIC_DISC_MALL",
			"MUSIC_DISC_MELLOHI",
			"MUSIC_DISC_STAL",
			"MUSIC_DISC_STRAD",
			"MUSIC_DISC_WAIT",
			"MUSIC_DISC_WARD",
		    "RECORD_11",
		    "RECORD_13",
		    "RECORD_BLOCKS",
		    "RECORD_CAT",
		    "RECORD_CHIRP",
		    "RECORD_FAR",
		    "RECORD_MALL",
		    "RECORD_MELLOHI",
		    "RECORD_STAL",
		    "RECORD_STRAD",
		    "RECORD_WAIT",
		    "RECORD_WARD");
	
	public EditorSoundMenu(ParticleHats core, Player owner, MenuBuilder menuBuilder, EditorSoundCallback soundCallback)
	{
		super(core, owner, menuBuilder);
		this.targetHat = menuBuilder.getBaseHat();
		
		menus = new HashMap<Integer, Inventory>();
		blockMenus = new HashMap<Integer, Inventory>();
		entityMenus = new HashMap<Integer, Inventory>();
		
		volumeItem = ItemUtil.createItem(Material.LEVER, Message.EDITOR_SOUND_MENU_SET_VOLUME);
		EditorLore.updateDoubleDescription(volumeItem, targetHat.getSoundVolume(), Message.EDITOR_SOUND_MENU_PITCH_DESCRIPTION);
		
		pitchItem = ItemUtil.createItem(Material.LEVER, Message.EDITOR_SOUND_MENU_SET_PITCH);
		EditorLore.updateDoubleDescription(pitchItem, targetHat.getSoundPitch(), Message.EDITOR_SOUND_MENU_PITCH_DESCRIPTION);
		
		setSoundAction = (event, slot) ->
		{
			final List<Sound> sounds = getCurrentFilterSounds();
			final int currentPage = getCurrentFilterPage();
			
			int index = slot + (currentPage * 45);
			Sound sound = sounds.get(index);
			if (sound != null)
			{
				if (event.isLeftClick()) {
					soundCallback.onSelect(sound);
				}
				
				else if (event.isRightClick())
				{
					stopSound();
					
					currentPlayingSound = sound;
					owner.playSound(owner.getLocation(), sound, (float) targetHat.getSoundVolume(), (float) targetHat.getSoundPitch());
					return EditorClickType.NONE;
				}
			}
			
			return EditorClickType.NEUTRAL;
		};

		build();
	}
	
	@Override
	public void onClose (boolean forced)
	{
		stopSound();
	}
	
	@Override
	public void open () {
		openMenu(menus, currentMiscPage);
	}
	
	public void openMenu (Map<Integer, Inventory> menus, int currentPage)
	{
		if (menus.containsKey(currentPage))
		{
			Inventory inv = menus.get(currentPage);
			
			menuBuilder.setOwnerState(GuiState.SWITCHING_EDITOR);
			//menuBuilder.setOwnerState(MenuState.SWITCHING);
			inv.setItem(52, volumeItem);
			inv.setItem(53, pitchItem);
			owner.openInventory(inv);
		}
	}

	@Override
	protected void build() 
	{		
		blockSounds  = new ArrayList<Sound>();
		entitySounds = new ArrayList<Sound>();
		miscSounds   = new ArrayList<Sound>();
		
		boolean useBlacklist = !SettingsManager.EDITOR_SHOW_BLACKLISTED_SOUNDS.getBoolean();
		
		for (Sound s : Sound.values())
		{
			if (useBlacklist && blacklist.contains(s.toString())) {
				continue;
			}
			
			String category = s.toString().split("_")[0];
			switch (category)
			{
			case "BLOCK":
				blockSounds.add(s);
				break;
			case "ENTITY":
				entitySounds.add(s);
				break;
			default:
				miscSounds.add(s);
				break;
			}
		}
		
		final int blockPages  = (int) Math.ceil((double) blockSounds.size() / 45D);
		final int entityPages = (int) Math.ceil((double) entitySounds.size() / 45D);
		final int miscPages   = (int) Math.ceil((double) miscSounds.size() / 45D);
		
		// Create our filter pages
		generateSoundMenu(menus, miscPages, Message.EDITOR_SOUND_MENU_MISC_TITLE, 0);
		generateSoundMenu(blockMenus, blockPages, Message.EDITOR_SOUND_MENU_BLOCK_TITLE, 1);
		generateSoundMenu(entityMenus, entityPages, Message.EDITOR_SOUND_MENU_ENTITY_TITLE, 2);
		
		// Fill our menus with sound
		populateSoundMenu(miscSounds, menus, CompatibleMaterial.MUSIC_DISC_CAT);
		populateSoundMenu(blockSounds, blockMenus, CompatibleMaterial.MUSIC_DISC_BLOCKS);
		populateSoundMenu(entitySounds, entityMenus, CompatibleMaterial.MUSIC_DISC_FAR);
		
		// Setup buttons
		setAction(49, (event, slot) ->
		{
			menuBuilder.goBack();
			return EditorClickType.NEUTRAL;
		});
		
		// Misc Filter
		setAction(45, (event, slot) ->
		{
			currentFilter = 0;
			openMenu(menus, currentMiscPage);
			return EditorClickType.NEUTRAL;
		});
		
		// Block Filter
		setAction(46, (event, slot) ->
		{
			currentFilter = 1;
			openMenu(blockMenus, currentBlockPage);
			return EditorClickType.NEUTRAL;
		});
		
		// Entity Filter
		setAction(47, (event, slot) ->
		{
			currentFilter = 2;
			openMenu(entityMenus, currentEntityPage);
			return EditorClickType.NEUTRAL;
		});
		
		// Previous Page
		setAction(48, (event, slot) ->
		{
			final Map<Integer, Inventory> menus = getCurrentFilter();
			int currentPage = getCurrentFilterPage();
			currentPage -= 1;
			setCurrentFilterPage(currentPage);
			openMenu(menus, currentPage);
			return EditorClickType.NEUTRAL;
		});
		
		// Next Page
		setAction(50, (event, slot) ->
		{
			final Map<Integer, Inventory> menus = getCurrentFilter();
			int currentPage = getCurrentFilterPage();
			currentPage += 1;
			setCurrentFilterPage(currentPage);
			openMenu(menus, currentPage);
			return EditorClickType.NEUTRAL;
		});
		
		// Fill in our main inventory
		for (int i = 0; i < 45; i++) {
			setAction(i, setSoundAction);
		}
		
		// Volume
		setAction(52, (event, slot) ->
		{
			final double increment = (event.isLeftClick() ? 0.1 : -0.1) * (event.isShiftClick() ? 10 : 1);
			double volume = MathUtil.round(MathUtil.clamp(targetHat.getSoundVolume() + increment, 0, 2), 2);
			
			targetHat.setSoundVolume(volume);
			EditorLore.updateDoubleDescription(volumeItem, volume, Message.EDITOR_SOUND_MENU_VOLUME_DESCRIPTION);
			getOpenMenu().setItem(52, volumeItem);
			return EditorClickType.NEUTRAL;
		});
		
		// Pitch
		setAction(53, (event, slot) ->
		{
			final double increment = (event.isLeftClick() ? 0.1 : -0.1) * (event.isShiftClick() ? 10 : 1);
			double pitch = MathUtil.round(MathUtil.clamp(targetHat.getSoundPitch() + increment, 0, 2), 2);
			
			targetHat.setSoundPitch(pitch);
			EditorLore.updateDoubleDescription(pitchItem, pitch, Message.EDITOR_SOUND_MENU_PITCH_DESCRIPTION);
			getOpenMenu().setItem(53, pitchItem);
			return EditorClickType.NEUTRAL;
		});
	}

	private void generateSoundMenu (Map<Integer, Inventory> menus, int pages, Message startingTitle, int categoryIndex)
	{
		for (int i = 0; i < pages; i++)
		{
			String menuTitle = startingTitle.getValue()
					.replace("{1}", Integer.toString(i + 1)).replace("{2}", Integer.toString(pages));
			Inventory menu = Bukkit.createInventory(null, 54, menuTitle);
			
			// Filters
			menu.setItem(45, ItemUtil.createItem(Material.BOWL, Message.EDITOR_SOUND_MENU_MISC_FILTER));
			menu.setItem(46, ItemUtil.createItem(Material.BOWL, Message.EDITOR_SOUND_MENU_BLOCK_FILTER));
			menu.setItem(47, ItemUtil.createItem(Material.BOWL, Message.EDITOR_SOUND_MENU_ENTITY_FILTER));
			
			// Controls
			menu.setItem(49, backButton);
			
			switch (categoryIndex)
			{
			case 0: ItemUtil.setItemType(menu.getItem(45), CompatibleMaterial.MUSHROOM_STEW); break;
			case 1: ItemUtil.setItemType(menu.getItem(46), CompatibleMaterial.MUSHROOM_STEW); break;
			case 2: ItemUtil.setItemType(menu.getItem(47), CompatibleMaterial.MUSHROOM_STEW); break;
			}
			
			// Next Page
			if ((i + 1) < pages) {
				menu.setItem(50, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_NEXT_PAGE));
			}
			
			// Previous Page
			if ((i + 1) > 1) {
				menu.setItem(48, ItemUtil.createItem(CompatibleMaterial.LIME_DYE, Message.EDITOR_MISC_PREVIOUS_PAGE));
			}
			
			menus.put(i, menu);
		}
	}
	
	private void populateSoundMenu (List<Sound> sounds, Map<Integer, Inventory> menus, CompatibleMaterial disc)
	{
		int index = 0;
		int page = 0;
		
		for (Sound s : sounds)
		{
			String songName = StringUtil.capitalizeFirstLetter(s.toString().toLowerCase());
			String title = Message.EDITOR_SOUND_MENU_SOUND_PREFIX.getValue().replace("{1}", songName);
			ItemStack item = ItemUtil.createItem(disc, title);
			
			Sound targetHatSound = targetHat.getSound();
			EditorLore.updateSoundDescription(item, s, targetHatSound, Message.EDITOR_SOUND_MENU_SOUND_DESCRIPTION);
			
			if (targetHatSound != null && targetHatSound.equals(s)) {
				ItemUtil.highlightItem(item);
			}
					
			menus.get(page).setItem(index, item);
			index++;
			if (index % 45 == 0)
			{
				index = 0;
				page++;
			}
		}
	}
	
	private Inventory getOpenMenu ()
	{
		switch (currentFilter)
		{
		default: return menus.get(currentMiscPage);
		case 1: return blockMenus.get(currentBlockPage);
		case 2: return entityMenus.get(currentEntityPage);
		}
	}
	
	private Map<Integer, Inventory> getCurrentFilter ()
	{
		switch (currentFilter)
		{
		default: return menus;
		case 1: return blockMenus;
		case 2: return entityMenus;
		}
	}
	
	private int getCurrentFilterPage ()
	{
		switch (currentFilter)
		{
		default: return currentMiscPage;
		case 1: return currentBlockPage;
		case 2: return currentEntityPage;
		}
	}
	
	private List<Sound> getCurrentFilterSounds ()
	{
		switch (currentFilter)
		{
		default: return miscSounds;
		case 1: return blockSounds;
		case 2: return entitySounds;
		}
	}
	
	private void setCurrentFilterPage (int page)
	{
		switch (currentFilter)
		{
		case 0: currentMiscPage = page; break;
		case 1: currentBlockPage = page; break;
		case 2: currentEntityPage = page; break;
		}
	}
	
	private void stopSound ()
	{
		if (currentPlayingSound != null) 
		{
			try {
				owner.stopSound(currentPlayingSound);
			} catch (NoSuchMethodError e) {}
		}
	}
}
