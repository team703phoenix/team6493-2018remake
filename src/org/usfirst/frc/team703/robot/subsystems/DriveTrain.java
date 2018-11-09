package org.usfirst.frc.team703.robot.subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import org.usfirst.frc.team703.robot.Robot;
import org.usfirst.frc.team703.robot.RobotMap;
import org.usfirst.frc.team703.robot.utilities.Utility;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Timer;

public class DriveTrain {
	
	//*************************************************************** 
	//
	// DATA FIELDS
	//
	//***************************************************************
	
	// Drive constants
	private final double WHEEL_DIAMETER = RobotMap.DRIVETRAIN_WHEEL_DIAMETER;
	private final double ACCELERATION_RATE = 0.06; // (lower value accelerates slower, higher value accelerates faster)
	private final double ACCELERATION_ACCEPTED_RANGE = 0.10;
	private final double CREEP_MODE_SCALER = 0.5;
	
	// Gyro constants
	private final double GYRO_CORRECTION_SCALER = 0.08;  // 0.08 for low gear
	private final double GYRO_SCALER = 1.04; // (lower value makes it turn more, higher value makes it turn less)
	
	// Encoder constants
	private final int TICKS_PER_ROTATION = 925;
	private final int ENCODER_STUCK_RANGE = inchesToTicks(1); // 1 inch per second
	private final double ENCODER_STUCK_GRACE_PERIOD = 1.0; // 1 second
	
	// Drive variables
	private double leftDrive, rightDrive;
	
	// Motor controllers
	private WPI_TalonSRX left1, right1;
	private WPI_VictorSPX left2, right2;
	
	// Robot
	private Robot robot;
	
	// Gyro
	private ADXRS450_Gyro gyro;
	
	// Timer used to see if the robot is stuck during autonomous
	Timer stuckTimer = new Timer();
	
	// Control variables
	boolean[] creepMode = {false, true};
	
	//*************************************************************** 
	//
	// CONSTRUCTORS
	//
	//***************************************************************
	
