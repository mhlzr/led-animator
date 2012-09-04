package org.b00lshit.processing.ledanimator.controller;

import java.util.Timer;

import org.b00lshit.processing.ledanimator.model.Animation;
import org.b00lshit.processing.ledanimator.model.Frame;
import org.b00lshit.processing.ledanimator.service.RainbowduinoService;

public class AnimationController {

    private Animation currentAnimation = null;
    private Frame currentFrame = null;

    private int frameIndex = 0;

    private Boolean animationComplete = false;

    private Timer frameUpdateTimer;

    private RainbowduinoService rainbowduinoService;


    public AnimationController(RainbowduinoService rainbdowduinoService) {
	this.rainbowduinoService = rainbdowduinoService;
    }


    public void startAnimation(Animation animation) {

	if (currentAnimation != null) {
	    destroyFrameUpdateTimer();
	    if (rainbowduinoService != null)
		rainbowduinoService.clearDisplay();
	    currentAnimation = null;
	    currentFrame = null;
	}

	frameIndex = 0;
	
	currentAnimation = animation;
	currentFrame = currentAnimation.frames.get(frameIndex);
	currentFrame.init(currentAnimation.getLedsPerAxis(), currentAnimation.getType());

	frameUpdateTimer = new Timer();
	frameUpdateTimerHandler(false);

	if (rainbowduinoService != null)
	    rainbowduinoService.sendFrame(currentFrame);

    }


    private void destroyFrameUpdateTimer() {
	frameUpdateTimer.cancel();
	frameUpdateTimer = null;
    }


    public void update() {
	if (currentAnimation == null)
	    return;

	if (currentFrame.hasTweens) {
	    currentFrame.updateTweens();
	}

    }


    /**
     * Gets called by a Timer at an intervall that is given by the duration of
     * each frame @see {@link AnimationUpdateTask}
     * 
     * @param forwardToNextFrame
     *            is need for the first Time the update Method gets called,
     *            where there should be no forwarding to the next Frame
     * 
     * 
     */
    public void frameUpdateTimerHandler(Boolean forwardToNextFrame) {

	if (animationComplete && !currentAnimation.repeat) {
	    destroyFrameUpdateTimer();
	    return;
	}

	if (forwardToNextFrame) {
	    forwardToNextFrame();
	    if (rainbowduinoService != null) {
		rainbowduinoService.sendFrame(currentFrame);
		rainbowduinoService.displayFrame();
	    }

	}

	frameUpdateTimer.schedule(new AnimationUpdateTask(this), currentFrame.getDuration());
    }


    /**
     * Forwards to the nextFrame, so that currentFrame will be the next one, or
     * stops the animation depending on the animation's repeat behaviour
     */
    public void forwardToNextFrame() {

	frameIndex++;

	// end of Frame-List has been reached
	if (frameIndex >= currentAnimation.frames.size()) {
	    if (currentAnimation.repeat)
		frameIndex = 0;
	    else {
		animationComplete = true;
		System.out.println("Animation Complete");
		return;
	    }

	}

	Frame previousFrame = currentFrame;

	currentFrame = currentAnimation.frames.get(frameIndex);

	if (previousFrame.hasTweens)
	    previousFrame.resetTweens();

	// Combine current and previousFrame
	if (currentFrame.clearOnEnter == false) {
	    currentFrame.combineWithFrame(previousFrame, currentAnimation.getLedsPerAxis(), currentAnimation.getType());
	} else {
	    currentFrame.init(currentAnimation.getLedsPerAxis(), currentAnimation.getType());
	}

    }


    /**
     * @return currentAnimation which holds all the animation information
     */
    public Animation getCurrentAnimation() {
	return currentAnimation;
    }


    /**
     * @return currentFrame which represents the Frame which is currently active
     */
    public Frame getCurrentFrame() {
	return currentFrame;
    }


    public Boolean getAnimationComplete() {
	return animationComplete;
    }

}
