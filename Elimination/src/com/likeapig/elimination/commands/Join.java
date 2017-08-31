package com.likeapig.elimination.commands;

import org.bukkit.entity.Player;

import com.likeapig.elimination.maps.Map;
import com.likeapig.elimination.maps.MapManager;
import com.likeapig.elimination.maps.MessageManager;
import com.likeapig.elimination.maps.MessageManager.MessageType;

public class Join extends Commands {

	public Join() {
		super("elimination.default", "Join a map.", "<map>", new String[] { "j" });
	}

	@Override
	public void onCommand(Player sender, final String[] args) {
		if (args.length == 0) {
			MessageManager.get().message(sender, "You must specify a map!", MessageType.BAD);
			return;
		}
		Map m = MapManager.get().getMap(sender);
		if (m != null) {
			MessageManager.get().message(sender, "You are already in a map!", MessageType.BAD);
			return;
		}
		String id = args[0];
		Map m2 = MapManager.get().getMap(id);
		if (m2 == null) {
			MessageManager.get().message(sender, "Unknown map.", MessageType.BAD);
			return;
		}
		if (m2.getASpawn() == null) {
			MessageManager.get().message(sender, "Alpha spawn not set.", MessageType.BAD);
			return;
		}
		if (m2.getBSpawn() == null) {
			MessageManager.get().message(sender, "Bravo spawn not set.", MessageType.BAD);
			return;
		}
		if (args.length == 1) {
			MessageManager.get().message(sender, "You must specify a team, Alpha or Bravo.", MessageType.BAD);
			return;
		}
		if (args[1].equalsIgnoreCase("alpha") || args[1].equalsIgnoreCase("a")) {
			if (m2.containsAPlayer(sender)) {
				MessageManager.get().message(sender, "You are already in a map!", MessageType.BAD);
				return;
			}
			if (m2.containsBPlayer(sender)) {
				MessageManager.get().message(sender, "You are already in a map!", MessageType.BAD);
				return;
			}
			m2.addAlphaPlayer(sender);
		}
		if (args[1].equalsIgnoreCase("bravo") || args[1].equalsIgnoreCase("b")) {
			if (m2.containsAPlayer(sender)) {
				MessageManager.get().message(sender, "You are already in a map!", MessageType.BAD);
				return;
			}
			if (m2.containsBPlayer(sender)) {
				MessageManager.get().message(sender, "You are already in a map!", MessageType.BAD);
				return;
			}
			m2.addBravoPlayer(sender);
		}
	}
}
