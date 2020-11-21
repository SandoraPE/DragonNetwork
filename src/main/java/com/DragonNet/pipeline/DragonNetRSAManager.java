package com.DragonNet.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.List;

/**
 * Custom RSA Decryption/Encryption manager.
 */
@Log4j2
public class DragonNetRSAManager extends ByteToMessageCodec<ByteBuf> {

    public DragonNetRSAManager(File keyLocation) {

    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

    }
}
