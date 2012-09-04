package org.b00lshit.processing.ledanimator.model;

/**
 * Represents one single LED
 * 
 * @author Matthieu Holzer
 * 
 */
public class LED {

    public int x;
    public int y;
    public int z;


    public LED(int x, int y, int z) {
	this.x = x;
	this.y = y;
	this.z = z;
    }


    @Override
    public String toString() {
	return x + "," + y + "," + z;
    }
}
