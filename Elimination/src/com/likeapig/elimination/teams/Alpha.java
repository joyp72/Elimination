package com.likeapig.elimination.teams;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.likeapig.elimination.maps.Map;

public class Alpha {
	
	private UUID id;
	private Location loc;
	private Map map;
	
	public Alpha(Player p, Map m) {
		loc = p.getLocation();
		id = p.getUniqueId();
		map = m;
	}
	
	public void restore() {
		Player p = Bukkit.getPlayer(id);
		p.teleport(loc);
	}
	
	public Map getMap() {
		return map;
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(id);
	}
	
	public Location getLoc() {
		return loc;
	}

}
