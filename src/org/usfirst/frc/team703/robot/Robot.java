package org.usfirst.frc.team703.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import org.usfirst.frc.team703.robot.subsystems.*;

public class Robot extends IterativeRobot {
	public Joystick driverCont = new Joystick(RobotMap.DRIVER);
	public Joystick cont = new Joystick(RobotMap.OPERATOR);

	public DriveTrain drive;
	public Elevator lift;
	public Arms intake;
	public Vision vision;
	public AutonHandler auton;

	boolean moveUp = false;
	boolean moveDown = false;
	boolean armToggle = true;
	
	boolean autoMode; // Used for multiple robot classes
	boolean autoEnd = false;

	public void robotInit() {
		drive = new DriveTrain(RobotMap.LEFT_FRONT_MOTOR_CHANNEL, RobotMap.LEFT_REAR_MOTOR_CHANNEL, 
				RobotMap.RIGHT_FRONT_MOTOR_CHANNEL, RobotMap.RIGHT_REAR_MOTOR_CHANNEL, this);
		lift = new Elevator();
		intake = new Arms();
		vision = new Vision(this);
		auton = new AutonHandler(drive, lift, intake, vision, this);
		
		auton.publishDashboard();
	}
	
	public void autonomousInit() {	
		autoMode = true;
		autoEnd = false;
		
		intake.close();
		
		auton.readFromDashboard();
	}
	
	public void autonomousPeriodic() {
		if (!autoEnd) {
			System.out.println("AUTONOMOUS RUNNING");
			auton.runAuton();
		}
		
		autoEnd = true;
	}
	
	public void teleopInit() {
		autoMode = false;
		
		lift.stop();
		vision.turnOffLED();
	}
	
	public void teleopPeriodic() {
		//System.out.println("Is stuck: " + drive.checkIfStuck());
		
		drive.tankDrive();
		
		intake.setSpeed(cont.getRawAxis(RobotMap.ARM_LEFT),cont.getRawAxis(RobotMap.ARM_RIGHT),
				cont.getRawButton(RobotMap.ARM_SHOOT[0]) && cont.getRawButton(RobotMap.ARM_SHOOT[1]));
		intake.toggleOpen(cont.getRawButton(RobotMap.TOGGLE_ARM_OPEN));
		intake.toggleDown(cont.getRawButton(RobotMap.TOGGLE_ARM_DOWN));
		
		lift.move(cont.getRawAxis(RobotMap.ELEVATOR_CONTROL));
		
		vision.toggleLED(driverCont.getRawButton(RobotMap.VISION_LIGHT_TOGGLE));
		vision.updateLatestErrorX();
		
		if (driverCont.getRawButton(9))
			vision.followTarget();
		
		
		//System.out.println("Error X: " + vision.getErrorX() + " | Error Y: " + vision.getErrorY());
	}
	
	public void disabledInit() {
		
	}
	
	public void disabledPeriodic() {
		
	}
	
	//*************************************************************** 
	//
	// GETTERS
	//
	//***************************************************************
	
	public boolean getAutoMode() {
		return autoMode;
	}
}