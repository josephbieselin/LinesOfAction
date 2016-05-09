/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package linesofaction_java;

import java.util.*;

/**
 *
 * @author joebi
 */
public class Player {
    // PRIVATE
    final private int value;
    
    private int numPieces = 6;
    final private List<Piece> pList;
    
    // Used for GUI purposes
    final private int P1_CODE = 1;
    final private int P2_CODE = 2;
    final String PLAYER1 = "x";
    final String PLAYER2 = "o";
    
    // PUBLIC
    
    // Constructor
    Player(int value) {
        this.value = value;
        pList = new ArrayList<>(numPieces);
    }
    
    // Copy Constructor
    Player(Player rhs) {
        this(rhs.getValue());
    }
    
    public int getValue() {
        return value;
    }
    
    public String getPlayerLetter() {
        String s = "";
        
        if (getValue() == P1_CODE) {
            s = PLAYER1;
        }
        else if(getValue() == P2_CODE) {
            s = PLAYER2;
        }
        
        return s;
    }
    
    // Removes the passed in Piece from pList and decrements numPieces
    public void removePiece(Piece p) {
        pList.remove(p);
        --numPieces;        
    }
    
    // Called numPieces times to initialize 
    public void addPiece(Piece p) {
        pList.add(p);
    }
    
    public void addPiece(Piece p, int index) {
        pList.add(index, p);
    }
    
    public boolean allConnected() {
        // all pieces are connected if there's only 1 left
        if (numPieces == 1)
            return true;
        
        List<Piece> connected = new ArrayList<>(numPieces);
        List<Piece> remaining = new ArrayList<>(numPieces);
        
        // connected only has 1 starting piece so far
        connected.add(pList.get(0));
        
        // remaining contains all the other pieces that must be checked
        for (int i = 1; i < numPieces; ++i) {
            remaining.add(pList.get(i));
        }
        
        return allConnected(connected, remaining);
    }
    
    private boolean allConnected(List<Piece> connected, List<Piece> remaining) {
        // remaining is empty (and thus all pieces were connected) so return true
        if (remaining.isEmpty())
            return true;
        
        // There's no more connected pieces to check so return false
        if (connected.isEmpty())
            return false;
        
        // get a Piece to check from connected
        Piece curPiece = connected.get(0);
        
        // add any adjacent Piece to curPiece to connected
        for (Piece p : remaining) {
            if (p.adjacent(curPiece)) {
                connected.add(p);
            }
        }
        
        // remove curPiece since we got any adjacent Piece to it
        connected.remove(curPiece);
        
        // any Piece found to be adjacent can be removed from remaining
        for (Piece p : connected) {
            remaining.remove(p);
        }
        
        // make a recursive call to determine connectedness
        return allConnected(connected, remaining);
    }
    
}
