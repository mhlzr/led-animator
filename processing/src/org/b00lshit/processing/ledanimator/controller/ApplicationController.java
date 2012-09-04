package org.b00lshit.processing.ledanimator.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.b00lshit.processing.ledanimator.model.Animation;
import org.b00lshit.processing.ledanimator.model.Config;
import org.b00lshit.processing.ledanimator.service.RainbowduinoService;
import org.b00lshit.processing.ledanimator.view.ApplicationView;
import org.b00lshit.processing.ledanimator.view.visualization.CubeView;
import org.b00lshit.processing.ledanimator.view.visualization.IVisualizationView;
import org.b00lshit.processing.ledanimator.view.visualization.MatrixView;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import processing.core.PApplet;
import eu.vahlas.json.schema.JSONSchema;
import eu.vahlas.json.schema.impl.JacksonSchemaProvider;

/**
 * Klasse mit Singleton, in welcher die Pragrammablauflogik enthalten ist. Hier
 * wird die Verbindung zum Rainbowduino mittels RainbdowduinoService erstellt,
 * auﬂerdem landen s‰mtliche WebSocketRequeste in dieser Klasse, wo sie in
 * handleAnimationRequest() verarbeitet oder verworfen werden. Auﬂerdem wird
 * hier der AnimationController instanziiert, sowie die passende Visualisierung
 * (2D oder 3D) erstellt.
 * 
 * @author Matthieu Holzer
 * 
 */
public class ApplicationController {

    private static ApplicationController _instance = null;

    private AnimationController animationController;

    private ApplicationView appView;

    public Boolean serialOutputEnabled = true;
    public Boolean visualOutputEnabled = true;

    private RainbowduinoService rainbowduinoService = null;
    private IVisualizationView visualizationView = null;

    private PApplet parent;

    /**
     * Singleton Construct
     * @return ApplicationController Instanz
     */
    public static ApplicationController getInstance() {

	if (_instance == null) {
	    _instance = new ApplicationController();
	}
	return _instance;
    }


    public void init(String comPort, PApplet parent, ApplicationView appView) {

	this.parent = parent;
	this.appView = appView;

	// connect to Rainbowduino
	if (serialOutputEnabled) {
	    rainbowduinoService = new RainbowduinoService(parent, comPort, Config.RAINBOWDUINO_BAUD_RATE);
	}

	if (visualOutputEnabled) {
	    parent.registerDraw(appView);
	}

	animationController = new AnimationController(rainbowduinoService);

    }


    public void draw() {

	if (visualOutputEnabled && visualizationView != null) {
	    visualizationView.draw(animationController.getCurrentFrame());
	}

    }


    public IVisualizationView createVisualizationView() {

	if (animationController.getCurrentAnimation() != null) {

	    // 2D - Matrix
	    if (animationController.getCurrentAnimation().getType().equals(Animation.ANIMATION_TYPE_2D)) {
		visualizationView = new MatrixView(parent, animationController.getCurrentAnimation().getLedsPerAxis());
	    }
	    // 3D - Cube
	    else {
		visualizationView = new CubeView(parent, animationController.getCurrentAnimation().getLedsPerAxis());
	    }
	}

	return visualizationView;
    }


    public String handleAnimationRequest(String requestString) {

	Boolean requestIsUrl = false;

	if (requestString.startsWith("http://"))
	    requestIsUrl = true;

	if (!requestIsUrl && !requestString.startsWith("{")) {
	    return "{\"status\":\"fail\", \"error\":\"Not a JSON-String or an URL\"}";
	}

	// PARSING JSON
	List<String> errors;
	ObjectMapper mapper = new ObjectMapper();
	JacksonSchemaProvider schemaProvider = new JacksonSchemaProvider(mapper);

	JSONSchema schema = null;
	try {
	    schema = schemaProvider.getSchema(new URL("http://localhost/animation-schema.json"));
	} catch (MalformedURLException e1) {
	    e1.printStackTrace();
	}

	if (requestIsUrl) {
	    try {
		errors = schema.validate(new URL(requestString));
	    } catch (MalformedURLException e) {
		return "{\"status\":\"fail\", \"error\":\"Not a valid URL\"}";
	    }
	} else {
	    errors = schema.validate(requestString);
	}

	if (errors.size() > 0) {
	    String errorString = "";
	    for (String s : errors) {
		errorString = errorString.concat(s);
	    }
	    return "{\"status\":\"fail\", \"error\":\"JSON is not valid\", \"details\":\"" + errorString + "\"}";
	}

	// if everything was fine
	Animation animation = null;

	try {
	    if (requestIsUrl)
		animation = mapper.readValue(new URL(requestString), Animation.class);
	    else
		animation = mapper.readValue(requestString, Animation.class);

	} catch (JsonParseException e) {
	    e.printStackTrace();
	    return "{\"status\":\"fail\", \"error\":\"JsonParseException\"}";
	} catch (JsonMappingException e) {
	    e.printStackTrace();
	    return "{\"status\":\"fail\", \"error\":\"JsonMappingException\"}";
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	    return "{\"status\":\"fail\", \"error\":\"MalformedURLException\"}";
	} catch (FileNotFoundException e) {
	    return "{\"status\":\"fail\", \"error\":\"Not a valid URL\"}";
	} catch (IOException e) {
	    e.printStackTrace();
	    return "{\"status\":\"fail\", \"error\":\"IOException\"}";
	}

	// parsing was successfull
	if (animation != null) {
	    visualizationView = null;
	    animationController.startAnimation(animation);
	    if (serialOutputEnabled) {
		rainbowduinoService.sendAnimationHeader(animation);
	    }
	    if (visualOutputEnabled) {
		visualizationView = createVisualizationView();
		appView.setAuthor(animation.author);
		appView.setTitle(animation.title);
	    }

	    return "{\"status\":\"okay\"}";
	}

	return "{\"status\":\"fail\"}";

    }
}
