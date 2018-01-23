package util.motor.drive;

import java.util.ArrayList;

import util.motor.basic.BasicMotor;

public class TankDrive extends Drive {

	public TankDrive(double maxAccel, ArrayList<BasicMotor> motorList) {
		super(maxAccel, motorList);
	}
	
	public TankDrive(double maxAccel, BasicMotor... motors) {
		super(maxAccel, motors);
	}
	
	public void update(double leftTargetSpeed, double rightTargetSpeed) {
		for(BasicMotor m : leftMotorList) {
			m.rampTo(leftTargetSpeed, maxAccel);
		}
		for(BasicMotor m : rightMotorList) {
			m.rampTo(rightTargetSpeed, maxAccel);
		}
	}

}
