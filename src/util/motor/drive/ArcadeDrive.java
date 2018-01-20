package util.motor.drive;
import java.util.ArrayList;
import util.motor.basic.BasicMotor;

public class ArcadeDrive extends Drive {

	public ArcadeDrive(ArrayList<BasicMotor> motorList, double increment) {
		super(motorList, increment);
	}
	
	public void update(double targetSpeed, double targetTurn) {
		for(BasicMotor m : leftMotorList) {
			m.rampTo(targetSpeed + targetTurn, maxAccel);
		}
		for(BasicMotor m : rightMotorList) {
			m.rampTo(targetSpeed - targetTurn, maxAccel);
		}
	}

}
