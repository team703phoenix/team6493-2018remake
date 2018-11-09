package org.usfirst.frc.team703.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Solenoid;
import org.usfirst.frc.team703.robot.RobotMap;
import org.usfirst.frc.team703.robot.utilities.Utility;

public class Arms {
    private static WPI_TalonSRX leftArm;
    private static WPI_TalonSRX rightArm;
    private static Solenoid close;
    private static Solenoid down;
    private boolean[] armsClosed = {true, true};
    private boolean[] armsDown = {false, true};
    private boolean[] creepMode = {false, true};
    public double mult = 1;
    public final double INTAKE_SPEED = 0.7;
    
    private final double CREEP_MODE_SCALER = 0.5;

    public Arms(){
        leftArm = new WPI_TalonSRX(RobotMap.ARM_MOTOR_LEFT);
        rightArm = new WPI_TalonSRX(RobotMap.ARM_MOTOR_RIGHT);
        close = new Solenoid(RobotMap.PCM_CHANNEL, RobotMap.ARM_SOLENOID_CHANNEL);
        down = new Solenoid(RobotMap.PCM_CHANNEL, RobotMap.ARM_LIFT_SOLENOID_CHANNEL);
    }

    public void open(){
        close.set(false);
        armsClosed[0] = false;
    }
    
    public void close() {
    	close.set(true);
    	armsClosed[0] = true;
    }
    
    public void toggleOpen(boolean button) {
    	if (Utility.toggle(armsClosed, button)) {
    		if (armsClosed[0])
    			close();
    		else
    			open();
    	}
    }
    
    public void up() {
    	down.set(false);
    	armsDown[0] = false;
    }
    
    public void down() {
    	down.set(true);
    	armsDown[0] = true;
    }
    
    public void toggleDown(boolean button) {
    	if (Utility.toggle(armsDown, button)) {
    		if (armsDown[0])
    			down();
    		else
    			up();
    	}
    }
    
    public void setSpeed(double speedLeft, double speedRight) {
    	if(Math.abs(speedLeft * mult) < RobotMap.ARM_DEADBAND){
            leftArm.set(0);
        }else{
        	if (!creepMode[0])
        		leftArm.set(speedLeft * mult);
        	else
        		leftArm.set(speedLeft * mult * CREEP_MODE_SCALER);
        }
        
        if(Math.abs(speedRight * mult) < RobotMap.ARM_DEADBAND){
            rightArm.set(0);
        }else{
        	if (!creepMode[0])
        		rightArm.set(-speedRight * mult);
        	else
        		rightArm.set(-speedRight * mult * CREEP_MODE_SCALER);
        }
    }

    public void setSpeed(double speedLeft, double speedRight, boolean reverse){
        if (reverse)
        	setSpeed(-speedLeft, -speedRight);
        else
        	setSpeed(speedLeft, speedRight);
    }
    
    public void setCreepMode(boolean creepModeOn) {
    	creepMode[0] = creepModeOn;
    }
    
    public void toggleCreepMode(boolean button) {
    	Utility.toggle(creepMode, button);
    }
}
