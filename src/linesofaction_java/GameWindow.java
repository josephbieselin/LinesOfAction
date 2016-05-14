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
public class GameWindow extends javax.swing.JFrame {

    // PRIVATE OBJECT VARIABLES
    private Player P1;
    private Player P2;
    
    // Who's turn is it?
    private Player currentPlayer;
    // Is userPlayer the Black (P1) or White (P2) Player?
    private Player userPlayer;
    // compPlayer is the opposite of userPlayer
    private Player compPlayer;
    // winner is the Player who gets all their Pieces connected
    private Player winner;
    
    // Is the game over?
    private boolean gameOver;
    
    // The Game Board is a square matrix of boardSize-by-boardSize made up of Piece objects
    final public int boardSize = 5;
    private final Piece gameBoard[][];
    // The Game Board gets user input through JToggleButton objects
    private final javax.swing.JToggleButton buttons[][];
    
    // What is the current piece chosen to potentially move?
    private Piece selectedPiece;
    
    // What is the position the Player wants to move selectedPiece to
    private int newX = -1; // -1 indicates no new position yet
    private int newY = -1; // -1 indicates no new position yet
            
    // Codes to determine how a move needs to be checked
    private final int ROW = 1;
    private final int COLUMN = 2;
    private final int LR_DIAG = 3;
    private final int RL_DIAG = 4;    
    
    /* ALPHA-BETA SEARCH ALGORITHM variables */
    // Temporary Players used to check new states of the game
    private Player tempCompPlayer;
    private Player tempUserPlayer;
    // Temporary board
    private Piece tempGameBoard[][];
    // Utility values for Alpha-Beta Search Algorithm
    private final int MAX = 100;
    private final int MIN = -100;
    private final int DRAW = 0;
    // Seen states in the search to not recurse over
    private Map<Integer, List<String>> seenStates;
    // Variable/Codes for AI Movement
    private final int numMoves = 8; // up, right, down, left, diagonally in 4 ways
    private final int UP            = 0;
    private final int UP_RIGHT      = 1;
    private final int RIGHT         = 2;
    private final int DOWN_RIGHT    = 3;
    private final int DOWN          = 4;
    private final int DOWN_LEFT     = 5;
    private final int LEFT          = 6;
    private final int UP_LEFT       = 7;
    // Max depth and time to traverse in Alpha-Beta Search
    private final int MAX_DEPTH = 4000;
    private final long MAX_TIME = 10; // 10 seconds
    private long startTime;
    // List that stores Pieces overtaken in MIN/MAX calls
    private List<Piece> removedPieces;
    
    // Values to show in Alpha-Beta Search call
    private long depth;
    private long nodesGenerated;
    private long evalCalledMax;
    private long evalCalledMin;
    private long pruningMax;
    private long pruningMin;
    
    // END OF OBJECT VARIABLES
    
    
    
    /**
     * Creates new form GameWindow
     */
    public GameWindow() {
        initComponents();
        
        // Initialize Player 1 and 2 and set currentPlayer to P1
        P1 = new Player(1);
        P2 = new Player(2);
        currentPlayer = P1;
        
        // User has not selected which Player to be yet
        userPlayer = null;
        compPlayer = null;   
        
        // Game is not over initially
        gameOver = false;
        
        // Initialize the 2D board with null
        gameBoard = new Piece[boardSize][boardSize];
        buttons = new javax.swing.JToggleButton[boardSize][boardSize];
        
        for (int i = 0; i < boardSize; ++i)
            for (int j = 0; j < boardSize; ++j)
                gameBoard[i][j] = null;
        

        // Store JToggleButtons into the buttons array
        setButtons();
        
        // Initialize game Pieces to the board and respective Players
        setPieces();
        
        // No Piece on the board has been selected yet
        selectedPiece = null;
    }

    // Set Pieces on the board and to the Players according to Lines of Actions rules
    private void setPieces() {
        Piece tempPiece;
        
        for (int i = 1; i < (boardSize - 1); ++i) {
            /* Player 1 - Black Player (x on the board) */
            // Top Row
            tempPiece = new Piece(0, i, P1);
            gameBoard[0][i] = tempPiece;
            P1.addPiece(tempPiece);
            buttons[0][i].setText(tempPiece.getPlayer().getPlayerLetter());
            // Bottom Row
            tempPiece = new Piece(boardSize - 1, i, P1);
            gameBoard[boardSize - 1][i] = tempPiece;
            P1.addPiece(tempPiece);
            buttons[boardSize - 1][i].setText(tempPiece.getPlayer().getPlayerLetter());
            
            /* Player 2 - White Player (o on the board) */
            // Left Column
            tempPiece = new Piece(i, 0, P2);
            gameBoard[i][0] = tempPiece;
            P2.addPiece(tempPiece);
            buttons[i][0].setText(tempPiece.getPlayer().getPlayerLetter());
            // Right Column
            tempPiece = new Piece(i, boardSize - 1, P2);
            gameBoard[i][boardSize - 1] = tempPiece;
            buttons[i][boardSize - 1].setText(tempPiece.getPlayer().getPlayerLetter());
            P2.addPiece(tempPiece);
        }
        
    }
    
    
    
    
    
    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    //                                                                //
    /*   ADD ONTO THIS FUNCTION WHENEVER  THE BOARD SIZE IS CHANGED   */
    //                                                                //
    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    // Set JToggleButtons on the board
    private void setButtons() {
        buttons[0][0] = BOARD_0_0;
        buttons[0][1] = BOARD_0_1;
        buttons[0][2] = BOARD_0_2;
        buttons[0][3] = BOARD_0_3;
        buttons[0][4] = BOARD_0_4;
        buttons[1][0] = BOARD_1_0;
        buttons[1][1] = BOARD_1_1;
        buttons[1][2] = BOARD_1_2;
        buttons[1][3] = BOARD_1_3;
        buttons[1][4] = BOARD_1_4;
        buttons[2][0] = BOARD_2_0;
        buttons[2][1] = BOARD_2_1;
        buttons[2][2] = BOARD_2_2;
        buttons[2][3] = BOARD_2_3;
        buttons[2][4] = BOARD_2_4;
        buttons[3][0] = BOARD_3_0;
        buttons[3][1] = BOARD_3_1;
        buttons[3][2] = BOARD_3_2;
        buttons[3][3] = BOARD_3_3;
        buttons[3][4] = BOARD_3_4;
        buttons[4][0] = BOARD_4_0;
        buttons[4][1] = BOARD_4_1;
        buttons[4][2] = BOARD_4_2;
        buttons[4][3] = BOARD_4_3;
        buttons[4][4] = BOARD_4_4;
        
        // initialized board does not have Players yet so disable buttons
        disableButtons();
    }
    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    //                                                                //
    /*   ADD ONTO THIS FUNCTION WHENEVER  THE BOARD SIZE IS CHANGED   */
    //                                                                //
    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    
    
    
    
    
    // Disable all JToggleButtons on the board
    private void disableButtons() {
        for (int i = 0; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {
                buttons[i][j].setEnabled(false);
            }
        }
        
        END_TURN_BUTTON.setEnabled(false);
    }
    
