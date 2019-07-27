package com.github.PhanaticD;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class AirPuffListener implements Listener {
	@EventHandler
	public void click(final PlayerInteractEvent event) {
		//Only check for left clicks
		if (event.getHand() != EquipmentSlot.HAND) {
			return;
		}
		if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_AIR) {
			return;
		}
		//left click on air is always cancelled so we have to specifically check here for left clicks on blocks
		if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.isCancelled()){
			return;
		}

		final Player p = event.getPlayer();
		final BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(p);
		if (bPlayer == null) {
			return;
		}
		if (bPlayer.canBend(CoreAbility.getAbility(AirPuff.class)) && !CoreAbility.hasAbility(event.getPlayer(), AirPuff.class)) {
			//finally make a new move for the player
			new AirPuff(p);
		}
	}
}
