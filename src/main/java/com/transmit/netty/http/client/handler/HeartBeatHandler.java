package com.transmit.netty.http.client.handler;

import com.transmit.netty.connect.ConnectionManager;
import com.transmit.netty.connect.NettyConnection;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 心跳检测
 */
@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        NettyConnection conn = (NettyConnection) ConnectionManager.getConnection(ctx.channel());
        // 超时事件
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleEvent = (IdleStateEvent) evt;
            if(conn != null){
                if (idleEvent.state() == IdleState.READER_IDLE) {
                    log.error("{}触发读超时事件", conn.getTag());
                } else if (idleEvent.state() == IdleState.WRITER_IDLE) {
                    log.error("{}触发写超时事件", conn.getTag());
                } else if (idleEvent.state() == IdleState.ALL_IDLE) {
                    log.error("{}触发超时事件", conn.getTag());
                }
            }else{
                log.error("通道{}触发超时事件", ctx.channel().id());
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
