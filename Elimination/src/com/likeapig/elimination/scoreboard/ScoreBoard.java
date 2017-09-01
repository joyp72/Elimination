package com.likeapig.elimination.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.likeapig.elimination.Main;
import com.likeapig.elimination.maps.Map;
import com.likeapig.elimination.maps.MapManager;
import com.likeapig.elimination.teams.Alpha;
import com.likeapig.elimination.teams.Bravo;

import net.md_5.bungee.api.ChatColor;

public class ScoreBoard {
	
	public static ScoreBoard instance;
	private int i = 10;
	
	static {
		instance = new ScoreBoard();
	}
	
	public static ScoreBoard get() {
		return instance;
	}
	
	public void removeSB(Player p) {
		p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
	}
	
	public void updateSB(Player p) {
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				Map m = MapManager.get().getMap(p);
				
				ScoreboardManager manager = Bukkit.getScoreboardManager();
				Scoreboard board = manager.getNewScoreboard();
				
				Objective main = board.registerNewObjective("Elimination ", "dummy");
				main.setDisplaySlot(DisplaySlot.SIDEBAR);
				main.setDisplayName(ChatColor.GOLD + "Elimination");
				
				Score timer = main.getScore(ChatColor.YELLOW + "Timer: ");
				timer.setScore(i);
				i--;
				
				Score blank2 = main.getScore("  ");
				blank2.setScore(i);
				i--;
				
				Score alpha = main.getScore(ChatColor.RED + "Alpha: "); 
				alpha.setScore(i);
				i--;
				for (Player aPlayer : m.getAPlayers()) {
					if (m.containsAPlayer(aPlayer)) {
						Alpha a = m.getAlpha(aPlayer);
						if (a.isDead()) {
							Score aP = main.getScore(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + aPlayer.getName());
							aP.setScore(i);
							i--;
						} else {
							Score aP = main.getScore(ChatColor.GRAY + aPlayer.getName());
							aP.setScore(i);
							i--;
						}
					}
				}
				
				Score blank = main.getScore(" ");
				blank.setScore(i);
				i--;
				
				Score bravo = main.getScore(ChatColor.BLUE + "Bravo: ");
				bravo.setScore(i);
				i--;
				for (Player bPlayer : m.getBPlayers()) {
					if (m.containsBPlayer(bPlayer)) {
						Bravo b = m.getBravo(bPlayer);
						if (b.isDead()) {
							Score bP = main.getScore(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + bPlayer.getName());
							bP.setScore(i);
							i--;
						} else {
							Score bP = main.getScore(ChatColor.GRAY + bPlayer.getName());
							bP.setScore(i);
							i--;
						}
					}
				}
				
				p.setScoreboard(board);
				i = 10;
			}
		}.runTask(Main.get());
	}

}
