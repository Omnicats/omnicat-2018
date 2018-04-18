package mechanism.drive;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import util.motor.drive.Drive;
import util.motor.drive.MecanumDrive;

public class PIDMecanumDrive {
	private MecanumDrive drive;
	public void setInvertedMotor(int ...port) {
		drive.setInverted(port);
	}
	public MecanumDrive getDrive() {
		return drive;
	}
	
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
	
	public PIDMecanumDrive(double kp, double ki, double kd, MecanumDrive drive) {
		this.kp = kp;
		this.ki = ki;
		this.kd = kd;
		this.drive = drive;
	}
	
	private double previousError = 0;
	private double i = 0;
	private double d = 0;
	private double previousTime = 0;
	private double dt = 0;
	
	public void update(double xSpeed, double ySpeed, double rotateSpeed, double gyroAngle, double error, boolean usePID) {
		dt = (System.currentTimeMillis() - previousTime)/1000;
		if(usePID) {
			i += error/dt;
			d = -(error - previousError)/dt;
			drive.update(xSpeed, ySpeed, kp*error + ki*i + kd*d, gyroAngle);
			previousError = error;
		}
		else {
			previousError = 0;
			i = 0;
			d = 0;
			drive.update(xSpeed, ySpeed, rotateSpeed, gyroAngle);
		}
		SmartDashboard.putNumber("P", kp*error);
		SmartDashboard.putNumber("I", ki*i);
		SmartDashboard.putNumber("D", kd*d);
		SmartDashboard.putNumber("KP", kp);
		SmartDashboard.putNumber("KI", ki);
		SmartDashboard.putNumber("KD", kd);
		SmartDashboard.putNumber("Output", kp*error + ki*i + kd*d);
		previousTime = System.currentTimeMillis()/1000;
	}
}
