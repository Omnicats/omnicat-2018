package util.motor.basic;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class CANTalon extends BasicMotor{
	public CANTalon(int port, boolean inverted) {
		super(new WPI_TalonSRX(port), inverted);
	}
	
	public static CANTalon[] motorsFromInt(int... ports) {
		CANTalon[] motors = new CANTalon[ports.length];
		for(int i = 0; i < ports.length; i++) {
			motors[i] = new CANTalon(ports[i], false);
		}
		return motors;
	}
}
