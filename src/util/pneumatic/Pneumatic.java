package util.pneumatic;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SolenoidBase;

public abstract class Pneumatic {
	protected SolenoidBase solenoid;
	
	public Pneumatic(SolenoidBase solenoid) {
		this.solenoid = solenoid;
	}
	
	public void reverse() {
		
	}
	
	public boolean extended;
	public void set(boolean extended) {
		
	}
	public boolean getExtended(){
		return false;
	}
}
