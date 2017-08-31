package com.likeapig.elimination;

import org.bukkit.plugin.java.JavaPlugin;

import com.likeapig.elimination.commands.CommandsManager;

public class Main extends JavaPlugin {
	
	public static Main instance;
	
	public static Main get() {
		return instance;
	}
	
	public void onEnable() {
		instance = this;
		getLogger().info("Elimination Enabled!");
		CommandsManager.get().setup();
	}

}
