package org.usfirst.frc.team703.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Solenoid;
import org.usfirst.frc.team703.robot.RobotMap;

public class Arms {
    private static WPI_TalonSRX leftArm;
    private static WPI_TalonSRX rightArm;
    private static Solenoid open;
    private static Solenoid up;
    public boolean armsOpen;
    public double mult = 1;

    public Arms(){
        leftArm = new WPI_TalonSRX(RobotMap.ARM_MOTOR_LEFT);
        rightArm = new WPI_TalonSRX(RobotMap.ARM_MOTOR_RIGHT);
        open = new Solenoid(RobotMap.PCM_CHANNEL, RobotMap.ARM_SOLENOID_CHANNEL);
        up = new Solenoid(RobotMap.PCM_CHANNEL, RobotMap.ARM_LIFT_SOLENOID_CHANNEL);
    }

    public void open(){
        open.set(true);
        armsOpen = true;
    }
    
    public void close() {
    	open.set(false);
    	armsOpen = false;
    }

    public void setSpeed(double speedLeft, double speedRight){
        if(Math.abs(speedLeft * mult) < RobotMap.ARM_DEADBAND){
            leftArm.set(0);
        }else{
            leftArm.set(speedLeft * mult);
        }
        
        if(Math.abs(speedRight * mult) < RobotMap.ARM_DEADBAND){
            rightArm.set(0);
        }else{
            rightArm.set(speedRight * mult);
        }
    }

    public void reverse(){
        if(mult > 0) {
        	mult = -1;
        }else {
        	mult = 1;
        }
    }
}
