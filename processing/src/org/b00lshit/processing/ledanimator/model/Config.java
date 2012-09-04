package org.b00lshit.processing.ledanimator.model;

/**
 * Global Constants for the Application
 * 
 * @author Matthieu Holzer
 * 
 */
public final class Config {

    public static final int RAINBOWDUINO_BAUD_RATE = 28800;
    public static final int MAX_LEDS_PER_AXIS = 8;
    public static final int MAX_LEDS_TOTAL = 192; // = 8*8 or 4*4*4
    public static final int BYTES_PER_LED = 6;

    public static final int APPLICATION_WIDTH = 800;
    public static final int APPLICATION_HEIGHT = 600;
    public static final int APPLICATION_FRAMERATE = 30;// (1/(RAINBOWDUINO_BAUD_RATE/192/BYTES_PER_LED))*1000;

    public static final int GUI_BACKGROUND_COLOR = 200;

    public static final String WAITING_TXT = "Waiting for data ...";;

}
