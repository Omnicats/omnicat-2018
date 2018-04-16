package mechanism;

import util.motor.basic.BasicMotor;
import util.motor.cluster.MotorCluster;

public class TapeLift {
	final double MAX_SPEED = 1;
	final double WINCH_TO_TAPE_RATIO = 0.5;
	private MotorCluster winch;
	private BasicMotor tape;
	
	public TapeLift(double maxAccel, BasicMotor[] motors) {
		winch = new MotorCluster(maxAccel, motors[1], motors[2]);
		tape = motors[0];
	}
	
	public void winchRampTo(double target) {
		winch.rampTo(target);
	}
	
	public void tapeRampTo(double target) {
		tape.rampTo(target);
	}
	
	public void bothRampTo(double target) {
		winch.rampTo(target * WINCH_TO_TAPE_RATIO);
	}
	
	public void twoButtonRun(boolean upButton, boolean downButton) {
		if(upButton) {
			winch.rampTo(MAX_SPEED * WINCH_TO_TAPE_RATIO);
			tape.rampTo(MAX_SPEED);
		}
		else if(downButton) {
			winch.rampTo(-MAX_SPEED * WINCH_TO_TAPE_RATIO);
			tape.rampTo(-MAX_SPEED);
		}
		else{
			winch.rampTo(0);
			tape.rampTo(0);
		}
	}
	
}
