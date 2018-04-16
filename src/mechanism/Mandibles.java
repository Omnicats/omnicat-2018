package mechanism;

import util.motor.basic.BasicMotor;
import util.sensor.digital.DigitalEncoder;

public class Mandibles {
	final double MAX_SPEED = 1;
	final double MAX_ROTATE = 0.5;
	final double UPPER_DEGREES_LIMIT = 105;
	final double LOWER_DEGREES_LIMIT = 85;
	final double SLOWDOWN_BAND = 4;
	
	private BasicMotor intakeMotor;
	private BasicMotor dabMotor;
	
	private DigitalEncoder dabEncoder;
	public DigitalEncoder getDabEncoder() {
		return dabEncoder;
	}
	
	public Mandibles(int port0, int port1, BasicMotor[] motors) {
		dabEncoder = new DigitalEncoder(port0, port1);
		intakeMotor = motors[0];
		dabMotor = motors[1];
	}
	
	public void rampTo(double target) {
		intakeMotor.rampTo(target*Math.abs(target)*0.5);
	}
	
	public void rampRotate(double target) {
		/*if(dabEncoder.getDegrees() < LOWER_DEGREES_LIMIT + SLOWDOWN_BAND && target < 0) {
			target = target * (dabEncoder.getDegrees() - LOWER_DEGREES_LIMIT)/SLOWDOWN_BAND;
		}
		else if(dabEncoder.getDegrees() > UPPER_DEGREES_LIMIT + SLOWDOWN_BAND && target > 0) {
			target = target * (UPPER_DEGREES_LIMIT - dabEncoder.getDegrees())/SLOWDOWN_BAND;
		}*/
		dabMotor.rampTo(target * Math.abs(target)*0.5);
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
