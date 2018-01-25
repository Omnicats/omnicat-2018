package util.motor.drive;
import java.util.ArrayList;
import util.motor.basic.BasicMotor;

public class MecanumDrive extends Drive {

	public MecanumDrive(double maxAccel, ArrayList<BasicMotor> motorList) {
		super(maxAccel, motorList);
	}
	
	public MecanumDrive(double maxAccel, BasicMotor... motors) {
		super(maxAccel, motors);
	}
	
	public void update(double targetSpeed, double targetTurn, double targetRotation) {
		motorList.get(0).rampTo(targetSpeed + targetTurn + targetRotation, maxAccel);
		motorList.get(1).rampTo(targetSpeed + targetTurn + targetRotation, maxAccel);
		motorList.get(2).rampTo(-targetSpeed - targetTurn + targetRotation, maxAccel);
		motorList.get(3).rampTo(-targetSpeed - targetTurn + targetRotation, maxAccel);
	}

}
