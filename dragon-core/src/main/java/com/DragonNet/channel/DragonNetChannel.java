package com.DragonNet.channel;

import com.DragonNet.pipeline.DragonNetPacketDecoder;
import com.DragonNet.pipeline.DragonNetPacketTelemetry;
import com.DragonNet.session.SessionHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class DragonNetChannel extends ChannelInitializer<SocketChannel> {

    private final SessionHandler sessionChannel;
    private final SslContext sslHandler;

    public DragonNetChannel(SessionHandler client, SslContext context) {
        this.sessionChannel = client;
        this.sslHandler = context;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslHandler != null) {
            pipeline.addLast("ssl", sslHandler.newHandler(ch.alloc()));
        }

        pipeline.addLast(new LengthFieldBasedFrameDecoder(0x7FFF, 0, 4, 0, 4));

        pipeline.addLast("timeout", new ReadTimeoutHandler(60));
        pipeline.addLast("packetsGroup", new DragonNetPacketDecoder());
        pipeline.addLast("packetTelemetry", new DragonNetPacketTelemetry(ch, sessionChannel));

        sessionChannel.addConnection(ch);
        ch.closeFuture().addListener((ChannelFutureListener) listener -> sessionChannel.removeConnection(ch));
    }
}
