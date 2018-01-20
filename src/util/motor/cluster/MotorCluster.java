package util.motor.cluster;

import java.util.ArrayList;

import util.motor.basic.BasicMotor;

public class MotorCluster extends BasicMotor{
	ArrayList<BasicMotor> motors = new ArrayList<BasicMotor>();
	
	public void addMotor(BasicMotor ...m) {
		for(BasicMotor b : m) {
			motors.add(b);
		}
	}
	
	public MotorCluster(ArrayList<BasicMotor> motors) {
		setController(motors.get(0).getController());
		this.motors.addAll(motors);
	}
	
	public MotorCluster(BasicMotor ...m) {
		addMotor(m);
	}
	
	public void rampTo(double target, double increment) {
		for(BasicMotor m : motors) {
			m.rampTo(target, increment);
		}
	}
	
}
