package com.mediusecho.particlehats.editor.menus;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.compatibility.CompatibleMaterial;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.ColorData;
import com.mediusecho.particlehats.particles.properties.ParticleData;
import com.mediusecho.particlehats.ui.menus.SingularMenu;
import com.mediusecho.particlehats.ui.properties.MenuClickEvent;
import com.mediusecho.particlehats.ui.properties.MenuClickResult;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorColorMenu extends SingularMenu {

	private final int particleIndex;
	private final Hat targetHat;
	private final MenuCallback callback;
	private final MenuAction setColorAction;
	
	private Map<Integer, Color> colors;
	
	public EditorColorMenu(ParticleHats core, EditorMenuManager menuManager, Player owner, int particleIndex, MenuCallback callback) 
	{
		super(core, menuManager, owner);
		
		this.particleIndex = particleIndex;
		this.targetHat = menuManager.getTargetHat();
		this.callback = callback;
		this.colors = new HashMap<Integer, Color>();
		this.inventory = Bukkit.createInventory(null, 54, Message.EDITOR_COLOUR_MENU_TITLE.getValue());
		
		this.setColorAction = (event, slot) ->
		{
			if (colors.containsKey(slot))
			{
				ColorData colorData = targetHat.getParticleData(particleIndex).getColorData();
				colorData.setColor(colors.get(slot));
				
				menuManager.closeCurrentMenu();
			}
			return MenuClickResult.NEUTRAL;
		};
		
		build();
	}

	@Override
	protected void build() 
	{
		setButton(47, backButtonItem, backButtonAction);
		for (int i = 0; i < 15; i++) {
			setAction(getNormalIndex(i, 10, 4), setColorAction);
		}
		
		ColorData colorData = targetHat.getParticleData(particleIndex).getColorData();
		Color color = colorData.getColor();
		
		ItemStack redItem = ItemUtil.createItem(CompatibleMaterial.ROSE_RED, Message.EDITOR_COLOUR_MENU_SET_RED_VALUE);
		EditorLore.updateColorDescription(redItem, color, colorData.isRandom(), Message.EDITOR_COLOUR_MENU_R_DESCRIPTION);
		setButton(16, redItem, (event, slot) ->
		{
			return updateRGB(event, targetHat, RGB.R);
		});
		
		ItemStack greenItem = ItemUtil.createItem(CompatibleMaterial.CACTUS_GREEN, Message.EDITOR_COLOUR_MENU_SET_GREEN_VALUE);
		EditorLore.updateColorDescription(greenItem, color, colorData.isRandom(), Message.EDITOR_COLOUR_MENU_G_DESCRIPTION);
		setButton(25, greenItem, (event, slot) ->
		{
			return updateRGB(event, targetHat, RGB.G);
		});
		
		ItemStack blueItem = ItemUtil.createItem(CompatibleMaterial.LAPIS_LAZULI, Message.EDITOR_COLOUR_MENU_SET_BLUE_VALUE);
		EditorLore.updateColorDescription(blueItem, color, colorData.isRandom(), Message.EDITOR_COLOUR_MENU_B_DESCRIPTION);
		setButton(34, blueItem, (event, slot) ->
		{
			return updateRGB(event, targetHat, RGB.B);
		});
		
		// Only enable particle scaling in 1.13+, 1.12- does not support the feature
		if (ParticleHats.serverVersion >= 13)
		{
			ItemStack sizeItem = ItemUtil.createItem(CompatibleMaterial.REPEATER, Message.EDITOR_COLOUR_MENU_SET_SIZE);
			EditorLore.updateDoubleDescription(sizeItem, targetHat.getParticleData(particleIndex).getScale(), Message.EDITOR_COLOUR_MENU_SIZE_DESCRIPTION);
			setButton(50, sizeItem, (event, slot) ->
			{
				double normal = event.isLeftClick() ? 0.1 : -0.1;
				double shift = event.isShiftClick() ? 0.1 : 1;
				double modifier = normal * shift;
				
				ParticleData data = targetHat.getParticleData(particleIndex);
				double size = data.getScale() + modifier;
				
				data.setScale(size);
				EditorLore.updateDoubleDescription(getItem(50), data.getScale(), Message.EDITOR_COLOUR_MENU_SIZE_DESCRIPTION);
				
				return event.isLeftClick() ? MenuClickResult.POSITIVE : MenuClickResult.NEGATIVE;
			});
		}
		
		ItemStack randomItem = ItemUtil.createItem(CompatibleMaterial.EXPERIENCE_BOTTLE, Message.EDITOR_COLOUR_MENU_SET_RANDOM, Message.EDITOR_COLOUR_MENU_RANDOM_DESCRIPTION);
		setButton(51, randomItem, (event, slot) ->
		{
			targetHat.getParticleData(particleIndex).getColorData().setRandom(true);
			
			EditorLore.updateColorDescription(getItem(16), color, true, Message.EDITOR_COLOUR_MENU_R_DESCRIPTION);
			EditorLore.updateColorDescription(getItem(25), color, true, Message.EDITOR_COLOUR_MENU_G_DESCRIPTION);
			EditorLore.updateColorDescription(getItem(34), color, true, Message.EDITOR_COLOUR_MENU_B_DESCRIPTION);
			
			return MenuClickResult.NEUTRAL;
		});
		
		setColor(10, Color.fromRGB(255, 255, 255), "WHITE");
		setColor(11, Color.fromRGB(255, 0, 0), "RED");
		setColor(12, Color.fromRGB(191, 255, 0), "LIME");
		setColor(13, Color.fromRGB(173, 216, 230), "LIGHT_BLUE");
		setColor(14, Color.fromRGB(255, 192, 203), "PINK");
		
		setColor(19, Color.fromRGB(190, 190, 190), "GRAY");
		setColor(20, Color.fromRGB(255, 140, 0), "ORANGE");
		setColor(21, Color.fromRGB(0, 255, 0), "GREEN");
		setColor(22, Color.fromRGB(0, 0, 255), "BLUE");
		setColor(23, Color.fromRGB(255, 0, 255), "MAGENTA");
		
		setColor(28, Color.fromRGB(0, 0, 0), "BLACK");
		setColor(29, Color.fromRGB(255, 255, 0), "YELLOW");
		setColor(30, Color.fromRGB(165, 42, 42), "BROWN");
		setColor(31, Color.fromRGB(160, 32, 219), "PURPLE");
		setColor(32, Color.fromRGB(0, 255, 255), "CYAN");
	}

	@Override
	public void onClose(boolean forced) 
	{
		if (!forced) {
			callback.onCallback();
		}
	}

	@Override
	public void onTick(int ticks) {}
	
	private void setColor (int slot, Color color, String colorName)
	{
		String title = Message.fromString("EDITOR_COLOUR_MENU_SET_" + colorName).getValue();
		CompatibleMaterial material = CompatibleMaterial.fromName(colorName + "_STAINED_GLASS_PANE", CompatibleMaterial.BLACK_STAINED_GLASS_PANE);
		
		setColor(slot, color);
		setItem(slot, ItemUtil.createItem(material, title, StringUtil.parseDescription(getColorDescription(color, title))));
	}
	
	private void setColor (int slot, Color color) {
		colors.put(slot, color);
	}
	
	private String getColorDescription (Color color, String title)
	{
		String description = Message.EDITOR_COLOUR_MENU_PRESET_DESCRIPTION.getValue();
		String s = description
				.replace("{1}", ChatColor.stripColor(title.toLowerCase()))
				.replace("{2}", Integer.toString(color.getRed()))
				.replace("{3}", Integer.toString(color.getGreen()))
				.replace("{4}", Integer.toString(color.getBlue()));
		return s;
	}
	
	private MenuClickResult updateRGB (MenuClickEvent event, Hat hat, RGB rgb)
	{
		int normalClick = event.isLeftClick() ? 1 : -1;
		int shiftClick  = event.isShiftClick() ? 10 : 1;
		int modifier    = normalClick * shiftClick;
		
		ColorData pc = targetHat.getParticleData(particleIndex).getColorData();
		Color color = pc.getStoredColor();
		
		pc.setRandom(false);
		
		switch (rgb)
		{
		case R:
			int r = MathUtil.clamp(color.getRed() + modifier, 0, 255);
			color = color.setRed(r);
			break;
		case G:
			int g = MathUtil.clamp(color.getGreen() + modifier, 0, 255);
			color = color.setGreen(g);
			break;
		case B:
			int b = MathUtil.clamp(color.getBlue() + modifier, 0, 255);
			color = color.setBlue(b);
			break;
		}
		
		EditorLore.updateColorDescription(getItem(16), color, false, Message.EDITOR_COLOUR_MENU_R_DESCRIPTION);
		EditorLore.updateColorDescription(getItem(25), color, false, Message.EDITOR_COLOUR_MENU_G_DESCRIPTION);
		EditorLore.updateColorDescription(getItem(34), color, false, Message.EDITOR_COLOUR_MENU_B_DESCRIPTION);
		
		hat.getParticleData(particleIndex).getColorData().setColor(color);
		return event.isLeftClick() ? MenuClickResult.POSITIVE : MenuClickResult.NEGATIVE;
	}
	
	private enum RGB
	{
		R,
		G,
		B;
	}

}
