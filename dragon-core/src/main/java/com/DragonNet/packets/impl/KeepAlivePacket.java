package com.DragonNet.packets.impl;

import com.DragonNet.packets.protocol.DragonNetProtocol;
import com.DragonNet.packets.Packet;

public class KeepAlivePacket extends Packet {

    @Override
    public int getPacketId() {
        return DragonNetProtocol.KEEP_ALIVE_PACKET;
    }

    @Override
    public Packet clone() {
        return new KeepAlivePacket();
    }

    @Override
    public String toString() {
        return "KeepAlivePacket()";
    }
}
