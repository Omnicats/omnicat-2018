package mechanism.drive;

import mechanism.sensor.RangefinderArray;
import util.motor.basic.BasicMotor;
import util.motor.drive.ArcadeDrive;

public class RangefinderDrive {
	public final int THRESHOLD = 6;
	
	public enum Mode {
		P, I, PI;
	}
	
	public final Mode mode = Mode.I;
	
	private ArcadeDrive drive;
	public void setInvertedMotor(int ...port) {
		drive.setInverted(port);
	}
	private RangefinderArray array;
	public RangefinderArray getArray() {
		return array;
	}
	private double turnSpeed = 0;
	
	private boolean useAssist = false;
	public void setUseAssist(boolean useAssist) {
		this.useAssist = useAssist;
	}
	public boolean getUseAssist() {
		return useAssist;
	}
	
	public RangefinderDrive(int leftPort, int centerPort, int rightPort, double maxAccel, BasicMotor ...motors) {
		array = new RangefinderArray(leftPort, centerPort, rightPort);
		drive = new ArcadeDrive(maxAccel, motors);
	}
	
	public void update(double targetSpeed, double targetTurn, boolean useAssist) {
		if(useAssist) {
			double leftValue = array.getValues()[0];
			double centerValue = array.getValues()[1];
			double rightValue = array.getValues()[2];
			if(mode.equals(Mode.I)) {
				if(Math.abs(centerValue - leftValue) < THRESHOLD) {
					turnSpeed -= 0.01;
				}
				else if(Math.abs(centerValue - rightValue) < THRESHOLD) {
					turnSpeed += 0.01;
				}
				else {
					turnSpeed = 0;
				}
			}
			else if(mode.equals(Mode.P)) {
				if(Math.abs(centerValue - leftValue) < THRESHOLD) {
					turnSpeed = -12/leftValue;
				}
				else if(Math.abs(centerValue - rightValue) < THRESHOLD) {
					turnSpeed = 12/rightValue;
				}
				else {
					turnSpeed = 0;
				}
			}
			drive.update(targetSpeed, turnSpeed);
		}
		else {
			drive.update(targetSpeed, targetTurn);
			turnSpeed = 0;
		}
	}
	
	public void update(double targetSpeed, double targetTurn) {
		update(targetSpeed, targetTurn, useAssist);
	}
}
