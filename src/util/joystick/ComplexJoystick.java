package util.joystick;
import edu.wpi.first.wpilibj.Joystick;

public class ComplexJoystick extends Joystick{
	public ComplexJoystick(int port) {
		super(port);
		buttonState = new boolean[getButtonCount()];
	}
	
	protected boolean[] buttonState;
	public boolean[] getButtonState(){
		return buttonState;
	}
	public void setbuttonState(boolean[] buttonState) {
		this.buttonState = buttonState;
	}
	
	protected boolean[] prevButtonState;
	public boolean[] getPrevButtonState(){
		return prevButtonState;
	}
	public void setPrevButtonState(boolean[] prevButtonState) {
		this.prevButtonState = prevButtonState;
	}
	
	public void updateButtonState() {
		setPrevButtonState(buttonState);
		for(int i = 0; i < getButtonCount(); i++) {
			buttonState[i] = getRawButton(i+1);
		}
	}
	
}
