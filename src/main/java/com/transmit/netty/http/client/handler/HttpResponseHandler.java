package com.transmit.netty.http.client.handler;

import com.transmit.netty.ResponseHandler;
import com.transmit.netty.connect.Connection;
import com.transmit.netty.connect.ConnectionManager;
import com.transmit.netty.connect.DefaultConnection;
import com.transmit.netty.connect.NettyConnection;
import com.transmit.netty.http.client.packet.HttpResponse;
import com.transmit.utils.BuffUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Optional;

@Slf4j
public class HttpResponseHandler extends ChannelInboundHandlerAdapter implements ResponseHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("channelRead");
        Connection conn = ConnectionManager.getConnection(ctx.channel());
        HttpResponse response = ((HttpResponse)((DefaultConnection) conn).getResponse(conn));
        if(msg instanceof DefaultHttpResponse) {
            DefaultHttpResponse r = (DefaultHttpResponse) msg;
            response.setCode((r.status().code()));
            if (msg instanceof ByteBuf) ((ByteBuf) msg).release();
            return;
        }
        // 内容
        if(msg instanceof DefaultHttpContent){
            ByteBuf buf = ((DefaultHttpContent) msg).content();
            response.appendBody(BuffUtils.convertByteBufToString(buf));
            if(buf != null)  buf.release();
            return;
        }

        if(msg instanceof LastHttpContent){
            response.setEnd(true);
            log.info("channelRead end");
            return;
        }
        log.info("channelRead ?????????");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive");
        NettyConnection conn = (NettyConnection) ConnectionManager.getConnection(ctx.channel());
        Optional.ofNullable(conn).ifPresent(c ->{
            c.connected();
        });
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        NettyConnection conn = (NettyConnection) ConnectionManager.getConnection(channel);
        Optional.ofNullable(conn).ifPresent(c ->{
            conn.close();
            // 打印日志
            printLog(conn, channel);
        });
        Optional.ofNullable(conn).orElseGet(()->{
            log.error("通道{}找不到连接", ctx.channel());
            return conn;
        });
        super.channelInactive(ctx);
    }

    /**
     * 输出断开日志
     * @param conn
     * @param channel
     */
    private void printLog(NettyConnection conn, Channel channel){
        InetSocketAddress localSocket = (InetSocketAddress) channel.localAddress();
        String localIp = localSocket.getAddress().getHostAddress();
        int localPort = localSocket.getPort();
        InetSocketAddress remoteSocket = (InetSocketAddress) channel.remoteAddress();
        String remoteIp = remoteSocket.getAddress().getHostAddress();
        int remotePort = remoteSocket.getPort();
        log.info("{} {}:{} 与远程 {}:{} 断开连接", conn.getTag(), localIp, localPort, remoteIp, remotePort);
    }
}
