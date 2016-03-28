package com.zanmc.survivalgames;

import java.io.DataOutputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.output.ByteArrayOutputStream;
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
import com.zanmc.survivalgames.commands.ForceDM;
import com.zanmc.survivalgames.commands.ForceStart;
import com.zanmc.survivalgames.commands.Join;
import com.zanmc.survivalgames.commands.Leave;
import com.zanmc.survivalgames.commands.Lobby;
import com.zanmc.survivalgames.commands.Points;
import com.zanmc.survivalgames.commands.SGCommand;
import com.zanmc.survivalgames.commands.TPLoc;
import com.zanmc.survivalgames.commands.Vote;
import com.zanmc.survivalgames.handlers.ChatHandler;
import com.zanmc.survivalgames.handlers.ChestHandler;
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
import com.zanmc.survivalgames.utils.ResetMap;

import net.minecraft.server.v1_9_R1.MinecraftServer;

public class SG extends JavaPlugin {

	public static int cdId;

	public static Connection connection;

	public static FileConfiguration config;
	public static FileConfiguration data = SettingsManager.getInstance().getData();
	public static ConsoleCommandSender cmd = Bukkit.getConsoleSender();

	public static int gamePID, PreGamePID, DMPID;
	public static int pretime, gametime, dmtime, dm;

	public static SG pl;

	public static Logger logger;
	public static ConsoleCommandSender clogger;

	@Override
	public void onLoad() {
		configs();
		SettingsManager.getInstance().setup(this);

		String lastmap = config.getString("lastmap");
		if (lastmap == null) {
			cmd.sendMessage("No maps played yet.");
		} else {
			cmd.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eRolling back last map: " + lastmap));
			ResetMap.rollback(lastmap, this);
		}
	}

	@Override
	public void onEnable() {
		logger = getLogger();
		clogger = getServer().getConsoleSender();
		pl = this;
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
		if (config.getBoolean("mysql.enabled")) {
			openConnection();
		}
		startPreGameCountdown();
	}

	@Override
	public void onDisable() {
		if (config.getBoolean("mysql.enabled")) {
			try {
				if (connection != null && connection.isClosed())
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (config.getBoolean("bungeecord")) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				sendToServer(p, config.getString("lobbyserver"));
			}
		}
	}

	// Bungeecord
	public static void sendToServer(Player p, String server) {
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);

			out.writeUTF("Connect");
			out.writeUTF(server);
			p.sendPluginMessage(pl, "BungeeCord", b.toByteArray());

			out.close();
		} catch (Exception er) {
			er.printStackTrace();
		}
	}

	// MYSQL
	public synchronized static void openConnection() {
		try {
			connection = DriverManager.getConnection(
					"jdbc:mysql://" + config.getString("mysql.host") + ":" + config.getString("mysql.port") + "/"
							+ config.getString("mysql.database"),
					config.getString("mysql.username"), config.getString("mysql.password"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static void closeConnection() {
		try {
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static boolean playerDataContains(String uuid) {
		try {
			PreparedStatement sql = connection.prepareStatement("select * from users where uuid=?");
			sql.setString(1, uuid);
			ResultSet result = sql.executeQuery();
			boolean contains = result.next();

			sql.close();
			return contains;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void registerCommands() {
		getCommand("addarena").setExecutor(new Addmap());
		getCommand("addspawn").setExecutor(new Addspawn());
		getCommand("vote").setExecutor(new Vote());
		getCommand("editarena").setExecutor(new Editarena());
		getCommand("savearena").setExecutor(new Editarena());
		getCommand("fstart").setExecutor(new ForceStart());
		getCommand("forcedm").setExecutor(new ForceDM());
		getCommand("join").setExecutor(new Join());
		getCommand("leave").setExecutor(new Leave());
		getCommand("setlobby").setExecutor(new Lobby());
		getCommand("tploc").setExecutor(new TPLoc());
		getCommand("sg").setExecutor(new SGCommand());
		getCommand("points").setExecutor(new Points());

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new OldCombat(), this);
		pm.registerEvents(new ChatHandler(), this);
		if (config.getBoolean("bungeecord"))
			getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	}

	public static void registerGameEvents() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new IngameListener(), SG.pl);
	}

	private static Listener preListener;
	private static Listener startListener;
	private static Listener graceListener;

	@SuppressWarnings("deprecation")
	private void registerPreEvents() {
		preListener = new JoinListener();
		MinecraftServer.getServer().setMotd(" " + SG.config.getString("settings.motd.lobby"));
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

	public static void startGameTimer() {
		gamePID = Bukkit.getScheduler().scheduleSyncRepeatingTask(SG.pl, new Runnable() {
			int countdown = 10;
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
					ChatUtil.broadcast("&eThere is a grace period for 5 seconds.");
				}
				if (gametime == 22) {
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
					ChestHandler.fillAllChests(Map.getActiveMap().getFileName());
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
						if (config.getBoolean("bungeecord"))
							sendToServer(p, config.getString("lobbyserver"));
						else
							p.kickPlayer(
									ChatColor.translateAlternateColorCodes('&', "&cNoone won!\n&cServer restarting"));
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
				Gamer g = Gamer.getGamer(p);
				if (config.getBoolean("mysql.enabled"))
					g.addWin();

				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (config.getBoolean("bungeecord"))
						sendToServer(p, config.getString("lobbyserver"));
					else
						pl.kickPlayer(ChatColor.translateAlternateColorCodes('&',
								"&6&l" + p.getName() + " &r&6won!\n&cServer restarting."));
				}
				Bukkit.getServer().shutdown();
			}
		}, 20 * 10);
	}
}