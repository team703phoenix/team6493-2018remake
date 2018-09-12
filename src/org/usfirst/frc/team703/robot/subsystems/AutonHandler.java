package org.usfirst.frc.team703.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team703.robot.utilities.Utility;

public class AutonHandler {
	/* My selectors iterate downwards from the chosen selection to the
     * next nearest in case the color is not in our favor. To instead make
     * it absolute, set this variable to true & put something in the "defaul()" method */
	
	// Dashboard inputs
	private String startingPos, gameData;
	private boolean isAbsolute;
	private int destination;
	
    private final DriveTrain drive;
    private final Elevator lift;
    private final Arms intake;
    
    // Sendable choosers
 	private SendableChooser<String> positionInput = new SendableChooser<>();
 	private SendableChooser<Boolean> destinationTypeInput = new SendableChooser<>();
 	private SendableChooser<Integer> destinationInput = new SendableChooser<>();
 	private SendableChooser<String> switchSideInput = new SendableChooser<>();
 	private SendableChooser<String> scaleSideInput = new SendableChooser<>();

    public AutonHandler(DriveTrain drive, Elevator lift, Arms intake) {
        this.drive = drive;
        this.lift = lift;
        this.intake = intake;
    }

    /* Gamedata is of format LRL with respect to the relevant alliance's
     * color. This has to be explicitly specified in a driverstation not
     * connected to an FMS, so if the value seems weird, defaults to LLL */

    public void runAuton(String gameData, int selection, String position, boolean isAbsolute) {
        this.isAbsolute = isAbsolute;

        checkGameData(gameData);

        switch (position) {
            case "L":leftSelector(gameData, selection);
            case "C":centerSelector(gameData);
            case "R":rightSelector(gameData, selection);
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

    private void leftSelector(String gameData, int selection) {
        if (selection == 0) {
            crossBaseline();
        } else {
            if (gameData.charAt(selection - 1) == 'L') {
                switch (selection) {
                    case 1:nearSwitch(true);
                    case 2:scale();
                    case 3:farSwitch(true);
                    default: throw new RuntimeException("Valid inputs for the selection are 0, 1, 2, or 3.");
                }

            } else {
                /* If going for that target is not an option, check the next
                 * closest option. If the next closest option is a baseline 
                 * cross, perform it. */
                if (isAbsolute) defaul(); else leftSelector(gameData, selection);
            }
        }
    }

    /* I'm assuming in the center we'll *always* do a switch auton. */
    private void centerSelector(String gameData) {
        if (gameData.charAt(0) == 'L') {
            drive.driveForward(48);
            drive.turnLeft(90);
            drive.driveForward(20);
            drive.turnRight(90);

            /* FIXME: Hey keeler, can you make sure that if the bot
             * runs into something it doesn't get stuck in a loop 
             * trying to move forward */
            drive.driveForward(90);

            switchLiftAndShoot();
            
        } else {
            drive.driveForward(48);
            drive.turnRight(90);
            drive.driveForward(20);
            drive.turnLeft(90);
            drive.driveForward(90);
            
            switchLiftAndShoot();
        }
    }

    private void rightSelector(String gameData, int selection) {
        if (selection == 0) {
            crossBaseline();
        } else {
            if (gameData.charAt(selection - 1) == 'R') {
                switch (selection) {
                	case -1:break;
                    case 1:nearSwitch(false); break;
                    case 2:scale(); break;
                    case 3:farSwitch(false); break;
                    default: throw new RuntimeException("Valid inputs for the selection are -1, 0, 1, 2, or 3.");
                }

            } else {
                /* If going for that target is not an option, check the next
                 * closest option. If the next closest option is a baseline 
                 * cross, perform it. */
                if (isAbsolute) defaul(); else rightSelector(gameData, selection);
            }
        }
    }

    private void nearSwitch(boolean onLeft) {
        /* Get on side of the switch ? */
        drive.driveForward(145);

        /* Turn in the correct direction */
        if (onLeft) drive.turnRight(90); else drive.turnLeft(90);

        drive.driveForward(20);

        switchLiftAndShoot();
    }

    private void scale() {
        /* Should be close to the scale */
        drive.driveForward(288);

        scaleLiftAndShoot();
    }

    private void farSwitch(boolean onLeft) {
        /* Get on side of the switch ? */
        drive.driveForward(505);

        /* Turn in the correct direction */
        if (onLeft) drive.turnRight(90); else drive.turnLeft(90);

        drive.driveForward(20);

        switchLiftAndShoot();
    }

    private void crossBaseline() {
        /* Google says the baseline is 120 inches forward? */
        drive.driveForward(120);
    }

    private void switchLiftAndShoot() {
        /* TODO: Figure out how Keeler's code handles the lifting & put in? */
        lift.up(true);
        Utility.sleep(500);        
        lift.up(false);

        /* TODO: Keeler what the hell do I set the speed & wait 
         * and are positive speeds in or out */
        intake.setSpeed(1, 1);
        Utility.sleep(1000);
        intake.setSpeed(0, 0);
    }

    private void scaleLiftAndShoot() {
        /* TODO: Figure out how Keeler's code handles the lifting & put in? */
        lift.up(true);
        Utility.sleep(1000);        
        lift.up(false);

        /* TODO: Keeler what the hell do I set the speed & wait 
         * and are positive speeds in or out */
        intake.setSpeed(1, 1);
        Utility.sleep(1000);
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
		destinationTypeInput.setName("Autonomous destination type");
		destinationTypeInput.addObject("Absolute target", true);
		destinationTypeInput.addDefault("Stay on current side", false);
		SmartDashboard.putData(destinationTypeInput);
		
		// Publish autonomous destination chooser
		destinationInput.setName("Autonomous destination");
		destinationInput.addObject("Cross baseline", 0);
		destinationInput.addObject("Near switch", 1);
		destinationInput.addObject("Scale", 2);
		destinationInput.addObject("Far switch", 3);
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
		
		System.out.println("Dashboard data published!");
		
	}
	
	public void readFromDashboard() {
		// Find starting position
		if (positionInput.getSelected() != null)
			startingPos = positionInput.getSelected();
		
		// Find destination type
		if (destinationTypeInput.getSelected() != null)
			isAbsolute = destinationTypeInput.getSelected();
		
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
		
		System.out.println("Current position: " + startingPos);
		System.out.println("Absolute destination: " + isAbsolute);
		System.out.println("Destination: " + destination);
		System.out.println("Game data: " + gameData);
	}
}