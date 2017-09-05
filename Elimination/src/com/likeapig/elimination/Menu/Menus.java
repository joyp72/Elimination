package com.likeapig.elimination.Menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.likeapig.elimination.maps.Map;

import net.md_5.bungee.api.ChatColor;

public class Menus {

	private static Inventory teams;

	public static Inventory getInvTeams() {
		return teams;
	}

	public Menus(Player p, Map m) {

		teams = Bukkit.createInventory(p, 9, m.getName());

		ItemStack alpha = new ItemStack(Material.CONCRETE, 1, (byte) 14);
		{
			ItemMeta meta = alpha.getItemMeta();
			meta.setDisplayName(ChatColor.RED + "Alpha");
			ArrayList<String> lore = new ArrayList<>();
			lore.add(ChatColor.GRAY + "Join alpha team");
			lore.add(" ");
			lore.add(ChatColor.GRAY + "Players: ");
			for (Player ap : m.getAPlayers()) {
				lore.add(ChatColor.RED + "  " + ap.getName());
			}
			meta.setLore(lore);
			alpha.setItemMeta(meta);
			teams.setItem(2, alpha);
		}

		ItemStack bravo = new ItemStack(Material.CONCRETE, 1, (byte) 11);
		{
			ItemMeta meta = bravo.getItemMeta();
			meta.setDisplayName(ChatColor.BLUE + "Bravo");
			ArrayList<String> lore = new ArrayList<>();
			lore.add(ChatColor.GRAY + "Join bravo team");
			lore.add(" ");
			lore.add(ChatColor.GRAY + "Players: ");
			for (Player bp : m.getBPlayers()) {
				lore.add(ChatColor.BLUE + bp.getName());
			}
			meta.setLore(lore);
			bravo.setItemMeta(meta);
			teams.setItem(6, bravo);
		}

		ItemStack blank = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
		{
			ItemMeta meta = blank.getItemMeta();
			meta.setDisplayName(" ");
			meta.addItemFlags(ItemFlag.values());
			blank.setItemMeta(meta);
			teams.setItem(0, blank);
			teams.setItem(1, blank);
			teams.setItem(3, blank);
			teams.setItem(5, blank);
			teams.setItem(7, blank);
			teams.setItem(8, blank);
		}

		ItemStack eli = new ItemStack(Material.EYE_OF_ENDER);
		{
			ItemMeta meta = eli.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Elimination");
			meta.addItemFlags(ItemFlag.values());
			ArrayList<String> lore = new ArrayList<>();
			lore.add(ChatColor.GRAY + "BETA PHASE");
			lore.add(" ");
			lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "Overview: ");
			String l[] = formatLore(
					"Elimination is a 2v2 (for now) bending minigame. Teammates will be able to revive each other and the first team to eliminate the other will win the round. The first team to win 5 rounds wins the game. If a team does not eliminate the other before the timer ends, a new round will start.",
					30, org.bukkit.ChatColor.GRAY);
			lore.add(l[0]);
			lore.add(l[1]);
			lore.add(l[2]);
			lore.add(l[3]);
			lore.add(l[4]);
			lore.add(l[5]);
			lore.add(l[6]);
			lore.add(l[7]);
			meta.setLore(lore);
			eli.setItemMeta(meta);
			eli.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 2);
			teams.setItem(4, eli);
		}

	}

	public static String[] formatLore(String text, int size, org.bukkit.ChatColor color) {
		List<String> ret = new ArrayList<String>();

		if (text == null || text.length() == 0)
			return new String[ret.size()];

		String[] words = text.split(" ");
		String rebuild = "";

		int lastAdded = 0;
		for (int i = 0; i < words.length; i++) {
			int wordLen = words[i].length();
			if (rebuild.length() + wordLen > 40 || words[i].contains("\n")
					|| words[i].equals(Character.LINE_SEPARATOR)) {
				lastAdded = i;

				ret.add(color + rebuild);
				rebuild = "";
				if (words[i].equalsIgnoreCase("\n")) {
					words[i] = "";
					continue;
				}

			}
			rebuild = rebuild + " " + words[i];

		}
		if (!rebuild.equalsIgnoreCase(""))
			ret.add(color + rebuild);

		String[] val = new String[ret.size()];
		for (int i = 0; i < ret.size(); i++)
			val[i] = ret.get(i);

		return val;
	}
}
