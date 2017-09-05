package com.likeapig.elimination.commands;

import org.bukkit.entity.Player;

import com.likeapig.elimination.maps.Map;
import com.likeapig.elimination.maps.MapManager;
import com.likeapig.elimination.maps.MessageManager;
import com.likeapig.elimination.maps.MessageManager.MessageType;
import com.likeapig.elimination.particles.Rift;

public class Test extends Commands {

	public Test() {
		super("elimination.admin", "Test", "", new String[] { "t" });
	}

	@Override
	public void onCommand(Player sender, String[] args) {
		if (args.length == 0) {
			Rift.get().spawnRift(sender.getLocation());
		}
		if (args.length == 1) {

			if (args[0].equalsIgnoreCase("stop")) {
				Rift.get().removeItem();
				Rift.get().removeRift();
			}
		}

	}

}
