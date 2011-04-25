package music;

/* Duration is a class used to represent a rhytmic duration.  It implements rational arithmetic
 * by representing all durations as Rationals.
 */
public class Timestep {
	Rational _duration;	// Time duration is stored as a rational number.
	
	public Timestep() {
		_duration = new Rational(0, 1);
	}
	
	public Timestep(Rational duration) {
		_duration = duration;
   }
	
	public Rational getDuration() {
		return _duration;
	}
	
	public Timestep setDuration(Rational newDuration) {
		_duration = newDuration;
		return this;
	}
	
	public void addDuration(Timestep dur) {
		setDuration(_duration.plus(dur.getDuration()));
	}
}