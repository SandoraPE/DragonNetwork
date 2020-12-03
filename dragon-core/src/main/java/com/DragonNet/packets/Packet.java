package com.DragonNet.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.SocketChannel;
import lombok.NonNull;

public abstract class Packet {

    /**
     * @return int
     */
    public abstract int getPacketId();

    /**
     * Encodes the packet to series of bytes from given buffer.
     *
     * @param buffer ByteBuf
     */
    public void encode(ByteBuf buffer) {
    }

    /**
     * Decodes a byte buffer from a client.
     *
     * @param buffer ByteBuf
     */
    public void decode(ByteBuf buffer) {
    }

    /**
     * Attempts to create a new copy of the overridden class, this function
     * does not copy its data or results of a decoded bytes.
     *
     * @return Packet
     */
    @NonNull
    public abstract Packet clone();
}
