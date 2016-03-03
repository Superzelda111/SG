package com.zanmc.survivalgames.listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.zanmc.survivalgames.handlers.PointSystem;

public class OldCombat implements Listener {

	@EventHandler
	public void enableOldPvp(PlayerJoinEvent event) {
		event.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED)
				.addModifier(new AttributeModifier("AttackSpeedRemover", 1.0, AttributeModifier.Operation.ADD_NUMBER));
	}

	@EventHandler
	public void reEnableForCompat(PlayerQuitEvent event) {
		event.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).removeModifier(
				new AttributeModifier("AttackSpeedRemover", 1.0, AttributeModifier.Operation.ADD_NUMBER));

		PointSystem.save(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onCreeperExplosion(EntityExplodeEvent event) {
		event.setCancelled(true);
	}

}
