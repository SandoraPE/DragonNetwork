package com.DragonNet.pipeline;

import com.DragonNet.packets.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DragonNetPacketTelemetry extends ChannelInboundHandlerAdapter {

    private final SocketChannel channel;

    public DragonNetPacketTelemetry(SocketChannel socket) {
        this.channel = socket;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Packet pk = (Packet) msg;

        // TODO: Handle packet here.

        log.info(pk.toString());

        channel.writeAndFlush(pk);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
