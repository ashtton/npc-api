package me.gleeming.npc.handler;

import lombok.Getter;
import me.gleeming.npc.NPC;
import me.gleeming.npc.listener.ConnectionListener;
import me.gleeming.npc.listener.MoveListener;
import me.gleeming.npc.nms.InjectorListener;
import me.gleeming.npc.nms.NMSController;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class NPCHandler {
    @Getter private static NPCHandler instance;

    @Getter private final List<NPC> npcs = new ArrayList<>();
    @Getter private final NMSController nmsController;
    public NPCHandler() {
        instance = this;
        nmsController = new NMSController();

        Bukkit.getPluginManager().registerEvents(new ConnectionListener(), Bukkit.getPluginManager().getPlugins()[0]);
        Bukkit.getPluginManager().registerEvents(new MoveListener(), Bukkit.getPluginManager().getPlugins()[0]);

        // We have to register the listener for the players
        // already online in the instance that a plugin didn't
        // load their npcs whenever the server started
        Bukkit.getOnlinePlayers().forEach(player -> {
            try {
                NPCHandler.getInstance().getNmsController().getNmsInjector().getConstructors()[0].newInstance(player, (InjectorListener) entityId -> NPCHandler.getInstance().getNpcs().stream()
                        .filter(npc -> npc.getFakePlayer().getEntityId() == entityId)
                        .forEach(npc -> npc.interact(player)));
            } catch (Exception ex) { ex.printStackTrace(); }
        });
    }
}
