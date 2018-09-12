package org.usfirst.frc.team703.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Solenoid;
import org.usfirst.frc.team703.robot.RobotMap;
import org.usfirst.frc.team703.robot.utilities.Utility;

public class Arms {
    private static WPI_TalonSRX leftArm;
    private static WPI_TalonSRX rightArm;
    private static Solenoid open;
    private static Solenoid up;
    private boolean[] armsOpen = {false, true};
    private boolean[] armsUp = {true, true};
    public double mult = 1;

    public Arms(){
        leftArm = new WPI_TalonSRX(RobotMap.ARM_MOTOR_LEFT);
        rightArm = new WPI_TalonSRX(RobotMap.ARM_MOTOR_RIGHT);
        open = new Solenoid(RobotMap.PCM_CHANNEL, RobotMap.ARM_SOLENOID_CHANNEL);
        up = new Solenoid(RobotMap.PCM_CHANNEL, RobotMap.ARM_LIFT_SOLENOID_CHANNEL);
    }

    public void open(){
        open.set(true);
        armsOpen[0] = true;
    }
    
    public void close() {
    	open.set(false);
    	armsOpen[0] = false;
    }
    
    public void toggleOpen(boolean button) {
    	if (Utility.toggle(armsOpen, button)) {
    		if (armsOpen[0])
    			open();
    		else
    			close();
    	}
    }
    
    public void up() {
    	up.set(true);
    	armsUp[0] = true;
    }
    
    public void down() {
    	up.set(false);
    	armsUp[0] = false;
    }
    
    public void toggleUp(boolean button) {
    	if (Utility.toggle(armsUp, button)) {
    		if (armsUp[0])
    			up();
    		else
    			down();
    	}
    }
    
    public void setSpeed(double speedLeft, double speedRight) {
    	if(Math.abs(speedLeft * mult) < RobotMap.ARM_DEADBAND){
            leftArm.set(0);
        }else{
        	leftArm.set(speedLeft * mult);
        }
        
        if(Math.abs(speedRight * mult) < RobotMap.ARM_DEADBAND){
            rightArm.set(0);
        }else{
        	rightArm.set(-speedRight * mult);
        }
    }

    public void setSpeed(double speedLeft, double speedRight, boolean reverse){
        if (reverse)
        	setSpeed(-speedLeft, -speedRight);
        else
        	setSpeed(speedLeft, speedRight);
    }
}
