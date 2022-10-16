package com.transmit.netty.http.client;

import com.transmit.netty.http.client.handler.HeartBeatHandler;
import com.transmit.netty.http.client.handler.HttpResponseHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class HttpClientlInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("httpIdleStateHandler", new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
        pipeline.addLast("httpHeartBeatHandler", new HeartBeatHandler());
        //pipeline.addLast("httpLoggingHandler", new LoggingHandler(LogLevel.ERROR));
        pipeline.addLast("httpDecoder", new HttpResponseDecoder(8192, 8192, 1024*65536, false));
        pipeline.addLast("httpResponseHandler", new HttpResponseHandler());
        pipeline.addLast("httpEncoder", new HttpRequestEncoder());
    }
}
