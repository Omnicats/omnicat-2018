package org.usfirst.frc.team1452.robot;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.VisionThread;
import mechanism.Conveyor;
import mechanism.Mandibles;
import mechanism.drive.PIDDrive;
import mechanism.drive.RangefinderDrive;
import mechanism.sensor.CubeDetector;
import mechanism.sensor.GripPipeline;
import mechanism.sensor.RangefinderArray;
import util.motor.basic.BasicMotor;
import util.motor.basic.PWMTalon;
import util.motor.drive.ArcadeDrive;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();
	Joystick j = new Joystick(0);
	PIDDrive drive;
	CubeDetector detector;
	Mandibles mandibles;
	PWMTalon[] talons;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
		talons = PWMTalon.motorsFromInt(0, 1, 2);
		/*detector = new CubeDetector();
		drive = new PIDDrive(0.6, 0.01, 0.11, new ArcadeDrive(0.2, PWMTalon.motorsFromInt(0, 1, 2, 3)));
		drive.setInvertedMotor(0, 1, 3);*/
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
		case customAuto:
			// Put custom auto code here
			break;
		case defaultAuto:
		default:
			// Put default auto code here
			break;
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		/*if(j.getRawButton(1)) {
			drive.update(j.getY(), j.getX(), (detector.getCenterX()-160)/160, true);
		}
		else {
			drive.update(j.getY(), j.getX(), (detector.getCenterX()-160)/160, false);
		}
		if(j.getRawButtonPressed(7)) {
			drive.setKp(drive.getKp() + 0.01);
		}
		if(j.getRawButtonPressed(8)) {
			drive.setKp(drive.getKp() - 0.01);
		}
		if(j.getRawButtonPressed(9)) {
			drive.setKi(drive.getKi() + 0.01);
		}
		if(j.getRawButtonPressed(10)) {
			drive.setKi(drive.getKi() - 0.01);
		}
		if(j.getRawButtonPressed(11)) {
			drive.setKd(drive.getKd() + 0.01);
		}
		if(j.getRawButtonPressed(12)) {
			drive.setKd(drive.getKd() - 0.01);
		}
		SmartDashboard.putNumber("CenterXTest", detector.getCenterX());*/
		talons[0].rampTo(j.getY());
		talons[1].rampTo(j.getY());
		talons[2].rampTo(j.getY()/2);
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}

