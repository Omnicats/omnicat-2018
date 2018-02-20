package mechanism;

import util.motor.basic.BasicMotor;
import util.motor.cluster.MotorCluster;

public class Conveyor {
	final double MAX_SPEED = 0.75;
	final double MAX_ROTATE = 0.5;
	private MotorCluster leftSide;
	private MotorCluster rightSide;
	private BasicMotor t2r;
	
	public Conveyor(double maxConveyorAccel, double maxT2rAccel, BasicMotor[] motors) {
		BasicMotor[] leftMotors = new BasicMotor[(motors.length-1)/2];
		BasicMotor[] rightMotors = new BasicMotor[(motors.length-1)/2];
		for(int i = 0; i < motors.length-1; i++) {
			if(i < leftMotors.length) {
				leftMotors[i] = motors[i];
			}
			else {
				rightMotors[i-rightMotors.length] = motors[i];
			}
		}
		leftSide = new MotorCluster(maxConveyorAccel, leftMotors);
		rightSide = new MotorCluster(maxConveyorAccel, rightMotors);
		t2r = motors[motors.length-1];
		t2r.setMaxAccel(maxT2rAccel);
	}
	
	public void rampTo(double target) {
		leftSide.rampTo(target);
		rightSide.rampTo(target);
		if(Math.abs(target)>0.1) {
			t2r.rampTo(Math.signum(target));
		}
		else {
			t2r.rampTo(0);
		}
	}
	
	public void rampLeft(double target) {
		leftSide.rampTo(target);
	}
	
	public void rampRight(double target) {
		rightSide.rampTo(target);
	}
	
	public void rampRotate(double target) {
		leftSide.rampTo(-target);
		rightSide.rampTo(target);
	}
	
	public void twoButtonRun(boolean upButton, boolean downButton) {
		if(upButton) {
			rampTo(MAX_SPEED);
		}
		else if(downButton) {
			rampTo(-MAX_SPEED);
		}
		else{
			rampTo(0);
		}
	}
	
	public void twoButtonRotate(boolean leftButton, boolean rightButton) {
		if(leftButton) {
			rampRotate(MAX_ROTATE);
		}
		else if(rightButton) {
			rampRotate(-MAX_ROTATE);
		}
		else {
			rampRotate(0);
		}
	}
}
