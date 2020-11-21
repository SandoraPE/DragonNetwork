package com.DragonNet.packets.impl;

import com.DragonNet.packets.Packet;
import com.DragonNet.packets.DragonNetProtocol;

public class ConsoleCommandPacket extends Packet {

    public String command;

    @Override
    public int getPacketId() {
        return DragonNetProtocol.CONSOLE_COMMAND;
    }

    @Override
    public Packet clone() {
        return new ConsoleCommandPacket();
    }
}
