package com.DragonNet.pipeline;

import com.DragonNet.packets.Packet;
import com.DragonNet.packets.impl.LoginPacket;
import com.DragonNet.session.SessionHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DragonNetPacketTelemetry extends ChannelInboundHandlerAdapter {

    private final SessionHandler session;
    private final SocketChannel channel;

    public DragonNetPacketTelemetry(SocketChannel channel, SessionHandler session) {
        this.session = session;
        this.channel = channel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (!session.isAuthenticated(channel) && !(msg instanceof LoginPacket)) {
            return;
        }

        session.queueRawPacket(channel, (Packet) msg);
    }
}
