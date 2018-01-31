package util.sensor.analog.rangefinder;

import edu.wpi.first.wpilibj.AnalogInput;

public class Rangefinder {
	private AnalogInput rangefinder;
	public boolean inInches = true;
	
	public enum Unit{
		Inches, Millimeters;
	}
	
	public Rangefinder(int port) {
		rangefinder = new AnalogInput(port);
	}
	
	public double getDistanceInches() {
		return getDistanceMM()*25.4;
	}
	
	public void setUnits(Unit unit) {
		if(unit.equals(Unit.Inches)) {
			inInches = true;
		}
		else {
			inInches = false;
		}
	}
	public Unit getUnits() {
		return inInches ? Unit.Inches : Unit.Millimeters;
	}
	
	public double getDistanceMM() {
		return rangefinder.getValue()*5;
	}
	
	public double getDistance() {
		return inInches ? getDistanceInches() : getDistanceMM();
	}

}
