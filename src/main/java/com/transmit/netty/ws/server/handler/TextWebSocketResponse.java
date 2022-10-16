package com.transmit.netty.ws.server.handler;

import com.transmit.netty.ResponseHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.RandomAccessFile;

@Slf4j
public class TextWebSocketResponse extends SimpleChannelInboundHandler<TextWebSocketFrame> implements ResponseHandler {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String text = msg.text();
        log.info("TextWebSocketFrameHandler:{}", text);
        if("yuv".equals(text)){
            File file = new File("D://video/yuv/output-1.png");
            RandomAccessFile randomAccessFile =new RandomAccessFile("D://video/yuv/output-1.png","rw");
            int a = (int) file.length();
            byte [] b= new byte[a];
            int len;
            while((len = randomAccessFile.read(b)) != -1){
                ByteBuf buf = Unpooled.buffer(len);
                buf.writeBytes(b, 0, len);
                ctx.channel().writeAndFlush(new BinaryWebSocketFrame(buf));
            }
        }
        log.info("TextWebSocketFrameHandler end");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("TextWebSocketFrameHandler channelActive");
    }
}
