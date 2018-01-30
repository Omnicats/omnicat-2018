package util.sensor.analog.gyro;

import edu.wpi.first.wpilibj.AnalogGyro;

public class Gyro {
	private AnalogGyro gyro;
	
	public Gyro(int port) {
		gyro = new AnalogGyro(port);
	}
	
	public double getAngle() {
		return gyro.getAngle();
	}
	
	public double getRate() {
		return gyro.getRate();
	}
	
	public AnalogGyro getGyro() {
		return gyro;
	}
}
