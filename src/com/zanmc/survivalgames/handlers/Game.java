package com.zanmc.survivalgames.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.zanmc.survivalgames.GameState;
import com.zanmc.survivalgames.SG;
import com.zanmc.survivalgames.utils.ChatUtil;
import com.zanmc.survivalgames.utils.LocUtil;

public class Game {

	private static boolean canstart = false;
	private static boolean hasStarted = false;

	public static boolean canStart() {
		return canstart;
	}

	public static void setCanStart(boolean b) {
		canstart = b;
	}

	public static boolean hasStarted() {
		return hasStarted;
	}

	public static void start() {
		if (Gamer.getGamers().size() > 0) {
			Bukkit.getScheduler().cancelTask(SG.PreGamePID);
			SG.unRegisterPreEvents();
			SG.registerStartEvents();
			Map.setActiveMap(VoteHandler.getWithMostVotes());
			System.out.println("Active Map: " + Map.getActiveMap().getMapName());
			hasStarted = true;
			GameState.setState(GameState.INGAME);
			
			ChestHandler.fillAllChests(Map.getActiveMap().getFileName());
			System.out.println("Filled chests with fun loot!");
			
			Bukkit.getWorld(Map.getActiveMap().getFileName()).setTime(0);
			
			int i = 0;
			for (Gamer pla : Gamer.getGamers()) {
				pla.setAlive(true);
				if (i >= 24)
					i = 0;
				Player p = pla.getPlayer();
				SG.clearPlayer(p);
				System.out.println("Players: " + pla.getName());
				LocUtil.teleportToGame(p, i);
				p.setGameMode(GameMode.ADVENTURE);
				ChatUtil.sendMessage(p, "The game has started! You have been given the ability: "); // TODO:
																									// Abilities
				SG.startGameTimer();
			}
		} else {
			ChatUtil.broadcast("Not enough players to start game!");
			Bukkit.getScheduler().cancelTask(SG.PreGamePID);
			SG.startPreGameCountdown();
		}

		/*
		 * for(Player p : Bukkit.getOnlinePlayers()){ if(i <
		 * Bukkit.getOnlinePlayers().size() / 2){
		 * Team.addToTeam(TeamType.TEAMONE, p); } else {
		 * Team.addToTeam(TeamType.TEAMTWO, p); } i++; ChatUtil.sendMessage(p,
		 * "The game has started! You have been given the ability: "); // TODO:
		 * Abilities }
		 */
	}

	public static void stop() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.kickPlayer(ChatColor.RED + "Server restarting");
		}
	}

}
