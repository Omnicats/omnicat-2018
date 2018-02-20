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
import mechanism.Conveyor;
import mechanism.Mandibles;
import mechanism.drive.PIDDrive;
import mechanism.sensor.CubeDetector;
import mechanism.sensor.GripPipeline;
import mechanism.sensor.RangefinderArray;
import util.motor.basic.BasicMotor;
import util.motor.basic.CANTalon;
import util.motor.basic.PWMTalon;
import util.motor.drive.ArcadeDrive;
import util.motor.drive.TankDrive;

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
	Joystick driveJ = new Joystick(0);
	Joystick mechJ = new Joystick(1);
	PIDDrive drive;
	//ArcadeDrive drive;
	CubeDetector detector;
	Mandibles mandibles;
	Conveyor conveyor;
	/*Trajectory.Config c;
	Waypoint[] waypoints;
	Trajectory trajectory;
	Trajectory leftTrajectory;
	Trajectory rightTrajectory;
	
	int segCounter;*/
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
		//c = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 1.0/60, 1.7, 2.0, 60.0);
		drive = new PIDDrive(0.6, 0.01, 0.11, new TankDrive(0.2, CANTalon.motorsFromInt(2, 3, 4, 5)));
		drive.setInvertedMotor(0, 1);
		detector = new CubeDetector(driveJ);
		mandibles = new Mandibles(0.2, new CANTalon[] {new CANTalon(0, false), new CANTalon(1, true)});
		conveyor = new Conveyor(0.2, 0.2, new CANTalon[] {new CANTalon(7, true), new CANTalon(6, false), new CANTalon(11, true)});
		//drive = new ArcadeDrive(0.2, PWMTalon.motorsFromInt(0, 1, 2, 3));
		//drive.setInverted(0, 1);
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
		/*segCounter = 0;
		waypoints = new Waypoint[] {
				new Waypoint(0, 2, Math.toRadians(45)),
				new Waypoint(2, 2, Math.toRadians(90))
		};
		trajectory = Pathfinder.generate(waypoints, c);
		double wheelbaseWidth = 0.65405;
		TankModifier m = new TankModifier(trajectory);
		m.modify(wheelbaseWidth);
		
		rightTrajectory = m.getRightTrajectory();
		leftTrajectory = m.getLeftTrajectory();
		*/
	}

	/**
	 * This function is called periodically during autonomous
	 */ 
	@Override
	public void autonomousPeriodic() {
		
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
		if(driveJ.getRawButton(1)) {
			drive.update(driveJ.getY(), driveJ.getThrottle(), (detector.getCenterX()-160)/160, true);
		}
		else {
			drive.update(driveJ.getY(), driveJ.getThrottle(), (detector.getCenterX()-160)/160, false);
		}
		mandibles.leftRampTo(mechJ.getY()-mechJ.getX()/3);
		mandibles.rightRampTo(mechJ.getY()+mechJ.getX()/3);
		conveyor.rampTo(mechJ.getThrottle());

		/*if(driveJ.getRawButtonPressed(11)) {
			drive.getDrive().reverseDrive();
		}*/
		if(driveJ.getRawButtonPressed(3)) {
			drive.getDrive().setDriveReversed(false);
		}
		if(driveJ.getRawButtonPressed(4)) {
			drive.getDrive().setDriveReversed(true);
		}
		//drive.update(driveJ.getY(), driveJ.getX());
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}

