package com.likeapig.elimination.maps;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_12_R1.ChatMessageType;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;

public class ActionBars {
	
	public static ActionBars instance;
	
	static {
		instance = new ActionBars();
	}
	
	public static ActionBars get() {
		return instance;
	}
	
	private ActionBars() {}
	
	public void addActionBar(Player p, String msg) {
		
		PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + msg + "\"}"), ChatMessageType.GAME_INFO);
		
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
		
	}

}
