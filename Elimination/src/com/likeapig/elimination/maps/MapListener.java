package com.likeapig.elimination.maps;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.likeapig.elimination.Main;
import com.likeapig.elimination.Menu.Menus;
import com.likeapig.elimination.teams.Alpha;
import com.likeapig.elimination.teams.Bravo;

import net.md_5.bungee.api.ChatColor;

public class MapListener implements Listener {

	private static MapListener instance;

	static {
		instance = new MapListener();
	}

	public static MapListener get() {
		return instance;
	}

	public void setup() {
		Bukkit.getPluginManager().registerEvents(this, Main.get());
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (MapManager.get().getMap(p) != null) {
			MapManager.get().getMap(p).kickPlayer(p);
		}
	}

	@EventHandler
	public void onPlayerPlaceBlock(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		Map m = MapManager.get().getMap(p);
		if (m != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerPlaceBlock(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Map m = MapManager.get().getMap(p);
		if (m != null) {
			if (!m.isStarted()) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Map m = MapManager.get().getMap(e.getPlayer());
		String s = e.getMessage().toLowerCase();
		if (m != null) {
			e.setMessage(ChatColor.GRAY + s);
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if (MapManager.get().getMap(p) != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerPickItem(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		if (MapManager.get().getMap(p) != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerFakeDeathE(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			Map m = MapManager.get().getMap(p);
			if (m != null) {
				if (!(e instanceof EntityDamageByEntityEvent)) {
					m.handleFakeDeathE(e);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerFakeDeath(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			Map m = MapManager.get().getMap(p);
			if (m != null) {
				m.handleFakeDeath(e);
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Map m = MapManager.get().getMap(p);
		if (m != null) {
			m.handlePlayerMove(e);
		}
	}

	@EventHandler
	public void onDeadInteract(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		Map m = MapManager.get().getMap(p);
		if (m != null) {
			if (m.containsAPlayer(p)) {
				Alpha a = m.getAlpha(p);
				if (a.isDead()) {
					e.setCancelled(true);
				}
			}
			if (m.containsBPlayer(p)) {
				Bravo b = m.getBravo(p);
				if (b.isDead()) {
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerSneak(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		Map m = MapManager.get().getMap(p);
		if (e.isCancelled()) {
			return;
		}
		if (m != null) {
			m.handleRevive(e);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractAtEntityEvent e) {
		Player p = e.getPlayer();
		Entity entity = e.getRightClicked();
		Map m = MapManager.get().getMap(p);
		if (m != null) {
			if (entity instanceof ArmorStand) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void SignEvent(final SignChangeEvent event) {
		if (event.getPlayer().hasPermission("elimination.admin")
				&& event.getLine(0).equalsIgnoreCase("[elimination]")) {
			if (MapManager.get().getMap(event.getLine(1)) != null) {
				MessageManager.get().message(event.getPlayer(), "Sign created !");
				final String map = event.getLine(1);
				final Location loc = event.getBlock().getLocation();
				new BukkitRunnable() {
					public void run() {
						MapManager.get().getMap(map).registerSign(loc);
					}
				}.runTaskLater(Main.get(), 10L);
				return;
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		final Map m = MapManager.get().getMap(p);
		if (p.hasPermission("elimination.default") && (e.getAction().equals((Object) Action.RIGHT_CLICK_BLOCK))) {
			if (m == null) {
				if (e.getClickedBlock().getType().equals((Object) Material.WALL_SIGN)
						|| e.getClickedBlock().getType().equals((Object) Material.SIGN_POST)) {
					final Sign s = (Sign) e.getClickedBlock().getState();
					if (s.getLine(0).trim().equalsIgnoreCase(ChatColor.GOLD + "Elimination")
							&& s.getLine(3).length() > 0 && MapManager.get().getMap(s.getLine(3)) != null) {
						final Map m2 = MapManager.get().getMap(s.getLine(3));
						new Menus(p, m2);
						p.openInventory(Menus.getInvTeams());
						e.setCancelled(true);
					}
				}
			}
			if (m != null) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Map m = MapManager.get().getMap(e.getPlayer());
		if (m != null) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.get(), new Runnable() {

				@Override
				public void run() {
					m.handleRespawn(e);
				}
			}, 20L);
		}
	}

	@EventHandler
	public void onPlayerHungerChange(FoodLevelChangeEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (MapManager.get().getMap(p) != null) {
				e.setCancelled(true);
			}
		}
	}

}
