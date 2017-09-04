package com.likeapig.elimination.maps;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.likeapig.elimination.Main;
import com.likeapig.elimination.Settings;
import com.likeapig.elimination.utils.LocationUtils;

public class MapManager {

	public static MapManager instance;
	private List<Map> maps;

	static {
		instance = new MapManager();
	}

	public static MapManager get() {
		return instance;
	}

	private MapManager() {
		maps = new ArrayList<Map>();
	}

	public void registerMap(String s) {
		if (getMap(s) == null) {
			Map m = new Map(s);
			maps.add(m);
		}
	}

	public void setupMaps() {
		maps.clear();
		if (Settings.get().get("maps") != null) {
			for (String s : Settings.get().getConfigSection().getKeys(false)) {
				try {
					registerMap(s);
				} catch (Exception ex) {
					Main.get().getLogger().info("Exception ocurred when loading map: " + s);
					ex.printStackTrace();
				}
			}
		}
	}

	public Map getMap(Player p) {
		for (Map m : maps) {
			if (m.containsAPlayer(p)) {
				return m;
			}
			if (m.containsBPlayer(p)) {
				return m;
			}
		}
		return null;
	}

	public Map getMap(String name) {
		for (Map m : maps) {
			if (m.getName().equalsIgnoreCase(name)) {
				return m;
			}
		}
		return null;
	}

	public void removeMap(Map m) {
		if (maps.contains(m)) {
			maps.remove(m);
			m.onRemoved();
		}
	}

	public List<Map> getMaps() {
		return maps;
	}

}
