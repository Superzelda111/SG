package com.zanmc.survivalgames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.zanmc.survivalgames.handlers.Gamer;
import com.zanmc.survivalgames.utils.ChatUtil;

public class Join implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can join SG.");
			return true;
		}
		Player p = (Player) sender;
		
		Gamer g = Gamer.getGamer(p);
		System.out.println("Added "+g.getName() + " to gamers.");
		ChatUtil.sendMessage(g.getPlayer(), "Joined game.");

		return false;
	}

}
