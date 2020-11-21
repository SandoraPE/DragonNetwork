package com.DragonNet.handler;

import com.DragonNet.packets.session.ClientSession;
import com.DragonNet.pipeline.handshake.ClientHandshakeProtocol;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ClientInstance extends ChannelInitializer<SocketChannel> {

    private final ClientSession client;

    public ClientInstance(ClientSession client) {
        this.client = client;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("handshake", new ClientHandshakeProtocol(client, ch));
    }
}
