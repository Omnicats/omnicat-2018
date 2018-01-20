package util.motor.basic;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class CANTalon extends BasicMotor{
	public CANTalon(int port, boolean inverted) {
		super(inverted);
		setController(new WPI_TalonSRX(port));
	}
}
