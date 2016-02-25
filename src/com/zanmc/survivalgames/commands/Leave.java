package com.zanmc.survivalgames.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.zanmc.survivalgames.SG;

public class Leave implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can leave!");
			return true;
		}

		Player p = (Player) sender;

		FileConfiguration c = SG.config;
		World w = Bukkit.getWorld(c.getString("lobby.world"));
		double x = c.getDouble("lobby.x");
		double y = c.getDouble("lobby.y");
		double z = c.getDouble("lobby.z");
		float pitch = (float) c.getInt("lobby.pitch");
		float yaw = (float) c.getInt("lobby.yaw");
		Location loc = new Location(w, x, y, z, yaw, pitch);
		p.teleport(loc);

		return false;

	}

}
