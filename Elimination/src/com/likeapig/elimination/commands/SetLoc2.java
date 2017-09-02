package com.likeapig.elimination.commands;

import org.bukkit.entity.Player;

import com.likeapig.elimination.maps.Map;
import com.likeapig.elimination.maps.MapManager;
import com.likeapig.elimination.maps.MessageManager;
import com.likeapig.elimination.maps.MessageManager.MessageType;

public class SetLoc2 extends Commands {
	
	public SetLoc2() {
		super("elimination.admin", "Set loc2 of a map", "<map>", new String[] { "sl2" });
	}
	
	@Override
	public void onCommand(Player sender, String[] args) {
		if (args.length == 0) {
			MessageManager.get().message(sender, "You must specify a map!", MessageType.BAD);
			return;
		}
		String id = args[0];
		Map m = MapManager.get().getMap(id);
		if (m == null ) {
			MessageManager.get().message(sender, "Unknown map.", MessageType.BAD);
			return;
		}
		m.setLoc2(sender.getLocation());
		MessageManager.get().message(sender, "Loc2 set for: " + m.getName(), MessageType.GOOD);
	}

}
