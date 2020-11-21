package com.DragonNet.packets.impl;

import com.DragonNet.packets.Packet;
import com.DragonNet.packets.DragonNetProtocol;
import io.netty.buffer.ByteBuf;

/**
 * Login packet using the most safest and secure encryption for each passwords.
 */
public class LoginPacket extends Packet {

    public String socketId = "Beta-1";
    public String passphrase = "testing123";

    @Override
    public int getPacketId() {
        return DragonNetProtocol.LOGIN_PROTOCOL;
    }

    @Override
    public Packet clone() {
        return new LoginPacket();
    }

    @Override
    public void encode(ByteBuf buffer) {
        writeString(buffer, socketId);
        writeString(buffer, null);
    }

    @Override
    public void decode(ByteBuf buffer) {
        socketId = readString(buffer);
        passphrase = readString(buffer);
    }

    public String toString() {
        return "LoginPacket(socketId=" + socketId + ", " + "passphrase=" + passphrase + ")";
    }
}
