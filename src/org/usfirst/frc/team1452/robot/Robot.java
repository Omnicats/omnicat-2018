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
import util.controlSystem.PID.PID;
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
	final double AUTO_SPEED = 0.6;
	final double AUTO_TURN = 0.5;
	final double DEGREES_TO_INCHES = 2/9;
	SendableChooser<Side> chooser = new SendableChooser<>();
	Joystick driveJ = new Joystick(0);
	Joystick mechJ = new Joystick(1);
	PIDDrive drive;
	PID driveStraightPID;
	PID turnPID;
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
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Center", Side.CENTER);
		chooser.addObject("Left", Side.LEFT);
		chooser.addObject("Right", Side.RIGHT);
		SmartDashboard.putData("Our side", chooser);
		drive = new PIDDrive(0.6, 0.01, 0.11, new TankDrive(0.2, CANTalon.motorsFromInt(2, 3, 4, 5)));
		//testDrive = new TankDrive(0.2, CANTalon.motorsFromInt(2, 3, 4, 5));
		drive.setInvertedMotor(2, 3);
		detector = new CubeDetector(driveJ);
		mandibles = new Mandibles(0.2, new CANTalon[] {new CANTalon(0, false), new CANTalon(1, true)});
		conveyor = new Conveyor(0.2, 0.2, new CANTalon[] {new CANTalon(7, true), new CANTalon(6, false), new CANTalon(11, true)});
		driveStraightPID = new PID("Drive Straight", 0.6, 0.01, 0.11);
		turnPID = new PID("Turn", 0.6, 0.01, 0.11);
		leftEncoder = new DigitalEncoder(2, 3);
		leftEncoder.setRotationsPerInch(-13.2629);
		rightEncoder = new DigitalEncoder(0, 1);
		rightEncoder.setRotationsPerInch(12.7324);
		if(driveMode.equals(Mode.JAMIE_MODE)) {
			DriverStation.reportWarning("Jamie Mode is enabled. You should be very careful with your inputs, as "
					+ "small touches may cause the robot to move wildly.", false);

		}
	}

	@Override
	public void autonomousInit() {
		String gameData = DriverStation.getInstance().getGameSpecificMessage();
		drive.getDrive().setDriveReversed(true);
		autoState = 0;
		turnError = 0;
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
		ourSide = chooser.getSelected();
		//******************************************************************************************************************************
		//change this every round
		//ourSide = Side.RIGHT;
		//******************************************************************************************************************************
		
		initialTime = System.currentTimeMillis();
	}

	/**
	 * This function is called periodically during autonomous
	 */ 
	@Override
	public void autonomousPeriodic() {
		SmartDashboard.putNumber("left encoder", leftEncoder.getInches());
		SmartDashboard.putNumber("right encoder", rightEncoder.getInches());
		if(ourSide.equals(Side.LEFT) && switchSide.equals(Side.LEFT)) {
			leftStraightAuto();
		}
		else if(ourSide.equals(Side.LEFT) && switchSide.equals(Side.RIGHT)) {
			leftAcrossAuto();
		}
		else if(ourSide.equals(Side.RIGHT) && switchSide.equals(Side.RIGHT)) {
			rightStraightAuto();
		}
		else if(ourSide.equals(Side.RIGHT) && switchSide.equals(Side.LEFT)) {
			rightAcrossAuto();
		}
		else if(ourSide.equals(Side.CENTER) && switchSide.equals(Side.LEFT)) {
			centerLeftAuto();
		}
		else if(ourSide.equals(Side.CENTER) && switchSide.equals(Side.RIGHT)) {
			centerRightAuto();
		}
		else {
			driveStraightAuto();
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
	
	public void leftStraightAuto() {
		if(autoState == 0) {
			drive.update(0.7 - (leftEncoder.getInches() - rightEncoder.getInches())/15 - leftEncoder.getInches()/1000, 0.7 - (rightEncoder.getInches() - leftEncoder.getInches())/15 - rightEncoder.getInches()/1000, 0, false);
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
			if(leftEncoder.getInches() > 20+turnError || rightEncoder.getInches() < -20-turnError) {
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
	
	public void leftAcrossAuto() {
		
	}
	
	public void rightStraightAuto() {
		if(autoState == 0) {
			drive.update(0.7 - (leftEncoder.getInches() - rightEncoder.getInches())/15 - leftEncoder.getInches()/1000, 0.7 - (rightEncoder.getInches() - leftEncoder.getInches())/15 - rightEncoder.getInches()/1000, 0, false);
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
			if(leftEncoder.getInches() < -20-turnError || rightEncoder.getInches() > 20+turnError) {
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
	
	public void rightAcrossAuto() {
		
	}
	
	public void centerLeftAuto() {
		if(autoState == 0) {
			
		}
	}
	
	public void centerRightAuto() {
		if(System.currentTimeMillis() - initialTime < 3000) {
			drive.update(0.7, 0.7, 0, false);
		}
		else if(System.currentTimeMillis() - initialTime < 6000) {
			drive.update(0, 0, 0, false);
			conveyor.rampTo(-0.6);
		}
		else {
			conveyor.rampTo(0);
		}
	}
	
	public void driveStraightAuto() {
		if(System.currentTimeMillis() - initialTime < 3000) {
			drive.update(0.7, 0.7, 0, false);
		}
		else {
			conveyor.rampTo(0);
		}
	}
	
	public boolean driveStraightInches(double inches) {
		if((leftEncoder.getInches() + rightEncoder.getInches())/2 >= inches) {
			driveStraightPID.reset();
			return true;
		}
		else {
			drive.arcadeUpdate(AUTO_SPEED, driveStraightPID.update(rightEncoder.getInches() - leftEncoder.getInches()));
			return false;
		}
	}
	
	public boolean turnDegrees(double degrees) {
		if(degrees >= 0) {
			if((leftEncoder.getInches() - rightEncoder.getInches())/2 >= degrees * DEGREES_TO_INCHES) {
				turnPID.reset();
				return true;
			}
			else {
				double error = turnPID.update(leftEncoder.getInches() + rightEncoder.getInches());
				drive.update(AUTO_TURN - error, -AUTO_TURN - error, 0, false);
				return false;
			}
		}
		else {
			if((leftEncoder.getInches() - rightEncoder.getInches())/2 <= degrees * DEGREES_TO_INCHES) {
				turnPID.reset();
				return true;
			}
			else {
				double error = turnPID.update(leftEncoder.getInches() + rightEncoder.getInches());
				drive.update(-AUTO_TURN - error, AUTO_TURN - error, 0, false);
				return false;
			}
		}
	}
}
