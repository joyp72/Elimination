package com.likeapig.elimination;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.likeapig.elimination.Menu.MenusListener;
import com.likeapig.elimination.commands.CommandsManager;
import com.likeapig.elimination.maps.Map;
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
		MenusListener.get().setup();
		
		if (Bukkit.getPluginManager().getPermission("elimination.default") == null) {
            Bukkit.getPluginManager().addPermission(new Permission("elimination.default"));
            Bukkit.getPluginManager().getPermission("elimination.default").setDefault(PermissionDefault.TRUE);
        }
		if (Bukkit.getPluginManager().getPermission("elimination.admin") == null) {
            Bukkit.getPluginManager().addPermission(new Permission("elimination.admin"));
            Bukkit.getPluginManager().getPermission("elimination.admin").setDefault(PermissionDefault.OP);
        }
	}

	public void onDisable() {
		this.getLogger().info("Disabled !");
		for (final Map m : MapManager.get().getMaps()) {
			m.kickAll(true);
		}
	}

}