	/** Creates a 2 motor drive train */
	public DriveTrain(int left1ID, int right1ID, Robot robot) {
		left1 = new WPI_TalonSRX(left1ID);
		left1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 10);
		right1 = new WPI_TalonSRX(right1ID);
		right1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 10);
		
		gyro = new ADXRS450_Gyro();
		
		this.robot = robot;
	}
	
	/** Creates a 4 motor drive train */
	public DriveTrain(int left1ID, int left2ID, int right1ID, int right2ID, Robot robot) {
		this(left1ID, right1ID, robot);
		
		left2 = new WPI_VictorSPX(left2ID);
		left2.follow(left1);

		right2 = new WPI_VictorSPX(right2ID);
		right2.follow(right1);
	}
	
	//*************************************************************** 
	//
	// TELEOP DRIVING
	//
	//***************************************************************

	/** Drive the robot using the given cont via tank drive */
	public void tankDrive() {
		tankDrive(-robot.driverCont.getRawAxis(RobotMap.DRIVE_LEFT),
				-robot.driverCont.getRawAxis(RobotMap.DRIVE_RIGHT));
	}
	
	/** Drive the robot using the given cont via tank drive */
	public void forcedTankDrive() {
		forcedTankDrive(-robot.driverCont.getRawAxis(RobotMap.DRIVE_LEFT),
				-robot.driverCont.getRawAxis(RobotMap.DRIVE_RIGHT));
	}
	
	/** Drive the robot using the given cont via arcade drive */
	public void arcadeDrive() {
		arcadeDrive(-robot.driverCont.getRawAxis(RobotMap.DRIVE_FORWARD) * 0.6,
				robot.driverCont.getRawAxis(RobotMap.DRIVE_TURN) * 0.6);
	}
	
	//*************************************************************** 
	//
	// AUTONOMOUS DRIVING
	//
	//***************************************************************
	
	/** Drives forward a given distance in inches */
	public void driveForward(double distanceInInches) {
		//System.out.println("DRIVE FORWARD START");
		encoderDrive(distanceInInches, false);
	}
	
	/** Drives backward a given distance in inches */
	public void driveBackward(double distanceInInches) {
		encoderDrive(distanceInInches, true);
	}
	
	/** Turns left a given number of degrees */
	public void turnLeft(double angleInDegrees) {
		turn(-angleInDegrees);
	}
	
	/** Turns right a given number of degrees */
	public void turnRight(double angleInDegrees) {
		turn(angleInDegrees);
	}
	
	//*************************************************************** 
	//
	// UTILITY DRIVING
	//
	//***************************************************************
	
	/** Drives the robot using the given variables via tank drive */
	public void tankDrive(double leftDrive, double rightDrive) {
		this.leftDrive = accelDecel(leftDrive, this.leftDrive);
		this.rightDrive = accelDecel(rightDrive, this.rightDrive);
		
		forcedTankDrive(this.leftDrive, this.rightDrive);
	}
	
	/** Drives the robot using the given variables via tank drive without an acceleration curve */
	public void forcedTankDrive(double leftDrive, double rightDrive) {
		if (Math.abs(leftDrive) > RobotMap.DRIVE_DEADBAND) {
			if (!creepMode[0])
				left1.set(leftDrive);
			else
				left1.set(leftDrive * CREEP_MODE_SCALER);
	 	} else
			left1.set(0);
		
		if (Math.abs(rightDrive) > RobotMap.DRIVE_DEADBAND) {
			if (!creepMode[0])
				right1.set(-rightDrive);
			else
				right1.set(-rightDrive * CREEP_MODE_SCALER);
		} else
			right1.set(0);
	}
	
	/** Drive the robot using the given variables via arcade drive */
	public void arcadeDrive(double forwardDrive, double turnDrive) {
		tankDrive(forwardDrive + turnDrive, forwardDrive - turnDrive);
	}
	
	/** Drives the robot using the given variables via arcade drive without an acceleration curve */
	public void forcedArcadeDrive(double forwardDrive, double turnDrive) {
		forcedTankDrive(forwardDrive + turnDrive, forwardDrive - turnDrive);
	}
	
	/** Acceleration curve */
	private double accelDecel(double desiredSpeed, double currentSpeed) {
		double acceptedRange = ACCELERATION_ACCEPTED_RANGE / 2;
		
		if (currentSpeed < desiredSpeed - acceptedRange)
			return currentSpeed += ACCELERATION_RATE;
		else if (currentSpeed > desiredSpeed + acceptedRange)
			return currentSpeed -= ACCELERATION_RATE;
		else
			return desiredSpeed;
	}
	
	/** Drives forward or backward a given distance at a given speed using encoders */
	private void encoderDrive(double distanceInInches, boolean reversed) {
		final double MIN_SPEED = reversed ? -0.4 : 0.4;
		final double MAX_SPEED = reversed ? -0.75 : 0.75;
		final double kP = 0.0003; //0.0002 for high gear
				
		//System.out.println("DRIVE START");
		
		double distanceInTicks = inchesToTicks(distanceInInches);
		resetEncoders();
		resetGyro();
		
		double error = 0;
		
		stuckTimer.reset();
		stuckTimer.start();
		
		while ((Math.abs(getLeftEncPosition()) < distanceInTicks || Math.abs(getRightEncPosition()) < distanceInTicks) && !robot.haltAutonomous()) {// && checkIfStuck() && stuckTimer.get() > ENCODER_STUCK_GRACE_PERIOD) {
			//System.out.println("DRIVE LOOP");
			error = (distanceInTicks - Math.abs(getLeftEncPosition())) * kP;
			if (reversed)
				error = -error;
			
			if (Math.abs(error) >= Math.abs(MIN_SPEED) && Math.abs(error) <= Math.abs(MAX_SPEED))
				gyroAssistedDrive(error);
			else if (Math.abs(error) < Math.abs(MIN_SPEED))
				gyroAssistedDrive(MIN_SPEED);
			else
				gyroAssistedDrive(MAX_SPEED);
		}
		
		//System.out.println("END DRIVE. Left: " + getLeftEncPosition() + " | Right: " + getRightEncPosition());
		
		forcedTankDrive(0, 0);
	}
	
	
	/** Drives the robot at a given speed, using the gyro to correct it if it steers off course */
	public void gyroAssistedDrive(double speed) {
		arcadeDrive(speed, -getGyroCorrectionAngle());
	}
	
	/** Turns a given number of degrees (positive angle turns right, negative angle turns left */
	/*public void turn(double angleInDegrees) {
		resetGyro();
		final double TURN_SPEED = (angleInDegrees < 0) ? -0.9 : 0.9;
		while (Math.abs(getGyroAngle()) < Math.abs(angleInDegrees) && !robot.haltAutonomous()) {
			System.out.println("TURNING: " + getGyroAngle());
			forcedArcadeDrive(0, TURN_SPEED);
		}
		
		forcedTankDrive(0, 0);
	}*/
	
	/** Turns a given number of degrees (positive angle turns right, negative angle turns left */
	public void turn(double angleInDegrees) {
		resetGyro();
		final double MIN_SPEED = (angleInDegrees < 0) ? -0.5 : 0.5; // 0.2
		final double MAX_SPEED = (angleInDegrees < 0) ? -0.85 : 0.85;
		final double kP = 0.01;  // 0.016 //0.03 works well for low gear (lower value means slower deceleration, higher value means faster deceleration)
		double error = 0;
		
		while (Math.abs(getGyroAngle()) < Math.abs(angleInDegrees) && !robot.haltAutonomous()) {
			error = (Math.abs(angleInDegrees) - Math.abs(getGyroAngle())) * kP;
			if (angleInDegrees < 0)
				error = -error;
			
			if (Math.abs(error) >= Math.abs(MIN_SPEED) && Math.abs(error) <= Math.abs(MAX_SPEED))
				forcedArcadeDrive(0, error);
			else if (Math.abs(error) < Math.abs(MIN_SPEED))
				forcedArcadeDrive(0, MIN_SPEED);
			else
				forcedArcadeDrive(0, MAX_SPEED);
		}
		
		forcedTankDrive(0, 0);
	}
	
	//*************************************************************** 
	//
	// ENCODER UTILITIES
	//
	//***************************************************************
	
	/** Converts inches to encoder ticks */
	private int inchesToTicks(double inches) {
		return (int)(inches / (WHEEL_DIAMETER * Math.PI) * TICKS_PER_ROTATION);
	}
	
	/** Resets the left and right encoders */
	public void resetEncoders() {
		System.out.println("Resetting encoders...");
		left1.getSensorCollection().setQuadraturePosition(0, 0);
		right1.getSensorCollection().setQuadraturePosition(0, 0);
		Utility.sleep(200);
		while (Math.abs(getLeftEncPosition()) > 200 || Math.abs(getRightEncPosition()) > 200) {
			System.out.println("Attempting to restart encoders again...");
			left1.getSensorCollection().setQuadraturePosition(0, 0);
			right1.getSensorCollection().setQuadraturePosition(0, 0);
			Utility.sleep(500);
		}
			//System.out.println("Resetting... Left enc: " + getLeftEncPosition() + " | Right enc: " + getRightEncPosition());
		
		System.out.println("Encoders reset. Left enc: " + getLeftEncPosition() + " | Right enc: " + getRightEncPosition());
	}
	
	public boolean checkIfStuck() {	
		if (Math.abs(getLeftEncVelocity()) < ENCODER_STUCK_RANGE ||
				Math.abs(getRightEncVelocity()) < ENCODER_STUCK_RANGE) {
			System.out.println("Robot is stuck. Halting...");
			return true;
		} else {
			return false;
		}
	}
	
	//*************************************************************** 
	//
	// GYRO UTILITIES
	//
	//***************************************************************
	
	/** Resets the gyro angle to 0 degrees */
	public void resetGyro() {
		while (Math.abs(getGyroAngle()) > 3) {
			gyro.reset();
		}
		
		System.out.println("Gyro_reset: " + getGyroAngle());
	}
	
	//*************************************************************** 
	//
	// GENERAL UTILITIES
	//
	//***************************************************************
	
	/** Turns creep mode on or off */
	public void setCreepMode(boolean creepModeOn) {
		creepMode[0] = creepModeOn;
	}
	
	/** Toggles creep mode */
	public void toggleCreepMode(boolean button) {
		Utility.toggle(creepMode, button);
	}
	
	//*************************************************************** 
	//
	// GETTERS
	//
	//***************************************************************
	
	/** Returns the current gyro angle */
	public double getGyroAngle() {
		return gyro.getAngle() * GYRO_SCALER;
	}
	
	/** Returns the value needed for gyro assisted drive */
	public double getGyroCorrectionAngle() {
		return getGyroAngle() * GYRO_CORRECTION_SCALER;
	}
	
	/** Returns the left encoder position */
	public int getLeftEncPosition() {
		return -left1.getSensorCollection().getQuadraturePosition();
	}
	
	/** Returns the left encoder velocity */
	public int getLeftEncVelocity() {
		return -left1.getSensorCollection().getQuadratureVelocity();
	}
	
	/** Returns the right encoder position */
	public int getRightEncPosition() {
		return right1.getSensorCollection().getQuadraturePosition();
	}
	
	/** Returns the right encoder velocity */
	public int getRightEncVelocity() {
		return right1.getSensorCollection().getQuadratureVelocity();
	}
	
}