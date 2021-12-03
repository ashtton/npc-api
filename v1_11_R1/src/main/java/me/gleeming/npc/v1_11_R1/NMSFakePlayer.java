package me.gleeming.npc.v1_11_R1;

import com.mojang.authlib.GameProfile;
import lombok.SneakyThrows;
import me.gleeming.npc.nms.NPCFakePlayer;
import net.minecraft.server.v1_11_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_11_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class NMSFakePlayer extends NPCFakePlayer {

    // Stores the teams for each player
    // to prevent the player from getting
    // multiple teams and disconnecting
    private static final HashMap<UUID, ScoreboardTeam> teams = new HashMap<>();

    // This is the fake player to manipulate
    private final EntityPlayer entityPlayer;

    // The item the npc is holding
    private ItemStack heldItem;

    public NMSFakePlayer(GameProfile gameProfile, Location location) {
        entityPlayer = new EntityPlayer(
                ((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle(),
                gameProfile,
                new PlayerInteractManager(((CraftWorld) Bukkit.getWorlds().get(0)).getHandle())
        );

        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @Override
    public void rotate(Player player, float yaw, float pitch) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) ((int) (yaw * 256.0F / 360.0F))));
        connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(entityPlayer.getId(), (byte) ((int) (yaw * 256.0F / 360.0F)), (byte) ((int) (pitch * 256.0F / 360.0F)), true));
    }

    @Override
    @SneakyThrows
    public void show(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
        rotate(player, entityPlayer.yaw, entityPlayer.pitch);

        if(heldItem != null) {
            connection.sendPacket(new PacketPlayOutEntityEquipment(entityPlayer.getId(), EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(heldItem)));
        }

        new Thread() {
            @SneakyThrows
            public void run() {
                Thread.sleep(1000);
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
            }
        }.start();
    }

    @Override
    public void hide(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
        connection.sendPacket(new PacketPlayOutEntityDestroy(entityPlayer.getId()));
    }

    @Override
    public void hideName(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        if(!teams.containsKey(player.getUniqueId())) {
            ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getNewScoreboard()).getHandle(), "npc");
            team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);

            // Create team packet
            connection.sendPacket(new PacketPlayOutScoreboardTeam(team, 0));

            // Update teams map
            teams.put(player.getUniqueId(), team);
        }

        // Nonnull scoreboard team
        ScoreboardTeam team = teams.get(player.getUniqueId());

        // Entity add to team packet
        connection.sendPacket(new PacketPlayOutScoreboardTeam(team, Collections.singletonList(entityPlayer.getName()), 3));
    }

    @Override
    public void holdItem(ItemStack item) {
        heldItem = item;
    }

    @Override
    public void disconnect(Player player) {
        teams.remove(player.getUniqueId());
    }

    @Override
    public Location getLocation() {
        return new Location(entityPlayer.getWorld().getWorld(), entityPlayer.locX, entityPlayer.locY, entityPlayer.locZ, entityPlayer.yaw, entityPlayer.pitch);
    }

    @Override
    public int getEntityId() {
        return entityPlayer.getId();
    }
}
