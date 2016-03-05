package com.zanmc.survivalgames;

import java.io.File;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import com.zanmc.survivalgames.commands.Addmap;
import com.zanmc.survivalgames.commands.Addspawn;
import com.zanmc.survivalgames.commands.Editarena;
import com.zanmc.survivalgames.commands.ForceStart;
import com.zanmc.survivalgames.commands.Join;
import com.zanmc.survivalgames.commands.Leave;
import com.zanmc.survivalgames.commands.Lobby;
import com.zanmc.survivalgames.commands.Points;
import com.zanmc.survivalgames.commands.SGCommand;
import com.zanmc.survivalgames.commands.TPLoc;
import com.zanmc.survivalgames.commands.Vote;
import com.zanmc.survivalgames.handlers.ChatHandler;
import com.zanmc.survivalgames.handlers.Game;
import com.zanmc.survivalgames.handlers.Gamer;
import com.zanmc.survivalgames.handlers.Map;
import com.zanmc.survivalgames.handlers.PointSystem;
import com.zanmc.survivalgames.handlers.VoteHandler;
import com.zanmc.survivalgames.listeners.GraceListener;
import com.zanmc.survivalgames.listeners.IngameListener;
import com.zanmc.survivalgames.listeners.JoinListener;
import com.zanmc.survivalgames.listeners.OldCombat;
import com.zanmc.survivalgames.listeners.StartListener;
import com.zanmc.survivalgames.utils.ChatUtil;
import com.zanmc.survivalgames.utils.LocUtil;

public class SG extends JavaPlugin {

	public static int cdId;

	public static FileConfiguration config;
	public static FileConfiguration data = SettingsManager.getInstance().getData();

	public static int gamePID, PreGamePID, DMPID;
	public static int pretime, gametime, dmtime, dm;

	public static SG pl;

	public static Logger logger;
	public static ConsoleCommandSender clogger;

	@Override
	public void onEnable() {
		logger = getLogger();
		clogger = getServer().getConsoleSender();
		pl = this;
		configs();
		SettingsManager.getInstance().setup(this);
		registerCommands();
		registerPreEvents();
		pretime = getConfig().getInt("settings.pretime") * 60;
		dmtime = getConfig().getInt("settings.deathmatch") * 60;
		data = SettingsManager.getInstance().getData();

		clogger.sendMessage(ChatColor.RED + "---------------------------------------");
		clogger.sendMessage(ChatColor.GREEN + "Enabling SurvivalGames by "
				+ getDescription().getAuthors().toString().replace("[", "").replace("]", ""));
		clogger.sendMessage(ChatColor.RED + "---------------------------------------");

		if (data.getConfigurationSection("arenas") == null) {
			clogger.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4No arenas created!"));
		} else {
			clogger.sendMessage("Registered maps:");
			for (String maps : data.getConfigurationSection("arenas").getKeys(false)) {
				Map map = new Map(data.getString("arenas." + maps + ".name"), maps);
				clogger.sendMessage(map.getMapName());
				WorldCreator worldc = new WorldCreator(map.getFileName());
				World world = worldc.createWorld();
				logger.log(Level.INFO, "World '" + world.getName() + "' imported");
			}
			Random rand = new Random();

			if (Map.getAllMaps().size() >= 6) {
				clogger.sendMessage("Size is bigger than 6");
				for (int i = 0; i < 6; i++) {
					Map map = Map.getAllMaps().get(rand.nextInt(Map.getAllMaps().size()));
					Map.setTempId(map, i + 1);
					Map.setVoteMaps();
				}
			} else {
				clogger.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSize: " + Map.getAllMaps().size()));
				for (int i = 0; i < Map.getAllMaps().size(); i++) {
					Map map = Map.getAllMaps().get(i);
					Map.setTempId(map, i + 1);
					System.out.println(map.getMapName() + " : " + Map.getTempId(map));
				}
				Map.setVoteMaps();
			}
		}
		startPreGameCountdown();
	}

