/*
Name:       Joseph Bieselin
Project:    Lines of Action (5x5 Board)
File Name:  Player.java

Course:     CS-GY 6613: Artifical Intelligence

Description:
    - Player object used to create a User and Computer Player in GameWindow.java
    - Every Player has an associated game shape that represent their Pieces on
      on the game board
    - Every Player starts out with a set number of positive Pieces on the board
    - A default and copy constructor have been defined
    - Associated getters and setters have been defined
    - adding and removing of Pieces from the Player's List of Pieces have been
      defined using the addPiece and removePiece functions
    - The "allConnected" function determines whether or not all the Pieces in a
      Player's List of Pieces are all adjacent to at least one other Piece in
      the List AND that they form a chain with no breaks in adjacent Pieces
*/


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
    
    private int numPieces = 0;
    private int startingPieces = 6;
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
        pList = new ArrayList<>(startingPieces);
//        
//        int i = 0;
//        while (i < startingPieces) {
//            addPiece(new Piece(0, i, this));
//        }
//        System.out.println(allConnected());
    }
    
    // Copy Constructor
    Player(Player rhs) {
        this(rhs.getValue());

        // Copy over some Pieces
        for (Piece p : rhs.pList) {
            addPiece(new Piece(p, this));
//            this.pList.add(new Piece(p, this));
        }
    }
    
    public int getValue() {
        return value;
    }
    
    public int getNumPieces() {
        return numPieces;
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
    
    public List<Piece> getPieces() {
        return pList;
    }
    
    // Removes the passed in Piece from pList and decrements numPieces
    public void removePiece(Piece p) {
        pList.get(pList.indexOf(p)).setRemoved(true);
        --numPieces;
    }
    
    // Called numPieces times to initialize 
    public void addPiece(Piece p) {
        // Piece not already in list
        if (pList.indexOf(p) == -1) {
            pList.add(p);
        }
        // Piece is in list already
        else {
            pList.get(pList.indexOf(p)).setRemoved(false);
        }
        
        ++numPieces;
    }
    
    public boolean allConnected() {
        // all pieces are connected if there's only 1 left
        if (numPieces <= 1)
            return true;
        
        List<Piece> connected = new ArrayList<>(numPieces);
        List<Piece> remaining = new ArrayList<>(numPieces);
        
        Piece p;
        
        int i = 0;
        while (i < pList.size()) {
            p = pList.get(i);
            ++i;
            if (!p.isRemoved()) {
                connected.add(p);
                break;
            }
        }
        // connected only has 1 starting piece so far
        
        // remaining contains all the other pieces that must be checked
        while (i < pList.size()) {
            p = pList.get(i);
            ++i;
            if (!p.isRemoved()) {
                remaining.add(p);
            }
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
