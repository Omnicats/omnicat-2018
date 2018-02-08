package util.motor.basic;

import edu.wpi.first.wpilibj.SpeedController;

public abstract class BasicMotor {
	protected double maxAccel = 0.2;
	public void setMaxAccel(double maxAccel) {
		this.maxAccel = maxAccel;
	}
	public double getMaxAccel() {
		return maxAccel;
	}
	
	protected double speed;
	public void setSpeed(double speed) {
		this.speed = speed;
		controller.set(speed);
	}
	public double getSpeed() {
		return speed;
	}

	protected SpeedController controller;
	public void setController(SpeedController controller) {
		this.controller = controller;
	}
	public SpeedController getController() {
		return controller;
	}
	
	protected boolean inverted;
	public void setInverted(boolean inverted) {
		this.inverted = inverted;
		controller.setInverted(inverted);
	}
	public boolean getInverted() {
		return inverted;
	}
	
	public BasicMotor(SpeedController controller) {
		this(controller, false);
	}
	
	public BasicMotor(SpeedController controller, boolean inverted) {
		setController(controller);
		setInverted(inverted);
	}
		
	public void rampTo(double target, double maxAccel) {
		if(Math.abs(getSpeed()-target) <= maxAccel) {
			setSpeed(target);
		}
		else if(getSpeed()<target) {
			setSpeed(getSpeed()+maxAccel);
		}
		else {
			setSpeed(getSpeed()-maxAccel);
		}
	}
	
	public static BasicMotor[] motorsFromInt(int... ports) {
		return null;
	};
	
	public void rampTo(double target) {
		rampTo(target, maxAccel);
	}
}