package org.usfirst.frc.team703.robot.subsystems;

public class AutonHandler {
    private final DriveTrain drive;
    private final Elevator lift;
    private final Arms intake;

    /* My selectors iterate downwards from the chosen selection to the
     * next nearest in case the color is not in our favor. To instead make
     * it absolute, set this variable to true & put something in the "defaul()" method */
    private boolean isAbsolute;

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
                    case 1:nearSwitch(false);
                    case 2:scale();
                    case 3:farSwitch(false);
                    default: throw new RuntimeException("Valid inputs for the selection are 0, 1, 2, or 3.");
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
        sleep(500);        
        lift.up(false);

        /* TODO: Keeler what the hell do I set the speed & wait 
         * and are positive speeds in or out */
        intake.setSpeed(1, 1);
        sleep(1000);
        intake.setSpeed(0, 0);
    }

    private void scaleLiftAndShoot() {
        /* TODO: Figure out how Keeler's code handles the lifting & put in? */
        lift.up(true);
        sleep(1000);        
        lift.up(false);

        /* TODO: Keeler what the hell do I set the speed & wait 
         * and are positive speeds in or out */
        intake.setSpeed(1, 1);
        sleep(1000);
        intake.setSpeed(0, 0);
    }

    private void sleep(long ms) {
        /* Just a wrapper for Thread.sleep so I can ignore interruptedexceptions */
        try {
            Thread.sleep(ms);
        } catch (InterruptedException i) {

        }
    }

    private void defaul() {
        crossBaseline();
    }
}