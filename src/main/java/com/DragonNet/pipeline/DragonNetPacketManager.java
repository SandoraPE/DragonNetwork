package com.DragonNet.pipeline;

import com.DragonNet.packets.Packet;
import com.DragonNet.utils.PacketRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class DragonNetPacketManager extends ByteToMessageCodec<Packet> {

    // Socket pipeline datagram:
    // DragonNetPacketManager [Encode] -> DragonNetPacketTelemetry -> DragonNetPacketManager [Decode]

    private final SocketChannel channel;

    public DragonNetPacketManager(SocketChannel socket) {
        this.channel = socket;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws InterruptedException {
        // "Length" "Data"
        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeInt(msg.getPacketId());
        msg.encode(buffer);

        out.writeShort(buffer.readableBytes());
        out.writeBytes(buffer);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        Packet pk = PacketRegistry.getPacket(in.readInt());
        pk.setChannel(channel);
        pk.decode(in);

        out.add(pk);

        // Clear out byte buffer from context, this will avoid decode() function repeating itself
        in.clear();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();

        log.info("[{}] has disconnected from the server.", ctx.channel().remoteAddress().toString());
    }
}
