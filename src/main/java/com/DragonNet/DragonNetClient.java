package com.DragonNet;

import com.DragonNet.handler.ClientInstance;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DragonNetClient {

    public SocketChannel socket;

    public DragonNetClient() throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);
        Class<? extends SocketChannel> classPath = NioSocketChannel.class;

        try {
            if (Epoll.isAvailable()) {
                workerGroup = new EpollEventLoopGroup();

                classPath = EpollSocketChannel.class;

                log.info("Using epoll for better performance.");
            } else {
                log.debug("No epoll capabilities found in this machine.");
            }

            log.info("Establishing Connection to 127.0.0.1:8080");

            // Attempt to use Epoll socket if it is available.
            Bootstrap b = new Bootstrap()
                    .group(workerGroup)
                    .channel(classPath)
                    .remoteAddress("127.0.0.1", 8080)
                    .handler(new ClientInstance(this));

            ChannelFuture promise = b.connect().sync();

            // Handshake protocol.
            ByteBuf buffer = Unpooled.buffer();
            buffer.writeBytes(ByteBufUtil.decodeHexDump("4f6820796f752061726520612066757272793f"));
            socket.writeAndFlush(buffer);
            //socket.writeAndFlush(new LoginPacket());

            log.info("Successful connection to 127.0.0.1:8080");

            promise.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }

        log.info("Successful disconnection.");
    }

    public static void main(String[] args) throws Exception {
        new DragonNetClient();
    }
}
