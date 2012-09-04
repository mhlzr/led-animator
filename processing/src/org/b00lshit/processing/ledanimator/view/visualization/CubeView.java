package org.b00lshit.processing.ledanimator.view.visualization;

import java.awt.Color;

import org.b00lshit.processing.ledanimator.model.Config;
import org.b00lshit.processing.ledanimator.model.Frame;

import peasy.PeasyCam;
import processing.core.PApplet;

public class CubeView implements IVisualizationView {

    private PApplet parent;

    private int radius = 50;
    private static final int PADDING = 150;
    private int ledsPerAxis;

    PeasyCam cam;
    Frame frame;


    public CubeView(PApplet parent, int ledsPerAxis) {

	this.parent = parent;
	this.ledsPerAxis = ledsPerAxis;

	cam = new PeasyCam(parent, parent.width / 2, parent.height / 2, 0, 900);
	// cam.setMinimumDistance(50);
	// cam.setMaximumDistance(1500);

    }


    public void draw(Frame frame) {

	parent.background(Config.GUI_BACKGROUND_COLOR);
	// parent.lights();

	Color rgb;

	for (int z = 0; z < ledsPerAxis; z++) {
	    for (int x = 0; x < ledsPerAxis; x++) {
		for (int y = 0; y < ledsPerAxis; y++) {
		    rgb = frame.getLedColorByCoordinate(x, z, y);
		    createSphere(x * PADDING, z * PADDING, y * PADDING, rgb);
		}

	    }

	}

    }


    private void createSphere(int x, int y, int z, Color color) {

	parent.noStroke();
	parent.fill(color.getRed(), color.getGreen(), color.getBlue());
	parent.pushMatrix();

	parent.translate(parent.width / 2 - PADDING * (ledsPerAxis - 1) / 2 + x, parent.height / 2 + PADDING * (ledsPerAxis - 1) / 2 - y, -PADDING
		* (ledsPerAxis - 1) / 2 + z);
	parent.sphere(radius);
	parent.popMatrix();
    }
}
