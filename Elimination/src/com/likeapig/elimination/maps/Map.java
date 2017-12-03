package com.likeapig.elimination.maps;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import com.likeapig.elimination.Main;
import com.likeapig.elimination.Settings;
import com.likeapig.elimination.maps.MessageManager.MessageType;
import com.likeapig.elimination.particles.Rift;
import com.likeapig.elimination.scoreboard.ScoreBoard;
import com.likeapig.elimination.teams.Alpha;
import com.likeapig.elimination.teams.Bravo;
import com.likeapig.elimination.utils.LocationUtils;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;

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
	private int countdown;
	private int c2;
	private int c3;
	private int id1;
	public List<Location> signs;
	public int votes;
	public List<Player> voted;
	public Location rift;
	public boolean zone;
	public boolean aClaiming;
	public boolean bClaiming;
	public int id;
	public int id2;
	public ArmorStand AAS;
	public ArmorStand BAS;
	public ArmorStand AS;

	public Map(String n) {
		name = n;
		aWins = 0;
		bWins = 0;
		countdown = 0;
		c2 = 0;
		c3 = 0;
		votes = 0;
		zone = false;
		aClaiming = false;
		bClaiming = false;
		alpha = new ArrayList<Alpha>();
		bravo = new ArrayList<Bravo>();
		aDead = new ArrayList<Player>();
		bDead = new ArrayList<Player>();
		signs = new ArrayList<Location>();
		voted = new ArrayList<Player>();
		loadFromConfig();
		saveToConfig();
		checkState();

	}

	public int getVotes() {
		return votes;
	}

	public void addVotes(Player p) {
		votes++;
		voted.add(p);
		if (votes >= 2) {
			start();
		}
	}

	public void setRift(Location l) {
		rift = l;
		saveToConfig();
	}

	public void addZone() {
		if (!zone) {
			Rift.get().spawnRift(rift);
			zone = true;
			Timer.get().stopTasks(this);
			if (zone) {

				id2 = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.get(), new Runnable() {

					Location l = rift.clone().add(0, 1.5, 0);

					@Override
					public void run() {
						if (!aClaiming && !bClaiming) {
							if (AAS != null) {
								AAS.remove();
								AAS = null;
							}
							if (BAS != null) {
								BAS.remove();
								BAS = null;
							}
							if (AS == null) {
								ItemStack item = new ItemStack(Material.CONCRETE);

								AS = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
								AS.setGravity(false);
								AS.setHelmet(item);
								AS.setVisible(false);
								AS.setSmall(false);
								AS.setCustomName(ChatColor.WHITE + "" + ChatColor.BOLD + "CLAIM ZONE");
								AS.setCustomNameVisible(true);
								AS.setSilent(true);
								AS.setCollidable(false);
								AS.setInvulnerable(true);
							}

							l.setYaw((l.getYaw() + 2.5F));

							AS.teleport(l);
						}
						if (aClaiming) {
							if (AS != null) {
								AS.remove();
								AS = null;
							}
							if (BAS != null) {
								BAS.remove();
								BAS = null;
							}
							if (AAS == null) {
								ItemStack item = new ItemStack(Material.CONCRETE, 1, (short) 14);

								AAS = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
								AAS.setGravity(false);
								AAS.setHelmet(item);
								AAS.setVisible(false);
								AAS.setSmall(false);
								AAS.setCustomName(ChatColor.RED + "" + ChatColor.BOLD + "CLAIMING ZONE");
								AAS.setCustomNameVisible(true);
								AAS.setSilent(true);
								AAS.setCollidable(false);
								AAS.setInvulnerable(true);
							}

							l.setYaw((l.getYaw() + 2.5F));

							AAS.teleport(l);
						}
						if (bClaiming) {
							if (AAS != null) {
								AAS.remove();
								AAS = null;
							}
							if (AS != null) {
								AS.remove();
								AS = null;
							}
							if (BAS == null) {
								ItemStack item = new ItemStack(Material.CONCRETE, 1, (short) 11);

								BAS = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
								BAS.setGravity(false);
								BAS.setHelmet(item);
								BAS.setVisible(false);
								BAS.setSmall(false);
								BAS.setCustomName(ChatColor.BLUE + "" + ChatColor.BOLD + "CLAIMING ZONE");
								BAS.setCustomNameVisible(true);
								BAS.setSilent(true);
								BAS.setCollidable(false);
								BAS.setInvulnerable(true);
							}

							l.setYaw((l.getYaw() + 2.5F));

							BAS.teleport(l);
						}
					}

				}, 0L, 0L);
				id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.get(), new Runnable() {

					int ai = 7;
					int bi = 7;
					Location l = rift.clone().add(0, 1.5, 0);

					@Override
					public void run() {
						for (Player p : getAPlayers()) {
							if (p.getLocation().distance(rift) < 3) {
								bClaiming = false;
								aClaiming = true;
								ai--;
								Titles.get().addTitle(p, ChatColor.RED + "Claiming..");
								Titles.get().addSubTitle(p, String.valueOf(ai));
								if (ai <= 0 && aClaiming) {
									Rift.get().removeRift();
									if (AAS != null) {
										AAS.remove();
										AAS = null;
									}
									ai = 7;
									aClaiming = false;
									zone = false;
									onEliminated(2);
									removeZone();
								}

							} else {
								aClaiming = false;
								Titles.get().addTitle(p, " ");
								Titles.get().addSubTitle(p, " ");
								ai = 7;
							}
						}
						for (Player p : getBPlayers()) {
							if (p.getLocation().distance(rift) < 3) {
								aClaiming = false;
								bClaiming = true;
								bi--;
								Titles.get().addTitle(p, ChatColor.BLUE + "Claiming..");
								Titles.get().addSubTitle(p, String.valueOf(bi));
								if (bi <= 0 && bClaiming) {
									Rift.get().removeRift();
									if (BAS != null) {
										BAS.remove();
										BAS = null;
									}
									bi = 7;
									bClaiming = false;
									zone = false;
									onEliminated(1);
									removeZone();
								}

							} else {
								bClaiming = false;
								Titles.get().addTitle(p, " ");
								Titles.get().addSubTitle(p, " ");
								bi = 7;
							}
						}
					}

				}, 0L, 20L);
			}
		}
	}

	public void removeZone() {
		Rift.get().removeRift();
		Bukkit.getServer().getScheduler().cancelTask(id);
		Bukkit.getServer().getScheduler().cancelTask(id2);
		zone = false;
		if (AAS != null) {
			AAS.remove();
			AAS = null;
		}
		if (AS != null) {
			AS.remove();
			AS = null;
		}
		if (BAS != null) {
			BAS.remove();
			BAS = null;
		}
	}
	
	public boolean isZone() {
		return zone;
	}

	public void onTimerTick(String arg, int timer) {
		if (arg.equalsIgnoreCase("endround")) {
			countdown = timer;
			updateBoard();
		}
		if (arg.equalsIgnoreCase("bwins")) {
			c2 = timer;
			for (Player bp : getBPlayers()) {
				Titles.get().addTitle(bp, ChatColor.WHITE + "" + ChatColor.BOLD + "0:0" + c2);
				Titles.get().addSubTitle(bp, ChatColor.WHITE + "All enemies down!");
			}
			for (Player ap : getAPlayers()) {
				Titles.get().addTitle(ap, ChatColor.WHITE + "" + ChatColor.BOLD + "0:0" + c2);
				Titles.get().addSubTitle(ap, ChatColor.WHITE + "All allies down!");
			}
		}
		if (arg.equalsIgnoreCase("awins")) {
			c3 = timer;
			for (Player ap : getAPlayers()) {
				Titles.get().addTitle(ap, ChatColor.WHITE + "" + ChatColor.BOLD + "0:0" + c3);
				Titles.get().addSubTitle(ap, ChatColor.WHITE + "All enemies down");
			}
			for (Player bp : getBPlayers()) {
				Titles.get().addTitle(bp, ChatColor.WHITE + "" + ChatColor.BOLD + "0:0" + c3);
				Titles.get().addSubTitle(bp, ChatColor.WHITE + "All allies down");
			}
		}
	}

	public void onTimerEnd(String arg) {
		if (arg.equalsIgnoreCase("endround")) {
			if (isStarted()) {
				addZone();
				for (Player ap : getAPlayers()) {
					Titles.get().addTitle(ap, ChatColor.WHITE + "Claim the Zone!");
					Titles.get().addSubTitle(ap, " ");
				}
				for (Player bp : getBPlayers()) {
					Titles.get().addTitle(bp, ChatColor.WHITE + "Claim the Zone!");
					Titles.get().addSubTitle(bp, " ");
				}
			}
		}
		if (arg.equalsIgnoreCase("bwins")) {
			if (aWins >= 5 || bWins >= 5) {
				startNewRound();
			} else {
				endRound();
			}
		}
		if (arg.equalsIgnoreCase("awins")) {
			if (aWins >= 5 || bWins >= 5) {
				startNewRound();
			} else {
				endRound();
			}
		}
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
		if (rift != null) {
			Settings.get().set("maps." + getName() + ".rift", LocationUtils.locationToString(rift));
		}
		final List<String> signs = new ArrayList<String>();
		for (final Location l : this.signs) {
			signs.add(LocationUtils.locationToString(l));
		}
		Settings.get().set("maps." + this.getName() + ".signs", signs);
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
		if (s.get("maps." + getName() + ".rift") != null) {
			String s4 = s.get("maps." + getName() + ".rift");
			rift = LocationUtils.stringToLocation(s4);
		}
		if (s.get("maps." + this.getName() + ".signs") != null) {
			final List<String> signs = s.get("maps." + this.getName() + ".signs");
			for (final String s6 : signs) {
				final Location l = LocationUtils.stringToLocation(s6);
				this.registerSign(l);
			}
		}
	}

	public void endRound() {
		message(ChatColor.RED + "The round has ended!");
		aDead.clear();
		bDead.clear();
		removeZone();
		countdown = 0;
		Timer.get().stopTasks(this);
		Bukkit.getServer().getScheduler().cancelTask(id1);
		updateBoard();
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.get(), new Runnable() {
			public void run() {
				for (Alpha a : alpha) {
					a.ready();
					a.removeDeathCircle();
					a.setDead(false);
					teleportAPlayers();
					Titles.get().addTitle(a.getPlayer(), " ");
					Titles.get().addSubTitle(a.getPlayer(), " ");
				}
				for (Bravo b : bravo) {
					b.ready();
					b.removeDeathCircle();
					b.setDead(false);
					teleportBPlayers();
					Titles.get().addTitle(b.getPlayer(), " ");
					Titles.get().addSubTitle(b.getPlayer(), " ");
				}
			}
		}, 5L);
		startNewRound();
	}

	public void handleRevive(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		Map m = MapManager.get().getMap(p);
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.get(), new Runnable() {
			int i = 4;

			@Override
			public void run() {
				if (isStarted()) {
					if (containsAPlayer(p)) {
						if (p.isSneaking()) {
							i--;
							for (Alpha a : alpha) {
								if (a.isDead()) {
									Location l = a.getDeathLoc();
									if (p.getLocation().distance(l) <= 1 && !getAlpha(p).isDead()) {
										Titles.get().addTitle(p, ChatColor.GREEN + "Reviving..");
										Titles.get().addSubTitle(p, String.valueOf(i));
										Titles.get().addTitle(a.getPlayer(),
												ChatColor.GREEN + "You are being revived..");
										Titles.get().addSubTitle(a.getPlayer(), String.valueOf(i));
										if (i <= 0) {
											a.ready();
											a.setDead(false);
											a.getPlayer().teleport(l);
											a.removeDeathCircle();
											onARemoveDeath(a.getPlayer());
											updateBoard();
											Titles.get().addTitle(p, " ");
											Titles.get().addSubTitle(p, ChatColor.GREEN + "Revived " + ChatColor.WHITE
													+ a.getPlayer().getName());
											return;
										}
									}
								}
							}
						} else {
							i = 4;
						}
					}
					if (containsBPlayer(p)) {
						if (p.isSneaking()) {
							i--;
							for (Bravo b : bravo) {
								if (b.isDead()) {
									Location l = b.getDeathLoc();
									if (p.getLocation().distance(l) <= 1 && !getBravo(p).isDead()) {
										Titles.get().addTitle(p, ChatColor.GREEN + "Reviving..");
										Titles.get().addSubTitle(p, String.valueOf(i));
										Titles.get().addTitle(b.getPlayer(),
												ChatColor.GREEN + "You are being revived..");
										Titles.get().addSubTitle(b.getPlayer(), String.valueOf(i));
										if (i <= 0) {
											b.ready();
											b.setDead(false);
											b.getPlayer().teleport(l);
											b.removeDeathCircle();
											onBRemoveDeath(b.getPlayer());
											updateBoard();
											Titles.get().addTitle(p, " ");
											Titles.get().addSubTitle(p, ChatColor.GREEN + "Revived " + ChatColor.WHITE
													+ b.getPlayer().getName());
											return;
										}
									}
								}
							}
						} else {
							i = 4;
						}
					}
				}
			}
		}, 0L, 20L);
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

	public void handleFakeDeathE(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			Map m = MapManager.get().getMap(p);
			Location l = p.getLocation();
			if (!isStarted()) {
				e.setCancelled(true);
			}
			BendingPlayer bp = BendingPlayer.getBendingPlayer(p);
			if (bp.canBendPassive(Element.AIR) && e.getCause() == DamageCause.FALL) {
				e.setCancelled(true);
				return;
			}
			if (isStarted()) {
				if (p.getHealth() - e.getDamage() < 0.5) {
					message(ChatColor.WHITE + p.getName() + " Died.");
					e.setCancelled(true);
					handleDeath(p);
					p.setGameMode(GameMode.SPECTATOR);
					Titles.get().addTitle(p, ChatColor.DARK_RED + "" + ChatColor.BOLD + "YOU DIED");
					Titles.get().addSubTitle(p, ChatColor.GRAY + "Wait for an ally to revive you..");
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
					p.teleport(l);
				}
			}
		}
	}

	public void handleFakeDeath(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			Map m = MapManager.get().getMap(p);
			Location l = p.getLocation();
			if (!isStarted()) {
				e.setCancelled(true);
			}
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
					if (e.getDamager() instanceof Player) {
						Player damager = (Player) e.getDamager();
						Titles.get().addTitle(damager, " ");
						Titles.get().addSubTitle(damager,
								ChatColor.GRAY + "Killed " + ChatColor.DARK_RED + p.getName());
						message(ChatColor.WHITE + p.getName() + " was killed by " + damager.getName());
					}
					e.setCancelled(true);
					handleDeath(p);
					p.setGameMode(GameMode.SPECTATOR);
					Titles.get().addTitle(p, ChatColor.DARK_RED + "" + ChatColor.BOLD + "YOU DIED");
					Titles.get().addSubTitle(p, ChatColor.GRAY + "Wait for an ally to revive you..");
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
					p.teleport(l);
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

	public void registerSign(final Location loc) {
		if (!this.signs.contains(loc) && loc.getBlock().getState() instanceof Sign) {
			this.signs.add(loc);
			this.updateSigns();
		}
		this.saveToConfig();
	}

	public void updateSigns() {
		for (final Location l : this.signs) {
			if (l.getBlock().getState() instanceof Sign) {
				final Sign s = (Sign) l.getBlock().getState();
				s.setLine(0, ChatColor.GOLD + "Elimination");
				s.setLine(3, this.getName());
				s.setLine(1, ChatColor.GREEN + "[JOIN]");
				s.update();
			} else {
				this.signs.remove(l);
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
			if (aDead.size() == getNumberOfAPlayers()) {
				onEliminated(1);
			}
			if (bDead.size() == getNumberOfBPlayers()) {
				onEliminated(2);
			}
		}
	}

	public List<Player> getADead() {
		return aDead;
	}

	public List<Player> getBDead() {
		return bDead;
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
			Timer.get().createTimer(getMap(), "bwins", 5).startTimer(getMap(), "bwins");
		}
		if (i == 2) {
			aWins++;
			message("Bravo team has been eliminated!");
			Timer.get().createTimer(getMap(), "awins", 5).startTimer(getMap(), "awins");
		}
	}

	public void startNewRound() {
		List<Alpha> aWinners = new ArrayList<Alpha>();
		List<Bravo> bWinners = new ArrayList<Bravo>();
		boolean flag = aWins + bWins == 0;
		if (aWins >= 5 || bWins >= 5) {
			if (aWins >= 5) {
				for (Alpha a : alpha) {
					titleGameWon(a.getPlayer());
					if (aWinners.isEmpty()) {
						aWinners.add(a);
					}
				}
				for (Bravo b : bravo) {
					titleGameLost(b.getPlayer());
				}
			}
			if (bWins >= 5) {
				for (Bravo b : bravo) {
					titleGameWon(b.getPlayer());
					if (bWinners.isEmpty()) {
						bWinners.add(b);
					}
				}
				for (Alpha a : alpha) {
					titleGameLost(a.getPlayer());
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
				message(ChatColor.GOLD + "Winner: " + bWinners.get(0).getPlayer().getName());
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
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.get(), new Runnable() {
			public void run() {
				Timer.get().createTimer(getMap(), "endround", 90).startTimer(getMap(), "endround");
			}
		}, 20L);
	}

	public void titleGameWon(Player p) {
		Titles.get().addTitle(p, ChatColor.DARK_RED + "" + ChatColor.BOLD + "GAME OVER");
		Titles.get().addSubTitle(p, ChatColor.GRAY + "YOUR TEAM WON!");
	}

	public void titleGameLost(Player p) {
		Titles.get().addTitle(p, ChatColor.DARK_RED + "" + ChatColor.BOLD + "GAME OVER");
		Titles.get().addSubTitle(p, ChatColor.GRAY + "YOUR TEAM LOST!");
	}

	public void stop() {

		votes = 0;
		voted.clear();
		resetWins();
		removeZone();
		countdown = 0;
		aDead.clear();
		bDead.clear();
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
		Timer.get().stopTasks(this);
		kickAll(true);
	}

	public void start() {
		votes = 0;
		voted.clear();
		updateBoard();
		setState(MapState.STARTED);
		Timer.get().stopTasks(this);
		for (Alpha a : alpha) {
			a.ready();
			Titles.get().addTitle(a.getPlayer(), ChatColor.GOLD + "" + ChatColor.BOLD + "GAME STARTED");
			Titles.get().addSubTitle(a.getPlayer(), ChatColor.GRAY + "Eliminate your oppenants to claim victory!");
		}
		for (Bravo b : bravo) {
			b.ready();
			Titles.get().addTitle(b.getPlayer(), ChatColor.GOLD + "" + ChatColor.BOLD + "GAME STARTED");
			Titles.get().addSubTitle(b.getPlayer(), ChatColor.GRAY + "Eliminate your oppenants to claim victory!");
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
		if (!containsAPlayer(p) && state.canJoin() && getNumberOfAPlayers() < 2) {
			Alpha a = new Alpha(p, this);
			alpha.add(a);
			p.teleport(aLoc);
			updateBoard();
			message(ChatColor.GREEN + p.getName() + " joined the Map.");
			MessageManager.get().message(p, "You joined Alpha team.");
			if (state.equals(MapState.WAITING) && (getNumberOfBPlayers() + getNumberOfAPlayers()) == 4) {
				start();
			}
		}
	}

	public void addBravoPlayer(Player p) {
		if (!containsBPlayer(p) && state.canJoin() && getNumberOfBPlayers() < 2) {
			Bravo b = new Bravo(p, this);
			bravo.add(b);
			p.teleport(bLoc);
			updateBoard();
			message(ChatColor.GREEN + p.getName() + " joined the game.");
			MessageManager.get().message(p, "You joined Bravo team.");
			if (state.equals(MapState.WAITING) && (getNumberOfBPlayers() + getNumberOfAPlayers()) == 4) {
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
			if (state.equals(MapState.STARTED) && getNumberOfAPlayers() < 2) {
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
			if (state.equals(MapState.STARTED) && getNumberOfBPlayers() < 2) {
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

	public int getCountdown() {
		return countdown;
	}

	public int getAWins() {
		return aWins;
	}

	public int getBWins() {
		return bWins;
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

	public boolean playerVoted(Player p) {
		if (voted.contains(p)) {
			return true;
		}
		return false;
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
