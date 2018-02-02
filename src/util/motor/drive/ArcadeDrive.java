package util.motor.drive;
import java.util.ArrayList;
import util.motor.basic.BasicMotor;

public class ArcadeDrive extends Drive {

	public ArcadeDrive(double maxAccel, ArrayList<BasicMotor> motorList) {
		super(maxAccel, motorList);
	}
	
	public ArcadeDrive(double maxAccel, BasicMotor... motors) {
		super(maxAccel, motors);
	}
	
	public ArcadeDrive(double maxAccel, int... motors) {
		this(maxAccel, BasicMotor.motorsFromInt(motors));
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
