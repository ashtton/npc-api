package me.gleeming.npc.v1_8_R1;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import lombok.SneakyThrows;
import me.gleeming.npc.nms.InjectorListener;
import net.minecraft.server.v1_8_R1.EnumEntityUseAction;
import net.minecraft.server.v1_8_R1.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R1.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
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

                    // Invalidates next packet on right click
                    if(packetPlayInUseEntity.a() != EnumEntityUseAction.ATTACK) {
                        nextBS = true;
                    }

                    Field field = packetPlayInUseEntity.getClass().getDeclaredField("a");
                    field.setAccessible(true);

                    injectorListener.interacted(field.getInt(packetPlayInUseEntity));
                }

                super.channelRead(channelHandlerContext, packet);
            }
        };


        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        Field channelField = connection.networkManager.getClass().getDeclaredField("i");
        channelField.setAccessible(true);

        ChannelPipeline channelPipeline = ((Channel) channelField.get(connection)).pipeline();
        channelPipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
    }
}
