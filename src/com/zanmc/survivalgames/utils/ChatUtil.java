package com.zanmc.survivalgames.utils;

import static org.bukkit.ChatColor.DARK_GRAY;
import static org.bukkit.ChatColor.WHITE;
import static org.bukkit.ChatColor.YELLOW;

import org.bukkit.entity.Player;

import com.zanmc.survivalgames.handlers.Gamer;

import net.md_5.bungee.api.ChatColor;

public class ChatUtil {
	
	public static void broadcast(String msg) {
		for(Gamer g : Gamer.getGamers()){
			g.getPlayer().sendMessage(prefix() + ChatColor.translateAlternateColorCodes('&', msg));
		}
}

public static void sendMessage(Player p, String msg){
	p.sendMessage(prefix() + msg);
}

public static String prefix() {
	return DARK_GRAY + "[" + YELLOW + "SG" + DARK_GRAY + "] " + WHITE;
}

}
