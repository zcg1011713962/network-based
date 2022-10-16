package com.transmit.utils;

import io.netty.buffer.ByteBuf;

public class BuffUtils {
    // 计算2个字节长度
    public static int len (byte one, byte two){
        StringBuffer sbf= new StringBuffer();
        sbf.append(String.format("%02x ", one).trim())
                .append(String.format("%02x ", two).trim());
        return Integer.parseInt(sbf.toString(), 16);
    }

    /**
     * 转成字符串
     * @param buf
     * @return
     */
    public static String convertByteBufToString(ByteBuf buf) {
        String str;
        if(buf.hasArray()) { // 处理堆缓冲区
            str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
        } else { // 处理直接缓冲区以及复合缓冲区
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            str = new String(bytes, 0, buf.readableBytes());
        }
        return str;
    }

}
