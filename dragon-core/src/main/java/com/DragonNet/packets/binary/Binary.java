package com.DragonNet.packets.binary;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

/**
 * Half-implementation of PocketMine-MP NetworkBinaryStream.
 */
public class Binary {

    public static String readString(ByteBuf buf) {
        int length = (int) Binary.readUnsignedVarInt(buf);
        if (length == 0) {
            return null;
        }

        byte[] bytes = new byte[length];
        for (int i = 0; i < length; ++i) {
            bytes[i] = buf.readByte();
        }

        return new String(bytes, CharsetUtil.UTF_8);
    }

    public static void writeString(ByteBuf buf, String string) {
        byte[] str;
        if (string == null) {
            str = new byte[0];
        } else {
            str = string.getBytes(CharsetUtil.UTF_8);
        }

        writeUnsignedInt(buf, str.length);
        buf.writeBytes(str);
    }

    public static void writeUnsignedInt(ByteBuf buffer, long value) {
        do {
            byte temp = (byte) (value & 0x7F);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
            if (value != 0) {
                temp |= 0x80;
            }
            buffer.writeByte(temp);
        } while (value != 0);
    }

    public static long readUnsignedVarInt(ByteBuf stream) {
        return read(stream, 5);
    }

    public static long readUnsignedVarLong(ByteBuf stream) {
        return read(stream, 10);
    }

    private static long read(ByteBuf stream, int maxSize) {
        long value = 0;
        int size = 0;
        int b;
        while (((b = stream.readByte()) & 0x80) == 0x80) {
            value |= (long) (b & 0x7F) << (size++ * 7);
            if (size >= maxSize) {
                throw new IllegalArgumentException("VarLong too big");
            }
        }

        return value | ((long) (b & 0x7F) << (size * 7));
    }
}
