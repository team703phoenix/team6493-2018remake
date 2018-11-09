package org.usfirst.frc.team703.robot.controls;

import edu.wpi.first.wpilibj.Joystick;

public class GuitarController extends Joystick {
	private static final int GREEN_FRET = 1;
	private static final int RED_FRET = 2;
	private static final int YELLOW_FRET = 4;
	private static final int BLUE_FRET = 3;
	private static final int ORANGE_FRET = 5;
	
	private static final int START_BUTTON = 8;
	private static final int SELECT_BUTTON = 7;
	
	private static final int WHAMMY_AXIS = 4;
	private static final int TILT_AXIS = 5;
	
	private boolean leftyMode = false;
	
	public GuitarController(int port) {
		super(port);
	}
	
	public boolean getGreenFret() {
		return getRawButton(GREEN_FRET);
	}
	
	public boolean getRedFret() {
		return getRawButton(RED_FRET);
	}
	
	public boolean getYellowFret() {
		return getRawButton(YELLOW_FRET);
	}
	
	public boolean getBlueFret() {
		return getRawButton(BLUE_FRET);
	}
	
	public boolean getOrangeFret() {
		return getRawButton(ORANGE_FRET);
	}
	
	public boolean getDownStrum() {
		return leftyMode ? getPOV() == 0 : getPOV() == 180;
	}
	
	public boolean getUpStrum() {
		return leftyMode ? getPOV() == 180 : getPOV() == 0;
	}
	
	public boolean getStartButton() {
		return getRawButton(START_BUTTON);
	}
	
	public boolean getSelectButton() {
		return getRawButton(SELECT_BUTTON);
	}
	
	public double getWhammyAxis() {
		return getRawAxis(WHAMMY_AXIS);
	}
	
	public double getTiltAxis() {
		return getRawAxis(TILT_AXIS);
	}
	
	public void setLeftyMode(boolean leftyMode) {
		this.leftyMode = leftyMode;
	}
}
