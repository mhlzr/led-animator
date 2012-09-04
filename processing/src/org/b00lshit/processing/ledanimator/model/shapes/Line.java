package org.b00lshit.processing.ledanimator.model.shapes;

import java.util.Vector;

import org.b00lshit.processing.ledanimator.model.LED;

/**
 * 
 * @author Matthieu Holzer
 * 
 *         Represents a line of LEDs in the same color. A line can only be drawn
 *         on one axis, x,y or z. The biggest distance between start- and
 *         endPoint sets the direction Vector.
 * 
 */
public class Line extends BaseShape implements IShape {

	private final int[] xVector = { 1, 0, 0 };
	private final int[] yVector = { 0, 1, 0 };
	private final int[] zVector = { 0, 0, 1 };

	public Vector<LED> getAllAffectedLeds() {

		Vector<LED> leds = new Vector<>();

		int[] startPosition = getXYZFromCoordinateString(coordinates.get(0));
		int[] endPosition = getXYZFromCoordinateString(coordinates.get(1));

		// Can't do anything here
		if (startPosition == null || endPosition == null)
			return null;

		int dx = Math.abs(endPosition[0] - startPosition[0]);
		int dy = Math.abs(endPosition[1] - startPosition[1]);
		int dz = Math.abs(endPosition[2] - startPosition[2]);

		int delta = Math.max(Math.max(dx, dy), dz);

		int[] vector;

		if (delta == dx) {
			vector = xVector;
		} else if (delta == dy) {
			vector = yVector;
		} else {
			vector = zVector;
		}

		for (int i = 0; i <= delta; i++) {
			leds.add(new LED(startPosition[0] + i * vector[0], startPosition[1]
					+ i * vector[1], startPosition[2] + i * vector[2]));
		}

		return leds;
	}

}
