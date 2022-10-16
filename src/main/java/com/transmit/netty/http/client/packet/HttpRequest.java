package com.transmit.netty.http.client.packet;

import com.transmit.netty.BasePacket;
import com.transmit.utils.UrlUtils;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import org.apache.commons.lang3.StringUtils;


public class HttpRequest implements BasePacket {
    private HttpMethod httpMethod;
    private HttpVersion httpVersion;
    private AsciiString keepAlive;
    private String parm;
    private String url;
    private String cookie;
    private DefaultFullHttpRequest request;
    private HttpRequest(){}

    private HttpRequest(Builder builder){
        this.httpMethod = builder.httpMethod;
        this.httpVersion = builder.httpVersion;
        this.url = builder.url;
        this.keepAlive = builder.keepAlive;
        this.parm = builder.parm;
        this.cookie = builder.cookie;
        this.buildeRequest();
    }

    public DefaultFullHttpRequest get(){
        return request;
    }

    public void buildeRequest(){
        String host = UrlUtils.checkUrl(url).getHost();
        int port = UrlUtils.checkUrl(url).getPort();
        String str = host + ":" + port;
        request = new DefaultFullHttpRequest(this.httpVersion, this.httpMethod, this.parm);
        request.headers().set(HttpHeaderNames.HOST, str);
        request.headers().set(HttpHeaderNames.CONNECTION, this.keepAlive);
        request.headers().set(HttpHeaderNames.ACCEPT,"*/*");
        request.headers().set(HttpHeaderNames.USER_AGENT,"VLC/3.0.12 LibVLC/3.0.12");
        if(!StringUtils.isBlank(cookie)){
            request.headers().set(HttpHeaderNames.COOKIE,this.cookie);
        }
    }

    public static class Builder{
        private HttpMethod httpMethod;
        private HttpVersion httpVersion;
        private String url;
        private AsciiString keepAlive;
        private String parm;
        private String cookie;

        public Builder(String url,String parm, HttpMethod httpMethod, HttpVersion httpVersion, AsciiString keepAlive){
            this.url = url;
            this.httpMethod = httpMethod;
            this.httpVersion = httpVersion;
            this.keepAlive = keepAlive;
            this.parm = parm;
        }

        public Builder(String url,String parm, HttpMethod httpMethod, HttpVersion httpVersion, AsciiString keepAlive,String cookie){
            this.url = url;
            this.httpMethod = httpMethod;
            this.httpVersion = httpVersion;
            this.keepAlive = keepAlive;
            this.parm = parm;
            this.cookie = cookie;
        }

        public HttpRequest build(){
            return new HttpRequest(this);
        }

    }
}
