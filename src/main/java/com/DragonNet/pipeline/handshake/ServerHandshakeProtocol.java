package com.DragonNet.pipeline.handshake;

import com.DragonNet.session.SessionHandler;
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

    // Handshake protocol (This is my interpretation of handshake protocol and is not using
    // the SYN-ACK protocol), this byte will have to be sent from client's stream in order to verify.
    // Otherwise the connection will be dropped. This way here is to perform drop-connection on idle
    // sockets or unwanted connection.
    private static final byte[] HANDSHAKE_PROTOCOL = ByteBufUtil.decodeHexDump("4f6820796f752061726520612066757272793f");

    private final SocketChannel channel;
    private final SessionHandler session;

    public ServerHandshakeProtocol(SessionHandler session, SocketChannel channel) {
        this.channel = channel;
        this.session = session;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        ctx.pipeline().addLast("timeout", new ReadTimeoutHandler(16));
        ctx.fireChannelRegistered();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
        out.writeBytes(msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
        // Direct buffer.
        if (buffer.readableBytes() >= HANDSHAKE_PROTOCOL.length) {
            byte[] bytes = new byte[HANDSHAKE_PROTOCOL.length];

            for (int i = 0; i < HANDSHAKE_PROTOCOL.length; ++i) {
                bytes[i] = buffer.readByte();
            }

            if (Arrays.equals(bytes, HANDSHAKE_PROTOCOL)) {
                ctx.channel().writeAndFlush(Unpooled.buffer().writeBytes(ByteBufUtil.decodeHexDump("5965732c20796573204920616d2061206675727279")));

                ctx.pipeline().remove("timeout");
                ctx.pipeline().remove("handshake");

                // Reference: https://stackoverflow.com/questions/26549567/best-way-to-send-out-two-byte-buffers-header-body-in-netty
                //
                // So default MTU for TCP socket transfer is 1500 bytes, this is interesting since I have no literal
                // way to understand this MTU thingy until now, so I have chosen this method to read packets at a maximum
                // length of a short which is 0x7FFF in hexadecimal. According to the referenced link, I will have to put a series
                // of byte which contains the length of a message following by the raw data itself.
                // Therefore: "Payload length" "Actual Payload" "Payload length " "Actual Payload" ...

                ctx.pipeline().addLast(new LengthFieldBasedFrameDecoder(0x7FFF, 0, 2, 0, 2));

                ctx.pipeline().addLast("timeout", new ReadTimeoutHandler(60));
                ctx.pipeline().addLast("packetsGroup", new DragonNetPacketManager(channel));
                ctx.pipeline().addLast("packetTelemetry", new DragonNetPacketTelemetry(session));
            }
        }
    }
}
