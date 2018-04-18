package util.motor.drive;
import java.util.ArrayList;

import edu.wpi.first.wpilibj.drive.Vector2d;
import edu.wpi.first.wpilibj.drive.RobotDriveBase.MotorType;
import util.motor.basic.BasicMotor;

public class MecanumDrive extends Drive {

	public MecanumDrive(double maxAccel, ArrayList<BasicMotor> motorList) {
		super(maxAccel, motorList);
	}
	
	public MecanumDrive(double maxAccel, BasicMotor... motors) {
		super(maxAccel, motors);
	}
	
	public void update(double targetSpeed, double targetTurn, double targetRotation) {
	}
	
	public void update(double xSpeed, double ySpeed, double zRotation, double gyroAngle) {
	    Vector2d input = new Vector2d(ySpeed, xSpeed);
	    input.rotate(-gyroAngle);

	    double[] wheelSpeeds = new double[4];
	    wheelSpeeds[0] = input.x + input.y + zRotation;
	    wheelSpeeds[1] = -input.x + input.y + zRotation;
	    wheelSpeeds[2] = input.x - input.y + zRotation;
	    wheelSpeeds[3] = -input.x - input.y + zRotation;

	    normalize(wheelSpeeds);
	    
	    motorList.get(0).rampTo(wheelSpeeds[0]);
	    motorList.get(1).rampTo(wheelSpeeds[1]);
	    motorList.get(2).rampTo(wheelSpeeds[2]);
	    motorList.get(3).rampTo(wheelSpeeds[3]);
	}

}
