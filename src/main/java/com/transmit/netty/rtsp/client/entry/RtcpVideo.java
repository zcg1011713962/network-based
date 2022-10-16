package com.transmit.netty.rtsp.client.entry;

import io.netty.buffer.ByteBuf;

public class RtcpVideo {
    private byte [] data;
    private int len;

    public RtcpVideo(int len, ByteBuf buf){
        data = new byte[len];
        buf.readBytes(data, 0, len);
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
