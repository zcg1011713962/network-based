package http.client;

import com.transmit.netty.http.client.HttpClient;
import com.transmit.netty.http.client.packet.HttpRequest;
import com.transmit.netty.http.client.packet.HttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class HttpTest {
    private static HttpClient client = new HttpClient();

    @Test
    public void connect() throws InterruptedException {
        String url = "http://www.baidu.com/";
        HttpRequest httpRequest = new HttpRequest.Builder(url, "", HttpMethod.GET, HttpVersion.HTTP_1_1, HttpHeaderValues.KEEP_ALIVE).build();
        if(client.createNettyConnect(url)){
            HttpResponse httpResponse = client.send(httpRequest);
            if(httpResponse != null){
                log.info("连接建立成功时间:{} 响应结束时间:{} 响应码:{} 响应体:{}", client.getConnectedTime(), client.getReadEndTime(), httpResponse.getCode(), httpResponse.getBody());
            }else{
                log.info("响应为空");
            }
        }else{
            log.info("建立连接失败");
        }
    }

}
