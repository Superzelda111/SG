package com.zanmc.survivalgames.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.zanmc.survivalgames.SG;

public class ChatHandler implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		int points = PointSystem.getPoints(e.getPlayer());
		String pointformat = "&a[&b" + points + "&a]&r";
		e.setFormat(SG.data.getString("settings.chat.format").replace("%points", pointformat)
				.replace("%name", e.getPlayer().getName()).replace("%msg", e.getMessage()));
	}
}