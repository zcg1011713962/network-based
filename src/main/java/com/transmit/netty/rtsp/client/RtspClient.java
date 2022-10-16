package com.transmit.netty.rtsp.client;

import com.transmit.netty.connect.NettyConnection;
import com.transmit.utils.UrlUtils;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

@Slf4j
public class RtspClient extends NettyConnection {

    public ChannelFuture nettyConnect(String url){
        URI uri= UrlUtils.checkUrl(url);
        ChannelFuture channelFuture = null;
        try {
            channelFuture = connect(UrlUtils.checkHost(uri), UrlUtils.checkPort(uri));
            channelFuture.sync();
        }catch (Exception e){
            log.error("{} 建立连接时:{}", tag, e.getMessage());
        }
        return channelFuture;
    }

}
