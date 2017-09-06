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
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;

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

				if (m != null) {

					ScoreboardManager manager = Bukkit.getScoreboardManager();
					Scoreboard board = manager.getNewScoreboard();

					Objective main = board.registerNewObjective("Elimination ", "dummy");
					main.setDisplaySlot(DisplaySlot.SIDEBAR);
					main.setDisplayName(ChatColor.GOLD + "Elimination");

					Score timer = main.getScore(ChatColor.YELLOW + "Timer: " + m.getCountdown());
					timer.setScore(i);
					i--;

					Score blank2 = main.getScore("  ");
					blank2.setScore(i);
					i--;

					Score alpha = main.getScore(ChatColor.RED + "Alpha: (" + m.getAWins() + "/5)");
					alpha.setScore(i);
					i--;
					for (Player aPlayer : m.getAPlayers()) {
						if (m.containsAPlayer(aPlayer)) {
							Alpha a = m.getAlpha(aPlayer);
							if (a.isDead()) {
								Score aP = main
										.getScore(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + aPlayer.getName());
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

					Score bravo = main.getScore(ChatColor.BLUE + "Bravo: (" + m.getBWins() + "/5)");
					bravo.setScore(i);
					i--;
					for (Player bPlayer : m.getBPlayers()) {
						if (m.containsBPlayer(bPlayer)) {
							Bravo b = m.getBravo(bPlayer);
							if (b.isDead()) {
								Score bP = main
										.getScore(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + bPlayer.getName());
								bP.setScore(i);
								i--;
							} else {
								Score bP = main.getScore(ChatColor.GRAY + bPlayer.getName());
								bP.setScore(i);
								i--;
							}
						}
					}
					
					BendingPlayer bp = BendingPlayer.getBendingPlayer(p);
					for (int ii = 1; ii < 10; ii++) {
						String abil = "Unbound Ability";
						CoreAbility ca = CoreAbility.getAbility(bp.getAbilities().get(ii));
						if (ca != null) {
							abil = ca.getName();
						}
						
						String sel = ">"  + ca.getElement().getColor() + abil;
						String norm = ca.getElement().getColor() + abil;
						
						String selCD = ">" + ca.getElement().getColor() + "" + ChatColor.STRIKETHROUGH + abil;
						String normCD = ca.getElement().getColor() + "" + ChatColor.STRIKETHROUGH + abil;
						
						if (bp.getBoundAbility() != null && bp.getBoundAbilityName().equalsIgnoreCase(ca.getName())) {
							Score sc = main.getScore(norm);
							if 
						}
					}

					p.setScoreboard(board);
					i = 10;
				}
			}
		}.runTask(Main.get());
	}

}
