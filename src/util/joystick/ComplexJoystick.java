package util.joystick;
import edu.wpi.first.wpilibj.Joystick;

public class ComplexJoystick {
	protected Joystick joystick;
	public Joystick getJoystick() {
		return joystick;
	}
	public void setJoystick(Joystick joystick){
		this.joystick = joystick;
	}
	
	protected int buttonCount;
	public int getButtonCount(){
		return buttonCount;
	}
	public void setButtonCount(int buttonCount) {
		this.buttonCount = buttonCount;
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
	
	public ComplexJoystick(Joystick joystick) {
		setJoystick(joystick);
		setButtonCount(getJoystick().getButtonCount());
	}
	
}
