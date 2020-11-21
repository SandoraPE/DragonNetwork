package com.DragonNet.packets.impl;

import com.DragonNet.packets.DragonNetProtocol;
import com.DragonNet.packets.Packet;

public class KeepAlivePacket extends Packet {

    @Override
    public int getPacketId() {
        return DragonNetProtocol.PING_PACKET;
    }

    @Override
    public Packet clone() {
        return new KeepAlivePacket();
    }
}
