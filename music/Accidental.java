package music;
/* Accidental is an enum type whose fields represent the different possible accidental values for a
 * pitch.
 */
public enum Accidental {
	DOUBLEFLAT (-2),
	FLAT (-1),
	NATURAL (0),
	SHARP (1),
	DOUBLESHARP (2);
	
	int _accid;
	Accidental(int accid) {
		_accid = accid;
	}
	
	public int intValue() {
		return _accid;
	}
}