    // Enable all JToggleButtons on the board
    private void enableButtons() {
        for (int i = 0; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {
                buttons[i][j].setEnabled(true);
            }
        }
        
        END_TURN_BUTTON.setEnabled(true);
    }    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        X_BUTTON = new javax.swing.JButton();
        O_BUTTON = new javax.swing.JButton();
        MAX_DEPTH_GAME_TREE_REACHED_TEXT = new javax.swing.JTextField();
        TOTAL_NUMBER_NODES_GENERATED_IN_TREE_TEXT = new javax.swing.JTextField();
        TIMES_EVALUATION_FUNCTION_CALLED_IN_MAX_TEXT = new javax.swing.JTextField();
        TIMES_EVALUATION_FUNCTION_CALLED_IN_MIN_TEXT = new javax.swing.JTextField();
        TIMES_PRUNING_OCCURRED_IN_MAX_TEXT = new javax.swing.JTextField();
        TIMES_PRUNING_OCCURRED_IN_MIN_TEXT = new javax.swing.JTextField();
        BOARD_0_1 = new javax.swing.JToggleButton();
        BOARD_0_2 = new javax.swing.JToggleButton();
        BOARD_0_3 = new javax.swing.JToggleButton();
        BOARD_0_4 = new javax.swing.JToggleButton();
        BOARD_1_1 = new javax.swing.JToggleButton();
        BOARD_1_2 = new javax.swing.JToggleButton();
        BOARD_1_3 = new javax.swing.JToggleButton();
        BOARD_2_0 = new javax.swing.JToggleButton();
        BOARD_3_0 = new javax.swing.JToggleButton();
        BOARD_4_0 = new javax.swing.JToggleButton();
        BOARD_2_1 = new javax.swing.JToggleButton();
        BOARD_2_2 = new javax.swing.JToggleButton();
        BOARD_2_3 = new javax.swing.JToggleButton();
        BOARD_2_4 = new javax.swing.JToggleButton();
        BOARD_3_1 = new javax.swing.JToggleButton();
        BOARD_3_2 = new javax.swing.JToggleButton();
        BOARD_3_3 = new javax.swing.JToggleButton();
        BOARD_3_4 = new javax.swing.JToggleButton();
        BOARD_4_1 = new javax.swing.JToggleButton();
        BOARD_4_2 = new javax.swing.JToggleButton();
        BOARD_4_3 = new javax.swing.JToggleButton();
        BOARD_4_4 = new javax.swing.JToggleButton();
        BOARD_0_0 = new javax.swing.JToggleButton();
        BOARD_1_0 = new javax.swing.JToggleButton();
        BOARD_1_4 = new javax.swing.JToggleButton();
        END_TURN_BUTTON = new javax.swing.JButton();
        DISPLAY_WINNER_TEXT_FIELD = new javax.swing.JLabel();
        MAX_DEPTH_GAME_TREE_REACHED_TEXT_FIELD = new javax.swing.JLabel();
        TOTAL_NUMBER_NODES_GENERATED_IN_TREE_TEXT_FIELD = new javax.swing.JLabel();
        TIMES_EVALUATION_FUNCTION_CALLED_IN_MAX_TEXT_FIELD = new javax.swing.JLabel();
        TIMES_EVALUATION_FUNCTION_CALLED_IN_MIN_TEXT_FIELD = new javax.swing.JLabel();
        TIMES_PRUNING_OCCURRED_IN_MAX_TEXT_FIELD = new javax.swing.JLabel();
        TIMES_PRUNING_OCCURRED_IN_MIN_TEXT_FIELD = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        X_BUTTON.setText("1 - x");
        X_BUTTON.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                X_BUTTONMouseClicked(evt);
            }
        });
        X_BUTTON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                X_BUTTONActionPerformed(evt);
            }
        });

        O_BUTTON.setText("2 - o");
        O_BUTTON.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                O_BUTTONMouseClicked(evt);
            }
        });

        MAX_DEPTH_GAME_TREE_REACHED_TEXT.setText("Max Depth of Game Tree Reached");
        MAX_DEPTH_GAME_TREE_REACHED_TEXT.setEnabled(false);

        TOTAL_NUMBER_NODES_GENERATED_IN_TREE_TEXT.setText("Total # of Nodes Generated in Tree");
        TOTAL_NUMBER_NODES_GENERATED_IN_TREE_TEXT.setEnabled(false);
        TOTAL_NUMBER_NODES_GENERATED_IN_TREE_TEXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TOTAL_NUMBER_NODES_GENERATED_IN_TREE_TEXTActionPerformed(evt);
            }
        });

        TIMES_EVALUATION_FUNCTION_CALLED_IN_MAX_TEXT.setText("# of Times Evalution Function called in MAX-VALUE");
        TIMES_EVALUATION_FUNCTION_CALLED_IN_MAX_TEXT.setEnabled(false);

        TIMES_EVALUATION_FUNCTION_CALLED_IN_MIN_TEXT.setText("# of Times Evalution Function called in MIN-VALUE");
        TIMES_EVALUATION_FUNCTION_CALLED_IN_MIN_TEXT.setEnabled(false);

        TIMES_PRUNING_OCCURRED_IN_MAX_TEXT.setText("# of Times Pruning Occurred in MAX-VALUE");
        TIMES_PRUNING_OCCURRED_IN_MAX_TEXT.setEnabled(false);
        TIMES_PRUNING_OCCURRED_IN_MAX_TEXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TIMES_PRUNING_OCCURRED_IN_MAX_TEXTActionPerformed(evt);
            }
        });

        TIMES_PRUNING_OCCURRED_IN_MIN_TEXT.setText("# of Times Pruning Occurred in MIN-VALUE");
        TIMES_PRUNING_OCCURRED_IN_MIN_TEXT.setEnabled(false);

        BOARD_0_1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_0_1MouseClicked(evt);
            }
        });

        BOARD_0_2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_0_2MouseClicked(evt);
            }
        });

        BOARD_0_3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_0_3MouseClicked(evt);
            }
        });

        BOARD_0_4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_0_4MouseClicked(evt);
            }
        });

        BOARD_1_1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_1_1MouseClicked(evt);
            }
        });

        BOARD_1_2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_1_2MouseClicked(evt);
            }
        });

        BOARD_1_3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_1_3MouseClicked(evt);
            }
        });

        BOARD_2_0.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_2_0MouseClicked(evt);
            }
        });

        BOARD_3_0.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_3_0MouseClicked(evt);
            }
        });

        BOARD_4_0.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_4_0MouseClicked(evt);
            }
        });

        BOARD_2_1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_2_1MouseClicked(evt);
            }
        });

        BOARD_2_2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_2_2MouseClicked(evt);
            }
        });

        BOARD_2_3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_2_3MouseClicked(evt);
            }
        });

        BOARD_2_4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_2_4MouseClicked(evt);
            }
        });

        BOARD_3_1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_3_1MouseClicked(evt);
            }
        });

        BOARD_3_2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_3_2MouseClicked(evt);
            }
        });

        BOARD_3_3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_3_3MouseClicked(evt);
            }
        });

        BOARD_3_4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_3_4MouseClicked(evt);
            }
        });

        BOARD_4_1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_4_1MouseClicked(evt);
            }
        });

        BOARD_4_2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_4_2MouseClicked(evt);
            }
        });

        BOARD_4_3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_4_3MouseClicked(evt);
            }
        });

        BOARD_4_4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_4_4MouseClicked(evt);
            }
        });

        BOARD_0_0.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_0_0MouseClicked(evt);
            }
        });

        BOARD_1_0.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_1_0MouseClicked(evt);
            }
        });

        BOARD_1_4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BOARD_1_4MouseClicked(evt);
            }
        });

        END_TURN_BUTTON.setText("End Turn");
        END_TURN_BUTTON.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                END_TURN_BUTTONMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(131, 131, 131)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(END_TURN_BUTTON)
                        .addGap(79, 79, 79))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(O_BUTTON)
                            .addComponent(X_BUTTON))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(TOTAL_NUMBER_NODES_GENERATED_IN_TREE_TEXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(TIMES_EVALUATION_FUNCTION_CALLED_IN_MAX_TEXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(TIMES_EVALUATION_FUNCTION_CALLED_IN_MIN_TEXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(TIMES_PRUNING_OCCURRED_IN_MAX_TEXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(TIMES_PRUNING_OCCURRED_IN_MIN_TEXT, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(MAX_DEPTH_GAME_TREE_REACHED_TEXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(139, 139, 139)
                                .addComponent(TOTAL_NUMBER_NODES_GENERATED_IN_TREE_TEXT_FIELD, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(133, 133, 133)
                                .addComponent(MAX_DEPTH_GAME_TREE_REACHED_TEXT_FIELD, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(123, 123, 123)
                                .addComponent(TIMES_EVALUATION_FUNCTION_CALLED_IN_MAX_TEXT_FIELD, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(133, 133, 133)
                                .addComponent(TIMES_EVALUATION_FUNCTION_CALLED_IN_MIN_TEXT_FIELD, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(BOARD_1_0, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(BOARD_1_1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(BOARD_1_2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(BOARD_0_0, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(BOARD_0_1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(BOARD_0_2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(BOARD_1_3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(BOARD_0_3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(BOARD_0_4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(BOARD_1_4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(BOARD_2_0, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BOARD_2_1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BOARD_2_2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BOARD_2_3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BOARD_2_4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(BOARD_3_0, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BOARD_3_1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BOARD_3_2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BOARD_3_3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BOARD_3_4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(BOARD_4_0, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BOARD_4_1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BOARD_4_2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BOARD_4_3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BOARD_4_4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(131, 131, 131)
                        .addComponent(DISPLAY_WINNER_TEXT_FIELD, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(87, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(136, 136, 136)
                        .addComponent(TIMES_PRUNING_OCCURRED_IN_MAX_TEXT_FIELD, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(147, 147, 147)
                        .addComponent(TIMES_PRUNING_OCCURRED_IN_MIN_TEXT_FIELD, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(113, 113, 113)
                .addComponent(X_BUTTON)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(O_BUTTON)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(END_TURN_BUTTON)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DISPLAY_WINNER_TEXT_FIELD, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BOARD_0_4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(BOARD_0_1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(BOARD_0_2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(BOARD_0_3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BOARD_1_3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(BOARD_1_0, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(BOARD_1_4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(BOARD_2_0, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(BOARD_2_1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(BOARD_2_2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(BOARD_2_3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(BOARD_2_4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(BOARD_3_0, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(BOARD_3_1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(BOARD_3_2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(BOARD_3_3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(BOARD_3_4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(BOARD_1_1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(MAX_DEPTH_GAME_TREE_REACHED_TEXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(MAX_DEPTH_GAME_TREE_REACHED_TEXT_FIELD, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(TOTAL_NUMBER_NODES_GENERATED_IN_TREE_TEXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(TOTAL_NUMBER_NODES_GENERATED_IN_TREE_TEXT_FIELD, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(12, 12, 12)
                                        .addComponent(TIMES_EVALUATION_FUNCTION_CALLED_IN_MAX_TEXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(TIMES_EVALUATION_FUNCTION_CALLED_IN_MAX_TEXT_FIELD, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(TIMES_EVALUATION_FUNCTION_CALLED_IN_MIN_TEXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(TIMES_EVALUATION_FUNCTION_CALLED_IN_MIN_TEXT_FIELD, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(6, 6, 6))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(BOARD_4_0, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(BOARD_4_1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(BOARD_4_2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(BOARD_4_3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(BOARD_4_4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(BOARD_1_2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(TIMES_PRUNING_OCCURRED_IN_MAX_TEXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TIMES_PRUNING_OCCURRED_IN_MAX_TEXT_FIELD, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(TIMES_PRUNING_OCCURRED_IN_MIN_TEXT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TIMES_PRUNING_OCCURRED_IN_MIN_TEXT_FIELD, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BOARD_0_0, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void X_BUTTONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_X_BUTTONActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_X_BUTTONActionPerformed

    private void O_BUTTONMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_O_BUTTONMouseClicked
        // TODO add your handling code here:
        if (userPlayer == null) {
            userPlayer = P2;
            compPlayer = P1;

            // enable JToggleButtons to play game
            enableButtons();
            
            /* User has chosen so these buttons are no longer needed */
            // Disable them
            X_BUTTON.setEnabled(false);
            O_BUTTON.setEnabled(false);
            // Make them invisible
            X_BUTTON.setVisible(false);
            O_BUTTON.setVisible(false);
        }
    }//GEN-LAST:event_O_BUTTONMouseClicked

    private void X_BUTTONMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_X_BUTTONMouseClicked
        // TODO add your handling code here:
        if (userPlayer == null) {
            userPlayer = P1;
            compPlayer = P2;
            
            // enable JToggleButtons to play game
            enableButtons();
            
            /* User has chosen so these buttons are no longer needed */
            // Disable them
            X_BUTTON.setEnabled(false);
            O_BUTTON.setEnabled(false);
            // Make them invisible
            X_BUTTON.setVisible(false);
            O_BUTTON.setVisible(false);
        }
    }//GEN-LAST:event_X_BUTTONMouseClicked

    private void TOTAL_NUMBER_NODES_GENERATED_IN_TREE_TEXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TOTAL_NUMBER_NODES_GENERATED_IN_TREE_TEXTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TOTAL_NUMBER_NODES_GENERATED_IN_TREE_TEXTActionPerformed

    private void TIMES_PRUNING_OCCURRED_IN_MAX_TEXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TIMES_PRUNING_OCCURRED_IN_MAX_TEXTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TIMES_PRUNING_OCCURRED_IN_MAX_TEXTActionPerformed

    private void BOARD_0_1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_0_1MouseClicked
        // TODO add your handling code here:
        
        int posX = 0;
        int posY = 1;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_0_1;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_0_1MouseClicked

    private void BOARD_0_2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_0_2MouseClicked
        // TODO add your handling code here:
        
        int posX = 0;
        int posY = 2;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_0_2;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_0_2MouseClicked

    private void BOARD_0_3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_0_3MouseClicked
        // TODO add your handling code here:
        
        int posX = 0;
        int posY = 3;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_0_3;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_0_3MouseClicked

    private void BOARD_0_4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_0_4MouseClicked
        // TODO add your handling code here:
        
        int posX = 0;
        int posY = 4;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_0_4;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_0_4MouseClicked

    private void BOARD_1_1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_1_1MouseClicked
        // TODO add your handling code here:
        
        int posX = 1;
        int posY = 1;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_1_1;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_1_1MouseClicked

    private void BOARD_1_2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_1_2MouseClicked
        // TODO add your handling code here:
        
        int posX = 1;
        int posY = 2;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_1_2;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_1_2MouseClicked

    private void BOARD_1_3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_1_3MouseClicked
        // TODO add your handling code here:
        
        int posX = 1;
        int posY = 3;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_1_3;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_1_3MouseClicked

    private void BOARD_2_0MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_2_0MouseClicked
        // TODO add your handling code here:
        
        int posX = 2;
        int posY = 0;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_2_0;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_2_0MouseClicked

    private void BOARD_2_1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_2_1MouseClicked
        // TODO add your handling code here:
        
        int posX = 2;
        int posY = 1;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_2_1;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_2_1MouseClicked

    private void BOARD_2_2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_2_2MouseClicked
        // TODO add your handling code here:
        
        int posX = 2;
        int posY = 2;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_2_2;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_2_2MouseClicked

    private void BOARD_2_3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_2_3MouseClicked
        // TODO add your handling code here:
        
        int posX = 2;
        int posY = 3;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_2_3;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_2_3MouseClicked

    private void BOARD_2_4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_2_4MouseClicked
        // TODO add your handling code here:
        
        int posX = 2;
        int posY = 4;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_2_4;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_2_4MouseClicked

    private void BOARD_3_0MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_3_0MouseClicked
        // TODO add your handling code here:
        
        int posX = 3;
        int posY = 0;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_3_0;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_3_0MouseClicked

    private void BOARD_3_1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_3_1MouseClicked
        // TODO add your handling code here:
        
        int posX = 3;
        int posY = 1;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_3_1;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_3_1MouseClicked

    private void BOARD_3_2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_3_2MouseClicked
        // TODO add your handling code here:
        
        int posX = 3;
        int posY = 2;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_3_2;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_3_2MouseClicked

    private void BOARD_3_3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_3_3MouseClicked
        // TODO add your handling code here:
        
        int posX = 3;
        int posY = 3;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_3_3;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_3_3MouseClicked

    private void BOARD_3_4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_3_4MouseClicked
        // TODO add your handling code here:
        
        int posX = 3;
        int posY = 4;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_3_4;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_3_4MouseClicked

    private void BOARD_4_0MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_4_0MouseClicked
        // TODO add your handling code here:
        
        int posX = 4;
        int posY = 0;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_4_0;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_4_0MouseClicked

    private void BOARD_4_1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_4_1MouseClicked
        // TODO add your handling code here:
        
        int posX = 4;
        int posY = 1;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_4_1;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_4_1MouseClicked

    private void BOARD_4_2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_4_2MouseClicked
        // TODO add your handling code here:
        
        int posX = 4;
        int posY = 2;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_4_2;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_4_2MouseClicked

    private void BOARD_4_3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_4_3MouseClicked
        // TODO add your handling code here:
        
        int posX = 4;
        int posY = 3;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_4_3;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_4_3MouseClicked

    private void BOARD_4_4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_4_4MouseClicked
        // TODO add your handling code here:
        
        int posX = 4;
        int posY = 4;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_4_4;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_4_4MouseClicked

    private void BOARD_0_0MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_0_0MouseClicked
        // TODO add your handling code here:
        
        int posX = 0;
        int posY = 0;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_0_0;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_0_0MouseClicked

    private void BOARD_1_0MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_1_0MouseClicked
        // TODO add your handling code here:
        
        int posX = 1;
        int posY = 0;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_0_0;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_1_0MouseClicked

    private void BOARD_1_4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOARD_1_4MouseClicked
        // TODO add your handling code here:
        
        int posX = 1;
        int posY = 4;
        
        javax.swing.JToggleButton jt;
//        jt = BOARD_0_0;
        jt = buttons[posX][posY];
       
        performClick(posX, posY, jt);
    }//GEN-LAST:event_BOARD_1_4MouseClicked

    private void END_TURN_BUTTONMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_END_TURN_BUTTONMouseClicked
        // TODO add your handling code here:
        
        // Do nothing if this button is not enabled
        if (!END_TURN_BUTTON.isEnabled()) {
            return;

        }
        // If it is compPlayer's turn, perform its move
        if (currentPlayer == compPlayer) {
            compPlay();
        }
    }//GEN-LAST:event_END_TURN_BUTTONMouseClicked

    
    /* Takes the x, y position clicked on the board and the reference to the board's JToggleButton.
    Checks to see there was already a Piece selected:
        If no: selects the Piece if the Piece is owned by the currentPlayer who's turn it is
        If yes:
            1) unselects the Piece if the same Piece is selected again
            2) takes over a Piece if the new position contains a Piece owned
               by the opposing player (and checks if such move is valid)
            3) moves the selectedPiece to the new position which does not
               contain any Piece (and checks if such move is valid)
    */
    private void performClick(int x, int y, javax.swing.JToggleButton jt) {
        // Do nothing if neither user nor computer Players have been set
        if (userPlayer == null || compPlayer == null) {
            jt.setSelected(false);
            return;
        }
        
        // Do nothing if t user clicked something while it is the computer's turn
        if (currentPlayer.getValue() == compPlayer.getValue()) {
            jt.setSelected(false);
            return;
        }
        
        // Do nothing if the game is already over
        if (gameOver) {
            jt.setSelected(false);
            return;
        }
        
        // Check if a Piece has been selected already
        if (selectedPiece == null) {
            // Check if there is a Piece for currentPlayer to select at this board position
            if ((gameBoard[x][y] != null) && gameBoard[x][y].getPlayer() == currentPlayer) {
                // Set selectedPiece to the Piece at this board position
                selectedPiece = gameBoard[x][y];
            }
            // There is no Piece for the currentPlayer here, so do not select this position
            else {
                jt.setSelected(false);
            }
        }
        
        // A Piece has already been selected
        else {
            // Same Piece selected again so unselect it
            if (selectedPiece == gameBoard[x][y]) {
                selectedPiece = null;
                jt.setSelected(false);
            }
            // Move Piece to new position if it is a valid move
            else {
                if (isValidMove(x, y, gameBoard)) {
                    // unselect buttons since a valid move was made
                    buttons[selectedPiece.getX()][selectedPiece.getY()].setSelected(false);
                    jt.setSelected(false);
                    
                    // update the board, Pieces, and Players Piece lists
                    makeMove(x, y, jt);
                    
                    // unselect the Piece now that move has been made
                    selectedPiece = null;
                    
                    // check to see if currentPlayer won with that move
                    if (checkWin(currentPlayer)) {
                        winner = currentPlayer;
                        gameOver = true;
                    }
                    // check to see if the other Player won with that move
                    else {
                        if (currentPlayer == userPlayer) {
                            if (checkWin(compPlayer)) {
                                winner = compPlayer;
                                gameOver = true;
                            }
                        }
                        else {
                            if (checkWin(userPlayer)) {
                                winner = userPlayer;
                                gameOver = true;
                            }
                        }
                    }
                    
                    changeTurn();
                    
                    END_TURN_BUTTON.setEnabled(true);
                }
                // move is not valid so do not select new position
                else {
                    jt.setSelected(false);
                }
            }
        }        
    }
    
    // updates Game elements when a valid move is made
    private void makeMove(int x, int y, javax.swing.JToggleButton jt) {
        // the move will overtake an enemy's Piece
        if ((gameBoard[x][y] != null) && (gameBoard[x][y].getPlayer() != currentPlayer)) {
            // Remove the Piece overtaken from the enemy Player's Pieces' list
            gameBoard[x][y].getPlayer().removePiece(gameBoard[x][y]);
        }
        
        // Update the GUI so that the selectedPiece's position is moved
        buttons[selectedPiece.getX()][selectedPiece.getY()].setText("");
        buttons[x][y].setText(selectedPiece.getPlayer().getPlayerLetter());
        
        // Move the Piece on the board
        gameBoard[x][y] = selectedPiece;
        gameBoard[selectedPiece.getX()][selectedPiece.getY()] = null;
        
        // Update the Piece's position
        selectedPiece.setPos(x, y);
    }
    
    // Checks to see if the p just won the game
    private boolean checkWin(Player p) {
        return p.allConnected();
    }

    // returns true if the new selected position is a valid move. otherwise false.
    public boolean isValidMove(int x, int y, Piece[][] board) {
        int pX = selectedPiece.getX();
        int pY = selectedPiece.getY();
        
        // Number of spaces the selectedPiece can move in some direction
        int moveSpaces;
        
        // Holder for a code to determine moving path
        int path;
        
        // selectedPiece and new position are in the same row
        if (pX == x) {
            moveSpaces = getPiecesInRow(selectedPiece, board);
            path = ROW;
        }
        // selectedPiece and new position are in the same column
        else if (pY == y) {
            moveSpaces = getPiecesInColumn(selectedPiece, board);
            path = COLUMN;
        }
        // selectedPiece and new position are in the same diagonal
        else if (onDiagonal(pX, pY, x, y)) {
            // left-to-right diagonal
            if (onLeftToRightDiagonal(pX, pY, x, y)) {
                moveSpaces = getPiecesInLeftToRightDiagonal(selectedPiece, board);
                path = LR_DIAG;
            }
            // right-to-left diagonal
            else {
                moveSpaces = getPiecesInRightToLeftDiagonal(selectedPiece, board);
                path = RL_DIAG;
            }
        }
        // selectedPiece cannot move to new position in any way
        else {
            return false;
        }
        
        return checkMove(pX, pY, x, y, moveSpaces, path, board);
    }
    
    // returns true if the move to a new position is valid. otherwise false.
    private boolean checkMove(int pX, int pY, int x, int y, int moveSpaces, int path, Piece[][] board) {
        switch (path) {
            case ROW:
                return checkRow(pX, pY, x, y, moveSpaces, board);
       
            case COLUMN:
                return checkColumn(pX, pY, x, y, moveSpaces, board);
                
            case LR_DIAG:
                return checkLRDiag(pX, pY, x, y, moveSpaces, board);
                
            case RL_DIAG:
                return checkRLDiag(pX, pY, x, y, moveSpaces, board);
                
            default:
                return false;
        }
    }
 
    // returns true if the row-wise movement is valid
    private boolean checkRow(int pX, int pY, int x, int y, int moveSpaces, Piece[][] board) {
        Piece tempPiece;

        if (pY > y) {
            // return false if the move does not match the new position
            if ((pY - moveSpaces) != y) {
                return false;
            }
            // return false if the move goes off the board
            else if ((pY - moveSpaces) < 0) {
                return false;
            }
            // move does not go off board, so check if valid
            else {
                for (int i = 1; i <= moveSpaces; ++i) {
                    // check previous position in the row
                    tempPiece = board[pX][pY - i];
                    // an empty space is valid to move over
                    if (tempPiece == null) {
                        continue;
                    }
                    // moves cannot pass over an enemy Piece unless they can land directly on enemy Piece
                    else if ((tempPiece.getPlayer() != currentPlayer) && (i != moveSpaces)) {
                        return false;
                    }
                    // move encounters a friendly Piece
                    else if ((tempPiece.getPlayer() == currentPlayer) && (i == moveSpaces)) {
                        // move is invalid if we try to land on a friendly Piece
                        return false;
                    }
                }
            }
        }
        else {
            // return false if the move does not match the new position
            if ((pY + moveSpaces) != y) {
                return false;
            }
            // return false if the move goes off the board
            else if ((pY + moveSpaces) >= boardSize) {
                return false;
            }
            // move does not go off board, so check if valid
            else {
                for (int i = 1; i <= moveSpaces; ++i) {
                    // check next position in the row
                    tempPiece = board[pX][pY + i];
                    // an empty space is valid to move over
                    if (tempPiece == null) {
                        continue;
                    }
                    // moves cannot pass over an enemy Piece unless they can land directly on an enemy Piece
                    else if((tempPiece.getPlayer() != currentPlayer) && (i != moveSpaces)) {
                        return false;
                    }
                    // move encounters a friendly Piece
                    else if ((tempPiece.getPlayer() == currentPlayer) && (i == moveSpaces)) {
                        // move is invalid if we try to land on a friendly Piece
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    // returns true if the column-wise movement is valid
    private boolean checkColumn(int pX, int pY, int x, int y, int moveSpaces, Piece[][] board) {
        Piece tempPiece;

        if (pX > x) {
            // return false if the move does not match the new position
            if ((pX - moveSpaces) != x) {
                return false;
            }
            // return false if the move goes off the board
            else if ((pX - moveSpaces) < 0) {
                return false;
            }
            // move does not go off board, so check if valid
            else {
                for (int i = 1; i <= moveSpaces; ++i) {
                    // check previous position in the column
                    tempPiece = board[pX - i][pY];
                    // an empty space is valid to move over
                    if (tempPiece == null) {
                        continue;
                    }
                    // moves cannot pass over an enemy Piece unless they can land directly on enemy Piece
                    else if ((tempPiece.getPlayer() != currentPlayer) && (i != moveSpaces)) {
                        return false;
                    }
                    // move encounters a friendly Piece
                    else if ((tempPiece.getPlayer() == currentPlayer) && (i == moveSpaces)) {
                        // move is invalid if we try to land on a friendly Piece
                        return false;
                    }
                }
            }
        }
        else {
            // return false if the move does not match the new position
            if ((pX + moveSpaces) != x) {
                return false;
            }
            // return false if the move goes off the board
            else if ((pX + moveSpaces) >= boardSize) {
                return false;
            }
            // move does not go off board, so check if valid
            else {
                for (int i = 1; i <= moveSpaces; ++i) {
                    // check next position in the column
                    tempPiece = board[pX + i][pY];
                    // an empty space is valid to move over
                    if (tempPiece == null) {
                        continue;
                    }
                    // moves cannot pass over an enemy Piece unless they can land directly on an enemy Piece
                    else if((tempPiece.getPlayer() != currentPlayer) && (i != moveSpaces)) {
                        return false;
                    }
                    // move encounters a friendly Piece
                    else if ((tempPiece.getPlayer() == currentPlayer) && (i == moveSpaces)) {
                        // move is invalid if we try to land on a friendly Piece
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    // returns true if the diagonal-wise movement is valid
    private boolean checkLRDiag(int pX, int pY, int x, int y, int moveSpaces, Piece[][] board) {
        Piece tempPiece;

        if (pX > x) {
            // return false if the move does not match the new position
            if (((pX - moveSpaces) != x) || ((pY - moveSpaces) != y)) {
                return false;
            }
            // return false if the move goes off the board
            else if (((pX - moveSpaces) < 0) || ((pY - moveSpaces) < 0)) {
                return false;
            }
            // move does not go off board, so check if valid
            else {
                for (int i = 1; i <= moveSpaces; ++i) {
                    // check previous position in the up-and-left diagonal
                    tempPiece = board[pX - i][pY - i];
                    // an empty space is valid to move over
                    if (tempPiece == null) {
                        continue;
                    }
                    // moves cannot pass over an enemy Piece unless they can land directly on enemy Piece
                    else if ((tempPiece.getPlayer() != currentPlayer) && (i != moveSpaces)) {
                        return false;
                    }
                    // move encounters a friendly Piece
                    else if ((tempPiece.getPlayer() == currentPlayer) && (i == moveSpaces)) {
                        // move is invalid if we try to land on a friendly Piece
                        return false;
                    }
                }
            }
        }
        else {
            // return false if the move does not match the new position
            if (((pX + moveSpaces) != x) || ((pY + moveSpaces) != y)) {
                return false;
            }
            // return false if the move goes off the board
            else if (((pX + moveSpaces) >= boardSize) || ((pY + moveSpaces) >= boardSize)) {
                return false;
            }
            // move does not go off board, so check if valid
            else {
                for (int i = 1; i <= moveSpaces; ++i) {
                    // check next position in the down-and-right diagonal
                    tempPiece = board[pX + i][pY + i];
                    // an empty space is valid to move over
                    if (tempPiece == null) {
                        continue;
                    }
                    // moves cannot pass over an enemy Piece unless they can land directly on an enemy Piece
                    else if((tempPiece.getPlayer() != currentPlayer) && (i != moveSpaces)) {
                        return false;
                    }
                    // move encounters a friendly Piece
                    else if ((tempPiece.getPlayer() == currentPlayer) && (i == moveSpaces)) {
                        // move is invalid if we try to land on a friendly Piece
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
     // returns true if the diagonal-wise movement is valid
    private boolean checkRLDiag(int pX, int pY, int x, int y, int moveSpaces, Piece[][] board) {
        Piece tempPiece;

        if (pX > x) {
            // return false if the move does not match the new position
            if (((pX - moveSpaces) != x) || ((pY + moveSpaces) != y)) {
                return false;
            }
            // return false if the move goes off the board
            else if (((pX - moveSpaces) < 0) || ((pY + moveSpaces) >= boardSize)) {
                return false;
            }
            // move does not go off board, so check if valid
            else {
                for (int i = 1; i <= moveSpaces; ++i) {
                    // check previous position in the up-and-left diagonal
                    tempPiece = board[pX - i][pY + i];
                    // an empty space is valid to move over
                    if (tempPiece == null) {
                        continue;
                    }
                    // moves cannot pass over an enemy Piece unless they can land directly on enemy Piece
                    else if ((tempPiece.getPlayer() != currentPlayer) && (i != moveSpaces)) {
                        return false;
                    }
                    // move encounters a friendly Piece
                    else if ((tempPiece.getPlayer() == currentPlayer) && (i == moveSpaces)) {
                        // move is invalid if we try to land on a friendly Piece
                        return false;
                    }
                }
            }
        }
        else {
            // return false if the move does not match the new position
            if (((pX + moveSpaces) != x) || ((pY - moveSpaces) != y)) {
                return false;
            }
            // return false if the move goes off the board
            else if (((pX + moveSpaces) >= boardSize) || ((pY - moveSpaces) < 0)) {
                return false;
            }
            // move does not go off board, so check if valid
            else {
                for (int i = 1; i <= moveSpaces; ++i) {
                    // check next position in the down-and-right diagonal
                    tempPiece = board[pX + i][pY - i];
                    // an empty space is valid to move over
                    if (tempPiece == null) {
                        continue;
                    }
                    // moves cannot pass over an enemy Piece unless they can land directly on an enemy Piece
                    else if((tempPiece.getPlayer() != currentPlayer) && (i != moveSpaces)) {
                        return false;
                    }
                    // move encounters a friendly Piece
                    else if ((tempPiece.getPlayer() == currentPlayer) && (i == moveSpaces)) {
                        // move is invalid if we try to land on a friendly Piece
                        return false;
                    }
                }
            }
        }
        
        return true;
    } 
    
    private int getPiecesInRow(Piece p, Piece[][] board) {
        int x = p.getX();
        
        int pieces = 0;
        
        for (int i = 0; i < boardSize; ++i) {
            if (board[x][i] != null) {
                ++pieces;
            }
        }
        
        return pieces;
    }
    
    private int getPiecesInColumn(Piece p, Piece[][] board) {
        int y = p.getY();
        
        int pieces = 0;
        
        for (int i = 0; i < boardSize; ++i) {
            if (board[i][y] != null) {
                ++pieces;
            }
        }
        
        return pieces;
    }
    
    private int getPiecesInLeftToRightDiagonal(Piece p, Piece[][] board) {
        int x = p.getX();
        int y = p.getY();
        
        // we know our current piece is in this diagonal
        int pieces = 1;
        
        // check for Pieces up-and-left
        for (int i = x-1, j = y-1; i >= 0 && j >= 0; --i, --j) {
            if (board[i][j] != null) {
                ++pieces;
            }
        }
        // check for Pieces down-and-right
        for (int i = x+1, j = y+1; i < boardSize && j < boardSize; ++i, ++j) {
            if (board[i][j] != null) {
                ++pieces;
            }
        }
        
        return pieces;
    }
    
    private int getPiecesInRightToLeftDiagonal(Piece p, Piece[][] board) {
        int x = p.getX();
        int y = p.getY();
        
        // we know our current piece is in this diagonal
        int pieces = 1;

        // check for Pieces up-and-right
        for (int i = x-1, j = y+1; i >= 0 && j < boardSize; --i, ++j) {
            if (board[i][j] != null) {
                ++pieces;
            }
        }
        // check for Pieces down-and-left
        for (int i = x+1, j = y-1; i < boardSize && j >= 0; ++i, --j) {
            if (board[i][j] != null) {
                ++pieces;
            }
        }
        
        return pieces;
    }  
        
    // returns true if the positions are diagonal to each other. otherwise false.
    private boolean onDiagonal(int pX, int pY, int x, int y) {
        for (int i = 0; i < boardSize; ++i) {
            // check up-and-left diagonal
            if ((pX - i == x) && (pY - i == y)) {
                return true;
            }
            // check down-and-right diagonal
            else if ((pX + i == x) && (pY + i == y)) {
                return true;
            }
            // check up-and-right diagonal
            else if ((pX - i == x) && (pY + i == y)) {
                return true;
            }
            // check down-and-left diagonal
            else if((pX + i == x) && (pY - i) == y){
                return true;
            }
        }
        
        return false;
    }
    
    /* returns true if the selectedPiece's x,y are on a diagonal from
       the TopLeft-to-BottomRight with the x,y of the new position.
       return false otherwise.
    */
    private boolean onLeftToRightDiagonal(int pX, int pY, int x, int y) {
        return ((pX > x) && (pY > y)) || ((pX < x) && (pY < y));
    }
    
    // Changes which Player's turn it is
    private void changeTurn() {
        if (currentPlayer == P1) {
            currentPlayer = P2;
        }
        else if (currentPlayer == P2) {
            currentPlayer = P1;
        }
    }
 
    /*
    Computer Player wins with utility of +100 (MAX)
    User Player wins with utility of -100 (MIN)
    Draw is utility of 0
    */
    private PieceAndDir ALPHA_BETA_SEARCH(Map<Integer, PieceAndDir> state) {
        
        // all values to show start at 0
        depth = 0;
        nodesGenerated = 0;
        evalCalledMax = 0;
        evalCalledMin = 0;
        pruningMax = 0;
        pruningMin = 0;        
        
        /*
        Create temp Players with Piece lists identical to the current state
        to test for MIN & MAX values without. This is done as to not change
        the actual state of the board until a move has been decided upon.
        */
        tempCompPlayer = new Player(compPlayer);
        tempUserPlayer = new Player(userPlayer);
        
        // Create temp board to test with
        tempGameBoard = new Piece[boardSize][boardSize];
        for (Piece p : tempCompPlayer.getPieces()) {
            if (!p.isRemoved()) {
                tempGameBoard[p.getX()][p.getY()] = p;
            }
        }
        for (Piece p : tempUserPlayer.getPieces()) {
            if (!p.isRemoved()) {
                tempGameBoard[p.getX()][p.getY()] = p;
            }
        }
        
        // Create the removedPieces List to store Pieces overtaken in MIN/MAX calls
        removedPieces = new ArrayList<>(boardSize * 2);
        
        // Create the seenStates Map
        seenStates = new HashMap<>();
        
        int v = MAX_VALUE(state, seenStates, MIN, MAX, 0);
        
        // return the Piece and its corresponding direction to move with max utility
        return state.get(v);
    }

    // returns a utility value for the best possible move
    private int MAX_VALUE(Map<Integer, PieceAndDir> state, Map<Integer, List<String>> seen, int alpha, int beta, int dpth) {
        /*
        This initial call to MAX_VALUE will never start off with a terminal condition.
        It is only called when the computer's turn starts.
        */
 
        // Root node depth is 0
        ++nodesGenerated; // root node is a node generated
        
        /* Update currentPlayer to test moves for temp AI Player */
        currentPlayer = tempCompPlayer;

        // v is initially the minimum possible value
        int v = MIN;
        
        // used to signify whether an opponent Piece was taken in a move made
        boolean pieceTaken;
        Piece removedPiece;
        
        int compX, compY;
        int compMoveSpaces;
        int compDirection;
        
        boolean repeatedState;
    
        // Test every Piece for currentPlayer
        for (Piece p : tempCompPlayer.getPieces()) {
            // If the current Piece is not actually on the board to be tested, go to the next Piece
            if (p.isRemoved()) {
                continue;
            }

            /* Update selectedPiece to test moves for 1 of the Pieces */
            selectedPiece = p;
            
            // original x,y position of the selectedPiece to move
            compX = selectedPiece.getX();
            compY = selectedPiece.getY();
            
            // Test all possible movements with selectedPiece p (up, up-right, right, right-down, etc.)
            for (int i = 0; i < numMoves; ++i) {
                compDirection = i;
                
                compMoveSpaces = checkMove(selectedPiece, compDirection, tempGameBoard);
                // compMoveSpaces is a positive int if the move is valid
                if (compMoveSpaces != 0) {
                    // pieceTaken == true if a Piece was overtaken in makeMove
                    pieceTaken = makeMove(selectedPiece, compDirection, compMoveSpaces, tempGameBoard);
                   
                    /* Graph Search to not repeat states already seen */
                    repeatedState = checkState(tempGameBoard, seen, "MAX");
                    // State has not been seen already so call MIN_VALUE
                    if (!repeatedState) {
                        // a new state has been reached so inc nodesGenerated
                        ++nodesGenerated;
                        
                        // Call MIN_VALUE and update v if needed
                        v = Math.max(v, MIN_VALUE(seen, alpha, beta, dpth+1));
                        
                        // Reset values to current MAX_VALUE call's values
                        currentPlayer = tempCompPlayer;
                        selectedPiece = p;
                        
                        // Add the current Piece and its direction to the state with utility v
                        state.put(v, new PieceAndDir(selectedPiece, i));
                    }
                    
                    /* Get state back to where it was before any move was made */
                    tempGameBoard[compX][compY] = selectedPiece;
//                    selectedPiece.setPos(compX, compY);
                    // Must place the Piece removed back onto the board to maintain state
                    if (pieceTaken) {
                        // Return the removed Piece to the board and to the Player's list
                        removedPiece = removedPieces.remove(removedPieces.size()-1);
                        tempGameBoard[removedPiece.getX()][removedPiece.getY()] = removedPiece;
                        removedPiece.getPlayer().addPiece(removedPiece);
                    }
                    // Just move the selectedPiece back to its location before move was made to maintain state
                    else {
                        // No Piece was in the position moved to so return that position to null
                        tempGameBoard[selectedPiece.getX()][selectedPiece.getY()] = null;
                    }
                    selectedPiece.setPos(compX, compY);
                    
                    // Return v if we've hit the max possible utility in this tree
                    if (v >= beta) {
                        // Pruning in MAX_VALUE happened so inc pruningMax
                        ++pruningMax;
                        return v;
                    }
                    // update alpha 
                    alpha = Math.max(alpha, v);
                }
            }
        }
        
        return v;
    }
    
    // returns a utility value for the best possible move
    private int MAX_VALUE(Map<Integer, List<String>> seen, int alpha, int beta, int dpth) {
        // Update depth if needed
        depth = Math.max(depth, dpth);
        
        /* Terminal Test to see if either Player won */
        // User Player checked first since MAX_VALUE will be called after User moves
        if (checkWin(tempUserPlayer)) {
            return MIN;
        }
        else if (checkWin(tempCompPlayer)) {
            return MAX;
        }
 
        // check for cutoff conditions
        if ((dpth >= MAX_DEPTH) || (((System.nanoTime() - startTime)/1000000000) >= MAX_TIME)) {
            // EVAL called in MAX_VALUE so inc evalCalledMax
            ++evalCalledMax;
            return EVAL(tempGameBoard, tempCompPlayer, tempUserPlayer);
        }
        
        /* Update currentPlayer to test moves for AI Player */
        currentPlayer = tempCompPlayer;

        // v is initially the minimum possible value
        int v = MIN;
        
        // used to signify whether an opponent Piece was taken in a move made
        boolean pieceTaken;
        Piece removedPiece;
        
        int compX, compY;
        int compMoveSpaces;
        int compDirection;
    
        boolean repeatedState;

        // Test every Piece for currentPlayer
        for (Piece p : tempCompPlayer.getPieces()) {
            // If the current Piece is not actually on the board to be tested, go to the next Piece
            if (p.isRemoved()) {
                continue;
            }

            /* Update selectedPiece to test moves for 1 of the Pieces */
            selectedPiece = p;
            
            // original x,y position of the selectedPiece to move
            compX = selectedPiece.getX();
            compY = selectedPiece.getY();
            
            // Test all possible movements with selectedPiece p (up, up-right, right, right-down, etc.)
            for (int i = 0; i < numMoves; ++i) {
                compDirection = i;
                
                compMoveSpaces = checkMove(selectedPiece, compDirection, tempGameBoard);
                // compMoveSpaces is a positive int if the move is valid
                if (compMoveSpaces != 0) {
                    // pieceTaken == true if a Piece was overtaken in makeMove
                    pieceTaken = makeMove(selectedPiece, compDirection, compMoveSpaces, tempGameBoard);
                   
                    /* Graph Search to not repeat states already seen */
                    repeatedState = checkState(tempGameBoard, seen, "MAX");
                    // State has not been seen already so call MIN_VALUE
                    if (!repeatedState) {
                        // a new state has been reached so inc nodesGenerated
                        ++nodesGenerated;
                        
                        // Call MIN_VALUE and update v if needed
                        v = Math.max(v, MIN_VALUE(seen, alpha, beta, dpth+1));
                        
                        // Reset values to current MAX_VALUE call's values
                        currentPlayer = tempCompPlayer;
                        selectedPiece = p;
                    }
                    
                    /* Get state back to where it was before any move was made */
                    tempGameBoard[compX][compY] = selectedPiece;
//                    selectedPiece.setPos(compX, compY);
                    // Must place the Piece removed back onto the board to maintain state
                    if (pieceTaken) {
                        // Return the removed Piece to the board and to the Player's list
                        removedPiece = removedPieces.remove(removedPieces.size()-1);
                        tempGameBoard[removedPiece.getX()][removedPiece.getY()] = removedPiece;
                        removedPiece.getPlayer().addPiece(removedPiece);
                    }
                    // Just move the selectedPiece back to its location before move was made to maintain state
                    else {
                        // No Piece was in the position moved to so return that position to null
                        tempGameBoard[selectedPiece.getX()][selectedPiece.getY()] = null;
                    }
                    selectedPiece.setPos(compX, compY);
                    
                    // Return v if we've hit the max possible utility in this tree
                    if (v >= beta) {
                        // Pruning in MAX_VALUE happened so inc pruningMax
                        ++pruningMax;
                        return v;
                    }
                    // update alpha 
                    alpha = Math.max(alpha, v);
                }
            }
        }
        
        return v;
    }
    
    // returns a utility value for the worst possible move
    private int MIN_VALUE(Map<Integer, List<String>> seen, int alpha, int beta, int dpth) {
        // Update depth if needed
        depth = Math.max(depth, dpth);

        /* Terminal Test to see if either Player won */
        // Comp Player checked first since MIN_VALUE will be called after Comp moves
        if (checkWin(tempCompPlayer)) {
            return MAX;
        }
        else if (checkWin(tempUserPlayer)) {
            return MIN;
        }
 
        // check for cutoff conditions
        if ((dpth >= MAX_DEPTH) || (((System.nanoTime() - startTime)/1000000000) >= MAX_TIME)) {
            // EVAL called in MIN_VALUE so inc evalCalledMin
            ++evalCalledMin;
            return EVAL(tempGameBoard, tempCompPlayer, tempUserPlayer);
        }
        
        /* Update currentPlayer to test moves for AI Player */
        currentPlayer = tempUserPlayer;

        // v is initially the maximum possible value
        int v = MAX;
        
        // used to signify whether an opponent Piece was taken in a move made
        boolean pieceTaken;
        Piece removedPiece;
        
        int userX, userY;
        int userMoveSpaces;
        int userDirection;
    
        boolean repeatedState;

        // Test every Piece for currentPlayer
        for (Piece p : tempUserPlayer.getPieces()) {
            // If the current Piece is not actually on the board to be tested, go to the next Piece
            if (p.isRemoved()) {
                continue;
            }  
            
            /* Update selectedPiece to test moves for 1 of the Pieces */
            selectedPiece = p;
            
            // original x,y position of the selectedPiece to move
            userX = selectedPiece.getX();
            userY = selectedPiece.getY();
            
            for (int i = 0; i < numMoves; ++i) {
                userDirection = i;
                
                userMoveSpaces = checkMove(selectedPiece, userDirection, tempGameBoard);
                // userMoveSpaces is a positive int if the move is valid
                if (userMoveSpaces != 0) {
                    // pieceTaken == true if a Piece was overtaken in makeMove
                    pieceTaken = makeMove(selectedPiece, userDirection, userMoveSpaces, tempGameBoard);
                   
                    /* Graph Search to not repeat states already seen */
                    repeatedState = checkState(tempGameBoard, seen, "MIN");
                    // State has not been seen already so call MIN_VALUE
                    if (!repeatedState) {
                        // a new state has been reached so inc nodesGenerated
                        ++nodesGenerated;
                        
                        // Call MAX_VALUE and update v if needed
                        v = Math.min(v, MAX_VALUE(seen, alpha, beta, dpth+1));
                        
                        // Reset values to current MIN_VALUE call's values
                        currentPlayer = tempUserPlayer;
                        selectedPiece = p;
                    }                    
                    
                    /* Get state back to where it was before any move was made */
                    tempGameBoard[userX][userY] = selectedPiece;
//                    selectedPiece.setPos(compX, compY);
                    // Must place the Piece removed back onto the board to maintain state
                    if (pieceTaken) {
                        // Return the removed Piece to the board and to the Player's list
                        removedPiece = removedPieces.remove(removedPieces.size()-1);
                        tempGameBoard[removedPiece.getX()][removedPiece.getY()] = removedPiece;
                        removedPiece.getPlayer().addPiece(removedPiece);
                    }
                    // Just move the selectedPiece back to its location before move was made to maintain state
                    else {
                        // No Piece was in the position moved to so return that position to null
                        tempGameBoard[selectedPiece.getX()][selectedPiece.getY()] = null;
                    }
                    selectedPiece.setPos(userX, userY);
                    
                    // Return v if we've hit the min possible utility in this tree
                    if (v <= alpha) {
                        // Pruning in MIN_VALUE happened so inc pruningMin
                        ++pruningMin;
                        return v;
                    }
                    // update beta 
                    beta = Math.min(beta, v);
                }
            }
        }
        
        return v;
    }
    
    // evaluation function when Alpha-Beta is stopped prematurely
    private int EVAL(Piece[][] board, Player compP, Player userP) {
        int util = 0;
        
        // Computer and User Lists of Pieces
        List<Piece> compL = new ArrayList<>();
        List<Piece> userL = new ArrayList<>();
        
        // Only user elements that are considered to be on the board
        for (Piece p : compP.getPieces()) {
            if (!p.isRemoved()) {
                compL.add(p);
            }
        }
        for (Piece p : userP.getPieces()) {
            if (!p.isRemoved()) {
                userL.add(p);
            }
        }
        
        // Each index will contain Lists of Pieces that are connected to each other
        List<List<Piece>> compConnectedParts = new ArrayList<>();
        List<List<Piece>> userConnectedParts = new ArrayList<>();
        
        // Lists containing connected Pieces
        List<Piece> compConnected = new ArrayList<>();
        List<Piece> userConnected = new ArrayList<>();
        
        // Put the first element of each Player's Pieces List into a connected List
        compConnected.add(compL.remove(0));
        userConnected.add(userL.remove(0));
        // Add the Lists to the Lists of connected Pieces
        compConnectedParts.add(compConnected);
        userConnectedParts.add(userConnected);
        
        /*
        If compP has more Pieces than userP left, that is bad for compP's utility.
        The less Pieces a Player has left, the closer they are to connecting them all.
        */
        util -= (compP.getNumPieces() - userP.getNumPieces()) * 8;
        
        
        Piece p;
        List<Piece> l;
        boolean contained;
        
        // Loop over Computer's List of Pieces and get their adjacency positions to each other
        for (int i = 0; i < compL.size(); ++i) {
            contained = false;
            
            p = compL.get(i);
            
            // Check if p is adjacent to any of our current contents
            for (int j = 0; j < compConnectedParts.size(); ++j) {
                l = compConnectedParts.get(j);
                for (int k = 0; k < l.size(); ++k) {
                    if (p.adjacent(l.get(k))) {
                        if (!l.contains(p)) {
                            l.add(p);
                        }
                    }
                }
            }

            for (int j = 0; j < compConnectedParts.size(); ++j) {
                l = compConnectedParts.get(j);
                if (l.contains(p)) {
                    contained = true;
                }
            }
            
            if (!contained) {
                compConnected = new ArrayList<>();
                compConnected.add(p);
                compConnectedParts.add(compConnected);            
            }
        }
        
        // Loop over USer's List of Pieces and get their adjacency positions to each other
        for (int i = 0; i < userL.size(); ++i) {
            contained = false;
            
            p = userL.get(i);
            
            // Check if p is adjacent to any of our current contents
            for (int j = 0; j < userConnectedParts.size(); ++j) {
                l = userConnectedParts.get(j);
                for (int k = 0; k < l.size(); ++k) {
                    if (p.adjacent(l.get(k))) {
                        if (!l.contains(p)) {
                            l.add(p);
                        }
                    }
                }
            }

            for (int j = 0; j < userConnectedParts.size(); ++j) {
                l = userConnectedParts.get(j);
                if (l.contains(p)) {
                    contained = true;
                }
            }
            
            if (!contained) {
                userConnected = new ArrayList<>();
                userConnected.add(p);
                userConnectedParts.add(userConnected);            
            }
        }
        
        /*
        If compP has more disconnected Pieces than userP , that is bad for compP's utility.
        The more disconnected a Player's Pieces are, the farther they are to connecting them all.
        */
        util -= (compConnectedParts.size() - userConnectedParts.size()) * 2;  
        
        
        float xDistanceFromCenter = 0;
        float yDistanceFromCenter = 0;
        float calcDistanceFromCenter = 0;
        float maxDistanceFromCenterPerConnected = 0;
        
        // Get the Piece with the largest calculated distance from the center for computer and user
        for (int i = 0; i < compConnectedParts.size(); ++i) {
            l = compConnectedParts.get(i);
            for (int j = 0; j < l.size(); ++j) {
                xDistanceFromCenter = Math.abs((float) l.get(j).getX() - (float) boardSize/2);
                yDistanceFromCenter = Math.abs((float) l.get(j).getY() - (float) boardSize/2);
                calcDistanceFromCenter = xDistanceFromCenter*xDistanceFromCenter + yDistanceFromCenter*yDistanceFromCenter;
                util -= calcDistanceFromCenter * 3;
//                maxDistanceFromCenterPerConnected = Math.max(maxDistanceFromCenterPerConnected, calcDistanceFromCenter);
            }
            /*
            If compP has Pieces far from the center, that is bad for its utility.
            That means it is less likely that moves will bring Pieces close together.
            */
//            util -= maxDistanceFromCenterPerConnected;
        }
        for (int i = 0; i < userConnectedParts.size(); ++i) {
            l = userConnectedParts.get(i);
            for (int j = 0; j < l.size(); ++j) {
                xDistanceFromCenter = Math.abs((float) l.get(j).getX() - (float) boardSize/2);
                yDistanceFromCenter = Math.abs((float) l.get(j).getY() - (float) boardSize/2);
                calcDistanceFromCenter = xDistanceFromCenter*xDistanceFromCenter + yDistanceFromCenter*yDistanceFromCenter;
                util += calcDistanceFromCenter * 3;
//                maxDistanceFromCenterPerConnected = Math.max(maxDistanceFromCenterPerConnected, calcDistanceFromCenter);
            }
            /*
            If userP has Pieces far from the center, that is good for compP's utility calculations.
            That means it is less likely that moves will bring userP's Pieces close together.
            */
//            util += maxDistanceFromCenterPerConnected;
        }        
        
        if (util >= MAX) {
            util = MAX - 5;
        }
        else if (util <= MIN) {
            util = MIN + 5;
        }
        
        return util;
    }
    
    /*
    Returns true if the board's state has already been seen. Otherwise false.
    If the state was not already in seen, it adds it to the seen states.
    */
    private boolean checkState(Piece[][] board, Map<Integer, List<String>> seen, String MINMAX) {
        int hashVal = 0;
        
        // MINMAX contains "MIN" or "MAX" depending on which of those 2 functions called this function
        String s = MINMAX;
        
        Piece p;
        
        // Loop over the board to compute the hash value and create the string for the current board state
        for (int i = 0; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {
                p = board[i][j];
                if (p == null) {
                    hashVal += (i*10)+(j*100)*1000;
                    s += "0";
                }
                else {
                    hashVal += (i*10)+(j*100)*p.getPlayerValue();
                    s += p.getPlayerLetter();
                }
            }
        }
        
        // if the hashVal has not been seen before, create a new entry with this string and return false
        if (seen.get(hashVal) == null) {
            List<String> temp = new ArrayList<>();
            temp.add(s);
            seen.put(hashVal, temp);
            return false;
        }
        // If the current state has not been seen, add the string to the list and return false
        else if (!seen.get(hashVal).contains(s)) {
            seen.get(hashVal).add(s);
            return false;
        }
        // The current state has been seen, so return true
        else {
            return true;
        }
    }                    
    
    // Return the number of spaces to move if it is a valid move. Otherwise return 0.
    private int checkMove(Piece p, int direction, Piece[][] board) {
        int moveSpaces;
        int pX = p.getX();
        int pY = p.getY();
        
        boolean validMove;
        
        // Get the number of spaces to be moved in the direction and update moveSpaces and validMove accordingly
        switch(direction) {
            case UP:
                moveSpaces = getPiecesInColumn(p, board);
                validMove = isValidMove(pX - moveSpaces, pY, board);
                break;
                
            case UP_RIGHT:
                moveSpaces = getPiecesInRightToLeftDiagonal(p, board);
                validMove = isValidMove(pX - moveSpaces, pY + moveSpaces, board);
                break;
                
            case RIGHT:
                moveSpaces = getPiecesInRow(p, board);
                validMove = isValidMove(pX, pY + moveSpaces, board);
                break;
                
            case DOWN_RIGHT:
                moveSpaces = getPiecesInLeftToRightDiagonal(p, board);
                validMove = isValidMove(pX + moveSpaces, pY + moveSpaces, board);
                break;
                
            case DOWN:
                moveSpaces = getPiecesInColumn(p, board);
                validMove = isValidMove(pX + moveSpaces, pY, board);
                break;
                
            case DOWN_LEFT:
                moveSpaces = getPiecesInRightToLeftDiagonal(p, board);
                validMove = isValidMove(pX + moveSpaces, pY - moveSpaces, board);
                break;
                
            case LEFT:
                moveSpaces = getPiecesInRow(p, board);
                validMove = isValidMove(pX, pY - moveSpaces, board);
                break;
                
            case UP_LEFT:
                moveSpaces = getPiecesInLeftToRightDiagonal(p, board);
                validMove = isValidMove(pX - moveSpaces, pY - moveSpaces, board);
                break;
                
            default:
                return 0;
        }
        
        // The move was valid so return the number of spaces that is a valid move
        if (validMove) {
            return moveSpaces;
        }
        // The move was not valid so return a 0
        else {
            return 0;
        }
        
    }
      
    // Return true if an opponent's Piece was taken over in the move. Otherwise false.
    private boolean makeMove(Piece p, int direction, int moveSpaces, Piece[][] board) {
        boolean takenOver = false;

        // selectedPiece's x,y
        int pX = p.getX();
        int pY = p.getY();
      
        // position to move to's x,y
        int x = pX;
        int y = pY;
        
        // Get the number of spaces to be moved in the direction and update moveSpaces and validMove accordingly
        switch(direction) {
            case UP:
                // Only the x-row value is changed to move up the rows
                x -= moveSpaces;
                break;
                
            case UP_RIGHT:
                x -= moveSpaces;
                y += moveSpaces;
                break;
                
            case RIGHT:
                // Only the y-column value is changed to move right in the columns
                y += moveSpaces;
                break;
                
            case DOWN_RIGHT:
                x += moveSpaces;
                y += moveSpaces;
                break;
                
            case DOWN:
                // Only the x-row value is changed to move down the rows
                x += moveSpaces;
                break;
                
            case DOWN_LEFT:
                x += moveSpaces;
                y -= moveSpaces;
                break;
                
            case LEFT:
                // Only the y-column value is changed to move left in the columns
                y -= moveSpaces;
                break;
                
            case UP_LEFT:
                x -= moveSpaces;
                y -= moveSpaces;
                break;
                
            default:
                return false;
        }
        

        // the move will overtake an enemy's Piece
        if ((board[x][y] != null) && (board[x][y].getPlayer() != currentPlayer)) {
            // Remove the Piece overtaken from the enemy Player's Pieces' list
            removedPieces.add(board[x][y]);
            board[x][y].getPlayer().removePiece(board[x][y]);
            takenOver = true;
        }
        
        // Move the Piece on the board
        board[x][y] = selectedPiece;
        board[selectedPiece.getX()][selectedPiece.getY()] = null;
        
        // Update the Piece's position
        selectedPiece.setPos(x, y);     
        
        return takenOver;
    }  

    // Actually make the move for the computer turn
    private void makeMove(Piece p, int direction, Piece[][] board) {
        // Number of spaces to move
        int moveSpaces = 0;
        
        // selectedPiece's x,y
        int pX = p.getX();
        int pY = p.getY();
      
        // position to move to's x,y
        int x = pX;
        int y = pY;
        
        // Get the number of spaces to be moved in the direction and update moveSpaces and validMove accordingly
        switch(direction) {
            case UP:
                moveSpaces = getPiecesInColumn(p, board);
                // Only the x-row value is changed to move up the rows
                x -= moveSpaces;
                break;
                
            case UP_RIGHT:
                moveSpaces = getPiecesInRightToLeftDiagonal(p, board);
                x -= moveSpaces;
                y += moveSpaces;
                break;
                
            case RIGHT:
                moveSpaces = getPiecesInRow(p, board);
                // Only the y-column value is changed to move right in the columns
                y += moveSpaces;
                break;
                
            case DOWN_RIGHT:
                moveSpaces = getPiecesInLeftToRightDiagonal(p, board);
                x += moveSpaces;
                y += moveSpaces;
                break;
                
            case DOWN:
                moveSpaces = getPiecesInColumn(p, board);
                // Only the x-row value is changed to move down the rows
                x += moveSpaces;
                break;
                
            case DOWN_LEFT:
                moveSpaces = getPiecesInRightToLeftDiagonal(p, board);
                x += moveSpaces;
                y -= moveSpaces;
                break;
                
            case LEFT:
                moveSpaces = getPiecesInRow(p, board);
                // Only the y-column value is changed to move left in the columns
                y -= moveSpaces;
                break;
                
            case UP_LEFT:
                moveSpaces = getPiecesInLeftToRightDiagonal(p, board);
                x -= moveSpaces;
                y -= moveSpaces;
                break;
        }
        
        // Update the actual gameBoard with the computer's move
        makeMove(x, y, buttons[x][y]);
    }
    
    // compPlayer takes its turn
    private void compPlay() {
        
        startTime = System.nanoTime();
        
        // Used for Alpha-Beta Search
        Map<Integer, PieceAndDir> state;
        PieceAndDir pieceToMove;
        
        // Play the game until it is over
        if (!gameOver) {
            if (currentPlayer == compPlayer) {
                // disable the buttons when it is the computer's turn
                disableButtons();
                
                state = new HashMap<>();
                pieceToMove = ALPHA_BETA_SEARCH(state);
                
                // Update our selectedPiece and make the move the AI chose
                selectedPiece = gameBoard[pieceToMove.p.getX()][pieceToMove.p.getY()];
                makeMove(selectedPiece, pieceToMove.dir, gameBoard);
                
                updateAlphaBetaStats();
                
                if (checkWin(compPlayer)) {
                    winner = compPlayer;
                    gameOver = true;
                }
                else if (checkWin(userPlayer)) {
                    winner = userPlayer;
                    gameOver = true;
                }
                
                // Reset so no Piece is selected
                selectedPiece = null;
                        
                // enable the buttons after the computer made its move
                enableButtons();
                
                // change the turn back to userPlayer
                currentPlayer = userPlayer;
            }
        }
        
        // Display the winner
        if (winner == userPlayer) {
            DISPLAY_WINNER_TEXT_FIELD.setEnabled(true);
            DISPLAY_WINNER_TEXT_FIELD.setText("User Wins!");
            disableButtons();
        }
        else if (winner == compPlayer) {
            DISPLAY_WINNER_TEXT_FIELD.setEnabled(true);
            DISPLAY_WINNER_TEXT_FIELD.setText("Computer Wins!");
            disableButtons();
        }
    }

    private void updateAlphaBetaStats() {
        MAX_DEPTH_GAME_TREE_REACHED_TEXT_FIELD.setText(Long.toString(depth));
        TOTAL_NUMBER_NODES_GENERATED_IN_TREE_TEXT_FIELD.setText(Long.toString(nodesGenerated));
        TIMES_EVALUATION_FUNCTION_CALLED_IN_MAX_TEXT_FIELD.setText(Long.toString(evalCalledMax));
        TIMES_EVALUATION_FUNCTION_CALLED_IN_MIN_TEXT_FIELD.setText(Long.toString(evalCalledMin));
        TIMES_PRUNING_OCCURRED_IN_MAX_TEXT_FIELD.setText(Long.toString(pruningMax));
        TIMES_PRUNING_OCCURRED_IN_MIN_TEXT_FIELD.setText(Long.toString(pruningMin));
        
    }

    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                new GameWindow().setVisible(true);
                // Create and display the Game
                GameWindow gw = new GameWindow();
                gw.setVisible(true);
//                gw.playGame();
            }
        });
        
//        playGame();
    }
    


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton BOARD_0_0;
    private javax.swing.JToggleButton BOARD_0_1;
    private javax.swing.JToggleButton BOARD_0_2;
    private javax.swing.JToggleButton BOARD_0_3;
    private javax.swing.JToggleButton BOARD_0_4;
    private javax.swing.JToggleButton BOARD_1_0;
    private javax.swing.JToggleButton BOARD_1_1;
    private javax.swing.JToggleButton BOARD_1_2;
    private javax.swing.JToggleButton BOARD_1_3;
    private javax.swing.JToggleButton BOARD_1_4;
    private javax.swing.JToggleButton BOARD_2_0;
    private javax.swing.JToggleButton BOARD_2_1;
    private javax.swing.JToggleButton BOARD_2_2;
    private javax.swing.JToggleButton BOARD_2_3;
    private javax.swing.JToggleButton BOARD_2_4;
    private javax.swing.JToggleButton BOARD_3_0;
    private javax.swing.JToggleButton BOARD_3_1;
    private javax.swing.JToggleButton BOARD_3_2;
    private javax.swing.JToggleButton BOARD_3_3;
    private javax.swing.JToggleButton BOARD_3_4;
    private javax.swing.JToggleButton BOARD_4_0;
    private javax.swing.JToggleButton BOARD_4_1;
    private javax.swing.JToggleButton BOARD_4_2;
    private javax.swing.JToggleButton BOARD_4_3;
    private javax.swing.JToggleButton BOARD_4_4;
    private javax.swing.JLabel DISPLAY_WINNER_TEXT_FIELD;
    private javax.swing.JButton END_TURN_BUTTON;
    private javax.swing.JTextField MAX_DEPTH_GAME_TREE_REACHED_TEXT;
    private javax.swing.JLabel MAX_DEPTH_GAME_TREE_REACHED_TEXT_FIELD;
    private javax.swing.JButton O_BUTTON;
    private javax.swing.JTextField TIMES_EVALUATION_FUNCTION_CALLED_IN_MAX_TEXT;
    private javax.swing.JLabel TIMES_EVALUATION_FUNCTION_CALLED_IN_MAX_TEXT_FIELD;
    private javax.swing.JTextField TIMES_EVALUATION_FUNCTION_CALLED_IN_MIN_TEXT;
    private javax.swing.JLabel TIMES_EVALUATION_FUNCTION_CALLED_IN_MIN_TEXT_FIELD;
    private javax.swing.JTextField TIMES_PRUNING_OCCURRED_IN_MAX_TEXT;
    private javax.swing.JLabel TIMES_PRUNING_OCCURRED_IN_MAX_TEXT_FIELD;
    private javax.swing.JTextField TIMES_PRUNING_OCCURRED_IN_MIN_TEXT;
    private javax.swing.JLabel TIMES_PRUNING_OCCURRED_IN_MIN_TEXT_FIELD;
    private javax.swing.JTextField TOTAL_NUMBER_NODES_GENERATED_IN_TREE_TEXT;
    private javax.swing.JLabel TOTAL_NUMBER_NODES_GENERATED_IN_TREE_TEXT_FIELD;
    private javax.swing.JButton X_BUTTON;
    // End of variables declaration//GEN-END:variables
}
