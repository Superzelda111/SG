package com.zanmc.survivalgames.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.zanmc.survivalgames.SG;
import com.zanmc.survivalgames.handlers.Gamer;
import com.zanmc.survivalgames.utils.ChatUtil;

public class IngameListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		p.setBedSpawnLocation(p.getLocation());
		Gamer g = Gamer.getGamer(p);
		g.setAlive(false);
		p.setHealth(20);
		p.setGameMode(GameMode.SPECTATOR);

		if (e.getDeathMessage().contains("hit the ground")) {
			e.setDeathMessage(ChatUtil.prefix() + p.getName() + "&bfell to his death");
			p.getWorld().strikeLightning(p.getLocation());
			ChatUtil.broadcast("A tribute has fallen. " + Gamer.getAliveGamers().size() + "/" + Gamer.getGamers().size()
					+ " tributes remain");
		}

		if (Gamer.getAliveGamers().size() == 1) {
			for (Gamer ga : Gamer.getAliveGamers()) {
				SG.win(ga.getPlayer());
			}
		}

	}
}