package gui;

import arranger.ArrangerConstants;
import music.*;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;

import java.awt.Point;

import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ScoreIllustrator {

	final static double LOG_2 = Math.log(2);

	final static int TOP_MARGIN	= 150;
	final static int LEFT_MARGIN	= 50;
	final static int RIGHT_MARGIN	= 50;

	final static int SYSTEM_SPACING = 90;
	final static int SYSTEM_LINE_SPACING = 10;
	
	final static int TIMESIG_WIDTH = 30;
	final static int KEYSIG_WIDTH = 30;
	final static int CLEF_WIDTH = 50;
	
	final static int STAFF_SPACING = 70;
	
	final static int NOTE_WIDTH = SYSTEM_LINE_SPACING;
	final static int NOTE_HEIGHT = SYSTEM_LINE_SPACING;

	final static int STEM_LENGTH = 30;

	final static int MEASURE_WIDTH = 100;
	final static int LEDGER_WIDTH = (int) (SYSTEM_LINE_SPACING * 1.5);

	Image _imgQuarter, _imgHalf, _imgWhole, _imgRest,
			_imgDoubleFlat, _imgFlat, _imgNatural, _imgSharp, _imgDoubleSharp,
			_imgClefG, _imgClefF, _imgClefC;

	// map each system to its y coordinate
	List<Integer> _systemPositions;

	public ScoreIllustrator() {
		// load images
		try {
			_imgQuarter		= ImageIO.read(new File("images/score/score_quarter.gif"));
			_imgHalf			= ImageIO.read(new File("images/score/score_half.gif"));
			_imgWhole	 	= ImageIO.read(new File("images/score/score_whole.gif"));
			_imgRest			= ImageIO.read(new File("images/score/score_rest.gif"));

			_imgDoubleFlat	= ImageIO.read(new File("images/score/score_dflat.gif"));
			_imgFlat			= ImageIO.read(new File("images/score/score_flat.gif"));
			_imgNatural	 	= ImageIO.read(new File("images/score/score_natural.gif"));
			_imgSharp		= ImageIO.read(new File("images/score/score_sharp.gif"));
			_imgDoubleSharp= ImageIO.read(new File("images/score/score_dsharp.gif"));

			_imgClefG		= ImageIO.read(new File("images/score/score_clef_g.png"));
			_imgClefF		= ImageIO.read(new File("images/score/score_clef_f.png"));
			_imgClefC		= ImageIO.read(new File("images/score/score_clef_c.png"));
		}
		catch (IOException e) {
			System.out.println("Error while loading musical images: " + e);
		}
	}

	public void drawPiece(Graphics g, Piece piece) {
		
		// used to draw things from left to right
		TreeMap<Timestamp, ListIterator<? extends Timestep>> timeline = new TreeMap<Timestamp, ListIterator<? extends Timestep>>();
		
		// positions
		_systemPositions = new ArrayList<Integer>();
		Map<Staff, Integer> staffPositions = new HashMap<Staff, Integer>();
		
		// the staff each object is in
		Map<ListIterator<Measure>, Staff> measureStaffs = new HashMap<ListIterator<Measure>, Staff>();
		Map<ListIterator<? extends Timestep>, Staff> timestepStaff = new HashMap<ListIterator<? extends Timestep>, Staff>();
		
		List<Clef> clefs;
		List<KeySignature> keySigs;
		List<TimeSignature> timeSigs;
		List<ChordSymbol> chords;
		
		ListIterator<Clef> clefIter;
		ListIterator<KeySignature> keyIter;
		ListIterator<TimeSignature> timeIter;
		ListIterator<ChordSymbol> chordIter;
		
		KeySignature currKeySig 	= null;
		TimeSignature currTimeSig 	= null;
		Map<Staff, List<MultiNote>> stemGroups = new HashMap<Staff, List<MultiNote>>();
		
		// use map to find which clef each staff is currently using
		Map<Staff, Clef> currClefs = new HashMap<Staff, Clef>();
		
		// list of measures in each staff
		List<ListIterator<Measure>> measureLists = new ArrayList<ListIterator<Measure>>();
		// current horizontal positions within each staff
		Map<Staff, Integer> staffX = new HashMap<Staff, Integer>();
		
		for (Staff staff : piece.getStaffs()) {
			List<Measure> staffMeasures = staff.getMeasures();
			ListIterator<Measure> measureIterator = staffMeasures.listIterator();
			
			measureLists.add(measureIterator);
			measureStaffs.put(measureIterator, staff);
			staffPositions.put(staff, staffPositions.size());
			stemGroups.put(staff, new ArrayList<MultiNote>());
		}
		
		while (!measureLists.isEmpty()) {
			// set up lists within each list of measures
			for (int i = 0; i < measureLists.size(); i++) {
				ListIterator<Measure> measureIter = measureLists.get(i);
				Measure m = measureIter.next();
				
				Staff staff = measureStaffs.get(measureIter);
				staffX.put(staff, LEFT_MARGIN);
				
				// if measure list is empty, don't add back to list
				if (!measureIter.hasNext()) {
					measureLists.remove(i);
					i--;
				}
				
				// get all the lists within each measure
				//-------------------------listiters-----------------------
				// load structure
				clefs		= m.getClefs();
				keySigs 	= m.getKeySignatures();
				timeSigs	= m.getTimeSignatures();
				chords		= m.getChordSymbols();
				
				clefIter	= clefs.listIterator();
				keyIter 	= keySigs.listIterator();
				timeIter	= timeSigs.listIterator();
				chordIter	= chords.listIterator();
				
				timeline.put(new Timestamp(Clef.class), clefIter);
				timeline.put(new Timestamp(KeySignature.class), keyIter);
				timeline.put(new Timestamp(TimeSignature.class), timeIter);
				timeline.put(new Timestamp(ChordSymbol.class), chordIter);
				
				// keep track of which staff each list is on
				timestepStaff.put(clefIter, staff);
				timestepStaff.put(keyIter, staff);
				timestepStaff.put(timeIter, staff);
				timestepStaff.put(chordIter, staff);
				//-----------------------end listiters----------------------
				List<Voice> voices = m.getVoices();
				
				for (Voice v : voices) {
					List<MultiNote> multis = v.getMultiNotes();
					ListIterator<MultiNote> multisList = multis.listIterator();
					
					timeline.put(new Timestamp(MultiNote.class), multisList);
					timestepStaff.put(multisList, staff);
				}
				
			}
			
			boolean startDrawing = true;
			int systemY	= TOP_MARGIN - SYSTEM_SPACING;
			int numStaffs = piece.getStaffs().size();
			
			while (timeline.size() > 0) {
				Timestamp timestamp = timeline.firstKey();
				ListIterator<? extends Timestep> currList = timeline.get(timestamp);
				
				if (currList == null) {
					System.out.println("There was an empty list of class: " + timestamp.getAssocClass());
					System.exit(1);
				}
				
				Timestep currDur = null;
				if (currList.hasNext()) {
					currDur = currList.next();
					timeline.remove(timestamp);
					timestamp.addDuration(currDur);
					timeline.put(timestamp, currList);
				}
				else {
					// don't add back to priority queue
					timeline.remove(timestamp);
					continue;
				}
				
				Staff currStaff = timestepStaff.get(currList);
				
				int nextX = staffX.get(currStaff);
				
				//int measureWidth = 100 * currTimeSig.getNumerator() / currTimeSig.getDenominator();
				// if extending into the margin, make a new line
				if (nextX > ArrangerConstants.WINDOW_WIDTH - RIGHT_MARGIN || startDrawing) {
					startDrawing = false;
					
					// if new line, draw systems
					//nextX = LEFT_MARGIN;
					staffX.put(currStaff, LEFT_MARGIN);
					systemY += SYSTEM_SPACING;
					
					_systemPositions.add(systemY);
					for (int i = 0; i < numStaffs; i++){
						drawSystem(g, systemY + i * STAFF_SPACING);
					}
				}
				
				int nextY = systemY + staffPositions.get(currStaff) * STAFF_SPACING;;
				
				// draw duration object
				if (currDur instanceof MultiNote) {
					//-----------------------MULTINOTE-----------------------
					MultiNote mnote = (MultiNote) currDur;
					Clef currClef = currClefs.get(currStaff);
					
					int noteX = nextX;
					int noteY = nextY;
					
					drawMultiNote(g, stemGroups.get(currStaff), currClef, mnote, noteX, noteY);
					Rational dur = mnote.getDuration();
					
					// nextX should increase proportional to note length
					int noteWidth = (int) ((double) dur.getNumerator() / dur.getDenominator() * MEASURE_WIDTH);
					staffX.put(currStaff, nextX + noteWidth);
				}
				else if (currDur instanceof ChordSymbol) {
					//---------------------CHORD SYMBOL----------------------
					ChordSymbol cSymbol = (ChordSymbol) currDur;
					
				}
				else if (currDur instanceof KeySignature) {
					//-----------------------KEY SIG-----------------------
					KeySignature keySig = (KeySignature) currDur;
					currKeySig = keySig;
					
					// draw key sig
					drawAccidental(g, Accidental.SHARP, nextX, nextY);
					staffX.put(currStaff, nextX + KEYSIG_WIDTH);
				}
				else if (currDur instanceof TimeSignature) {
					//-----------------------TIME SIG-----------------------
					TimeSignature timeSig = (TimeSignature) currDur;
					currTimeSig = timeSig;
					
					// draw time sig
					g.setFont(new Font("Arial", 0, 24));
					g.drawString("" + timeSig.getNumerator(), nextX, nextY + 1 * SYSTEM_LINE_SPACING);
					g.drawString("" + timeSig.getDenominator(), nextX, nextY + 3 * SYSTEM_LINE_SPACING);
					staffX.put(currStaff, nextX + TIMESIG_WIDTH);
				}
				else if (currDur instanceof Clef) {
					//-------------------------CLEF-------------------------
					System.out.println(nextY);
					Clef clef = (Clef) currDur;
					currClefs.put(currStaff, clef);
					
					drawClef(g, clef, nextX, nextY);
					staffX.put(currStaff, nextX + CLEF_WIDTH);
				}
				else {
					System.out.println("Unrecognized timestep: " + currDur);
				}
			}
		}
	}

	/* Draws all pitches within the multinote
	 *
	 */
	private void drawMultiNote(Graphics g, List<MultiNote> stemGroup, Clef currClef, MultiNote mn, int nextX, int nextY) {
		Rational dur = mn.getDuration();

		int numer = dur.getNumerator();
		int denom = dur.getDenominator();

		int numerValue = (int) (Math.log(numer) / LOG_2);
		int denomValue = (int) (Math.log(denom) / LOG_2);

		List<Pitch> pitches = mn.getPitches();
		if (pitches.size() == 0)
			return;

		int minLine, maxLine;
		minLine = maxLine = getLineNumber(currClef, pitches.get(0));
		
		if (pitches.size() == 0) {
			// draw rest
			drawRest(g, numerValue, denomValue, nextX, nextY);
		}
		
		for (Pitch p : pitches) {
			// add 4 since the third line is "number 0"
			int line = getLineNumber(currClef, p);
			minLine = Math.min(minLine, line);
			maxLine = Math.max(maxLine, line);

			int noteX = nextX;
			int noteY = getLineOffset(currClef, line) + nextY;

			// if too low or too high, draw ledger line
			if (line < -5 || line > 5)
				drawLedgerLine(g, noteX, noteY);
			
			drawPitch(g, numerValue, denomValue, noteX, noteY);
		}

		// draw stem or add to stem group
		if (denomValue >= 3) {
			stemGroup.add(mn);
		}
		else {
			if (denomValue != 0) {
				// don't draw stem for whole notes
				int minOffset = getLineOffset(currClef, minLine);
				int maxOffset = getLineOffset(currClef, maxLine);

				int stemX = nextX;
				if (minLine + maxLine <= 0) {
					// upwards stem
					maxOffset -= STEM_LENGTH;
					stemX += NOTE_WIDTH / 2;
				}
				else {
					// downwards stem
					minOffset += STEM_LENGTH;
					stemX -= NOTE_WIDTH / 2;
				}

				drawStem(g, stemX, nextY, minOffset, maxOffset);
			}

			if (stemGroup.size() > 0) {
				// render previous group
				renderStemGroup(stemGroup);
			}
		}
	}
	
	private void drawRest(Graphics g, int numerValue, int denomValue, int xc, int yc) {
		// draw rest
		
	}
	
	private void drawStem(Graphics g, int xc, int yc, int minOffset, int maxOffset) {
		g.drawLine(xc, yc + minOffset, xc, yc + maxOffset);
	}

	private void renderStemGroup(List<MultiNote> stemGroup) {
		if (stemGroup.size() <= 1) {
			// not actually a group
			stemGroup.clear();
			return;
		}
		/*
		Rational totalDuration = new Rational(0, 1);
		int totalLines = 0;

		for (int i = stemGroup.size() - 1; i >= 0; i--) {
			MultiNote mnote = stemGroup.get(i);

			// calc average (above center = stems downward, below center = stems upward)
			List<Pitch> pitches = mnote.getPitches();
			for (Pitch p : pitches) {
				totalLines += getLineNumber(clef, p);
			}

			// calc total duration
			totalDuration = totalDuration.plus(mnote.getDuration());
		}

		// -1 = upwards, 1 = downwards
		boolean stemDirection = (totalLines >= 0) ? -1 : 1;

		// calc slope of stem bar (last - first) / totalDuration
		MultiNote first = stemGroup.get(0);
		MultiNote last = stemGroup.get(stemGroup.size() - 1);

		double slope = (last.getY() - first.getY()) / (last.getX() - first.getX())
		double maxBarOffset = 0;

		// also calc how much to offset the bar vertically
		for (MultiNote mn : stemGroup) {
			int dx = mn.getX() - first.getX();
			int expectedY = first.getY() + dx * slope;
			maxBarOffset = Math.max(maxBarOffset, expectedY);

			int stemX = mn.getX();
			g.drawLine(stemX, mn.getY(), );
		}

		// draw stems and additional bars next to first bar (for 16th, etc)
		for (MultiNote mn : stemGroup) {

		}

		int barSX = first.getX();
		int barSY = first.getY() + stemDirection * (stemLength + maxBarOffset);
		int barEX = last.getX();
		int barEY = last.getX() + stemDirection * (stemLength + maxBarOffset);
		g.drawLine(barSX, barSY, barEX, barEY);
		*/
		//stemGroup = new ArrayList<MultiNote>();
		stemGroup.clear();
	}

	private void drawPitch(Graphics g, int numerValue, int denomValue, int xc, int yc) {
		// draw circle on the correct line
		//g.drawImage(_imgQuarter, xc, yc, null);

		if (numerValue == 0) {
			// note is a base note (eighth, quarter, half, etc)
			drawBaseNoteHead(g, denomValue, xc, yc);
		}
		else {
			// note is a base note + dots
			int base = numerValue - numerValue / 2;
			drawBaseNoteHead(g, base, xc, yc);

			// draw dots
			int dots = numerValue - 3;

		}
	}

	/*	Draws the note head for a base value (numerator is 1)
	 *
	 */
	private void drawBaseNoteHead(Graphics g, int denomValue, int xc, int yc) {

		if (denomValue < 3) {
			switch (denomValue) {
			case 0:
				// whole note
				dynamicDrawNoteHead(g, xc, yc, true);
				break;

			case 1:
				// half note
				dynamicDrawNoteHead(g, xc, yc, true);
				break;

			case 2:
				// quarter note
				dynamicDrawNoteHead(g, xc, yc, false);
				break;
			default:

			}
		}
		else {
			// eighth or smaller
			dynamicDrawNoteHead(g, xc, yc, false);
		}
	}

	private void dynamicDrawNoteHead(Graphics g, int xc, int yc, boolean whiteFill) {
		int sx = xc - NOTE_WIDTH / 2;
		int sy = yc - NOTE_HEIGHT / 2;

		if (whiteFill)
			g.setColor(Color.WHITE);

		g.fillOval(sx, sy, NOTE_WIDTH, NOTE_HEIGHT);

		if (whiteFill){
			g.setColor(Color.BLACK);
			g.drawOval(sx, sy, NOTE_WIDTH, NOTE_HEIGHT);
		}
	}

	private void drawSystem(Graphics g, int yc) {
		for (int i = 0; i < 5; i++) {
			int yp = yc + i * SYSTEM_LINE_SPACING;
			g.drawLine(LEFT_MARGIN, yp, ArrangerConstants.WINDOW_WIDTH - RIGHT_MARGIN, yp);
		}
	}

	private void drawClef(Graphics g, Clef c, int xc, int yc) {
		switch (c.getClefName()) {
		case GCLEF:
			g.drawImage(_imgClefG, xc, yc, null);
			break;
		case FCLEF:
			g.drawImage(_imgClefF, xc, yc, null);
			break;
		case CCLEF:
			g.drawImage(_imgClefC, xc, yc, null);
			break;
		}
	}

	private void drawLedgerLine(Graphics g, int xc, int yc) {
		g.drawLine(xc - LEDGER_WIDTH / 2, yc, xc + LEDGER_WIDTH / 2, yc);
	}

	private void drawAccidental(Graphics g, Accidental accid, int xc, int yc) {
		Image accidImage = _imgNatural;

		switch (accid) {
			case DOUBLEFLAT:
				accidImage = _imgDoubleFlat;
				break;
			case FLAT:
				accidImage = _imgFlat;
				break;
			case NATURAL:
				accidImage = _imgNatural;
				break;
			case SHARP:
				accidImage = _imgSharp;
				break;
			case DOUBLESHARP:
				accidImage = _imgDoubleSharp;
				break;
			default:
				System.out.println("Accidental " + accid);
				System.exit(1);
				break;
		}

		g.drawImage(accidImage, xc, yc, null);
	}

	// getLineNumber takes in a pitch and returns an int representing the pitch's line number.
	public int getLineNumber(Clef c, Pitch pitch) {
		int centerValue = c.getCenterValue();
		int pitchValue = pitch.getNoteLetter().intValue() + pitch.getOctave() * 7;
		return pitchValue - centerValue + c.getCenterLine();
	}

	public int getLineOffset(Clef c, int line) {
		// - 4 since
		return -(line - 4) * SYSTEM_LINE_SPACING / 2;
	}
}
