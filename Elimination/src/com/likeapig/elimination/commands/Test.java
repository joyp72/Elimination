package com.likeapig.elimination.commands;

import org.bukkit.entity.Player;

import com.likeapig.elimination.particles.Particles;

public class Test extends Commands {
	
	public Test() {
		super("build.admin", "Test", "", new String[] { "t" });
	}
	
	@Override
	public void onCommand(Player sender, String[] args) {
		if (args.length == 0) {
			Particles.get().playDeathCircle(sender.getLocation());
		}
	}

}
