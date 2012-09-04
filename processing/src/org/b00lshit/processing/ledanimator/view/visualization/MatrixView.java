package org.b00lshit.processing.ledanimator.view.visualization;

import java.awt.Color;

import org.b00lshit.processing.ledanimator.model.Frame;

import processing.core.PApplet;

public class MatrixView implements IVisualizationView {

    private int radius;
    private PApplet parent;
    private int ledsPerAxis;


    public MatrixView(PApplet parent, int ledsPerAxis) {
	this.parent = parent;
	this.ledsPerAxis = ledsPerAxis;

	radius = ((Math.min(parent.width,parent.height)-100) / ledsPerAxis)/2;
    }


    public void draw(Frame frame) {

	int xOffset = radius + parent.width/2 - (radius*ledsPerAxis);
	int yOffset = radius + parent.height/2 - (radius*ledsPerAxis);
	int index = 0;

	Color rgb;

	for (int y = 0; y < ledsPerAxis; y++) {
	    for (int x = 0; x < ledsPerAxis; x++) {
		rgb = frame.getLedColorByCoordinate(x, y, 0);
		parent.fill(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
		parent.ellipse(xOffset + index * radius*2, yOffset, radius*2, radius*2);
		index++;
	    }
	    yOffset += radius*2;
	    index = 0;

	}

    }

}
