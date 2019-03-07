package com.mediusecho.particlehats.editor.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorParticleOverviewMenu extends EditorMenu {

	private final Hat targetHat;
	private final Map<Integer, ParticleEffect> particles;
	private final EditorAction editAction;
	private final Map<Integer, Boolean> modifiedParticles;
	
	public EditorParticleOverviewMenu(Core core, Player owner, MenuBuilder menuBuilder) 
	{
		super(core, owner, menuBuilder);
		
		targetHat = menuBuilder.getTargetHat();
		particles = new HashMap<Integer, ParticleEffect>();
		modifiedParticles = new HashMap<Integer, Boolean>();
		editAction = (event, slot) ->
		{
			int particleIndex = getClampedIndex(slot, 10, 2);
			if (event.isLeftClick())
			{
				EditorParticleSelectionMenu editorParticleMenu = new EditorParticleSelectionMenu(core, owner, menuBuilder, particleIndex, (particle) ->
				{
					Hat hat = menuBuilder.getTargetHat();
					hat.setParticle(particleIndex, particle);
					
					ItemStack item = getItem(slot);
					item.setType(particle.getMaterial());
					
					ItemUtil.setItemName(item, particle.getName());
					EditorLore.updateParticleDescription(item, targetHat, particleIndex);	
					modifiedParticles.put(particleIndex, true);
					
					menuBuilder.goBack();
				});
				menuBuilder.addMenu(editorParticleMenu);
				editorParticleMenu.open();
			}
			
			else if (event.isRightClick())
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
							modifiedParticles.put(particleIndex, true);
							EditorLore.updateParticleDescription(getItem(slot), targetHat, particleIndex);
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
						
						EditorIconMenu editorBlockMenu = new EditorIconMenu(core, owner, menuBuilder, menuTitle, blockTitle, blockDescription, (item) ->
						{
							if (item.getType().isBlock()) 
							{
								targetHat.setParticleBlock(particleIndex, item.getType());
								modifiedParticles.put(particleIndex, true);
								EditorLore.updateParticleDescription(getItem(slot), targetHat, particleIndex);
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
						
						EditorIconMenu editorItemMenu = new EditorIconMenu(core, owner, menuBuilder, menuTitle, itemTitle, itemDescription, (item) ->
						{
							if (!item.getType().isBlock()) 
							{
								targetHat.setParticleItem(particleIndex, item);
								modifiedParticles.put(particleIndex, true);
								EditorLore.updateParticleDescription(getItem(slot), targetHat, particleIndex);
							}
						});
						menuBuilder.addMenu(editorItemMenu);
						editorItemMenu.open();
						break;
					}
					 
					case ITEMSTACK_DATA:
					{
						// TODO: Update item count description
						EditorItemStackMenu editorItemStackMenu = new EditorItemStackMenu(core, owner, menuBuilder, particleIndex);
						menuBuilder.addMenu(editorItemStackMenu);
						editorItemStackMenu.open();
						break;
					}
				}
			}
			
			return EditorClickType.NEUTRAL;
		};
		
		inventory = Bukkit.createInventory(null, 54, Message.EDITOR_PARTICLE_OVERVIEW_MENU_TITLE.getValue());
		build();
	}
	
	@Override
	public void onClose (boolean forced)
	{
		Database database = core.getDatabase();
		String menuName = menuBuilder.getEditingMenu().getName();
		
		for (Entry<Integer, Boolean> particles : modifiedParticles.entrySet())
		{
			if (particles.getValue()) {
				database.saveParticleData(menuName, targetHat, particles.getKey());
			}
		}
	}

	@Override
	protected void build() 
	{
		setButton(49, backButton, backAction);
		
		String itemTitle = Message.EDITOR_PARTICLE_OVERVIEW_PARTICLE_NAME.getValue();
		
		int particlesSupported = targetHat.getType().getParticlesSupported();
		for (int i = 0; i < particlesSupported; i++)
		{
			ParticleEffect particle = targetHat.getParticle(i);
			Material material = particle.getMaterial();
			ItemStack item = ItemUtil.createItem(material, itemTitle.replace("{1}", Integer.toString(i + 1)));
			
			particles.put(i, particle);
			modifiedParticles.put(i, false);
			EditorLore.updateParticleDescription(item, targetHat, i);
			
			setItem(getNormalIndex(i, 10, 2), item);
		}
		
		for (int i = 0; i <= 27; i++) {
			setAction(getNormalIndex(i, 10, 2), editAction);
		}
	}

}
