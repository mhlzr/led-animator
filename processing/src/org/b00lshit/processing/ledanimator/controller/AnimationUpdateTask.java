package org.b00lshit.processing.ledanimator.controller;

import java.util.TimerTask;

public class AnimationUpdateTask extends TimerTask {

    private AnimationController animationController;


    public AnimationUpdateTask(AnimationController animationController) {
	this.animationController = animationController;
    }


    public void run() {
	if (animationController != null)
	    animationController.frameUpdateTimerHandler(true);
    }

}
