package util.motor.basic;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Talon;

public class PWMTalon extends BasicMotor{
	public PWMTalon(int port, boolean inverted) {
		super(inverted);
		setController(new Talon(port));
	}
}
