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
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.ui.menus.SingularMenu;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorSoundMenu extends SingularMenu {

	private final Hat targetHat;
	private final MenuObjectCallback callback;
	private final ItemStack volumeItem;
	private final ItemStack pitchItem;
	
	private final int miscPages;
	private final int blockPages;
	private final int entityPages;
	
	private Map<Integer, Inventory> miscMenus;
	private Map<Integer, Inventory> blockMenus;
	private Map<Integer, Inventory> entityMenus;
	
	private List<Sound> miscSounds;
	private List<Sound> blockSounds;
	private List<Sound> entitySounds;
	
	private int currentMiscPage = 0;
	private int currentBlockPage = 0;
	private int currentEntityPage = 0;
	
	private Sound currentPlayingSound;
	
	private SoundFilter currentFilter = SoundFilter.MISC;
	
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
	
	public EditorSoundMenu(ParticleHats core, EditorMenuManager menuManager, Player owner, MenuObjectCallback callback) 
	{
		super(core, menuManager, owner);
		
		this.targetHat = menuManager.getBaseHat();
		this.callback = callback;
		this.miscMenus = new HashMap<Integer, Inventory>();
		this.blockMenus = new HashMap<Integer, Inventory>();
		this.entityMenus = new HashMap<Integer, Inventory>();
		this.miscSounds = new ArrayList<Sound>();
		this.blockSounds = new ArrayList<Sound>();
		this.entitySounds = new ArrayList<Sound>();
		
		this.volumeItem = ItemUtil.createItem(Material.LEVER, Message.EDITOR_SOUND_MENU_SET_VOLUME);
		EditorLore.updateDoubleDescription(volumeItem, targetHat.getSoundVolume(), Message.EDITOR_SOUND_MENU_PITCH_DESCRIPTION);
		
		this.pitchItem = ItemUtil.createItem(Material.LEVER, Message.EDITOR_SOUND_MENU_SET_PITCH);
		EditorLore.updateDoubleDescription(pitchItem, targetHat.getSoundPitch(), Message.EDITOR_SOUND_MENU_PITCH_DESCRIPTION);
		
		boolean useBlacklist = SettingsManager.EDITOR_SHOW_BLACKLISTED_SOUNDS.getBoolean();
		for (Sound sound : Sound.values())
		{
			if (!useBlacklist && blacklist.contains(sound.toString())) {
				continue;
			}
			
			String category = sound.toString().split("_")[0];
			switch (category)
			{
			case "BLOCK":
				blockSounds.add(sound);
				break;
			case "ENTITY":
				entitySounds.add(sound);
				break;
			default:
				miscSounds.add(sound);
			}
		}
		
		this.miscPages = MathUtil.calculatePageCount(miscSounds.size(), 45);
		this.blockPages = MathUtil.calculatePageCount(blockSounds.size(), 45);
		this.entityPages = MathUtil.calculatePageCount(entitySounds.size(), 45);
		
		build();
	}
	
	@Override
	public void open () {
		openMenu(miscMenus, currentMiscPage);
	}
	
	public void openMenu (Map<Integer, Inventory> menus, int currentPage)
	{
		if (menus.containsKey(currentPage))
		{
			Inventory menu = menus.get(currentPage);
			
			menu.setItem(52, volumeItem);
			menu.setItem(53, pitchItem);
			
			menuManager.isOpeningMenu(this);
			owner.openInventory(menu);
		}
	}

	@Override
	protected void build() 
	{
		// Create our filter pages
		generateSoundMenu(miscMenus, miscPages, Message.EDITOR_SOUND_MENU_MISC_TITLE, 0);
		generateSoundMenu(blockMenus, blockPages, Message.EDITOR_SOUND_MENU_BLOCK_TITLE, 1);
		generateSoundMenu(entityMenus, entityPages, Message.EDITOR_SOUND_MENU_ENTITY_TITLE, 2);
		
		// Fill our menus sound
		int blocksSize = blockSounds.size();
		int entitiesSize = entitySounds.size();
		
		populateSoundMenu(miscSounds, miscMenus, CompatibleMaterial.MUSIC_DISC_CAT, blocksSize, entitiesSize);
		populateSoundMenu(blockSounds, blockMenus, CompatibleMaterial.MUSIC_DISC_BLOCKS, blocksSize, entitiesSize);
		populateSoundMenu(entitySounds, entityMenus, CompatibleMaterial.MUSIC_DISC_FAR, blocksSize, entitiesSize);
		
		setAction(49, backButtonAction);
		
		// Misc Filter
		setAction(45, (event, slot) ->
		{
			currentFilter = SoundFilter.MISC;
			openMenu(miscMenus, currentMiscPage);
			return MenuClickResult.NEUTRAL;
		});
		
		// Block Filter
		setAction(46, (event, slot) ->
		{
			currentFilter = SoundFilter.BLOCKS;
			openMenu(blockMenus, currentBlockPage);
			return MenuClickResult.NEUTRAL;
		});
		
		// Entity Filter
		setAction(47, (event, slot) ->
		{
			currentFilter = SoundFilter.ENTITIES;
			openMenu(entityMenus, currentEntityPage);
			return MenuClickResult.NEUTRAL;
		});
		
		// Previous Page
		setAction(48, (event, slot) ->
		{
			final Map<Integer, Inventory> menus = getCurrentFilter();
			int currentPage = getCurrentFilterPage();
			currentPage -= 1;
			setCurrentFilterPage(currentPage);
			openMenu(menus, currentPage);
			return MenuClickResult.NEUTRAL;
		});
		
		// Next Page
		setAction(50, (event, slot) ->
		{
			final Map<Integer, Inventory> menus = getCurrentFilter();
			int currentPage = getCurrentFilterPage();
			currentPage += 1;
			setCurrentFilterPage(currentPage);
			openMenu(menus, currentPage);
			return MenuClickResult.NEUTRAL;
		});
		
		// Volume
		setAction(52, (event, slot) ->
		{
			final double increment = (event.isLeftClick() ? 0.1 : -0.1) * (event.isShiftClick() ? 10 : 1);
			double volume = MathUtil.round(MathUtil.clamp(targetHat.getSoundVolume() + increment, 0, 2), 2);
			
			targetHat.setSoundVolume(volume);
			EditorLore.updateDoubleDescription(volumeItem, volume, Message.EDITOR_SOUND_MENU_VOLUME_DESCRIPTION);
			getOpenMenu().setItem(52, volumeItem);
			return MenuClickResult.NEUTRAL;
		});
		
		// Pitch
		setAction(53, (event, slot) ->
		{
			final double increment = (event.isLeftClick() ? 0.1 : -0.1) * (event.isShiftClick() ? 10 : 1);
			double pitch = MathUtil.round(MathUtil.clamp(targetHat.getSoundPitch() + increment, 0, 2), 2);
			
			targetHat.setSoundPitch(pitch);
			EditorLore.updateDoubleDescription(pitchItem, pitch, Message.EDITOR_SOUND_MENU_PITCH_DESCRIPTION);
			getOpenMenu().setItem(53, pitchItem);
			return MenuClickResult.NEUTRAL;
		});
		
		MenuAction setSoundAction = (event, slot) ->
		{
			final List<Sound> sounds = getCurrentFilterSounds();
			final int currentPage = getCurrentFilterPage();
			
			int index = slot + (currentPage * 45);
			Sound sound = sounds.get(index);
			if (sound != null)
			{
				if (event.isLeftClick()) {
					callback.onSelect(sound);
				}
				
				else if (event.isRightClick())
				{
					stopSound();
					
					currentPlayingSound = sound;
					owner.playSound(owner.getLocation(), sound, (float) targetHat.getSoundVolume(), (float) targetHat.getSoundPitch());
					return MenuClickResult.NONE;
				}
			}
			
			return MenuClickResult.NEUTRAL;
		};
		
		for (int i = 0; i < 45; i++) {
			setAction(i, setSoundAction);
		}
	}

	@Override
	public void onClose(boolean forced) 
	{
		stopSound();
	}

	@Override
	public void onTick(int ticks) {}
	
	@Override
	public boolean hasInventory (Inventory inventory) 
	{
		switch (currentFilter)
		{
		default:
			return miscMenus.containsValue(inventory);
			
		case BLOCKS:
			return blockMenus.containsValue(inventory);
			
		case ENTITIES:
			return entityMenus.containsValue(inventory);
		}
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
			menu.setItem(49, backButtonItem);
			
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
	
	private void populateSoundMenu (List<Sound> sounds, Map<Integer, Inventory> menus, CompatibleMaterial disc, int blockSoundSize, int entitySoundSize)
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
			
			Inventory menu = menus.get(page);
			menu.setItem(index, item);
					
			if (blockSoundSize == 0) {
				menu.setItem(46, null);
			}
			
			if (entitySoundSize == 0) {
				menu.setItem(47, null);
			}
			
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
		default:
			return miscMenus.get(currentMiscPage);
			
		case BLOCKS:
			return blockMenus.get(currentBlockPage);
			
		case ENTITIES:
			return entityMenus.get(currentEntityPage);
		}
	}
	
	private Map<Integer, Inventory> getCurrentFilter ()
	{
		switch (currentFilter)
		{
		default:
			return miscMenus;
			
		case BLOCKS:
			return blockMenus;
			
		case ENTITIES:
			return entityMenus;
		}
	}
	
	private int getCurrentFilterPage ()
	{
		switch (currentFilter)
		{
		default:
			return currentMiscPage;
			
		case BLOCKS:
			return currentBlockPage;
			
		case ENTITIES:
			return currentEntityPage;
		}
	}
	
	private List<Sound> getCurrentFilterSounds ()
	{
		switch (currentFilter)
		{
		default:
			return miscSounds;
			
		case BLOCKS:
			return blockSounds;
			
		case ENTITIES:
			return entitySounds;
		}
	}
	
	private void setCurrentFilterPage (int page)
	{
		switch (currentFilter)
		{
		case MISC:
			currentMiscPage = page;
			break;
			
		case BLOCKS:
			currentBlockPage = page;
			break;
			
		case ENTITIES:
			currentEntityPage = page;
			break;
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
	
	private enum SoundFilter {
		
		MISC,
		BLOCKS,
		ENTITIES;
		
	}

}
