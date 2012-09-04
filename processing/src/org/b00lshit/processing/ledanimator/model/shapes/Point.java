package org.b00lshit.processing.ledanimator.model.shapes;

import java.util.Vector;

import org.b00lshit.processing.ledanimator.model.LED;

public class Point extends BaseShape implements IShape {

	public Vector<LED> getAllAffectedLeds() {

		Vector<LED> leds = new Vector<>();

		for (String coStr : coordinates) {

			int[] coords = getXYZFromCoordinateString(coStr);
			leds.add(new LED(coords[0], coords[1], coords[2]));

		}

		return leds;
	}
}
