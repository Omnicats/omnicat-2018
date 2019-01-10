package org.usfirst.frc.team1452.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import mechanism.Conveyor;
import mechanism.Mandibles;
import mechanism.TapeLift;
import mechanism.drive.PIDDrive;
import mechanism.sensor.CubeDetector;
import util.controlSystem.PID.PID;
import util.motor.basic.CANTalon;
import util.motor.drive.TankDrive;
import util.sensor.analog.gyro.Gyro;
import util.sensor.analog.rangefinder.Rangefinder;
import util.sensor.digital.DigitalEncoder;
import util.sensor.digital.TouchSensor;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class PowerUpRobot extends IterativeRobot {
	final double AUTO_SPEED = 0.6;
	final double AUTO_TURN = 0.7;
	final double DEGREES_TO_INCHES = 1.8/9.0;
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
	TapeLift lift;
	DigitalEncoder leftEncoder;
	DigitalEncoder rightEncoder;
	TouchSensor safety;
	boolean deployed;
	Double turnError = 0.0;
	public enum Side{
		LEFT,
		CENTER,
		RIGHT,
		STRAIGHT;
	}
	public enum Mode{
		JAMIE_MODE,
		NORMAL_PERSON_MODE;
	}
	Mode driveMode = Mode.NORMAL_PERSON_MODE;
	public enum LiftMode{
		STANDARD,
		HOOK,
		CLIMB
	}
	LiftMode liftMode = LiftMode.STANDARD;
	Side ourSide;
	Side switchSide;
	long initialTime;
	long autoTime;
	double autoState;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any0000 initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Center", Side.CENTER);
		chooser.addObject("Left", Side.LEFT);
		chooser.addObject("Right", Side.RIGHT);
		chooser.addObject("Straight", Side.STRAIGHT);
		SmartDashboard.putData("Our side", chooser);
		drive = new PIDDrive(0.6, 0.01, 0.11, new TankDrive(0.2, CANTalon.motorsFromInt(11, 10, 0, 1)));
		//testDrive = new TankDrive(0.2, CANTalon.motorsFromInt(2, 3, 4, 5));
		drive.setInvertedMotor(2, 3);
		detector = new CubeDetector(driveJ);
		mandibles = new Mandibles(5, 4, new CANTalon[] {new CANTalon(5, false), new CANTalon(4, true)});
		conveyor = new Conveyor(0.2, 0.2, new CANTalon[] {new CANTalon(2, true), new CANTalon(3, false), new CANTalon(6, true)});
		lift = new TapeLift(0.2, new CANTalon[] {new CANTalon(7, false), new CANTalon(8, false), new CANTalon(9, false)});
		driveStraightPID = new PID("Drive Straight", 0.6, 0.01, 0.11);
		turnPID = new PID("Turn", 0.6, 0.01, 0.11);
		leftEncoder = new DigitalEncoder(0, 1);
		leftEncoder.setRotationsPerInch(-13.2629);
		rightEncoder = new DigitalEncoder(2, 3);
		rightEncoder.setRotationsPerInch(12.7324);
		safety = new TouchSensor(9);
		mandibles.getDabEncoder().setRotationsPerDegree(1);
		mandibles.getDabEncoder().getEncoder().reset();
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
		turnError = 0.0;
		leftEncoder.getEncoder().reset();
		rightEncoder.getEncoder().reset();
		mandibles.getDabEncoder().getEncoder().reset();
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
		autoTime = System.currentTimeMillis();
		deployed = false;
	}

	/**
	 * This function is called periodically during autonomous
	 */ 
	@Override
	public void autonomousPeriodic() {
		SmartDashboard.putNumber("left encoder", leftEncoder.getInches());
		SmartDashboard.putNumber("right encoder", rightEncoder.getInches());
		/*if(ourSide.equals(Side.LEFT) && switchSide.equals(Side.LEFT)) {
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
		}*/
		if(ourSide.equals(Side.LEFT) && switchSide.equals(Side.LEFT)) {
			leftStraightAuto();
			//sketchyLeftAuto();
		}
		else if(ourSide.equals(Side.RIGHT) && switchSide.equals(Side.RIGHT)) {
			rightStraightAuto();
		}
		else if(ourSide.equals(Side.CENTER) && switchSide.equals(Side.RIGHT)) {
			centerRightAuto();
		}
		else if(ourSide.equals(Side.CENTER) && switchSide.equals(Side.LEFT)) {
			centerLeftAuto();
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
		liftMode = LiftMode.STANDARD;
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
		//if(!liftMode.equals(LiftMode.CLIMB)) {
			if(driveMode.equals(Mode.JAMIE_MODE)) {
				if(driveJ.getRawButton(6)) {
					drive.update(-driveJ.getY() * 0.7, -driveJ.getThrottle() * 0.7, (detector.getCenterX()-160)/160, true);
				}
				else {
					drive.update(-driveJ.getY() * 0.7, -driveJ.getThrottle() * 0.7, (detector.getCenterX()-160)/160, false);
				}
				if(Math.max(Math.abs(driveJ.getY()), Math.abs(driveJ.getThrottle())) > 0.5) {
					DriverStation.reportWarning("Be careful! You are attempting to drive the robot at a high speed. Are you sure you want to do that?", false);
				}
			}
			else {
				if(driveJ.getRawButton(6)) {
					drive.update(-driveJ.getY(), -driveJ.getThrottle(), (detector.getCenterX()-160)/160, true);
				}
				else {
					drive.update(-driveJ.getY(), -driveJ.getThrottle(), (detector.getCenterX()-160)/160, false);
				}
			}
			mandibles.rampTo(mechJ.getY());
			mandibles.rampRotate(-mechJ.getX());
			safety.update();
			//if(safety.getState() || mechJ.getRawButton(5)) {
				conveyor.rampTo(Math.abs(mechJ.getThrottle())*-mechJ.getThrottle());
			//}
			SmartDashboard.putBoolean("safety", safety.getState());
		//}
		/*if(driveJ.getRawButtonPressed(6)) {
			driveMode = Mode.JAMIE_MODE;
			DriverStation.reportWarning("Jamie Mode is enabled. You should be very careful with your inputs, as "
					+ "small touches may cause the robot to move wildly.", false);
		}
		else if(driveJ.getRawButton(8)) {
			driveMode = Mode.NORMAL_PERSON_MODE;
		}*/
		if(driveJ.getRawButtonPressed(1) || driveJ.getRawButtonPressed(2) || driveJ.getRawButtonPressed(3) || driveJ.getRawButtonPressed(4)) {
			if(driveMode.equals(Mode.JAMIE_MODE)) {
				DriverStation.reportWarning("Jamie mode is disabled. Feel the power!", false);
				driveMode = Mode.NORMAL_PERSON_MODE;
			}
			else {
				driveMode = Mode.JAMIE_MODE;
				DriverStation.reportWarning("Jamie Mode is enabled. You should be very careful with your inputs, as "
					+ "small touches may cause the robot to move wildly.", false);
			}
		}
		/*if(liftMode.equals(LiftMode.STANDARD)) {
			if(driveJ.getRawButton(5)) {
				liftMode = LiftMode.CLIMB;
			}
			else if(mechJ.getRawButton(5)) {
				liftMode = LiftMode.HOOK;
			}
			mandibles.rampTo(mechJ.getY());
			mandibles.rampRotate(-mechJ.getX());
			conveyor.rampTo(-Math.abs(mechJ.getThrottle())*mechJ.getThrottle());
		}
		else if(liftMode.equals(LiftMode.HOOK)) {
			if(driveJ.getRawButton(5)) {
				liftMode = LiftMode.CLIMB;
			}
			else if(mechJ.getRawButton(7)) {
				liftMode = LiftMode.STANDARD;
			}
			mandibles.rampTo(mechJ.getY());
			mandibles.rampRotate(-mechJ.getX());
			lift.bothRampTo(mechJ.getY());
		}
		else if(liftMode.equals(LiftMode.CLIMB)){
			if(driveJ.getRawButton(7)) {
				liftMode = LiftMode.STANDARD;
			}
			mandibles.rampTo(mechJ.getY());
			mandibles.rampRotate(-mechJ.getX());
			conveyor.rampTo(Math.abs(-mechJ.getThrottle())*mechJ.getThrottle());
			lift.tapeRampTo(driveJ.getY());
			lift.winchRampTo(driveJ.getThrottle());
		}*/
				
		SmartDashboard.putNumber("left encoder", leftEncoder.getInches());
		SmartDashboard.putNumber("right encoder", rightEncoder.getInches());
		SmartDashboard.putNumber("dab encoder", mandibles.getDabEncoder().getDegrees());
		/*if(driveJ.getRawButtonPressed(11)) {
			drive.getDrive().reverseDrive();
		}*/
		if(driveJ.getRawButton(5)) {
			drive.getDrive().setDriveReversed(false);
		}
		else if(driveJ.getRawButton(7)) {
			drive.getDrive().setDriveReversed(true);
		}
		/*if(driveJ.getRawButton(5)) {
			drive.getDrive().reverseDrive();
		}*/
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
		if(driveJ.getRawButtonPressed(1) || driveJ.getRawButtonPressed(2) || driveJ.getRawButtonPressed(3) || driveJ.getRawButtonPressed(4)) {
			if(driveMode.equals(Mode.JAMIE_MODE)) {
				DriverStation.reportWarning("Jamie mode is disabled. Feel the power!", false);
				driveMode = Mode.NORMAL_PERSON_MODE;
			}
			else {
				driveMode = Mode.JAMIE_MODE;
				DriverStation.reportWarning("Jamie Mode is enabled. You should be very careful with your inputs, as "
					+ "small touches may cause the robot to move wildly.", false);
			}
		}
	}
	
	public void sketchyLeftAuto() {
		if(System.currentTimeMillis() - initialTime < 500) {
			drive.update(0.7, 0.65, 0, false);
			mandibles.rampRotate(-0.6);
		}
		else if(System.currentTimeMillis() - initialTime < 1000) {
			drive.update(0.7, 0.65, 0, false);
			conveyor.rampTo(-0.4);
			mandibles.rampRotate(0);
		}
		else if(System.currentTimeMillis() - initialTime < 2500) {
			drive.update(0.7, 0.65, 0, false);
			conveyor.rampTo(0);
			mandibles.rampRotate(0);
		}
		else if(System.currentTimeMillis() - initialTime < 6000) {
			drive.update(0, 0, 0, false);
			if(switchSide.equals(Side.LEFT)) {
				conveyor.rampTo(0.6);
			}
		}
		else {
			conveyor.rampTo(0);
		}

	}
	
	public void leftStraightAuto() {
		if(autoState == 0) {
			drive.update(0.8 - (leftEncoder.getInches() - rightEncoder.getInches())/15 - leftEncoder.getInches()/1000, 0.8 - (rightEncoder.getInches() - leftEncoder.getInches())/15 - rightEncoder.getInches()/1000, 0, false);
			if(leftEncoder.getInches() > 140 || rightEncoder.getInches() > 140) {
				autoState = 0.5;
				initialTime = System.currentTimeMillis();
			}
		}
		else if(autoState == 0.5) {
			drive.update(0, 0, 0, false);
			mandibles.rampRotate(-0.6);
			if(System.currentTimeMillis() - initialTime > 500) {
				turnError = (rightEncoder.getInches() - leftEncoder.getInches())/2;
				autoState = 1;
				leftEncoder.getEncoder().reset();
				rightEncoder.getEncoder().reset();
			}
		}
		else if(autoState == 1) {
			//0.55
			mandibles.rampRotate(0);
			drive.update(0.6 - (leftEncoder.getInches() + rightEncoder.getInches())/25, -0.6 - (rightEncoder.getInches() + leftEncoder.getInches())/25, 0, false);
			if(leftEncoder.getInches() > 20+turnError || rightEncoder.getInches() < -20-turnError) {
				autoState = 1.5;
				initialTime = System.currentTimeMillis();
			}
		}
		else if(autoState == 1.5) {
			mandibles.rampRotate(0);
			conveyor.rampTo(-0.4);
			drive.update(0, 0, 0, false);
			if(System.currentTimeMillis() - initialTime > 500) {
				leftEncoder.getEncoder().reset();
				rightEncoder.getEncoder().reset();
				autoState = 2;
			}
		}
		else if(autoState == 2) {
			//0.7
			mandibles.rampRotate(0);
			conveyor.rampTo(0);
			drive.update(0.7, 0.7, 0, false);
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
			if(switchSide.equals(Side.LEFT)){
				conveyor.rampTo(0.6);
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
			drive.update(0.8 - (leftEncoder.getInches() - rightEncoder.getInches())/15 - leftEncoder.getInches()/1000, 0.8 - (rightEncoder.getInches() - leftEncoder.getInches())/15 - rightEncoder.getInches()/1000, 0, false);
			if(leftEncoder.getInches() > 140 || rightEncoder.getInches() > 140) {
				autoState = 0.5;
				initialTime = System.currentTimeMillis();
			}
		}
		else if(autoState == 0.5) {
			drive.update(0, 0, 0, false);
			mandibles.rampRotate(-0.6);
			if(System.currentTimeMillis() - initialTime > 500) {
				turnError = (leftEncoder.getInches() - rightEncoder.getInches())/2;
				autoState = 1;
				leftEncoder.getEncoder().reset();
				rightEncoder.getEncoder().reset();
			}
		}
		else if(autoState == 1) {
			//0.55
			mandibles.rampRotate(0);
			drive.update(-0.6 - (leftEncoder.getInches() + rightEncoder.getInches())/25, 0.6 - (rightEncoder.getInches() + leftEncoder.getInches())/25, 0, false);
			if(leftEncoder.getInches() < -20-turnError || rightEncoder.getInches() > 20+turnError) {
				autoState = 1.5;
				initialTime = System.currentTimeMillis();
			}
		}
		else if(autoState == 1.5) {
			drive.update(0, 0, 0, false);
			conveyor.rampTo(-0.4);
			mandibles.rampRotate(0);
			if(System.currentTimeMillis() - initialTime > 500) {
				leftEncoder.getEncoder().reset();
				rightEncoder.getEncoder().reset();
				autoState = 2;
			}
		}
		else if(autoState == 2) {
			//0.7
			conveyor.rampTo(0);
			drive.update(0.7, 0.7, 0, false);
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
				conveyor.rampTo(0.6);
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
		if(System.currentTimeMillis()-autoTime>500 && !deployed) {
			deployed=deployMandibles(autoTime+500);
		}
		
		if(autoState==0){
			if(deployed){
				begoneConveyor();
				begoneMandibles();
			}
			if(driveStraightInches(AUTO_SPEED, 20)) {
				autoState=1;
			}
		}else if(autoState==1) {
			if(deployed){
				begoneConveyor();
				begoneMandibles();
			}
			if(turnDegrees(AUTO_TURN, -110)){
				autoState=2;
			}
		}else if(autoState==2) {
			if(deployed){
				begoneConveyor();
				begoneMandibles();
			}
			if(driveStraightInches(AUTO_SPEED, 110)) {
				autoState=3;
			}
		}else if(autoState == 3) {
			if(deployed){
				begoneConveyor();
				begoneMandibles();
			}
			if(turnDegrees(AUTO_TURN, 90)){
				autoState=4;
				initialTime = System.currentTimeMillis();
			}
		}else if (autoState == 4) {
			if(deployed){
				begoneConveyor();
				begoneMandibles();
			}
			if(driveStraightTime(AUTO_SPEED, 2000, initialTime)){
				autoState=5;
				initialTime = System.currentTimeMillis();
			}
		}else if(autoState==5) {
			begoneDrive();
			if(runConveyorTime(.6,1500,initialTime)){
				autoState=6;
			}
		}else {
			begoneDrive();
			begoneConveyor();
		}
	}
	
	public void centerRightAuto() {
		/*if(System.currentTimeMillis()-autoTime>200 && !deployed) {
			deployed=deployMandibles(autoTime+200);
		}
		
		if(autoState==0){
			if(deployed){
				begoneConveyor();
				begoneMandibles();
			}
			if(driveStraightInches(AUTO_SPEED, 104)) {
				autoState=1;
				initialTime = System.currentTimeMillis();
			}
		}else if(autoState==1) {
			if(deployed){
				begoneConveyor();
				begoneMandibles();
			}
			if(driveStraightTime(AUTO_SPEED, 200, initialTime)){
				autoState=2;
				initialTime = System.currentTimeMillis();
			}
		}else if(autoState==2) {
			begoneDrive();
			if(runConveyorTime(.6,1500,initialTime)){
				autoState=3;
			}
		}else {
			begoneDrive();
			begoneConveyor();
		}*/
		
		if(System.currentTimeMillis() - initialTime < 500) {
			drive.update(0.7, 0.7, 0, false);
			mandibles.rampRotate(-0.6);
		}
		else if(System.currentTimeMillis() - initialTime < 1000) {
			drive.update(0.7, 0.7, 0, false);
			conveyor.rampTo(-0.4);
			mandibles.rampRotate(0);
		}
		else if(System.currentTimeMillis() - initialTime < 2800) {
			drive.update(0.7, 0.7, 0, false);
			conveyor.rampTo(0);
			mandibles.rampRotate(0);
		}
		else if(System.currentTimeMillis() - initialTime < 6000) {
			drive.update(0, 0, 0, false);
			if(switchSide.equals(Side.RIGHT)) {
				conveyor.rampTo(0.6);
			}
		}
		else {
			conveyor.rampTo(0);
		}
	}
	
	public void driveStraightAuto() {
		if(System.currentTimeMillis() - initialTime < 500) {
			drive.update(0.7, 0.7, 0, false);
			mandibles.rampRotate(-0.6);
		}
		else if(System.currentTimeMillis() - initialTime < 1000) {
			drive.update(0.7, 0.7, 0, false);
			conveyor.rampTo(-0.4);
			mandibles.rampRotate(0);
		}
		else if(System.currentTimeMillis() - initialTime < 2500) {
			drive.update(0.7, 0.7, 0, false);
			conveyor.rampTo(0);
			mandibles.rampRotate(0);
		}
		else {
			drive.update(0, 0, 0, false);
			conveyor.rampTo(0);
		}
		/*if(autoState == 0) {
			if(turnDegrees(AUTO_TURN, -90)) {
				autoState = 1;
			}
		}
		else {
			begoneDrive();
		}*/
	}
	
	public boolean driveStraightInches(double speed, double inches) {
		if((leftEncoder.getInches() + rightEncoder.getInches())/2 >= inches) {
			turnError = leftEncoder.getInches() - rightEncoder.getInches();
			leftEncoder.getEncoder().reset();
			rightEncoder.getEncoder().reset();
			return true;
		}
		else {
			drive.update(speed - (leftEncoder.getInches() - rightEncoder.getInches())/15 - leftEncoder.getInches()/1000, speed - (rightEncoder.getInches() - leftEncoder.getInches())/15 - rightEncoder.getInches()/1000, 0, false);
			return false;
		}
	}
	
	
	public boolean driveStraightTime(double speed, int ms, long initialTime) {
		if(System.currentTimeMillis() - initialTime >= ms) {
			return true;
		}
		else {
			drive.update(speed - (leftEncoder.getInches() - rightEncoder.getInches())/15 - leftEncoder.getInches()/1000, speed - (rightEncoder.getInches() - leftEncoder.getInches())/15 - rightEncoder.getInches()/1000, 0, false);
			return false;
		}
	}
	
	public void begoneDrive() {
		drive.update(0, 0, 0, false);
	}
	
	public void begoneConveyor() {
		conveyor.rampTo(0);
	}
	
	public void begoneMandibles() {
		mandibles.rampRotate(0);
	}
	
	public boolean turnDegrees(double speed, double degrees) {
		if(degrees >= 0) {
			if((leftEncoder.getInches() - rightEncoder.getInches())/2 >=  degrees * DEGREES_TO_INCHES - turnError) {
				leftEncoder.getEncoder().reset();
				rightEncoder.getEncoder().reset();
				return true;
			}
			drive.update(speed - (leftEncoder.getInches() + rightEncoder.getInches())/25, -speed - (rightEncoder.getInches() + leftEncoder.getInches())/25, 0, false);
			return false;
		}
		else {
			if((leftEncoder.getInches() - rightEncoder.getInches())/2 <=  degrees * DEGREES_TO_INCHES - turnError) {
				leftEncoder.getEncoder().reset();
				rightEncoder.getEncoder().reset();
				return true;
			}
			drive.update(-speed - (leftEncoder.getInches() + rightEncoder.getInches())/25, speed - (rightEncoder.getInches() + leftEncoder.getInches())/25, 0, false);
			return false;
		}
	}
	
	public boolean runConveyorTime(double speed, int ms, long initialTime) {
		if(System.currentTimeMillis()-initialTime >= ms) {
			return true;
		}
		conveyor.rampTo(speed);
		return false;
	}
	public boolean deployMandibles(long initialTime) {
		if(System.currentTimeMillis() - initialTime < 500) {
			mandibles.rampRotate(-0.6);
		}
		else if(System.currentTimeMillis() - initialTime < 1000) {
			conveyor.rampTo(-0.4);
			mandibles.rampRotate(0);
		}
		if(System.currentTimeMillis()-initialTime >= 1000) {
			conveyor.rampTo(0);
			mandibles.rampRotate(0);
			return true;
		}
		return false;
	}
}
