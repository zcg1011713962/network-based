package com.transmit.netty.connect;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConnectionManager{
    // (标识，连接)
    private static ConcurrentMap <String, Connection> connectionMap = new ConcurrentHashMap();

    /**
     * 存放连接
     * @param tag
     * @param connection
     * @return
     */
    public static Connection setConnection(String tag, Connection connection){
        if(connectionMap.containsKey(tag)){
            connectionMap.remove(tag);
        }
        return connectionMap.put(tag, connection);
    }

    /**
     * 获取连接
     * @param tag 标识
     * @return
     */
    public static Connection getConnection(String tag){
        return connectionMap.get(tag);
    }

    /**
     * 获取连接
     * @param channel 通道
     * @return
     */
    public static Connection getConnection(Channel channel){
        Connection c =  connectionMap.values().stream().filter(connection -> {
            if (connection instanceof NettyConnection) {
                NettyConnection n = (NettyConnection) connection;
                return n.channel.id() == channel.id();
            }
            return false;
        }).findFirst().orElse(null);
        return c;
    }

}
