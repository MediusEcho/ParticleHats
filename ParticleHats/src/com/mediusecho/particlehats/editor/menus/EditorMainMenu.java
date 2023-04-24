package com.mediusecho.particlehats.editor.menus;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.compatibility.CompatibleSound;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.database.type.DatabaseType;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.particles.properties.*;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.AbstractStaticMenu;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EditorMainMenu extends AbstractStaticMenu {

	protected int particleButtonSlot = 39;
	protected int trackingButtonSlot = 29;
	protected int countButtonSlot = 33;
	protected int equipButtonSlot = 52;
	protected int scaleItemButtonSlot = 31;
	
	protected final EditorMenuManager editorManager;
	protected final EditorBaseMenu editorBaseMenu;
	protected final Hat targetHat;
	protected final PlayerState ownerState;
	
	protected final ItemStack noParticleItem = ItemUtil.createItem(CompatibleMaterial.BARRIER, Message.EDITOR_MAIN_MENU_NO_PARTICLES, Message.EDITOR_MAIN_MENU_NO_PARTICLES_DESCRIPTION);
	protected final ItemStack singleParticleItem = ItemUtil.createItem(Material.REDSTONE, Message.EDITOR_MAIN_MENU_SET_PARTICLE);
	protected final ItemStack multipleParticlesItem = ItemUtil.createItem(Material.BUCKET, Message.EDITOR_MAIN_MENU_EDIT_PARTICLES);
	
	protected final MenuAction setParticleAction;
	protected final MenuAction editParticleAction;
	protected final MenuAction noParticle4U;
	
	public EditorMainMenu(ParticleHats core, EditorMenuManager menuManager, Player owner) 
	{
		super(core, menuManager, owner);
		
		this.editorManager = menuManager;
		this.editorBaseMenu = menuManager.getEditingMenu();
		this.targetHat = editorManager.getTargetHat();
		this.ownerState = core.getPlayerState(owner);
		this.inventory = Bukkit.createInventory(null, 54, Message.EDITOR_MAIN_MENU_TITLE.getValue());
		
		setParticleAction = (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorParticleSelectionMenu editorParticleMenu = new EditorParticleSelectionMenu(core, menuManager, owner, 0, (particle) ->
				{
					ParticleEffect pe = (ParticleEffect)particle;
					if (pe == null) {
						return;
					}
					
					targetHat.setParticle(0, pe);
					
					// Add this particle to the recents list
					core.getParticleManager().addParticleToRecents(ownerID, pe);
					
					if (targetHat.getEffect().getParticlesSupported() == 1) {
						EditorLore.updateParticleDescription(getItem(particleButtonSlot), targetHat, 0);
					}
					
					menuManager.closeCurrentMenu();
				});
				
				menuManager.addMenu(editorParticleMenu);
				editorParticleMenu.open();
			}
			
			else if (event.isRightClick()) {
				onParticleEdit(getItem(particleButtonSlot), 0);
			}
			return MenuClickResult.NEUTRAL;
		};
		
		editParticleAction = (event, slot) ->
		{
			EditorParticleMenuOverview editorParticleMenuOverview = new EditorParticleMenuOverview(core, editorManager, owner, this);
			menuManager.addMenu(editorParticleMenuOverview);
			editorParticleMenuOverview.open();
			return MenuClickResult.NEUTRAL;
		};
		
		noParticle4U = (event, slot) ->
		{
			CompatibleSound.ENTITY_VILLAGER_NO.play(owner, 1.0f, 1.0f);
			return MenuClickResult.NONE;
		};
		
		build();
	}

	@Override
	protected void build() 
	{
		buildSection();
		
		// Main Menu
		setButton(46, mainMenuButtonItem, backButtonAction);
		
		// Meta
		ItemStack metaItem = ItemUtil.createItem(CompatibleMaterial.SIGN, Message.EDITOR_MAIN_MENU_SET_META);
		EditorLore.updateGenericDescription(metaItem, Message.EDITOR_MAIN_MENU_META_DESCRIPTION);
		setButton(13, metaItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorMetaMenu editorMetaMenu = new EditorMetaMenu(core, editorManager, owner);
				menuManager.addMenu(editorMetaMenu);
				editorMetaMenu.open();
			}
			
			else if (event.isRightClick())
			{
				EditorDescriptionMenu editorDescriptionMenu = new EditorDescriptionMenu(core, editorManager, owner, true);
				menuManager.addMenu(editorDescriptionMenu);
				editorDescriptionMenu.open();
			}
			
			return MenuClickResult.NEUTRAL;
		});
		
		// Price
		ItemStack priceItem = ItemUtil.createItem(Material.GOLD_NUGGET, Message.EDITOR_MAIN_MENU_SET_PRICE);
		EditorLore.updatePriceDescription(priceItem, targetHat.getPrice(), Message.EDITOR_MAIN_MENU_PRICE_DESCRIPTION);
		setButton(15, priceItem, (event, slot) ->
		{
			final int increment = (event.isLeftClick() ? 1 : -1) * (event.isShiftClick() ? 10 : 1);
			final int price = targetHat.getPrice() + increment;
			
			targetHat.setPrice(price);
			EditorLore.updatePriceDescription(getItem(15), targetHat.getPrice(), Message.EDITOR_MAIN_MENU_PRICE_DESCRIPTION);
			return event.isLeftClick() ? MenuClickResult.POSITIVE : MenuClickResult.NEGATIVE;
		});
		
		// Sound
		ItemStack soundItem = ItemUtil.createItem(CompatibleMaterial.MUSIC_DISC_STRAD, Message.EDITOR_MAIN_MENU_SET_SOUND);
		EditorLore.updateSoundItemDescription(soundItem, targetHat);
		setButton(28, soundItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorSoundMenu editorSoundMenu = new EditorSoundMenu(core, editorManager, owner, (sound) ->
				{
					menuManager.closeCurrentMenu();
					
					if (sound == null) {
						return;
					}
					
					Sound s = (Sound)sound;
					
					targetHat.setSound(s);
					EditorLore.updateSoundItemDescription(getItem(28), targetHat);
				});
				
				menuManager.addMenu(editorSoundMenu);
				editorSoundMenu.open();
			}
			
			else if (event.isShiftRightClick())
			{
				targetHat.removeSound();
				EditorLore.updateSoundItemDescription(getItem(28), targetHat);
			}
			
			return MenuClickResult.NEUTRAL;
		});
		
		// Clone
		ItemStack cloneItem = ItemUtil.createItem(CompatibleMaterial.PRISMARINE_SHARD, Message.EDITOR_MAIN_MENU_CLONE, Message.EDITOR_MAIN_MENU_CLONE_DESCRIPTION);
		setButton(30, cloneItem, (event, slot) ->
		{
			if (targetHat.isModified())
			{
				int targetSlot = editorManager.getTargetSlot();
				core.getDatabase().saveHat(editorBaseMenu.getMenuInventory().getName(), targetSlot, targetHat);
				targetHat.clearPropertyChanges();
			}
			
			EditorSlotMenu editorSlotMenu = new EditorSlotMenu(core, editorManager, owner, editorBaseMenu, true);
			menuManager.addMenu(editorSlotMenu);
			editorSlotMenu.open();
			return MenuClickResult.NEUTRAL;
		});
		
		// Move
		ItemStack moveItem = ItemUtil.createItem(CompatibleMaterial.MAP, Message.EDITOR_MAIN_MENU_MOVE, Message.EDITOR_MAIN_MENU_MOVE_DESCRIPTION);
		setButton(32, moveItem, (event, slot) ->
		{
			EditorMenuSelectionMenu editorMenuSelectionMenu = new EditorMenuSelectionMenu(core, editorManager, owner, false, (menuName) ->
			{
				if (menuName == null) {
					return;
				}
				
				String name = (String)menuName;
				EditorTransferMenu editorTransferMenu = new EditorTransferMenu(core, editorManager, owner, name);
				menuManager.addMenu(editorTransferMenu);
				editorTransferMenu.open();
			});
			
			menuManager.addMenu(editorMenuSelectionMenu);
			editorMenuSelectionMenu.open();
			return MenuClickResult.NEUTRAL;
		});
		
		// Node
		ItemStack nodeItem = ItemUtil.createItem(Material.ANVIL, Message.EDITOR_MAIN_MENU_EDIT_NODES, Message.EDITOR_MAIN_MENU_NODE_DESCRIPTION);
		setButton(34, nodeItem, (event, slot) ->
		{
			EditorNodeMenuOverview editorNodeMenuOverview = new EditorNodeMenuOverview(core, editorManager, owner);
			menuManager.addMenu(editorNodeMenuOverview);
			editorNodeMenuOverview.open();
			return MenuClickResult.NEUTRAL;
		});
		
		// Slot
		ItemStack slotItem = ItemUtil.createItem(Material.ITEM_FRAME, Message.EDITOR_MAIN_MENU_SET_SLOT, Message.EDITOR_MAIN_MENU_SLOT_DESCRIPTION);
		setButton(38, slotItem, (event, slot) ->
		{
			EditorSlotMenu editorSlotMenu = new EditorSlotMenu(core, editorManager, owner, editorBaseMenu, false);
			menuManager.addMenu(editorSlotMenu);
			editorSlotMenu.open();
			return MenuClickResult.NEUTRAL;
		});
		
		// Icon
		ItemStack iconItem = targetHat.getItem();
		ItemUtil.setNameAndDescription(iconItem, Message.EDITOR_MAIN_MENU_SET_ICON, Message.EDITOR_MAIN_MENU_ICON_DESCRIPTION);
		setButton(41, iconItem, (event, slot) ->
		{
			EditorIconMenuOverview editorIconMenuOverview = new EditorIconMenuOverview(core, editorManager, owner, (mainItem) ->
			{
				if (mainItem == null) {
					return;
				}
				
				ItemStack item = (ItemStack)mainItem;
				ItemUtil.setItemType(getItem(41), item);
			});
			
			menuManager.addMenu(editorIconMenuOverview);
			editorIconMenuOverview.open();
			
			return MenuClickResult.NEUTRAL;
		});
		
		// Potion
		ItemStack potionItem = ItemUtil.createItem(Material.POTION, Message.EDITOR_MAIN_MENU_SET_POTION);
		EditorLore.updatePotionDescription(potionItem, targetHat.getPotion());
		setButton(42, potionItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorPotionMenu editorPotionMenu = new EditorPotionMenu(core, editorManager, owner, () -> 
				{
					EditorLore.updatePotionDescription(getItem(42), targetHat.getPotion());
				});
				
				menuManager.addMenu(editorPotionMenu);
				editorPotionMenu.open();
			}
			
			else if (event.isShiftRightClick())
			{
				targetHat.removePotion();
				EditorLore.updatePotionDescription(getItem(42), targetHat.getPotion());
			}
			
			return MenuClickResult.NEUTRAL;
		});
	}
	
	protected void buildSection ()
	{
		// Equip
		ItemStack equipItem = ItemUtil.createItem(Material.DIAMOND_HELMET, Message.EDITOR_MISC_EQUIP, Message.EDITOR_MAIN_MENU_EQUIP_DESCRIPTION);
		setButton(equipButtonSlot, equipItem, (event, slot) ->
		{
			// Stop if the player has more than the maximum allowed hats
			if (!ownerState.canEquip())
			{
				owner.sendMessage(Message.HAT_EQUIPPED_OVERFLOW.replace("{1}", Integer.toString(SettingsManager.MAXIMUM_HAT_LIMIT.getInt())));
				return MenuClickResult.NEGATIVE;
			}
			
			Hat clone;
			
			if (event.isShiftClick()) {
				clone = targetHat.equippableClone();
			} else {
				clone = editorManager.getBaseHat().equippableClone();
			}
			
			clone.setCanBeSaved(false);
			clone.setPermanent(false);
			clone.setDuration(15);
			clone.clearPropertyChanges();
			
			core.getParticleManager().equipHat(owner, clone, false);
			menuManager.closeInventory();
			return MenuClickResult.NEUTRAL;
		});
		
		// Type
		ItemStack typeItem = ItemUtil.createItem(CompatibleMaterial.CYAN_DYE, Message.EDITOR_MAIN_MENU_SET_TYPE);
		EditorLore.updateTypeDescription(typeItem, targetHat);
		setButton(10, typeItem, (event, slot) ->
		{
			if (!event.isShiftClick())
			{
				EditorTypeMenu editorTypeMenu = new EditorTypeMenu(core, editorManager, owner, (obj) ->
				{
					ParticleType type = targetHat.getType();
					
					// Reset animation if new type doesn't support it
					if (!type.supportsAnimation() && targetHat.getAnimation() == ParticleAnimation.ANIMATED) {
						targetHat.setAnimation(ParticleAnimation.STATIC);
					}
					
					EditorLore.updateTrackingDescription(getItem(trackingButtonSlot), targetHat);
					EditorLore.updateTypeDescription(getItem(10), targetHat);
					
					setButton(particleButtonSlot, getParticleItem(), getParticleAction());
					
					if (targetHat.getEffect().getParticlesSupported() == 1) {
						EditorLore.updateParticleDescription(getItem(particleButtonSlot), targetHat, 0);
					}
					
					else 
					{
						if (targetHat.getParticleData(0).hasPropertyChanges()) {
							core.getDatabase().saveParticleData(editorBaseMenu.getMenuInventory().getName(), targetHat, 0);
						}
					}
				});
				
				menuManager.addMenu(editorTypeMenu);
				editorTypeMenu.open();
			}
			
			else
			{
				int id = targetHat.getAnimation().getID();
				ParticleAnimation animation = ParticleAnimation.fromID(MathUtil.wrap(id + 1, ParticleAnimation.values().length, 0));
				targetHat.setAnimation(animation);
				EditorLore.updateTypeDescription(getItem(10), targetHat);
			}
			
			return MenuClickResult.NEUTRAL;
		});
		
		// Location
		ItemStack locationItem = ItemUtil.createItem(Material.CLAY_BALL, Message.EDITOR_MAIN_MENU_SET_LOCATION);
		EditorLore.updateLocationDescription(locationItem, targetHat.getLocation(), Message.EDITOR_MAIN_MENU_LOCATION_DESCRIPTION);
		setButton(11, locationItem, (event, slot) ->
		{
			final int increment = event.isLeftClick() ? 1 : -1;
			final int locationID = MathUtil.wrap(targetHat.getLocation().getID() + increment, ParticleLocation.values().length, 0);
			final ParticleLocation location = ParticleLocation.fromId(locationID);
			
			targetHat.setLocation(location);
			EditorLore.updateLocationDescription(getItem(11), location, Message.EDITOR_MAIN_MENU_LOCATION_DESCRIPTION);
			return event.isLeftClick() ? MenuClickResult.POSITIVE : MenuClickResult.NEGATIVE;
		});
		
		// Speed
		ItemStack speedItem = ItemUtil.createItem(Material.SUGAR, Message.EDITOR_MAIN_MENU_SET_SPEED);

		// Temporary tweak until v5.0
		if (core.getDatabaseType() == DatabaseType.YAML)
		{
			EditorLore.updateDoubleDescription(speedItem, targetHat.getSpeed(), Message.EDITOR_MAIN_MENU_SPEED_DESCRIPTION);
			setButton(16, speedItem, (event, slot) ->
			{
				final double normalClick    = event.isLeftClick() ? 0.1D : -0.1D;
				final double shiftClick     = event.isShiftClick() ? 10D : 1D;
				final double modifier       = normalClick * shiftClick;
				final double speed = event.isMiddleClick() ? 0D : targetHat.getSpeed() + modifier;

				targetHat.setSpeed(speed);
				EditorLore.updateDoubleDescription(getItem(16), targetHat.getSpeed(), Message.EDITOR_MAIN_MENU_SPEED_DESCRIPTION);
				return event.isLeftClick() ? MenuClickResult.POSITIVE : MenuClickResult.NEGATIVE;
			});
		}

		// Mysql
		else {
			EditorLore.updateIntegerDescription(speedItem, (int) targetHat.getSpeed(), Message.EDITOR_MAIN_MENU_SPEED_DESCRIPTION);
			setButton(16, speedItem, (event, slot) ->
			{
				final int increment = event.isLeftClick() ? 1 : -1;
				final int speed = (int) targetHat.getSpeed() + increment;

				targetHat.setSpeed(speed);
				EditorLore.updateIntegerDescription(getItem(16), (int) targetHat.getSpeed(), Message.EDITOR_MAIN_MENU_SPEED_DESCRIPTION);
				return event.isLeftClick() ? MenuClickResult.POSITIVE : MenuClickResult.NEGATIVE;
			});
		}
		
		// Action
		ItemStack actionItem = ItemUtil.createItem(CompatibleMaterial.GUNPOWDER, Message.EDITOR_MAIN_MENU_SET_ACTION);
		EditorLore.updateGenericActionDescription(actionItem, targetHat);
		setButton(19, actionItem, (event, slot) ->
		{
			EditorActionMenuOverview editorActionMenuOverview = new EditorActionMenuOverview(core, editorManager, owner, () ->
			{
				EditorLore.updateGenericActionDescription(getItem(19), editorManager.getBaseHat());
			});
			
			menuManager.addMenu(editorActionMenuOverview);
			editorActionMenuOverview.open();
			
			return MenuClickResult.NEUTRAL;
		});
		
		// Mode
		ItemStack modeItem = ItemUtil.createItem(CompatibleMaterial.ROSE_RED, Message.EDITOR_MAIN_MENU_SET_MODE);
		EditorLore.updateModeDescription(modeItem, targetHat.getMode(), Message.EDITOR_MAIN_MENU_MODE_DESCRIPTION);
		setButton(20, modeItem, (event, slot) ->
		{
			List<ParticleMode> modes = ParticleMode.getSupportedModes();
			
			final int increment = event.isLeftClick() ? 1 : -1;
			final int size = modes.size();
			final int index = MathUtil.wrap(modes.indexOf(targetHat.getMode()) + increment, size, 0);
			final ParticleMode mode = modes.get(index);
			
			targetHat.setMode(mode);
			EditorLore.updateModeDescription(getItem(20), mode, Message.EDITOR_MAIN_MENU_MODE_DESCRIPTION);
			return event.isLeftClick() ? MenuClickResult.POSITIVE : MenuClickResult.NEGATIVE;
		});
		
		// Frequency
		ItemStack frequencyItem = ItemUtil.createItem(CompatibleMaterial.COMPARATOR, Message.EDITOR_MAIN_MENU_SET_UPDATE_FREQUENCY);
		EditorLore.updateFrequencyDescription(frequencyItem, targetHat.getUpdateFrequency(), Message.EDITOR_MAIN_MENU_UPDATE_FREQUENCY_DESCRIPTION);
		setButton(22, frequencyItem, (event, slot) ->
		{
			final int increment = event.isLeftClick() ? 1 : -1;
			int frequency = targetHat.getUpdateFrequency() + increment;
			
			if (event.isMiddleClick()) {
				frequency = 2;
			}
			
			targetHat.setUpdateFrequency(frequency);
			EditorLore.updateFrequencyDescription(getItem(22), targetHat.getUpdateFrequency(), Message.EDITOR_MAIN_MENU_UPDATE_FREQUENCY_DESCRIPTION);
			return event.isLeftClick() ? MenuClickResult.POSITIVE : MenuClickResult.NEGATIVE;
		});
		
		// Angle
		ItemStack angleItem = ItemUtil.createItem(Material.SLIME_BALL, Message.EDITOR_MAIN_MENU_SET_ANGLE);
		EditorLore.updateVectorDescription(angleItem, targetHat.getAngle(), Message.EDITOR_MAIN_MENU_VECTOR_DESCRIPTION);
		setButton(24, angleItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorAngleMenu editorAngleMenu = new EditorAngleMenu(core, editorManager, owner, () ->
				{
					EditorLore.updateVectorDescription(getItem(24), editorManager.getTargetHat().getAngle(), Message.EDITOR_MAIN_MENU_VECTOR_DESCRIPTION);
				});
				
				menuManager.addMenu(editorAngleMenu);
				editorAngleMenu.open();
			}
			
			else if (event.isShiftRightClick())
			{
				targetHat.setAngle(0, 0, 0);
				EditorLore.updateVectorDescription(getItem(24), editorManager.getTargetHat().getAngle(), Message.EDITOR_MAIN_MENU_VECTOR_DESCRIPTION);
			}
			
			return MenuClickResult.NEUTRAL;
		});
		
		// Offset
		ItemStack offsetItem = ItemUtil.createItem(CompatibleMaterial.REPEATER, Message.EDITOR_MAIN_MENU_SET_OFFSET);
		EditorLore.updateOffsetDescription(offsetItem, targetHat);
		setButton(25, offsetItem, (event, slot) ->
		{
			EditorOffsetMenu editorOffsetMenu = new EditorOffsetMenu(core, editorManager, owner, () ->
			{
				EditorLore.updateOffsetDescription(getItem(25), editorManager.getTargetHat());
			});
			
			menuManager.addMenu(editorOffsetMenu);
			editorOffsetMenu.open();
			
			return MenuClickResult.NEUTRAL;
		});
		
		// Tracking
		ItemStack trackingItem = ItemUtil.createItem(Material.COMPASS, Message.EDITOR_MAIN_MENU_SET_TRACKING_METHOD); 
		EditorLore.updateTrackingDescription(trackingItem, targetHat);
		setButton(trackingButtonSlot, trackingItem, (event, slot) ->
		{
			List<ParticleTracking> methods = targetHat.getEffect().getSupportedTrackingMethods();
			
			final int increment = event.isLeftClick() ? 1 : -1;
			final int size = methods.size();
			final int index = MathUtil.wrap(methods.indexOf(targetHat.getTrackingMethod()) + increment, size, 0);
			
			targetHat.setTrackingMethod(methods.get(index));
			EditorLore.updateTrackingDescription(getItem(trackingButtonSlot), targetHat);
			return event.isLeftClick() ? MenuClickResult.POSITIVE : MenuClickResult.NEGATIVE;
		});
		
		// Count
		ItemStack countItem = ItemUtil.createItem(CompatibleMaterial.WHEAT_SEEDS, Message.EDITOR_MAIN_MENU_SET_COUNT);
		EditorLore.updateIntegerDescription(countItem, targetHat.getCount(), Message.EDITOR_MAIN_MENU_COUNT_DESCRIPTION);
		setButton(countButtonSlot, countItem, (event, slot) ->
		{
			final int increment = event.isLeftClick() ? 1 : -1;
			final int count = targetHat.getCount() + increment;
			
			targetHat.setCount(count);
			EditorLore.updateIntegerDescription(getItem(countButtonSlot), targetHat.getCount(), Message.EDITOR_MAIN_MENU_COUNT_DESCRIPTION);
			return event.isLeftClick() ? MenuClickResult.POSITIVE : MenuClickResult.NEGATIVE;
		});
		
		// Particle
		ItemStack particleItem = getParticleItem();
		if (targetHat.getEffect().getParticlesSupported() == 1) {
			EditorLore.updateParticleDescription(particleItem, targetHat, 0);
		}
		setButton(particleButtonSlot, getParticleItem(), getParticleAction());
		
		// Scale
		ItemStack scaleItem = ItemUtil.createItem(CompatibleMaterial.PUFFERFISH, Message.EDITOR_MAIN_MENU_SET_SCALE);
		EditorLore.updateDoubleDescription(scaleItem, targetHat.getScale(), Message.EDITOR_MAIN_MENU_SCALE_DESCRIPTION);
		setButton(scaleItemButtonSlot, scaleItem, (event, slot) ->
		{
			double normalClick    = event.isLeftClick() ? 0.1f : -0.1f;
			double shiftClick     = event.isShiftClick() ? 10 : 1;
			double modifier       = normalClick * shiftClick;
			boolean isMiddleClick = event.isMiddleClick();
			
			double scale = isMiddleClick ? 1 : targetHat.getScale() + modifier;
			targetHat.setScale(scale);
			
			EditorLore.updateDoubleDescription(getItem(scaleItemButtonSlot), targetHat.getScale(), Message.EDITOR_MAIN_MENU_SCALE_DESCRIPTION);

			if (isMiddleClick) {
				return MenuClickResult.NEUTRAL;
			} else {
				return event.isLeftClick() ? MenuClickResult.POSITIVE : MenuClickResult.NEGATIVE;
			}
		});
	}

	@Override
	public void onClose(boolean forced) 
	{
		Database database = core.getDatabase();
		
		if (targetHat.getType().getParticlesSupported() == 1)
		{
			if (targetHat.getParticleData(0).hasPropertyChanges()) {
				database.saveParticleData(editorManager.getMenuName(), targetHat, 0);
			}
		}
		
		if (!forced)
		{
			Hat hat = editorManager.getBaseHat();
			if (hat == null) {
				return;
			}
			
			String name = editorManager.getMenuName();
			
			if (hat.isModified())
			{
				int targetSlot = editorManager.getTargetSlot();
				database.saveHat(name, targetSlot, hat);
				hat.clearPropertyChanges();
			}
			
			if (hat.getNodeCount() > 0)
			{
				for (Hat node : hat.getNodes())
				{
					if (node.isModified())
					{
						database.saveNode(name, node.getIndex(), node);
						node.clearPropertyChanges();
					}
				}
			}
		}
	}

	@Override
	public void onTick(int ticks) 
	{
		
	}
	
	/**
	 * Returns an appropriate item for the current ParticleType
	 * @return
	 */
	private ItemStack getParticleItem () 
	{
		Hat hat = editorManager.getTargetHat();
		int particlesSupported = hat.getEffect().getParticlesSupported();
		
		if (particlesSupported == 0) {
			return noParticleItem;
		}
		
		if (particlesSupported == 1) {
			return singleParticleItem;
		}
		
		return multipleParticlesItem;
	}
	
	/**
	 * Returns an appropriate EditorAction for the current ParticleType
	 * @return
	 */
	private MenuAction getParticleAction () 
	{
		Hat hat = editorManager.getTargetHat();
		int particlesSupported = hat.getEffect().getParticlesSupported();
		
		if (particlesSupported == 0) {
			return noParticle4U;
		}
		
		if (particlesSupported == 1) {
			return setParticleAction;
		}
		
		return editParticleAction;
	}
	
	public void onParticleEdit (ItemStack item, int particleIndex)
	{
		ParticleEffect pe = targetHat.getParticle(particleIndex);
		switch (pe.getProperty())
		{
			case NO_DATA:
				break;

			case COLOR_TRANSITION:
			case COLOR:
			{
				EditorColorMenu editorColorMenu = new EditorColorMenu(core, editorManager, owner, particleIndex, () ->
				{
					EditorLore.updateParticleDescription(item, targetHat, particleIndex);
				});
				menuManager.addMenu(editorColorMenu);
				editorColorMenu.open();
			}
			break;
			
			case BLOCK_DATA:
			{
				Message menuTitle = Message.EDITOR_ICON_MENU_BLOCK_TITLE;
				Message blockTitle = Message.EDITOR_ICON_MENU_BLOCK_INFO;
				Message blockDescription = Message.EDITOR_ICON_MENU_BLOCK_DESCRIPTION;
				
				EditorItemPromptMenu editorItemMenu = new EditorItemPromptMenu(core, editorManager, owner, menuTitle, blockTitle, blockDescription, (clickedItem) ->
				{
					menuManager.closeCurrentMenu();
					
					if (clickedItem == null) {
						return;
					}
					
					ItemStack i = (ItemStack)clickedItem;
					if (!i.getType().isBlock()) {
						return;
					}
					
					targetHat.setParticleBlock(particleIndex, i);
					EditorLore.updateParticleDescription(item, targetHat, particleIndex);
				});
				
				menuManager.addMenu(editorItemMenu);
				editorItemMenu.open();
			}
			break;
			
			case ITEM_DATA:
			{
				Message menuTitle = Message.EDITOR_ICON_MENU_ITEM_TITLE;
				Message itemTitle = Message.EDITOR_ICON_MENU_ITEM_INFO;
				Message itemDescription = Message.EDITOR_ICON_MENU_ITEM_DESCRIPTION;
				
				EditorItemPromptMenu editorItemMenu = new EditorItemPromptMenu(core, editorManager, owner, menuTitle, itemTitle, itemDescription, (clickedItem) ->
				{
					menuManager.closeCurrentMenu();
					
					if (clickedItem == null) {
						return;
					}
					
					ItemStack i = (ItemStack)clickedItem;
					if (i.getType().isBlock()) {
						return;
					}
					
					targetHat.setParticleItem(particleIndex, i);
					EditorLore.updateParticleDescription(item, targetHat, particleIndex);
				});
				
				menuManager.addMenu(editorItemMenu);
				editorItemMenu.open();
			}
			break;
			
			case ITEMSTACK_DATA:
			{
				EditorItemStackMenu editorItemStackMenu = new EditorItemStackMenu(core, editorManager, owner, particleIndex, () ->
				{
					EditorLore.updateParticleDescription(item, targetHat, particleIndex);
				});
				
				menuManager.addMenu(editorItemStackMenu);
				editorItemStackMenu.open();
			}
			break;
		}
	}

}
