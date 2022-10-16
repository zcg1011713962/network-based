package com.transmit.netty.connect;


import io.netty.channel.ChannelFuture;

public interface Connection {

    ChannelFuture connect(String host, int port);

    void connected();

    ChannelFuture close();

    void bind(int port);

}

