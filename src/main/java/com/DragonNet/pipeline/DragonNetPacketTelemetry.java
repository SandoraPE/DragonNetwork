package com.DragonNet.pipeline;

import com.DragonNet.packets.Packet;
import com.DragonNet.packets.session.SessionHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DragonNetPacketTelemetry extends ChannelInboundHandlerAdapter {

    private final SessionHandler session;

    public DragonNetPacketTelemetry(SessionHandler session) {
        this.session = session;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        session.queueRawPacket((Packet) msg);
    }
}
