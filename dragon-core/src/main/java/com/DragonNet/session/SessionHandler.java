package com.DragonNet.session;

import com.DragonNet.packets.Packet;
import io.netty.channel.socket.SocketChannel;

/**
 * A session handler for server/client instances.
 *
 * <p> All sessions provided will be executed in netty's io thread. It is advisable to not
 * perform thread-blocking operation within these functions.
 */
public interface SessionHandler {

    /**
     * Handles raw packet.
     */
    void queueRawPacket(SocketChannel channel, Packet packet);

    /**
     * Queues a connection instance to the server. The server
     * should perform appropriate actions with this channel such as writing a packet.
     *
     * @param channel SocketChannel instance of the connected client/server.
     */
    void addConnection(SocketChannel channel);

    /**
     * Remove a connection instance from the server. The server should
     * perform appropriate actions with this channel to remove it from entry.
     *
     * @param channel SocketChannel instance of the disconnected client/server.
     */
    void removeConnection(SocketChannel channel);

    /**
     * Checks if the socket provided is authenticated.
     *
     * @return {@code true} if the socket is authenticated.
     */
    boolean isAuthenticated(SocketChannel channel);
}
