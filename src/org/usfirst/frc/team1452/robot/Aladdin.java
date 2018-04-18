package org.usfirst.frc.team1452.robot;

import edu.wpi.first.wpilibj.*;
import mechanism.drive.PIDMecanumDrive;
import util.motor.basic.PWMTalon;
import util.motor.drive.MecanumDrive;

public class Aladdin extends IterativeRobot {
	PIDMecanumDrive drive;
	
	public void robotInit() {
		drive = new PIDMecanumDrive(0.6, 0.01, 0.11, new MecanumDrive(0.01, PWMTalon.motorsFromInt(0, 1, 2, 3)));
	}

	public void autonomousInit() {
		
	}

	public void autonomousPeriodic() {
		
	}

	public void teleopInit() {

	}
	
	public void teleopPeriodic() {
	}

	public void testPeriodic() {
		
	}
	
	public void disabledInit() {
	}
	
	public void disabledPeriodic() {

	}
}