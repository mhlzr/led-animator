package org.b00lshit.processing.ledanimator.model.shapes;

import java.awt.Color;
import java.util.List;
import java.util.Random;

public class BaseShape {

    public List<String> colors;
    public List<String> coordinates;

    // Not part of JSON-Docs
    private Color[] randomColors;


    /**
     * Splits the Coord-String and returns the coordinates
     * 
     * @param coordString
     *            (e.g.: 0,1,3 or 0,4)
     * @return an array including X,Y,Z Coordinates
     */
    public int[] getXYZFromCoordinateString(String coordString) {

	int firstToken, lastToken, length, x, y, z = 0;

	firstToken = coordString.indexOf(",");
	length = coordString.length();

	x = Integer.parseInt(coordString.substring(0, firstToken));

	// 0,2 or 0,0,1
	if (coordString.lastIndexOf(",") == firstToken) {
	    // 0,2
	    y = Integer.parseInt(coordString.substring(firstToken + 1, length));
	    z = 0;
	} else {
	    // 0,0,1
	    lastToken = coordString.lastIndexOf(",");

	    y = Integer.parseInt(coordString.substring(firstToken + 1, lastToken));
	    z = Integer.parseInt((coordString.substring(lastToken + 1, length)));
	}

	int[] coords = { x, y, z };

	return coords;
    }


    public Color getColorByIndex(int index) {

	if (index >= colors.size())
	    return null;

	Color rgb;

	// Random-Color
	if (colors.get(index).equals("random")) {

	    if (randomColors == null)
		randomColors = new Color[colors.size()];

	    //this would be one random color for all LEDs on this frame
//	    if (randomColors[index] != null) {
//		rgb = randomColors[index];
//	    } else {
		Random rand = new Random();
		rgb = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
		randomColors[index] = rgb;
//	    }

	}

	// Hexadezimal RGB Color-Value
	else {
	    rgb = Color.decode(colors.get(index));
	}

	return rgb;
    }


    public Boolean isTween() {
	return (colors.size() > 1);
    }


    public void resetRandomTweenColors() {
	randomColors = null;
    }

}
