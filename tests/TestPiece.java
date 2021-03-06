package tests;

import music.*;

public class TestPiece extends Piece {

	public TestPiece() {
		super();

		// pitches
		Pitch cn4 = new Pitch(NoteLetter.C, 4, Accidental.NATURAL, false);
		Pitch dn4 = new Pitch(NoteLetter.D, 4, Accidental.NATURAL, false);
		Pitch en4 = new Pitch(NoteLetter.E, 4, Accidental.NATURAL, false);
		Pitch fs4 = new Pitch(NoteLetter.F, 4, Accidental.SHARP, false);
		Pitch gn4 = new Pitch(NoteLetter.G, 4, Accidental.NATURAL, false);

		Pitch an2 = new Pitch(NoteLetter.A, 2, Accidental.NATURAL, false);
		Pitch bf2 = new Pitch(NoteLetter.B, 2, Accidental.FLAT, false);
		Pitch bn2 = new Pitch(NoteLetter.B, 3, Accidental.NATURAL, false);
		Pitch cn3 = new Pitch(NoteLetter.C, 3, Accidental.NATURAL, false);

		// multinotes
		MultiNote treble1 = new MultiNote(new Rational(1, 2));
			treble1.getPitches().add(cn4);
		MultiNote treble2 = new MultiNote(new Rational(1, 4));
			treble2.getPitches().add(dn4);
		MultiNote treble3 = new MultiNote(new Rational(1, 8));
			treble3.getPitches().add(en4);
			treble3.getPitches().add(gn4);
		MultiNote treble4 = new MultiNote(new Rational(1, 8));
			treble4.getPitches().add(fs4);
		MultiNote treble5 = new MultiNote(new Rational(3, 4)); // rest

		MultiNote bass1 = new MultiNote(new Rational(1, 4));
			bass1.getPitches().add(cn3);
		MultiNote bass2 = new MultiNote(new Rational(1, 4));
			bass2.getPitches().add(bf2);
		MultiNote bass3 = new MultiNote(new Rational(1, 4));
			bass3.getPitches().add(an2);
		MultiNote bass4 = new MultiNote(new Rational(1, 4));
			bass4.getPitches().add(bn2);
		MultiNote bass5 = new MultiNote(new Rational(1, 2)); // rest

		// voices
		Voice voice1_1_1 = new Voice();				// staff 1, measure 1, voice 1
			voice1_1_1.getMultiNotes().add(treble1);
			voice1_1_1.getMultiNotes().add(treble2);
		Voice voice1_2_1 = new Voice();
			voice1_2_1.getMultiNotes().add(treble2);
			voice1_2_1.getMultiNotes().add(treble3);
			voice1_2_1.getMultiNotes().add(treble4);
			voice1_2_1.getMultiNotes().add(treble2);
		Voice voice1_3_1 = new Voice();
			voice1_3_1.getMultiNotes().add(treble5);
		Voice voice2_1_1 = new Voice();
			voice2_1_1.getMultiNotes().add(bass1);
			voice2_1_1.getMultiNotes().add(bass2);
			voice2_1_1.getMultiNotes().add(bass2);
		Voice voice2_2_1 = new Voice();
			voice2_2_1.getMultiNotes().add(bass3);
			voice2_2_1.getMultiNotes().add(bass4);
		Voice voice2_3_1 = new Voice();
			voice2_3_1.getMultiNotes().add(bass5);
			voice2_3_1.getMultiNotes().add(bass1);

		// clefs
		Clef cleftreble = new Clef(new Rational(3, 4), ClefName.GCLEF, -2);
		Clef clefbass = new Clef(new Rational(3, 4), ClefName.FCLEF, 2);

		// time signatures
		TimeSignature timesig1 = new TimeSignature(new Rational(3, 4), 3, 4);

		// key signatures
		KeySignature keysig1 = new KeySignature(new Rational(3, 4), 0, true);

		// chord symbols
		ChordSymbol chordsymbol1 = new ChordSymbol(new Rational(3, 4), new ScaleDegree(1, Accidental.NATURAL), ChordType.MAJOR);
		ChordSymbol chordsymbol2 = new ChordSymbol(new Rational(3, 4), new ScaleDegree(4, Accidental.NATURAL), ChordType.MINOR);
		ChordSymbol chordsymbol3 = new ChordSymbol(new Rational(3, 4), new ScaleDegree(5, Accidental.NATURAL), ChordType.HDIMIN7);

		// measures
		Measure measure1_1 = new Measure();
			measure1_1.getKeySignatures().add(keysig1);
			measure1_1.getTimeSignatures().add(timesig1);
			measure1_1.getClefs().add(cleftreble);
			measure1_1.getVoices().add(voice1_1_1);
			measure1_1.getChordSymbols().add(chordsymbol1);
		Measure measure1_2 = new Measure();
			measure1_2.getKeySignatures().add(keysig1);
			measure1_2.getTimeSignatures().add(timesig1);
			measure1_2.getClefs().add(cleftreble);
			measure1_2.getVoices().add(voice1_2_1);
			measure1_2.getChordSymbols().add(chordsymbol2);
		Measure measure1_3 = new Measure();
			measure1_3.getKeySignatures().add(keysig1);
			measure1_3.getTimeSignatures().add(timesig1);
			measure1_3.getClefs().add(cleftreble);
			measure1_3.getVoices().add(voice1_3_1);
			measure1_3.getChordSymbols().add(chordsymbol3);

		Measure measure2_1 = new Measure();
			measure2_1.getKeySignatures().add(keysig1);
			measure2_1.getTimeSignatures().add(timesig1);
			measure2_1.getClefs().add(clefbass);
			measure2_1.getVoices().add(voice2_1_1);
			measure2_1.getChordSymbols().add(chordsymbol1);
		Measure measure2_2 = new Measure();
			measure2_2.getKeySignatures().add(keysig1);
			measure2_2.getTimeSignatures().add(timesig1);
			measure2_2.getClefs().add(clefbass);
			measure2_2.getVoices().add(voice2_2_1);
			measure2_2.getChordSymbols().add(chordsymbol2);
		Measure measure2_3 = new Measure();
			measure2_3.getKeySignatures().add(keysig1);
			measure2_3.getTimeSignatures().add(timesig1);
			measure2_3.getClefs().add(clefbass);
			measure2_3.getVoices().add(voice2_3_1);
			measure1_3.getChordSymbols().add(chordsymbol3);

		// staffs
		Staff stafftreble = new Staff();
			stafftreble.getMeasures().add(measure1_1);
			stafftreble.getMeasures().add(measure1_2);
			stafftreble.getMeasures().add(measure1_3);
		Staff staffbass = new Staff();
			staffbass.getMeasures().add(measure2_1);
			staffbass.getMeasures().add(measure2_2);
			staffbass.getMeasures().add(measure2_3);

		// piece
		getStaffs().add(stafftreble);
		getStaffs().add(staffbass);
	}

	public static void main(String[] args) {
		new TestPiece();
	}
}