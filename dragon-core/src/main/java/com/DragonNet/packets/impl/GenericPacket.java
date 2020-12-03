package com.DragonNet.packets.impl;

import com.DragonNet.packets.Packet;
import io.netty.buffer.ByteBuf;
import lombok.extern.log4j.Log4j2;

import java.util.Base64;

@Log4j2
public class GenericPacket extends Packet {

    @Override
    public void decode(ByteBuf buffer) {
        byte[] data = new byte[buffer.readableBytes()];
        for (int i = 0; i < buffer.readableBytes(); i++) {
            data[i] = buffer.readByte();
        }

        log.info("Unhandled packet {}", Base64.getEncoder().encodeToString(data));
    }

    @Override
    public int getPacketId() {
        return 0;
    }

    @Override
    public Packet clone() {
        return new GenericPacket();
    }
}
