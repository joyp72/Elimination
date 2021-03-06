package com.likeapig.elimination.commands;

import org.bukkit.entity.Player;

import com.likeapig.elimination.maps.Map;
import com.likeapig.elimination.maps.MapManager;
import com.likeapig.elimination.maps.MessageManager;
import com.likeapig.elimination.maps.MessageManager.MessageType;
import com.likeapig.elimination.particles.Rift;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;

public class Test extends Commands {

	public Test() {
		super("elimination.admin", "Test", "", new String[] { "t" });
	}

	@Override
	public void onCommand(Player sender, String[] args) {
		Player p = sender;
		BendingPlayer bp = BendingPlayer.getBendingPlayer(p);
		int i = 1;
		CoreAbility ca = CoreAbility.getAbility(bp.getAbilities().get(i));
		String norm = ca.getElement().getColor() + ca.getName();
		p.sendMessage(norm);

	}

}