	private void registerCommands() {
		getCommand("addarena").setExecutor(new Addmap());
		getCommand("addspawn").setExecutor(new Addspawn());
		getCommand("vote").setExecutor(new Vote());
		getCommand("editarena").setExecutor(new Editarena());
		getCommand("savearena").setExecutor(new Editarena());
		getCommand("fstart").setExecutor(new ForceStart());
		getCommand("join").setExecutor(new Join());
		getCommand("leave").setExecutor(new Leave());
		getCommand("setlobby").setExecutor(new Lobby());
		getCommand("tploc").setExecutor(new TPLoc());
		getCommand("sg").setExecutor(new SGCommand());
		getCommand("points").setExecutor(new Points());

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new OldCombat(), this);
		pm.registerEvents(new ChatHandler(), this);
	}

	public static void registerGameEvents() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new IngameListener(), SG.pl);
	}

	private static Listener preListener;
	private static Listener startListener;
	private static Listener graceListener;

	private void registerPreEvents() {
		preListener = new JoinListener();
		Bukkit.getPluginManager().registerEvents(preListener, this);
	}

	public static void unRegisterPreEvents() {
		HandlerList.unregisterAll(preListener);
	}

	public static void registerStartEvents() {
		startListener = new StartListener();
		Bukkit.getPluginManager().registerEvents(startListener, SG.pl);
	}

	public static void unregisterStartEvents() {
		HandlerList.unregisterAll(startListener);
	}

	public static void registerGraceEvents() {
		graceListener = new GraceListener();
		Bukkit.getPluginManager().registerEvents(graceListener, SG.pl);
	}

	public static void unregisterGraceEvents() {
		HandlerList.unregisterAll(graceListener);
	}

	private void configs() {
		config = getConfig();
		saveDefaultConfig();
		if (config.getBoolean("rewriteconfig")) {
			File file = new File(getDataFolder() + File.separator + "config.yml");
			file.delete();
			saveDefaultConfig();
		}
	}

	public static void startPreGameCountdown() {
		PreGamePID = Bukkit.getScheduler().scheduleSyncRepeatingTask(SG.pl, new Runnable() {

			@Override
			public void run() {
				if (pretime % 60 == 0) {
					if (pretime == 60) {
						ChatUtil.broadcast("Game starting in " + pretime / 60 + " minute.");
					} else {
						ChatUtil.broadcast("Game starting in " + pretime / 60 + " minutes.");
					}

					ChatUtil.broadcast(ChatColor.translateAlternateColorCodes('&',
							"&6=============== &bSurvivalGames: &eVoting &6==============="));
					ChatUtil.broadcast("Vote: [/vote <id>]");
					for (Map map : Map.getVoteMaps()) {
						ChatUtil.broadcast(Map.getTempId(map) + " > " + map.getMapName() + " ["
								+ VoteHandler.getVotesMap(map) + " votes]");
					}
					ChatUtil.broadcast(ChatColor.translateAlternateColorCodes('&',
							"&6================================================="));
					Bukkit.broadcastMessage(ChatColor.AQUA + "" + Gamer.getGamers().size() + "/24" + ChatColor.GREEN
							+ " tributes waiting to play.");
				}
				if (pretime == 45 || pretime == 30 || pretime == 15 || (pretime >= 0 && pretime <= 10)) {
					ChatUtil.broadcast("Game starting in " + pretime + " seconds.");
				}
				if (pretime == 0) {
					Game.start();
				}
				pretime--;
			}
		}, 0, 20);
	}

	@SuppressWarnings("deprecation")
	public static void startGameTimer() {
		gamePID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(SG.pl, new Runnable() {
			int countdown = 10;
			int gracecountdown = 5;
			int dmcountdown = 10;

			@Override
			public void run() {
				if (gametime <= 15 && gametime > 5) {
					ChatUtil.broadcast("&cStarting in &4&l" + countdown + " &r&cseconds!");
					countdown--;
				}
				if (gametime == 16) {
					unregisterStartEvents();
					registerGameEvents();
					registerGraceEvents();
					ChatUtil.broadcast("&b&lThe game has started!");
					ChatUtil.broadcast("&eThere is a grace period for 15 seconds.");
				}
				if (gametime >= 21 && gametime < 26) {
					ChatUtil.broadcast("&eGrace period ending in &6&l" + gracecountdown + " &r&eseconds!");
					gracecountdown--;
				}
				if (gametime == 27) {
					unregisterGraceEvents();
					ChatUtil.broadcast("&6Grace period is over! &lFight&r&6!");
				}
				if (gametime == (dmtime / 60 - 10) * 60) {
					ChatUtil.broadcast("&cDeathmatch in &4&l10 &cminutes.");
				}
				if (gametime == (dmtime / 60 - 5) * 60) {
					ChatUtil.broadcast("&cDeathmatch in &4&l5 &cminutes.");
					Bukkit.getWorld(Map.getActiveMap().getFileName()).setTime(8000);
				}
				if (gametime == (dmtime / 60 - 1) * 60) {
					ChatUtil.broadcast("&cDeathmatch in &4&l1 &cminute.");
				}
				if (gametime == dmtime - 30) {
					Bukkit.getWorld(Map.getActiveMap().getFileName()).setTime(18000);
					ChatUtil.broadcast("&cTeleporting players to deathmatch. Waiting for players to load world.");
					registerStartEvents();
					int i = 0;
					for (Gamer pla : Gamer.getAliveGamers()) {
						if (i >= 24)
							i = 0;
						Player p = pla.getPlayer();
						System.out.println("Players: " + pla.getName());
						LocUtil.teleportToGame(p, i);
						p.setGameMode(GameMode.ADVENTURE);
						i++;
					}
				}

				if (gametime >= (dmtime - 10) && gametime < dmtime) {
					ChatUtil.broadcast("&cDeathmatch in &4&l" + dmcountdown + " &cseconds.");
					dmcountdown--;
				}
				if (gametime == dmtime) {
					Deathmatch();
				}

				gametime++;
			}

		}, 0, 20);
	}

	private static void Deathmatch() {
		unregisterStartEvents();
		registerGameEvents();
		startDeathmatchTimer();
	}

	private static void startDeathmatchTimer() {
		DMPID = Bukkit.getScheduler().scheduleSyncRepeatingTask(SG.pl, new Runnable() {

			@Override
			public void run() {
				if (dm == 0) {
					ChatUtil.broadcast("&cGame will end in &4&l5 &r&cminutes!");
					System.out.println("5 minutes till stop.");
				}
				if (dm == 1 * 60) {
					System.out.println("4 minutes till stop.");
				}
				if (dm == 2 * 60) {
					ChatUtil.broadcast("&cGame will end in &4&l3 &r&cminutes!");
					System.out.println("3 minutes till stop.");
					Bukkit.getWorld(Map.getActiveMap().getFileName()).setTime(18000);
				}
				if (dm == 4 * 60) {
					ChatUtil.broadcast("&cGame will end in &4&l1 &r&cminute!");
					System.out.println("1 minute till stop.");
				}
				if (dm == 5 * 60) {
					ChatUtil.broadcast("&cEnding game. No win due to multiple players left.");
				}
				if (dm == (5 * 60) + 5) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.kickPlayer(ChatColor.translateAlternateColorCodes('&', "&cNoone won!\n&cServer restarting"));
					}
					Bukkit.getServer().shutdown();
				}
				dm++;
			}
		}, 0, 20);
	}

	public static void clearPlayer(Player p) {
		p.getInventory().clear();
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
		p.setHealth(20);
		p.setFoodLevel(25);
		p.setFireTicks(0);
		p.setFallDistance(0.0F);
		p.setLevel(0);
		p.setExp(0);
		for (PotionEffect pe : p.getActivePotionEffects())
			p.removePotionEffect(pe.getType());
	}

	public static void win(Player p) {

		ChatUtil.broadcast("&6&l" + p.getName() + "&r won the SurvivalGames!");
		Bukkit.getScheduler().scheduleSyncDelayedTask(SG.pl, new Runnable() {

			@Override
			public void run() {
				PointSystem.addPoints(p, 200);
				for (Player pl : Bukkit.getOnlinePlayers()) {
					pl.kickPlayer(ChatColor.translateAlternateColorCodes('&',
							"&6&l" + p.getName() + " &r&6won!\n&cServer restarting."));
				}
				Bukkit.getServer().shutdown();
			}

		}, 20 * 10);

	}
}