package com.likeapig.elimination;

import org.bukkit.plugin.java.JavaPlugin;

import com.likeapig.elimination.commands.CommandsManager;
import com.likeapig.elimination.maps.MapListener;
import com.likeapig.elimination.maps.MapManager;

public class Main extends JavaPlugin {
	
	public static Main instance;
	
	public static Main get() {
		return instance;
	}
	
	public void onEnable() {
		instance = this;
		getLogger().info("Elimination Enabled!");
		CommandsManager.get().setup();
		Settings.get().setup(this);
		MapManager.get().setupMaps();
		MapListener.get().setup();
	}

}
