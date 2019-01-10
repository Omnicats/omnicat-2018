package org.usfirst.frc.team1452.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import mechanism.drive.PIDMecanumDrive;
import mechanism.sensor.CubeDetector;
import util.motor.basic.BasicMotor;
import util.motor.basic.PWMTalon;
import util.motor.cluster.MotorCluster;
import util.motor.drive.MecanumDrive;
import util.sensor.analog.gyro.Gyro;

public class Aladdin extends IterativeRobot {
	PIDMecanumDrive drive;
	Joystick j;
	Gyro gyro;
	CubeDetector detector;
	MotorCluster undulator;
	PWMTalon frontLeft;
	PWMTalon backRight;
	boolean undulate = false;
	boolean bumperControl = false;
	double strafeSpeedBumpers = 0;
	
	public void robotInit() {
		undulator = new MotorCluster(PWMTalon.motorsFromInt(4, 5, 6, 9));
		frontLeft = new PWMTalon(7, false);
		backRight = new PWMTalon(8, false);
		drive = new PIDMecanumDrive(0.6, 0.01, 0.11, new MecanumDrive(0.03, PWMTalon.motorsFromInt(1, 0, 3, 2)));
		drive.getDrive().setInverted(3);
		gyro = new Gyro(0);
		gyro.getGyro().calibrate();
		j = new Joystick(0);
	}
	
	public void robotPeriodic() {
		SmartDashboard.putNumber("Gyro", gyro.getAngle());
		SmartDashboard.putNumber("Gyro rate", gyro.getRate());
	}

	public void autonomousInit() {
		
	}

	public void autonomousPeriodic() {
		
	}

	public void teleopInit() {

	}
	
	public void teleopPeriodic() {
		//drive.update(j.getX(), j.getY(), j.getThrottle(), gyro.getAngle(), detector.getError(), j.getRawButton(5));
		if(bumperControl) {
			drive.update(strafeSpeedBumpers, .3*j.getY(), .5*j.getZ()*Math.abs(j.getZ()), 0, 0, false);
		}
		else {
			drive.update(.7*j.getX()*Math.abs(j.getX()), .3*j.getY(), .5*j.getZ()*Math.abs(j.getZ()), 0, 0, false);
		}
		
		if(j.getRawButtonPressed(7)) {
			bumperControl = !bumperControl;
		}
		if(j.getRawButton(5)) {
			strafeSpeedBumpers = -0.4;
		}
		else if(j.getRawButton(6)) {
			strafeSpeedBumpers = 0.4;
		}
		else {
			strafeSpeedBumpers = 0;
		}
		
		if(undulate) {
			undulator.rampTo(.14);
			frontLeft.rampTo(0.11);
			backRight.rampTo(0.11);
		}
		else {
			undulator.rampTo(0);
			frontLeft.rampTo(0);
			backRight.rampTo(0);
		}
		if(j.getRawButtonPressed(8)) {
			undulate = !undulate;
		}
		
	}

	public void testPeriodic() {
		
	}
	
	public void disabledInit() {
		
	}
	
	public void disabledPeriodic() {

	}
}