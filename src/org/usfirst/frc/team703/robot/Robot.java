package org.usfirst.frc.team703.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import org.usfirst.frc.team703.robot.subsystems.*;

public class Robot extends IterativeRobot {
	private String switchSetup;

	public Joystick driverCont = new Joystick(RobotMap.DRIVER);
	public Joystick cont = new Joystick(RobotMap.OPERATOR);

	public DriveTrain drive;
	public Elevator lift;
	public Arms intake;
	
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
		
		auton = new AutonHandler(drive, lift, intake);
		auton.publishDashboard();
	}
	
	public void autonomousInit() {
		switchSetup = DriverStation.getInstance().getGameSpecificMessage();
		
		autoMode = true;
		autoEnd = false;
	}
	
	public void autonomousPeriodic() {
		if (!autoEnd) {
			//auton.runAuton(switchSetup, selection, position, false);
		}
		
		autoEnd = true;
	}
	
	public void teleopInit() {
		autoMode = false;
	}
	
	public void teleopPeriodic() {
		drive.tankDrive();
		intake.setSpeed(cont.getRawAxis(RobotMap.ARM_LEFT),cont.getRawAxis(RobotMap.ARM_RIGHT),
				cont.getRawButton(RobotMap.ARM_SHOOT[0]) && cont.getRawButton(RobotMap.ARM_SHOOT[1]));
		intake.toggleOpen(cont.getRawButton(RobotMap.TOGGLE_ARM_OPEN));
		intake.toggleUp(cont.getRawButton(RobotMap.TOGGLE_ARM_UP));
		lift.move(cont.getRawAxis(RobotMap.ELEVATOR_CONTROL));
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