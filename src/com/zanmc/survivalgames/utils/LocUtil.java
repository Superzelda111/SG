package com.zanmc.survivalgames.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.zanmc.survivalgames.SG;
import com.zanmc.survivalgames.handlers.Map;

public class LocUtil {

	static FileConfiguration config = SG.config;
	private static World world = Bukkit.getWorld(SG.pl.getConfig().getString("lobby.world"));
	private static double x = config.getDouble("lobby.x");
	private static double y = config.getDouble("lobby.y");
	private static double z = config.getDouble("lobby.z");
	private static float yaw = config.getInt("lobby.yaw");
	private static float pitch = config.getInt("lobby.pitch");

	private static Location spawnLocation = new Location(world, x, y, z, yaw, pitch);

	public static void teleportToLobby(Player p) {
		p.teleport(spawnLocation);
	}

	public static void teleportAllToSpawn() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			teleportToLobby(p);
		}
	}

	public static void teleportToGame(Player p, int i) {
		System.out.println(i);
		System.out.println(Map.getActiveMap().getMapName());
		p.teleport(Map.getActiveMap().getSpawn(i));
	}

}
