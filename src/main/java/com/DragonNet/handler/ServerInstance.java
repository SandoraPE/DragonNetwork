package com.DragonNet.handler;

import com.DragonNet.pipeline.handshake.ServerHandshakeProtocol;
import com.DragonNet.session.SessionHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLException;

public class ServerInstance extends ChannelInitializer<SocketChannel> {

    private final SessionHandler serverSession;

    public ServerInstance(SessionHandler session) {
        this.serverSession = session;
    }

    @Override
    public void initChannel(SocketChannel ch) throws SSLException {
        ChannelPipeline pipeline = ch.pipeline();

        SslHandler engine = SslContextBuilder
                .forServer(ServerInstance.class.getResourceAsStream("/certificate/certificate.crt"), ServerInstance.class.getResourceAsStream("/certificate/private_key.key"))
                .build().newHandler(ch.alloc());

        pipeline.addLast("ssl", engine);
        pipeline.addLast("handshake", new ServerHandshakeProtocol(serverSession, ch));
    }
}