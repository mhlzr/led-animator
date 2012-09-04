package org.b00lshit.processing.ledanimator;

import org.b00lshit.processing.ledanimator.controller.ApplicationController;
import org.b00lshit.processing.ledanimator.model.Config;
import org.b00lshit.processing.ledanimator.service.WebServerThread;
import org.b00lshit.processing.ledanimator.view.ApplicationView;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.serial.Serial;

/**
 * MainClass of the LEDAnimation-Tool, an application to parse LEDAnimation-Data
 * from JSON-files, visualizing those animations and a serial output to a
 * Rainbowduino-board of those.
 * 
 * Based on Processing
 * 
 * @author Matthieu Holzer
 * @version 0.1
 */
public class Application extends PApplet {

    private static final long serialVersionUID = -2560117848202608157L;

    private String comPort = "COM1";
    private ApplicationController appController;
    private WebServerThread webSocketServer;


    /**
     * Standard Processing setup-method
     */
    public void setup() {

	size(800, 600, PConstants.OPENGL);
	frameRate(Config.APPLICATION_FRAMERATE);

	// Starting the WebSocketServer as an own Thread, ApplicationController
	// as callback
	webSocketServer = new WebServerThread(appController = ApplicationController.getInstance());
	webSocketServer.start();

	ApplicationView appView = new ApplicationView(this);

	try {
	    comPort = Serial.list()[1];
	} catch (Exception e) {
	    appController.serialOutputEnabled = false;
	}

	appController.init(comPort, this, appView);

	//DEBUG
	//appController.handleAnimationRequest("http://dl.dropbox.com/u/959008/animations/example2D-plane.json");
    }


    /**
     * Standard Processing draw-method
     */
    public void draw() {
	appController.draw();
    }


    /**
     * main-Method
     * 
     */
    public static void main(String[] args) {
	PApplet.main(new String[] { Application.class.getName() });
    }

}
