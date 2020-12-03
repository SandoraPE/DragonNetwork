package com.DragonNet.packets.impl;

import com.DragonNet.packets.protocol.DragonNetProtocol;
import com.DragonNet.packets.Packet;
import com.DragonNet.packets.binary.Binary;
import io.netty.buffer.ByteBuf;

public class LoginPacket extends Packet {

    public int assignedNode = 0;
    public String password = "";
    public boolean isPublicKeyAuth = false;

    public byte[] publicKeyData = new byte[0];

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
        buffer.writeInt(assignedNode);
        Binary.writeString(buffer, password);
        buffer.writeBoolean(isPublicKeyAuth);

        buffer.writeInt(publicKeyData.length);
        buffer.writeBytes(publicKeyData);
    }

    @Override
    public void decode(ByteBuf buffer) {
        assignedNode = buffer.readInt();
        password = Binary.readString(buffer);
        isPublicKeyAuth = buffer.readBoolean();

        int length = buffer.readInt();

        publicKeyData = new byte[length];
        for (int i = 0; i < length; i++) {
            publicKeyData[i] = buffer.readByte();
        }
    }

    @Override
    public String toString() {
        return "LoginPacket(socketId=" + assignedNode + ", " + "passphrase=" + password + ", isPublicKeyAuth=" + isPublicKeyAuth + ")";
    }
}
