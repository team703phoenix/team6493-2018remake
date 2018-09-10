package org.usfirst.frc.team703.robot.subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.usfirst.frc.team703.robot.Robot;
import org.usfirst.frc.team703.robot.RobotMap;
import org.usfirst.frc.team703.robot.utilities.Utility;

public class Vision {
	
	// Constants
	private final double TURNING_SCALER = 0.015;
	
	// Robot
	private Robot robot;
	
	// Limelight output
	private NetworkTable limelight = NetworkTableInstance.getDefault().getTable("limelight");
	
	// Control variables
	private boolean[] ledToggle = {false, true};
	private double latestErrorX = 0;
	
	/** Creates a new vision pipeline */
	public Vision(Robot robot) {	
		this.robot = robot;
	}
	
	/** Scans the area to find the given object */
	public void scan(boolean scanLeft) {
		if (!hasValidTarget() && (robot.getAutoMode()) ? robot.isAutonomous() : !robot.cont.getRawButton(RobotMap.VISION_STOP))
			robot.drive.arcadeDrive(0, (scanLeft) ? -0.5 : 0.5);
	}
	
	/** Drives toward a target made of vision tape, using the latest error in the x direction to determine which direction to scan in */
	public void driveTowardTarget() {
		driveTowardTarget(latestErrorX < 0);
	}
	
	/** Drives toward a target made of vision tape */
	public void driveTowardTarget(boolean scanLeft) {
		setTargetPipeline(200);
		driveTowardObject(7, scanLeft); // final percentage of screen
		robot.drive.driveForward(12);
	}
	
	/** Picks up a power cube, using the latest error in the x direction to determine which direction to scan in */
	public void pickupCube() {
		pickupCube(latestErrorX < 0);
	}
	
	/** Picks up a power cube */
	public void pickupCube(boolean scanLeft) {
		/* TODO: Make pickupCube function */
		
		/* robot.arm.lower();
		driveTowardCubeWithoutStopping(scanLeft);
		if ((robot.getAutoMode()) ? robot.isAutonomous() : !robot.controller.getRawButton(1)) {
			robot.drive.forcedArcadeDrive(0.5, 0);
			robot.arm.intake();
		}
		robot.drive.forcedTankDrive(0, 0); */
	}
	
	/** Drives toward a power cube, using the latest error in the x direction to determine which direction to scan in */
	public void driveTowardCube() {
		driveTowardCube(latestErrorX < 0);
	}
	
	/** Drives toward a power cube */
	public void driveTowardCube(boolean scanLeft) {
		driveTowardCubeWithoutStopping(scanLeft);
		
		robot.drive.forcedTankDrive(0, 0);
	}
	
	/** Drives toward a power cube without stopping once it reaches the cube */
	private void driveTowardCubeWithoutStopping(boolean scanLeft) {
		setCubePipeline(250);
		driveTowardObjectWithoutStopping(35, scanLeft);
	}
	
	/** Drives toward an object without stopping once it reaches the object */
	private void driveTowardObjectWithoutStopping(double finalPercentage, boolean scanLeft) {
		while (getPercentageOfScreen() < finalPercentage && ((robot.getAutoMode()) ? robot.isAutonomous() : !robot.cont.getRawButton(RobotMap.VISION_STOP))) {
			if (!hasValidTarget())
				scan(scanLeft);				
			else
				robot.drive.arcadeDrive(0.9, getErrorX() * TURNING_SCALER);
		}
	}
	
	/** Drives toward an object until it is a given distance away */
	private void driveTowardObject(double finalPercentage, boolean scanLeft) {
		driveTowardObjectWithoutStopping(finalPercentage, scanLeft);
		
		robot.drive.forcedTankDrive(0, 0);
	}
	
