package com.likeapig.elimination.commands;

import org.bukkit.entity.Player;

import com.likeapig.elimination.maps.Map;
import com.likeapig.elimination.maps.MapManager;
import com.likeapig.elimination.maps.MessageManager;
import com.likeapig.elimination.maps.MessageManager.MessageType;

public class Test extends Commands {

	public Test() {
		super("elimination.admin", "Test", "", new String[] { "t" });
	}

	@Override
	public void onCommand(Player sender, String[] args) {
		Map m = MapManager.get().getMap(sender);
		
		MessageManager.get().message(sender, "Votes: " + String.valueOf(m.getVotes()));
		
	}

}
