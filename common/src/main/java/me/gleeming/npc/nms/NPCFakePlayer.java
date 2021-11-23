package me.gleeming.npc.nms;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class NPCFakePlayer {
    /**
     * Makes an NPC visible to a player
     * via sending the add player packet
     * @param player Player
     */
    public abstract void show(Player player);

    /**
     * Makes an NPC no longer visible via
     * sending the player remove packet
     * @param player Player
     */
    public abstract void hide(Player player);

    /**
     * Rotates a players looking position
     * @param player Player
     * @param yaw yaw
     * @param pitch pitch
     */
    public abstract void rotate(Player player, float yaw, float pitch);

    /**
     * Hides an NPCs name for a player, must
     * be called after the show method
     * @param player Player
     */
    public abstract void hideName(Player player);

    /**
     * Gets the location of the fake player
     * @return Location
     */
    public abstract Location getLocation();

    /**
     * Gets the entity id of the fake player
     * @return Entity ID
     */
    public abstract int getEntityId();

    /**
     * Called whenever a player disconnects
     * @param player Player
     */
    public abstract void disconnect(Player player);
}
