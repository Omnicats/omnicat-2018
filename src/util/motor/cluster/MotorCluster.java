package util.motor.cluster;

import java.util.ArrayList;

import util.motor.basic.BasicMotor;

public class MotorCluster{
	ArrayList<BasicMotor> motors = new ArrayList<BasicMotor>();
	
	protected double maxAccel;
	public void setMaxAccel(double maxAccel) {
		this.maxAccel = maxAccel;
	}
	public double getMaxAccel() {
		return maxAccel;
	}
	
	public void addMotor(BasicMotor ...motors) {
		for(BasicMotor motor : motors) {
			this.motors.add(motor);
		}
	}
	
	public MotorCluster(ArrayList<BasicMotor> motors) {
		this(0.2, motors);
	}
	
	public MotorCluster(BasicMotor ...motors) {
		this(0.2, motors);
	}
	
	public MotorCluster(double maxAccel, ArrayList<BasicMotor> motors) {
		this.motors.addAll(motors);
		this.maxAccel = maxAccel;
	}
	
	public MotorCluster(double maxAccel, BasicMotor ...motors) {
		addMotor(motors);
		this.maxAccel = maxAccel;
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
	
	public void rampTo(double target) {
		rampTo(target, maxAccel);
	}
	
	protected double speed;
	public void setSpeed(double speed) {
		this.speed = speed;
		for(BasicMotor m : motors) {
			m.setSpeed(speed);
		}
	}
	public double getSpeed() {
		return speed;
	}
	
	public void setInverted(boolean inverted, int ...index) {
		for(int i : index) {
			motors.get(i).setInverted(inverted);
		}
	}
	public void setInverted(int ...index) {
		setInverted(true, index);
	}
}
