package util.motor.basic;
import edu.wpi.first.wpilibj.Talon;

public class PWMTalon extends BasicMotor{
	public PWMTalon(int port, boolean inverted) {
		super(inverted);
		setController(new Talon(port));
	}
}
