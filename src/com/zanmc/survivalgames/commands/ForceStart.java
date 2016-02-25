package com.zanmc.survivalgames.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.zanmc.survivalgames.handlers.Game;
import com.zanmc.survivalgames.handlers.VoteHandler;

public class ForceStart implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Game.start();
		sender.sendMessage("Map " + VoteHandler.getWithMostVotes().getMapName() + " selected");

		return false;
	}

}
