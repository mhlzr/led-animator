package org.b00lshit.processing.ledanimator.model.shapes;

import java.util.Vector;

import org.b00lshit.processing.ledanimator.model.LED;

public class Plane extends BaseShape implements IShape {

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

		if (dx < dy && dx < dz)
			dx = 1;
		if (dy < dx && dy < dz)
			dy = 1;
		if (dz < dx && dz < dy)
			dz = 1;

		for (int x = 0; x <= dx; x++) {
			for (int y = 0; y <= dy; y++) {
				for (int z = 0; z <= dz; z++) {
					leds.add(new LED(startPosition[0] + x,
							startPosition[1] + y, startPosition[2] + z));
				}
			}
		}

		return leds;
	}

}
