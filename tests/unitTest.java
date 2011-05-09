package tests;

import java.util.*;

import music.*;
import logic.Analyzer;
import tests.MidiAPITest;
import util.Edge;
import util.Graph;
import util.Node;

public class unitTest{
        public static void main(String args[]) {
        	
                KeySignature CMajor = new KeySignature(new Rational(4, 4), 0, true);
                KeySignature DMajor = new KeySignature(new Rational(4, 4), 2, true);
                Pitch C = MidiAPITest.createPitchFromMidiPitch(60);
                Pitch D = MidiAPITest.createPitchFromMidiPitch(62);
                Pitch E = MidiAPITest.createPitchFromMidiPitch(64);
                Pitch F = MidiAPITest.createPitchFromMidiPitch(65);
                Pitch G = MidiAPITest.createPitchFromMidiPitch(67);
                
                Analyzer analyzer = new Analyzer();
                List<List<Pitch>> melody = new ArrayList<List<Pitch>>();
                
                ArrayList<Pitch> melodyInstance1 = new ArrayList<Pitch>();
                melody.add(melodyInstance1);
                melodyInstance1.add(C);
                melodyInstance1.add(E);
                
                ArrayList<Pitch> melodyInstance2 = new ArrayList<Pitch>();
                melody.add(melodyInstance2);
                melodyInstance2.add(F);
                
                ArrayList<Pitch> melodyInstance3 = new ArrayList<Pitch>();
                melody.add(melodyInstance3);
                melodyInstance3.add(G);
                
                ArrayList<Pitch> melodyInstance4 = new ArrayList<Pitch>();
                melody.add(melodyInstance4);
                melodyInstance4.add(C);
                melodyInstance4.add(E);
               
                List<List<ChordSymbol>> allPossibleChords = analyzer.analyzeMelody(melody, CMajor);
                
                
                //testing testing

            	ChordSymbol c1 = new ChordSymbol(new ScaleDegree(1, Accidental.NATURAL), ChordType.MAJOR);
            	ChordSymbol c2 = new ChordSymbol(new ScaleDegree(6, Accidental.NATURAL), ChordType.GERAUG6);
            	System.out.println(c1.equals(c2));
            	
            	System.out.println(analyzer.getChordPreferencesGraph().findNode(c1).getValue());
            	//testing testing
                
                System.out.println("ChordProgressions Graph contains the following ChordSymbols: ");
                for(Node<ChordSymbol> toPrint : analyzer.getChordPreferencesGraph().getNodes()) {
                	
                	System.out.println(toPrint.getValue().getSymbolText());
                }
                System.out.println("graph is " + analyzer.getChordPreferencesGraph());
                
                //print out all possible chords
                int melodyNo = 0;
                for(List<ChordSymbol> melodyInstance : allPossibleChords) {
                	
                	System.out.println("melodyInstance " + melodyNo);
                	for(ChordSymbol chordsym : melodyInstance) {
                		
                		System.out.println(chordsym.toString());
                	}
                	
                	melodyNo++;
                }
                
                Graph<ChordSymbol> possibleProgressionsGraph = analyzer.createPossibleProgressionsGraph(allPossibleChords);
                printGraph(possibleProgressionsGraph);
        }
        
        //Function that prints the all the possible traversals from the starting node of printGraph
        public static void printGraph(Graph<ChordSymbol> printGraph) {
        	System.out.println("graph is " + printGraph);
        	Node<ChordSymbol> rootNode = printGraph.getStartingNode();
        	
        	List<Edge<ChordSymbol>> startingEdges = rootNode.getFollowing();
        	for(Edge<ChordSymbol> edge : startingEdges) {
        		
        		ArrayList<ChordSymbol> chordSymbolPrintList = new ArrayList<ChordSymbol>();
        		
        		Node<ChordSymbol> startingNode = edge.getBack();
        		chordSymbolPrintList.add(startingNode.getValue());
        		printGraphHelper(startingNode, chordSymbolPrintList);
        	}
        	
        }
        
        //Helper function for printGraph
        public static void printGraphHelper(Node<ChordSymbol> current, ArrayList<ChordSymbol> toPrint) {
        	
        	List<Edge<ChordSymbol>> followingEdges = current.getFollowing();
        	if(followingEdges.isEmpty()) {
        		
        		//Graph is thoroughly traversed, print List toPrint;
        		for(ChordSymbol chordsym : toPrint) {
        			
        			System.out.print(chordsym.getSymbolText() + " - ");
        		}
        		
        		System.out.println("");
        	}
        	else {
        		
        		for(int i = 0; i < followingEdges.size(); i++) {
        			
        			
        			Node<ChordSymbol> nextNode = followingEdges.get(i).getBack();
        			if(i > 0) {//not first element in the list

        				//clone the toPrint List
        				ArrayList<ChordSymbol> toPrintClone = (ArrayList<ChordSymbol>) toPrint.clone();
        				toPrintClone.add(nextNode.getValue());
        				
        				//recur
        				printGraphHelper(nextNode, toPrintClone);
        				
        			}
        			else {//first element in the list
        				
        				toPrint.add(nextNode.getValue());
        				
        				//recur
        				printGraphHelper(nextNode, toPrint);
        			}
        		}
        	}
        }
        
        
}