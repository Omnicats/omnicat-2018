package mechanism;

import util.motor.basic.BasicMotor;
import util.motor.cluster.MotorCluster;

public class OldMandibles {
	final double MAX_SPEED = 1;
	final double MAX_ROTATE = 0.5;
	private MotorCluster leftSide;
	private MotorCluster rightSide;
	
	public OldMandibles(double maxAccel, BasicMotor[] motors) {
		BasicMotor[] leftMotors = new BasicMotor[motors.length/2];
		BasicMotor[] rightMotors = new BasicMotor[motors.length/2];
		for(int i = 0; i < motors.length; i++) {
			if(i < leftMotors.length) {
				leftMotors[i] = motors[i];
			}
			else {
				rightMotors[i-rightMotors.length] = motors[i];
			}
		}
		leftSide = new MotorCluster(maxAccel, leftMotors);
		rightSide = new MotorCluster(maxAccel, rightMotors);
	}
	
	public void rampTo(double target) {
		leftSide.rampTo(target*Math.abs(target));
		rightSide.rampTo(target*Math.abs(target));
	}
	
	public void rampRotate(double target) {
		leftSide.rampTo(-target);
		rightSide.rampTo(target);
	}
	
	public void leftRampTo(double target) {
		leftSide.rampTo(target*Math.abs(target));
	}
	
	public void rightRampTo(double target) {
		rightSide.rampTo(target*Math.abs(target));
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
