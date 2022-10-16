package com.transmit.netty.rtsp.client;

import com.transmit.netty.http.client.handler.HeartBeatHandler;
import com.transmit.netty.rtsp.client.codec.RtspDecoder;
import com.transmit.netty.rtsp.client.handler.RtspResponseHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Inbound（入站）事件由Inbound处理程序以自下而上的方向处理
 * Outbound（出站）事件由Outbound处理程序在自上而下的方向进行处理
 */
public class RtspClientlInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("rtspIdleStateHandler", new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
        pipeline.addLast("rtspHeartBeatHandler", new HeartBeatHandler());
        pipeline.addLast("rtspDecoder", new RtspDecoder());
        //pipeline.addLast("rtspLoggingHandler", new LoggingHandler(LogLevel.ERROR));
        pipeline.addLast("rtspClientHandler",new RtspResponseHandler());
        // pipeline.addLast("rtspEncoder", new RtspEncoder());
    }
}
