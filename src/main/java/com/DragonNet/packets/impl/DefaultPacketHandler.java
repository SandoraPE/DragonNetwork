package com.DragonNet.packets.impl;

import com.DragonNet.packets.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

public class DefaultPacketHandler extends Packet {

    @Override
    public void decode(ByteBuf buffer) {
        System.out.println("Unhandled packet, 0x" + ByteBufUtil.hexDump(buffer));
    }

    @Override
    public int getPacketId() {
        return 0;
    }

    @Override
    public Packet clone() {
        return new DefaultPacketHandler();
    }
}
