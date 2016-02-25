package com.zanmc.survivalgames.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.zanmc.survivalgames.handlers.Map;

public class LocUtil {

	private static Location spawnLocation = new Location(Bukkit.getWorld("lobby"), 0.5, 64, 0.5);

	public static void teleportToSpawn(Player p) {
		p.teleport(spawnLocation);
	}

	public static void teleportAllToSpawn() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			teleportToSpawn(p);
		}
	}

	public static void teleportToGame(Player p, int i) {
		System.out.println(i);
		System.out.println(Map.getActiveMap().getMapName());
		p.teleport(Map.getActiveMap().getSpawn(i));
	}

}