	/** DEMO: Follows a target made of vision tape from a certain distance */
	public void followTarget() {
		final double FOLLOWING_DISTANCE = 5;
		final double kP = 0.25;
		final double MAX_SPEED = 0.6;
		
		setTargetPipeline(250);
		
		while (robot.isEnabled() && !robot.cont.getRawButton(RobotMap.VISION_STOP)) {
			if (!hasValidTarget())
				scan(latestErrorX < 0);
			else {
				double forwardDrive = (FOLLOWING_DISTANCE - getPercentageOfScreen()) * kP;
				System.out.println(forwardDrive);
				if (forwardDrive < MAX_SPEED)
					robot.drive.arcadeDrive(forwardDrive, getErrorX() * TURNING_SCALER);
				else
					robot.drive.arcadeDrive(MAX_SPEED, getErrorX() * TURNING_SCALER);
			}
		}
		
		robot.drive.forcedTankDrive(0, 0);
	}
	
	/** Configures the vision pipeline to work with the cube (max exposure, LEDs off) */
	public void setCubePipeline() {
		setCubePipeline(0);
	}
	
	/** Configures the vision pipeline to work with the cube (max exposure, LEDs off) and pauses for a given amount of milliseconds */
	public void setCubePipeline(int timeoutMs) {
		if ((!robot.getAutoMode() || robot.isAutonomous())) {
			turnOffLED();
			setNumber("pipeline", 0);
			Utility.sleep(timeoutMs);
		}
	} 
	
	/** Configures the vision pipeline to work with the vision target (min exposure, LEDs on) */
	public void setTargetPipeline() {
		setTargetPipeline(0);
	}
	
	/** Configures the vision pipeline to work with the vision target (min exposure, LEDs on) and pauses for a given amount of milliseconds */
	public void setTargetPipeline(int timeoutMs) {
		if ((!robot.getAutoMode() || robot.isAutonomous())) {
			turnOnLED();
			setNumber("pipeline", 1);
			Utility.sleep(timeoutMs);
		}
	}
	
	/** Returns true if the limelight has a valid target in its sights */
	public boolean hasValidTarget() {
		return getNumber("tv", 0) == 1;
	}
	
	/** Returns the percentage of the screen that the highlighted target is taking up, used to find distance */
	public double getPercentageOfScreen() {
		return getDouble("ta", 0);
	}
	
	/** Returns how far off center the found object is in the X direction (positive value means the object is to the right of center, negative value means the object is to the left of center) */
	public double getErrorX() {
		return getDouble("tx", 0);
	}
	
	/** Updates the latest error in the x direction if the target is in view of the limelight */
	public void updateLatestErrorX() {
		if (hasValidTarget())
			latestErrorX = getDouble("tx", 0);
	}
	
	/** Returns the latest error in the x direction (used to enable the limelight to remember which direction to scan in) */
	public double getLatestErrorX() {
		return latestErrorX;
	}
	
	/** Returns how far off center the found object is in the Y direction (positive value means the object is above center, negative value means the object is to below center) */
	public double getErrorY() {
		return getDouble("ty", 0);
	}
	
	/** Sets the given key to the given value */
	private void setNumber(String key, int value) {
		limelight.getEntry(key).setNumber(value);
	}
	
	/** Finds an integer from the limelight output using the given key */
	private int getNumber(String key, int defaultValue) {
		return limelight.getEntry(key).getNumber(defaultValue).intValue();
	}
	
	/** Finds a double from the limelight output using the given key */
	public double getDouble(String key, double defaultValue) {
		return limelight.getEntry(key).getDouble(defaultValue);
	}
	
	/** Turns the limelight's LEDs on */
	public void turnOnLED() {
		setLED(true);
	}
	
	/** Turns the limelight's LEDs off */
	public void turnOffLED() {
		setLED(false);
	}
	
	/** Toggles the LEDs on and off */
	public void toggleLED(boolean button) {
		if (Utility.toggle(ledToggle, button))
			setLED(ledToggle[0]);
	}
	
	/** Turns the limelight's LEDs on or off based on the given input */
	public void setLED(boolean on) {
		ledToggle[0] = on;
		setNumber("ledMode", on ? 0 : 1);
	}
	
	/** Returns true if the limelight's LEDs are on */
	public boolean isLEDOn() {
		return getNumber("ledMode", -1) == 0;
	}
	
	/** Takes a snapshot with the limelight */
	public void takeSnapshot() {
		setNumber("snapshot", 1);
	}
}