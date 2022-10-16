package com.transmit.netty.rtsp.client.codec;

import com.transmit.netty.rtsp.client.entry.RtcpAudio;
import com.transmit.netty.rtsp.client.entry.RtcpVideo;
import com.transmit.netty.rtsp.client.entry.RtpAudio;
import com.transmit.netty.rtsp.client.entry.RtpVideo;
import com.transmit.netty.rtsp.client.packet.RtspResPacket;
import com.transmit.utils.BuffUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class RtspDecoder extends ByteToMessageDecoder {
    private static ConcurrentMap<Channel, RtspResPacket> responsePacketMap = new ConcurrentHashMap<>();
    private static ByteBuf warrappBuff;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < 1){
            return;
        }
        RtspResPacket rtspResPacket = putPacketMap(ctx);
        if(rtspResPacket.getType() == 0){
            // rtsp协议
            String text = getRow(in, ctx.channel());
            log.info("解码器收到响应报文:{}", text);
            rtspResPacket = rowToEntry(text, ctx);
            if(rtspResPacket == null) {
                log.error("响应报文解析异常:{}", text);
            }
            in.readBytes(in.readableBytes());
            out.add(rtspResPacket);
        }else if (rtspResPacket.getType() == 1){
            byte [] b = new byte[in.readableBytes()];
            in.readBytes(b);
            if(b[0] == 0x52){
                return;
            }
            // log.info(ByteBufUtil.hexDump(b));
            if(warrappBuff == null){
                warrappBuff = Unpooled.wrappedBuffer(b);
            }else{
                byte [] w = new byte[warrappBuff.readableBytes()];
                warrappBuff.readBytes(w);
                warrappBuff = Unpooled.wrappedBuffer(w, b);
            }
            // 解析RTP
            rtp(out);
        }
    }

    private void rtp(List<Object> out){
        // 1B 协议类型 0x00 video rtp   0x01 video rtcp 0x02 audio rtp 0x03 audio rtcp
        byte type = warrappBuff.getByte(1);
        // 2B data length
        int len = BuffUtils.len(warrappBuff.getByte(2), warrappBuff.getByte(3));
        if(warrappBuff.readableBytes() - 4 >= len){
            warrappBuff.skipBytes(4); // rtsp I frame
            rtpHead();
            len = len - 12;
            switch(type){
                case 0x00:
                    log.info("video rtp");
                    log.info(ByteBufUtil.hexDump(warrappBuff,0, len));
                    RtpVideo rtpVideo = new RtpVideo(len, warrappBuff);
                    out.add(rtpVideo);
                    break;
                case 0x01:
                    log.info("video rtcp");
                    log.info(ByteBufUtil.hexDump(warrappBuff,0, len));
                    RtcpVideo rtcpVideo = new RtcpVideo(len, warrappBuff);
                    out.add(rtcpVideo);
                    break;
                case 0x02:
                    log.info("audio rtp");
                    log.info(ByteBufUtil.hexDump(warrappBuff,0, len));
                    RtpAudio rtpAudio = new RtpAudio(len, warrappBuff);
                    out.add(rtpAudio);
                    break;
                case 0x03:
                    log.info("audio rtcp");
                    log.info(ByteBufUtil.hexDump(warrappBuff,0, len));
                    RtcpAudio rtcpAudio = new RtcpAudio(len, warrappBuff);
                    out.add(rtcpAudio);
                    break;
                default:
                    break;
            }
        }
        warrappBuff.discardReadBytes();
    }

    private void rtpHead(){
        // rtp包头
        ByteBuf buf = warrappBuff.copy(0,12);
        warrappBuff.skipBytes(12);
        warrappBuff.discardReadBytes();
        byte b1= buf.readByte();
        byte b2= buf.readByte();
        byte b3= buf.readByte();
        byte b4= buf.readByte();
        byte b5= buf.readByte();

    }

    public RtspResPacket rowToEntry(String text, ChannelHandlerContext ctx){
        RtspResPacket rtspResPacket = putPacketMap(ctx).clear();
        String [] strs = text.split("\r\n");
        if(strs == null || strs.length == 0){
            return null;
        }
        for (String str : strs) {
            if (str.contains("RTSP")) {
                String[] s = str.split(String.valueOf(StringUtil.SPACE));
                rtspResPacket.setStatus(Integer.parseInt(s[1]));
            } else if (str.contains("CSeq")) {
                String[] s = str.split(String.valueOf(StringUtil.SPACE));
                rtspResPacket.setCseq(s[1]);
            } else if (str.contains("Server")) {
                String[] s = str.split(String.valueOf(StringUtil.SPACE));
                rtspResPacket.setServer(s[1]);
            } else if (str.contains("Cache-Control")) {
                String[] s = str.split(String.valueOf(StringUtil.SPACE));
                rtspResPacket.setCacheControl(s[1]);
            } else if (str.contains("Public")) {
                String[] s = str.replace("Public:", "").split(String.valueOf(StringUtil.COMMA));
                List options = Arrays.asList(s);
                rtspResPacket.setOption(options);
            } else if (str.contains("application/sdp")){
                String[] s = str.split(String.valueOf(StringUtil.SPACE));
                rtspResPacket.setContentType(s[1]);
            } else if (str.contains("Transport")){
                String[] s = str.split(String.valueOf(StringUtil.SPACE));
                rtspResPacket.setTransport(s[1]);
            } else if (str.contains("Session")){
                String[] s1 = str.split(String.valueOf(StringUtil.SPACE));
                String [] s = s1[1].split(";");
                rtspResPacket.setSession(s[1]);
            }
        }
        return rtspResPacket;
    }


    private String getRow(ByteBuf in, Channel channel) throws Exception{
        // ByteBuf buf = wrappedBuffer(channel, in);
        // \r\n = 0x0d 0x0a 空格 = 0x20
        // 以 /r/n /r/n 结尾作为一段报文
        int length = in.readableBytes();
        byte [] valid = new byte[length];
        // 读取报文
        in.readBytes(valid,0, valid.length);
        return new String(valid, 0, valid.length);
        // 防止粘包
        /*for(int i = 0 ; i < length; i++){
            valid [i] = buf.getByte(i);
            if(buf.readableBytes() > 3){
                byte a = buf.getByte(i);
                byte b = buf.getByte(i + 1);
                byte c = buf.getByte(i + 2);
                byte d = buf.getByte(i + 3);
                // /r/n /r/n
                if(a == 0x0d && b == 0x0a && c == 0x0d && d ==  0x0a){
                    valid [i + 1] = b;
                    valid [i + 2] = c;
                    valid [i + 3] = d;
                    if(buf.readableBytes() > 4 && buf.getByte(i + 4) == 0x76 && buf.getByte(i + 5) == 0x3d){
                        // sdp

                    }
                    resBuf(channel, buf, valid, length);
                    return new String(valid, 0, valid.length);
                }
            }
        }
        // 没有以/r/n /r/n 结尾
        channelMap.put(channel, buf);
        return StringUtils.EMPTY;*/
    }

   /* private ByteBuf wrappedBuffer(Channel channel, ByteBuf in){
        ByteBuf old =  channelMap.get(channel);
        if(old != null){
            return Unpooled.wrappedBuffer(old.copy(), in.copy());
        }
        return in;
    }*/

    /*private void resBuf(Channel channel, ByteBuf buf, byte[] valid, int length){
        if(valid.length == length){
            return;
        }
        ByteBuf resBuf = Unpooled.wrappedBuffer(ByteBufUtil.getBytes(buf, valid.length + 1, length - valid.length));
        channelMap.put(channel, resBuf);
    }*/

    private RtspResPacket putPacketMap(ChannelHandlerContext ctx){
        if(responsePacketMap.get(ctx.channel()) == null){
            RtspResPacket rtspResPacket= new RtspResPacket();
            responsePacketMap.put(ctx.channel(), rtspResPacket);
            return rtspResPacket;
        }
        return responsePacketMap.get(ctx.channel());
    }

}
