package me.gleeming.npc.listener;

import lombok.SneakyThrows;
import me.gleeming.npc.handler.NPCHandler;
import me.gleeming.npc.nms.InjectorListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    @SneakyThrows
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Injects player with a packet listener
        NPCHandler.getInstance().getNmsController().getNmsInjector().getConstructors()[0].newInstance(player, (InjectorListener) entityId -> NPCHandler.getInstance().getNpcs().stream()
                .filter(npc -> npc.getFakePlayer().getEntityId() == entityId)
                .forEach(npc -> npc.interact(player)));

        // Shows the player all npcs they have access to
        NPCHandler.getInstance().getNpcs().stream()
                .filter(npc -> npc.getConfig().isShowToAll() || npc.getPlayers().contains(player.getUniqueId()))
                .forEach(npc -> {
                    npc.hide(player);
                    npc.show(player);
                });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        NPCHandler.getInstance().getNpcs().stream()
                .filter(npc -> npc.getConfig().isShowToAll() || npc.getPlayers().contains(player.getUniqueId()))
                .forEach(npc -> npc.getFakePlayer().disconnect(player));
    }
}
