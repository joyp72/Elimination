package com.likeapig.elimination.commands;

import org.bukkit.entity.Player;

import com.likeapig.elimination.maps.Map;
import com.likeapig.elimination.maps.MapManager;
import com.likeapig.elimination.maps.MessageManager;
import com.likeapig.elimination.maps.MessageManager.MessageType;

public class SetMaxPlayers extends Commands {
	
	public SetMaxPlayers() {
		super("elimination.admin", "Set max players", "<map>", new String[] { "smp" });
	}
	
	@Override
	public void onCommand(Player sender, String[] args) {
		if (args.length == 0) {
			MessageManager.get().message(sender, "You must specify a map!", MessageType.BAD);
			return;
		}
		String id = args[0];
		int i = Integer.parseInt(args[1]);
		Map m = MapManager.get().getMap(id);
		if (m == null ) {
			MessageManager.get().message(sender, "Unknown map.", MessageType.BAD);
			return;
		}
		m.setMaxPlayers(i);
		MessageManager.get().message(sender, "Set max players on " + m.getName() + " to: " + i, MessageType.GOOD);
	}

}
