package util.motor.drive;

import java.util.ArrayList;

import util.motor.basic.BasicMotor;

public class TankDrive extends Drive {

	public TankDrive(ArrayList<BasicMotor> motorList, double maxAccel) {
		super(motorList, maxAccel);
		// TODO Auto-generated constructor stub
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
