package org.usfirst.frc.team703.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
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
		
		UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(320, 240);
	}
	
	public void autonomousInit() {	
		autoMode = true;
		autoEnd = false;
		
		intake.close();
		
		auton.readFromDashboard();
	}
	
	public void autonomousPeriodic() {
		if (!autoEnd) {
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
		
		drive.setCreepMode(driverCont.getRawButton(RobotMap.DRIVE_CREEP_MODE));
		
		drive.arcadeDrive();
		
		intake.setCreepMode(cont.getRawButton(RobotMap.INTAKE_CREEP_MODE));
		
		intake.setSpeed(cont.getRawAxis(RobotMap.ARM_LEFT),cont.getRawAxis(RobotMap.ARM_RIGHT),
				cont.getRawButton(RobotMap.ARM_SHOOT[0]) && cont.getRawButton(RobotMap.ARM_SHOOT[1]));
		intake.toggleOpen(cont.getRawButton(RobotMap.TOGGLE_ARM_OPEN));
		intake.toggleDown(cont.getRawButton(RobotMap.TOGGLE_ARM_DOWN));
		
		lift.move(cont.getRawAxis(RobotMap.ELEVATOR_CONTROL));
		
		vision.toggleLED(driverCont.getRawButton(RobotMap.VISION_LIGHT_TOGGLE));
		vision.updateLatestErrorX();
		
		if (driverCont.getRawButton(9))
			vision.followTarget();
		
		//if (driverCont.getRawButton(6))
			//vision.pickupCube();
	}
	
	public void disabledInit() {
		
	}
	
	public void disabledPeriodic() {
		
	}
	
	//***************************************************************
	//
	// UTILITIES
	//
	//***************************************************************
	
	public boolean haltAutonomous() {
		boolean halt = false;
		
		if (!isAutonomous()) {
			System.out.print("Robot is not in autonomous. ");
			halt = true;
		}
		
		if (!isEnabled()) {
			System.out.print("Robot is not enabled. ");
			halt = true;
		}
		
		if (halt)
			System.out.println("Autonomous halting prematurely...");
		
		return halt;
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