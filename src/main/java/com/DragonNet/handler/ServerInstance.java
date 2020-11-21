package com.DragonNet.handler;

import com.DragonNet.packets.session.ServerSession;
import com.DragonNet.pipeline.handshake.ServerHandshakeProtocol;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

public class ServerInstance extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    private final ServerSession serverSession;

    public ServerInstance(ServerSession session, SslContext sslCtx) {
        this.sslCtx = sslCtx;
        this.serverSession = session;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // TODO: SSL/AES Private key authentication.
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }

        // Reference: https://stackoverflow.com/questions/26549567/best-way-to-send-out-two-byte-buffers-header-body-in-netty
        //
        // So default MTU for TCP socket transfer is 1500 bytes, this is interesting since I have no literal
        // way to understand this MTU thingy until now, so I have chosen this method to read packets at a maximum
        // length of a short which is 0x7FFF in hexadecimal. According to the referenced link, I will have to put a series
        // of byte which contains the length of a message following by the raw data itself.
        // Therefore: "Payload length" "Actual Payload" "Payload length " "Actual Payload" ...

        pipeline.addLast("handshake", new ServerHandshakeProtocol(ch));

        ch.closeFuture().addListener(serverSession::handleDisconnection);
    }
}