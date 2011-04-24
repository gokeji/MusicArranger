package music;

/* TimeSig represents a musical time signature, which specifies which type of note represents
 * a beat, and how many beats are contained within a measure.
 */
public class TimeSignature extends Duration {
	Duration _duration;					// Specifies the number of beats the time signature lasts for
	Duration _measureDuration;			// Specifies the duration of a measure under the time signature.  NOTE: This is different from the time signature's duration within the piece!

	public TimeSignature(){
		
	}
	
	public Duration getDuration() {
		return _duration;
	}
	
	public Duration getMeasureDuration() {
		return _measureDuration;
	}
}