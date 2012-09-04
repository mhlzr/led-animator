package org.b00lshit.processing.ledanimator.model;

import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.b00lshit.processing.ledanimator.model.shapes.Cube;
import org.b00lshit.processing.ledanimator.model.shapes.IShape;
import org.b00lshit.processing.ledanimator.model.shapes.Line;
import org.b00lshit.processing.ledanimator.model.shapes.Plane;
import org.b00lshit.processing.ledanimator.model.shapes.Point;

/**
 * Represents a single Frame of an Animation and its Display-Informations
 * 
 * @author Matthieu Holzer
 */

public class Frame {

    private int duration = 1000;
    public Boolean clearOnEnter = true;

    public List<Point> points;
    public List<Line> lines;
    public List<Plane> planes;
    public List<Cube> cubes;

    private int ledsPerAxis;
    private Color[][][] ledData;

    private String type;

    private Vector<IShape> tweens;
    public Boolean hasTweens = false;
    private double tweenIterationCount = 0;


    /**
     * Combines the LED-information of a previous Frame with this frame. This is
     * used if a Frame has its "clearOnEnter"-value declared as false. The data
     * of this frame overwrites the data of the previous one
     * 
     * @param previousFrame
     *            The frame which data should be kept
     * @param ledsPerAxis
     *            Number of LEDs per axis
     * @param type
     *            Type of Animation (2D/3D)
     */
    public void combineWithFrame(Frame previousFrame, int ledsPerAxis, String type) {

	ledData = new Color[ledsPerAxis][ledsPerAxis][ledsPerAxis];

	for (int x = 0; x < ledsPerAxis; x++) {
	    for (int y = 0; y < ledsPerAxis; y++) {
		for (int z = 0; z < ledsPerAxis; z++) {
		    ledData[x][y][z] = previousFrame.getLedColorByCoordinate(x, y, z);
		}
	    }
	}

	init(ledsPerAxis, type);
    }


    /**
     * Reads the LED-data for each Shape-List, stores the data in ledData
     * 
     * @param ledsPerAxis
     *            Number of Leds Per Axis
     * @param type
     *            Type of Animation (2D/3D)
     * 
     */
    public void init(int ledsPerAxis, String type) {

	this.ledsPerAxis = ledsPerAxis;
	this.type = type;

	if (ledData == null)
	    ledData = new Color[ledsPerAxis][ledsPerAxis][ledsPerAxis];

	readLEDInformationsFromSubShapes();

    }


    /**
     * Loops through all kind of sub-shapes and calls addShapeDataToLedDataList
     * with the sub-shape as parameter.
     */
    private void readLEDInformationsFromSubShapes() {

	if (cubes != null && cubes.size() > 0) {
	    for (Cube cube : cubes) {
		addShapeDataToLedDataList(cube);
	    }
	}

	if (planes != null && planes.size() > 0) {
	    for (Plane plane : planes) {
		addShapeDataToLedDataList(plane);
	    }
	}

	if (lines != null && lines.size() > 0) {
	    for (Line line : lines) {
		addShapeDataToLedDataList(line);
	    }
	}

	if (points != null && points.size() > 0) {
	    for (Point point : points) {
		addShapeDataToLedDataList(point);
	    }
	}
    }


    /**
     * Writes the color-values for each LED included in a shape to the
     * ledData-List. Also sets the hasFrame-flag to true if one of the shapes is
     * a tween. The shape is then added to a special Tween-Vector.
     * 
     * @param shape
     *            The Shape to add to the ledData-List
     */
    private void addShapeDataToLedDataList(IShape shape) {

	// Decoding the Color-Value, each static Shape can only have one color
	if (shape.getColorByIndex(0) == null)
	    return;

	// If there is a tween, add it to the tweenList
	if (shape.isTween()) {
	    hasTweens = true;

	    if (tweens == null)
		tweens = new Vector<IShape>();

	    tweens.add(shape);
	}

	for (LED led : shape.getAllAffectedLeds()) {
	    ledData[led.x][led.y][led.z] = shape.getColorByIndex(0);
	}

    }


    /**
     * A debug-Method to dump the current LED-Informations to the cmdline.
     */
    public void dumpLEDData() {
	System.out.println("------------------------------------------");
	for (int x = 0; x < ledsPerAxis; x++) {
	    for (int y = 0; y < ledsPerAxis; y++) {
		for (int z = 0; z < ledsPerAxis; z++) {
		    System.out.print(x);
		    System.out.print(" | ");
		    System.out.print(y);
		    System.out.print(" | ");
		    System.out.print(z);
		    System.out.print(" | ");
		    System.out.println(ledData[x][y][z]);
		}
	    }
	}
	System.out.println("------------------------------------------");
    }


    /**
     * Gets the Color-Object from the ledData-Array
     * 
     * @param x
     *            X-Coordinate
     * @param y
     *            Y-Coordinate
     * @param z
     *            Z-Coordinate
     * @return an Color-Object representing the LED-Color
     */
    public Color getLedColorByCoordinate(int x, int y, int z) {

	if (x >= ledsPerAxis || y >= ledsPerAxis || z >= ledsPerAxis)
	    throw new Error("Index out of bounds");

	Color color = ledData[x][y][z];

	if (color != null) {
	    return color;

	} else {
	    color = new Color(0, 0, 0);
	}

	return color;

    }


    /**
     * Used to increase the tweenCounter and to calculate new interpolated
     * colors
     */
    public void updateTweens() {

	Color interColor;

	for (IShape shape : tweens) {

	    interColor = getInterpolatedColor(shape.getColorByIndex(0), shape.getColorByIndex(1));

	    for (LED led : shape.getAllAffectedLeds()) {
		ledData[led.x][led.y][led.z] = interColor;
	    }
	}

	tweenIterationCount++;
    }


    /**
     * Interpolates the given colors by linear interpolation during the frame
     * duration @link http://en.wikipedia.org/wiki/Linear_interpolation
     * 
     * @param startColor
     * @param endColor
     * @return the interpolated Color
     */
    private Color getInterpolatedColor(Color startColor, Color endColor) {

	// TODO find out why i need that "4" at 2D and "6" at 3D?!
	int unknownFactor = (type.equals(Animation.ANIMATION_TYPE_2D)) ? 4 : 6;

	double progress = (tweenIterationCount * unknownFactor) / (Config.APPLICATION_FRAMERATE * (duration / 1000));

	if (progress >= 1.0)
	    progress = 1;

	int r = (int) Math.round(startColor.getRed() + (endColor.getRed() - startColor.getRed()) * progress);
	int g = (int) Math.round(startColor.getGreen() + (endColor.getGreen() - startColor.getGreen()) * progress);
	int b = (int) Math.round(startColor.getBlue() + (endColor.getBlue() - startColor.getBlue()) * progress);

	return new Color(r, g, b);
    }


    /**
     * Resets the colors of each tween and the tweenCounter
     */
    public void resetTweens() {
	tweenIterationCount = 1;

	for (IShape shape : tweens) {
	    shape.resetRandomTweenColors();
	}

	tweens = null;
    }


    public int getDuration() {
	return duration;
    }


    public void setDuration(int duration) {
	this.duration = duration;
    }

}
