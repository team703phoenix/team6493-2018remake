/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

/* TODO: Assign values to dummy variables */

package org.usfirst.frc.team703.robot;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
	public static final int LEFT_FRONT_MOTOR_CHANNEL = 1;
	public static final int LEFT_REAR_MOTOR_CHANNEL = 2;
	public static final int RIGHT_FRONT_MOTOR_CHANNEL = 3;
	public static final int RIGHT_REAR_MOTOR_CHANNEL = 4;

	public static final double DRIVE_DEADBAND = 0.1;
	public static final double ARM_DEADBAND = 0.1;
	public static final double ELEVATOR_DEADBAND = 0.1;

	public static final int PRIMARY_LIFT_MOTOR_CHANNEL = 5;
	public static final int SECONDARY_LIFT_MOTOR_CHANNEL = 6;
	
	public static final int PCM_CHANNEL = 11;

	public static final int LIFT_SOLENOID_CHANNEL = 0;

	public static final int ARM_SOLENOID_CHANNEL = 1;
	public static final int ARM_LIFT_SOLENOID_CHANNEL = 2;

	public static final int ARM_MOTOR_LEFT = 9;
	public static final int ARM_MOTOR_RIGHT = 10;
	
	public static final int DRIVER = 0;
	public static final int OPERATOR = 1;
	  
	public static final int LEFT_ENCODER_CHANNEL_A = 0;
	public static final int LEFT_ENCODER_CHANNEL_B = 1;
	public static final int RIGHT_ENCODER_CHANNEL_A = 2;
	public static final int RIGHT_ENCODER_CHANNEL_B = 3;
	
	public static final int QUADRATURE_FACTOR = 4;
	public static final int ENCODER_FACTOR = 360;
	public static final int PULSES_PER_REVOLUTION = QUADRATURE_FACTOR * ENCODER_FACTOR;
	public static final int DRIVETRAIN_WHEEL_DIAMETER = 8;
	public static final double DRIVETRAIN_WHEEL_CIRCUMFERENCE = DRIVETRAIN_WHEEL_DIAMETER * Math.PI;
	public static final double DRIVETRAIN_ENCODER_INCHES_PER_PULSE = DRIVETRAIN_WHEEL_CIRCUMFERENCE / PULSES_PER_REVOLUTION;
	
	/* Button Mapping for Driving */
	public static final int DRIVE_LEFT = 1;
	public static final int DRIVE_RIGHT = 3;
	public static final int DRIVE_FORWARD = 1;
	public static final int DRIVE_TURN = 0;

	/* Button Mapping for Elevator */
	public static final int ELEVATOR_CONTROL = 1;
	
	/* Button Mapping for Arms */
	public static final int ARM_LEFT = 2;
	public static final int ARM_RIGHT = 3;
	public static final int[] ARM_SHOOT = {5, 6};
	public static final int TOGGLE_ARM_OPEN = 10;
	public static final int TOGGLE_ARM_UP = 9;
	
	/* Button Mapping for Vision Functions */
	public static final int VISION_STOP = 15; // DUMMY VALUE
    
	// For example to map the left and right motors, you could define the
	// following variables to use with your drivetrain subsystem.
	// public static int leftMotor = 1;
	// public static int rightMotor = 2;

	// If you are using multiple modules, make sure to define both the port
	// number and the module. For example you with a rangefinder:
	// public static int rangefinderPort = 1;
	// public static int rangefinderModule = 1;
}

