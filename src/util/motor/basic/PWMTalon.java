package util.motor.basic;
import edu.wpi.first.wpilibj.Talon;

public class PWMTalon extends BasicMotor{
	public PWMTalon(int port, boolean inverted) {
		super(new Talon(port), inverted);
	}
	
	public static PWMTalon[] motorsFromInt(int... ports) {
		PWMTalon[] motors = new PWMTalon[ports.length];
		for(int i = 0; i < ports.length; i++) {
			motors[i] = new PWMTalon(ports[i], false);
		}
		return motors;
	}
}
