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
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.ui.menus.SingularMenu;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorParticleMenuOverview extends SingularMenu {

	private final EditorMenuManager editorManager;
	private final EditorMainMenu editorMainMenu;
	private final Hat targetHat;
	
	private Map<Integer, ParticleEffect> particles;
	private Map<Integer, Boolean > modifiedParticles;
	
	public EditorParticleMenuOverview(ParticleHats core, EditorMenuManager menuManager, Player owner, EditorMainMenu editorMainMenu) 
	{
		super(core, menuManager, owner);
		
		this.editorManager = menuManager;
		this.editorMainMenu = editorMainMenu;
		this.targetHat = menuManager.getTargetHat();
		this.particles = new HashMap<Integer, ParticleEffect>();
		this.modifiedParticles = new HashMap<Integer, Boolean>();	
		this.inventory = Bukkit.createInventory(null, 54, Message.EDITOR_PARTICLE_OVERVIEW_MENU_TITLE.getValue());
		
		build();
	}

	@Override
	protected void build() 
	{
		setButton(49, backButtonItem, backButtonAction);
		
		MenuAction editAction = (event, slot) ->
		{
			int particleIndex = getClampedIndex(slot, 10, 2);
			if (event.isLeftClick())
			{
				EditorParticleSelectionMenu editorParticleSelectionMenu = new EditorParticleSelectionMenu(core, editorManager, owner, particleIndex, (particle) ->
				{
					menuManager.closeCurrentMenu();
					
					if (particle == null) {
						return;
					}
					
					ParticleEffect pe = (ParticleEffect)particle;
					
					targetHat.setParticle(particleIndex, pe);
					
					// Add this particle to our recents list
					core.getParticleManager().addParticleToRecents(ownerID, pe);
					
					ItemStack item = getItem(slot);
					ItemUtil.setItemType(item, pe.getItem());
					
					EditorLore.updateParticleDescription(item, targetHat, particleIndex);	
					modifiedParticles.put(particleIndex, true);
				});
				
				menuManager.addMenu(editorParticleSelectionMenu);
				editorParticleSelectionMenu.open();
			}
			
			else if (event.isRightClick())
			{
				editorMainMenu.onParticleEdit(getItem(slot), particleIndex);
			}
			
			return MenuClickResult.NEUTRAL;
		};
		
		for (int i = 0; i < 28; i++) {
			setAction(getNormalIndex(i, 10, 2), editAction);
		}
		
		String itemTitle = Message.EDITOR_PARTICLE_OVERVIEW_PARTICLE_NAME.getValue();
		int particlesSupported = targetHat.getType().getParticlesSupported();
		for (int i = 0; i < particlesSupported; i++)
		{
			ParticleEffect particle = targetHat.getParticle(i);
			ItemStack item = particle.getItem().clone();
			ItemUtil.setItemName(item, itemTitle.replace("{1}", Integer.toString(i + 1)));
			
			particles.put(i, particle);
			modifiedParticles.put(i, false);
			EditorLore.updateParticleDescription(item, targetHat, i);
			
			setItem(getNormalIndex(i, 10, 2), item);
		}
	}

	@Override
	public void onClose(boolean forced) 
	{
		Database database = core.getDatabase();
		String menuName = editorManager.getMenuName();
		
		for (Entry<Integer, Boolean> entry : modifiedParticles.entrySet())
		{
			if (entry.getValue()) {
				database.saveParticleData(menuName, targetHat, entry.getKey());
			}
		}
		
		for (int i = 0; i < targetHat.getType().getParticlesSupported(); i++)
		{
			if (targetHat.getParticleData(i).hasPropertyChanges()) {
				database.saveParticleData(menuName, targetHat, i);
			}
		}
	}

	@Override
	public void onTick(int ticks) {}

}
