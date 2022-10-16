package com.transmit.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;

@Slf4j
public class UrlUtils {
    /**
     * 检查URL
     * @param url
     * @return
     */
    public static URI checkUrl(String url){
        /*if(url.contains("rtsp://")){
            URI uri = URI.create(url);
            return uri;
        }
        if(!url.contains("http://") && !url.contains("https://")){
            url = "http://" + url;
        }*/
        return URI.create(url);
    }

    /**
     * 检查端口
     * @param uri
     * @return
     */
    public static int checkPort(URI uri){
        int port = 80 ;
        if("rtsp".equals(uri.getScheme())){
            port = 554;
        }
        if(uri.getPort() > 0){
            return uri.getPort();
        }
        log.warn("使用默认端口:{}", port);
        return port;
    }

    /**
     * 检查主机
     * @param uri
     * @return
     */
    public static String checkHost(URI uri){
        return uri.getHost();
    }

}
