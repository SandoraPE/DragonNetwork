package com.DragonNet.packets.impl;

import com.DragonNet.packets.Packet;
import com.DragonNet.packets.DragonNetProtocol;

public class ConsoleRedirectPacket extends Packet {

    public String messageBuffer;

    @Override
    public int getPacketId() {
        return DragonNetProtocol.CONSOLE_REDIRECT;
    }

    @Override
    public Packet clone() {
        return new ConsoleRedirectPacket();
    }
}
