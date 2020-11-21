package com.DragonNet.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;

public abstract class Packet {

    private SocketChannel channel;

    /**
     * @return int
     */
    public abstract int getPacketId();

    final public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }

    final public SocketChannel getChannel() {
        return this.channel;
    }

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
    public abstract Packet clone();

    protected String readString(ByteBuf buf) {
        int length = buf.readInt();
        if (length == 0) {
            return null;
        }

        byte[] bytes = new byte[length];
        for (int i = 0; i < length; ++i) {
            bytes[i] = buf.readByte();
        }

        return new String(bytes, CharsetUtil.UTF_8);
    }

    protected void writeString(ByteBuf buf, String string) {
        byte[] str;
        if (string == null) {
            str = new byte[0];
        } else {
            str = string.getBytes(CharsetUtil.UTF_8);
        }

        buf.writeInt(str.length);
        buf.writeBytes(str);
    }
}
