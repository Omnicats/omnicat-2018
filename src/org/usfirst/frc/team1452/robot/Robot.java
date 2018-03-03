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
import util.sensor.analog.gyro.Gyro;
import util.sensor.analog.rangefinder.Rangefinder;
import util.sensor.digital.DigitalEncoder;

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
	TankDrive testDrive;
	Gyro gyro;
	Rangefinder rangefinder;
	CubeDetector detector;
	Mandibles mandibles;
	Conveyor conveyor;
	DigitalEncoder leftEncoder;
	DigitalEncoder rightEncoder;
	double turnError;
	public enum Side{
		LEFT,
		CENTER,
		RIGHT;
	}
	public enum Mode{
		JAMIE_MODE,
		NORMAL_PERSON_MODE;
	}
	Mode driveMode = Mode.NORMAL_PERSON_MODE;
	Side ourSide;
	Side switchSide;
	long initialTime;
	double autoState;
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
		//testDrive = new TankDrive(0.2, CANTalon.motorsFromInt(2, 3, 4, 5));
		drive.setInvertedMotor(2, 3);
		detector = new CubeDetector(driveJ);
		mandibles = new Mandibles(0.2, new CANTalon[] {new CANTalon(0, false), new CANTalon(1, true)});
		conveyor = new Conveyor(0.2, 0.2, new CANTalon[] {new CANTalon(7, true), new CANTalon(6, false), new CANTalon(11, true)});
		gyro = new Gyro(0);
		gyro.getGyro().reset();
		gyro.getGyro().calibrate();
		leftEncoder = new DigitalEncoder(2, 3);
		leftEncoder.setRotationsPerInch(-13.2629);
		rightEncoder = new DigitalEncoder(0, 1);
		rightEncoder.setRotationsPerInch(12.7324);
		if(driveMode.equals(Mode.JAMIE_MODE)) {
			DriverStation.reportWarning("Jamie Mode is enabled. You should be very careful with your inputs, as "
					+ "small touches may cause the robot to move wildly.", false);

		}
		//rangefinder = new Rangefinder(1);
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
		String gameData = DriverStation.getInstance().getGameSpecificMessage();
		drive.getDrive().setDriveReversed(true);
		autoState = 0;
		gyro.getGyro().reset();
		leftEncoder.getEncoder().reset();
		rightEncoder.getEncoder().reset();
		if(driveMode.equals(Mode.JAMIE_MODE)) {
			DriverStation.reportWarning("Jamie Mode is enabled. You should be very careful with your inputs, as "
					+ "small touches may cause the robot to move wildly.", false);

		}
		if(gameData.length() > 0) {
			if(gameData.charAt(0) == 'L') {
				switchSide = Side.LEFT;
			}
			else {
				switchSide = Side.RIGHT;
			}
		}
		
		//******************************************************************************************************************************
		//change this every round
		ourSide = Side.RIGHT;
		//******************************************************************************************************************************
		
		initialTime = System.currentTimeMillis();
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
		SmartDashboard.putNumber("left encoder", leftEncoder.getInches());
		SmartDashboard.putNumber("right encoder", rightEncoder.getInches());
		if(ourSide.equals(Side.LEFT)) {
			if(autoState == 0) {
				drive.update(0.7 - (leftEncoder.getInches() - rightEncoder.getInches())/15 - leftEncoder.getInches()/500, 0.7 - (rightEncoder.getInches() - leftEncoder.getInches())/15 - rightEncoder.getInches()/500, 0, false);
				if(leftEncoder.getInches() > 148 || rightEncoder.getInches() > 148) {
					autoState = 0.5;
					initialTime = System.currentTimeMillis();
				}
			}
			else if(autoState == 0.5) {
				drive.update(0, 0, 0, false);
				if(System.currentTimeMillis() - initialTime > 500) {
					turnError = (rightEncoder.getInches() - leftEncoder.getInches())/2;
					autoState = 1;
					leftEncoder.getEncoder().reset();
					rightEncoder.getEncoder().reset();
				}
			}
			else if(autoState == 1) {
				//0.55
				drive.update(0.55 - (leftEncoder.getInches() + rightEncoder.getInches())/25, -0.55 - (rightEncoder.getInches() + leftEncoder.getInches())/25, 0, false);
				if(leftEncoder.getInches() > 19+turnError || rightEncoder.getInches() < -19-turnError) {
					autoState = 1.5;
					initialTime = System.currentTimeMillis();
				}
			}
			else if(autoState == 1.5) {
				drive.update(0, 0, 0, false);
				if(System.currentTimeMillis() - initialTime > 500) {
					leftEncoder.getEncoder().reset();
					rightEncoder.getEncoder().reset();
					autoState = 2;
				}
			}
			else if(autoState == 2) {
				//0.7
				drive.update(0.5, 0.5, 0, false);
				if(System.currentTimeMillis() - initialTime > 3000) {
					autoState = 2.5;
					initialTime = System.currentTimeMillis();
				}
			}
			else if(autoState == 2.5) {
				drive.update(0, 0, 0, false);
				if(System.currentTimeMillis() - initialTime > 500) {
					initialTime = System.currentTimeMillis();
					autoState = 3;
				}
			}
			else if(autoState == 3) {
				drive.update(0, 0, 0, false);
				if(switchSide.equals(Side.RIGHT)){
					conveyor.rampTo(-0.6);
				}
				if(System.currentTimeMillis() - initialTime > 3000) {
					autoState = 4;
				}
			}
			else if(autoState == 4) {
				conveyor.rampTo(0);
				drive.update(0, 0, 0, false);
			}
		}
		else if(ourSide.equals(Side.RIGHT)) {
			if(autoState == 0) {
				drive.update(0.7 - (leftEncoder.getInches() - rightEncoder.getInches())/15 - leftEncoder.getInches()/500, 0.7 - (rightEncoder.getInches() - leftEncoder.getInches())/15 - rightEncoder.getInches()/500, 0, false);
				if(leftEncoder.getInches() > 148 || rightEncoder.getInches() > 148) {
					autoState = 0.5;
					initialTime = System.currentTimeMillis();
				}
			}
			else if(autoState == 0.5) {
				drive.update(0, 0, 0, false);
				if(System.currentTimeMillis() - initialTime > 500) {
					turnError = (leftEncoder.getInches() - rightEncoder.getInches())/2;
					autoState = 1;
					leftEncoder.getEncoder().reset();
					rightEncoder.getEncoder().reset();
				}
			}
			else if(autoState == 1) {
				//0.55
				drive.update(-0.55 - (leftEncoder.getInches() + rightEncoder.getInches())/25, 0.55 - (rightEncoder.getInches() + leftEncoder.getInches())/25, 0, false);
				if(leftEncoder.getInches() < -19-turnError || rightEncoder.getInches() > 19+turnError) {
					autoState = 1.5;
					initialTime = System.currentTimeMillis();
				}
			}
			else if(autoState == 1.5) {
				drive.update(0, 0, 0, false);
				if(System.currentTimeMillis() - initialTime > 500) {
					leftEncoder.getEncoder().reset();
					rightEncoder.getEncoder().reset();
					autoState = 2;
				}
			}
			else if(autoState == 2) {
				//0.7
				drive.update(0.5, 0.5, 0, false);
				if(System.currentTimeMillis() - initialTime > 3000) {
					autoState = 2.5;
					initialTime = System.currentTimeMillis();
				}
			}
			else if(autoState == 2.5) {
				drive.update(0, 0, 0, false);
				if(System.currentTimeMillis() - initialTime > 500) {
					initialTime = System.currentTimeMillis();
					autoState = 3;
				}
			}
			else if(autoState == 3) {
				drive.update(0, 0, 0, false);
				if(switchSide.equals(Side.RIGHT)){
					conveyor.rampTo(-0.6);
				}
				if(System.currentTimeMillis() - initialTime > 3000) {
					autoState = 4;
				}
			}
			else if(autoState == 4) {
				conveyor.rampTo(0);
				drive.update(0, 0, 0, false);
			}
		}
		else {
			if(System.currentTimeMillis() - initialTime < 3000) {
				drive.update(0.7, 0.7, 0, false);
			}
			else if(System.currentTimeMillis() - initialTime < 6000) {
				drive.update(0, 0, 0, false);
				if(switchSide.equals(Side.RIGHT)) {
					conveyor.rampTo(-0.6);
				}
			}
			else {
				conveyor.rampTo(0);
			}
		}
	}
	
	public void disabledInit() {
		if(driveMode.equals(Mode.JAMIE_MODE)) {
			DriverStation.reportWarning("Jamie Mode is enabled. You should be very careful with your inputs, as "
					+ "small touches may cause the robot to move wildly.", false);

		}
	}
	
	public void disabledPeriodic() {
		if(driveJ.getRawButtonPressed(5)) {
			driveMode = Mode.JAMIE_MODE;
			DriverStation.reportWarning("Jamie Mode is enabled. You should be very careful with your inputs, as "
					+ "small touches may cause the robot to move wildly.", false);
		}
		else if(driveJ.getRawButton(6)) {
			driveMode = Mode.NORMAL_PERSON_MODE;
		}
	}

	public void teleopInit() {
		drive.getDrive().setDriveReversed(false);
		if(driveMode.equals(Mode.JAMIE_MODE)) {
			DriverStation.reportWarning("Jamie Mode is enabled. You should be very careful with your inputs, as "
					+ "small touches may cause the robot to move wildly.", false);

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
		/*if(driveJ.getRawButton(1)) {
			drive.update(-driveJ.getY(), -driveJ.getThrottle(), (detector.getCenterX()-160)/160, true);
		}
		else {
			drive.update(-driveJ.getY(), -driveJ.getThrottle(), (detector.getCenterX()-160)/160, false);
		}*/
		//drive.update(driveJ.getY(), driveJ.getThrottle(), 0, false);
		if(driveMode.equals(Mode.JAMIE_MODE)) {
			drive.update(-driveJ.getY() * 0.7, -driveJ.getThrottle() * 0.7, (detector.getCenterX()-160)/160, false);
			if(Math.max(Math.abs(driveJ.getY()), Math.abs(driveJ.getThrottle())) > 0.5) {
				DriverStation.reportWarning("Be careful! You are attempting to drive the robot at a high speed. Are you sure you want to do that?", false);
			}
		}
		else {
			drive.update(-driveJ.getY(), -driveJ.getThrottle(), (detector.getCenterX()-160)/160, false);
		}
		if(driveJ.getRawButtonPressed(5)) {
			driveMode = Mode.JAMIE_MODE;
			DriverStation.reportWarning("Jamie Mode is enabled. You should be very careful with your inputs, as "
					+ "small touches may cause the robot to move wildly.", false);
		}
		else if(driveJ.getRawButton(6)) {
			driveMode = Mode.NORMAL_PERSON_MODE;
		}
		mandibles.leftRampTo(mechJ.getY()-mechJ.getX()/3);
		mandibles.rightRampTo(mechJ.getY()+mechJ.getX()/3);
		conveyor.rampTo(Math.abs(mechJ.getThrottle())*mechJ.getThrottle());
		SmartDashboard.putNumber("left encoder", leftEncoder.getInches());
		SmartDashboard.putNumber("right encoder", rightEncoder.getInches());
		SmartDashboard.putNumber("Gyro Angle", gyro.getAngle());
		SmartDashboard.putNumber("Gyro Rate", gyro.getRate());
		/*if(driveJ.getRawButtonPressed(11)) {
			drive.getDrive().reverseDrive();
		}*/
		if(driveJ.getRawButton(3)) {
			drive.getDrive().setDriveReversed(false);
		}
		else if(driveJ.getRawButton(4)) {
			drive.getDrive().setDriveReversed(true);
		}
		//testDrive.update(driveJ.getY(), driveJ.getThrottle());
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}

