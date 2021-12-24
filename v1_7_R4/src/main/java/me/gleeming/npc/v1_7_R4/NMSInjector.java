package me.gleeming.npc.v1_7_R4;

import lombok.SneakyThrows;
import me.gleeming.npc.nms.InjectorListener;
import net.minecraft.server.v1_7_R4.PacketPlayInUseEntity;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelPipeline;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class NMSInjector {

    // This version of the game likes to send
    // two entity use packets per each right click,
    // so we just invalidate one of the packets
    boolean nextBS = false;

    @SneakyThrows
    public NMSInjector(Player player, InjectorListener injectorListener) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                if (packet instanceof PacketPlayInUseEntity) {
                    if(nextBS) {
                        nextBS = false;
                        return;
                    }

                    PacketPlayInUseEntity packetPlayInUseEntity = (PacketPlayInUseEntity) packet;

                    Field field = packetPlayInUseEntity.getClass().getDeclaredField("a");
                    field.setAccessible(true);

                    injectorListener.interacted(field.getInt(packetPlayInUseEntity));
                }

                super.channelRead(channelHandlerContext, packet);
            }
        };


        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        Field channelField = connection.networkManager.getClass().getDeclaredField("m");
        channelField.setAccessible(true);

        ChannelPipeline channelPipeline = ((Channel) channelField.get(connection.networkManager)).pipeline();
        channelPipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
    }
}
