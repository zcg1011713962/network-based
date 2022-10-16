package com.transmit.netty;

import com.transmit.netty.connect.Connection;
import com.transmit.netty.http.client.HttpClient;
import com.transmit.netty.http.client.HttpClientlInitializer;
import com.transmit.netty.rtsp.client.RtspClient;
import com.transmit.netty.rtsp.client.RtspClientlInitializer;
import com.transmit.netty.ws.server.WebSocketChannelInitializer;
import com.transmit.netty.ws.server.WebSocketServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BootstrapManager {
    private static EventLoopGroup group = null;
    private static EventLoopGroup workGroup = null;
    /**
     * 获取 Bootstrap
     * @return
     */
    public static Bootstrap getBootstrap(Connection connection){
        Bootstrap bootstrap= new Bootstrap();
        bootstrap.group(getClientGroup())
                .channel(NioSocketChannel.class)
                .handler(getChannelHandler(connection))
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_RCVBUF, Integer.MAX_VALUE)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 1024, 65536 * 100));
        return bootstrap;
    }

    /**
     * 作为服务端
     * @param connection
     * @return
     */
    public static ServerBootstrap getServerBootstrap(Connection connection){
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,getServerWokerGroup()).channel(NioServerSocketChannel.class)
                .childHandler(getChannelHandler(connection));
        return serverBootstrap;
    }

    /**
     * 获取客户端工作线程组
     * @return
     */
    private static EventLoopGroup getClientGroup() {
        if (group == null) {
            synchronized(BootstrapManager.class) {
                if (group == null) {
                    int availableCount = Runtime.getRuntime().availableProcessors() * 4;
                    group = new NioEventLoopGroup(availableCount);
                    log.info("Client_NioEventLoopGroup:{}", availableCount);
                }
            }
        }
        return group;
    }

    /**
     * 服务端工作线程组
     * @return
     */
    private static EventLoopGroup getServerWokerGroup() {
        if (workGroup == null) {
            synchronized(BootstrapManager.class) {
                if (workGroup == null) {
                    int availableCount = Runtime.getRuntime().availableProcessors() * 4;
                    workGroup = new NioEventLoopGroup(availableCount);
                    log.info("Server_Woker_NioEventLoopGroup:{}", availableCount);
                }
            }
        }
        return workGroup;
    }

    /**
     * 初始化处理器
     */
    private static ChannelHandler getChannelHandler(Connection connection){
        if(connection instanceof HttpClient){
            return new HttpClientlInitializer();
        }else if (connection instanceof RtspClient){
            return new RtspClientlInitializer();
        }else if (connection instanceof WebSocketServer){
            return new WebSocketChannelInitializer();
        }
        throw new RuntimeException();
    }
}
