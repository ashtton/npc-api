package me.gleeming.npc.v1_11_R1;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import me.gleeming.npc.nms.InjectorListener;
import net.minecraft.server.v1_11_R1.PacketPlayInUseEntity;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class NMSInjector {

    // This version of the game likes to send
    // two entity use packets per each right click,
    // so we just invalidate one of the packets
    boolean nextBS = false;

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
                    if(packetPlayInUseEntity.a() != PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                        nextBS = true;
                    }

                    Field field = packetPlayInUseEntity.getClass().getDeclaredField("a");
                    field.setAccessible(true);

                    injectorListener.interacted(field.getInt(packetPlayInUseEntity));
                }

                super.channelRead(channelHandlerContext, packet);
            }
        };

        ChannelPipeline channelPipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();
        channelPipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
    }
}
