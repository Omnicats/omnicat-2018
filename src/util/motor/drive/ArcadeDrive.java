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
		
	public void update(double targetSpeed, double targetTurn) {
		if(driveReversed) {
			targetTurn = -targetTurn;
		}
		/*targetSpeed = limit(targetSpeed);
	    targetSpeed = applyDeadband(targetSpeed, m_deadband);

	    targetTurn = limit(targetTurn);
	    targetTurn = applyDeadband(targetTurn, m_deadband);

	    // Square the inputs (while preserving the sign) to increase fine control
	    // while permitting full power.
	    if (squaredInputs) {
	      targetSpeed = Math.copySign(targetSpeed * targetSpeed, targetSpeed);
	      targetTurn = Math.copySign(targetTurn * targetTurn, targetTurn);
	    }

	    double leftMotorOutput;
	    double rightMotorOutput;

	    double maxInput = Math.copySign(Math.max(Math.abs(targetSpeed), Math.abs(targetTurn)), targetSpeed);

	    if (targetSpeed >= 0.0) {
	      // First quadrant, else second quadrant
	      if (targetTurn >= 0.0) {
	        leftMotorOutput = maxInput;
	        rightMotorOutput = targetSpeed - targetTurn;
	      } else {
	        leftMotorOutput = targetSpeed + targetTurn;
	        rightMotorOutput = maxInput;
	      }
	    } else {
	      // Third quadrant, else fourth quadrant
	      if (targetTurn >= 0.0) {
	        leftMotorOutput = targetSpeed + targetTurn;
	        rightMotorOutput = maxInput;
	      } else {
	        leftMotorOutput = maxInput;
	        rightMotorOutput = targetSpeed - targetTurn;
	      }
	    }*/

	    //m_leftMotor.set(limit(leftMotorOutput) * m_maxOutput);
	    //m_rightMotor.set(-limit(rightMotorOutput) * m_maxOutput);
	    
		for(BasicMotor m : leftMotorList) {
			m.rampTo(targetSpeed - targetTurn, maxAccel);
		}
		for(BasicMotor m : rightMotorList) {
			m.rampTo(targetSpeed + targetTurn, maxAccel);
		}
	}

}
