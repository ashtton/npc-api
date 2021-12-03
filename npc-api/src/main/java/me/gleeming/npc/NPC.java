package me.gleeming.npc;

import lombok.Getter;
import lombok.SneakyThrows;
import me.gleeming.npc.config.NPCConfiguration;
import me.gleeming.npc.handler.NPCHandler;
import me.gleeming.npc.handler.NPCListener;
import me.gleeming.npc.nms.NPCFakePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class NPC {

    // These are the players that need to be able and see the npc
    private final List<UUID> players = new ArrayList<>();

    // These are the players that cannot see the npc right now
    private final List<UUID> hiddenFrom = new ArrayList<>();

    private final NPCFakePlayer fakePlayer;
    private final NPCConfiguration config;

    @SneakyThrows
    public NPC(Location location, NPCConfiguration config) {
        if(NPCHandler.getInstance() == null) new NPCHandler();
        this.config = config;

        players.addAll(config.getDefaultPlayers());
        fakePlayer = (NPCFakePlayer) NPCHandler.getInstance().getNmsController().getNmsFakePlayer().getDeclaredConstructors()[0].newInstance(config.getSkin().toGameProfile(config.isShowCosmetics()), location);
        fakePlayer.holdItem(config.getHeldItem());

        // Shows the NPC to the default players
        players.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            show(player);
        });

        // Shows to all online players if supposed to
        if(config.isShowToAll()) {
            Bukkit.getOnlinePlayers().forEach(this::show);
        }

        NPCHandler.getInstance().getNpcs().add(this);
    }

    /**
     * Shows the NPC to a player
     * @param player Player
     */
    public void show(Player player) {
        hiddenFrom.remove(player.getUniqueId());
        fakePlayer.show(player);
        if(!config.isNametagVisible()) fakePlayer.hideName(player);
    }

    /**
     * Hides the npc from a player
     * @param player Player
     */
    public void hide(Player player) {
        fakePlayer.hide(player);
    }

    /**
     * Called whenever a player interacts
     * with a npc, not meant to be called
     * outside the direct listener class
     */
    public void interact(Player player) {
        if(config.getListener() != null) config.getListener().interact(player);
    }
}
