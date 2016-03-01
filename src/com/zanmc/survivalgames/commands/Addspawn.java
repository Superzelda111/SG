package com.zanmc.survivalgames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.zanmc.survivalgames.SettingsManager;

public class Addspawn implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player))
			return true;

		Player p = (Player) sender;
		if (p.hasPermission("sg.admin")) {
			if (args.length == 0) {
				p.sendMessage(ChatColor.RED + "Usage: /addspawn <filename> <index>");
			} else {
				FileConfiguration data = SettingsManager.getInstance().getData();
				data.set("arenas." + args[0] + ".spawns." + args[1] + ".x", p.getLocation().getX());
				data.set("arenas." + args[0] + ".spawns." + args[1] + ".y", p.getLocation().getY());
				data.set("arenas." + args[0] + ".spawns." + args[1] + ".z", p.getLocation().getZ());
				SettingsManager.getInstance().saveData();
				p.sendMessage("Spawn '" + args[1] + "' set at " + p.getLocation().getX() + " " + p.getLocation().getY()
						+ " " + p.getLocation().getZ());
			}

		} else {
			p.sendMessage(ChatColor.RED + "No permission");
			return true;
		}

		return false;
	}

}
