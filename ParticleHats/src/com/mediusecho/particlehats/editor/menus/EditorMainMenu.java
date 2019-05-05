package com.mediusecho.particlehats.editor.menus;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.particles.properties.ParticleAnimation;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleMode;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;
import com.mediusecho.particlehats.particles.properties.ParticleType;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;

public class EditorMainMenu extends EditorMenu {
	
	// These will let us extend this class to the node editor
	protected int particleItemSlot = 39;
	protected int trackingItemSlot = 29;
	protected int countItemSlot    = 33;
	protected int equipItemSlot    = 52;
	protected int scaleItemSlot    = 31;
	
	protected final ItemStack noParticleItem = ItemUtil.createItem(CompatibleMaterial.BARRIER, Message.EDITOR_MAIN_MENU_NO_PARTICLES, Message.EDITOR_MAIN_MENU_NO_PARTICLES_DESCRIPTION);
	protected final ItemStack singleParticleItem = ItemUtil.createItem(Material.REDSTONE, Message.EDITOR_MAIN_MENU_SET_PARTICLE);
	protected final ItemStack multipleParticlesItem = ItemUtil.createItem(Material.BUCKET, Message.EDITOR_MAIN_MENU_EDIT_PARTICLES);
	
	protected final EditorAction setParticleAction;
	protected final EditorAction editParticleAction;
	protected final EditorAction noParticle4U;
	
	protected final Hat targetHat;
	
	public EditorMainMenu(ParticleHats core, Player owner, MenuBuilder menuBuilder) 
	{
		super(core, owner, menuBuilder);
		targetHat = menuBuilder.getTargetHat();
		inventory = Bukkit.createInventory(null, 54, Message.EDITOR_MAIN_MENU_TITLE.getValue());
		
		setParticleAction = (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorParticleSelectionMenu editorParticleMenu = new EditorParticleSelectionMenu(core, owner, menuBuilder, 0, (particle) ->
				{
					Hat hat = menuBuilder.getTargetHat();
					hat.setParticle(0, particle);
					
					// Add this particle to our recents list
					core.getParticleManager().addParticleToRecents(ownerID, particle);
					
					if (targetHat.getEffect().getParticlesSupported() == 1) {
						EditorLore.updateParticleDescription(getItem(particleItemSlot), targetHat, 0);
					}
					
					menuBuilder.goBack();
				});
				menuBuilder.addMenu(editorParticleMenu);
				editorParticleMenu.open();
			}
			
			else if (event.isRightClick()) {
				onParticleEdit(getItem(particleItemSlot), 0);
			}
			
			return EditorClickType.NEUTRAL;
		};
		
		editParticleAction = (event, slot) ->
		{
			EditorParticleOverviewMenu editorParticleOverviewMenu = new EditorParticleOverviewMenu(core, owner, menuBuilder, this);
			menuBuilder.addMenu(editorParticleOverviewMenu);
			editorParticleOverviewMenu.open();
			return EditorClickType.NEUTRAL;
		};
		
		noParticle4U = (event, slot) ->
		{
			owner.playSound(owner.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
			return EditorClickType.NONE;
		};
		
		build();
	}
	
	@Override
	public void onClose (boolean forced)
	{
		Database database = core.getDatabase();
		
		// Only save particle data if we're closing this menu manually.
		// Hats are saved automatically when menus are forced to close
		if (!forced)
		{	
			Hat hat = menuBuilder.getBaseHat();
			if (hat != null)
			{
				String menuName = menuBuilder.getEditingMenu().getName();	
				
				if (hat.isModified())
				{
					int targetSlot = menuBuilder.getTargetSlot();
					database.saveHat(menuName, targetSlot, hat);
					hat.clearPropertyChanges();
				}
				
				if (hat.getNodeCount() > 0) 
				{
					for (Hat node : hat.getNodes())
					{
						if (node.isModified()) 
						{
							database.saveNode(menuName, node.getIndex(), node);
							node.clearPropertyChanges();
						}
					}
				}
			}
		}
			
		// Save any unsaved particle data
		if (targetHat.getType().getParticlesSupported() == 1)
		{
			if (targetHat.getParticleData(0).hasPropertyChanges()) {
				database.saveParticleData(menuBuilder.getMenuName(), targetHat, 0);
			}
		}
	}
	
