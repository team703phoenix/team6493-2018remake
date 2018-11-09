package org.usfirst.frc.team703.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team703.robot.utilities.Utility;
import org.usfirst.frc.team703.robot.Robot;

public class AutonHandler {
	/* My selectors iterate downwards from the chosen selection to the
     * next nearest in case the color is not in our favor. To instead make
     * it absolute, set this variable to true & put something in the "defaul()" method */
	
	// Constants
	public static final int ELEVATOR_TIMEOUT_SWITCH = 1100;
	public static final int ELEVATOR_TIMEOUT_SCALE = 2750;
	
	public static final int ARM_DISPENSE_TIMEOUT = 700;
	public static final double ARM_SHOOT_SPEED = 0.9;
	public static final double ARM_SHOOT_SPEED_SCALE = 1.0;
	
	// Dashboard inputs
	private String startingPos, gameData;
	private boolean isAbsolute, crossField, prioritizeSwitch;
	private int destination, numOfCubes;
	
    private final DriveTrain drive;
    private final Elevator lift;
    private final Arms intake;
    private final Vision vision;
    private final Robot robot;
    
    // Sendable choosers
 	private SendableChooser<String> positionInput = new SendableChooser<>();
 	private SendableChooser<Boolean> destinationTypeInput = new SendableChooser<>();
 	private SendableChooser<Boolean> crossFieldInput = new SendableChooser<>();
 	private SendableChooser<Integer> destinationInput = new SendableChooser<>();
 	private SendableChooser<Boolean> priorityInput = new SendableChooser<>();
 	private SendableChooser<String> switchSideInput = new SendableChooser<>();
 	private SendableChooser<String> scaleSideInput = new SendableChooser<>();
 	private SendableChooser<Integer> numOfCubesInput = new SendableChooser<>();

    public AutonHandler(DriveTrain drive, Elevator lift, Arms intake, Vision vision, Robot robot) {
        this.drive = drive;
        this.lift = lift;
        this.intake = intake;
        this.vision = vision;
        this.robot = robot;
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
            default: throw new RuntimeException("Somehow you set the position to an invalid value. It can only be L, C, or R.");
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
	    			if ((destination == 2 && prioritizeSwitch) || (destination == 1 && !prioritizeSwitch))
	    				defaul();
	    			else {
	    				if (prioritizeSwitch)
	    					destination += 1;
	    				else
	    					destination -= 1;
	    				leftSelector();
	    			}
	    		}
	    	}
    	}
	}

    private void centerSelector() {	
    	if (destination != -1) { // -1 means don't move
			drive.driveForward(20);
			vision.setTargetPipeline();
			drive.turn((switchIsLeft()) ? -45 : 45);
			drive.driveForward((switchIsLeft()) ? 65 : 59);
			drive.turn((switchIsLeft()) ? 45 : -45);

			vision.driveTowardTarget(!switchIsLeft());
			switchLiftAndShoot();
			vision.setCubePipeline();
			
			for (int i = 2; i <= numOfCubes; i++) {
				switchLowerLift();
				
				drive.driveBackward(40);
				intake.down();
				drive.turn((switchIsLeft()) ? 45 : -45);
				vision.pickupCube();
				
				drive.driveBackward(30);
				drive.turn((switchIsLeft() ? -30 : 30));
				vision.driveTowardTarget(switchIsLeft());
				
				switchLiftAndShoot();
			}
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
	    			if ((destination == 2 && prioritizeSwitch) || (destination == 1 && !prioritizeSwitch))
	    				defaul();
	    			else {
	    				if (prioritizeSwitch)
	    					destination += 1;
	    				else
	    					destination -= 1;
	    				rightSelector();
	    			}
	    		}
	    	}
    	}
    }

    private void nearSwitch() {
    	if (switchIsAdjacent() || crossField) {
	    	drive.driveForward(switchIsAdjacent() ? 148 : 210);
	    	drive.turn(switchIsLeft() ? 90 : -90);
			drive.driveForward(switchIsAdjacent() ? 16 : 180);
		
			if (!switchIsAdjacent()) {
				drive.turn(switchIsLeft() ? 90 : -90);
				drive.driveForward(62);
				drive.turn(switchIsLeft() ? 90 : -90);
				drive.driveForward(16);
			}
			
			switchLiftAndShoot();
    	} else {
    		defaul();
    	}
    }

    private void scale() {   	
    	if (scaleIsAdjacent()) { // No cross field option for scale
    		drive.driveForward(305);
    		drive.turn(scaleIsLeft() ? 90 : -90);
    		//drive.driveForward(13);
    		
    		scaleLiftAndShoot();
    	} else {
    		crossBaseline();
    	}
    	
        /* Should be close to the scale 
        drive.driveForward(288);

        scaleLiftAndShoot();*/
    }

    private void crossBaseline() {
        drive.driveForward(120);
    }

    public void switchLiftAndShoot() {
    	if (!robot.haltAutonomous()) {
	        lift.up();
	        Utility.sleep(ELEVATOR_TIMEOUT_SWITCH);        
	        lift.stop();
	
	        intake.setSpeed(-ARM_SHOOT_SPEED, -ARM_SHOOT_SPEED);
	        Utility.sleep(ARM_DISPENSE_TIMEOUT);
	        intake.setSpeed(0, 0);
    	}
    }
    
    public void switchLowerLift() {
    	if (!robot.haltAutonomous()) {
	    	lift.goToBottom();
    	}
    }

    public void scaleLiftAndShoot() {
    	System.out.println("START SHOOT (maybe?)");
    	System.out.println("Is autonomous? " + robot.isAutonomous() + " | Is enabled? " + robot.isEnabled());
		System.out.println("START SHOOT (fr)");
        lift.up();
        Utility.sleep(ELEVATOR_TIMEOUT_SCALE);        
        lift.stop();
        
        intake.setSpeed(-ARM_SHOOT_SPEED_SCALE, -ARM_SHOOT_SPEED_SCALE);
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
		
		// Publish destination priority chooser
		priorityInput.setName("Destination priority (best option only)");
		priorityInput.addDefault("Switch", true);
		priorityInput.addObject("Scale", false);
		SmartDashboard.putData(priorityInput);
		
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
		switchSideInput.setName("Switch side (for testing only)");
		switchSideInput.addObject("Left switch", "Left");
		switchSideInput.addObject("Right switch", "Right");
		switchSideInput.addDefault("Field default", "Field default");
		SmartDashboard.putData(switchSideInput);
		
		// Publish scale side chooser
		scaleSideInput.setName("Scale side (for testing only)");
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
		
		// Find destination priority
		if (priorityInput.getSelected() != null)
			prioritizeSwitch = priorityInput.getSelected();
		
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
			gameData = gameData.substring(0, 1) + scaleSideInput.getSelected().charAt(0) + gameData.substring(2);
		
		// Find number of cubes
		if (numOfCubesInput.getSelected() != null)
			numOfCubes = numOfCubesInput.getSelected();
		
		System.out.println("Current position: " + startingPos);
		System.out.println("Absolute destination: " + isAbsolute);
		System.out.println("Destination: " + destination);
		System.out.println("Game data: " + gameData);
	}
	
	public boolean switchIsAdjacent() {
		return gameData.charAt(0) == startingPos.charAt(0);
	}
	
	public boolean switchIsLeft() {
		return gameData.charAt(0) == 'L';
	}
	
	public boolean scaleIsAdjacent() {
		return gameData.charAt(1) == startingPos.charAt(0);
	}
	
	public boolean scaleIsLeft() {
		return gameData.charAt(1) == 'L';
	}
}