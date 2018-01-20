package util.motor.basic;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Talon;


public class BasicMotor{
	
	public enum motorType{
		CANTALON, TALON
	}
	
	protected double speed;
	public void setSpeed(double speed) {
		if(speed > 1) {
			speed = 1;
		}else if(speed < -1) {
			speed = -1;
		}
		switch(type) {
			case CANTALON:
				((WPI_TalonSRX)controller).set(speed);
				break;
				
			case TALON:
				((Talon)controller).set(speed);
				break;
		}
		this.speed = speed;
	}
	public double getSpeed() {
		return speed;
	}

	protected Object controller;
	public void setController(Object controller) {
		this.controller = controller;
	}
	public Object getController() {
		return controller;
	}
	
	protected motorType type;
	public void setType(motorType type) {
		this.type = type;
	}
	public motorType getType() {
		return type;
	}
	
	protected boolean inverted;
	public void setInverted(boolean inverted) {
		this.inverted = inverted;
		switch(type) {
			case CANTALON:
				((WPI_TalonSRX)controller).setInverted(inverted);
				break;
				
			case TALON:
				((Talon)controller).setInverted(inverted);
				break;
		}	
	}
	public boolean getInverted() {
		return inverted;
	}
	
	public BasicMotor(motorType type, int port) {
		this(type, port, false);
	}
	
	public BasicMotor(motorType type, int port, boolean inverted) {
		switch(type) {
			case CANTALON:
				setController(new WPI_TalonSRX(port));
				break;
				
			case TALON:
				setController(new Talon(port));
				break;
		}
		setType(type);
		setInverted(inverted);
	}

		
	public void rampTo(double target, double increment) {
		if(Math.abs(getSpeed()-target) <= increment) {
			setSpeed(target);
		}
		else if(getSpeed()<target) {
			setSpeed(getSpeed()+increment);
		}
		else {
			setSpeed(getSpeed()-increment);
		}
	}
}