package org.usfirst.frc.team703.robot.subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import org.usfirst.frc.team703.robot.Robot;
import org.usfirst.frc.team703.robot.RobotMap;
import org.usfirst.frc.team703.robot.utilities.Utility;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;

public class DriveTrain {
	
	//*************************************************************** 
	//
	// DATA FIELDS
	//
	//***************************************************************
	
	// Drive constants
	private final double WHEEL_DIAMETER = RobotMap.DRIVETRAIN_WHEEL_DIAMETER;
	private final double ACCELERATION_RATE = 0.07; // 0.10 was preferred for driving (2017 robot) (lower value accelerates slower, higher value accelerates faster)
	private final double ACCELERATION_ACCEPTED_RANGE = 0.10; // 0.15 was used for driving with 0.10 acceleration rate
	
	// Gyro constants
	private final double GYRO_CORRECTION_SCALER = 0.05;  // 0.08 for low gear
	private final double GYRO_SCALER = 1; //1.025; // 1.02 for roborio gyro (lower value makes it turn more, higher value makes it turn less) 1.065
	
	// Encoder constants
	private final int TICKS_PER_ROTATION = 750;
	
	// Drive variables
	private double leftDrive, rightDrive;
	
	// Motor controllers
	private WPI_TalonSRX left1, left2, left3, right1, right2, right3;
	
	// Robot
	private Robot robot;
	
	// Gyro
	private ADXRS450_Gyro gyro;
	
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
		
		left2 = new WPI_TalonSRX(left2ID);
		left2.follow(left1);

		right2 = new WPI_TalonSRX(right2ID);
		right2.follow(right1);
	}
	
	/** Creates a 6 motor drive train */
	public DriveTrain(int left1ID, int left2ID, int left3ID, int right1ID, int right2ID, int right3ID, Robot robot) {
		this(left1ID, left2ID, right1ID, right2ID, robot);
		
		left3 = new WPI_TalonSRX(left3ID);
		left3.follow(left1);
		
		right3 = new WPI_TalonSRX(right3ID);
		right3.follow(right1);
	}
	
	//*************************************************************** 
	//
	// TELEOP DRIVING
	//
	//***************************************************************

	/** Drive the robot using the given cont via tank drive */
	public void tankDrive() {
		tankDrive(-robot.leftJoy.getRawAxis(RobotMap.DRIVE_LEFT), -robot.rightJoy.getRawAxis(RobotMap.DRIVE_RIGHT));
	}
	
	/** Drive the robot using the given cont via arcade drive */
	public void arcadeDrive() {
		arcadeDrive(-robot.leftJoy.getRawAxis(RobotMap.DRIVE_FORWARD), robot.leftJoy.getRawAxis(RobotMap.DRIVE_TURN));
	}
	
	//*************************************************************** 
	//
	// AUTONOMOUS DRIVING
	//
	//***************************************************************
	
	/** Drives forward a given distance in inches */
	public void driveForward(double distanceInInches) {
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
		if (Math.abs(leftDrive) > RobotMap.DRIVE_DEADBAND)
			left1.set(-leftDrive);
		else
			left1.set(0);
		
		if (Math.abs(rightDrive) > RobotMap.DRIVE_DEADBAND)
			right1.set(rightDrive);
		else
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
		final double MIN_SPEED = reversed ? -0.3 : 0.3;
		final double MAX_SPEED = reversed ? -0.5 : 0.5;
		final double kP = 0.0003; //0.0002 for high gear
		
		double distanceInTicks = inchesToTicks(distanceInInches);
		resetEncoders();
		resetGyro();
		
		double error = 0;
		
		while (Math.abs(getLeftEncPosition()) < distanceInTicks && Math.abs(getRightEncPosition()) < distanceInTicks && robot.isAutonomous() && robot.isEnabled()) {
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
		
		forcedTankDrive(0, 0);
	}
	
	/** Drives the robot at a given speed, using the gyro to correct it if it steers off course */
	public void gyroAssistedDrive(double speed) {
		forcedArcadeDrive(speed, -getGyroCorrectionAngle());
	}
	
	/** Turns a given number of degrees (positive angle turns right, negative angle turns left */
	public void turn(double angleInDegrees) {
		resetGyro();
		final double MIN_SPEED = (angleInDegrees < 0) ? -0.25 : 0.25; // 0.2
		final double MAX_SPEED = (angleInDegrees < 0) ? -0.9 : 0.9;
		final double kP = 0.015;  // 0.016 //0.03 works well for low gear (lower value means slower deceleration, higher value means faster deceleration)
		double error = 0;
		
		while (Math.abs(getGyroAngle()) < Math.abs(angleInDegrees) && robot.isAutonomous() && robot.isEnabled()) {
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
	private double inchesToTicks(double inches) {
		return inches / (WHEEL_DIAMETER * Math.PI) * TICKS_PER_ROTATION;
	}
	
	/** Resets the left and right encoders */
	public void resetEncoders() {
		left1.getSensorCollection().setQuadraturePosition(0, 0);
		right1.getSensorCollection().setQuadraturePosition(0, 0);
		Utility.sleep(200);
	}
	
	//*************************************************************** 
	//
	// GYRO UTILITIES
	//
	//***************************************************************
	
	/** Resets the gyro angle to 0 degrees */
	public void resetGyro() {
		gyro.reset();
		while (Math.abs(getGyroAngle()) > 3);
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
		int encPosition = -left1.getSensorCollection().getQuadraturePosition();
		System.out.println("Left enc: " + encPosition);
		return encPosition;
	}
	
	/** Returns the right encoder position */
	public int getRightEncPosition() {
		int encPosition = right1.getSensorCollection().getQuadraturePosition();
		System.out.println("Right enc: " + encPosition);
		return encPosition;
	}
	
}