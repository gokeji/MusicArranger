package tests;

import music.*;

public class LongMelodyPiece extends Piece {

	public LongMelodyPiece() {
		super();

		// pitches
		Pitch cs4 = new Pitch(NoteLetter.C, 4, Accidental.SHARP, false);
		Pitch dn4 = new Pitch(NoteLetter.D, 4, Accidental.NATURAL, false);
		Pitch en4 = new Pitch(NoteLetter.E, 4, Accidental.NATURAL, false);
		Pitch fs4 = new Pitch(NoteLetter.F, 4, Accidental.SHARP, false);
		Pitch gn4 = new Pitch(NoteLetter.G, 4, Accidental.NATURAL, false);
		Pitch an4 = new Pitch(NoteLetter.A, 4, Accidental.NATURAL, false);
		Pitch bn4 = new Pitch(NoteLetter.B, 4, Accidental.NATURAL, false);
		
		// multinotes
		MultiNote I = new MultiNote(new Rational(2, 4));
			I.getPitches().add(dn4);
			I.getPitches().add(fs4);
			I.getPitches().add(an4);
		MultiNote IV = new MultiNote(new Rational(1, 4));
			IV.getPitches().add(dn4);
			IV.getPitches().add(gn4);
			IV.getPitches().add(bn4);
		MultiNote V = new MultiNote(new Rational(1, 4));
			V.getPitches().add(cs4);
			V.getPitches().add(en4);
			V.getPitches().add(an4);
		
		// clefs
		Clef cleftreble = new Clef(new Rational(4, 4), ClefName.GCLEF, -2);
		
		// time signatures
		TimeSignature timesig1 = new TimeSignature(new Rational(4, 4), 4, 4);
		
		// key signatures
		KeySignature keysig1 = new KeySignature(new Rational(4, 4), 2, true);
		
		// chord symbols
		ChordSymbol chordsymbol1 = new ChordSymbol(new Rational(4, 4), 1, ChordType.MAJOR);
		ChordSymbol chordsymbol2 = new ChordSymbol(new Rational(4, 4), 4, ChordType.MINOR);
		ChordSymbol chordsymbol3 = new ChordSymbol(new Rational(4, 4), 5, ChordType.MAJOR7);
		
		// staffs
		Staff stafftreble = new Staff();
		
		Voice voice1_1_1 = new Voice();				// staff 1, measure 1, voice 1
			voice1_1_1.getMultiNotes().add(I);
			voice1_1_1.getMultiNotes().add(IV);
			voice1_1_1.getMultiNotes().add(V);
		
		// measures
		Measure measure1_1 = new Measure();
			measure1_1.getKeySignatures().add(keysig1);
			measure1_1.getTimeSignatures().add(timesig1);
			measure1_1.getClefs().add(cleftreble);
			measure1_1.getVoices().add(voice1_1_1);
			measure1_1.getChordSymbols().add(chordsymbol1);
		
		for (int i = 0; i < 10; i++) {
			stafftreble.getMeasures().add(measure1_1);
		}
		
		// piece
		getStaffs().add(stafftreble);
	}

	public static void main(String[] args) {
		new TestPiece();
	}
}