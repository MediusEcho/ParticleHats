package com.mediusecho.particlehats.editor.menus;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorParticleOverviewMenu extends EditorMenu {

	private final Database database;
	
	private final Hat targetHat;
	private final Map<Integer, ParticleEffect> particles;
	private final EditorAction editAction;
	private final Map<Integer, Boolean> modifiedParticles;
	
	public EditorParticleOverviewMenu(ParticleHats core, Player owner, MenuBuilder menuBuilder, EditorMainMenu editorMainMenu) 
	{
		super(core, owner, menuBuilder);
		
		database = core.getDatabase();
		
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
					
					// Add this particle to our recents list
					core.getParticleManager().addParticleToRecents(ownerID, particle);
					
					ItemStack item = getItem(slot);
					ItemUtil.setItemType(item, particle.getItem());
					//item.setType(particle.getMaterial());
					
					EditorLore.updateParticleDescription(item, targetHat, particleIndex);	
					modifiedParticles.put(particleIndex, true);
					
					menuBuilder.goBack();
				});
				menuBuilder.addMenu(editorParticleMenu);
				editorParticleMenu.open();
			}
			
			else if (event.isRightClick()) {
				editorMainMenu.onParticleEdit(getItem(slot), particleIndex);
			}
			
			return EditorClickType.NEUTRAL;
		};
		
		inventory = Bukkit.createInventory(null, 54, Message.EDITOR_PARTICLE_OVERVIEW_MENU_TITLE.getValue());
		build();
	}
	
	@Override
	public void onClose (boolean forced)
	{
		for (Entry<Integer, Boolean> particles : modifiedParticles.entrySet())
		{
			if (particles.getValue()) {
				database.saveParticleData(menuBuilder.getMenuName(), targetHat, particles.getKey());
			}
		}
		
		for (int i = 0; i < targetHat.getType().getParticlesSupported(); i++)
		{
			if (targetHat.getParticleData(i).hasPropertyChanges()) {
				database.saveParticleData(menuBuilder.getMenuName(), targetHat, i);
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
			ItemStack item = particle.getItem().clone();
			ItemUtil.setItemName(item, itemTitle.replace("{1}", Integer.toString(i + 1)));
			//ItemStack item = ItemUtil.createItem(particleItem.getType(), itemTitle.replace("{1}", Integer.toString(i + 1)));
			
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
