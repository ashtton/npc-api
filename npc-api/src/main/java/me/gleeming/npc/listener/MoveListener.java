package me.gleeming.npc.listener;

import me.gleeming.npc.handler.NPCHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        NPCHandler.getInstance().getNpcs().stream()
                .filter(npc -> npc.getConfig().isShowToAll() || npc.getPlayers().contains(player.getUniqueId()))
                .forEach(npc -> {
                    if(!npc.getHiddenFrom().contains(player.getUniqueId()) && npc.getFakePlayer().getLocation().distance(player.getLocation()) >= 75) {
                        npc.getHiddenFrom().add(player.getUniqueId());
                        npc.hide(player);
                    }

                    if(npc.getHiddenFrom().contains(player.getUniqueId()) && npc.getFakePlayer().getLocation().distance(player.getLocation()) < 75) {
                        npc.show(player);
                    }
                });
    }
}
