package com.transmit.netty.connect;

import com.transmit.netty.BootstrapManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.time.Instant;
@Slf4j
public class NettyConnection extends DefaultConnection{
    protected Channel channel;
    @Override
    public ChannelFuture connect(String host, int port) {
        connectTime = Instant.now().toEpochMilli();
        ChannelFuture channelFuture = BootstrapManager.getBootstrap(this).connect(host, port);
        this.channel = channelFuture.channel();
        return channelFuture;
    }

    @Override
    public void connected() {
        connectedTime = Instant.now().toEpochMilli();
    }

    @Override
    public ChannelFuture close() {
        closedTime = Instant.now().toEpochMilli();
        if(channel != null && channel.isOpen()){
            return channel.close();
        }
        return null;
    }

    @Override
    public void bind(int port) {
        try{
            ChannelFuture channelFuture = BootstrapManager.getServerBootstrap(this).bind(new InetSocketAddress(port)).sync();
            log.info("websocket bind port:{}", port);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
           log.error("{}", e);
        }
    }
}
