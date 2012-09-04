package org.b00lshit.processing.ledanimator.service;

import java.util.Date;

import org.b00lshit.processing.ledanimator.controller.ApplicationController;
import org.b00lshit.processing.ledanimator.service.requestHandler.AjaxHandler;
import org.b00lshit.processing.ledanimator.service.requestHandler.WebSocketHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.WebSocketConnection;

public class WebServerThread extends Thread {

    private WebServer webServer;
    private ApplicationController controller;


    public WebServerThread(ApplicationController controller) {
	this.controller = controller;

	webServer = WebServers.createWebServer(8080);
	webServer.add("/webSocket", new WebSocketHandler(this));
	webServer.add("/ajax", new AjaxHandler(this));
	webServer.start();

	System.out.println("Server running at " + webServer.getUri());
    }


    public void onWebSocketRequest(WebSocketConnection socketSonnection, String msg) {
	System.out.println(msg);
	socketSonnection.send(controller.handleAnimationRequest(msg));
    }


    public void onAjaxRequest(HttpRequest request, HttpResponse response) {
	// TODO response to client is ambigous

	response.header("Content-type", "application/json").header("Connection", "Keep-Alive").header("Keep-Alive", "timeout=5, max=100")
		.content(controller.handleAnimationRequest(request.postParam("data"))).end();
    }


    public void run() {

    }

}
