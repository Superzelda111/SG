package com.zanmc.survivalgames.handlers;

import org.bukkit.ChatColor;

import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_9_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_9_R1.PacketPlayOutTitle.EnumTitleAction;

public class Title {

	private PacketPlayOutTitle title(String text, int fadein, int duration, int fadeout) {
		return new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{'text':'" + text + "'}"), fadein,
				duration, fadeout);
	}

	private PacketPlayOutTitle subtitle(String text, int fadein, int duration, int fadeout) {
		return new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{'text':'" + text + "'}"), fadein,
				duration, fadeout);
	}

	public Title(EntityPlayer p, String title, String subtitle, int fadein, int duration, int fadeout) {
		p.playerConnection
				.sendPacket(this.title(ChatColor.translateAlternateColorCodes('&', title), fadein, duration, fadeout));
		p.playerConnection.sendPacket(
				this.subtitle(ChatColor.translateAlternateColorCodes('&', subtitle), fadein, duration, fadeout));
	}
}
