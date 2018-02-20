package util.motor.drive;
import java.util.ArrayList;
import java.util.Arrays;

import util.motor.basic.BasicMotor;


public abstract class Drive{	
	
	protected double maxAccel;
	public double getMaxAccel() {
		return maxAccel;
	}public void setMaxAccel(double maxAccel) {
		this.maxAccel = maxAccel;
	}
	
	protected boolean driveReversed = false;
	public boolean getDriveReversed() {
		return driveReversed;
	}
	public void setDriveReversed(boolean driveReversed) {
		if(this.driveReversed != driveReversed) {
			for(BasicMotor m : motorList) {
				m.setInverted(!m.getInverted());
			}
		}
		this.driveReversed = driveReversed;
	}
	public void reverseDrive() {
		setDriveReversed(!getDriveReversed());
	}
	
	protected ArrayList<BasicMotor> leftMotorList;
	public ArrayList<BasicMotor> getleftMotorList(){
		return leftMotorList;
	}public void setleftMotorList(ArrayList<BasicMotor> leftMotorList) {
		this.leftMotorList = leftMotorList;
	}
	
	protected ArrayList<BasicMotor> rightMotorList;
	public ArrayList<BasicMotor> getrightMotorList(){
		return rightMotorList;
	}public void setrightMotorList(ArrayList<BasicMotor> rightMotorList) {
		this.rightMotorList = rightMotorList;
	}
	
	protected ArrayList<BasicMotor> motorList;
	public ArrayList<BasicMotor> getMotorList(){
		return motorList;
	}public void setMotorList(ArrayList<BasicMotor> motorList) {
		this.motorList = motorList;
	}
		
	public void setInverted(boolean isInverted, int ...i) {
		for(int j : i) {
			motorList.get(j).setInverted(isInverted);
		}
	}public void setInverted(int ...i) {
		for(int j : i) {
			motorList.get(j).setInverted(true);
		}
	}
	
	public Drive(double maxAccel, ArrayList<BasicMotor> motorList) {
		this.motorList = motorList;
		leftMotorList = new ArrayList<BasicMotor>();
		for(int i = 0; i < motorList.size()/2; i++) {
			leftMotorList.add(motorList.get(i));
		}
		rightMotorList = new ArrayList<BasicMotor>();
		for(int i = motorList.size()/2; i < motorList.size(); i++) {
			rightMotorList.add(motorList.get(i));
		}
		
		this.maxAccel = maxAccel;
	}
	
	public Drive(double maxAccel, BasicMotor ...motors) {
		this(maxAccel, new ArrayList<BasicMotor>(Arrays.asList(motors)));
	}
	
	public void update(double targetSpeed, double turnSpeed) {

	}
	

}
