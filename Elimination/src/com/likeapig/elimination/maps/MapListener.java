package com.likeapig.elimination.maps;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.likeapig.elimination.Main;
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
	public void onPlayerBreakBlock(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Map m = MapManager.get().getMap(p);
		if (m != null) {
			e.setCancelled(true);
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
