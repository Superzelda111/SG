package com.zanmc.survivalgames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.zanmc.survivalgames.SG;
import com.zanmc.survivalgames.handlers.Gamer;
import com.zanmc.survivalgames.handlers.PointSystem;
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
			for (Player pl : Bukkit.getOnlinePlayers()) {
				pl.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_THUNDER, 20, 1);
			}
			ChatUtil.broadcast("A tribute has fallen. " + Gamer.getAliveGamers().size() + "/" + Gamer.getGamers().size()
					+ " tributes remain");
		}

		if (Gamer.getAliveGamers().size() == 1) {
			for (Gamer ga : Gamer.getAliveGamers()) {
				SG.win(ga.getPlayer());
			}
		}
		PointSystem.addPoints(p, 50);
		if (SG.config.getBoolean("mysql.enabled")) {
			if (SG.playerDataContains(p.getUniqueId().toString())) {
				g.addDeath();
			}
		}
		PointSystem.save(p);
	}

	@EventHandler
	public void onDeath2(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player p = (Player) e.getEntity();
			Player d = (Player) e.getDamager();
			if (p.getHealth() - e.getDamage() < 1) {
				Gamer g = Gamer.getGamer(p);
				g.setAlive(false);
				p.setHealth(20);
				p.setGameMode(GameMode.SPECTATOR);

				ChatUtil.broadcast(p.getName() + " was killed by " + d.getName());
				for (Player pl : Bukkit.getOnlinePlayers()) {
					pl.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_THUNDER, 20, 1);
				}
				ChatUtil.broadcast("A tribute has fallen. " + Gamer.getAliveGamers().size() + "/"
						+ Gamer.getGamers().size() + " tributes remain");

				if (Gamer.getAliveGamers().size() == 1) {
					for (Gamer ga : Gamer.getAliveGamers()) {
						SG.win(ga.getPlayer());
					}
				}

				PointSystem.addPoints(p, SG.config.getInt("points.lose"));
				PointSystem.addPoints(d, SG.config.getInt("points.kill"));
				if (SG.config.getBoolean("mysql.enabled"))
					Gamer.getGamer(d).addKill();
			}
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.SURVIVAL)
			return;
		if (event.getBlock().getType() == Material.LEAVES) {
			event.setCancelled(true);
		}
		if (event.getBlock().getType() == Material.GLASS) {
			event.setCancelled(true);
		}
		if (event.getBlock().getType() == Material.THIN_GLASS) {
			event.setCancelled(true);
		}
	}

}