package com.DragonNet.handler;

import com.DragonNet.pipeline.handshake.ClientHandshakeProtocol;
import com.DragonNet.session.ClientSession;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLException;

public class ClientInstance extends ChannelInitializer<SocketChannel> {

    private final ClientSession client;

    public ClientInstance(ClientSession client) {
        this.client = client;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws SSLException {
        ChannelPipeline pipeline = ch.pipeline();

        SslHandler ssl = SslContextBuilder.forClient()
                .trustManager(ServerInstance.class.getResourceAsStream("/certificate/certificate.crt"))
                .build().newHandler(ch.alloc());

        pipeline.addLast("ssl", ssl);
        pipeline.addLast("handshake", new ClientHandshakeProtocol(client, ch));
    }
}
