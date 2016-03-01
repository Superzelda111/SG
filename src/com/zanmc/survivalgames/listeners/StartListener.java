package com.zanmc.survivalgames.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class StartListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		Location from = e.getFrom();

		if (from.getZ() != e.getTo().getZ() && from.getX() != e.getTo().getX()) {
			player.teleport(e.getFrom());
		}
	}

	@EventHandler
	public void onMOTD(ServerListPingEvent e) {
		e.setMotd("Starting\nVIP-JOIN");
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		event.setCancelled(true);
	}
}
