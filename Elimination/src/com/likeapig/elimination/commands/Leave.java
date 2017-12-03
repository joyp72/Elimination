package com.likeapig.elimination.commands;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.alessiodp.partiesapi.Parties;
import com.alessiodp.partiesapi.interfaces.PartiesAPI;
import com.likeapig.elimination.maps.Map;
import com.likeapig.elimination.maps.MapManager;
import com.likeapig.elimination.maps.MessageManager;
import com.likeapig.elimination.maps.MessageManager.MessageType;

public class Leave extends Commands {
	
	PartiesAPI api = Parties.getApi();
	
	public Leave() {
		super("elimination.default", "Leave a map", "", new String[] { "l" });
	}
	
	@Override
	public void onCommand(Player sender, String[] args) {
		Player p = sender;
		UUID id = p.getUniqueId();
		Map m = MapManager.get().getMap(p);
		if (m == null) {
			MessageManager.get().message(p, "You are not in a map.", MessageType.BAD);
			return;
		}
		if (api.haveParty(id)) {
			String party = api.getPartyName(id);
			if (api.getPartyLeader(party) == id) {
				for (Player pm: api.getPartyOnlinePlayers(party)) {
					if (MapManager.get().getMap(pm) != null) {
						m.kickPlayer(pm);
					}
				}
			} else {
				m.kickPlayer(p);
			}
		} else {
			m.kickPlayer(p);
		}
	}

}
