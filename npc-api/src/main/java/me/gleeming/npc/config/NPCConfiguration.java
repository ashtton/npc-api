package me.gleeming.npc.config;

import lombok.Getter;
import me.gleeming.npc.handler.NPCListener;
import me.gleeming.npc.skin.Skin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class NPCConfiguration {
    // The skin that will be displayed
    private Skin skin = new Skin();

    // Players to originally show the npc to
    private final List<UUID> defaultPlayers = new ArrayList<>();

    // Whether or not the nametag is visible
    boolean nametagVisible = false;

    // Whether or not a players' cosmetics are visible
    boolean showCosmetics = false;

    // The listener called whenever a npc is interacted with
    private NPCListener listener = null;

    // Show to all players
    private boolean showToAll = false;

    // The item the npc holds
    private ItemStack heldItem = null;

    /**
     * Changes the show to all option
     * @param showToAll Showing to all
     * @return NPC Configuration
     */
    public NPCConfiguration showToAll(boolean showToAll) {
        this.showToAll = showToAll;
        return this;
    }

    /**
     * Changes the held item
     * @param heldItem Held Item
     * @return NPC Configuration
     */
    public NPCConfiguration heldItem(ItemStack heldItem) {
        this.heldItem = heldItem;
        return this;
    }

    /**
     * Adds a player to the beginning players list
     * @param player Player
     * @return NPC Configuration
     */
    public NPCConfiguration addPlayer(Player player) {
        defaultPlayers.add(player.getUniqueId());
        return this;
    }

    /**
     * Sets the NPC interaction listener
     * @param listener Listener
     * @return NPC Configuration
     */
    public NPCConfiguration listener(NPCListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * Updates showing cosmetics
     * @param showCosmetics Cosmetics Visible
     * @return NPC Configuration
     */
    public NPCConfiguration showCosmetics(boolean showCosmetics) {
        this.showCosmetics = showCosmetics;
        return this;
    }

    /**
     * Updates nametag visibility
     * @param nametagVisible Nametag Visible
     * @return NPC Configuration
     */
    public NPCConfiguration nametagVisible(boolean nametagVisible) {
        this.nametagVisible = nametagVisible;
        return this;
    }

    /**
     * Updates the skin
     * @param skin Skin
     * @return NPC Configuration
     */
    public NPCConfiguration skin(Skin skin) {
        this.skin = skin;
        return this;
    }
}
