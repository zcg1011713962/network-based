package com.transmit.netty.connect;

import com.transmit.netty.Response;
import com.transmit.netty.http.client.HttpClient;
import com.transmit.netty.http.client.packet.HttpResponse;
import io.netty.channel.ChannelFuture;

import java.util.UUID;

public abstract class DefaultConnection implements Connection{
    protected String tag;
    protected long connectTime;
    protected long connectedTime;
    protected long readEndTime;
    protected long closedTime;
    protected Response response;

    public DefaultConnection(){
        buildTag();
    }
    @Override
    public ChannelFuture connect(String host, int port) {
        return null;
    }

    private void buildTag(){
        tag = UUID.randomUUID().toString();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Response getResponse(Connection conn) {
        if(response == null){
            if(conn instanceof HttpClient){
                response = new HttpResponse();
            }
        }
        return response;
    }

    public long getConnectTime() {
        return connectTime;
    }
    public long getConnectedTime() {
        return connectedTime;
    }

    public long getReadEndTime() {
        return readEndTime;
    }
}
