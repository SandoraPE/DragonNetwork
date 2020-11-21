package com.DragonNet.packets.session;

import com.DragonNet.handler.ServerInstance;
import com.DragonNet.packets.Packet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Log4j2
public class ServerSession implements SessionHandler {

    private EventLoopGroup workerGroup;
    private EventLoopGroup bossGroup;

    private final BlockingQueue<Packet> packetQueue = new LinkedBlockingQueue<>();

    public ServerSession(int serverPort) {
        initSession(serverPort);
    }

    /**
     * Attempts to safely read all packets in main thread.
     */
    public void tickSession() {
        Packet pk;
        while ((pk = packetQueue.poll()) != null) {
            log.info(pk.toString());
        }
    }

    private void initSession(int serverPort) {
        // Prepare Epoll if available, the reason why bossGroup is below 4 is that
        // we do not want too much processing on an inbound connection since this is
        // a time-expensive operation, instead we are preparing large number of threads on worker
        // group that is responsible in processing packets from given socket.

        bossGroup = new NioEventLoopGroup(4);
        workerGroup = new NioEventLoopGroup(12);
        Class<? extends ServerChannel> classPath = NioServerSocketChannel.class;
        try {
            if (Epoll.isAvailable()) {
                bossGroup = new EpollEventLoopGroup(4);
                workerGroup = new EpollEventLoopGroup(12);

                classPath = EpollServerSocketChannel.class;

                log.info("Using Epoll for better performance.");
            } else {
                log.debug("No Epoll capabilities found in this machine.");
            }

            ServerBootstrap b = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(classPath)
                    .childHandler(new ServerInstance(this, null))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            b.bind(serverPort).sync();

            log.info("Currently listening on port: {}", serverPort);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    @Override
    public void queueRawPacket(Packet packet) {
        packetQueue.add(packet);
    }
}
