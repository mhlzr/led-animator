package org.b00lshit.processing.ledanimator.model;

import java.util.List;

/**
 * Represents a single Animation (=one JSON-file) and has a list of Frames.
 * 
 * @author Matthieu Holzer
 */

public class Animation {

    public static String ANIMATION_TYPE_2D = "2d";
    public static String ANIMATION_TYPE_3D = "3d";

    public String title;
    public String author;
    private String type;
    private int ledsPerAxis;
    public Boolean repeat;

    public List<Frame> frames;


    public int getLedsPerAxis() {
	return ledsPerAxis;
    }


    public void setLedsPerAxis(int ledsPerAxis) {
	if (ledsPerAxis > Config.MAX_LEDS_PER_AXIS) {
	    throw new Error("Too many Leds per Axis defined");
	} else
	    this.ledsPerAxis = ledsPerAxis;
    }


    public String getType() {
	return type;
    }


    public void setType(String type) {
	type = type.toLowerCase();

	if (!type.equals(ANIMATION_TYPE_2D) && !type.equals(ANIMATION_TYPE_3D)) {
	    throw new Error("Animation Type neither 2D or 3D");
	} else {
	    this.type = type;
	}
    }

}
