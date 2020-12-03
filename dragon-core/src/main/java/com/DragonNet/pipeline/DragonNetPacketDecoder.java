package com.DragonNet.pipeline;

import com.DragonNet.packets.Packet;
import com.DragonNet.packets.PacketManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class DragonNetPacketDecoder extends ByteToMessageCodec<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) {
        // "Length" "Data"
        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeInt(msg.getPacketId());
        msg.encode(buffer);

        out.writeInt(buffer.readableBytes());
        out.writeBytes(buffer);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        Packet pk = PacketManager.getPacket(in.readInt());
        pk.decode(in);

        if (in.readableBytes() > 0) {
            int leftover = in.readableBytes();

            byte[] bytes = new byte[leftover];

            for (int i = 0; i < leftover; i++) {
                bytes[i] = in.readByte();
            }

            log.info("Still {} bytes unread in pid={}: 0x{}", leftover, pk.getPacketId(), ByteBufUtil.hexDump(bytes));
        }

        out.add(pk);
    }
}
