package com.likeapig.elimination.Menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.likeapig.elimination.Main;
import com.likeapig.elimination.maps.Map;
import com.likeapig.elimination.maps.MapManager;
import com.likeapig.elimination.maps.MessageManager;
import com.likeapig.elimination.maps.MessageManager.MessageType;

import net.md_5.bungee.api.ChatColor;

public class MenusListener implements Listener {

	private static MenusListener instance;

	static {
		instance = new MenusListener();
	}

	public static MenusListener get() {
		return instance;
	}

	public void setup() {
		Bukkit.getPluginManager().registerEvents(this, Main.get());
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {

		// Team Menu
		if (Menus.getInvTeams() != null) {
			if (e.getInventory().getName().equalsIgnoreCase(Menus.getInvTeams().getName())) {
				if (e.getCurrentItem() == null) {
					return;
				}
				if (e.getCurrentItem().getType() == Material.CONCRETE) {
					if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName())
							.equalsIgnoreCase("Alpha")) {
						e.setCancelled(true);

						if (e.getWhoClicked() instanceof Player) {
							Player p = (Player) e.getWhoClicked();
							Map m = MapManager.get().getMap(p);
							if (m != null) {
								MessageManager.get().message(p, "You are already in a map", MessageType.BAD);
								return;
							}
							Map m2 = MapManager.get().getMap(Menus.getInvTeams().getName());
							if (m2.isStarted()) {
								MessageManager.get().message(p, "Map is being used!", MessageType.BAD);
								return;
							}
							m2.addAlphaPlayer(p);
						}
					}
					if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName())
							.equalsIgnoreCase("Bravo")) {
						e.setCancelled(true);

						if (e.getWhoClicked() instanceof Player) {
							Player p = (Player) e.getWhoClicked();
							Map m = MapManager.get().getMap(p);
							if (m != null) {
								MessageManager.get().message(p, "You are already in a map", MessageType.BAD);
								return;
							}
							Map m2 = MapManager.get().getMap(Menus.getInvTeams().getName());
							if (m2.isStarted()) {
								MessageManager.get().message(p, "Map is being used!", MessageType.BAD);
								return;
							}
							m2.addBravoPlayer(p);
						}
					}
				}
				if (e.getCurrentItem().getType() == Material.STAINED_GLASS_PANE || e.getCurrentItem().getType() == Material.EYE_OF_ENDER) {
					e.setCancelled(true);
				}
			}
		}

	}

}
