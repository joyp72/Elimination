package com.likeapig.elimination.maps;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.likeapig.elimination.maps.MessageManager.MessageType;
import com.likeapig.elimination.teams.Alpha;
import com.likeapig.elimination.teams.Bravo;

import net.md_5.bungee.api.ChatColor;

public class Map {

	private String name;
	private static List<Alpha> alpha;
	private static List<Bravo> bravo;
	private Location aLoc;
	private Location bLoc;

	public Map(String n) {
		name = n;
		alpha = new ArrayList<Alpha>();
		bravo = new ArrayList<Bravo>();

	}

	public void addAlphaPlayer(Player p) {
		if (!containsAPlayer(p) && getNumberOfAPlayers() <= 3) {
			Alpha a = new Alpha(p, this);
			alpha.add(a);
			p.teleport(aLoc);
			message(ChatColor.GREEN + p.getName() + " joined the Map.");
		}
	}

	public void addBravoPlayer(Player p) {
		if (!containsBPlayer(p) && getNumberOfBPlayers() <= 3) {
			Bravo b = new Bravo(p, this);
			bravo.add(b);
			p.teleport(bLoc);
			message(ChatColor.GREEN + p.getName() + " joined the game.");
		}
	}

	public void removeAlphaPlayer(Player p) {
		if (containsAPlayer(p)) {
			Alpha a = getAlpha(p);
			a.restore();
			alpha.remove(a);
		}
	}

	public void removeBravoPlayer(Player p) {
		if (containsBPlayer(p)) {
			Bravo b = getBravo(p);
			b.restore();
			bravo.remove(b);
		}
	}

	public void kickAll(boolean b) {
		for (Player p : getAPlayers()) {
			if (b) {
				kickPlayer(p);
			} else {
				removeAlphaPlayer(p);
			}
		}

		for (Player p : getBPlayers()) {
			if (b) {
				kickPlayer(p);
			} else {
				removeBravoPlayer(p);
			}
		}
	}

	public void kickPlayer(Player p) {
		if (containsAPlayer(p)) {
			removeAlphaPlayer(p);
		}
		if (containsBPlayer(p)) {
			removeBravoPlayer(p);
		}
		message(ChatColor.RED + p.getName() + " left the map.");
		MessageManager.get().message(p, "You left the map.", MessageType.BAD);
	}

	public void endRound() {

	}

	public void message(String msg) {
		for (Player p : getAPlayers()) {
			MessageManager.get().message(p, msg);
		}
		for (Player p : getBPlayers()) {
			MessageManager.get().message(p, msg);
		}
	}

	public int getNumberOfAPlayers() {
		return alpha.size();
	}

	public int getNumberOfBPlayers() {
		return bravo.size();
	}

	public List<Player> getAPlayers() {
		List<Player> players = new ArrayList<Player>();
		for (Alpha a : alpha) {
			players.add(a.getPlayer());
		}
		return players;
	}

	public List<Player> getBPlayers() {
		List<Player> players = new ArrayList<Player>();
		for (Bravo b : bravo) {
			players.add(b.getPlayer());
		}
		return players;
	}

	public static boolean containsAPlayer(Player p) {
		for (Alpha a : alpha) {
			if (a.getPlayer().equals(p)) {
				return true;
			}
		}
		return false;
	}

	public static boolean containsBPlayer(Player p) {
		for (Bravo b : bravo) {
			if (b.getPlayer().equals(p)) {
				return true;
			}
		}
		return false;
	}

	public static Alpha getAlpha(Player p) {
		for (Alpha a : alpha) {
			if (a.getPlayer().equals(p)) {
				return a;
			}
		}
		return null;
	}

	public static Bravo getBravo(Player p) {
		for (Bravo b : bravo) {
			if (b.getPlayer().equals(p)) {
				return b;
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public Map getMap() {
		return this;
	}

}
