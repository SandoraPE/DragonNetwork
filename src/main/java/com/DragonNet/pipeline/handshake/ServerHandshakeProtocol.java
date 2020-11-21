package com.DragonNet.pipeline.handshake;

import com.DragonNet.pipeline.DragonNetPacketManager;
import com.DragonNet.pipeline.DragonNetPacketTelemetry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.List;

/**
 * Initialize handshake protocol, otherwise drop connection.
 */
@Log4j2
public class ServerHandshakeProtocol extends ByteToMessageCodec<ByteBuf> {

    private static final byte[] HANDSHAKE_PROTOCOL = ByteBufUtil.decodeHexDump("4f6820796f752061726520612066757272793f");

    private final SocketChannel channel;

    public ServerHandshakeProtocol(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        ctx.pipeline().addLast("timeout", new ReadTimeoutHandler(16));
        ctx.fireChannelRegistered();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        out.writeBytes(msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        // Direct buffer.
        if (buffer.readableBytes() >= HANDSHAKE_PROTOCOL.length) {
            byte[] bytes = new byte[HANDSHAKE_PROTOCOL.length];

            for (int i = 0; i < HANDSHAKE_PROTOCOL.length; ++i) {
                bytes[i] = buffer.readByte();
            }

            if (Arrays.equals(bytes, HANDSHAKE_PROTOCOL)) {
                out.add(Unpooled.buffer().writeBytes(ByteBufUtil.decodeHexDump("5965732c20796573204920616d2061206675727279")));

                ctx.pipeline().remove("timeout");
                ctx.pipeline().remove("handshake");

                ctx.pipeline().addLast(new LengthFieldBasedFrameDecoder(0x7FFF, 0, 2, 0, 2));

                ctx.pipeline().addLast("packetsGroup", new DragonNetPacketManager(channel));
                ctx.pipeline().addLast("packetTelemetry", new DragonNetPacketTelemetry(channel));
            }
        }
    }
}
