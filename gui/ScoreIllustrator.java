package gui;

import arranger.ArrangerConstants;
import music.*;
import java.io.File;

import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.ListIterator;
import java.util.Map;
import java.util.HashMap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.IOException;

public class ScoreIllustrator {
	
	final static double LOG_2 = Math.log(2);
	
	final static int TOP_MARGIN	= 150;
	final static int LEFT_MARGIN	= 50;
	final static int RIGHT_MARGIN	= 50;
	final static int SYSTEM_LINE_SPACING = 10;
	final static int SYSTEM_SPACING = 80;
	
	final static int NOTE_WIDTH = SYSTEM_LINE_SPACING;
	final static int NOTE_HEIGHT = SYSTEM_LINE_SPACING;
	
	final static int MEASURE_WIDTH = 100;
	final static int LEDGER_WIDTH = (int) (SYSTEM_LINE_SPACING * 1.2);
	
	Image _imgQuarter, _imgHalf, _imgWhole, _imgRest,
			_imgDoubleFlat, _imgFlat, _imgNatural, _imgSharp, _imgDoubleSharp;
	
	// map each system to its y coordinate
	Map<Integer, Integer> _systemPositions;
	
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
		}
		catch (IOException e) {
			System.out.println("Error while loading musical images: " + e);
		}
	}
	
	public void drawPiece(Graphics g, Piece piece) {
		// list manuevering
		PriorityQueue<TimestampAssociator> pQueue = new PriorityQueue<TimestampAssociator>();
		
		//-------------------------listiters-----------------------
		// load structure
		List<KeySignature> keySigs 	= piece.getKeySignatures();
		List<TimeSignature> timeSigs 	= piece.getTimeSignatures();
		List<ChordSymbol> chords		= piece.getChordSymbols();
		
		ListIterator<KeySignature> keyIter 	= keySigs.listIterator();
		ListIterator<TimeSignature> timeIter = timeSigs.listIterator();
		ListIterator<ChordSymbol> chordIter = chords.listIterator();
		
		/*
		pQueue.add(new TimestampAssociator(keyIter));
		pQueue.add(new TimestampAssociator(timeIter));
		pQueue.add(new TimestampAssociator(chordIter));
		*/
		//-----------------------end listiters----------------------
		
		//-----------------------current data-----------------------
		// use map to find which staff each voice is on
		Map<ListIterator<? extends Timestep>, Staff> timestepStaff = new HashMap<ListIterator<? extends Timestep>, Staff>();
		// use map to find which clef each staff is currently using
		Map<Staff, Clef> currClefs = new HashMap<Staff, Clef>();
		
		KeySignature currKeySig 	= keySigs.get(0);
		TimeSignature currTimeSig 	= timeSigs.get(0);
		//ChordSymbol currChord		= chords.get(0);
		List<MultiNote> stemGroup = new ArrayList<MultiNote>();
		//---------------------end current data---------------------
		
		//------------------------load notes------------------------
		// add multinote lists from each staff's voice to pQueue
		List<Staff> staffs = piece.getStaffs();
		for (Staff st : staffs) {
			List<Voice> voices = st.getVoices();
			
			// handle clefs
			List<Clef> initClef = st.getClefs();
			ListIterator<Clef> clefIter = initClef.listIterator();
			timestepStaff.put(clefIter, st);
			
			//###pQueue.add(new TimestampAssociator(clefIter);
			
			// get first clef on each staff
			currClefs.put(st, initClef.get(0));
			
			for (Voice v : voices) {
				List<MultiNote> multis = v.getMultiNotes();
				ListIterator<MultiNote> multisList = multis.listIterator();
				
				// so we can get the staff a voice is on easily
				timestepStaff.put(multisList, st);
				pQueue.add(new TimestampAssociator(multisList));
			}
		}
		//----------------------end load notes----------------------
		
		// start drawing
		boolean startDrawing = true;
		int staffX	= LEFT_MARGIN;
		int nextY 	= TOP_MARGIN - SYSTEM_SPACING;
		int nextX 	= staffX;
		
		while (pQueue.size() > 0) {
			TimestampAssociator timeAssoc = pQueue.poll();
			ListIterator<? extends Timestep> currList = timeAssoc.getAssociated();
			
			Timestep currDur = null;
			if (currList.hasNext()) {
				currDur = (Timestep) currList.next();
				
				timeAssoc.addDuration(currDur);
				pQueue.add(timeAssoc);
			}
			else {
				// don't add back to priority queue
				continue;
			}
			
			int measureWidth = 100 * currTimeSig.getNumerator() / currTimeSig.getDenominator();
			// if extending into the margin, make a new line
			if (nextX + measureWidth > ArrangerConstants.WINDOW_WIDTH - RIGHT_MARGIN || startDrawing) {
				startDrawing = false;
				
				// if new line, draw systems
				nextX = LEFT_MARGIN;
				nextY += SYSTEM_SPACING;
				drawSystem(g, nextY);
			}
			
			// draw duration object
			if (currDur instanceof MultiNote) {
				//-----------------------MULTINOTE-----------------------
				MultiNote mnote = (MultiNote) currDur;
				
				Staff stf = timestepStaff.get(currList);
				Clef currClef = currClefs.get(stf);
				
				drawMultiNote(g, stemGroup, currClef, mnote, nextX, nextY);
				Rational dur = mnote.getDuration();
				
				// nextX should increase proportional to note length
				int noteWidth = (int) ((double) dur.getNumerator() / dur.getDenominator() * MEASURE_WIDTH);
				nextX += noteWidth;
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
				
			}
			else if (currDur instanceof TimeSignature) {
				//-----------------------TIME SIG-----------------------
				TimeSignature timeSig = (TimeSignature) currDur;
				currTimeSig = timeSig;
				
				// draw time sig
				
			}
			else if (currDur instanceof Clef) {
				//-------------------------CLEF-------------------------
				Clef clef = (Clef) currDur;
				drawClef(g, clef, nextX, nextY);
				nextX += 100; //clef image width
			}
			else {
				System.out.println("Unrecognized timestep: " + currDur);
			}
			
			staffX += measureWidth;
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
		
		// get current clef
		
		List<Pitch> pitches = mn.getPitches();
		for (Pitch p : pitches) {
			// add 4 since the third line is "number 0"
			int line = getLineNumber(currClef, p);
			
			int noteX = nextX;
			int noteY = -(line - 4) * SYSTEM_LINE_SPACING / 2 + nextY;
			
			// if too low or too high, draw ledger line
			if (line < -5 || line > 5)
				drawLedgerLine(g, noteX, noteY);
			
			drawNote(g, numerValue, denomValue, noteX, noteY);
		}
		
		// draw stem?
		
		if (denomValue >= 3) {
			stemGroup.add(mn);
		}
		else if (stemGroup.size() > 0) {
			// render previous group
			renderStemGroup(stemGroup);
		}
	}
	
	private void renderStemGroup(List<MultiNote> stemGroup) {
		Rational totalDuration = new Rational(0, 1);
		int totalLines = 0;
		
		for (int i = stemGroup.size() - 1; i >= 0; i++) {
			MultiNote mnote = stemGroup.get(i);
			
			// calc average (above center = stems downward, below center = stems upward)
			List<Pitch> pitches = mnote.getPitches();
			for (Pitch p : pitches) {
				//totalLines += getLineNumber(clef, p);
			}
			
			// calc total duration
			totalDuration = totalDuration.plus(mnote.getDuration());
		}
		
		// calc slope of stem bar (last - first) / totalDuration
		MultiNote first = stemGroup.get(0);
		MultiNote last = stemGroup.get(stemGroup.size() - 1);
		
		
		stemGroup = new ArrayList<MultiNote>();

	}
	
	private void drawNote(Graphics g, int numerValue, int denomValue, int xc, int yc) {
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
		//g.drawImage(
	}
	
	private void drawStem(Graphics g, int xc, int sy, int ey) {
		g.drawLine(xc, sy, xc, ey);
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
}
