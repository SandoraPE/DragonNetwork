package com.DragonNet.session;

import com.DragonNet.handler.ClientInstance;
import com.DragonNet.packets.Packet;
import com.DragonNet.packets.impl.KeepAlivePacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Log4j2
public class ClientSession implements SessionHandler {

    private final static int CONNECTED = 0x1;
    private final static int DISCONNECTED = 0x2;
    private final static int RECONNECTING = 0x3;

    private SocketChannel channel;

    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;

    private int currentTick = 0;
    private int reconnectionFlags = 0x0;

    private final Queue<Packet> queuedPackets = new LinkedBlockingQueue<>();
    private final Queue<Packet> packetQueue = new LinkedBlockingQueue<>();

    private final List<Integer> reconnectTimeout = Arrays.asList(3, 5, 8, 16, 32, 51, 60);

    public ClientSession(InetSocketAddress address) {
        setConnectionFlag(DISCONNECTED, true);

        initSession(address);
    }

    public boolean hasFlags(int flagId) {
        return ((reconnectionFlags >> flagId) & 1) == 1;
    }

    public void setConnectionFlag(int flagId, boolean flags) {
        if (flags) {
            this.reconnectionFlags |= 1 << flagId;
        } else {
            this.reconnectionFlags &= ~(1 << flagId);
        }
    }

    @SneakyThrows
    private void initSession(InetSocketAddress address) {
        workerGroup = new NioEventLoopGroup(4);
        Class<? extends SocketChannel> classPath = NioSocketChannel.class;

        if (Epoll.isAvailable()) {
            workerGroup = new EpollEventLoopGroup();

            classPath = EpollSocketChannel.class;

            log.info("Using epoll for better performance.");
        } else {
            log.debug("No epoll capabilities found in this machine.");
        }

        log.info("Establishing Connection to 127.0.0.1:8080");

        // Attempt to use Epoll socket if it is available.
        bootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(classPath)
                .remoteAddress(address)
                .handler(new ClientInstance(this));

        initHandshake();
    }

    private void initHandshake() {
        setConnectionFlag(RECONNECTING, true);

        bootstrap.connect().addListener((ChannelFutureListener) listener -> {
            if (listener.cause() != null) {
                log.warn("Unable to connect to the prior server, attempting to reconnect");

                setConnectionFlag(RECONNECTING, false);
                return;
            }

            // Handshake protocol.
            ByteBuf buffer = Unpooled.buffer();
            buffer.writeBytes(ByteBufUtil.decodeHexDump("4f6820796f752061726520612066757272793f"));
            listener.channel().writeAndFlush(buffer).addListener((ChannelFutureListener) future -> {
                setConnectionFlag(RECONNECTING, false);
                setConnectionFlag(DISCONNECTED, false);
                setConnectionFlag(CONNECTED, true);

                log.info("Connection to the prior server has been established!");

                future.channel().closeFuture().addListener((ChannelFutureListener) e -> {
                    log.warn("Lost connection to the prior server, attempting to reconnect");

                    setConnectionFlag(CONNECTED, false);
                    setConnectionFlag(DISCONNECTED, true);
                    setConnectionFlag(RECONNECTING, false);

                    channel = null;
                });
            });
        });
    }

    @Override
    public void queueRawPacket(Packet packet) {
        packetQueue.add(packet);
    }

    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }

    public void sendPacket(Packet packet) {
        if (hasFlags(DISCONNECTED)) {
            queuedPackets.add(packet);
            return;
        }

        channel.writeAndFlush(packet);
    }

    private int currentStage = 0;

    public void tickSession() {
        currentTick++;

        if (hasFlags(DISCONNECTED) && !hasFlags(RECONNECTING)) {
            int timeout = reconnectTimeout.get(currentStage);

            if ((currentTick % (timeout * 50)) == 0) {
                initHandshake();

                if (currentStage < (reconnectTimeout.size() - 1)) {
                    currentStage++;
                }
            }
        } else if (hasFlags(CONNECTED)) {
            Packet pk;
            while ((pk = queuedPackets.poll()) != null) {
                sendPacket(pk);
            }

            while ((pk = packetQueue.poll()) != null) {
                log.info(pk.toString());
            }

            // https://stackoverflow.com/questions/21358800/tcp-keep-alive-to-determine-if-client-disconnected-in-netty/21372593
            if ((currentTick % (10 * 50)) == 0) {
                sendPacket(new KeepAlivePacket());
            }
        }
    }

    public void shutdown() {
        workerGroup.shutdownGracefully();

        log.info("Successful disconnection.");
    }
}
