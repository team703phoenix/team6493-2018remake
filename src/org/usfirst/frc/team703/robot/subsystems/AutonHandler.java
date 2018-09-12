package org.usfirst.frc.team703.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team703.robot.utilities.Utility;

public class AutonHandler {
	/* My selectors iterate downwards from the chosen selection to the
     * next nearest in case the color is not in our favor. To instead make
     * it absolute, set this variable to true & put something in the "defaul()" method */
	
	// Constants
	public static final int ELEVATOR_TIMEOUT_SWITCH = 500;
	public static final int ELEVATOR_TIMEOUT_SCALE = 1000;
	
	public static final int ARM_DISPENSE_TIMEOUT = 1000;
	
	// Dashboard inputs
	private String startingPos, gameData;
	private boolean isAbsolute, crossField;
	private int destination, numOfCubes;
	
    private final DriveTrain drive;
    private final Elevator lift;
    private final Arms intake;
    
    // Sendable choosers
 	private SendableChooser<String> positionInput = new SendableChooser<>();
 	private SendableChooser<Boolean> destinationTypeInput = new SendableChooser<>();
 	private SendableChooser<Boolean> crossFieldInput = new SendableChooser<>();
 	private SendableChooser<Integer> destinationInput = new SendableChooser<>();
 	private SendableChooser<String> switchSideInput = new SendableChooser<>();
 	private SendableChooser<String> scaleSideInput = new SendableChooser<>();
 	private SendableChooser<Integer> numOfCubesInput = new SendableChooser<>();

    public AutonHandler(DriveTrain drive, Elevator lift, Arms intake) {
        this.drive = drive;
        this.lift = lift;
        this.intake = intake;
    }

    /* Gamedata is of format LRL with respect to the relevant alliance's
     * color. This has to be explicitly specified in a driverstation not
     * connected to an FMS, so if the value seems weird, defaults to LLL */

    public void runAuton() {
        checkGameData(gameData);

        switch (startingPos) {
            case "L": leftSelector(); break;
            case "C": centerSelector(); break;
            case "R": rightSelector(); break;
            default: throw new RuntimeException("Somehow you set the position to an invalid value. " +
            		"It can only be L, C, or R.");
        }
    }

    private void checkGameData(String gameData) {
        if (gameData.length() != 3) gameData = "LLL";
    }

    /* The selection integer is within the range [1,3], corresponding
     * to the closest target (the nearest switch) with the lowest possible 
     * value (1) and the farthest switch with 3. The following method will
     * prioritize the selection, but if the selection is unavailable, it 
     * will attempt the nearest available option. If the selection is 0, it 
     * will simply attempt to cross the baseline. */

    private void leftSelector() {
    	if (destination != -1) { // -1 means don't move
    		if (destination == 0)
    			crossBaseline();
	    	else {
	    		if (gameData.charAt(destination - 1) == 'L' || isAbsolute) { 
	    			switch (destination) {
	    				case 1: nearSwitch(); break;
	    				case 2: scale(); break;
	    				default: throw new RuntimeException("Valid inputs for the auton selection are -1, 0, 1, or 2.");
	    			}
	    		} else {
	    			if (destination == 2)
	    				defaul();
	    			else {
	    				destination += 1;
	    				leftSelector();
	    			}
	    		}
	    	}
    	}
	}

    /* I'm assuming in the center we'll *always* do a switch auton. */
    private void centerSelector() {
    	/* TODO: Port over center auton from 703 code */
    	
    	if (destination != -1) { // -1 means don't move
	        /*if (gameData.charAt(0) == 'L') {
	            drive.driveForward(48);
	            drive.turnLeft(90);
	            drive.driveForward(20);
	            drive.turnRight(90);
	
	             FIXME: Hey keeler, can you make sure that if the bot
	             * runs into something it doesn't get stuck in a loop 
	             * trying to move forward
	             * ...
	             * It shouldn't, encoders detect wheel rotation instead of 
	             * distance covered, so it should still stop at the normal
	             * point 
	            drive.driveForward(90);
	
	            switchLiftAndShoot();
	            
	        } else {
	            drive.driveForward(48);
	            drive.turnRight(90);
	            drive.driveForward(20);
	            drive.turnLeft(90);
	            drive.driveForward(90);
	            
	            switchLiftAndShoot();
	        }*/
    	}
    }

    private void rightSelector() {
    	if (destination != -1) { // -1 means don't move
    		if (destination == 0)
    			crossBaseline();
	    	else {
	    		if (gameData.charAt(destination - 1) == 'R' || isAbsolute) { 
	    			switch (destination) {
	    				case 1: nearSwitch(); break;
	    				case 2: scale(); break;
	    				default: throw new RuntimeException("Valid inputs for the auton selection are -1, 0, 1, or 2.");
	    			}
	    		} else {
	    			if (destination == 2)
	    				defaul();
	    			else {
	    				destination += 1;
	    				rightSelector();
	    			}
	    		}
	    	}
    	}
    }

    private void nearSwitch() {
    	/* TODO: Port over side switch auton from 703 code */
    	
        /* Get on side of the switch ? 
        drive.driveForward(145);

         Turn in the correct direction 
        if () drive.turnRight(90); else drive.turnLeft(90);

        drive.driveForward(20);

        switchLiftAndShoot();*/
    }

