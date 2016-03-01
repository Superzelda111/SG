package com.zanmc.survivalgames.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.entity.Player;

import com.zanmc.survivalgames.SG;
import com.zanmc.survivalgames.utils.DataPair;

import net.minecraft.server.v1_9_R1.ChunkProviderServer;
import net.minecraft.server.v1_9_R1.WorldServer;

public class GenerationHandler {
	
	/**
	 * 
	 * Code provided by NubeBuster, github.com/NubeBuster
	 * 
	 */

	public static void deleteWorld(String w) {
		File worldFolder = new File(w);
		if (!worldFolder.exists())
			worldFolder.mkdirs();
		clear(worldFolder);
	}

	private static List<DataPair> toGenerate = new ArrayList<DataPair>();
	private static int task;

	public static void generateChunks(String warld) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(SG.pl, new Runnable() {
			public void run() {
				int chunks1 = Bukkit.getViewDistance() + 3;
				int chunks2 = Bukkit.getViewDistance() + 32;

				int chunksDistance = Math.max(chunks1, chunks2);
				Chunk spawn = Bukkit.getWorld(warld).getSpawnLocation().getChunk();
				for (int x = -chunksDistance; x <= chunksDistance; x++) {
					for (int z = -chunksDistance; z <= chunksDistance; z++) {
						DataPair pair = new DataPair(spawn.getX() + x, spawn.getZ() + z);
						toGenerate.add(pair);
					}
				}
				final double totalChunks = toGenerate.size();
				task = Bukkit.getScheduler().scheduleSyncRepeatingTask(SG.pl, new Runnable() {
					private double chunksGenerated;
					private long lastLogged;

					public void run() {
						World world = Bukkit.getWorld(warld);
						if (this.lastLogged + 5000L < System.currentTimeMillis()) {
							System.out.print(
									"Generating: " + (int) Math.floor(this.chunksGenerated / totalChunks * 100D) + "%");
							this.lastLogged = System.currentTimeMillis();
						}
						long startedGeneration = System.currentTimeMillis();
						Iterator<DataPair> cordsItel = toGenerate.iterator();
						WorldServer nmsWorld;
						ChunkProviderServer nmsChunkProviderServer;
						int cx;
						while ((cordsItel.hasNext()) && (startedGeneration + 50L > System.currentTimeMillis())) {
							DataPair pair = (DataPair) cordsItel.next();

							if (!world.isChunkLoaded(pair.getX(), pair.getZ())) {
								world.loadChunk(pair.getX(), pair.getZ());
								nmsWorld = ((CraftWorld) world).getHandle();
								nmsChunkProviderServer = nmsWorld.getChunkProviderServer();
								for (cx = pair.getX() - 1; cx <= pair.getX() + 1; cx++)
									for (int cz = pair.getZ() - 1; cz <= pair.getZ() + 1; cz++)
										nmsChunkProviderServer.getChunkAt(cx, cz);
								nmsChunkProviderServer.getChunkAt(pair.getX(), pair.getZ());

								world.unloadChunk(pair.getX(), pair.getZ());
							}
							cordsItel.remove();
							this.chunksGenerated += 1D;
						}
						if (!cordsItel.hasNext()) {
							System.out.println("World is done generating");
							for (Player op : Bukkit.getOnlinePlayers())
								if (op.isOp())
									op.sendMessage(ChatColor.RED + "[NubesHG] The chunks are done generating.");
							Bukkit.getScheduler().cancelTask(task);
						}
					}
				}, 1L, 1L);
			}
		}, 0L);
	}

	private static void clear(File file) {
		if (!file.exists())
			return;
		if (file.isFile()) {
			file.delete();
		} else {
			for (File f : file.listFiles())
				clear(f);
			file.delete();
		}
	}
}
