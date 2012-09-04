package org.b00lshit.processing.ledanimator.service.requestHandler;

import org.b00lshit.processing.ledanimator.service.WebServerThread;
import org.webbitserver.WebSocketConnection;

public class WebSocketHandler implements org.webbitserver.WebSocketHandler {

    private WebServerThread webServer;


    public WebSocketHandler(WebServerThread webServer) {
	this.webServer = webServer;
    }


    public void onMessage(WebSocketConnection connection, String msg) throws Throwable {
	webServer.onWebSocketRequest(connection, msg);
    }


    public void onClose(WebSocketConnection arg0) throws Throwable {

    }


    public void onMessage(WebSocketConnection arg0, byte[] arg1) throws Throwable {
    }


    public void onOpen(WebSocketConnection arg0) throws Throwable {
    }


    public void onPing(WebSocketConnection arg0, byte[] arg1) throws Throwable {
    }


    public void onPong(WebSocketConnection arg0, byte[] arg1) throws Throwable {
    }

}