	public void onParticleEdit (ItemStack item, int particleIndex)
	{
		ParticleEffect particle = targetHat.getParticle(particleIndex);
		switch (particle.getProperty())
		{
			case NO_DATA:
				break;
		
			case COLOR:
			{
				EditorColorMenu editorColorMenu = new EditorColorMenu(core, owner, menuBuilder, particleIndex, () ->
				{
					EditorLore.updateParticleDescription(item, targetHat, particleIndex);
				});
				menuBuilder.addMenu(editorColorMenu);
				editorColorMenu.open();
				break;
			}
			
			case BLOCK_DATA:
			{
				Message menuTitle = Message.EDITOR_ICON_MENU_BLOCK_TITLE;
				Message blockTitle = Message.EDITOR_ICON_MENU_BLOCK_INFO;
				Message blockDescription = Message.EDITOR_ICON_MENU_BLOCK_DESCRIPTION;
				
				EditorIconMenu editorBlockMenu = new EditorIconMenu(core, owner, menuBuilder, menuTitle, blockTitle, blockDescription, (i) ->
				{
					if (i.getType().isBlock()) 
					{
						targetHat.setParticleBlock(particleIndex, i);
						EditorLore.updateParticleDescription(item, targetHat, particleIndex);
					}
				});
				menuBuilder.addMenu(editorBlockMenu);
				editorBlockMenu.open();
				break;
			}
			
			case ITEM_DATA:
			{
				Message menuTitle = Message.EDITOR_ICON_MENU_ITEM_TITLE;
				Message itemTitle = Message.EDITOR_ICON_MENU_ITEM_INFO;
				Message itemDescription = Message.EDITOR_ICON_MENU_ITEM_DESCRIPTION;
				
				EditorIconMenu editorItemMenu = new EditorIconMenu(core, owner, menuBuilder, menuTitle, itemTitle, itemDescription, (i) ->
				{
					if (!i.getType().isBlock()) 
					{
						targetHat.setParticleItem(particleIndex, i);
						EditorLore.updateParticleDescription(item, targetHat, particleIndex);
					}
				});
				menuBuilder.addMenu(editorItemMenu);
				editorItemMenu.open();
				break;
			}
			 
			case ITEMSTACK_DATA:
			{
				EditorItemStackMenu editorItemStackMenu = new EditorItemStackMenu(core, owner, menuBuilder, particleIndex, () ->
				{
					EditorLore.updateParticleDescription(item, targetHat, particleIndex);
				});
				menuBuilder.addMenu(editorItemStackMenu);
				editorItemStackMenu.open();
				break;
			}
		}
	}
	
	/**
	 * Updates the main menu to reflect an angle property update
	 */
	private void onAngleChange () {
		EditorLore.updateVectorDescription(getItem(24), menuBuilder.getTargetHat().getAngle(), Message.EDITOR_MAIN_MENU_VECTOR_DESCRIPTION);
	}
	
