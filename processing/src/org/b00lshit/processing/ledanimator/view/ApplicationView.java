package org.b00lshit.processing.ledanimator.view;

import org.b00lshit.processing.ledanimator.model.Config;

import processing.core.PApplet;
import controlP5.ControlP5;

public class ApplicationView {

    // http://www.sojamo.de/libraries/controlP5/
    private ControlP5 controlP5;

    private String author = "";
    private String title = "";


    public ApplicationView(PApplet parent) {
	controlP5 = new ControlP5(parent);
	controlP5.addTextlabel("placeholder", Config.WAITING_TXT, 20, 20);
    }


    public void draw() {

	if (author.equals("") && title.equals("")) {
	    return;
	}

	controlP5.remove("placeholder");

	controlP5.remove("author");
	controlP5.remove("title");
	controlP5.addTextlabel("author", "AUTHOR: " + author, 20, 20);
	controlP5.addTextlabel("title", "TITLE: " + title, 20, 40);
    }


    public void setAuthor(String author) {
	this.author = author;
    }


    public void setTitle(String title) {
	this.title = title;
    }

}
