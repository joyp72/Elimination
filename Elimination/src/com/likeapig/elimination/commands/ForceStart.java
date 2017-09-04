package com.likeapig.elimination.commands;

import org.bukkit.entity.Player;

import com.likeapig.elimination.maps.Map;
import com.likeapig.elimination.maps.MapManager;
import com.likeapig.elimination.maps.MessageManager;
import com.likeapig.elimination.maps.MessageManager.MessageType;

public class ForceStart extends Commands {
	
	public ForceStart() {
		super("elimination.admin", "Force starts a game", "", new String[] { "fs" });
	}
	
	@Override
	public void onCommand(Player sender, String[] args) {
		Map m = MapManager.get().getMap(sender);
		if (m == null) {
			MessageManager.get().message(sender, "You are not in a map.", MessageType.BAD);
			return;
		}
		if (m.isStarted()) {
			MessageManager.get().message(sender, "The game has already been started.", MessageType.BAD);
			return;
		}
		m.start();
		MessageManager.get().message(sender, "You forced the game to start!");
	}

}
