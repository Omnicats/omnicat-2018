package mechanism;

import util.motor.basic.BasicMotor;
import util.motor.cluster.MotorCluster;

public class TapeLift {
	final double MAX_SPEED = 0.75;
	private MotorCluster lift;
	
	public TapeLift(double maxAccel, BasicMotor[] motors) {
		lift = new MotorCluster(maxAccel, motors);
	}
	
	public void rampTo(double target) {
		lift.rampTo(target);
	}
	
	public void twoButtonRun(boolean upButton, boolean downButton) {
		if(upButton) {
			lift.rampTo(MAX_SPEED);
		}
		else if(downButton) {
			lift.rampTo(-MAX_SPEED);
		}
		else{
			lift.rampTo(0);
		}
	}
	
}
