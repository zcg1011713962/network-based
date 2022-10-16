package com.transmit.netty.http.client.packet;

import com.transmit.netty.Response;

public class HttpResponse implements Response {
    private int code;
    private String headers;
    private StringBuffer body;
    private boolean end;

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body.toString();
    }

    public void appendBody(String body) {
        this.body = this.body == null ? new StringBuffer() : this.body.append(body);
    }
}
