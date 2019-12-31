package com.mediusecho.particlehats.commands.subcommands;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.Particle;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.ParticleEffect;
import com.mediusecho.particlehats.particles.effects.SkinnableCapeEffect;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleTracking;
import com.mediusecho.particlehats.particles.properties.ParticleType;
import com.mediusecho.particlehats.permission.Permission;

public class DebugCommand extends Command {

	@Override
	public boolean execute(ParticleHats core, Sender sender, String label, ArrayList<String> args) 
	{		
		BufferedImage image = core.getDatabase().getImages(false).get("derpy_creeper");
		if (image == null) {
			sender.sendMessage("image is null");
		}
		
		Hat hat = new Hat();
		hat.setType(ParticleType.CUSTOM);
		
		SkinnableCapeEffect capeEffect = new SkinnableCapeEffect(image, "cape");
		capeEffect.build();
		
		hat.setCustomType(capeEffect);
		hat.setLocation(ParticleLocation.CHEST);
		hat.setParticle(0, ParticleEffect.REDSTONE);
		hat.getParticleData(0).getColorData().setColor(Color.WHITE);
		hat.setTrackingMethod(ParticleTracking.TRACK_BODY_ROTATION);
		
		core.getPlayerState(sender.getPlayer()).addHat(hat);
		
		return false;
	}

	@Override
	public String getName() {
		return "debug";
	}
	
	@Override
	public String getArgumentName () {
		return "debug";
	}

	@Override
	public Message getUsage() {
		return Message.UNKNOWN;
	}

	@Override
	public Message getDescription() {
		return Message.UNKNOWN;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_ALL;
	}
	
	@Override
	public boolean showInHelp() {
		return false;
	}
	
	@Override
	public boolean isPlayerOnly() {
		return true;
	}

}
