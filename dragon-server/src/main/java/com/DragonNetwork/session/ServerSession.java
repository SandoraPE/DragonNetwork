package com.DragonNetwork.session;

import com.DragonNet.channel.DragonNetChannel;
import com.DragonNet.packets.Packet;
import com.DragonNet.session.SessionHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public class ServerSession implements SessionHandler {

    private EventLoopGroup workerGroup;
    private EventLoopGroup bossGroup;

    private final ConcurrentHashMap<SocketChannel, PacketHandler> packetGroup = new ConcurrentHashMap<>();

    public ServerSession(int serverPort) {
        initSession(serverPort);
    }

    /**
     * Attempts to safely read all packets in main thread.
     */
    public void tickSession() {
        packetGroup.values().forEach(PacketHandler::processPacket);
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

            SslContext context = SslContextBuilder.forServer(ServerSession.class.getResourceAsStream("/certificate/certificate.crt"), ServerSession.class.getResourceAsStream("/certificate/private_key.key")).build();
            ServerBootstrap b = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(classPath)
                    .childHandler(new DragonNetChannel(this, context))
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
    public void queueRawPacket(SocketChannel channel, Packet packet) {
        PacketHandler group = packetGroup.get(channel);
        if (group == null) {
            return;
        }
        group.queueRecvPacket(packet);
    }

    @Override
    public void addConnection(SocketChannel channel) {
        packetGroup.put(channel, new PacketHandler(channel));

        log.info("[{}] has connected to the server", channel.localAddress().toString());
    }

    @Override
    public void removeConnection(SocketChannel channel) {
        packetGroup.remove(channel);

        log.info("[{}] has disconnected from the server", channel.localAddress().toString());
    }

    @Override
    public boolean isAuthenticated(SocketChannel channel) {
        PacketHandler group = packetGroup.get(channel);
        if (group == null) {
            return false;
        }

        return group.isAuthenticated();
    }
}
