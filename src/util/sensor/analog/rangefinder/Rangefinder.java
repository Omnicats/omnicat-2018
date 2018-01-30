package util.sensor.analog.rangefinder;

import edu.wpi.first.wpilibj.Ultrasonic;

public class Rangefinder {
	private Ultrasonic rangefinder;
	public boolean inInches = true;
	
	public enum Unit{
		Inches, Millimeters;
	}
	
	public Rangefinder(int port1, int port2) {
		rangefinder = new Ultrasonic(port1, port2);
	}
	
	public double getDistanceInches() {
		return rangefinder.getRangeInches();
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
		return rangefinder.getRangeMM();
	}
	
	public double getDistance() {
		return inInches ? getDistanceInches() : getDistanceMM();
	}

}
