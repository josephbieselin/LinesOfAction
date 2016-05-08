/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package linesofaction_java;

/**
 *
 * @author joebi
 */
public class Piece implements Comparable<Piece>{
    // PRIVATE
    private int x;
    private int y;
    private Player p;

    
    // PUBLIC
    
    // Constructor
    public Piece(int x, int y, Player p) {
        this.x = x;
        this.y = y;
        this.p = p;
    }
    
    // Copy Constructor
    public Piece(Piece rhs) {
//        this(rhs.getX(), rhs.getY(), new Player(rhs.getPlayer()));
        this(rhs.getX(), rhs.getY(), rhs.getPlayer());
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setX(int newX) {
        x = newX;
    }
    
    public void setY(int newY) {
        y = newY;
    }
    
    public void setPos(int newX, int newY) {
        setX(newX);
        setY(newY);
    }
    
    public Player getPlayer() {
        return p;
    }
    
    public int getPlayerValue() {
        return p.getValue();
    }
    
    // Return true if the two Pieces are adjacent to each other
    public boolean adjacent(Piece rhs) {
        if ( (x != rhs.x) && (x+1 != rhs.x) && (x-1 != rhs.x) ) {
            return false;
        }
        else if ( (y != rhs.y) && (y+1 != rhs.y) && (y-1 != rhs.y) ) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public int compareTo(Piece rhs) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        
        // Check for object equality
        if (this == rhs) {
            return EQUAL;
        }
        
        // Check for comparison between x, y values in objects
        if (x < rhs.x) {
            return BEFORE;
        }
        else if (x == rhs.x) {
            if (y < rhs.y) {
                return BEFORE;
            }
            else if (y == rhs.y) {
                return EQUAL;
            }
            else {
                return AFTER;
            }
        }
        else {
            return AFTER;
        }
    }
}
