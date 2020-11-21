package com.DragonNet.pipeline.handshake;

import com.DragonNet.packets.session.ClientSession;
import com.DragonNet.pipeline.DragonNetPacketManager;
import com.DragonNet.pipeline.DragonNetPacketTelemetry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class ClientHandshakeProtocol extends ByteToMessageCodec<ByteBuf> {

    private static final byte[] HANDSHAKE_PROTOCOL = ByteBufUtil.decodeHexDump("5965732c20796573204920616d2061206675727279");

    private final SocketChannel channel;
    private final ClientSession handler;

    public ClientHandshakeProtocol(ClientSession handler, SocketChannel channel) {
        this.channel = channel;
        this.handler = handler;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
        out.writeBytes(msg);
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
        // Direct buffer.
        if (buffer.readableBytes() >= HANDSHAKE_PROTOCOL.length) {
            byte[] bytes = new byte[HANDSHAKE_PROTOCOL.length];

            for (int i = 0; i < HANDSHAKE_PROTOCOL.length; ++i) {
                bytes[i] = buffer.readByte();
            }

            if (Arrays.equals(bytes, HANDSHAKE_PROTOCOL)) {
                ctx.pipeline().remove("handshake");

                ctx.pipeline().addLast(new LengthFieldBasedFrameDecoder(0x7FFF, 0, 2, 0, 2));

                ctx.pipeline().addLast("packetsGroup", new DragonNetPacketManager(channel));
                ctx.pipeline().addLast("packetTelemetry", new DragonNetPacketTelemetry(handler));

                handler.setChannel(channel);
            }
        }
    }
}