	/**
	 * Returns an appropriate item for the current ParticleType
	 * @return
	 */
	private ItemStack getParticleItem () 
	{
		Hat hat = menuBuilder.getTargetHat();
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
	private EditorAction getParticleAction () 
	{
		Hat hat = menuBuilder.getTargetHat();
		int particlesSupported = hat.getEffect().getParticlesSupported();
		
		if (particlesSupported == 0) {
			return noParticle4U;
		}
		
		if (particlesSupported == 1) {
			return setParticleAction;
		}
		
		return editParticleAction;
	}
	
	/**
	 * Builds a secion of this menu that can be used by the node main menu
	 */
	protected void buildSection ()
	{
		// Equip
		setButton(equipItemSlot, ItemUtil.createItem(Material.DIAMOND_HELMET, Message.EDITOR_MISC_EQUIP), (event, slot) ->
		{
			Hat clone = targetHat.equippableClone();
			clone.setPermanent(false);
			clone.setDuration(10);
			clone.clearPropertyChanges();
			
			core.getParticleManager().equipHat(ownerID, clone);
			owner.closeInventory();
			return EditorClickType.NEUTRAL;
		});
		
		// Type
		ItemStack typeItem = ItemUtil.createItem(CompatibleMaterial.CYAN_DYE, Message.EDITOR_MAIN_MENU_SET_TYPE);
		EditorLore.updateTypeDescription(typeItem, targetHat);
		setButton(10, typeItem, (event, slot) ->
		{
			if (!event.isShiftClick())
			{
				EditorTypeMenu editorTypeMenu = new EditorTypeMenu(core, owner, menuBuilder, () ->
				{
					ParticleType type = targetHat.getType();
					if (!type.supportsAnimation() && targetHat.getAnimation().equals(ParticleAnimation.ANIMATED)) {
						targetHat.setAnimation(ParticleAnimation.STATIC);
					}
					
					EditorLore.updateTrackingDescription(getItem(trackingItemSlot), targetHat);
					EditorLore.updateTypeDescription(getItem(10), targetHat);
					
					ItemStack particleItem = getParticleItem();
					setButton(particleItemSlot, particleItem, getParticleAction());
					
					if (targetHat.getEffect().getParticlesSupported() == 1) {
						EditorLore.updateParticleDescription(getItem(particleItemSlot), targetHat, 0);
					}
					
					else 
					{
						if (targetHat.getParticleData(0).hasPropertyChanges()) {
							core.getDatabase().saveParticleData(menuBuilder.getMenuName(), targetHat, 0);
						}
					}
				});
				menuBuilder.addMenu(editorTypeMenu);
				editorTypeMenu.open();
			}
			
			else
			{
				int id = targetHat.getAnimation().getID();
				ParticleAnimation animation = ParticleAnimation.fromID(MathUtil.wrap(id + 1, ParticleAnimation.values().length, 0));
				targetHat.setAnimation(animation);
				EditorLore.updateTypeDescription(getItem(10), targetHat);
			}
			return EditorClickType.NEUTRAL;
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
			return event.isLeftClick() ? EditorClickType.POSITIVE : EditorClickType.NEGATIVE;
		});
		
		// Speed
		ItemStack speedItem = ItemUtil.createItem(Material.SUGAR, Message.EDITOR_MAIN_MENU_SET_SPEED);
		EditorLore.updateIntegerDescription(speedItem, targetHat.getSpeed(), Message.EDITOR_MAIN_MENU_SPEED_DESCRIPTION);
		setButton(16, speedItem, (event, slot) ->
		{
			final int increment = event.isLeftClick() ? 1 : -1;
			final int speed = targetHat.getSpeed() + increment;
			
			targetHat.setSpeed(speed);
			EditorLore.updateIntegerDescription(getItem(16), targetHat.getSpeed(), Message.EDITOR_MAIN_MENU_SPEED_DESCRIPTION);
			return event.isLeftClick() ? EditorClickType.POSITIVE : EditorClickType.NEGATIVE;
		});
		
		// Action
		ItemStack actionItem = ItemUtil.createItem(CompatibleMaterial.GUNPOWDER, Message.EDITOR_MAIN_MENU_SET_ACTION);
		EditorLore.updateGenericActionDescription(actionItem, targetHat);
		setButton(19, actionItem, (event, slot) ->
		{
			EditorActionOverviewMenu editorActionOverviewMenu = new EditorActionOverviewMenu(core, owner, menuBuilder, () ->
			{
				EditorLore.updateGenericActionDescription(getItem(19), menuBuilder.getBaseHat());
			});
			menuBuilder.addMenu(editorActionOverviewMenu);
			editorActionOverviewMenu.open();
			return EditorClickType.NEUTRAL;
		});
		
		// Mode
		ItemStack modeItem = ItemUtil.createItem(CompatibleMaterial.ROSE_RED, Message.EDITOR_MAIN_MENU_SET_MODE);
		EditorLore.updateModeDescription(modeItem, targetHat.getMode(), Message.EDITOR_MAIN_MENU_MODE_DESCRIPTION);
		setButton(20, modeItem, (event, slot) ->
		{
			final int increment = event.isLeftClick() ? 1 : -1;
			final int modeID = MathUtil.wrap(targetHat.getMode().getID() + increment, ParticleMode.values().length, 0);
			final ParticleMode mode = ParticleMode.fromId(modeID);
			
			targetHat.setMode(mode);
			EditorLore.updateModeDescription(getItem(20), mode, Message.EDITOR_MAIN_MENU_MODE_DESCRIPTION);
			return event.isLeftClick() ? EditorClickType.POSITIVE : EditorClickType.NEGATIVE;
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
			return event.isLeftClick() ? EditorClickType.POSITIVE : EditorClickType.NEGATIVE;
		});
		
		// Angle
		ItemStack angleItem =  ItemUtil.createItem(Material.SLIME_BALL, Message.EDITOR_MAIN_MENU_SET_ANGLE);
		EditorLore.updateVectorDescription(angleItem, targetHat.getAngle(), Message.EDITOR_MAIN_MENU_VECTOR_DESCRIPTION);
		setButton(24, angleItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorAngleMenu editorAngleMenu = new EditorAngleMenu(core, owner, menuBuilder, () ->
				{
					EditorLore.updateVectorDescription(getItem(24), menuBuilder.getTargetHat().getAngle(), Message.EDITOR_MAIN_MENU_VECTOR_DESCRIPTION);
				});
				menuBuilder.addMenu(editorAngleMenu);
				editorAngleMenu.open();
			}
			
			else if (event.isShiftRightClick()) 
			{
				targetHat.setAngle(0, 0, 0);
				onAngleChange();
			}
			return EditorClickType.NEUTRAL;
		});
		
