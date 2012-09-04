package org.b00lshit.processing.ledanimator.service.requestHandler;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.b00lshit.processing.ledanimator.service.WebServerThread;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.dependencies.org.jboss.netty.buffer.ChannelBuffer;
import org.webbitserver.dependencies.org.jboss.netty.handler.codec.http.HttpChunkTrailer;

public class AjaxHandler implements HttpHandler, HttpChunkTrailer {

    private WebServerThread webServer;


    public AjaxHandler(WebServerThread webServer) {
	this.webServer = webServer;

    }


    public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
	webServer.onAjaxRequest(request, response);
    }


    public ChannelBuffer getContent() {
	return null;
    }


    public void setContent(ChannelBuffer arg0) {

    }


    public void addHeader(String arg0, Object arg1) {

    }


    public void clearHeaders() {

    }


    public boolean containsHeader(String arg0) {
	return false;
    }


    public String getHeader(String arg0) {
	return null;
    }


    public Set<String> getHeaderNames() {
	return null;
    }


    public List<Entry<String, String>> getHeaders() {
	return null;
    }


    public List<String> getHeaders(String arg0) {
	return null;
    }


    public boolean isLast() {
	return false;
    }


    public void removeHeader(String arg0) {
    }


    public void setHeader(String arg0, Object arg1) {
    }


    public void setHeader(String arg0, Iterable<?> arg1) {

    }

}
