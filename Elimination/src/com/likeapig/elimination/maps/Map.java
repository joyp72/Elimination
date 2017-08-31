package com.likeapig.elimination.maps;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.likeapig.elimination.maps.MessageManager.MessageType;
import com.likeapig.elimination.teams.Alpha;
import com.likeapig.elimination.teams.Bravo;

import net.md_5.bungee.api.ChatColor;

public class Map {

	private String name;
	private static List<Alpha> alpha;
	private static List<Bravo> bravo;
	private static List<Player> aDead;
	private static List<Player> bDead;
	private int aWins;
	private int bWins;
	private Location aLoc;
	private Location bLoc;
	private MapState state;

	public Map(String n) {
		name = n;
		aWins = 0;
		bWins = 0;
		alpha = new ArrayList<Alpha>();
		bravo = new ArrayList<Bravo>();
		aDead = new ArrayList<Player>();
		bDead = new ArrayList<Player>();
		checkState();

	}

	public void endRound() {
		message(ChatColor.RED + "The round has ended!");
		startNewRound();
	}
	
	public void handleDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if (isStarted()) {
			if (containsAPlayer(p)) {
				onADeath(p);
			}
			if (containsBPlayer(p)) {
				onBDeath(p);
			}
			if (aDead.size() >= 3) {
				onEliminated(1);
			}
			if (bDead.size() >= 3) {
				onEliminated(2);
			}
		}
	}
	
	public void onEliminated(int i) {
		if (i == 1) {
			bWins++;
			message("Alpha team has been eliminated!");
			endRound();
			return;
		}
		if (i == 2) {
			aWins++;
			message("Bravo team has been eliminated!");
			endRound();
			return;
		}
	}

	public void startNewRound() {
		List<Alpha> aWinners = new ArrayList<Alpha>();
		List<Bravo> bWinners = new ArrayList<Bravo>();
		int won = 0;
		boolean flag = aWins + bWins == 0;
		if (aWins >= 5 || bWins >= 5) {
			if (aWins >= 5) {
				won = 1;
				for (Alpha a : alpha) {
					if (aWinners.isEmpty()) {
						aWinners.add(a);
					}
				}
			}
			if (bWins >= 5) {
				won = 2;
				for (Bravo b : bravo) {
					if (bWinners.isEmpty()) {
						bWinners.add(b);
					}
				}
			}
			message(ChatColor.RED + "GAME OVER");
			if (won == 1) {
				if (aWinners.size() > 1) {
					String s = ChatColor.GOLD + "Winners: ";
					for (Alpha a : aWinners) {
						if (s.equalsIgnoreCase(ChatColor.GOLD + "Winners: ")) {
							s = String.valueOf(s) + a.getPlayer().getName();
						} else {
							s = String.valueOf(s) + ", " + a.getPlayer().getName();
						}
					}
					message(s);
					stop();
					return;
				} else {
					message(ChatColor.GOLD + "Winner: " + aWinners.get(0).getPlayer().getName());
					stop();
					return;
				}
			}
			if (won == 2) {
				if (bWinners.size() > 1) {
					String s = ChatColor.GOLD + "Winners: ";
					for (Bravo b : bWinners) {
						if (s.equalsIgnoreCase(ChatColor.GOLD + "Winners: ")) {
							s = String.valueOf(s) + b.getPlayer().getName();
						} else {
							s = String.valueOf(s) + ", " + b.getPlayer().getName();
						}
					}
					message(s);
					stop();
					return;
				} else {
					message(ChatColor.GOLD + "Winner: " + aWinners.get(0).getPlayer().getName());
					stop();
					return;
				}
			}
		}
		if (flag) {
			message(ChatColor.GOLD + "Game has Started!");
		} else {
			message(ChatColor.GOLD + "Round has Started!");
		}
		teleportAPlayers();
		teleportBPlayers();
	}

	public void stop() {
		setState(MapState.WAITING);
		kickAll(true);
	}

	public void start() {
		setState(MapState.STARTED);
		startNewRound();
	}

	public void teleportAPlayers() {
		for (Player p : getAPlayers()) {
			p.teleport(aLoc);
		}
	}

	public void teleportBPlayers() {
		for (Player p : getBPlayers()) {
			p.teleport(bLoc);
		}
	}

	public void onADeath(Player p) {
		if (containsAPlayer(p)) {
			if (!aDead.contains(p)) {
				aDead.add(p);
			}
		}
	}

	public void onBDeath(Player p) {
		if (containsBPlayer(p)) {
			if (!bDead.contains(p)) {
				bDead.add(p);
			}
		}
	}

	public void checkState() {
		boolean flag = false;
		if (aLoc == null) {
			flag = true;
		}
		if (bLoc == null) {
			flag = true;
		}
		if (flag) {
			setState(MapState.STOPPED);
			return;
		}
		setState(MapState.WAITING);
	}

	public void setState(MapState m) {
		state = m;
	}

	public void addAlphaPlayer(Player p) {
		if (!containsAPlayer(p) && state.canJoin() && getNumberOfAPlayers() <= 3) {
			Alpha a = new Alpha(p, this);
			alpha.add(a);
			p.teleport(aLoc);
			message(ChatColor.GREEN + p.getName() + " joined the Map.");
			if (state.equals(MapState.WAITING) && getNumberOfAPlayers() == 3) {
				start();
			}
		}
	}

	public void addBravoPlayer(Player p) {
		if (!containsBPlayer(p) && getNumberOfBPlayers() <= 3) {
			Bravo b = new Bravo(p, this);
			bravo.add(b);
			p.teleport(bLoc);
			message(ChatColor.GREEN + p.getName() + " joined the game.");
			if (state.equals(MapState.WAITING) && getNumberOfBPlayers() == 3) {
				start();
			}
		}
	}

	public void removeAlphaPlayer(Player p) {
		if (containsAPlayer(p)) {
			Alpha a = getAlpha(p);
			a.restore();
			alpha.remove(a);
			if (state.equals(MapState.STARTED) && getNumberOfAPlayers() < 1) {
				stop();
				message(ChatColor.RED + "Players left, stopping game.");
			}
		}
	}

	public void removeBravoPlayer(Player p) {
		if (containsBPlayer(p)) {
			Bravo b = getBravo(p);
			b.restore();
			bravo.remove(b);
			if (state.equals(MapState.STARTED) && getNumberOfBPlayers() < 1) {
				stop();
				message(ChatColor.RED + "Players left, stopping game.");
			}
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

	public void addAWins() {
		aWins++;
	}

	public void addBWins() {
		bWins++;
	}

	public void resetWins() {
		if (aWins != 0) {
			aWins = 0;
		}
		if (bWins != 0) {
			bWins = 0;
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

	public Location getASpawn() {
		return aLoc;
	}

	public Location getBSpawn() {
		return bLoc;
	}

	public String getName() {
		return name;
	}

	public Map getMap() {
		return this;
	}
	
	public boolean isStarted() {
		return state.equals(MapState.STARTED);
	}

	public String getStateName() {
		return state.getName();
	}

	public MapState getState() {
		return state;
	}

	public enum MapState {
		WAITING("WAITING", 0, "WAITING", true), STARTING("STARTING", 1, "STARTING", true), STARTED("STARTED", 2,
				"STARTED", false), STOPPED("STOPPED", 3, "STOPPED", false);

		private boolean allowJoin;
		private String name;

		private MapState(final String s, final int n, final String name, final Boolean allowJoin) {
			this.allowJoin = allowJoin;
			this.name = name;
		}

		public boolean canJoin() {
			return this.allowJoin;
		}

		public String getName() {
			return this.name;
		}
	}

}
