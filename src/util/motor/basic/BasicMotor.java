package util.motor.basic;

import edu.wpi.first.wpilibj.SpeedController;


public abstract class BasicMotor {
	
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
	
	public BasicMotor() {
		this(false);
	}
	
	public BasicMotor(boolean inverted) {
		setInverted(inverted);
	}

		
	public void rampTo(double target, double increment) {
		if(Math.abs(getSpeed()-target) <= increment) {
			setSpeed(target);
		}
		else if(getSpeed()<target) {
			setSpeed(getSpeed()+increment);
		}
		else {
			setSpeed(getSpeed()-increment);
		}
	}
}