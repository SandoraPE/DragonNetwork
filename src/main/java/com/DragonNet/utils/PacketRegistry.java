package com.DragonNet.utils;

import com.DragonNet.packets.impl.DefaultPacketHandler;
import com.DragonNet.packets.impl.LoginPacket;
import com.DragonNet.packets.Packet;

public class PacketRegistry {

    private final static Packet[] packets = new Packet[255];

    public static Packet getPacket(int packetId) {
        if (packetId <= 255 && packets[packetId] != null) {
            return packets[packetId].clone();
        }

        return packets[0];
    }

    static {
        packets[0] = new DefaultPacketHandler();
        packets[1] = new LoginPacket();
    }
}
