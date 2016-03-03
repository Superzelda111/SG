package com.zanmc.survivalgames.handlers;

import java.util.HashMap;

import org.bukkit.entity.Player;

import com.zanmc.survivalgames.SG;
import com.zanmc.survivalgames.SettingsManager;

public class PointSystem {

	private static HashMap<String, Integer> points = new HashMap<String, Integer>();

	public static int getPoints(Player p) {
		return points.get(p.getUniqueId().toString());
	}

	public static boolean hasPoints(String uuid) {
		return points.containsKey(uuid);
	}

	public static void addPoints(Player p, int amount) {
		int old = points.get(p.getUniqueId().toString());
		points.put(p.getUniqueId().toString(), old + amount);
	}

	public static boolean removePoints(Player p, int amount) {
		int old = points.get(p.getUniqueId().toString());
		if (old - amount >= 0)
			return false;
		points.put(p.getUniqueId().toString(), old - amount);
		return true;
	}

	public static void setPoints(Player p, int amount) {
		points.put(p.getUniqueId().toString(), amount);
	}

	public static void save(Player p) {
		SG.data.set("users." + p.getUniqueId().toString() + ".points", points.get(p.getUniqueId().toString()));
		SettingsManager.getInstance().saveData();
	}

	public static boolean load(Player p) {
		if (SG.data.getString("users." + p.getUniqueId().toString() + ".points") != null) {
			points.put(p.getUniqueId().toString(), SG.data.getInt("users." + p.getUniqueId().toString() + ".points"));
			return true;
		} else {
			System.out.println("User does not exist in database.");
			return false;
		}

	}

}
