package org.usfirst.frc.team703.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import org.usfirst.frc.team703.robot.subsystems.*;

public class Robot extends IterativeRobot {

	public Joystick leftJoy = new Joystick(RobotMap.LEFT_DRIVER);
	public Joystick rightJoy = new Joystick(RobotMap.RIGHT_DRIVER);
	public Joystick cont = new Joystick(RobotMap.OPERATOR);

	public DriveTrain drive;
	public Elevator lift;
	public Arms intake;

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
	}
	
	public void autonomousInit() {
		autoMode = true;
		autoEnd = false;
	}
	
	public void autonomousPeriodic() {
		if (!autoEnd) {
			drive.driveForward(48);
		}
		
		autoEnd = true;
	}
	
	public void teleopInit() {
		autoMode = false;
	}
	
	public void teleopPeriodic() {
		if(cont.getRawButton(RobotMap.TOGGLE_ARM) && armToggle) {
			if(intake.armsOpen) {
				intake.close();
			}else {
				intake.open();
			}
			armToggle = false;
		}else if(cont.getRawButton(RobotMap.TOGGLE_ARM)) {
			armToggle = true;
		}
		
		if(cont.getRawButton(RobotMap.ELEVATOR_UP)){
			moveUp = true;
		}else{
			moveUp = false;
		}

		if(cont.getRawButton(RobotMap.ELEVATOR_DOWN)){
			moveDown = true;
		}else{
			moveDown = false;
		}

		drive.tankDrive();
		intake.setSpeed(cont.getRawAxis(RobotMap.ARM_LEFT),cont.getRawAxis(RobotMap.ARM_RIGHT));
		lift.up(moveUp);
		lift.down(moveDown);
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