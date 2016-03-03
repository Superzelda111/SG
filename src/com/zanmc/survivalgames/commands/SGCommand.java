package com.zanmc.survivalgames.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.zanmc.survivalgames.SG;
import com.zanmc.survivalgames.utils.ChatUtil;

public class SGCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		sender.sendMessage(ChatUtil.prefix() + SG.pl.getDescription().getName() + " is running version "
				+ SG.pl.getDescription().getVersion() + " by "
				+ SG.pl.getDescription().getAuthors().toString().replace("[", "").replace("]", ""));

		return true;
	}

}
