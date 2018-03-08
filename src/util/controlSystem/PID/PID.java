package util.controlSystem.PID;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PID {	
	private double kp;
	public double getKp() {
		return kp;
	}
	public void setKp(double kp) {
		this.kp = kp;
	}
	
	private double ki;
	public double getKi() {
		return ki;
	}
	public void setKi(double ki) {
		this.ki = ki;
	}
	
	private double kd;
	public double getKd() {
		return kd;
	}
	public void setKd(double kd) {
		this.kd = kd;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void reset() {
		previousError = 0;
		i = 0;
		d = 0;
	}
	
	public PID(double kp, double ki, double kd) {
		this.kp = kp;
		this.ki = ki;
		this.kd = kd;
	}
	
	private double previousError = 0;
	private double i = 0;
	private double d = 0;
	private double previousTime = 0;
	private double dt = 0;
	
	public double update(double error) {
		dt = (System.currentTimeMillis() - previousTime)/1000;
		i += error/dt;
		d = -(error - previousError)/dt;
		previousError = error;
		SmartDashboard.putNumber(name + " P", kp*error);
		SmartDashboard.putNumber(name + " I", ki*i);
		SmartDashboard.putNumber(name + " D", kd*d);
		SmartDashboard.putNumber(name + " KP", kp);
		SmartDashboard.putNumber(name + " KI", ki);
		SmartDashboard.putNumber(name + " KD", kd);
		SmartDashboard.putNumber(name + " Output", kp*error + ki*i + kd*d);
		previousTime = System.currentTimeMillis()/1000;
		return kp*error + ki*i + kd*d;
	}
}
