package com.likeapig.elimination.teams;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.likeapig.elimination.maps.Map;

public class Alpha {

	private UUID id;
	private Location loc;
	private Map map;
	private GameMode gm;
	private ItemStack[] inv;
	private ItemStack[] armor;
	private int level;
	private float xp;
	private Location deathLoc;
	private boolean isDead;

	public Alpha(Player p, Map m) {
		loc = p.getLocation();
		id = p.getUniqueId();
		map = m;
		gm = p.getGameMode();
		inv = p.getInventory().getContents();
		armor = p.getInventory().getArmorContents();
		level = p.getLevel();
		xp = p.getExp();
	}

	public void ready() {
		Player p = Bukkit.getPlayer(id);
		if (p.getGameMode() != GameMode.SURVIVAL) {
			p.setGameMode(GameMode.SURVIVAL);
		}
		p.getInventory().clear();
		p.setLevel(0);
		p.setExp(0f);
	}

	public void restore() {
		Player p = Bukkit.getPlayer(id);
		p.teleport(loc);
		p.setGameMode(gm);
		p.getInventory().setContents(inv);
		p.getInventory().setArmorContents(armor);
		p.setLevel(level);
		p.setExp(xp);
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

	public void setDeathLoc(Location l) {
		deathLoc = l;
	}

	public Location getDeathLoc() {
		return deathLoc;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean b) {
		isDead = b;
	}

}
