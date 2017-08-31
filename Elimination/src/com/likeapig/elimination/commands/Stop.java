package com.likeapig.elimination.commands;

import org.bukkit.entity.Player;

import com.likeapig.elimination.maps.Map;
import com.likeapig.elimination.maps.MapManager;
import com.likeapig.elimination.maps.MessageManager;
import com.likeapig.elimination.maps.MessageManager.MessageType;

public class Stop extends Commands {
	
	public Stop() {
		super("elimination.default", "Stop a game", "", new String[] { "" });
	}
	
	@Override
	public void onCommand(Player sender, String[] args) {
		Map m = MapManager.get().getMap(sender);
		if (m == null) {
			MessageManager.get().message(sender, "You are not in an arena.", MessageType.BAD);
			return;
		}
		if (!m.isStarted()) {
			MessageManager.get().message(sender, "The game has not started yet.", MessageType.BAD);
			return;
		}
		m.endRound();
		MessageManager.get().message(sender, "Round ended.");
	}
}