		// Offset
		ItemStack offsetItem = ItemUtil.createItem(CompatibleMaterial.REPEATER, Message.EDITOR_MAIN_MENU_SET_OFFSET);
		EditorLore.updateOffsetDescription(offsetItem, targetHat);
		setButton(25, offsetItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorOffsetMenu editorOffsetMenu = new EditorOffsetMenu(core, owner, menuBuilder, () ->
				{
					EditorLore.updateOffsetDescription(getItem(25), menuBuilder.getTargetHat());
				});
				menuBuilder.addMenu(editorOffsetMenu);
				editorOffsetMenu.open();
			}
			return EditorClickType.NEUTRAL;
		});
		
		// Tracking
		ItemStack trackingItem = ItemUtil.createItem(Material.COMPASS, Message.EDITOR_MAIN_MENU_SET_TRACKING_METHOD); 
		EditorLore.updateTrackingDescription(trackingItem, targetHat);
		setButton(trackingItemSlot, trackingItem, (event, slot) ->
		{
			List<ParticleTracking> methods = targetHat.getEffect().getSupportedTrackingMethods();
			
			final int increment = event.isLeftClick() ? 1 : -1;
			final int size = methods.size();
			final int index = MathUtil.wrap(methods.indexOf(targetHat.getTrackingMethod()) + increment, size, 0);
			
			targetHat.setTrackingMethod(methods.get(index));
			EditorLore.updateTrackingDescription(getItem(trackingItemSlot), targetHat);
			return event.isLeftClick() ? EditorClickType.POSITIVE : EditorClickType.NEGATIVE;
		});
		
		// Count
		ItemStack countItem = ItemUtil.createItem(CompatibleMaterial.WHEAT_SEEDS, Message.EDITOR_MAIN_MENU_SET_COUNT);
		EditorLore.updateIntegerDescription(countItem, targetHat.getCount(), Message.EDITOR_MAIN_MENU_COUNT_DESCRIPTION);
		setButton(countItemSlot, countItem, (event, slot) ->
		{
			final int increment = event.isLeftClick() ? 1 : -1;
			final int count = targetHat.getCount() + increment;
			
			targetHat.setCount(count);
			EditorLore.updateIntegerDescription(getItem(countItemSlot), targetHat.getCount(), Message.EDITOR_MAIN_MENU_COUNT_DESCRIPTION);
			return event.isLeftClick() ? EditorClickType.POSITIVE : EditorClickType.NEGATIVE;
		});
		
		// Particle
		ItemStack particleItem = getParticleItem();
		if (targetHat.getEffect().getParticlesSupported() == 1) {
			EditorLore.updateParticleDescription(particleItem, targetHat, 0);
		}
		setButton(particleItemSlot, getParticleItem(), getParticleAction());
		
		// Scale
		ItemStack scaleItem = ItemUtil.createItem(CompatibleMaterial.PUFFERFISH, Message.EDITOR_MAIN_MENU_SET_SCALE);
		EditorLore.updateDoubleDescription(scaleItem, targetHat.getScale(), Message.EDITOR_MAIN_MENU_SCALE_DESCRIPTION);
		setButton(scaleItemSlot, scaleItem, (event, slot) ->
		{
			double normalClick    = event.isLeftClick() ? 0.1f : -0.1f;
			double shiftClick     = event.isShiftClick() ? 10 : 1;
			double modifier       = normalClick * shiftClick;
			boolean isMiddleClick = event.isMiddleClick();
			
			double scale = isMiddleClick ? 1 : targetHat.getScale() + modifier;
			targetHat.setScale(scale);
			
			EditorLore.updateDoubleDescription(getItem(scaleItemSlot), targetHat.getScale(), Message.EDITOR_MAIN_MENU_SCALE_DESCRIPTION);

			if (isMiddleClick) {
				return EditorClickType.NEUTRAL;
			} else {
				return event.isLeftClick() ? EditorClickType.POSITIVE : EditorClickType.NEGATIVE;
			}
		});
	}

	@Override
	protected void build() 
	{
		buildSection();
		
		// Main Menu
		setButton(46, mainMenuButton, backAction);
		
		// Meta
		ItemStack metaItem = ItemUtil.createItem(Material.SIGN, Message.EDITOR_MAIN_MENU_SET_META);
		EditorLore.updateGenericDescription(metaItem, Message.EDITOR_MAIN_MENU_META_DESCRIPTION);
		setButton(13, metaItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorMetaMenu editorMetaMenu = new EditorMetaMenu(core, owner, menuBuilder);
				menuBuilder.addMenu(editorMetaMenu);
				editorMetaMenu.open();
			}
			
			else if (event.isRightClick())
			{
				EditorDescriptionMenu editorDescriptionMenu = new EditorDescriptionMenu(core, owner, menuBuilder, true);
				menuBuilder.addMenu(editorDescriptionMenu);
				editorDescriptionMenu.open();
			}
			
			return EditorClickType.NEUTRAL;
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
			return event.isLeftClick() ? EditorClickType.POSITIVE : EditorClickType.NEGATIVE;
		});
		
		// Sound
		ItemStack soundItem = ItemUtil.createItem(CompatibleMaterial.MUSIC_DISC_STRAD, Message.EDITOR_MAIN_MENU_SET_SOUND);
		EditorLore.updateSoundItemDescription(soundItem, targetHat);
		setButton(28, soundItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorSoundMenu editorSoundMenu = new EditorSoundMenu(core, owner, menuBuilder, (sound) ->
				{
					targetHat.setSound(sound);
					EditorLore.updateSoundItemDescription(getItem(28), targetHat);
					menuBuilder.goBack();
				});
				menuBuilder.addMenu(editorSoundMenu);
				editorSoundMenu.open();
			}
			
			else if (event.isShiftRightClick()) 
			{
				targetHat.removeSound();
				EditorLore.updateSoundItemDescription(getItem(28), targetHat);
			}
			return EditorClickType.NEUTRAL;
		});
		
		// Clone
		ItemStack cloneItem = ItemUtil.createItem(CompatibleMaterial.PRISMARINE_SHARD, Message.EDITOR_MAIN_MENU_CLONE, Message.EDITOR_MAIN_MENU_CLONE_DESCRIPTION);
		setButton(30, cloneItem, (event, slot) ->
		{
			EditorSlotMenu editorSlotMenu = new EditorSlotMenu(core, owner, menuBuilder, true);
			menuBuilder.addMenu(editorSlotMenu);
			editorSlotMenu.open();
			return EditorClickType.NEUTRAL;
		});
		
		// Move
		ItemStack moveItem = ItemUtil.createItem(CompatibleMaterial.MAP, Message.EDITOR_MAIN_MENU_MOVE, Message.EDITOR_MAIN_MENU_MOVE_DESCRIPTION);
		setButton(32, moveItem, (event, slot) ->
		{
			EditorMenuSelectionMenu editorMenuSelectionMenu = new EditorMenuSelectionMenu(core, owner, menuBuilder, false, (menu) ->
			{
				EditorTransferMenu editorTransferMenu = new EditorTransferMenu(core, owner, menuBuilder, menu);
				menuBuilder.addMenu(editorTransferMenu);
				editorTransferMenu.open();
			});
			menuBuilder.addMenu(editorMenuSelectionMenu);
			editorMenuSelectionMenu.open();
			return EditorClickType.NEUTRAL;
		});
		
		// Node
		ItemStack nodeItem = ItemUtil.createItem(Material.ANVIL, Message.EDITOR_MAIN_MENU_EDIT_NODES, Message.EDITOR_MAIN_MENU_NODE_DESCRIPTION);
		setButton(34, nodeItem, (event, slot) ->
		{
			EditorNodeOverviewMenu editorNodeOverviewMenu = new EditorNodeOverviewMenu(core, owner, menuBuilder);
			menuBuilder.addMenu(editorNodeOverviewMenu);
			editorNodeOverviewMenu.open();
			return EditorClickType.NEUTRAL;
		});
		
		// Slot
		ItemStack slotItem = ItemUtil.createItem(Material.ITEM_FRAME, Message.EDITOR_MAIN_MENU_SET_SLOT, Message.EDITOR_MAIN_MENU_SLOT_DESCRIPTION);
		setButton(38, slotItem, (event, slot) ->
		{
			EditorSlotMenu editorSlotMenu = new EditorSlotMenu(core, owner, menuBuilder, false);
			menuBuilder.addMenu(editorSlotMenu);
			editorSlotMenu.open();
			return EditorClickType.NEUTRAL;
		});
		
		// Icon
		ItemStack iconItem = targetHat.getItem();//ItemUtil.createItem(targetHat.getMaterial(), Message.EDITOR_MAIN_MENU_SET_ICON, Message.EDITOR_MAIN_MENU_ICON_DESCRIPTION);
		ItemUtil.setNameAndDescription(iconItem, Message.EDITOR_MAIN_MENU_SET_ICON, Message.EDITOR_MAIN_MENU_ICON_DESCRIPTION);
		setButton(41, iconItem, (event, slot) ->
		{
			EditorIconOverviewMenu editorIconOverviewMenu = new EditorIconOverviewMenu(core, owner, menuBuilder, (item) ->
			{
				ItemUtil.setItemType(getItem(41), item);
			});
			menuBuilder.addMenu(editorIconOverviewMenu);
			editorIconOverviewMenu.open();
			return EditorClickType.NEUTRAL;
		});
		
		// Potion
		ItemStack potionItem = ItemUtil.createItem(Material.POTION, Message.EDITOR_MAIN_MENU_SET_POTION);
		EditorLore.updatePotionDescription(potionItem, targetHat.getPotion());
		setButton(42, potionItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorPotionMenu editorPotionMenu = new EditorPotionMenu(core, owner, menuBuilder, () ->
				{
					EditorLore.updatePotionDescription(getItem(42), targetHat.getPotion());
				}); 
				menuBuilder.addMenu(editorPotionMenu);
				editorPotionMenu.open();
			}
			
			else if (event.isShiftRightClick())
			{
				targetHat.removePotion();
				EditorLore.updatePotionDescription(getItem(42), targetHat.getPotion());
			}
			return EditorClickType.NEUTRAL;
		});
	}
}
