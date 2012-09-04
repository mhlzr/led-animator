package org.b00lshit.processing.ledanimator.service;

import java.awt.Color;

import org.b00lshit.processing.ledanimator.controller.ApplicationController;
import org.b00lshit.processing.ledanimator.model.Animation;
import org.b00lshit.processing.ledanimator.model.Config;
import org.b00lshit.processing.ledanimator.model.Frame;

import processing.core.PApplet;
import processing.serial.Serial;

/**
 * Service for sending bytes to the Rainbodwuino-Controller
 * 
 * @link 
 *       http://www.faludi.com/2006/03/21/signed-and-unsigned-bytes-in-processing
 * 
 * @link http://darksleep.com/player/JavaAndUnsignedTypes.html
 * 
 * @author Matthieu Holzer
 * 
 */
public class RainbowduinoService {

    private Serial serial;

    private boolean isConnected;
    private String animationType;
    private byte ledsPerAxis;

    private static int CMD_BLANK_DISPLAY = 0x00;
    private static int CMD_DISPLAY_NEXT_FRAME = 0xAA;
    private static int CMD_HEADER_START = 0xFF;
    private static int CMD_HEADER_END = 0x00;
    private static byte CMD_2D_MODUS = 2;
    private static byte CMD_3D_MODUS = 3;


    public RainbowduinoService(PApplet parent, String port, int baud) {

	try {
	    serial = new Serial(parent, port, baud);
	    isConnected = true;
	} catch (Exception e) {
	    isConnected = false;
	    ApplicationController.getInstance().serialOutputEnabled = false;
	}

    }


    public void sendAnimationHeader(Animation animation) {
	if (!isConnected)
	    return;
	byte[] header = new byte[3];

	header[0] = (byte) CMD_HEADER_START;

	if (animation.getType().equals(Animation.ANIMATION_TYPE_2D)) {
	    header[1] = CMD_2D_MODUS;
	    animationType = Animation.ANIMATION_TYPE_2D;
	} else if (animation.getType().equals(Animation.ANIMATION_TYPE_3D)) {
	    header[1] = CMD_3D_MODUS;
	    animationType = Animation.ANIMATION_TYPE_3D;
	} else
	    throw new Error("Unsupported Animation Type");

	header[2] = ledsPerAxis = (byte) animation.getLedsPerAxis();

	write(header);
    }


    public void sendFrame(Frame frame) {
	if (!isConnected)
	    return;

	byte[] data;
	int index = 0;
	Color color;

	if (animationType == Animation.ANIMATION_TYPE_2D) {

	    data = new byte[Config.BYTES_PER_LED * ledsPerAxis * ledsPerAxis];

	    for (int x = 0; x < ledsPerAxis; x++) {
		for (int y = 0; y < ledsPerAxis; y++) {

		    color = frame.getLedColorByCoordinate(x, y, 0);

		    if (color.getRed() >= 0 || color.getGreen() >= 0 || color.getBlue() >= 0) {
			addLEDDataToByteArray(data, index++, x, y, 0, color);
		    }
		}
	    }
	}

	else {

	    data = new byte[Config.BYTES_PER_LED * ledsPerAxis * ledsPerAxis * ledsPerAxis];

	    for (int x = 0; x < ledsPerAxis; x++) {
		for (int y = 0; y < ledsPerAxis; y++) {
		    for (int z = 0; z < ledsPerAxis; z++) {

			color = frame.getLedColorByCoordinate(x, y, z);

			if (color.getRed() >= 0 || color.getGreen() >= 0 || color.getBlue() >= 0) {
			    addLEDDataToByteArray(data, index++, x, z, y, color);
			}
		    }
		}

	    }
	}

	write(data);
    }


    /**
     * A Helper method which converts the LEDData fields to bytes, performing a
     * check to see if they are in the right range and adds them to a given
     * byte-array
     * 
     * @param data
     *            the byte-Array
     * @param index
     *            the current byte-Offset-index
     * @param x
     *            X-Coordinate
     * @param y
     *            Y-Coordinate
     * @param z
     *            Z-Coordinate
     * @param color
     *            awt.Color for the LED
     * 
     */
    private void addLEDDataToByteArray(byte[] data, int index, int x, int y, int z, Color color) {

	data[index * Config.BYTES_PER_LED] = (byte) x;
	data[index * Config.BYTES_PER_LED + 1] = (byte) y;
	data[index * Config.BYTES_PER_LED + 2] = (byte) z;

	int r = (color.getRed() < 255) ? color.getRed() : 254;
	int g = (color.getGreen() < 255) ? color.getGreen() : 254;
	int b = (color.getBlue() < 255) ? color.getBlue() : 254;

	data[index * Config.BYTES_PER_LED + 3] = (byte) r;
	data[index * Config.BYTES_PER_LED + 4] = (byte) g;
	data[index * Config.BYTES_PER_LED + 5] = (byte) b;

    }


    // DEBUG
    public void sendRandom2DFrame() {
	if (!isConnected)
	    return;

	byte[] data = new byte[6 * 8 * 8];

	int index = 0;

	for (byte x = 0; x < 8; x++) {
	    for (byte y = 0; y < 8; y++) {

		data[index * 6] = x;
		data[index * 6 + 1] = y;
		data[index * 6 + 2] = 0;

		data[index * 6 + 3] = (byte) (Math.random() * 254);
		data[index * 6 + 4] = (byte) (Math.random() * 254);
		data[index * 6 + 5] = (byte) (Math.random() * 254);

		index++;
	    }

	}

	write(data);
    }


    // DEBUG
    public void sendRandom3DFrame() {
	if (!isConnected)
	    return;

	byte[] data = new byte[6 * 4 * 4 * 4];

	int index = 0;

	for (byte x = 0; x < 4; x++) {
	    for (byte y = 0; y < 4; y++) {
		for (byte z = 0; z < 4; z++) {

		    data[index * 6] = x;
		    data[index * 6 + 1] = y;
		    data[index * 6 + 2] = z;

		    data[index * 6 + 3] = (byte) (Math.random() * 254);
		    data[index * 6 + 4] = (byte) (Math.random() * 254);
		    data[index * 6 + 5] = (byte) (Math.random() * 254);

		    index++;
		}
	    }

	}

	write(data);
    }


    public void displayFrame() {
	if (!isConnected)
	    return;

	byte[] cmd = new byte[3];

	cmd[0] = (byte) CMD_HEADER_START;
	cmd[1] = (byte) CMD_DISPLAY_NEXT_FRAME;
	cmd[2] = (byte) CMD_HEADER_END;

	write(cmd);
    }


    public void clearDisplay() {
	if (!isConnected)
	    return;

	byte[] cmd = new byte[3];

	cmd[0] = (byte) CMD_HEADER_START;
	cmd[1] = (byte) CMD_BLANK_DISPLAY;
	cmd[2] = (byte) CMD_HEADER_END;

	write(cmd);
    }


    private void write(byte[] data) {
	try {
	    serial.write(data);
	} catch (Exception e) {
	    ApplicationController.getInstance().serialOutputEnabled = false;
	}
    }
}
