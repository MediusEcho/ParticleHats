package com.mediusecho.particlehats.editor.menus;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.ParticleColor;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorColorMenu extends EditorMenu {

	private Map<Integer, Color> colors;
	
	private final int particleIndex;
	private final EditorGenericCallback callback;
	private final EditorAction setColorAction;
	private final Hat targetHat;
	
	public EditorColorMenu(Core core, Player owner, MenuBuilder menuBuilder, int particleIndex, EditorGenericCallback callback) 
	{
		super(core, owner, menuBuilder);
		this.particleIndex = particleIndex;
		this.callback = callback;
		
		targetHat = menuBuilder.getTargetHat();
		colors = new HashMap<Integer, Color>();
		
		setColorAction = (event, slot) ->
		{
			if (colors.containsKey(slot)) 
			{
				ParticleColor color = targetHat.getParticleColor(particleIndex);
				color.setColor(colors.get(slot));
				color.setRandom(false);
				
				targetHat.setParticleColor(particleIndex, color);
				menuBuilder.goBack();
			}
			return EditorClickType.NEUTRAL;
		};
		
		inventory = Bukkit.createInventory(null, 54, Message.EDITOR_COLOR_MENU_TITLE.getValue());
		build();
	}
	
	@Override
	public void onClose (boolean forced)
	{
		if (!forced) {
			callback.onExecute();
		}
	}
	
	private void setColor (int slot, Color color) {
		colors.put(slot, color);
	}
	
	private String getColorDescription (Color color, String title)
	{
		String description = Message.EDITOR_COLOR_MENU_PRESET_DESCRIPTION.getValue();
		String s = description
				.replace("{1}", ChatColor.stripColor(title.toLowerCase()))
				.replace("{2}", Integer.toString(color.getRed()))
				.replace("{3}", Integer.toString(color.getGreen()))
				.replace("{4}", Integer.toString(color.getBlue()));
		return s;
	}
	
	private EditorClickType updateRGB (EditorClickEvent event, Hat hat, RGB rgb)
	{
		int normalClick = event.isLeftClick() ? 1 : -1;
		int shiftClick  = event.isShiftClick() ? 10 : 1;
		int modifier    = normalClick * shiftClick;
		
		ParticleColor pc = hat.getParticleColor(particleIndex);
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
		
		EditorLore.updateColorDescription(getItem(16), color, false, Message.EDITOR_COLOR_MENU_R_DESCRIPTION);
		EditorLore.updateColorDescription(getItem(25), color, false, Message.EDITOR_COLOR_MENU_G_DESCRIPTION);
		EditorLore.updateColorDescription(getItem(34), color, false, Message.EDITOR_COLOR_MENU_B_DESCRIPTION);
		
		hat.setParticleColor(particleIndex, color);
		return event.isLeftClick() ? EditorClickType.POSITIVE : EditorClickType.NEGATIVE;
	}
	
	private void setColor (int slot, Color color, String colorName)
	{
		String title = Message.fromString("EDITOR_COLOR_MENU_SET_" + colorName).getValue();
		Material material = ItemUtil.materialFromString(colorName + "_STAINED_GLASS_PANE", Material.BLACK_STAINED_GLASS_PANE);
		
		setColor(slot, color);
		setItem(slot, ItemUtil.createItem(material, title, StringUtil.parseDescription(getColorDescription(color, title))));
	}

	@Override
	protected void build() 
	{
		setButton(47, backButton, backAction);
		for (int i = 0; i < 15; i++) {
			setAction(getNormalIndex(i, 10, 4), setColorAction);
		}
		
		ParticleColor pc = targetHat.getParticleColor(particleIndex);
		Color color = pc.getColor();
		
		ItemStack redItem = ItemUtil.createItem(Material.ROSE_RED, Message.EDITOR_COLOR_MENU_SET_RED_VALUE);
		EditorLore.updateColorDescription(redItem, color, pc.isRandom(), Message.EDITOR_COLOR_MENU_R_DESCRIPTION);
		setButton(16, redItem, (event, slot) ->
		{
			return updateRGB(event, targetHat, RGB.R);
		});
		
		ItemStack greenItem = ItemUtil.createItem(Material.CACTUS_GREEN, Message.EDITOR_COLOR_MENU_SET_GREEN_VALUE);
		EditorLore.updateColorDescription(greenItem, color, pc.isRandom(), Message.EDITOR_COLOR_MENU_G_DESCRIPTION);
		setButton(25, greenItem, (event, slot) ->
		{
			return updateRGB(event, targetHat, RGB.G);
		});
		
		ItemStack blueItem = ItemUtil.createItem(Material.LAPIS_LAZULI, Message.EDITOR_COLOR_MENU_SET_BLUE_VALUE);
		EditorLore.updateColorDescription(blueItem, color, pc.isRandom(), Message.EDITOR_COLOR_MENU_B_DESCRIPTION);
		setButton(34, blueItem, (event, slot) ->
		{
			return updateRGB(event, targetHat, RGB.B);
		});
		
		ItemStack randomItem = ItemUtil.createItem(Material.EXPERIENCE_BOTTLE, Message.EDITOR_COLOR_MENU_SET_RANDOM, Message.EDITOR_COLOR_MENU_RANDOM_DESCRIPTION);
		setButton(51, randomItem, (event, slot) ->
		{
			targetHat.getParticleColor(particleIndex).setRandom(true);
			
			EditorLore.updateColorDescription(getItem(16), color, true, Message.EDITOR_COLOR_MENU_R_DESCRIPTION);
			EditorLore.updateColorDescription(getItem(25), color, true, Message.EDITOR_COLOR_MENU_G_DESCRIPTION);
			EditorLore.updateColorDescription(getItem(34), color, true, Message.EDITOR_COLOR_MENU_B_DESCRIPTION);
			
			return EditorClickType.NEUTRAL;
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

	private enum RGB
	{
		R,
		G,
		B;
	}
}
