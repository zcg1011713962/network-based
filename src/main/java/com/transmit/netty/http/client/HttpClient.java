package com.transmit.netty.http.client;

import com.transmit.netty.connect.ConnectionManager;
import com.transmit.netty.connect.NettyConnection;
import com.transmit.netty.http.client.packet.HttpRequest;
import com.transmit.netty.http.client.packet.HttpResponse;
import com.transmit.utils.UrlUtils;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HttpClient extends NettyConnection {
    private int readTimeOut = 5;

    public HttpClient(){
        super();
    }

    /**
     * netty连接
     * @param url
     */
    public boolean createNettyConnect(String url){
        try {
            URI uri= UrlUtils.checkUrl(url);
            ChannelFuture channelFuture = connect(UrlUtils.checkHost(uri), UrlUtils.checkPort(uri));
            ChannelFuture future = channelFuture.sync();
            if(future.isSuccess()){
                ConnectionManager.setConnection(tag,this);
                return true;
            }
        }catch (Exception e){
            log.error("{} 建立连接时:{}", tag, e.getMessage());
        }
        return false;
    }

    public HttpResponse send(HttpRequest request) throws InterruptedException {
        ChannelFuture future = this.channel.writeAndFlush(request.get());
        if (future.sync().isSuccess()) {
            int i = 0;
            while(response == null || !((HttpResponse) response).isEnd()){
                if(++i > readTimeOut) break;
                TimeUnit.SECONDS.sleep(1);
            }
            return (HttpResponse) response;
        }
        return null;
    }


}
