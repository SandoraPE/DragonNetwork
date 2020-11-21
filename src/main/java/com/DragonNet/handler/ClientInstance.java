package com.DragonNet.handler;

import com.DragonNet.DragonNetClient;
import com.DragonNet.pipeline.handshake.ClientHandshakeProtocol;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ClientInstance extends ChannelInitializer<SocketChannel> {

    private final DragonNetClient client;

    public ClientInstance(DragonNetClient client) {
        this.client = client;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new ClientHandshakeProtocol());

//
//        pipeline.addLast(new LengthFieldBasedFrameDecoder(32767, 0, 2, 0, 2));
//
//        pipeline.addLast("packetsGroup", new DragonNetPacketManager(ch));
//        pipeline.addLast("packetTelemetry", new DragonNetPacketTelemetry(ch));

        client.socket = ch;
    }
}
