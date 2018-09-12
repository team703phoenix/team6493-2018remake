package org.usfirst.frc.team703.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.Solenoid;
import org.usfirst.frc.team703.robot.RobotMap;

public class Elevator {
    private static WPI_TalonSRX liftMotorPrime;
    private static WPI_VictorSPX liftMotorSecunde;
    private static Solenoid brake;

    public Elevator(){
        liftMotorPrime = new WPI_TalonSRX(RobotMap.PRIMARY_LIFT_MOTOR_CHANNEL);
        liftMotorSecunde = new WPI_VictorSPX(RobotMap.SECONDARY_LIFT_MOTOR_CHANNEL);

        brake = new Solenoid(RobotMap.PCM_CHANNEL, RobotMap.LIFT_SOLENOID_CHANNEL);

        liftMotorSecunde.follow(liftMotorPrime);
    }
    
    public void move(double axis) {
    	if (Math.abs(axis) > RobotMap.ELEVATOR_DEADBAND) {
    		liftMotorPrime.set(axis);
    		brake.set(false);
    	} else {
    		liftMotorPrime.set(0);
    		brake.set(true);
    	}
    }
    
    public void up(boolean up) {
    	if (up)
    		move(1);
    	else
    		move(0);
    }
    
    public void down(boolean down) {
    	if (down)
    		move(-1);
    	else
    		move(0);
    }
}