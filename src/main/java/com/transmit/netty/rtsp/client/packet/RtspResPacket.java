package com.transmit.netty.rtsp.client.packet;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

public class RtspResPacket {
    private int type;
    // rtsp
    private String method;
    private int status;
    private String cseq;
    private String cacheControl;
    private String server;
    private String expires;
    private String contentType;
    private String contentLength;
    private String contentBase;
    private String session;
    private String date;
    private String transport;
    private List<String> option = new ArrayList<>();
    // sdp
    private Attributes attributes = new Attributes();

    /**
     * 清理-除了session contentType
     * @return
     */
    public RtspResPacket clear(){
        method = "";
        status = 0;
        cseq="";
        cacheControl="";
        server="";
        expires="";
        contentType="";
        contentLength="";
        contentBase="";
        date="";
        transport="";
        option = new ArrayList<>();
        attributes.clear();
        return this;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getOption() {
        return option;
    }

    public void setOption(List<String> option) {
        this.option = option;
    }

    public String getCacheControl() {
        return cacheControl;
    }

    public void setCacheControl(String cacheControl) {
        this.cacheControl = cacheControl;
    }

    public String getCseq() {
        return cseq;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentLength() {
        return contentLength;
    }

    public void setContentLength(String contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentBase() {
        return contentBase;
    }

    public void setContentBase(String contentBase) {
        this.contentBase = contentBase;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setCseq(String cseq) {
        this.cseq = cseq;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RtspResPacket{" +
                "method='" + method + '\'' +
                ", status=" + status +
                ", cseq='" + cseq + '\'' +
                ", cacheControl='" + cacheControl + '\'' +
                ", server='" + server + '\'' +
                ", expires='" + expires + '\'' +
                ", contentType='" + contentType + '\'' +
                ", contentLength='" + contentLength + '\'' +
                ", contentBase='" + contentBase + '\'' +
                ", session='" + session + '\'' +
                ", date='" + date + '\'' +
                ", option=" + option +
                ", attributes=" + attributes +
                '}';
    }
}
