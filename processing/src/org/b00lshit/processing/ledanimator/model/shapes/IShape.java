package org.b00lshit.processing.ledanimator.model.shapes;

import java.awt.Color;
import java.util.Vector;

import org.b00lshit.processing.ledanimator.model.LED;

public interface IShape {

	public Vector<LED> getAllAffectedLeds();

	public Color getColorByIndex(int index);

	public Boolean isTween();

	public void resetRandomTweenColors();
}
