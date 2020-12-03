package com.DragonNet.packets;

import com.DragonNet.packets.impl.GenericPacket;
import com.DragonNet.packets.impl.DisconnectPacket;
import com.DragonNet.packets.impl.KeepAlivePacket;
import com.DragonNet.packets.impl.LoginPacket;
import com.DragonNet.packets.protocol.DragonNetProtocol;

public class PacketManager {

    private final static Packet[] packets = new Packet[255];

    public static Packet getPacket(int packetId) {
        if (packetId <= 255 && packets[packetId] != null) {
            return packets[packetId].clone();
        }

        return packets[0];
    }

    public static void registerPacket(Packet packet, boolean force) {
        if (packet.getPacketId() < 0 || packet.getPacketId() > 255) {
            throw new IllegalArgumentException("Packet id cannot be lesser than 0 or bigger than 255");
        } else if (packets[packet.getPacketId()] != null && !force) {
            throw new IllegalArgumentException("Cannot re-register a packet that is already been registered.");
        }

        packets[packet.getPacketId()] = packet;
    }

    static {
        packets[0] = new GenericPacket();
        packets[DragonNetProtocol.LOGIN_PROTOCOL] = new LoginPacket();
        packets[DragonNetProtocol.DISCONNECT_PACKET] = new DisconnectPacket();
        packets[DragonNetProtocol.KEEP_ALIVE_PACKET] = new KeepAlivePacket();
    }
}
