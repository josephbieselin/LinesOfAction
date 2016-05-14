/*
Name:       Joseph Bieselin
Project:    Lines of Action (5x5 Board)
File Name:  PieceAndDir.java

Course:     CS-GY 6613: Artifical Intelligence

Description:
    - Struct used in GameWindow.java
    - Used to determine which Piece should be moved and in which direction
*/


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
public class PieceAndDir {
    // Struct variables
    public Piece p;
    public int dir;
    
    // constuctor
    PieceAndDir(Piece p, int d) {
        this.p = p;
        this.dir = d;
    }
}
