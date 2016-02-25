package com.zanmc.survivalgames.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

public class ChestHandler {

	private static List<ItemStack> materials = new ArrayList<ItemStack>();
	private static List<Location> openedChests = new ArrayList<Location>();

	public static void fillChests(Location loc) {
		if (openedChests.contains(loc))
			return;

	}

	public static void fill(World world) {
		materials.add(new ItemStack(Material.APPLE,3));
		materials.add(new ItemStack(Material.STONE_SWORD,1));
		materials.add(new ItemStack(Material.COOKED_BEEF,2));
		materials.add(new ItemStack(Material.IRON_AXE,1));
		materials.add(new ItemStack(Material.STICK,2));
		materials.add(new ItemStack(Material.DIAMOND,1));
		World w = world;
		for (Chunk c : w.getLoadedChunks()) {
			for (BlockState block : c.getTileEntities()) {
				if ((block instanceof Chest)) {
					Chest chest = (Chest) block;
					for(int i = 0; 8 > i; i++){ //or make items.size to how many max items you want to spawn in the chest
						
						//the rand(27) should be the size of the chest.
						}
				}
			}
		}
	}
}
