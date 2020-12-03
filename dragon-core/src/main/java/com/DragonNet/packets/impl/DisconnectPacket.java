package com.DragonNet.packets.impl;

import com.DragonNet.packets.protocol.DragonNetProtocol;
import com.DragonNet.packets.Packet;
import io.netty.buffer.ByteBuf;

public class DisconnectPacket extends Packet {

    public static int REMOTE_SERVER_CLOSED = 1;
    public static int CLIENT_SERVER_CLOSED = 2;
    public static int AUTHENTICATION_FAIL = 3;
    public static int RATE_LIMITED = 4;

    public int errorCode = REMOTE_SERVER_CLOSED;
    public int rateLimit = 0;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeInt(errorCode);
        buffer.writeInt(rateLimit);
    }

    @Override
    public void decode(ByteBuf buffer) {
        errorCode = buffer.readInt();
        rateLimit = buffer.readInt();
    }

    @Override
    public int getPacketId() {
        return DragonNetProtocol.DISCONNECT_PACKET;
    }

    @Override
    public Packet clone() {
        return new DisconnectPacket();
    }
}