    private void scale() {
    	/* TODO: Make scale auton */
    	
        /* Should be close to the scale 
        drive.driveForward(288);

        scaleLiftAndShoot();*/
    }

    private void crossBaseline() {
    	/* TODO: Port over baseline auton from 703 code */
    	
        /* Google says the baseline is 120 inches forward? 
        drive.driveForward(120);*/
    }

    private void switchLiftAndShoot() {
        lift.up();
        Utility.sleep(ELEVATOR_TIMEOUT_SWITCH);        
        lift.stop();

        /* TODO: Keeler what the hell do I set the speed & wait 
         * and are positive speeds in or out */
        intake.setSpeed(-1, -1);
        Utility.sleep(ARM_DISPENSE_TIMEOUT);
        intake.setSpeed(0, 0);
    }

    private void scaleLiftAndShoot() {
        lift.up();
        Utility.sleep(ELEVATOR_TIMEOUT_SCALE);        
        lift.stop();

        /* TODO: Keeler what the hell do I set the speed & wait 
         * and are positive speeds in or out */
        intake.setSpeed(-1, -1);
        Utility.sleep(ARM_DISPENSE_TIMEOUT);
        intake.setSpeed(0, 0);
    }


    private void defaul() {
        crossBaseline();
    }
    
    /** Publishes the sendable choosers that determine the autonomous path to the dashboard */
	public void publishDashboard() {
		// Publish starting position chooser
		positionInput.setName("Starting position");
		positionInput.addObject("Left position", "L");
		positionInput.addDefault("Center position", "C");
		positionInput.addObject("Right position", "R");
		SmartDashboard.putData(positionInput);
		
		// Publish autonomous destination type chooser
		destinationTypeInput.setName("Autonomous destination type (left & right paths only)");
		destinationTypeInput.addObject("Absolute target", true);
		destinationTypeInput.addDefault("Best option on current side", false);
		SmartDashboard.putData(destinationTypeInput);
		
		// Publish cross field chooser
		crossFieldInput.setName("Can the robot cross the field? (absolute target only)");
		crossFieldInput.addObject("Robot can cross field", true);
		crossFieldInput.addDefault("Robot cannot cross field", false);
		SmartDashboard.putData(crossFieldInput);
		
		// Publish autonomous destination chooser
		destinationInput.setName("Autonomous destination");
		destinationInput.addObject("Cross baseline", 0);
		destinationInput.addObject("Near switch", 1);
		destinationInput.addObject("Scale", 2);
		destinationInput.addDefault("Don't move", -1);
		SmartDashboard.putData(destinationInput);
		
		// Publish switch side chooser
		switchSideInput.setName("Switch side");
		switchSideInput.addObject("Left switch", "Left");
		switchSideInput.addObject("Right switch", "Right");
		switchSideInput.addDefault("Field default", "Field default");
		SmartDashboard.putData(switchSideInput);
		
		// Publish scale side chooser
		scaleSideInput.setName("Scale side");
		scaleSideInput.addObject("Left scale", "Left");
		scaleSideInput.addObject("Right scale", "Right");
		scaleSideInput.addDefault("Field default", "Field default");
		SmartDashboard.putData(scaleSideInput);
		
		// Publish number of cubes chooser
		numOfCubesInput.setName("Number of cubes (center path only)");
		numOfCubesInput.addDefault("1", 1);
		numOfCubesInput.addObject("2", 2);
		numOfCubesInput.addObject("3", 3);
		SmartDashboard.putData(numOfCubesInput);
		
		System.out.println("Dashboard data published!");
		
	}
	
	public void readFromDashboard() {
		// Find starting position
		if (positionInput.getSelected() != null)
			startingPos = positionInput.getSelected();
		
		// Find destination type
		if (destinationTypeInput.getSelected() != null)
			isAbsolute = destinationTypeInput.getSelected();
		
		// Find cross field instruction
		if (crossFieldInput.getSelected() != null)
			crossField = crossFieldInput.getSelected();
		
		// Find destination
		if (destinationInput.getSelected() != null)
			destination = destinationInput.getSelected();
		
		// Find game data
		if (DriverStation.getInstance().getGameSpecificMessage().length() > 0)
			gameData = DriverStation.getInstance().getGameSpecificMessage();
		else
			gameData = "LLL";
		
		// Find switch side
		if (switchSideInput.getSelected() != null && !switchSideInput.getSelected().equals("Field default"))
			gameData = switchSideInput.getSelected().charAt(0) + gameData.substring(1);
		
		// Find scale side
		if (scaleSideInput.getSelected() != null && !scaleSideInput.getSelected().equals("Field default"))
			gameData = gameData.substring(0, 0) + scaleSideInput.getSelected().charAt(0) + gameData.substring(2);
		
		// Find number of cubes
		if (numOfCubesInput.getSelected() != null)
			numOfCubes = numOfCubesInput.getSelected();
		
		System.out.println("Current position: " + startingPos);
		System.out.println("Absolute destination: " + isAbsolute);
		System.out.println("Destination: " + destination);
		System.out.println("Game data: " + gameData);
	}
}