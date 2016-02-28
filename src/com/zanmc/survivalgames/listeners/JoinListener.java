package com.zanmc.survivalgames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.zanmc.survivalgames.SG;
import com.zanmc.survivalgames.handlers.Gamer;
import com.zanmc.survivalgames.handlers.Map;
import com.zanmc.survivalgames.handlers.Title;
import com.zanmc.survivalgames.handlers.VoteHandler;

public class JoinListener implements Listener {

	@EventHandler
	public void onJoinPre(PlayerLoginEvent event) {
		if (event.getResult() == Result.KICK_FULL)
			event.setResult(Result.ALLOWED);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Gamer g = Gamer.getGamer(event.getPlayer());
		g.remove();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		FileConfiguration c = SG.config;
		World w = Bukkit.getWorld(c.getString("lobby.world"));
		double x = c.getDouble("lobby.x");
		double y = c.getDouble("lobby.y");
		double z = c.getDouble("lobby.z");
		float pitch = (float) c.getInt("lobby.pitch");
		float yaw = (float) c.getInt("lobby.yaw");
		Location loc = new Location(w, x, y, z, yaw, pitch);
		p.teleport(loc);
		p.setBedSpawnLocation(loc);

		SG.clearPlayer(p);
		p.setGameMode(GameMode.SURVIVAL);

		if (p.hasPermission("sg.admin")) {
			p.sendMessage("Joined as admin. Type /join to join the game");
		} else {
			Gamer g = Gamer.getGamer(p);
			System.out.println("Added " + g.getName() + " to gamers.");
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aVote: &8[&b/sg vote 1-6&8]"));

			for (Map map : Map.getVoteMaps()) {
				p.sendMessage(Map.getTempId(map) + " > " + map.getMapName() + " [" + VoteHandler.getVotesMap(map)
						+ " votes]");
			}

			p.sendMessage(ChatColor.AQUA + "" + Gamer.getGamers().size() + "/24" + ChatColor.GREEN
					+ " tributes waiting to play.");
		}

		new Title(((CraftPlayer) p).getHandle(), "&6Welcome to SG!", "&aDo &f/vote &ato vote for your map!", 20, 40, 20);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.SURVIVAL)
			event.setCancelled(true);
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.SURVIVAL)
			event.setCancelled(true);
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.SURVIVAL)
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.SURVIVAL)
			event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onTarget(EntityTargetEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.SURVIVAL)
			event.setCancelled(true);
	}

}
