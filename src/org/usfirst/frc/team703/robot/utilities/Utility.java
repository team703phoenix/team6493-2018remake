package org.usfirst.frc.team703.robot.utilities;

public class Utility {
	
	/** Sleeps for a given amount of milliseconds */
	public static void sleep(int timeInMs) {
		try {
			Thread.sleep(timeInMs);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}
	
	/** Toggles a value if a button is pressed (toggle[0] should be set to the default value, 
	 * toggle[1] should always be set to true), returns true if the value was just toggled */ 
	public static boolean toggle(boolean[] toggle, boolean button) {
		// toggle[0] is the toggled value
		// toggle[1] indicates if toggle[0] can be toggled (prevents rapid toggling)
		// toggle[1] becomes false when the button is pressed, and becomes true when the button is released
		// toggle[0] can only be toggled if toggle[1] is true
		
		if (button && toggle[1]) {
			toggle[1] = false;
			toggle[0] = !toggle[0];
			return true;
		} else if (!button)
			toggle[1] = true;
		return false;
		
	}
	
}