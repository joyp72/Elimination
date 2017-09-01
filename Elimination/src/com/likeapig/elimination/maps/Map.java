package com.likeapig.elimination.maps;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.likeapig.elimination.Main;
import com.likeapig.elimination.Settings;
import com.likeapig.elimination.maps.MessageManager.MessageType;
import com.likeapig.elimination.scoreboard.ScoreBoard;
import com.likeapig.elimination.teams.Alpha;
import com.likeapig.elimination.teams.Bravo;
import com.likeapig.elimination.utils.LocationUtils;

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
		loadFromConfig();
		saveToConfig();
		checkState();

	}

	public void setALoc(Location l) {
		aLoc = l;
		checkState();
		saveToConfig();
	}

	public void setBLoc(Location l) {
		bLoc = l;
		checkState();
		saveToConfig();
	}

	public void saveToConfig() {
		if (aLoc != null) {
			Settings.get().set("maps." + getName() + ".aloc", LocationUtils.locationToString(aLoc));
		}
		if (bLoc != null) {
			Settings.get().set("maps." + getName() + ".bloc", LocationUtils.locationToString(bLoc));
		}
	}

	public void loadFromConfig() {
		Settings s = Settings.get();
		if (s.get("maps." + getName() + ".aloc") != null) {
			String s3 = s.get("maps." + getName() + ".aloc");
			aLoc = LocationUtils.stringToLocation(s3);
		}
		if (s.get("maps." + getName() + ".bloc") != null) {
			String s2 = s.get("maps." + getName() + ".bloc");
			bLoc = LocationUtils.stringToLocation(s2);
		}
	}

	public void endRound() {
		message(ChatColor.RED + "The round has ended!");
		aDead.clear();
		bDead.clear();
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.get(), new Runnable() {
			@Override
			public void run() {
				for (Alpha a : alpha) {
					a.ready();
					a.removeDeathCircle();
					a.setDead(false);
				}
				for (Bravo b : bravo) {
					b.ready();
					b.removeDeathCircle();
					b.setDead(false);
				}
			}
		}, 20L);
		startNewRound();
	}

	public void handleRevive(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		Map m = MapManager.get().getMap(p);
		if (isStarted()) {
			if (containsAPlayer(p)) {
				if (p.isSneaking()) {
					for (Alpha a : alpha) {
						if (a.isDead()) {
							Location l = a.getDeathLoc();
							if (p.getLocation().distance(l) <= 1  && !getAlpha(p).isDead()) {
								a.ready();
								a.setDead(false);
								a.getPlayer().teleport(l);
								a.removeDeathCircle();
								onARemoveDeath(a.getPlayer());
								updateBoard();
							}
						}
					}
				}
				if (containsBPlayer(p)) {
					for (Bravo b : bravo) {
						if (b.isDead()) {
							Location l = b.getDeathLoc();
							if (p.getLocation().distance(l) <= 1  && !getBravo(p).isDead()) {
								b.ready();
								b.setDead(false);
								b.getPlayer().teleport(l);
								b.removeDeathCircle();
								onBRemoveDeath(b.getPlayer());
								updateBoard();
							}
						}
					}
				}
			}
		}
	}

	public void handleRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		if (isStarted()) {
			if (containsAPlayer(p)) {
				Alpha a = getAlpha(p);
				p.teleport(a.getDeathLoc());
			}
			if (containsBPlayer(p)) {
				Bravo b = getBravo(p);
				p.teleport(b.getDeathLoc());
			}
		}
	}

	public void handleFakeDeath(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			Map m = MapManager.get().getMap(p);
			Location l = p.getLocation();
			Location lt = p.getLocation().clone().add(0, 2, 0);
			if (isStarted()) {
				if (e.getDamager() instanceof Player) {
					if (containsAPlayer(p) && containsAPlayer((Player) e.getDamager())) {
						e.setCancelled(true);
					}
					if (containsBPlayer(p) && containsBPlayer((Player) e.getDamager())) {
						e.setCancelled(true);
					}
				}
				if (p.getHealth() - e.getDamage() < 0.5) {
					e.setCancelled(true);
					handleDeath(p);
					p.setGameMode(GameMode.SPECTATOR);
					if (containsAPlayer(p)) {
						Alpha a = getAlpha(p);
						a.setDead(true);
						a.playDeathCircle(l);
						a.setDeathLoc(l);
						updateBoard();
					}
					if (containsBPlayer(p)) {
						Bravo b = getBravo(p);
						b.setDead(true);
						b.playDeathCircle(l);
						b.setDeathLoc(l);
						updateBoard();
					}
					p.teleport(lt);
				}
			}
		}
	}

	public void handlePlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Map m = MapManager.get().getMap(p);
		Location from = e.getFrom();
		Location to = e.getTo();
		if (m != null) {
			if (isStarted()) {
				if (containsAPlayer(p)) {
					Alpha a = getAlpha(p);
					Location l = a.getDeathLoc();
					if (a.isDead()) {
						if (to.getX() != from.getX() || to.getY() != from.getY() || to.getZ() != from.getZ()) {
							p.teleport(from);
						}
					}
				}
				if (containsBPlayer(p)) {
					Bravo b = getBravo(p);
					Location l = b.getDeathLoc();
					if (b.isDead()) {
						if (to.getX() != from.getX() || to.getY() != from.getY() || to.getZ() != from.getZ()) {
							p.teleport(from);
						}
					}
				}
			}
		}
	}

	public void handleDeath(Player p) {
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

	public void removeDeath(Player p) {
		if (isStarted()) {
			if (containsAPlayer(p)) {
				onARemoveDeath(p);
			}
			if (containsBPlayer(p)) {
				onBRemoveDeath(p);
			}
		}
	}

	public void onEliminated(int i) {
		if (i == 1) {
			bWins++;
			message("Alpha team has been eliminated!");
			endRound();
		}
		if (i == 2) {
			aWins++;
			message("Bravo team has been eliminated!");
			endRound();
		}
	}

	public void startNewRound() {
		List<Alpha> aWinners = new ArrayList<Alpha>();
		List<Bravo> bWinners = new ArrayList<Bravo>();
		boolean flag = aWins + bWins == 0;
		if (aWins >= 5 || bWins >= 5) {
			if (aWins >= 5) {
				for (Alpha a : alpha) {
					if (aWinners.isEmpty()) {
						aWinners.add(a);
					}
				}
			}
			if (bWins >= 5) {
				for (Bravo b : bravo) {
					if (bWinners.isEmpty()) {
						bWinners.add(b);
					}
				}
			}
			message(ChatColor.RED + "GAME OVER");
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
			}
			if (aWinners.size() == 1) {
				message(ChatColor.GOLD + "Winner: " + aWinners.get(0).getPlayer().getName());
				stop();
				return;
			}
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
			}
			if (bWinners.size() == 1) {
				message(ChatColor.GOLD + "Winner: " + aWinners.get(0).getPlayer().getName());
				stop();
				return;
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
		aWins = 0;
		bWins = 0;
		for (Alpha a : alpha) {
			a.restore();
			a.removeDeathCircle();
			a.setDead(false);
		}
		for (Bravo b : bravo) {
			b.restore();
			b.removeDeathCircle();
			b.setDead(false);
		}
		setState(MapState.WAITING);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.get(), new Runnable() {
			@Override
			public void run() {
				kickAll(true);
			}
		}, 10L);
	}

	public void start() {
		setState(MapState.STARTED);
		for (Alpha a : alpha) {
			a.ready();
		}
		for (Bravo b : bravo) {
			b.ready();
		}
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

	public void onARemoveDeath(Player p) {
		if (containsAPlayer(p)) {
			if (aDead.contains(p)) {
				aDead.remove(p);
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

	public void onBRemoveDeath(Player p) {
		if (containsBPlayer(p)) {
			if (bDead.contains(p)) {
				bDead.remove(p);
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
			updateBoard();
			message(ChatColor.GREEN + p.getName() + " joined the Map.");
			MessageManager.get().message(p, "You joined Alpha team.");
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
			updateBoard();
			message(ChatColor.GREEN + p.getName() + " joined the game.");
			MessageManager.get().message(p, "You joined Bravo team.");
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
			updateBoard();
			a.removeDeathCircle();
			a.removeNameTag();
			ScoreBoard.get().removeSB(p);
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
			updateBoard();
			b.removeDeathCircle();
			b.removeNameTag();
			ScoreBoard.get().removeSB(p);
			if (state.equals(MapState.STARTED) && getNumberOfBPlayers() < 1) {
				stop();
				message(ChatColor.RED + "Players left, stopping game.");
			}
		}
	}

	public void updateBoard() {
		for (Player aP : getAPlayers()) {
			ScoreBoard.get().updateSB(aP);
		}
		for (Player bP : getBPlayers()) {
			ScoreBoard.get().updateSB(bP);
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

	public void onRemoved() {
		if (isStarted()) {
			stop();
		} else {
			for (Player p : getAPlayers()) {
				removeAlphaPlayer(p);
			}
			for (Player p : getBPlayers()) {
				removeBravoPlayer(p);
			}
		}
		setState(MapState.STOPPED);
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

	public boolean containsAPlayer(Player p) {
		for (Alpha a : alpha) {
			if (a.getPlayer().equals(p)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsBPlayer(Player p) {
		for (Bravo b : bravo) {
			if (b.getPlayer().equals(p)) {
				return true;
			}
		}
		return false;
	}

	public Alpha getAlpha(Player p) {
		for (Alpha a : alpha) {
			if (a.getPlayer().equals(p)) {
				return a;
			}
		}
		return null;
	}

	public Bravo getBravo(Player p) {
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
