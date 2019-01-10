package org.usfirst.frc.team1452.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Robot extends IterativeRobot {
	DoubleSolenoid d = new DoubleSolenoid(0, 1);
	Joystick j = new Joystick(0);
	
	
	public void robotInit() {
	}

	public void autonomousInit() {
		
	}

	public void autonomousPeriodic() {
		
	}

	public void teleopInit() {

	}
	
	public void teleopPeriodic() {
		if(j.getRawButtonPressed(2)) {
			if(d.get() == Value.kForward)
				d.set(Value.kReverse);
			else
				d.set(Value.kForward);
		}
	}

	public void testPeriodic() {
		
	}
	
	public void disabledInit() {
	}
	
	public void disabledPeriodic() {

	}
}